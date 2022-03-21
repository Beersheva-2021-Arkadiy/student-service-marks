package telran.students.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telran.students.dto.*;
import telran.students.jpa.entities.*;
import telran.students.jpa.repo.*;
import telran.students.service.interfaces.*;
import javax.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class StudentServiceJpa implements StudentService {

    StudentsRepository studentsRepository;
    SubjectsRepository subjectsRepository;
    MarksRepository marksRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public StudentServiceJpa(StudentsRepository studentsRepository, SubjectsRepository subjectsRepository, MarksRepository marksRepository) {
        this.studentsRepository = studentsRepository;
        this.subjectsRepository = subjectsRepository;
        this.marksRepository = marksRepository;
    }

    @Override
    public void addStudent(Student student) {
        studentsRepository.save(StudentJpa.build(student));
    }

    @Override
    public void addSubject(Subject subject) {
        subjectsRepository.save(SubjectJpa.build(subject));
    }

    @Override
    @Transactional
    public Mark addMark(Mark mark) {
        StudentJpa studentJpa = studentsRepository.findById(mark.stid).orElse(null);
        SubjectJpa subjectJpa = subjectsRepository.findById(mark.suid).orElse(null);
        if (studentJpa != null && subjectJpa != null) {
            marksRepository.save(new MarkJpa(mark.value, studentJpa, subjectJpa));
            return mark;
        }
        return null;
    }

    @Override
    public List<StudentSubjectMark> getMarksStudentBySubject(String name, String subject) {
        return marksRepository.findByStudentJpaNameAndSubjectJpaSubject(name, subject);
    }

    @Override
    public List<String> getBestStudents() {
        return marksRepository.findBestStudents();
    }

    @Override
    public List<String> getTopBestStudents(int nStudents) {
        return marksRepository.findTopBestStudents(nStudents);
    }

    @Override
    public List<Student> getTopBestStudentsBySubject(int nStudents, String subject) {
        return studentsRepository.findTopBestStudentsSubject(nStudents, subject)
                .stream().map(StudentJpa::toStudentDto).toList();
    }

    @Override
    public List<StudentSubjectMark> getMarksOfWorstStudents(int nStudents) {
        return marksRepository.findMarksOfWorstStudents(nStudents);
    }

    @Override
    public List<IntervalMarks> marksDistribution(int interval) {
        return marksRepository.findMarksDistribution(interval);
    }

    @Override
    public List<String> jpqlQuery(String jpql) {
        Query query = entityManager.createQuery(jpql);
        return getResult(query);
    }

    private List<String> simpleQuery(List result) {
        return result.stream().map(Object::toString).toList();
    }

    private List<String> multiProjectionRequest(List<Object[]> result) {
        return result.stream().map(Arrays::deepToString).toList();
    }

    @Override
    public List<String> nativeQuery(String sql) {
        return (List<String>) entityManager.createNativeQuery(sql).getResultList();
    }

    @Override
    @Transactional
    public List<Student> removeStudents(int avgMark, int nMarks) {
        List<StudentJpa> listJpa = studentsRepository.findStudentsForDeletion(avgMark, nMarks);
        listJpa.forEach(studentsRepository::delete);
        return listJpa.stream().map(StudentJpa::toStudentDto).toList();
    }

    private List<String> getResult(Query query) {
        List result = query.getResultList();
        if (result.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return result.get(0).getClass().isArray() ? multiProjectionRequest(result) :
                simpleQuery(result);
    }

}
