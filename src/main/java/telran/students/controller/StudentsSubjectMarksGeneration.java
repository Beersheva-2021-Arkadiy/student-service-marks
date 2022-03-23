package telran.students.controller;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;
import telran.students.dto.*;
import telran.students.service.interfaces.StudentService;
import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

//@Component
public class StudentsSubjectMarksGeneration {

    static Logger LOG = LoggerFactory.getLogger("Generation");

    @Value("${app.generation.amount:100}")
    int nMarks;

    @Autowired
    StudentService studentService;

    String[] names = {"Abraham", "Sarah", "Itshak", "Rahel", "Asaf", "Yacob","Rivka", "Yosef",
            "Benyanim", "Dan", "Ruben", "Moshe", "Aron", "Yehashua", "David", "Salomon", "Nefertity",
            "Naftaly", "Natan","Asher"};
    String[] subjects = {"Java core", "Java Technologies", "Spring Data", "Spring Security", "Spring Cloud",
            "CSS", "HTML", "JS", "React", "Material-UI"};

    @PostConstruct
    void fillDatabase() {
        addStudents();
        addSubjects();
        addMarks();
        LOG.info("Database filled by random values.");
    }

    private void addStudents() {
        IntStream.range(0, names.length)
                .forEach(index -> {
                    studentService.addStudent(new StudentDto(index + 1, names[index]));
                });
    }

    private void addSubjects() {
        IntStream.range(0, subjects.length)
                .forEach(index -> {
                    studentService.addSubject(new SubjectDto(index + 1, subjects[index]));
                });
    }

    private void addMarks() {
        IntStream.range(0, nMarks).forEach(index -> addOneMark());
    }

    private void addOneMark() {
        int studentId = getRandomNumber(1, names.length);
        int subjectId = getRandomNumber(1, subjects.length);
        int mark = getRandomNumber(60, 100);
        studentService.addMark(new MarkDto(studentId, subjectId, mark));
    }

    private int getRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}
