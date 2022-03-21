package telran.students.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import telran.students.dto.Student;
import telran.students.jpa.entities.StudentJpa;

import java.util.List;

public interface StudentsRepository extends JpaRepository<StudentJpa, Integer> {
    @Query(value="select stid, name from students join marks on stid=student_stid "
            + "join subjects on suid=subject_suid where subject=:subject "
            + "group by stid, name order by avg(mark) desc limit :nStudents", nativeQuery=true)
    List<StudentJpa> findTopBestStudentsSubject(@Param("nStudents") int nStudents,
                                                @Param("subject") String subject);

    @Modifying
    @Query("DELETE FROM StudentJpa WHERE stid " +
            "in(SELECT m.studentJpa.stid FROM MarkJpa m GROUP BY m.studentJpa.stid" +
            " HAVING avg(m.mark) < :avgMArk AND COUNT(*) < :nMarks)")
    int deleteStudents(@Param("avgMArk") double avgMark, @Param("nMarks") long nMarks);

    @Query("SELECT FROM StudentJpa s WHERE stid " +
            "in(SELECT m.studentJpa.stid FROM MarkJpa m GROUP BY m.studentJpa.stid" +
            " HAVING avg(m.mark) < :avgMArk AND COUNT(*) < :nMarks)")
    List<StudentJpa> findStudentsForDeletion(@Param("avgMArk") double avgMark, @Param("nMarks") long nMarks);

}
