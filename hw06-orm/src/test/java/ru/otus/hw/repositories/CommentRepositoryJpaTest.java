package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями к книгам ")
@DataJpaTest
@Import({CommentRepositoryJpa.class})
class CommentRepositoryJpaTest {

    @Autowired
    private CommentRepositoryJpa repository;

    @Autowired
    private TestEntityManager em;

    private Book dbFirstBook;

    private Book dbSecondBook;

    private List<Comment> dbCommentsForFirstBook;

    private List<Comment> dbCommentsForSecondBook;

    private Comment newCommentForFirstBook;

    private Comment newCommentForSecondBook;

    private Comment changeCommentForFirstBook;

    private Comment changeCommentForSecondBook;

    @BeforeEach
    void setUp() {
        dbFirstBook = getBookFromDb(1);
        dbSecondBook = getBookFromDb(2);
        dbCommentsForFirstBook = getCommentsForFirstBook();
        dbCommentsForSecondBook = getCommentsForSecondBook();
        newCommentForFirstBook = getNewComment(dbFirstBook);
        newCommentForSecondBook = getNewComment(dbSecondBook);
        changeCommentForFirstBook = getChangeComment(dbCommentsForFirstBook.get(0));
        changeCommentForSecondBook = getChangeComment(dbCommentsForSecondBook.get(0));
    }

    @DisplayName("должен загружать комментарии по id книги")
    @Test
    void shouldReturnCorrectCommentsByBookId() {
        List<Comment> actualCommentsForFirstBook = repository.findAllByBookId(dbFirstBook.getId());
        assertThat(actualCommentsForFirstBook)
                .usingRecursiveComparison()
                .isEqualTo(dbCommentsForFirstBook);

        List<Comment> actualCommentsForSecondBook = repository.findAllByBookId(dbSecondBook.getId());
        assertThat(actualCommentsForSecondBook)
                .usingRecursiveComparison()
                .isEqualTo(dbCommentsForSecondBook);
    }

    @DisplayName("должен сохранять новый комментарий к книге")
    @Test
    void shouldSaveNewComment() {
        List<Comment> newComments = List.of(newCommentForFirstBook, newCommentForSecondBook);
        for (Comment expectedComment : newComments) {
            var returnedComment = repository.save(expectedComment);
            assertThat(returnedComment).isNotNull()
                    .matches(comment -> comment.getId() > 0)
                    .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

            assertThat(repository.findById(returnedComment.getId()))
                    .isPresent()
                    .get()
                    .isEqualTo(returnedComment);
        }
    }

    @DisplayName("должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        List<Comment> changeComments = List.of(changeCommentForFirstBook, changeCommentForSecondBook);
        for (Comment expectedComment : changeComments) {
            assertThat(repository.findById(expectedComment.getId()))
                    .isPresent()
                    .get()
                    .isNotEqualTo(expectedComment);

            var returnedComment = repository.save(expectedComment);
            assertThat(returnedComment).isNotNull()
                    .matches(comment -> comment.getId() > 0)
                    .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

            assertThat(repository.findById(returnedComment.getId()))
                    .isPresent()
                    .get()
                    .isEqualTo(returnedComment);
        }
    }

    @DisplayName("должен удалять комментарий по id ")
    @Test
    void shouldDeleteComment() {
        assertThat(repository.findById(1L)).isPresent();
        repository.deleteById(1L);
        assertThat(repository.findById(1L)).isEmpty();
    }

    private Book getBookFromDb(long bookId) {
        return em.find(Book.class, bookId);
    }

    private List<Comment> getCommentsForFirstBook() {
        return List.of(em.find(Comment.class, 1L),
                em.find(Comment.class, 2L),
                em.find(Comment.class, 3L));
    }

    private List<Comment> getCommentsForSecondBook() {
        return List.of(em.find(Comment.class, 4L),
                em.find(Comment.class, 5L),
                em.find(Comment.class, 6L));
    }

    private Comment getNewComment(Book book) {
        return new Comment(0, "Comment_10050", book);
    }

    private Comment getChangeComment(Comment comment) {
        return new Comment(comment.getId(), "Comment_10050", comment.getBook());
    }
}