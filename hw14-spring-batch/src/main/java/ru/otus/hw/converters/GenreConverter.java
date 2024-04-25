package ru.otus.hw.converters;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreJdbcDto;
import ru.otus.hw.models.Genre;

import java.util.HashMap;
import java.util.Map;

@Component
public class GenreConverter {

    private final Map<Long, Genre> genreIdsMap;

    public GenreConverter() {
        this.genreIdsMap = new HashMap<>();
    }

    public String genreToString(Genre genre) {
        return "Id: %s, Name: %s".formatted(genre.getId(), genre.getName());
    }

    public Genre getGenre(Long genreJdbcDtoId) {
        return genreIdsMap.get(genreJdbcDtoId);
    }

    public Genre convertToDomain(GenreJdbcDto genreJdbcDto) {
        String genreId = new ObjectId().toString();
        Genre genre = new Genre(genreId, genreJdbcDto.getName());
        genreIdsMap.put(genreJdbcDto.getId(), genre);
        return genre;
    }
}
