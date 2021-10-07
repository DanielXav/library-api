package com.danielxavierr.libraryapi.resource;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.danielxavierr.libraryapi.DTO.BookDTO;
import com.danielxavierr.libraryapi.entity.Book;
import com.danielxavierr.libraryapi.exceptions.ApiErrors;
import com.danielxavierr.libraryapi.exceptions.BusinessException;
import com.danielxavierr.libraryapi.service.BookService;

@RestController
@RequestMapping(value = "/api/books")
public class BookController {

	private BookService service;
	private ModelMapper modelMapper;
	
	public BookController(BookService service, ModelMapper mapper) {
		this.service = service;
		this.modelMapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create(@RequestBody @Valid BookDTO dto) {
		
		Book entity = modelMapper.map(dto, Book.class);
		
		entity = service.save(entity);
		
		return modelMapper.map(entity, BookDTO.class);
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		
		return new ApiErrors(bindingResult);
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BusinessException.class)
	public ApiErrors handleBusinessException(BusinessException ex) {
		return new ApiErrors(ex);
	}
}
