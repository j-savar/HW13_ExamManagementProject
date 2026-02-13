package ir.oliateaching.domains.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;


@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class BaseDomain <ID extends Number> implements Serializable {

    public static final String ID_COLUMN = "id";
    public static final String CREATE_DATE_COLUMN = "create_date";
    public static final String LAST_UPDATE_DATE_COLUMN = "last_update_date";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID_COLUMN)
    private ID id;

    @Column(name = CREATE_DATE_COLUMN)
    private ZonedDateTime createDate;

    @Column(name = LAST_UPDATE_DATE_COLUMN)
    private ZonedDateTime lastUpdateDate;


    @PrePersist
    public void performPrePersistLogic(){
        setCreateDate(ZonedDateTime.now());
        setLastUpdateDate(ZonedDateTime.now());
    }

    @PreUpdate
    public void performPreUpdateLogic(){
        setLastUpdateDate(ZonedDateTime.now());
    }
}
