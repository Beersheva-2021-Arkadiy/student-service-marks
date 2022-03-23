package telran.students.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "students")
public class Student {

    @Id
    int stid;
    List<SubjectMark> marks;
    String name;

    public Student(int stid, String name) {
        this.stid = stid;
        this.name = name;
        this.marks = new ArrayList<>();
    }
}
