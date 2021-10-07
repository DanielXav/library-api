package com.danielxavierr.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.danielxavierr.libraryapi.entity.Book;
import com.danielxavierr.libraryapi.exceptions.BusinessException;
import com.danielxavierr.libraryapi.repository.BookRepository;
import com.danielxavierr.libraryapi.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService service;
	
	@MockBean // Simula o comportamento do repository
	BookRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository);
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		// Cenário
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book))
			.thenReturn(Book.builder()
					.id(1l)
					.isbn("123")
					.author("Fulano")
					.title("Senhor dos aneis")
					.build());
	
		// Execução
		Book savedBook = service.save(book);
		
		// Verificação
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("123");
		assertThat(savedBook.getTitle()).isEqualTo("Senhor dos aneis");
		assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
	}
	
	@Test
	@DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
	public void shouldNotSaveABookWithDuplicatedISBN() {
		
		//Cenário
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		//Execução
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));
		
		//Verificações
		assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Isbn já cadastrado.");
			
		Mockito.verify(repository, Mockito.never()).save(book); // Veriricar que o repositorio nunca vai salvar
	}
	
	public Book createValidBook() {
		return Book.builder().isbn("123").author("Fulano").title("Senhor dos aneis").build();
	}
}
