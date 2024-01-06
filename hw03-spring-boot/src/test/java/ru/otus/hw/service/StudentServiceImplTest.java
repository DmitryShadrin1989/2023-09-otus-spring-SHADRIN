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

    LocalizedIOService ioService;

    StudentService studentService;

    Student DB_STUDENT_1 = getStudent1();

    @BeforeEach
    void setUp() {
        ioService = mock(LocalizedIOServiceImpl.class);
        studentService = new StudentServiceImpl(ioService);
        when(ioService.readStringWithPromptLocalized("StudentService.input.first.name")).thenReturn(DB_STUDENT_1.firstName());
        when(ioService.readStringWithPromptLocalized("StudentService.input.last.name")).thenReturn(DB_STUDENT_1.lastName());
    }

    @Test
    @DisplayName("Должен возвращать корректно созданный экземпляр класса Student ")
    void shouldReturnStudent() {
        Student actualStudent = studentService.determineCurrentStudent();
        Student expectedStudent = new Student(DB_STUDENT_1.firstName(), DB_STUDENT_1.lastName());
        assertThat(actualStudent)
                .usingRecursiveComparison()
                .isEqualTo(expectedStudent);
    }
}