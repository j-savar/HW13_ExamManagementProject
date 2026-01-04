package ir.oliateaching.dto;


import ir.oliateaching.enums.UserRole;
import ir.oliateaching.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class UserUpdateDTO {

    private Long id;

    @NotBlank(message = "First name cannot be empty")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "First name can only contain letters")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Last name can only contain letters")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Pattern(regexp = "^09\\d{9}$", message = "Phone number must start with 09 and be 11 digits")
    private String phoneNumber;

    @NotNull(message = "Role cannot be empty")
    private UserRole role;

    @NotNull(message = "Status cannot be empty")
    private UserStatus status;

    // Additional fields for specific roles
    private String studentCode;
    private String teacherCode;
    private String fieldOfStudy;
    private Integer entryYear;
    private String expertise;
    private String degree;


    @Override
    public String toString() {
        return "UserUpdateDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", status=" + status +
                '}';
    }
}
