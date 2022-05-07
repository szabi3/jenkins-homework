package service;

import domain.Grade;
import domain.Homework;
import domain.Student;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import validation.GradeValidator;
import validation.HomeworkValidator;
import validation.StudentValidator;
import validation.Validator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    Service service;

    Validator<Student> studentValidator;
    Validator<Homework> homeworkValidator;
    Validator<Grade> gradeValidator;

    StudentXMLRepository studentXMLRepository;
    HomeworkXMLRepository homeworkXMLRepository;
    GradeXMLRepository gradeXMLRepository;

    @BeforeEach
    public void initialize() {
        studentValidator = new StudentValidator();
        homeworkValidator = new HomeworkValidator();
        gradeValidator = new GradeValidator();

        studentXMLRepository = new StudentXMLRepository(studentValidator, "student.xml");
        homeworkXMLRepository = new HomeworkXMLRepository(homeworkValidator, "homework.xml");
        gradeXMLRepository = new GradeXMLRepository(gradeValidator, "grades.xml");

        service = new Service(studentXMLRepository, homeworkXMLRepository, gradeXMLRepository);
    }


    @Test
    @DisplayName("Can't create student because id is null")
    public void studentIdNull() {
        assertEquals(1, service.saveStudent(null, "Gipsz Jakab", 533));
    }

    @Test
    @DisplayName("Can't create student with a groupID equal or lower than 110")
    public void studentLowGroupId() {
        assertNotEquals(0, service.saveStudent("0", "Random name", 109));
    }

    @Test
    @DisplayName("Can't create student with a groupID equal or higher than 938")
    public void studentHighGroupId() {
        assertTrue(service.saveStudent("0", "Nagy Lajos", 938) == 1);
    }

    @DisplayName("Can create grades between [0, 10] interval")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    public void shouldCreateGrades(Integer grade) {
        assertAll(
                () -> assertEquals(0, service.saveStudent("0", "Fekete Peter", 523)),
                () -> assertNotEquals(1, service.saveHomework("1", "IoS homework", 7, 3)),
                () -> assertEquals(0, service.saveGrade("0", "1", grade, 7, ""))
        );
    }

    @Test
    @DisplayName("Can't create homework earlier deadline than startline")
    public void homeworkWithBadDeadlineStartline() {
        assertNotEquals(0, service.saveHomework("12", "Bad Homework", 2, 7));
    }

    @BeforeEach
    public void clear() {
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<Entities>\n</Entities>";
        try {
            PrintWriter writerGrades = new PrintWriter("grades.xml");
            writerGrades.print(text);
            writerGrades.close();
            PrintWriter writerStudent = new PrintWriter("student.xml");
            writerStudent.print(text);
            writerStudent.close();
            PrintWriter writerHomework = new PrintWriter("homework.xml");
            writerHomework.print(text);
            writerHomework.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error in clearing the xmls");
        }
    }
}