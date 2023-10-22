package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionTestData {
    public static Question getQuestion1() {
        String text = "Which option is a good way to handle the exception?";
        List<Answer> answerList = new ArrayList<>();
        answerList.add(new Answer("@SneakyThrow%false|e.printStackTrace()", false));
        answerList.add(new Answer("Rethrow with wrapping in business exception (for example, QuestionReadException)", true));
        answerList.add(new Answer("Ignoring exception", false));
        return new Question(text, answerList);
    }

    public static Question getQuestion2() {
        String text = "What is the deadline for completing homework on the course \"Developer on Spring Framework\" in OTUS?";
        List<Answer> answerList = new ArrayList<>();
        answerList.add(new Answer("One week", false));
        answerList.add(new Answer("Two months", false));
        answerList.add(new Answer("Before the end of the course", true));
        answerList.add(new Answer("Three days", false));
        return new Question(text, answerList);
    }
}
