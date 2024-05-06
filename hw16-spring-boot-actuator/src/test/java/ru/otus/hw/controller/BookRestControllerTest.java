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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Rest контроллер для работы с книгами")
@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Getter
    private static List<BookDto> books;

    private static BookDto newBook;

    private static BookDto changeBook;

    @BeforeAll
    static void setUp() {
        books = BookDto.toDtoList(BookTestData.getDbBooks());
        newBook = new BookDto(BookTestData.getNewBook());
        changeBook = new BookDto(BookTestData.getChangeBook());
    }

    @DisplayName("должен возвращать список книг")
    @Test
    void shouldReturnBookDtoList() throws Exception {
        given(bookService.findAll()).willReturn(books);

        mvc.perform(get("/api/library/book"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(books)));
        verify(bookService, times(1)).findAll();
    }

    @DisplayName("должен возвращать книгу по ID")
    @ParameterizedTest
    @MethodSource("getBooks")
    void shouldReturnBookDto(BookDto book) throws Exception {
        given(bookService.findById(book.getId())).willReturn(book);

        mvc.perform(get("/api/library/book/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(book)));
        verify(bookService, times(1)).findById(book.getId());
    }

    @DisplayName("должен сохранить новую книгу")
    @Test
    void shouldCreateBook() throws Exception {
        given(bookService.insert(newBook)).willReturn(newBook);

        mvc.perform(post("/api/library/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(newBook)));
        verify(bookService, times(1)).insert(newBook);
    }

    @DisplayName("должен сохранить обновленную книгу")
    @Test
    void shouldEditBook() throws Exception {
        given(bookService.update(changeBook)).willReturn(changeBook);

        mvc.perform(put("/api/library/book/{id}", changeBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeBook)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(changeBook)));
        verify(bookService, times(1)).update(changeBook);
    }

    @DisplayName("должен удалить книгу")
    @Test
    void shouldDeleteBook() throws Exception {
        mvc.perform(delete("/api/library/book/{id}", changeBook.getId()))
                .andExpect(status().isOk());
        verify(bookService, times(1)).deleteById(changeBook.getId());
    }
}