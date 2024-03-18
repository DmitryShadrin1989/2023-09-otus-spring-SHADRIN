package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.data.CommentTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Rest контроллер для работы с комментариями к книгам")
@WebMvcTest
@ContextConfiguration(classes = CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private static Map<String, List<Comment>> dbMapBooksComments;

    @Getter
    private static List<CommentDto> dbComments;

    private static CommentDto dbNewComment;

    private static CommentDto dbChangeComment;

    @Getter
    private static List<BookDto> dbBooks;

    @BeforeAll
    static void setUp() {
        dbMapBooksComments = CommentTestData.getDbMapBooksComments();
        dbComments = CommentDto.toDtoList(CommentTestData.getDbComments());
        dbNewComment = new CommentDto(CommentTestData.getNewComment());
        dbChangeComment = new CommentDto(CommentTestData.getChangeComment());
        dbBooks = BookDto.toDtoList(BookTestData.getDbBooks());
    }

    @DisplayName("должен возвращать спискок комментариев для книги")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCommentList(BookDto book) throws Exception {
        List<CommentDto> expectedComments = CommentDto.toDtoList(dbMapBooksComments.get(book.getId()));
        given(commentService.findAllByBookId(book.getId())).willReturn(expectedComments);

        mvc.perform(get("/api/library/book/{bookId}/comment", book.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedComments)));
        verify(commentService, times(1)).findAllByBookId(book.getId());
    }

    @ParameterizedTest
    @MethodSource("getDbComments")
    @DisplayName("должен возвращать комментарий к книге")
    void shouldReturnComment(CommentDto comment) throws Exception {
        given(commentService.findById(comment.getId())).willReturn(comment);

        mvc.perform(get("/api/library/book/{bookId}/comment/{id}", comment.getBookId(), comment.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(comment)));
        verify(commentService, times(1)).findById(comment.getId());
    }

    @Test
    @DisplayName("должен сохранить новый комментарий к книге")
    void shouldCreateComment() throws Exception {
        given(commentService.insert(dbNewComment)).willReturn(dbNewComment);

        mvc.perform(post("/api/library/book/{bookId}/comment", dbNewComment.getBookId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dbNewComment)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dbNewComment)));
        verify(commentService, times(1)).insert(dbNewComment);
    }

    @Test
    @DisplayName("должен сохранить обновленный комментарий к книге")
    void shouldEditComment() throws Exception {
        given(commentService.update(dbChangeComment)).willReturn(dbChangeComment);

        mvc.perform(put("/api/library/book/{bookId}/comment/{id}",
                        dbChangeComment.getBookId(), dbChangeComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dbChangeComment)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dbChangeComment)));
        verify(commentService, times(1)).update(dbChangeComment);
    }

    @Test
    @DisplayName("должен удалить комментарий к книге")
    void shouldDeleteComment() throws Exception {
        mvc.perform(delete("/api/library/book/{bookId}/comment/{id}",
                dbChangeComment.getBookId(), dbChangeComment.getId()))
                .andExpect(status().isOk());
        verify(commentService, times(1)).deleteById(dbChangeComment.getId());
    }
}