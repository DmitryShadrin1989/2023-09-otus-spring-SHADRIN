package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Controller
@RequestMapping(AuthorController.REST_URL)
@RequiredArgsConstructor
public class AuthorController {

    public static final String REST_URL = "/library/authors";

    private final AuthorService authorService;

    @GetMapping()
    public String listAuthorsPage(Model model) {
        List<Author> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authorsList";
    }
}
