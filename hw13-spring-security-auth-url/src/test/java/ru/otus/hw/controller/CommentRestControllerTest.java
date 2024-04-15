package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.data.CommentTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Rest контроллер для работы с комментариями к книгам")
@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private static Map<String, List<Comment>> mapBooksComments;

    @Getter
    private static List<CommentDto> comments;

    private static CommentDto newComment;

    private static CommentDto changeComment;

    @Getter
    private static List<BookDto> books;

    @BeforeAll
    static void setUp() {
        mapBooksComments = CommentTestData.getDbMapBooksComments();
        comments = CommentDto.toDtoList(CommentTestData.getDbComments());
        newComment = new CommentDto(CommentTestData.getNewComment());
        changeComment = new CommentDto(CommentTestData.getChangeComment());
        books = BookDto.toDtoList(BookTestData.getDbBooks());
    }

    @DisplayName("должен возвращать спискок комментариев для книги")
    @ParameterizedTest
    @MethodSource("getBooks")
    void shouldReturnCommentList(BookDto book) throws Exception {
        List<CommentDto> expectedComments = CommentDto.toDtoList(mapBooksComments.get(book.getId()));
        given(commentService.findAllByBookId(book.getId())).willReturn(expectedComments);

        mvc.perform(get("/api/library/book/{bookId}/comment", book.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedComments)));
        verify(commentService, times(1)).findAllByBookId(book.getId());
    }

    @DisplayName("должен возвращать комментарий к книге")
    @ParameterizedTest
    @MethodSource("getComments")
    void shouldReturnComment(CommentDto comment) throws Exception {
        given(commentService.findById(comment.getId())).willReturn(comment);

        mvc.perform(get("/api/library/book/{bookId}/comment/{id}", comment.getBookId(), comment.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(comment)));
        verify(commentService, times(1)).findById(comment.getId());
    }

    @DisplayName("должен сохранить новый комментарий к книге")
    @Test
    void shouldCreateComment() throws Exception {
        given(commentService.insert(newComment)).willReturn(newComment);

        mvc.perform(post("/api/library/book/{bookId}/comment", newComment.getBookId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(newComment)));
        verify(commentService, times(1)).insert(newComment);
    }

    @DisplayName("должен сохранить обновленный комментарий к книге")
    @Test
    void shouldEditComment() throws Exception {
        given(commentService.update(changeComment)).willReturn(changeComment);

        mvc.perform(put("/api/library/book/{bookId}/comment/{id}",
                        changeComment.getBookId(), changeComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeComment)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(changeComment)));
        verify(commentService, times(1)).update(changeComment);
    }

    @DisplayName("должен удалить комментарий к книге")
    @Test
    void shouldDeleteComment() throws Exception {
        mvc.perform(delete("/api/library/book/{bookId}/comment/{id}",
                changeComment.getBookId(), changeComment.getId()))
                .andExpect(status().isOk());
        verify(commentService, times(1)).deleteById(changeComment.getId());
    }
}