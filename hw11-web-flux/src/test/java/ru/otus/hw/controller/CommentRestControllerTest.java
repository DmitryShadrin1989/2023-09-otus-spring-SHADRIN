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
import ru.otus.hw.data.CommentTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@DisplayName("Rest контроллер для работы с комментариями к книгам")
@WebFluxTest
@ContextConfiguration(classes = CommentRestController.class)
class CommentRestControllerTest {

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private WebTestClient webTestClient;

    private static Map<String, List<Comment>> mapBooksComments;

    @Getter
    private static List<Comment> comments;

    private static Comment newComment;

    private static Comment changeComment;

    @Getter
    private static List<BookDto> books;

    @BeforeAll
    static void setUp() {
        mapBooksComments = CommentTestData.getMapBooksComments();
        comments = CommentTestData.getComments();
        newComment = CommentTestData.getNewComment();
        changeComment = CommentTestData.getChangeComment();
        books = BookDto.toDtoList(BookTestData.getBooks());
    }

    @DisplayName("должен возвращать спискок комментариев для книги")
    @ParameterizedTest
    @MethodSource("getBooks")
    void shouldReturnCommentList(BookDto book) {
        List<Comment> expectedComments = mapBooksComments.get(book.getId());
        when(commentRepository.findAllByBookId(book.getId())).thenReturn(Flux.fromIterable(expectedComments));

        webTestClient.get()
                .uri("/api/library/book/{bookId}/comment", book.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CommentDto.class)
                .hasSize(3)
                .isEqualTo(CommentDto.toDtoList(expectedComments));

        verify(commentRepository, times(1)).findAllByBookId(book.getId());
    }

    @ParameterizedTest
    @MethodSource("getComments")
    @DisplayName("должен возвращать комментарий к книге")
    void shouldReturnComment(Comment comment) {
        when(commentRepository.findById(comment.getId())).thenReturn(Mono.just(comment));

        webTestClient.get()
                .uri("/api/library/book/{bookId}/comment/{id}",
                        comment.getBook().getId(),
                        comment.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(new CommentDto(comment));

        verify(commentRepository, times(1)).findById(comment.getId());
    }

    @Test
    @DisplayName("должен сохранить новый комментарий к книге")
    void shouldCreateComment() {
        String bookId = newComment.getBook().getId();
        when(bookRepository.findById(bookId)).thenReturn(Mono.just(newComment.getBook()));

        when(commentRepository.save(any())).thenReturn(Mono.just(newComment));

        CommentDto newCommentDto = new CommentDto(newComment);
        webTestClient.post()
                .uri("/api/library/book/{bookId}/comment", bookId)
                .bodyValue(newCommentDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(newCommentDto);

        verify(bookRepository, times(1)).findById(bookId);
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("должен сохранить обновленный комментарий к книге")
    void shouldEditComment() {
        when(commentRepository.findById(changeComment.getId())).thenReturn(Mono.just(changeComment));

        String bookId = changeComment.getBook().getId();
        when(bookRepository.findById(bookId)).thenReturn(Mono.just(changeComment.getBook()));

        when(commentRepository.save(any())).thenReturn(Mono.just(changeComment));

        CommentDto changeCommentDto = new CommentDto(changeComment);
        webTestClient.put()
                .uri("/api/library/book/{bookId}/comment/{id}", bookId, changeComment.getId())
                .bodyValue(changeCommentDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(changeCommentDto);

        verify(commentRepository, times(1)).findById(changeComment.getId());
        verify(bookRepository, times(1)).findById(bookId);
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("должен удалить комментарий к книге")
    void shouldDeleteComment() {
        when(commentRepository.deleteById(changeComment.getId())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/library/book/{bookId}/comment/{id}",
                        changeComment.getBook().getId(),
                        changeComment.getId())
                .exchange()
                .expectStatus()
                .isOk();

        verify(commentRepository, times(1)).deleteById(changeComment.getId());
    }
}