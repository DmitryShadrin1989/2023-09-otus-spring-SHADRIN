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
import ru.otus.hw.data.CommentTestData;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.mongodb.changelogs.test.InitMongoDBDataChangeLog;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе ReactiveMongoRepository для работы с комментариями к книгам ")
@DataMongoTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Getter
    private static List<Book> books;

    private static Map<String, List<Comment>> mapBooksComments;

    private static List<Comment> comments;

    private static Comment newComment;

    private static Comment changeComment;

    @BeforeAll
    static void setUp() {
        books = BookTestData.getBooks();
        mapBooksComments = CommentTestData.getMapBooksComments();
        comments = mapBooksComments.values().stream()
                .flatMap(Collection::stream)
                .toList();
        newComment = CommentTestData.getNewComment();
        changeComment = CommentTestData.getChangeComment();
    }

    @BeforeEach
    void beforeEach() {
        reactiveMongoTemplate.dropCollection(Comment.class).block();
        InitMongoDBDataChangeLog.initData(reactiveMongoTemplate, comments);
    }

    @DisplayName("должен загружать комментарии по id книги")
    @ParameterizedTest
    @MethodSource("getBooks")
    void shouldReturnCorrectCommentsByBookId(Book book) {
        var actualComments = repository.findAllByBookId(book.getId());
        var expectedComments = mapBooksComments.get(book.getId());
        StepVerifier
                .create(actualComments)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @DisplayName("должен сохранять новый комментарий к книге")
    @Test
    void shouldSaveNewComment() {
        var expectedComment = newComment;
        var returnedComment = repository.save(expectedComment);
        StepVerifier
                .create(returnedComment)
                .assertNext(comment -> assertThat(comment).isNotNull()
                        .matches(c -> !c.getId().isEmpty())
                        .usingRecursiveComparison()
                        .ignoringExpectedNullFields()
                        .isEqualTo(expectedComment)
                )
                .expectComplete()
                .verify();
    }

    @DisplayName("должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        var expectedComment = changeComment;
        var returnedComment = reactiveMongoTemplate.findById(expectedComment.getId(), Comment.class);
        StepVerifier
                .create(returnedComment)
                .assertNext(comment -> assertThat(comment).isNotNull()
                        .usingRecursiveComparison()
                        .isNotEqualTo(expectedComment)
                )
                .expectComplete()
                .verify();

        returnedComment = repository.save(expectedComment);
        StepVerifier
                .create(returnedComment)
                .assertNext(comment -> assertThat(comment).isNotNull()
                        .usingRecursiveComparison()
                        .isEqualTo(expectedComment)
                )
                .expectComplete()
                .verify();
    }

    @DisplayName("должен удалять комментарий по id ")
    @Test
    void shouldDeleteComment() {
        var returnedComment = reactiveMongoTemplate.findById("id_1", Comment.class);
        StepVerifier
                .create(returnedComment)
                .assertNext(book -> assertThat(book).isNotNull())
                .expectComplete()
                .verify();

        repository.deleteById("id_1").block();
        returnedComment = reactiveMongoTemplate.findById("id_1", Comment.class);
        StepVerifier
                .create(returnedComment)
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }
}