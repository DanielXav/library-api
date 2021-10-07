package com.danielxavierr.libraryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danielxavierr.libraryapi.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

	boolean existsByIsbn(String isbn);

}
