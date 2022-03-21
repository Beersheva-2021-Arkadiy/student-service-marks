package telran.students.jpa.entities;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "marks")
public class MarkJpa {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int mark;

    @ManyToOne
    StudentJpa studentJpa;

    @ManyToOne
    SubjectJpa subjectJpa;

    public MarkJpa() {}

    public MarkJpa(int mark, StudentJpa studentJpa, SubjectJpa subjectJpa) {
        this.mark = mark;
        this.studentJpa = studentJpa;
        this.subjectJpa = subjectJpa;
    }
}
