package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequestMapping(GenreController.URL)
@RequiredArgsConstructor
public class GenreController {

    static final String URL = "/library/genres";

    private final GenreService genreService;

    @GetMapping()
    public String listGenresPage(Model model) {
        List<Genre> genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "genresList";
    }
}
