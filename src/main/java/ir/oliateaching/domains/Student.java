package ir.oliateaching.domains;


import ir.oliateaching.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = Student.TABLE_NAME)
@PrimaryKeyJoinColumn(name = Student.PRIMARY_KEY_JOIN_COLUMN)
public class Student extends User {

    public static final String TABLE_NAME = "students";
    public static final String PRIMARY_KEY_JOIN_COLUMN = "user_id";

    public static final String STUDENT_CODE_COLUMN = "student_code";
    public static final String FIELD_OF_STUDY_COLUMN = "field_of_study";
    public static final String ENTRY_YEAR_COLUMN = "entry_year";



    @Column(name = STUDENT_CODE_COLUMN, unique = true, length = 10)
    private String studentCode;

    @Column(name = FIELD_OF_STUDY_COLUMN, length = 100)
    private String fieldOfStudy;

    @Column(name = ENTRY_YEAR_COLUMN)
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
