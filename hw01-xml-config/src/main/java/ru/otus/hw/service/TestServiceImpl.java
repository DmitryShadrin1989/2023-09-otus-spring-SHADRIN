package ru.otus.hw.service;


import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        printQuestionsWithAnswers(questionDao.findAll());
    }

    public void printQuestionsWithAnswers(List<Question> questions) {
        for (Question question : questions) {
            StringBuilder questionBuilder = new StringBuilder(question.text());
            questionBuilder.append("%n").append("%s%n".repeat(question.answers().size()));
            ioService.printFormattedLine(questionBuilder.toString(), question.answers().stream()
                    .map(Answer::text).toList().toArray());
        }
    }
}
