package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.RoleRepository;

@Component
@RequiredArgsConstructor
public class LibraryHealthIndicator implements HealthIndicator {

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final GenreRepository genreRepository;

    private final RoleRepository roleRepository;

    @Override
    public Health health() {
        if (authorRepository.count() == 0 || bookRepository.count() == 0 || commentRepository.count() == 0
                || genreRepository.count() == 0 || roleRepository.count() == 0) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "The problem with the data")
                    .build();
        } else {
            return Health.up()
                    .status(Status.UP)
                    .withDetail("message", "All right")
                    .build();
        }
    }
}
