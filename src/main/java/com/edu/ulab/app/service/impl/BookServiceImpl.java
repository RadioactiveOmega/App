package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.UpdateExeption;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        if(!bookRepository.existsById(book.getId())){
            throw new UpdateExeption("Failed to update with book: " + book + "\nBook with id " + book.getId() + " is not exist");
        }
        Book updatedBook = bookRepository
                .findById(book.getId())
                .orElseThrow(() -> new UpdateExeption("Failed to update with book: " +
                        book + "\nBook with id " + book.getId() + " is null"));
        /*
        * На сколько я понял, findById может вытащить null из репозитория, и тогда возможны две реализации:
        * кинуть исключение (что и сделано) или сохранить эту книгу в репозиторий
        * */
        log.info("Get book for update: {}", updatedBook);
        updatedBook.setUserId(book.getUserId());
        updatedBook.setAuthor(book.getAuthor());
        updatedBook.setTitle(book.getTitle());
        updatedBook.setPageCount(book.getPageCount());
        log.info("Updated book: {}", updatedBook);
        bookRepository.save(updatedBook);
        return bookMapper.bookToBookDto(updatedBook);

        /*
        * Подобная реализация написана как пример на сайте https://www.baeldung.com/spring-data-crud-repository-save, см. пункт 5.
        * Но скорее всего то, что ниже, тоже будет работать
        * */

//        Book book = bookMapper.bookDtoToBook(bookDto);
//        log.info("Mapped book: {}", book);
//        bookRepository.deleteById(book.getId());
//        Book savedBook = bookRepository.save(book);
//        log.info("Updated book: {}", savedBook);
//        return bookMapper.bookToBookDto(savedBook);

    }

    @Override
    public List<BookDto> getAllBooks() {
        log.info("Got a request for all books");
        return bookRepository.getAllBooks().stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookToBookDto)
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        log.info("Got a request for a book with id: {}", id);
        if(id == null){
            throw new NullPointerException("Id may not be null");
        }
        Book foundBook = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book with id " + id + " not found"));
        log.info("Found book: {}", foundBook);
        return bookMapper.bookToBookDto(foundBook);
    }

    @Override
    public void deleteBookById(Long id) {
        log.info("Received a request to delete a book with id: {}", id);
        if(id == null){
            throw new NullPointerException("Id may not be null");
        }
        log.info("Received a request to delete a book with id: {}", id);
        bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Failed to delete book with id " + id + ". Book is not exist"));
        log.info("Delete a book with id: {}", id);
        bookRepository.deleteById(id);
    }
}
