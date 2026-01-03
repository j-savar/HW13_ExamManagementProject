package ir.oliateaching.domains.base;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;



@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class BaseDomain <ID extends Number> implements Serializable {

    public static final String  ID_COLUMN = "id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN)
    private ID id;
}
