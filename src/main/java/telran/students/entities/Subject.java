package telran.students.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "subjects")
@AllArgsConstructor
public class Subject {

    @Id
    int suid;
    String subject;

}
