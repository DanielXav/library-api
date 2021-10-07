package com.danielxavierr.libraryapi.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.danielxavierr.libraryapi.DTO.BookDTO;
import com.danielxavierr.libraryapi.entity.Book;
import com.danielxavierr.libraryapi.exceptions.BusinessException;
import com.danielxavierr.libraryapi.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class) // Cria um contexto para rodar o teste
@ActiveProfiles("test") // Rodar com o perfil de teste
@WebMvcTest // Testar o comportamento da API
@AutoConfigureMockMvc // Configura o objeto pra fazer as requisições
public class BookControllerTest {

	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean // Mock especializado
	BookService service;
	
	@Test
	@DisplayName("Deve criar um livro com sucesso.")
	public void createBookTest() throws Exception {
		
		BookDTO dto = createNewBook();
		Book savedBook = Book.builder().id(10l).author("JK").title("Harry Potter").isbn("001").build();
		
		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
		
		String json = new ObjectMapper().writeValueAsString(dto); // Transforma o dto em json
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(BOOK_API)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
			.andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
			.andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
			.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));
	}
	
	@Test
	@DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação de livros.")
	public void createInvalidBookTest() throws Exception {
		
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
		
		// Json todo nulo, porque está passando um novo DTO
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(3))); // Tamanho 3 por que vai ser 3 mensagens de erro
			
	}
	
	@Test
	@DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
	public void createBookWithDuplicatedIsbn() throws Exception {
		
		BookDTO dto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(dto);
		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException("Isbn já cadastrado."));
		
		// Json todo nulo, porque está passando um novo DTO
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value("Isbn já cadastrado."));
	}
	
	private BookDTO createNewBook() {
		return BookDTO.builder().author("JK").title("Harry Potter").isbn("001").build();
	}
}
