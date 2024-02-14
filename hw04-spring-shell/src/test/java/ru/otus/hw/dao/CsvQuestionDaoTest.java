package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.LocaleConfig;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.config.TestFileNameProvider;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static ru.otus.hw.dao.QuestionTestData.*;

@DisplayName("Репозиторий для чтения вопросов с ответами из CSV файла ")
@SpringBootTest
class CsvQuestionDaoTest {
    @MockBean
    private TestFileNameProvider testFileNameProvider;

    @MockBean
    private LocaleConfig localeConfig;

    @MockBean
    private TestConfig testConfig;

    @Autowired
    private QuestionDao questionDao;

    @Test
    @DisplayName("Должен загружать список вопросов с ответами из CSV файла")
    void shouldReturnQuestionsList() {
        doReturn("questions.csv").when(testFileNameProvider).getTestFileName();
        var actualQuestions = questionDao.findAll();
        var expectedQuestions = List.of(getQuestion1(), getQuestion2(), getQuestion3());
        assertThat(actualQuestions)
                .usingRecursiveComparison()
                .isEqualTo(expectedQuestions);
    }
}