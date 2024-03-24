package ru.otus.hw.controller;

import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@DisplayName("Rest контроллер для работы с книгами")
@WebFluxTest
@ContextConfiguration(classes = BookRestController.class)
class BookRestControllerTest {

    @MockBean
    BookRepository bookRepository;

    @MockBean
    AuthorRepository authorRepository;

    @MockBean
    GenreRepository genreRepository;

    @MockBean
    CommentRepository commentRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Getter
    private static List<Book> books;

    private static Book newBook;

    private static Book changeBook;

    @BeforeAll
    static void setUp() {
        books = BookTestData.getBooks();
        newBook = BookTestData.getNewBook();
        changeBook = BookTestData.getChangeBook();
    }

    @Test
    @DisplayName("должен возвращать список книг")
    void shouldReturnBookDtoList() {
        when(bookRepository.findAll()).thenReturn(Flux.fromIterable(books));

        webTestClient.get()
                .uri("/api/library/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookDto.class)
                .hasSize(3)
                .isEqualTo(BookDto.toDtoList(books));

        verify(bookRepository, times(1)).findAll();
    }


    @ParameterizedTest
    @MethodSource("getBooks")
    @DisplayName("должен возвращать книгу по ID")
    void shouldReturnBookDto(Book book) {
        when(bookRepository.findById(book.getId())).thenReturn(Mono.just(book));

        webTestClient.get()
                .uri("/api/library/book/{id}", book.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookDto.class)
                .isEqualTo(new BookDto(book));

        verify(bookRepository, times(1)).findById(book.getId());
    }

    @Test
    @DisplayName("должен сохранить новую книгу")
    void shouldCreateBook() {
        String authorId = newBook.getAuthor().getId();
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(newBook.getAuthor()));

        List<String> genreIds = newBook.getGenres().stream().map(Genre::getId).toList();
        when(genreRepository.findAllById(genreIds)).thenReturn(Flux.fromIterable(newBook.getGenres()));

        when(bookRepository.save(any())).thenReturn(Mono.just(newBook));

        webTestClient.post()
                .uri("/api/library/book")
                .bodyValue(new BookDto(newBook))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookDto.class)
                .isEqualTo(new BookDto(newBook));

        verify(authorRepository, times(1)).findById(authorId);
        verify(genreRepository, times(1)).findAllById(genreIds);
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("должен сохранить обновленную книгу")
    void shouldEditBook() {
        when(bookRepository.findById(changeBook.getId())).thenReturn(Mono.just(books.get(0)));

        String authorId = changeBook.getAuthor().getId();
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(changeBook.getAuthor()));

        List<String> genreIds = changeBook.getGenres().stream().map(Genre::getId).toList();
        when(genreRepository.findAllById(genreIds)).thenReturn(Flux.fromIterable(changeBook.getGenres()));

        when(bookRepository.save(any())).thenReturn(Mono.just(changeBook));

        webTestClient.post()
                .uri("/api/library/book")
                .bodyValue(new BookDto(changeBook))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookDto.class)
                .isEqualTo(new BookDto(changeBook));

        verify(authorRepository, times(1)).findById(authorId);
        verify(genreRepository, times(1)).findAllById(genreIds);
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("должен удалить книгу")
    void shouldDeleteBook() {
        when(bookRepository.deleteById(changeBook.getId())).thenReturn(Mono.empty());
        when(commentRepository.deleteByBookId(changeBook.getId())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/library/book/{id}", changeBook.getId())
                .exchange()
                .expectStatus()
                .isOk();

        verify(bookRepository, times(1)).deleteById(changeBook.getId());
        verify(commentRepository, times(1)).deleteByBookId(changeBook.getId());
    }
}