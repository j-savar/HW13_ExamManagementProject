package ir.oliateaching.domains;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Year;




@Getter
@Setter
@Entity
@ToString
@Table(name = Student.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue(Student.DISCRIMINATOR_VALUE)
@PrimaryKeyJoinColumn(name = Student.PERSON_ID_COLUMN)
public class Student extends Person {

    public static final String TABLE_NAME = "students";
    public static final String DISCRIMINATOR_VALUE = "STUDENT";
    public static final String PERSON_ID_COLUMN = "person_id";

    public static final String STUDENT_CODE_COLUMN = "student_code";
    public static final String MAJOR_COLUMN = "major";
    public static final String ENTRY_YEAR_COLUMN = "entry_year";
    public static final String GRADE_POINT_AVERAGE_COLUMN = "grade_point_average";
    public static final String TOTAL_CREDITS_COLUMN = "total_credits";
    public static final String IS_ACTIVE_COLUMN = "is_active";


    @Column(name = STUDENT_CODE_COLUMN, unique = true, nullable = false, length = 10)
    private String studentCode;

    @Column(name = MAJOR_COLUMN, nullable = false, length = 100)
    private String major;

    @Column(name = ENTRY_YEAR_COLUMN, nullable = false)
    private Year entryYear;

    @Column(name = GRADE_POINT_AVERAGE_COLUMN)
    private Double gradePointAverage;

    @Column(name = TOTAL_CREDITS_COLUMN)
    private Integer totalCredits;

    @Column(name = IS_ACTIVE_COLUMN)
    private Boolean isActive;

    public Student(String firstName, String lastName, LocalDate birthDate,
                   String studentCode, String major, Year entryYear,
                   Double gradePointAverage, Integer totalCredits, Boolean isActive) {
        super(firstName, lastName, birthDate);
        this.studentCode = studentCode;
        this.major = major;
        this.entryYear = entryYear;
        this.gradePointAverage = gradePointAverage;
        this.totalCredits = totalCredits;
        this.isActive = isActive;
    }

    public Student() {
    }
}
