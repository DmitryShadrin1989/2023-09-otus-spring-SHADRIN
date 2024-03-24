package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

@RestController
@RequiredArgsConstructor
public class GenreRestController {

    private final GenreRepository genreRepository;

    @GetMapping("/api/library/genre")
    public Flux<Genre> getListGenres() {
        return genreRepository.findAll();
    }
}