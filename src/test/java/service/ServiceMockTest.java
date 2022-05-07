package service;

import domain.Grade;
import domain.Homework;
import domain.Student;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

public class ServiceMockTest {
    private Service service;
    private StudentXMLRepository studentXMLRepository;
    private HomeworkXMLRepository homeworkXMLRepository;
    private GradeXMLRepository gradeXMLRepository;

    @Before
    public void setup() {
        studentXMLRepository = mock(StudentXMLRepository.class);
        homeworkXMLRepository = mock(HomeworkXMLRepository.class);
        gradeXMLRepository = mock(GradeXMLRepository.class);

        service = new Service(studentXMLRepository, homeworkXMLRepository, gradeXMLRepository);
    }

    @Test
    @DisplayName("Can't create student because id is null")
    public void studentIdNull() {
        Student student = new Student(null, "Gipsz Jakab", 533);
        when(studentXMLRepository.save(student)).thenReturn(student);
        assertEquals(1, service.saveStudent(null, "Gipsz Jakab", 533));
    }

    @Test
    @DisplayName("Can't create homework earlier deadline than startline")
    public void homeworkWithBadDeadlineStartline() {
        Homework homework = new Homework("0", "Bad Homework", 2, 7);
        when(homeworkXMLRepository.save(homework)).thenReturn(homework);
        assertNotEquals(0, service.saveHomework("0", "Bad Homework", 2, 7));

        verify(homeworkXMLRepository).save(homework);
    }

    @Test
    @DisplayName("Can create grade between [0, 10] interval")
    public void shouldCreateGrade() {
        Student student = new Student("12", "Random Student", 533);
        Homework homework = new Homework("13", "Just a random homework", 7, 3);

        when(homeworkXMLRepository.findOne(anyString())).thenReturn(homework);
        when(studentXMLRepository.findOne(anyString())).thenReturn(student);
        when(gradeXMLRepository.save(any(Grade.class))).thenReturn(null);

        assertEquals(0, service.saveGrade("12", "13", 8, 7, ""));
    }
}
