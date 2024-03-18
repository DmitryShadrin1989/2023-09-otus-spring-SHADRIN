package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Book;
import ru.otus.hw.mongodb.changelogs.test.InitMongoDBDataChangeLog;
import ru.otus.hw.data.BookTestData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе MongoRepository для работы с книгами ")
@DataMongoTest
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static List<Book> dbBooks;

    private static Book newBook;

    private static Book changeBook;

    @BeforeAll
    static void setUp() {
        dbBooks = BookTestData.getDbBooks();
        newBook = BookTestData.getNewBook();
        changeBook = BookTestData.getChangeBook();
    }

    @BeforeEach
    void beforeEach() {
        mongoTemplate.dropCollection(Book.class);
        InitMongoDBDataChangeLog.initData(mongoTemplate, dbBooks);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {
        var actualBook = repository.findById(expectedBook.getId());
        assertThat(actualBook).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = repository.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks)
                .hasSize(3)
                .usingRecursiveComparison()
                .isEqualTo(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = newBook;
        var returnedBook = repository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> !book.getId().isEmpty())
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = changeBook;
        assertThat(mongoTemplate.findById(expectedBook.getId(), Book.class))
                .usingRecursiveComparison().isNotEqualTo(expectedBook);

        var returnedBook = repository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> !book.getId().isEmpty())
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(mongoTemplate.findById("id_1", Book.class)).isNotNull();
        repository.deleteById("id_1");
        assertThat(mongoTemplate.findById("id_1", Book.class)).isNull();
    }

    public static List<Book> getDbBooks() {
        return dbBooks;
    }
}