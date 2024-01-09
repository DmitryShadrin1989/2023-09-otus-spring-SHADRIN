package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static ru.otus.hw.dao.QuestionTestData.*;

@DisplayName("Репозиторий для чтения вопросов с ответами из CSV файла ")
class CsvQuestionDaoTest {

    private QuestionDao questionDao;

    @BeforeEach
    void setUp() {
        TestFileNameProvider testFileNameProvider = mock(TestFileNameProvider.class);
        doReturn("questions.csv").when(testFileNameProvider).getTestFileName();
        questionDao = new CsvQuestionDao(testFileNameProvider);
    }

    @Test
    @DisplayName("Должен загружать список вопросов с ответами из CSV файла")
    void shouldReturnQuestionsList() {
        var actualQuestions = questionDao.findAll();
        var expectedQuestions = List.of(getQuestion1(), getQuestion2(), getQuestion3());
        assertThat(actualQuestions)
                .usingRecursiveComparison()
                .isEqualTo(expectedQuestions);
    }
}