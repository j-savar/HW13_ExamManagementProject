package ir.oliateaching.domains;


import ir.oliateaching.domains.base.BaseDomain;
import ir.oliateaching.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = Course.TABLE_NAME)
@DiscriminatorValue(Course.DISCRIMINATOR_VALUE)
//@PrimaryKeyJoinColumn(name = Course.USER_ID_COLUMN)
public class Course extends BaseDomain<Long> {

    public static final String TABLE_NAME = "courses";
    public static final String DISCRIMINATOR_VALUE = "COURSE";
    public static final String STUDENT_ID_COLUMN = "student_id";
    public static final String COURSE_CODE_COLUMN = "course_code";
    public static final String TITLE_COLUMN = "title";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String START_DATE_COLUMN = "start_date";
    public static final String END_DATE_COLUMN = "end_date";
    public static final String STATUS_COLUMN = "status";
    public static final String CREDIT_COLUMN = "credit";
    public static final String TEACHER_ID_COLUMN = "teacher_id";
    public static final String COURSE_STUDENTS_JOIN_COLUMN = "course_students";
    public static final String COURSE_ID_COLUMN = "course_id";
    public static final String CREATED_AT_COLUMN = "created_at";
    public static final String UPDATED_AT_COLUMN = "updated_at";




    @Column(name = COURSE_CODE_COLUMN, nullable = false, unique = true, length = 20)
    private String courseCode;

    @Column(name = TITLE_COLUMN, nullable = false, length = 100)
    private String title;

    @Column(name = DESCRIPTION_COLUMN, columnDefinition = "TEXT")
    private String description;

    @Column(name = START_DATE_COLUMN, nullable = false)
    private LocalDate startDate;

    @Column(name = END_DATE_COLUMN, nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = STATUS_COLUMN, nullable = false)
    private CourseStatus status;

    @Column(name = CREDIT_COLUMN)
    private Integer credit;

    @ManyToOne
    @JoinColumn(name = TEACHER_ID_COLUMN)
    private Teacher teacher;

    @ManyToMany
    @JoinTable(
            name = COURSE_STUDENTS_JOIN_COLUMN,
            joinColumns = @JoinColumn(name = COURSE_ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = STUDENT_ID_COLUMN)
    )

    private List<Student> students = new ArrayList<>();

    @Column(name = CREATED_AT_COLUMN, nullable = false)
    private LocalDate createdAt;

    @Column(name = UPDATED_AT_COLUMN)
    private LocalDate updatedAt;

    public Course() {
        this.status = CourseStatus.PLANNED;
        this.createdAt = LocalDate.now();
    }

    public Course(String courseCode, String title, LocalDate startDate, LocalDate endDate) {
        this();
        this.courseCode = courseCode;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
        this.updatedAt = LocalDate.now();
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        this.updatedAt = LocalDate.now();
    }


    // Methods for help us
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            student.enrollInCourse(this);
            this.updatedAt = LocalDate.now();
        }
    }

    public void removeStudent(Student student) {
        if (students.contains(student)) {
            students.remove(student);
            student.unenrollFromCourse(this);
            this.updatedAt = LocalDate.now();
        }
    }

    public int getStudentCount() {
        return students.size();
    }

    public boolean isActive() {
        return status == CourseStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + getId() +
                ", courseCode='" + courseCode + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", teacher=" + (teacher != null ? teacher.getFullName() : "Not assigned") +
                ", students=" + students.size() +
                '}';
    }
}
