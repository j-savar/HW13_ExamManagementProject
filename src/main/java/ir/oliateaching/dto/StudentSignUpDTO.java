package ir.oliateaching.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;




@Getter
@Setter
public class StudentSignUpDTO extends PersonDTO {

    @NotBlank(message = "Student code cannot be empty")
    @Pattern(regexp = "^\\d{10}$", message = "Student code must be 10 digits")
    private String studentCode;

    @NotBlank(message = "Major cannot be empty")
    @Size(min = 2, max = 100, message = "Major must be between 2 and 100 characters")
    private String major;

    @NotNull(message = "Entry year cannot be empty")
    @Min(value = 1300, message = "Entry year must be at least 1300")
    @Max(value = 2026, message = "Entry year cannot exceed 1500")
    private Integer entryYear;

    @DecimalMin(value = "0.0", message = "GPA cannot be negative")
    @DecimalMax(value = "20.0", message = "GPA cannot exceed 20")
    private Double gradePointAverage;

    @Min(value = 0, message = "Total credits cannot be negative")
    private Integer totalCredits;
}
