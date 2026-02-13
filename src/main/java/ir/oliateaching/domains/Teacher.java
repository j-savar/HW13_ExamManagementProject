package ir.oliateaching.domains;


import ir.oliateaching.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Setter
@Getter
@Table(name = Teacher.TABLE_NAME)
//@DiscriminatorValue(Teacher.DISCRIMINATOR_VALUE)
@PrimaryKeyJoinColumn(name = Teacher.PRIMARY_KEY_JOIN_COLUMN)
public class Teacher extends User {

    public static final String TABLE_NAME = "teachers";
    public static final String PRIMARY_KEY_JOIN_COLUMN = "user_id";
    //public static final String DISCRIMINATOR_VALUE = "TEACHER";

    public static final String TEACHER_CODE_COLUMN = "teacher_code";
    public static final String EXPERTISE_COLUMN = "expertise";
    public static final String DEGREE_COLUMN = "degree";


    @Column(name = TEACHER_CODE_COLUMN, unique = true, length = 10)
    private String teacherCode;

    @Column(name = EXPERTISE_COLUMN, length = 200)
    private String expertise;

    @Column(name = DEGREE_COLUMN, length = 100)
    private String degree;

    @OneToMany(mappedBy = "teacher")
    private List<Course> teachingCourses = new ArrayList<>();


    public Teacher() {
        setRole(UserRole.TEACHER);
    }

    public Teacher(String username, String password, String firstName,
                   String lastName, String email) {
        super(username, password, firstName, lastName, email, UserRole.TEACHER);
    }


    public void addTeachingCourse(Course course) {
        if (!teachingCourses.contains(course)) {
            teachingCourses.add(course);
            course.setTeacher(this);
        }
    }

    public void removeTeachingCourse(Course course) {
        teachingCourses.remove(course);
        course.setTeacher(null);
    }

}