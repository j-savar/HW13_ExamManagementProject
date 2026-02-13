package ir.oliateaching.domains;


import ir.oliateaching.domains.base.BaseDomain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



@Getter
@Setter
@Entity
@Table(name = Exam.TABLE_NAME)
public class Exam extends BaseDomain<Long> {

    public static final String TABLE_NAME = "exams";
    public static final String EXAM_CODE_COLUMN = "exam_code";
    public static final String COURSE_ID_COLUMN = "course_id";
    public static final String TITLE_COLUMN = "exam_title";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String START_DATE_COLUMN = "start_date";
    public static final String END_DATE_COLUMN = "end_date";
    public static final String MAPPED_BY_EXAM_TABLE_COLUMN = "exam";
    public static final String CREATED_AT_COLUMN = "created_at";
    public static final String UPDATED_AT_COLUMN = "updated_at";


    @Column(name = EXAM_CODE_COLUMN, nullable = false, unique = true, length = 20)
    private String examCode;

    @Column(name = TITLE_COLUMN, nullable = false, length = 100)
    private String examTitle;

    @Column(name = DESCRIPTION_COLUMN, columnDefinition = "TEXT")
    private String description;

    @Column(name = START_DATE_COLUMN, nullable = false)
    private LocalDate startDate;

    @Column(name = END_DATE_COLUMN, nullable = false)
    private LocalDate endDate;


    @ManyToOne
    @JoinColumn(name = COURSE_ID_COLUMN)
    private Course course;

    @OneToMany(mappedBy = MAPPED_BY_EXAM_TABLE_COLUMN, cascade =  CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions =  new ArrayList<>();


    @Column(name = CREATED_AT_COLUMN, nullable = false)
    private LocalDate createdAt;

    @Column(name = UPDATED_AT_COLUMN)
    private LocalDate updatedAt;

    public Exam() {
        this.createdAt = LocalDate.now();
    }

    public Exam(String examCode, String examTitle,
                LocalDate startDate, LocalDate endDate) {
        this();
        this.examCode = examCode;
        this.examTitle = examTitle;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setCourse(Course course) {
        this.course = course;
        this.updatedAt = LocalDate.now();
    }

    public void addQuestion(Question question) {
        if (!questions.contains(question)) {
            questions.add(question);
            question.setExam(this);
            this.updatedAt = LocalDate.now();
        }
    }

    public void removeQuestion(Question question) {
        if(questions.contains(question)) {
            questions.remove(question);
            question.setExam(null);
            this.updatedAt = LocalDate.now();
        }
    }

    public double getTotalScores(){
        return questions.stream()
                .mapToDouble(Question::getScore)
                .sum();
    }

    public int getQuestionCount() {
        return questions.size();
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id='" + getId() + '\'' +
                "examCode='" + examCode + '\'' +
                ", examTitle='" + examTitle + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", questions count='" + getQuestionCount() + '\'' +
                ", course={" + course + "}" +
                '}';
    }

}
