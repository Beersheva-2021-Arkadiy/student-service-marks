package telran.students.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telran.students.dto.*;
import telran.students.service.interfaces.*;
import java.util.*;

@RestController
@RequestMapping("/students")
public class StudentsRestController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

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

    @GetMapping("/best/subject")
    public List<StudentDto> getBestStudentsSubject(@RequestParam(name="amount") int nStudents, String subject ) {
        return studentService.getTopBestStudentsBySubject(nStudents, subject);
    }

    @PostMapping("/query")
    public List<String> getQueryResult(@RequestBody QueryDto queryDto) {
        return queryDto.type == QueryTypeDto.JPQL ? studentService.jpqlQuery(queryDto.query) :
                studentService.nativeQuery(queryDto.query);
    }

    @GetMapping("/worst/marks")
    public List<StudentSubjectMark> getMarksOfWorstStudents(@RequestParam ("amount") int nStudents) {
        return studentService.getMarksOfWorstStudents(nStudents);
    }
    @GetMapping("/distribution/marks")
    public List<IntervalMarks> getMarksDistribution(@RequestParam ("interval")int interval) {
        return studentService.marksDistribution(interval);
    }

    @DeleteMapping("/delete")
    public List<StudentDto> delete(@RequestParam("avgMark") int avgMark, @RequestParam("nMarks") int nMarks) {
        return studentService.removeStudents(avgMark, nMarks);
    }


}
