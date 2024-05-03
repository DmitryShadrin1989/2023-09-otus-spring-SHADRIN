package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.services.UserService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping("/api/library/user/current")
    public ResponseEntity<Map<String, String>> getCurrentUsername() {
        return ResponseEntity.ok(Map.of("userName", userService.getCurrentUsername()));
    }
}
