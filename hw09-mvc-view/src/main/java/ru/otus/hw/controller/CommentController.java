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
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentInsertUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final BookService bookService;

    private final CommentService commentService;

    @GetMapping("/library/comments")
    public String listCommentsPage(@RequestParam("bookId") String bookId, Model model) {
        List<Comment> comments = commentService.findAllByBookId(bookId);
        BookDto book = bookService.findById(bookId);
        model.addAttribute("comments", comments);
        model.addAttribute("book", book);
        return "commentListForBook";
    }

    @GetMapping("/library/comments/create")
    public String createPage(@RequestParam("bookId") String bookId, Model model) {
        model.addAttribute("comment", new  CommentInsertUpdateDto(null, null, bookId));
        model.addAttribute("books", bookService.findAll());
        return "commentCreate";
    }

    @PostMapping("/library/comments/create")
    public String createComment(@Valid @ModelAttribute("comment") CommentInsertUpdateDto comment,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("books", bookService.findAll());
            return "commentCreate";
        }
        commentService.insert(comment);
        return "redirect:/library/comments?bookId=" + comment.getBookId();
    }

    @GetMapping("/library/comments/edit/{id}")
    public String editPage(@PathVariable String id, Model model) {
        model.addAttribute("comment", CommentInsertUpdateDto.toDto(commentService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Something went wrong and there is no such book anymore - %s".formatted(id)))));
        model.addAttribute("books", bookService.findAll());
        return "commentEdit";
    }

    @PostMapping("/library/comments/edit/{id}")
    public String editComment(@PathVariable String id, @Valid @ModelAttribute("comment") CommentInsertUpdateDto comment,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("books", bookService.findAll());
            return "commentEdit";
        }
        commentService.update(comment);
        return "redirect:/library/comments?bookId=" + comment.getBookId();
    }

    @PostMapping("/library/comments/delete/{id}")
    public String deleteComment(@PathVariable String id, @RequestParam("bookId") String bookId) {
        commentService.deleteById(id);
        return "redirect:/library/comments?bookId=" + bookId;
    }
}
