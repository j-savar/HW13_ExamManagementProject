package ir.oliateaching.dto;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;



@Getter
@Setter
public class TeacherSignUpDTO extends PersonDTO{

    @NotBlank(message = "Teacher code cannot be empty")
    @Pattern(regexp = "^T\\d{5}$", message = "Teacher code must start with T and be 6 characters")
    private String teacherCode;

    @Size(max = 100, message = "Education degree cannot exceed 100 characters")
    private String educationDegree;

    @NotNull(message = "Academic degree cannot be empty")
    private AcademicDegree academicDegree;

    @DecimalMin(value = "0.0", message = "Monthly salary cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid salary format")
    private BigDecimal monthlySalary;

    @Size(max = 100, message = "Department name cannot exceed 100 characters")
    private String department;

    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 50, message = "Experience years cannot exceed 50")
    private Integer yearsOfExperience;

    @NotNull(message = "Employment status cannot be empty")
    private Boolean isFullTime;
}
