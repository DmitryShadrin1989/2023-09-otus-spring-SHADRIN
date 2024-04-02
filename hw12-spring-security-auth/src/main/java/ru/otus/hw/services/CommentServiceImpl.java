package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
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
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public CommentDto findById(String id) {
        return new CommentDto(commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Something went wrong and there is no such book anymore - %s".formatted(id))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAllByBookId(String bookId) {
        return CommentDto.toDtoList(commentRepository.findAllByBookId(bookId));
    }

    @Override
    public CommentDto insert(CommentDto commentDto) {
        return new CommentDto(save(null, commentDto.getContent(),
                commentDto.getBookId()));
    }

    @Override
    @Transactional
    public CommentDto update(CommentDto commentDto) {
        return new CommentDto(save(commentDto.getId(), commentDto.getContent(),
                commentDto.getBookId()));
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
