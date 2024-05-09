package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    @Override
    @CircuitBreaker(name = "genreCircuitBreaker", fallbackMethod = "fallbackGenre")
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    private List<Genre> fallbackGenre(Exception ex) {
        log.error("An error occurred while getting the list of genres. Error message: %s".formatted(ex.getMessage()));
        return List.of(new Genre("", "Novel"));
    }
}
