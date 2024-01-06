package ru.otus.hw.service;

import ru.otus.hw.domain.Student;

public class StudentTestData {

    public static Student getStudent1() {
        return new Student("Dmitry", "Shadrin");
    }
}
