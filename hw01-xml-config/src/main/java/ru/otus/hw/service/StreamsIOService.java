package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.io.PrintStream;
import java.util.List;

public class StreamsIOService implements IOService {
    private final PrintStream printStream;

    public StreamsIOService(PrintStream printStream) {

        this.printStream = printStream;
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }

    @Override
    public void printQuestionsWithAnswers(List<Question> questions) {
        for (Question question : questions) {
            StringBuilder questionBuilder = new StringBuilder(question.text());
            questionBuilder.append("%n").append("%s%n".repeat(question.answers().size()));
            printFormattedLine(questionBuilder.toString(), question.answers().stream()
                    .map(Answer::text).toList().toArray());
        }
    }
}
