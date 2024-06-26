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
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.mongodb.changelogs.test.InitMongoDBDataChangeLog;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.data.CommentTestData;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@DisplayName("Репозиторий на основе MongoRepository для работы с комментариями к книгам ")
@DataMongoTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Getter
    private static List<Book> books;

    private static Map<String, List<Comment>> mapBooksComments;

    private static List<Comment> comments;

    private static Comment newComment;

    private static Comment changeComment;

    @BeforeAll
    static void setUp() {
        books = BookTestData.getDbBooks();
        mapBooksComments = CommentTestData.getDbMapBooksComments();
        comments = mapBooksComments.values().stream()
                .flatMap(Collection::stream)
                .toList();
        newComment = CommentTestData.getNewComment();
        changeComment = CommentTestData.getChangeComment();
    }

    @BeforeEach
    void beforeEach() {
        mongoTemplate.dropCollection(Comment.class);
        InitMongoDBDataChangeLog.initData(mongoTemplate, comments);
    }

    @DisplayName("должен загружать комментарии по id книги")
    @ParameterizedTest
    @MethodSource("getBooks")
    void shouldReturnCorrectCommentsByBookId(Book book) {
        List<Comment> actualComments = repository.findAllByBookId(book.getId());
        List<Comment> expectedComments = mapBooksComments.get(book.getId());
        assertThat(actualComments)
                .usingRecursiveComparison()
                .isEqualTo(expectedComments);
    }

    @DisplayName("должен сохранять новый комментарий к книге")
    @Test
    void shouldSaveNewComment() {
        var expectedComment = newComment;
        var returnedComment = repository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> !comment.getId().isEmpty())
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);
    }

    @DisplayName("должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        Comment expectedComment = changeComment;
        assertThat(mongoTemplate.findById(expectedComment.getId(), Comment.class)).isNotEqualTo(expectedComment);

        var returnedComment = repository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> !comment.getId().isEmpty())
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);
    }

    @DisplayName("должен удалять комментарий по id ")
    @Test
    void shouldDeleteComment() {
        assertThat(mongoTemplate.findById("id_1", Comment.class)).isNotNull();
        repository.deleteById("id_1");
        assertThat(mongoTemplate.findById("id_1", Comment.class)).isNull();
    }
}