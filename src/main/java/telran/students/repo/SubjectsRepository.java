package telran.students.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import telran.students.entities.Subject;

public interface SubjectsRepository extends MongoRepository<Subject, Integer> {

}
