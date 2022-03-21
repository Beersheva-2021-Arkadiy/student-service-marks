package telran.students.jpa.entities;

import lombok.Data;
import lombok.Getter;
import telran.students.dto.Subject;
import javax.persistence.*;
@Data
@Entity
@Table(name = "subjects")
public class SubjectJpa {

    @Id
    int suid;

    @Column(nullable = false, unique = true)
    String subject;

    public static SubjectJpa build(Subject subject) {
        SubjectJpa res = new SubjectJpa();
        res.suid = subject.suid;
        res.subject = subject.subject;
        return res;
    }

    public Subject toSubjectDto() {
        Subject subject = new Subject();
        subject.suid = getSuid();
        subject.subject = getSubject();
        return subject;
    }

}
