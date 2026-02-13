package ir.oliateaching.domains;


import ir.oliateaching.enums.UserRole;
import ir.oliateaching.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = Admin.TABLE_NAME)
@PrimaryKeyJoinColumn(name = Admin.PRIMARY_KEY_JOIN_COLUMN)
public class Admin extends User {

    public static final String TABLE_NAME = "admins";
    public static final String PRIMARY_KEY_JOIN_COLUMN = "user_id";
    public static final String ADMIN_LEVEL_COLUMN = "admin_level";
    public static final String DEPARTMENT_COLUMN = "department";


    @Column(name = ADMIN_LEVEL_COLUMN)
    private Integer adminLevel;

    @Column(name = DEPARTMENT_COLUMN, length = 100)
    private String department;

    public Admin() {
        setRole(UserRole.ADMIN);
        setStatus(UserStatus.APPROVED);
    }

    public Admin(String username, String password, String firstName,
                 String lastName, String email) {
        super(username, password, firstName, lastName, email, UserRole.ADMIN);
        setStatus(UserStatus.APPROVED);
    }
}
