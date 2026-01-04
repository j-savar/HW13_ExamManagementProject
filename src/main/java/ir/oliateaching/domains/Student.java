package ir.oliateaching.domains;


import ir.oliateaching.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = Student.TABLE_NAME)
@DiscriminatorValue(Student.DISCRIMINATOR_VALUE)
@PrimaryKeyJoinColumn(name = Student.USER_ID_COLUMN)
public class Student extends User {

    public static final String TABLE_NAME = "students";
    public static final String DISCRIMINATOR_VALUE = "STUDENT";
    public static final String USER_ID_COLUMN = "user_id";

    public static final String STUDENT_CODE_COLUMN = "student_code";
    public static final String FIELD_OF_STUDY_COLUMN = "field_of_study";
    public static final String ENTRY_YEAR_COLUMN = "entry_year";


    @Column(name = STUDENT_CODE_COLUMN, unique = true, nullable = false, length = 10)
    private String studentCode;

    @Column(name = FIELD_OF_STUDY_COLUMN, nullable = false, length = 100)
    private String fieldOfStudy;

    @Column(name = ENTRY_YEAR_COLUMN, nullable = false)
    private Integer entryYear;

    @ManyToMany(mappedBy = TABLE_NAME)
    private List<Course> enrolledCourses = new ArrayList<>();

    public Student() {
        setRole(UserRole.STUDENT);
    }

    public Student(String username, String password, String firstName,
                   String lastName, String email) {
        super(username, password, firstName, lastName, email, UserRole.STUDENT);
    }

    public void enrollInCourse(Course course) {
        if (!enrolledCourses.contains(course)) {
            enrolledCourses.add(course);
        }
    }

    public void unenrollFromCourse(Course course) {
        enrolledCourses.remove(course);
    }
}
