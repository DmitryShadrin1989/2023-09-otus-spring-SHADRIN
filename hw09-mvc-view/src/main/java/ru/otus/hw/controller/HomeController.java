package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(HomeController.URL)
@RequiredArgsConstructor
public class HomeController {

    static final String URL = "/library";

    @GetMapping()
    public String homePage(Model model) {
        return "homePage";
    }
}
