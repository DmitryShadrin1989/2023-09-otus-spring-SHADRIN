package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentJdbcDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

@RequiredArgsConstructor
@Component
public class CommentConverter {

    public String commentToString(Comment comment) {
        return "Id: %s, content: %s".formatted(
                comment.getId(),
                comment.getContent());
    }

    public Comment convertToDomain(CommentJdbcDto commentJdbcDto) {
        return new Comment(null, commentJdbcDto.getContent(),
                new Book(commentJdbcDto.getBookId(), null, null, null));
    }
}
