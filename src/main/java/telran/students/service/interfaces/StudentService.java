package telran.students.service.interfaces;

import telran.students.dto.*;

import java.util.List;

public interface StudentService {

    void addStudent(StudentDto studentDto);
    void addSubject(SubjectDto subjectDto);
    MarkDto addMark(MarkDto markDto);

    List<StudentSubjectMark> getMarksStudentBySubject(String name, String subject);
    List<String> getBestStudents();
    List<String> getTopBestStudents(int nStudents);
    List<StudentDto> getTopBestStudentsBySubject(int nStudents, String subject);
    List<StudentSubjectMark> getMarksOfWorstStudents(int nStudents);
    List<IntervalMarks> marksDistribution(int interval);
    List<String> jpqlQuery(String jpql);
    List<String> nativeQuery(String sql);
    List<StudentDto> removeStudents(int avgMark, int nMarks);

}
