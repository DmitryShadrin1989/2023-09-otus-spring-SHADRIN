package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.data.AuthorTestData;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.data.GenreTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInsertUpdateDto;
import ru.otus.hw.dto.GenreToEditBookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Контроллер для работы с книгами")
@WebMvcTest
@ContextConfiguration(classes = BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    private static List<BookDto> dbBooks;

    private static List<Author> dbAuthors;

    private static List<Genre> dbGenres;

    private static Book newBook;

    private static Book changeBook;

    @BeforeAll
    static void setUp() {
        dbBooks = BookDto.toDtoList(BookTestData.getDbBooks());
        dbAuthors = AuthorTestData.getDbAuthors();
        dbGenres = GenreTestData.getDbGenres();
        newBook = BookTestData.getNewBook();
        changeBook = BookTestData.getChangeBook();
    }

    @Test
    @DisplayName("должен загружать страницу со списком книг")
    void shouldReturnBookListPage() throws Exception {
        given(bookService.findAll()).willReturn(dbBooks);

        mvc.perform(get(BookController.URL))
                .andExpect(status().isOk())
                .andExpect(view().name("booksList"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", dbBooks));;
    }

    @Test
    @DisplayName("должен загружать странницу создания книги")
    void shouldReturnBookCreatePage() throws Exception {
        given(authorService.findAll()).willReturn(dbAuthors);
        given(genreService.findAll()).willReturn(dbGenres);

        mvc.perform(get(BookController.URL + "/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookCreate"))
                .andExpect(model().attribute("authors", dbAuthors))
                .andExpect(model().attribute("genres", GenreToEditBookDto.toDtoList(dbGenres, null)))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    @DisplayName("должен сохранить новую книгу и сделать редирект")
    void shouldCreateBook() throws Exception {
        BookInsertUpdateDto bookInsert = BookInsertUpdateDto.toDto(new BookDto(newBook));

        mvc.perform(post(BookController.URL + "/create")
                .flashAttr("book", bookInsert))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BookController.URL));

        verify(bookService, times(1)).insert(bookInsert);
    }

    @Test
    @DisplayName("должен загружать странницу редактирования книги")
    void shouldReturnBookEditPage() throws Exception {
        given(bookService.findById(changeBook.getId())).willReturn(new BookDto(changeBook));
        given(authorService.findAll()).willReturn(dbAuthors);
        given(genreService.findAll()).willReturn(dbGenres);

        mvc.perform(get(BookController.URL + "/edit/{id}", changeBook.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("bookEdit"))
                .andExpect(model().attribute("authors", dbAuthors))
                .andExpect(model().attribute("genres", GenreToEditBookDto.toDtoList(dbGenres,
                        changeBook.getGenres().stream().map(Genre::getId).toList())))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    @DisplayName("должен сохранить обновленную книгу и сделать редирект")
    void shouldEditBook() throws Exception {
        BookInsertUpdateDto bookEdit = BookInsertUpdateDto.toDto(new BookDto(changeBook));

        mvc.perform(post(BookController.URL + "/edit/{id}", changeBook.getId())
                        .flashAttr("book", bookEdit))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BookController.URL));

        verify(bookService, times(1)).update(bookEdit);
    }

    @Test
    @DisplayName("должен удалить книгу и сделать редирект")
    void shouldDeleteBook() throws Exception {
        mvc.perform(post(BookController.URL + "/delete/{id}", changeBook.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BookController.URL));

        verify(bookService, times(1)).deleteById(changeBook.getId());
    }
}