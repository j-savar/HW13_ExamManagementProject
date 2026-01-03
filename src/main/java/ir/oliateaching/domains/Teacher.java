package ir.oliateaching.domains;


import ir.oliateaching.enums.AcademicDegree;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;




@Entity
@Setter
@Getter
@Table(name = Teacher.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
@ToString
@DiscriminatorValue(Teacher.DISCRIMINATOR_VALUE)
@PrimaryKeyJoinColumn(name = Teacher.PRIMARY_KEY_JOIN_COLUMN)
public class Teacher extends Person {

    public static final String TABLE_NAME = "teachers";
    public static final String PRIMARY_KEY_JOIN_COLUMN = "person_id";
    public static final String DISCRIMINATOR_VALUE = "TEACHER";

    public static final String TEACHER_CODE_COLUMN = "teacher_code";
    public static final String EDUCATION_DEGREE_COLUMN = "education_degree";
    public static final String ACADEMIC_DEGREE_COLUMN = "academic_degree";
    public static final String MONTHLY_SALARY_COLUMN = "monthly_salary";
    public static final String DEPARTMENT_COLUMN = "department";
    public static final String YEARS_OF_EXPERIENCE_COLUMN = "years_of_experience";
    public static final String IS_FULL_TIME_COLUMN = "is_full_time";


    @Column(name = TEACHER_CODE_COLUMN, unique = true, nullable = false, length = 10)
    private String teacherCode;

    @Column(name = EDUCATION_DEGREE_COLUMN, length = 100)
    private String educationDegree;

    @Enumerated(EnumType.STRING)
    @Column(name = ACADEMIC_DEGREE_COLUMN, nullable = false)
    private AcademicDegree academicDegree;

    @Column(name = MONTHLY_SALARY_COLUMN, precision = 12, scale = 2)
    private BigDecimal monthlySalary;

    @Column(name = DEPARTMENT_COLUMN, length = 100)
    private String department;

    @Column(name = YEARS_OF_EXPERIENCE_COLUMN)
    private Integer yearsOfExperience;

    @Column(name = IS_FULL_TIME_COLUMN)
    private Boolean isFullTime;



    public Teacher(String firstName, String lastName, LocalDate birthDate,
                   String teacherCode, String educationDegree,
                   AcademicDegree academicDegree, BigDecimal monthlySalary,
                   String department, Integer yearsOfExperience,  Boolean isFullTime) {
        super(firstName, lastName, birthDate);
        this.teacherCode = teacherCode;
        this.educationDegree = educationDegree;
        this.academicDegree = academicDegree;
        this.monthlySalary = monthlySalary;
        this.department = department;
        this.yearsOfExperience = yearsOfExperience;
        this.isFullTime = isFullTime;
    }

    public Teacher() {
    }

    public BigDecimal getAnnualSalary() {
        if (monthlySalary == null) {
            return BigDecimal.ZERO;
        }
        return monthlySalary.multiply(new BigDecimal(12));
    }

    public String getAcademicTitle() {
        return switch (academicDegree) {
            case BACHELOR -> "Bachelor";
            case MASTER -> "Master";
            case PHD -> "PhD";
            case PROFESSOR -> "Professor";
            default -> "Unknown";
        };
    }
}