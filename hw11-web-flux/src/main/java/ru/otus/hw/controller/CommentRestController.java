package ru.otus.hw.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @GetMapping("/api/library/book/{bookId}/comment")
    public Flux<CommentDto> getCommentsList(@PathVariable String bookId) {
        return commentRepository.findAllByBookId(bookId).map(CommentDto::new);
    }

    @GetMapping("/api/library/book/{bookId}/comment/{id}")
    public Mono<CommentDto> getComment(@PathVariable String bookId, @PathVariable String id) {
        return commentRepository.findById(id).map(CommentDto::new);
    }

    @PutMapping("/api/library/book/{bookId}/comment/{id}")
    public Mono<CommentDto> updateComment(@PathVariable String bookId, @PathVariable String id,
                                                 @RequestBody CommentDto commentDto) {
        return Mono.zip(
                commentRepository.findById(commentDto.getId())
                        .switchIfEmpty(Mono.error(new NotFoundException("Comment with id %s not found"
                                .formatted(commentDto.getId())))),
                bookRepository.findById(commentDto.getBookId())
                        .switchIfEmpty(Mono.error(new NotFoundException("Book with id %s not found"
                                .formatted(commentDto.getBookId()))))
        ).flatMap(data -> {
            String changeContent = commentDto.getContent();
            Book changeBook = data.getT2();
            return commentRepository.save(new Comment(commentDto.getId(), changeContent, changeBook));
        }).map(CommentDto::new);

    }

    @PostMapping("/api/library/book/{bookId}/comment")
    public Mono<CommentDto> createComment(@PathVariable String bookId, @RequestBody CommentDto commentDto) {
        return bookRepository.findById(commentDto.getBookId())
                        .switchIfEmpty(Mono.error(new NotFoundException("Book with id %s not found"
                                .formatted(commentDto.getBookId()))))
        .flatMap(book -> {
            String content = commentDto.getContent();
            return commentRepository.save(new Comment(null, content, book));
        }).map(CommentDto::new);
    }

    @DeleteMapping("/api/library/book/{bookId}/comment/{id}")
    public Mono<Void> deleteComment(@PathVariable String bookId, @PathVariable String id) {
        return commentRepository.deleteById(id);
    }
}
