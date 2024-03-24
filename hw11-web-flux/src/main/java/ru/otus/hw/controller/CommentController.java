package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class CommentController {

    @GetMapping("/library/book/{bookId}/comment")
    public String getCommentsListPage(@PathVariable String bookId, Model model) {
        model.addAttribute("bookId", bookId);
        return "commentList";
    }

    @GetMapping("/library/book/{bookId}/comment/new")
    public String getCommentCreatePage(@PathVariable String bookId, Model model) {
        model.addAttribute("bookId", bookId);
        return "comment";
    }

    @GetMapping("/library/book/{bookId}/comment/{id}")
    public String getCommentEditPage(@PathVariable String bookId, @PathVariable String id, Model model) {
        model.addAttribute("bookId", bookId);
        model.addAttribute("id", id);
        return "comment";
    }
}
