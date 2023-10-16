package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.otus.hw.service.QuestionTestData.QUESTION_1;
import static ru.otus.hw.service.QuestionTestData.QUESTION_2;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {
    @Mock
    private IOService ioService;
    @Mock
    private QuestionDao questionDao;
    @InjectMocks
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        given(questionDao.findAll()).willReturn(List.of(QUESTION_1, QUESTION_2));
    }

    @Test
    void executeTest() {
        testService.executeTest();
        verify(ioService, times(1)).printLine("");
        verify(questionDao, times(1)).findAll();
    }
}