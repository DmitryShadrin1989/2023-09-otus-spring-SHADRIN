package ru.otus.hw.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final CommentRepository commentRepository;

    @GetMapping("/api/library/book")
    public Flux<BookDto> getListBooksPage() {
        return bookRepository.findAll().map(BookDto::new);
    }

    @GetMapping("/api/library/book/{id}")
    public Mono<BookDto> getBook(@PathVariable String id) {
        return bookRepository.findById(id).map(BookDto::new);
    }

    @PutMapping("/api/library/book/{id}")
    public Mono<BookDto> updateBook(@PathVariable String id, @RequestBody BookDto bookDto) {
        return Mono.zip(
                bookRepository.findById(bookDto.getId())
                        .switchIfEmpty(Mono.error(new NotFoundException("Book with id %s not found"
                                .formatted(bookDto.getId())))),
                authorRepository.findById(bookDto.getAuthorId())
                        .switchIfEmpty(Mono.error(new NotFoundException("Author with id %s not found"
                                .formatted(bookDto.getAuthorId())))),
                genreRepository.findAllById(bookDto.getGenreIds())
                        .switchIfEmpty(Mono.error(new NotFoundException("Genres with ids %s not found"
                                .formatted(bookDto.getGenreIds()))))
                        .collectList()
        ).flatMap(data -> {
            String changeTitle = bookDto.getTitle();
            Author changeAuthor = data.getT2();
            List<Genre> changeGenres = data.getT3();
            return bookRepository.save(new Book(bookDto.getId(), changeTitle, changeAuthor, changeGenres));
        }).map(BookDto::new);
    }

    @PostMapping("/api/library/book")
    public Mono<BookDto> createBook(@RequestBody BookDto bookDto) {
        return Mono.zip(
                authorRepository.findById(bookDto.getAuthorId())
                        .switchIfEmpty(Mono.error(new NotFoundException("Author with id %s not found"
                                .formatted(bookDto.getAuthorId())))),
                genreRepository.findAllById(bookDto.getGenreIds())
                        .switchIfEmpty(Mono.error(new NotFoundException("Genres with ids %s not found"
                                .formatted(bookDto.getGenreIds()))))
                        .collectList()
        ).flatMap(data -> {
            String title = bookDto.getTitle();
            Author author = data.getT1();
            List<Genre> genres = data.getT2();
            return bookRepository.save(new Book(null, title, author, genres));
        }).map(BookDto::new);
    }

    @DeleteMapping("/api/library/book/{id}")
    public Mono<Void> deleteBook(@PathVariable String id) {
        return bookRepository.deleteById(id).then(Mono.defer(() -> commentRepository.deleteByBookId(id)));
    }
}
