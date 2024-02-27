package ru.otus.hw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInsertUpdateDto;
import ru.otus.hw.dto.GenreToEditBookDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/library/books")
    public String listBooksPage(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "booksList";
    }

    @GetMapping("/library/books/create")
    public String createPage(Model model) {
        model.addAttribute("book", new BookInsertUpdateDto(null, "New book", null, null));
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", GenreToEditBookDto.toDtoList(genreService.findAll(), null));
        return "bookCreate";
    }

    @PostMapping("/library/books/create")
    public String createBook(@Valid @ModelAttribute("book") BookInsertUpdateDto book,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", GenreToEditBookDto.toDtoList(genreService.findAll(), book.getGenreIds()));
            return "bookCreate";
        }
        bookService.insert(book);
        return "redirect:/library/books";
    }

    @GetMapping("/library/books/edit/{id}")
    public String editPage(@PathVariable String id, Model model) {
        BookInsertUpdateDto book = BookInsertUpdateDto.toDto(bookService.findById(id));
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", GenreToEditBookDto.toDtoList(genreService.findAll(), book.getGenreIds()));
        return "bookEdit";
    }

    @PostMapping("/library/books/edit/{id}")
    public String editBook(@PathVariable String id, @Valid @ModelAttribute("book") BookInsertUpdateDto book,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", GenreToEditBookDto.toDtoList(genreService.findAll(), book.getGenreIds()));
            return "bookEdit";
        }
        bookService.update(book);
        return "redirect:/library/books";
    }

    @PostMapping("/library/books/delete/{id}")
    public String deleteBook(@PathVariable String id) {
        bookService.deleteById(id);
        return "redirect:/library/books";
    }
}
