package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAllByBookId(String bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    @Override
    @Transactional
    public Comment insert(String content, String bookId) {
        return save(null, content, bookId);
    }

    @Override
    @Transactional
    public Comment update(String id, String content, String bookId) {
        return save(id, content, bookId);
    }

    @Override
    @Transactional
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
}
