package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@RequiredArgsConstructor
@ShellComponent
public class TestRunnerServiceCommands {

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Start test", key = "start")
    public void startTest() {
        testRunnerService.run();
    }
}
