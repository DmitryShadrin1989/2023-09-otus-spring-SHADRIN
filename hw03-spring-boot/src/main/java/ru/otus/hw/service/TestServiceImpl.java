package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        for (var question: questions) {
            Answer answer = getAnswerToTheQuestion(question);
            var isAnswerValid = answer.isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private Answer getAnswerToTheQuestion(Question question) {
        ioService.printLine(question.text());
        AtomicInteger countAnswer = new AtomicInteger();
        List<Answer> answers = question.answers();
        String answersForPrint = answers.stream()
                .map(a -> countAnswer.incrementAndGet() + ": " + a.text())
                .collect(Collectors.joining(String.format("%n")));
        ioService.printLine(answersForPrint);
        int answerNum = ioService.readIntForRangeWithPromptLocalized(1, answers.size(),
                "TestService.answer.enter"
                ,"TestService.answer.error");
        return answers.get(answerNum - 1);
    }
}
