package telran.students.jpa.entities;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import telran.students.dto.Student;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "students")
public class StudentJpa {

    @Id
    int stid;

    @Column(nullable = false, unique = true)
    String name;

    @OneToMany(mappedBy = "student_jpa")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<MarkJpa> marks;

    public static StudentJpa build(Student student) {
        StudentJpa res = new StudentJpa();
        res.stid = student.stid;
        res.name = student.name;
        return res;
    }

    public Student toStudentDto() {
        Student student = new Student();
        student.stid = getStid();
        student.name = getName();
        return student;
    }

}
