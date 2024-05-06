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

    private final SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userGuest;

    private final SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userReader;

    private final SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userAdmin;

    private final ObjectMapper objectMapper;

    private final BookDto newBook;

    private final BookDto changeBook;

    private final CommentDto newComment;

    private final CommentDto changeComment;

    public SecurityOfEndpointsArgumentsProvider() {
        this.userGuest = user("Mike Guest").roles("GUEST");
        this.userReader = user("Mike Guest").roles("READER");
        this.userAdmin = user("Mike Guest").roles("ADMIN");
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
                        "переходим на страницу со списком книг. Пользователь с ролью GUEST",
                        userGuest,
                        get("/library/book"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком книг без пользователя",
                        null,
                        get("/library/book"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу создания новой книги. Пользователь с ролью ADMIN",
                        userAdmin,
                        get("/library/book/new"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу создания новой книги. Пользователь с ролью READER",
                        userReader,
                        get("/library/book/new"),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "переходим на страницу создания новой книги. Пользователь с ролью GUEST",
                        userGuest,
                        get("/library/book/new"),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "переходим на страницу создания новой книги без пользователя",
                        null,
                        get("/library/book/new"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу редактирования книги. Пользователь с ролью ADMIN",
                        userAdmin,
                        get("/library/book/{id}", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу редактирования книги. Пользователь с ролью READER",
                        userReader,
                        get("/library/book/{id}", changeBook.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "переходим на страницу редактирования книги. Пользователь с ролью GUEST",
                        userGuest,
                        get("/library/book/{id}", changeBook.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "переходим на страницу редактирования книги без пользователя",
                        null,
                        get("/library/book/{id}", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем список книг. Пользователь с ролью GUEST",
                        userGuest,
                        get("/api/library/book"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем список книг без пользователя",
                        null,
                        get("/api/library/book"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем книгу по ID. Пользователь с ролью GUEST",
                        userGuest,
                        get("/api/library/book/{id}", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем книгу по ID без пользователя",
                        null,
                        get("/api/library/book/{id}", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "создаем новую книгу. Пользователь с ролью ADMIN",
                        userAdmin,
                        post("/api/library/book").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newBook)),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "создаем новую книгу. Пользователь с ролью READER",
                        userReader,
                        post("/api/library/book").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newBook)),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "создаем новую книгу. Пользователь с ролью GUEST",
                        userGuest,
                        post("/api/library/book").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newBook)),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "создаем новую книгу без пользователя",
                        null,
                        post("/api/library/book").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newBook)),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "изменяем книгу. Пользователь с ролью ADMIN",
                        userAdmin,
                        put("/api/library/book/{id}", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeBook)),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "изменяем книгу. Пользователь с ролью READER",
                        userReader,
                        put("/api/library/book/{id}", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeBook)),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "изменяем книгу. Пользователь с ролью GUEST",
                        userGuest,
                        put("/api/library/book/{id}", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeBook)),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "изменяем книгу без пользователя",
                        null,
                        put("/api/library/book/{id}", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeBook)),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "удаляем книгу. Пользователь с ролью ADMIN",
                        userAdmin,
                        delete("/api/library/book/{id}", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "удаляем книгу. Пользователь с ролью READER",
                        userReader,
                        delete("/api/library/book/{id}", changeBook.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "удаляем книгу. Пользователь с ролью GUEST",
                        userGuest,
                        delete("/api/library/book/{id}", changeBook.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "удаляем книгу без пользователя",
                        null,
                        delete("/api/library/book/{id}", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }

    private Stream<Arguments> getCommentsArgumentsStream() throws JsonProcessingException {
        return Stream.of(
                Arguments.of(
                        "переходим на страницу со списком комментариев к книге. Пользователь с ролью READER",
                        userReader,
                        get("/library/book/{bookId}/comment", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком комментариев к книге. Пользователь с ролью GUEST",
                        userGuest,
                        get("/library/book/{bookId}/comment", changeBook.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "переходим на страницу со списком комментариев к книге без пользователя",
                        null,
                        get("/library/book/{bookId}/comment", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу создания нового комментария к книге. " +
                                "Пользователь с ролью READER",
                        userReader,
                        get("/library/book/{bookId}/comment/new", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу создания нового комментария к книге. " +
                                "Пользователь с ролью GUEST",
                        userGuest,
                        get("/library/book/{bookId}/comment/new", changeBook.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "переходим на страницу создания нового комментария к книге без пользователя",
                        null,
                        get("/library/book/{bookId}/comment/new", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу редактирования комментария к книге. " +
                                "Пользователь с ролью ADMIN",
                        userAdmin,
                        get("/library/book/{bookId}/comment/{id}",
                                changeBook.getId(),
                                changeComment.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу редактирования комментария к книге. " +
                                "Пользователь с ролью READER",
                        userReader,
                        get("/library/book/{bookId}/comment/{id}",
                                changeBook.getId(),
                                changeComment.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "переходим на страницу редактирования комментария к книге. " +
                                "Пользователь с ролью GUEST",
                        userGuest,
                        get("/library/book/{bookId}/comment/{id}",
                                changeBook.getId(),
                                changeComment.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "переходим на страницу редактирования комментария к книге без пользователя",
                        null,
                        get("/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем список комментариев к книге. Пользователь с ролью READER",
                        userReader,
                        get("/api/library/book/{bookId}/comment", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем список комментариев к книге. Пользователь с ролью GUEST",
                        userGuest,
                        get("/api/library/book/{bookId}/comment", changeBook.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "получаем список комментариев к книге без пользователя",
                        null,
                        get("/api/library/book/{bookId}/comment", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем комментарий к книге по ID. Пользователь с ролью READER",
                        userReader,
                        get("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем комментарий к книге по ID. Пользователь с ролью GUEST",
                        userGuest,
                        get("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "получаем комментарий к книге по ID без пользователя",
                        null,
                        get("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "создаем новый комметарий к книге. Пользователь с ролью READER",
                        userReader,
                        post("/api/library/book/{bookId}/comment", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newComment)),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "создаем новый комметарий к книге. Пользователь с ролью GUEST",
                        userGuest,
                        post("/api/library/book/{bookId}/comment", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newComment)),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "создаем новый комметарий к книге без пользователя",
                        null,
                        post("/api/library/book/{bookId}/comment", changeBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newComment)),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "изменяем комментарий к книге. Пользователь с ролью ADMIN",
                        userAdmin,
                        put("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeComment)),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "изменяем комментарий к книге. Пользователь с ролью READER",
                        userReader,
                        put("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeComment)),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "изменяем комментарий к книге. Пользователь с ролью GUEST",
                        userGuest,
                        put("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeComment)),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "изменяем комментарий к книге без пользователя",
                        null,
                        put("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(changeComment)),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "удаляем комментарий к книге. Пользователь с ролью ADMIN",
                        userAdmin,
                        delete("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "удаляем комментарий к книге. Пользователь с ролью READER",
                        userReader,
                        delete("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "удаляем комментарий к книге. Пользователь с ролью GUEST",
                        userGuest,
                        delete("/api/library/book/{bookId}/comment/{id}",
                                changeBook.getId(), changeComment.getId()),
                        List.of(status().isForbidden())
                ),
                Arguments.of(
                        "удаляем комментарий к книге без пользователя",
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
                        "переходим на страницу со списком авторов. Пользователь с ролью GUEST",
                        userGuest,
                        get("/library/author"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком авторов без пользователя",
                        null,
                        get("/library/author"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем спискок авторов. Пользователь с ролью GUEST",
                        userGuest,
                        get("/api/library/author"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем спискок авторов без пользователя",
                        null,
                        get("/api/library/author"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }

    private Stream<Arguments> getGenresArgumentsStream() {
        return Stream.of(
                Arguments.of(
                        "переходим на страницу со списком жанров. Пользователь с ролью GUEST",
                        userGuest,
                        get("/library/genre"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком жанров без пользователя",
                        null,
                        get("/library/genre"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "получаем спискок жанров. Пользователь с ролью GUEST",
                        userGuest,
                        get("/api/library/genre"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "получаем спискок жанров без пользователя",
                        null,
                        get("/api/library/genre"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }
}
