package com.danielxavierr.libraryapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.danielxavierr.libraryapi.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
	public void returnTrueWhenIsbnExists() {
		// Cenário
		String isbn = "123";
		Book book = Book.builder().title("Senhor dos aneis").author("Fulano").isbn(isbn).build();
		entityManager.persist(book);
		
		// Execução
		boolean exists = repository.existsByIsbn(isbn);
		
		// Verificação
		assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Deve retornar false quando não existir um livro na base com o isbn informado")
	public void returnFlaseWhenIsbnDoesntExists() {
		// Cenário
		String isbn = "321";
		
		// Execução
		boolean exists = repository.existsByIsbn(isbn);
		
		// Verificação
		assertThat(exists).isFalse();
	}
}
