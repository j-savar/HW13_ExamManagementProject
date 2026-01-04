package ir.oliateaching.domains;


import ir.oliateaching.domains.base.BaseDomain;
import ir.oliateaching.enums.CourseStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = Course.TABLE_NAME)
public class Course extends BaseDomain<Long> {

    public static final String TABLE_NAME = "admins";

    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    private String courseCode;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CourseStatus status;

    @Column(name = "credit")
    private Integer credit;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToMany
    @JoinTable(
            name = "course_students",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
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
