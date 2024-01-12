package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями к книгам ")
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private TestEntityManager em;

    private static final List<Book> DB_BOOKS = BookTestData.getDbBooks();

    private Map<Book, List<Comment>> dbMapBooksComments;

    @BeforeEach
    void setUp() {
        dbMapBooksComments = getDbMapBooksComments();
    }

    @DisplayName("должен загружать комментарии по id книги")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectCommentsByBookId(Book book) {
        List<Comment> actualComments = repository.findAllByBookId(book.getId());
        List<Comment> expectedComments = dbMapBooksComments.get(book);
        assertThat(actualComments)
                .usingRecursiveComparison()
                .isEqualTo(expectedComments);
    }

    @DisplayName("должен сохранять новый комментарий к книге")
    @ParameterizedTest
    @MethodSource("getNewComments")
    void shouldSaveNewComment(Comment newComment) {
        var returnedComment = repository.save(newComment);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(newComment);

        assertThat(em.find(Comment.class, returnedComment.getId())).isEqualTo(returnedComment);
    }

    @DisplayName("должен сохранять измененный комментарий к книге")
    @ParameterizedTest
    @MethodSource("getDbChangeComments")
    void shouldSaveUpdatedComment(Comment expectedComment) {
        assertThat(em.find(Comment.class, expectedComment.getId())).isNotEqualTo(expectedComment);

        var returnedComment = repository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(em.find(Comment.class, returnedComment.getId())).isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий по id ")
    @Test
    void shouldDeleteComment() {
        assertThat(em.find(Comment.class, 1L)).isNotNull();
        repository.deleteById(1L);
        assertThat(em.find(Comment.class, 1L)).isNull();
    }

    private Map<Book, List<Comment>> getDbMapBooksComments() {
        Map<Book, List<Comment>> dbMapBooksComments = new HashMap<>();
        dbMapBooksComments.put(DB_BOOKS.get(0), List.of(em.find(Comment.class, 1L),
                em.find(Comment.class, 2L),
                em.find(Comment.class, 3L)));
        dbMapBooksComments.put(DB_BOOKS.get(1), List.of(em.find(Comment.class, 4L),
                em.find(Comment.class, 5L),
                em.find(Comment.class, 6L)));
        dbMapBooksComments.put(DB_BOOKS.get(2), List.of());
        return dbMapBooksComments;
    }

    private static List<Comment> getNewComments() {
        return List.of(new Comment(0, "Comment_10050", DB_BOOKS.get(0)),
                new Comment(0, "Comment_10051", DB_BOOKS.get(1)));
    }

    private static List<Comment> getDbChangeComments() {
        return List.of(new Comment(1L, "Comment_10050", DB_BOOKS.get(0)),
                new Comment(4L, "Comment_10051", DB_BOOKS.get(1)));
    }

    public static List<Book> getDbBooks() {
        return DB_BOOKS;
    }
}