package ir.oliateaching.domains;


import ir.oliateaching.domains.base.BaseDomain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;




@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = Person.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = Person.PERSON_TYPE_COLUMN, discriminatorType = DiscriminatorType.STRING)
public abstract class Person extends BaseDomain<Long> {

    public static final String TABLE_NAME = "person";
    public static final String PERSON_TYPE_COLUMN = "person_type";

    public static final String FIRST_NAME_COLUMN = "first_name";
    public static final String LAST_NAME_COLUMN = "last_name";
    public static final String BIRTH_DATE_COLUMN = "birth_date";
    public static final String EMAIL_COLUMN = "email";
    public static final String PASSWORD_COLUMN = "password";


    @Column(name = FIRST_NAME_COLUMN, nullable = false, length = 50)
    private String firstName;

    @Column(name = LAST_NAME_COLUMN, nullable = false, length = 50)
    private String lastName;

    @Column(name = BIRTH_DATE_COLUMN)
    private LocalDate birthDate;

    @Column(name = EMAIL_COLUMN, nullable = false, length = 50)
    private String email;

    @Column(name = PASSWORD_COLUMN, nullable = false, length = 50)
    private String password;
}
