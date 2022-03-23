package telran.students.service.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telran.students.dto.*;
import telran.students.entities.*;
import telran.students.repo.*;
import telran.students.service.interfaces.*;
import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {

    private static final int MAX_MARK = 100;
    private static final int MIN_MARK = 60;
    private SubjectsRepository subjectsRepository;
    private StudentsRepository studentsRepository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public StudentServiceImpl(SubjectsRepository subjectsRepository, StudentsRepository studentsRepository, MongoTemplate mongoTemplate) {
        this.subjectsRepository = subjectsRepository;
        this.studentsRepository = studentsRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void addStudent(StudentDto studentDto) {
        studentsRepository.save(new Student(studentDto.stid, studentDto.name));
    }

    @Override
    public void addSubject(SubjectDto subjectDto) {
        subjectsRepository.save(new Subject(subjectDto.suid, subjectDto.subject));
    }

    @Override
    public MarkDto addMark(MarkDto markDto) {
        Student student = studentsRepository.findById(markDto.stid).orElse(null);
        Subject subject = subjectsRepository.findById(markDto.suid).orElse(null);
        if (subject == null || student == null) return null;
        student.getMarks().add(new SubjectMark(subject.getSubject(), markDto.value));
        studentsRepository.save(student);
        return markDto;
    }

    private double getAvgCollegeMark() {
        UnwindOperation unwindOperation = Aggregation.unwind("marks");
        GroupOperation groupOperation = Aggregation.group().avg("marks.mark").as("avgMark");
        Aggregation pipeline = Aggregation.newAggregation(unwindOperation, groupOperation);
        return mongoTemplate
                .aggregate(pipeline, Student.class, Document.class)
                .getUniqueMappedResult()
                .getDouble("avgMark");
    }

    @Override
    public List<String> getTopBestStudents(int nStudents) {
        List<AggregationOperation> operationList = getStudentAvgMark(Sort.Direction.DESC);
        LimitOperation limit = Aggregation.limit(nStudents);
        operationList.add(limit);
        return resultProcessing(operationList, true);
    }

    private List<AggregationOperation> getStudentAvgMark(Sort.Direction sortDirection) {
        UnwindOperation unwindOperation = Aggregation.unwind("marks");
        GroupOperation groupOperation = Aggregation.group("name")
                .avg("marks.mark").as("avgMark");
        SortOperation sortOperation = Aggregation.sort(sortDirection, "avgMark");
        return new ArrayList<>(Arrays.asList(unwindOperation, groupOperation, sortOperation));
    }

    @Override
    public List<String> getBestStudents() {
        List<AggregationOperation> operationList = getStudentAvgMark(Sort.Direction.DESC);
        double avgCollegeMark = getAvgCollegeMark();
        MatchOperation matchOperation = Aggregation.match(Criteria.where("avgMark").gt(avgCollegeMark));
        operationList.add(matchOperation);
        return resultProcessing(operationList, true);
    }

    @Override
    public List<StudentDto> getTopBestStudentsBySubject(int nStudents, String subject) {
        UnwindOperation unwindOperation = Aggregation.unwind("marks");
        MatchOperation matchOperation = Aggregation.match(Criteria.where("marks.subject").is(subject));
        GroupOperation groupOperation = Aggregation.group("stid", "name").avg("marks.mark").as("avgMark");
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "avgMark");
        LimitOperation limitOperation = Aggregation.limit(nStudents);
        List<Document> documents = mongoTemplate
                .aggregate(Aggregation.newAggregation(unwindOperation, matchOperation, groupOperation, sortOperation, limitOperation), Student.class, Document.class)
                .getMappedResults();
        return documents.stream()
                .map(this::getStudent)
                .toList();
    }

    private StudentDto getStudent(Document document) {
        Document data = (Document) document.get("_id");
        return new StudentDto(data.getInteger("stid"), data.getString("name"));
    }

    private List<String> resultProcessing(List<AggregationOperation> operationList, boolean withMark) {
        try {
            List<Document> documentRes = mongoTemplate
                    .aggregate(Aggregation.newAggregation(operationList), Student.class, Document.class)
                    .getMappedResults();
            return documentRes.stream()
                    .map(doc -> doc.getString("_id") + ( withMark ? " : " + doc.getDouble("avgMark").intValue() : ""))
                    .toList();
        } catch (Exception e) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(e.getMessage());
            return errorMessage;
        }
    }

    @Override
    public List<StudentSubjectMark> getMarksOfWorstStudents(int nStudents) {
        List<AggregationOperation> operationList = getStudentAvgMark(Sort.Direction.ASC);
        operationList.add(Aggregation.limit(nStudents));

        List<String> theWorstStudentsNames = resultProcessing(operationList, false);
        List<Student> theWorstStudents = studentsRepository.findByNameIn(theWorstStudentsNames);

        if (theWorstStudents == null) return Collections.emptyList();
        return theWorstStudents.stream()
                .flatMap(item -> item.getMarks()
                        .stream()
                        .map(mark -> getStudentSubjectMark(mark, item.getName())))
                .toList();
    }

    @Override
    public List<StudentSubjectMark> getMarksStudentBySubject(String name, String subject) {
        Student student = studentsRepository.findByName(name);
        if (student == null) return Collections.emptyList();
        return student.getMarks()
                .stream()
                .filter(mark -> mark.getSubject().equals(subject))
                .map(mark -> getStudentSubjectMark(mark, name))
                .toList();
    }

    private StudentSubjectMark getStudentSubjectMark(SubjectMark mark, String name) {
        return new StudentSubjectMark() {
            @Override
            public String getStudentName() {
                return name;
            }

            @Override
            public String getSubjectSubject() {
                return mark.getSubject();
            }

            @Override
            public int getMark() {
                return mark.getMark();
            }
        };
    }

    @Override
    public List<IntervalMarks> marksDistribution(int interval) {
        int nInterval = (MAX_MARK - MIN_MARK) / interval;
        UnwindOperation unwindOperation = Aggregation.unwind("marks");
        BucketAutoOperation bucketAutoOperation = Aggregation.bucketAuto("marks.mark", interval);
        List<Document> bucketDocs = mongoTemplate
                .aggregate(Aggregation.newAggregation(unwindOperation, bucketAutoOperation), Student.class, Document.class)
                .getMappedResults();
        return bucketDocs.stream()
                .map(this::getIntervalMarks)
                .toList();
    }

    private IntervalMarks getIntervalMarks(Document doc) {
        Document data = (Document) doc.get("_id");

        return new IntervalMarks() {
            @Override
            public int getMin() {
                return data.getInteger("min");
            }

            @Override
            public int getMax() {
                return data.getInteger("max");
            }

            @Override
            public int getOccurrences() {
                return doc.getInteger("count");
            }
        };
    }

    @Override
    public List<String> jpqlQuery(String jpql) {
        List<String> res = new ArrayList<>();
        res.add("JPQL isn't support");
        return res;
    }

    @Override
    public List<String> nativeQuery(String sql) {
        try {
            BasicQuery query = new BasicQuery(sql);
            List<Student> res = mongoTemplate.find(query, Student.class);
            return res.stream().map(Student::toString).toList();
        } catch (Exception e) {
            List<String> errorMessage = new ArrayList<>();
            errorMessage.add(e.getMessage());
            return errorMessage;
        }
    }

    @Override
    @Transactional
    public List<StudentDto> removeStudents(int avgMark, int nMarks) {
        UnwindOperation unwindOperation = Aggregation.unwind("marks");
        GroupOperation groupOperation = Aggregation.group("stid", "name")
                .avg("marks.mark").as("avgMark")
                .count().as("count");
        MatchOperation matchOperation = Aggregation.match(Criteria.where("avgMark")
                .lt( (double) avgMark)
                .and("count").lt(nMarks));
        List<Document> documents = mongoTemplate
                .aggregate(Aggregation.newAggregation(unwindOperation, groupOperation, matchOperation), Student.class, Document.class)
                .getMappedResults();

        List<StudentDto> studentsForRemoving = documents.stream()
                .map(this::getStudent)
                .toList();

        studentsRepository.deleteAllById(studentsForRemoving.stream().map(s -> s.stid).toList());

        return studentsForRemoving;
    }
}
