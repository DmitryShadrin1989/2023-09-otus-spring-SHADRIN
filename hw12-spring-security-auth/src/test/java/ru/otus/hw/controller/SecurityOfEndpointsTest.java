package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.otus.hw.data.SecurityOfEndpointsArgumentsProvider;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@DisplayName("Защита ресурсов при работе с контроллерами для книг")
@WebMvcTest
@ContextConfiguration(classes = {BookRestController.class, BookController.class,
        CommentController.class, CommentRestController.class,
        AuthorController.class, AuthorRestController.class,
        GenreController.class, GenreRestController.class})
@Import(SecurityConfiguration.class)
public class SecurityOfEndpointsTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @DisplayName("должен сделать редирект на страницу аутентификации для анонимного пользователя")
    @ParameterizedTest
    @ArgumentsSource(SecurityOfEndpointsArgumentsProvider.class)
    void shouldCheckSecurityOfResource(String describe,
                                       SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor user,
                                       MockHttpServletRequestBuilder requestBuilder,
                                       List<ResultMatcher> matchers) throws Exception {
        if (user != null) {
            requestBuilder = requestBuilder.with(user);
        }
        mvc.perform(requestBuilder)
                .andExpectAll(matchers.toArray(new ResultMatcher[0]));
    }
}
