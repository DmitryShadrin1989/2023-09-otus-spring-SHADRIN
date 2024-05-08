package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/library")
    public String getHomePage() {
        return "homePage";
    }

    @GetMapping("/")
    public String getRedirectToHomePage() {
        return "redirect:/library";
    }
}
