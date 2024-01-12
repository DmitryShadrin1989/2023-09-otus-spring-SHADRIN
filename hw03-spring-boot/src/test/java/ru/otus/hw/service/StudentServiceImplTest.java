package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.domain.Student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.otus.hw.service.StudentTestData.getStudent1;

@DisplayName("Сервис для работы с сущностью Student ")
class StudentServiceImplTest {

    private LocalizedIOService ioService;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        ioService = mock(LocalizedIOServiceImpl.class);
        studentService = new StudentServiceImpl(ioService);
    }

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