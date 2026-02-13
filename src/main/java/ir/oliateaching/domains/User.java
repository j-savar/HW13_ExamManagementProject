package ir.oliateaching.domains;


import ir.oliateaching.domains.base.BaseDomain;
import ir.oliateaching.enums.UserRole;
import ir.oliateaching.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.internal.NaturalIdHelper;

import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@Entity
@Table(name = User.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends BaseDomain<Long> {

    public static final String TABLE_NAME = "users";

    public static final String USERNAME_COLUMN = "username";
    public static final String PASSWORD_COLUMN = "password";
    public static final String FIRST_NAME_COLUMN = "first_name";
    public static final String LAST_NAME_COLUMN = "last_name";
    public static final String EMAIL_COLUMN = "email";
    public static final String PHONE_NUMBER_COLUMN = "phone_number";
    public static final String NATIONAL_ID_COLUMN = "national_id";
    public static final String ROLE_COLUMN = "role";
    public static final String STATUS_COLUMN = "status";
    public static final String REGISTRATION_DATE_COLUMN = "registration_date";
    public static final String APPROVED_DATE_COLUMN = "approved_date";
    public static final String APPROVED_BY_COLUMN = "approved_by";



    @Column(name = USERNAME_COLUMN, nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = PASSWORD_COLUMN, nullable = false, length = 100)
    private String password;

    @Column(name = FIRST_NAME_COLUMN, nullable = false, length = 50)
    private String firstName;

    @Column(name = LAST_NAME_COLUMN, nullable = false, length = 50)
    private String lastName;

    @Column(name = EMAIL_COLUMN, nullable = false, length = 50)
    private String email;

    @Column(name = PHONE_NUMBER_COLUMN, length = 11)
    private String phoneNumber;

    @Column(name = NATIONAL_ID_COLUMN, unique = true, length = 10)
    private String nationalId;

    @Enumerated(EnumType.STRING)
    @Column(name = ROLE_COLUMN, nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = STATUS_COLUMN, nullable = false)
    private UserStatus status;

    @Column(name = REGISTRATION_DATE_COLUMN, nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = APPROVED_DATE_COLUMN)
    private LocalDateTime approvedDate;

    @Column(name = APPROVED_BY_COLUMN)
    private Long approvedBy;

    protected User() {}

    public User(String username, String password, String firstName, String lastName,
                String email, UserRole role) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.status = UserStatus.PENDING;
        this.registrationDate = LocalDateTime.now();
    }

    // Methods for help us
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isApproved() {
        return status == UserStatus.APPROVED;
    }

    public boolean isPending() {
        return status == UserStatus.PENDING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", role=" + role +
                ", status=" + status +
                '}';
    }

}
