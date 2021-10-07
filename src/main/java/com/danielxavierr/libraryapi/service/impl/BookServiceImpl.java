package com.danielxavierr.libraryapi.service.impl;

import org.springframework.stereotype.Service;

import com.danielxavierr.libraryapi.entity.Book;
import com.danielxavierr.libraryapi.exceptions.BusinessException;
import com.danielxavierr.libraryapi.repository.BookRepository;
import com.danielxavierr.libraryapi.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		this.repository = repository;
	}

	@Override
	public Book save(Book book) {
		if (repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado.");
		}
		return repository.save(book);
	}

}
