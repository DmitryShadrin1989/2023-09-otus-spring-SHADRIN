package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConverter commentConverter;

    //ac 1
    @ShellMethod(value = "Find all comments for book", key = "ac")
    public String findAllCommentsByBookId(long bookId) {
        return commentService.findAllByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    //cbid 1
    @ShellMethod(value = "Find comment by id", key = "cbid")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    //cins aaaaaaaaaaaaa 1
    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String content, long bookId) {
        var saveComment = commentService.insert(content, bookId);
        return commentConverter.commentToString(saveComment);
    }

    //cupd 1 change_content 1
    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(long id, String content, long bookId) {
        var saveComment = commentService.update(id, content, bookId);
        return commentConverter.commentToString(saveComment);
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }
}
