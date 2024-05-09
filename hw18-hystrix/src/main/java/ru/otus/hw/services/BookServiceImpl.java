package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "bookCircuitBreaker", fallbackMethod = "fallbackGetBook")
    public BookDto findById(String id) {
        log.info("DB Name: " + mongoTemplate.getDb().getName());
        return new BookDto(bookRepository.findById(id)
                   .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id))));
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "bookCircuitBreaker", fallbackMethod = "fallbackFindAllBooks")
    public List<BookDto> findAll() {
        return BookDto.toDtoList(bookRepository.findAll());
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "bookCircuitBreaker", fallbackMethod = "fallbackBookOperation")
    public BookDto insert(BookDto bookDto) {
        return new BookDto(save(null, bookDto.getTitle(), bookDto.getAuthorId(), bookDto.getGenreIds()));
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "bookCircuitBreaker", fallbackMethod = "fallbackBookOperation")
    public BookDto update(BookDto bookDto) {
        return new BookDto(save(bookDto.getId(), bookDto.getTitle(), bookDto.getAuthorId(), bookDto.getGenreIds()));
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "bookCircuitBreaker", fallbackMethod = "fallbackDeleteBook")
    public void deleteById(String id) {
        findById(id);
        bookRepository.deleteById(id);
        commentRepository.deleteByBookId(id);
    }

    private Book save(String id, String title, String authorId, List<String> genresIds) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres)) {
            throw new EntityNotFoundException("Genres with ids %s not found".formatted(genresIds));
        }
        var book = new Book(id, title, author, genres);
        return bookRepository.save(book);
    }

    private BookDto fallbackGetBook(String id, Exception ex) {
        log.error("An error occurred when receiving the book by ID - %s. Error message: %s"
                .formatted(id, ex.getMessage()));
        return new BookDto("", "Default books tittle", "Default books author", List.of());
    }

    private List<BookDto> fallbackFindAllBooks(Exception ex) {
        log.error("An error occurred while getting the list of books. Error message: %s".formatted(ex.getMessage()));
        return List.of();
    }

    private BookDto fallbackBookOperation(BookDto bookDto, Exception ex) {
        log.error("Error when trying to perform an operation with a book - %s. Error message: %s"
                .formatted(bookDto.toString(), ex.getMessage()));
        return bookDto;
    }

    private void fallbackDeleteBook(String id, Exception ex) {
        log.error("An error occurred when deleting a book by ID - %s. Error message: %s"
                .formatted(id, ex.getMessage()));
    }
}
