package telran.students.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import telran.students.dto.QueryDto;
import telran.students.dto.QueryType;
import telran.students.dto.Student;
import telran.students.service.interfaces.*;
import java.util.*;

@RestController
@RequestMapping("/students")
public class StudentsRestController {

    StudentService studentService;

    @Autowired
    public StudentsRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/subject/mark")
    public List<StudentSubjectMark> getStudentSubjectMark(String name, String subject) {
        return studentService.getMarksStudentBySubject(name, subject);
    }

    @GetMapping("/best")
    public List<String> getBestStudents(@RequestParam(required = false, defaultValue = "0", name = "amount") int nStudents) {
        return nStudents == 0 ? studentService.getBestStudents() : studentService.getTopBestStudents(nStudents);
    }

    @PostMapping("/query")
    public List<String> getQueryResult(@RequestBody QueryDto queryDto) {
        return queryDto.type == QueryType.JPQL ? studentService.jpqlQuery(queryDto.query) :
                studentService.nativeQuery(queryDto.query);
    }

    @GetMapping("/worst/marks")
    public List<StudentSubjectMark> getMarksOfWorstStudents(@RequestParam ("amount")int nStudents) {
        return studentService.getMarksOfWorstStudents(nStudents);
    }
    @GetMapping("/distribution/marks")
    public List<IntervalMarks> getMarksDistribution(int interval) {
        return studentService.marksDistribution(interval);
    }

    @DeleteMapping("/delete")
    public List<Student> delete(@RequestParam("avgMark") int avgMark, @RequestParam("nMarks") int nMarks) {
        return studentService.removeStudents(avgMark, nMarks);
    }


}
