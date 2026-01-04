package ir.oliateaching.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CourseEnrollmentDTO {

    @NotNull(message = "Course ID cannot be empty")
    private Long courseId;

    @NotNull(message = "Student ID cannot be empty")
    private Long studentId;


    @Override
    public String toString() {
        return "CourseEnrollmentDTO{" +
                "courseId=" + courseId +
                ", studentId=" + studentId +
                '}';
    }
}
