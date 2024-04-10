package ru.otus.hw.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityOfEndpointsArgumentsProvider implements ArgumentsProvider {

    private final SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor user;

    private final ObjectMapper objectMapper;

    private final BookDto newBook;

    private final BookDto changeBook;

    private final CommentDto newComment;

    private final CommentDto changeComment;

    public SecurityOfEndpointsArgumentsProvider() {
        this.user = user("User");
        this.objectMapper = new ObjectMapper();
        this.newBook = new BookDto(BookTestData.getNewBook());
        this.changeBook = new BookDto(BookTestData.getChangeBook());
        this.newComment = new CommentDto(CommentTestData.getNewComment());
        this.changeComment = new CommentDto(CommentTestData.getChangeComment());
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws JsonProcessingException {


        return Stream.of(getBooksArgumentsStream(), getCommentsArgumentsStream(), getAuthorsArgumentsStream(),
                        getGenresArgumentsStream())
                .flatMap(i -> i);
    }

    private Stream<Arguments> getBooksArgumentsStream() throws JsonProcessingException {
        return Stream.of(
                Arguments.of(
                        "переходим на страницу со списком книг c user",
                        user,
                        get("/library/book"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком книг без user",
                        null,
                        get("/library/book"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу создания новой книги c user",
                        user,
                        get("/library/book/new"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу создания новой книги без user",
                        null,
                        get("/library/book/new"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу редактирования книги c user",
                        user,
                        get("/library/book/{id}", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу редактирования книги без user",
                        null,
                        get("/library/book/{id}", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем список книг c user",
                        user,
                        get("/api/library/book"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем список книг без user",
                        null,
                        get("/api/library/book"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем книгу по ID c user",
                        user,
                        get("/api/library/book/{id}", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем книгу по ID без user",
                        null,
                        get("/api/library/book/{id}", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "создаем новую книгу с user",
                        user,
                        post("/api/library/book").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newBook)),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "создаем новую книгу без user",
                        null,
                        post("/api/library/book").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newBook)),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "изменяем книгу с user",
                        user,
                        put("/api/library/book/{id}", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeBook)),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "изменяем книгу без user",
                        null,
                        put("/api/library/book/{id}", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeBook)),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "удаляем книгу с user",
                        user,
                        delete("/api/library/book/{id}", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "удаляем книгу без user",
                        null,
                        delete("/api/library/book/{id}", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }

    private Stream<Arguments> getCommentsArgumentsStream() throws JsonProcessingException {
        return Stream.of(
                Arguments.of(
                        "переходим на страницу со списком комментариев к книге c user",
                        user,
                        get("/library/book/{bookId}/comment", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком комментариев к книге без user",
                        null,
                        get("/library/book/{bookId}/comment", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу создания нового комментария к книге c user",
                        user,
                        get("/library/book/{bookId}/comment/new", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу создания нового комментария к книге без user",
                        null,
                        get("/library/book/{bookId}/comment/new", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу редактирования комментария к книге c user",
                        user,
                        get("/library/book/{bookId}/comment/{id}",
                                changeBook.getId(),
                                changeComment.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу редактирования комментария к книге без user",
                        null,
                        get("/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем список комментариев к книге c user",
                        user,
                        get("/api/library/book/{bookId}/comment", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем список комментариев к книге без user",
                        null,
                        get("/api/library/book/{bookId}/comment", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем комментарий к книге по ID c user",
                        user,
                        get("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем комментарий к книге по ID без user",
                        null,
                        get("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "создаем новый комметарий к книге с user",
                        user,
                        post("/api/library/book/{bookId}/comment", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newComment)),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "создаем новый комметарий к книге без user",
                        null,
                        post("/api/library/book/{bookId}/comment", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newComment)),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "изменяем комментарий к книге с user",
                        user,
                        put("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeComment)),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "изменяем комментарий к книге без user",
                        null,
                        put("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeComment)),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "удаляем комментарий к книге с user",
                        user,
                        delete("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "удаляем комментарий к книге без user",
                        null,
                        delete("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }

    private Stream<Arguments> getAuthorsArgumentsStream() {
        return Stream.of(
                Arguments.of(
                        "переходим на страницу со списком авторов c user",
                        user,
                        get("/library/author"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком авторов без user",
                        null,
                        get("/library/author"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем спискок авторов c user",
                        user,
                        get("/api/library/author"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем спискок авторов без user",
                        null,
                        get("/api/library/author"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }

    private Stream<Arguments> getGenresArgumentsStream() {
        return Stream.of(
                Arguments.of(
                        "переходим на страницу со списком жанров c user",
                        user,
                        get("/library/genre"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком жанров без user",
                        null,
                        get("/library/genre"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем спискок жанров c user",
                        user,
                        get("/api/library/genre"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем спискок жанров без user",
                        null,
                        get("/api/library/genre"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }
}
