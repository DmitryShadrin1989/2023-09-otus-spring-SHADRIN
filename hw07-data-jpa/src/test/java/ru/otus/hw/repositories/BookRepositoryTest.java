package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.repositories.BookTestData.*;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private TestEntityManager em;

    private List<Book> dbBooks;

    private Book newBook;

    private Book changeBook;

    @BeforeEach
    void setUp() {
        List<Author> dbAuthors = BookTestData.getDbAuthors();
        List<Genre> dbGenres = BookTestData.getDbGenres();
        dbBooks = BookTestData.getDbBooks(dbAuthors, dbGenres);
        newBook = getNewBook();
        changeBook = getChangeBook();
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
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(em.find(Book.class, returnedBook.getId())).isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = changeBook;
        assertThat(em.find(Book.class, expectedBook.getId())).isNotEqualTo(expectedBook);

        var returnedBook = repository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(em.find(Book.class, returnedBook.getId())).isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(em.find(Book.class, 1L)).isNotNull();
        repository.deleteById(1L);
        assertThat(em.find(Book.class, 1L)).isNull();
    }

    private Book getNewBook() {
        Author author = em.find(Author.class, 3L);
        List<Genre> genres = List.of(em.find(Genre.class, 3L), em.find(Genre.class, 4L));
        return new Book(0, "BookTitle_5", author, genres);
    }


    private Book getChangeBook() {
        Author author = em.find(Author.class, 1L);
        List<Genre> genres = List.of(em.find(Genre.class, 1L), em.find(Genre.class, 2L));
        return new Book(1, "BookTitle_10500", author, genres);
    }

    public static List<Book> getDbBooks() {
        return BookTestData.getDbBooks();
    }
}