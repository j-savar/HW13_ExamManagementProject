package ir.oliateaching.domains;

import ir.oliateaching.domains.base.BaseDomain;
import ir.oliateaching.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
@Table(name = Question.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Question extends BaseDomain<Long> {

    public static final String TABLE_NAME = "questions";
    public static final String EXAM_ID_COLUMN = "exam_id";
    public static final String QUESTION_TYPE_COLUMN = "question_type";
    public static final String QUESTION_TEXT_COLUMN = "question_text";
    public static final String QUESTION_SCORE_COLUMN = "question_score";



    @Column(name = QUESTION_TEXT_COLUMN, nullable = false, length = 2000)
    private String text;

    @Column(name = QUESTION_SCORE_COLUMN, nullable = false)
    public double score;

    @Enumerated(EnumType.STRING)
    @Column(name = QUESTION_TYPE_COLUMN, nullable = false)
    private QuestionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = EXAM_ID_COLUMN)
    private Exam exam;

    public abstract String display();

    public abstract boolean validateAnswer(Object answer);
}