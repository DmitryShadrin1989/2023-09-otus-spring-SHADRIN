package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BookController {

    @GetMapping("/library/book")
    public String getBooksListPage() {
        return "bookList";
    }

    @GetMapping("/library/book/new")
    public String getBookCreatePage(Model model) {
        model.addAttribute("bookId", null);
        return "book";
    }

    @GetMapping("/library/book/{id}")
    public String getBookEditPage(@PathVariable String id, Model model) {
        model.addAttribute("bookId", id);
        return "book";
    }
}
