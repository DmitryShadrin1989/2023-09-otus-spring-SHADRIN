package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.domain.Student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.otus.hw.service.StudentTestData.getStudent1;

@DisplayName("Сервис для работы с сущностью Student ")
@SpringBootTest
class StudentServiceImplTest {

    @MockBean
    private LocalizedIOService ioService;

    @Autowired
    private StudentService studentService;

    @Test
    @DisplayName("Должен возвращать корректно созданный экземпляр класса Student ")
    void shouldReturnStudent() {
        Student expectedStudent = getStudent1();
        when(ioService.readStringWithPromptLocalized("StudentService.input.first.name")).thenReturn(expectedStudent.firstName());
        when(ioService.readStringWithPromptLocalized("StudentService.input.last.name")).thenReturn(expectedStudent.lastName());
        Student actualStudent = studentService.determineCurrentStudent();
        assertThat(actualStudent)
                .usingRecursiveComparison()
                .isEqualTo(expectedStudent);
    }
}