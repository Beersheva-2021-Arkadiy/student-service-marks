package telran.students.service.interfaces;

import telran.students.dto.*;

import java.util.List;

public interface StudentService {

    void addStudent(Student student);
    void addSubject(Subject subject);
    Mark addMark(Mark mark);

    List<StudentSubjectMark> getMarksStudentBySubject(String name, String subject);
    List<String> getBestStudents();
    List<String> getTopBestStudents(int nStudents);
    List<Student> getTopBestStudentsBySubject(int nStudents, String subject);
    List<StudentSubjectMark> getMarksOfWorstStudents(int nStudents);
    List<IntervalMarks> marksDistribution(int interval);
    List<String> jpqlQuery(String jpql);
    List<String> nativeQuery(String sql);
    List<Student> removeStudents(int avgMark, int nMarks);

}
