package ru.otus.hw.repositories;

import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.test.StepVerifier;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.models.Book;
import ru.otus.hw.mongodb.changelogs.test.InitMongoDBDataChangeLog;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе ReactiveMongoRepository для работы с книгами ")
@DataMongoTest
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

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

    @BeforeEach
    void beforeEach() {
        reactiveMongoTemplate.dropCollection(Book.class).block();
        InitMongoDBDataChangeLog.initData(reactiveMongoTemplate, books);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {
        var actualBook = repository.findById(expectedBook.getId());
        StepVerifier
                .create(actualBook)
                .assertNext(author -> assertThat(author)
                        .usingRecursiveComparison()
                        .isEqualTo(expectedBook))
                .expectComplete()
                .verify();
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = repository.findAll();
        var expectedBooks = books;
        StepVerifier
                .create(actualBooks)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = newBook;
        var returnedBook = repository.save(expectedBook);
        StepVerifier
                .create(returnedBook)
                .assertNext(book -> assertThat(book).isNotNull()
                        .matches(b -> !b.getId().isEmpty())
                        .usingRecursiveComparison()
                        .ignoringExpectedNullFields()
                        .isEqualTo(expectedBook)
                )
                .expectComplete()
                .verify();
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = changeBook;
        var returnedBook = reactiveMongoTemplate.findById(expectedBook.getId(), Book.class);
        StepVerifier
                .create(returnedBook)
                .assertNext(book -> assertThat(book).isNotNull()
                        .usingRecursiveComparison()
                        .isNotEqualTo(expectedBook)
                )
                .expectComplete()
                .verify();

        returnedBook = repository.save(expectedBook);
        StepVerifier
                .create(returnedBook)
                .assertNext(book -> assertThat(book).isNotNull()
                        .usingRecursiveComparison()
                        .isEqualTo(expectedBook)
                )
                .expectComplete()
                .verify();
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var returnedBook = reactiveMongoTemplate.findById("id_1", Book.class);
        StepVerifier
                .create(returnedBook)
                .assertNext(book -> assertThat(book).isNotNull())
                .expectComplete()
                .verify();

        repository.deleteById("id_1").block();
        returnedBook = reactiveMongoTemplate.findById("id_1", Book.class);
        StepVerifier
                .create(returnedBook)
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }
}