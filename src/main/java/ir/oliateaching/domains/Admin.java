package ir.oliateaching.domains;


import ir.oliateaching.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = Admin.TABLE_NAME)
@DiscriminatorValue(Admin.DISCRIMINATOR_VALUE)
@PrimaryKeyJoinColumn(name = Admin.PRIMARY_KEY_JOIN_COLUMN)
public class Admin extends User {

    public static final String TABLE_NAME = "admins";
    public static final String PRIMARY_KEY_JOIN_COLUMN = "user_id";
    public static final String DISCRIMINATOR_VALUE = "ADMIN";

    @Column(name = "admin_level")
    private Integer adminLevel;

    @Column(name = "department", length = 100)
    private String department;

    public Admin() {
        setRole(UserRole.ADMIN);
        setStatus(ir.oliateaching.enums.UserStatus.APPROVED); // مدیر خودکار تأیید می‌شود
    }

    public Admin(String username, String password, String firstName,
                 String lastName, String email) {
        super(username, password, firstName, lastName, email, UserRole.ADMIN);
        setStatus(ir.oliateaching.enums.UserStatus.APPROVED);
    }
}
