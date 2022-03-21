package telran.students.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import telran.students.jpa.entities.MarkJpa;
import telran.students.service.interfaces.IntervalMarks;
import telran.students.service.interfaces.StudentSubjectMark;
import java.util.List;

public interface MarksRepository extends JpaRepository<MarkJpa, Integer> {
    List<StudentSubjectMark> findByStudentJpaNameAndSubjectJpaSubject(String name, String subject);

    @Query("select s.name, round(avg(m.mark)) from MarkJpa m join m.studentJpa s group by s.name having avg(m.mark) > (select avg(mark) from MarkJpa) order by avg (m.mark) desc")
    List<String> findBestStudents();

    @Query(value = "select s.name, round(avg(mark)) from marks join students s on student_jpa_stid = stid group by s.name order by avg(mark) desc limit :nStudents", nativeQuery = true)
    List<String> findTopBestStudents(@Param("nStudents") int nStudents);

    @Query(value="select name as studentName,"
            + " subject as subjectSubject, mark from marks join students "
            + "on stid=student_jpa_stid join subjects on subject_jpa_suid=suid "
            + "where stid in (select stid from marks join students "
            + "on student_jpa_stid=stid group by stid order by avg(mark) limit :nStudents)",
            nativeQuery=true)
    List<StudentSubjectMark> findMarksOfWorstStudents(@Param("nStudents") int nStudents);

    @Query(value="select mark/:interval * :interval as min,"
            + " :interval * (mark/:interval + 1) - 1 as max, count(*) as occurrences from "
            + " marks  group by min,  max order by min", nativeQuery=true)
    List<IntervalMarks> findMarksDistribution(int interval);
}
