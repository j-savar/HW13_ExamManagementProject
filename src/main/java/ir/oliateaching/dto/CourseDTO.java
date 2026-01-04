package ir.oliateaching.dto;

import ir.oliateaching.enums.CourseStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class CourseDTO {

    @NotBlank(message = "Course code cannot be empty")
    @Size(min = 3, max = 20, message = "Course code must be between 3 and 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Course code can only contain letters, numbers and hyphens")
    private String courseCode;

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Start date cannot be empty")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be empty")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @Min(value = 1, message = "Credit must be at least 1")
    @Max(value = 6, message = "Credit cannot exceed 6")
    private Integer credit;

    private Long teacherId;

    @NotNull(message = "Status cannot be empty")
    private CourseStatus status;


    @Override
    public String toString() {
        return "CourseDTO{" +
                "courseCode='" + courseCode + '\'' +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}
