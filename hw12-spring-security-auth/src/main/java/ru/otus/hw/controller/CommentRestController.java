package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/api/library/book/{bookId}/comment")
    public ResponseEntity<List<CommentDto>> getCommentsList(@PathVariable String bookId) {
        return ResponseEntity.ok(commentService.findAllByBookId(bookId));
    }

    @GetMapping("/api/library/book/{bookId}/comment/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable String bookId, @PathVariable String id) {
        return ResponseEntity.ok(commentService.findById(id));
    }

    @PutMapping("/api/library/book/{bookId}/comment/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable String bookId, @PathVariable String id,
                                                 @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(commentService.update(commentDto));
    }

    @PostMapping("/api/library/book/{bookId}/comment")
    public ResponseEntity<CommentDto> createComment(@PathVariable String bookId, @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(commentService.insert(commentDto));
    }

    @DeleteMapping("/api/library/book/{bookId}/comment/{id}")
    public void deleteComment(@PathVariable String bookId, @PathVariable String id) {
        commentService.deleteById(id);
    }
}
