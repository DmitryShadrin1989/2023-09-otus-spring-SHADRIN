package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "commentCircuitBreaker", fallbackMethod = "fallbackGetComment")
    public CommentDto findById(String id) {
        return new CommentDto(commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Something went wrong and there is no such book anymore - %s".formatted(id))));
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "commentCircuitBreaker", fallbackMethod = "fallbackFindAllByBookIdComments")
    public List<CommentDto> findAllByBookId(String bookId) {
        return CommentDto.toDtoList(commentRepository.findAllByBookId(bookId));
    }

    @Override
    @CircuitBreaker(name = "commentCircuitBreaker", fallbackMethod = "fallbackCommentOperation")
    public CommentDto insert(CommentDto commentDto) {
        return new CommentDto(save(null, commentDto.getContent(),
                commentDto.getBookId()));
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "commentCircuitBreaker", fallbackMethod = "fallbackCommentOperation")
    public CommentDto update(CommentDto commentDto) {
        return new CommentDto(save(commentDto.getId(), commentDto.getContent(),
                commentDto.getBookId()));
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "commentCircuitBreaker", fallbackMethod = "fallbackDeleteComment")
    public void deleteById(String id) {
        findById(id);
        commentRepository.deleteById(id);
    }

    private Comment save(String id, String content, String bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        var comment = new Comment(id, content, book);
        return commentRepository.save(comment);
    }

    private CommentDto fallbackGetComment(String id, Exception ex) {
        log.error("An error occurred when receiving the comment by ID - %s. Error message: %s"
                .formatted(id, ex.getMessage()));
        return new CommentDto("", "Default content", "");
    }

    private List<CommentDto> fallbackFindAllByBookIdComments(String bookId, Exception ex) {
        log.error("An error occurred while getting the list of books by bookId - %s. Error message: %s"
                .formatted(bookId, ex.getMessage()));
        return List.of();
    }

    private CommentDto fallbackCommentOperation(CommentDto commentDto, Exception ex) {
        log.error("Error when trying to perform an operation with a comment - %s. Error message: %s"
                .formatted(commentDto.toString(), ex.getMessage()));
        return commentDto;
    }

    private void fallbackDeleteComment(String id, Exception ex) {
        log.error("An error occurred when deleting a comment by ID - %s. Error message: %s"
                .formatted(id, ex.getMessage()));
    }
}
