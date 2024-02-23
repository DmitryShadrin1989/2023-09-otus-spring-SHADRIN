package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.data.CommentTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentInsertUpdateDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Контроллер для работы с комментариями к книгам")
@WebMvcTest
@ContextConfiguration(classes = CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private BookService bookService;

    private static Map<String, List<Comment>> dbMapBooksComments;

    private static Comment dbNewComment;

    private static Comment dbChangeComment;

    private static List<Book> dbBooks;

    @BeforeAll
    static void setUp() {
        dbMapBooksComments = CommentTestData.getDbMapBooksComments();
        dbNewComment = CommentTestData.getNewComment();
        dbChangeComment = CommentTestData.getChangeComment();
        dbBooks = BookTestData.getDbBooks();
    }

    @DisplayName("должен загружать view со списком комментариев для книги")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCommentListPage(Book book) throws Exception {
        given(commentService.findAllByBookId(book.getId())).willReturn(dbMapBooksComments.get(book.getId()));
        given(bookService.findById(book.getId())).willReturn(new BookDto(book));

        mvc.perform(get(CommentController.URL + "/book").param("bookId", book.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("commentListForBook"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("comments", dbMapBooksComments.get(book.getId())));
    }

    @Test
    @DisplayName("должен загружать странницу создания комментария к книге")
    void shouldReturnCommentCreatePage() throws Exception {
        given(bookService.findAll()).willReturn(BookDto.toDtoList(dbBooks));

        mvc.perform(get(CommentController.URL + "/create").param("bookId", dbNewComment.getBook().getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("commentCreate"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    @DisplayName("должен сохранить новый комментарий к книге и сделать редирект")
    void shouldCreateComment() throws Exception {
        CommentInsertUpdateDto commentInsert = CommentInsertUpdateDto.toDto(dbNewComment);

        mvc.perform(post(CommentController.URL + "/create")
                        .flashAttr("comment", commentInsert))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CommentController.URL + "/book?bookId=" + commentInsert.getBookId()));

        verify(commentService, times(1)).insert(commentInsert);
    }

    @Test
    @DisplayName("должен загружать странницу редактирования комментария к книге")
    void shouldReturnCommentEditPage() throws Exception {
        given(commentService.findById(dbChangeComment.getId())).willReturn(Optional.ofNullable(dbChangeComment));
        given(bookService.findAll()).willReturn(BookDto.toDtoList(dbBooks));

        mvc.perform(get(CommentController.URL + "/edit/{id}", dbChangeComment.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("commentEdit"))
                .andExpect(model().attributeExists("books", "comment"));
    }

    @Test
    @DisplayName("должен сохранить обновленный комментарий к книге и сделать редирект")
    void shouldEditComment() throws Exception {
        CommentInsertUpdateDto commentUpdate = CommentInsertUpdateDto.toDto(dbChangeComment);

        mvc.perform(post(CommentController.URL + "/edit/{id}", dbChangeComment.getId())
                        .flashAttr("comment", commentUpdate))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CommentController.URL + "/book?bookId=" + commentUpdate.getBookId()));

        verify(commentService, times(1)).update(commentUpdate);
    }

    @Test
    @DisplayName("должен удалить комментарий к книге и сделать редирект")
    void shouldDeleteComment() throws Exception {
        mvc.perform(post(CommentController.URL + "/delete/{id}", dbChangeComment.getId())
                        .param("bookId", dbChangeComment.getBook().getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CommentController.URL + "/book?bookId=" + dbChangeComment.getBook().getId()));

        verify(commentService, times(1)).deleteById(dbChangeComment.getId());
    }

    public static List<Book> getDbBooks() {
        return dbBooks;
    }
}