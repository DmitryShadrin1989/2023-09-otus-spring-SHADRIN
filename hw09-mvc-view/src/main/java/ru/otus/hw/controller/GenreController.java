package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/library/genres")
    public String listGenresPage(Model model) {
        List<Genre> genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "genresList";
    }
}
