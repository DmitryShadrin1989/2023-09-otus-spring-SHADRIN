package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/library/authors")
    public String listAuthorsPage(Model model) {
        List<Author> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authorsList";
    }
}
