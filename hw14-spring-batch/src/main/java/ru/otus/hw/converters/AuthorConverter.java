package ru.otus.hw.converters;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorJdbcDto;
import ru.otus.hw.models.Author;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthorConverter {

    private final Map<Long, Author> authorIdsMap;

    public AuthorConverter() {
        this.authorIdsMap = new ConcurrentHashMap<>();
    }

    public String authorToString(Author author) {
        return "Id: %s, FullName: %s".formatted(author.getId(), author.getFullName());
    }

    public Author getAuthor(Long authorJdbcDtoId) {
        return authorIdsMap.get(authorJdbcDtoId);
    }

    public Author convertToDomain(AuthorJdbcDto authorJdbcDto) {
        String authorId = new ObjectId().toString();
        Author author = new Author(authorId, authorJdbcDto.getFullName());
        authorIdsMap.put(authorJdbcDto.getId(), author);
        return author;
    }
}
