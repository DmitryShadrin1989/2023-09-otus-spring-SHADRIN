package ru.otus.hw.services;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "authorCircuitBreaker", fallbackMethod = "fallbackAuthor")
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    private List<Author> fallbackAuthor(Exception ex) {
        log.error("An error occurred while getting the list of authors. Error message: %s".formatted(ex.getMessage()));
        return List.of(new Author("", "Alexandr Pushkin"));
    }
}
