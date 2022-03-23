package telran.students.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import telran.students.entities.Student;

import java.util.List;

public interface StudentsRepository extends MongoRepository<Student, Integer> {
    Student findByName(String name);
    List<Student> findByNameIn(List<String> theWorstStudentsNames);
}
