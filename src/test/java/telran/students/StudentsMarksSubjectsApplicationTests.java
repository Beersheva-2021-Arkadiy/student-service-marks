package telran.students;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import telran.students.dto.*;
import telran.students.service.interfaces.StudentService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentsMarksSubjectsApplicationTests {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudentService studentService;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    @Order(1)
    void dbLoad() {
        // Fill DB
        studentService.addStudent(new Student(1, "Moshe"));
        studentService.addStudent(new Student(2, "David"));
        studentService.addStudent(new Student(3, "Aaron"));

        studentService.addSubject(new Subject(1, "React"));
        studentService.addSubject(new Subject(2, "Java"));

        studentService.addMark(new Mark(1, 1, 90));
        studentService.addMark(new Mark(1, 2, 90));
        studentService.addMark(new Mark(2, 1, 80));
        studentService.addMark(new Mark(2, 2, 80));
        studentService.addMark(new Mark(3, 1, 40));
    }


    @Test
    void bestStudents() throws Exception {
        String resJSON = mockMvc.perform(MockMvcRequestBuilders.get("/students/best"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String[] res = mapper.readValue(resJSON, String[].class);
        Assertions.assertEquals(2, res.length);
        Assertions.assertTrue(res[0].contains("Moshe"));
        Assertions.assertTrue(res[1].contains("David"));
    }

}
