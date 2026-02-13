package ir.oliateaching.domains;



import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = EssayQuestion.TABLE_NAME)
@PrimaryKeyJoinColumn(name = EssayQuestion.PRIMARY_KEY_JOIN_COLUMN)
@EqualsAndHashCode(callSuper = true)
public class EssayQuestion extends Question {

    public static final String TABLE_NAME = "essay_question";
    public static final String PRIMARY_KEY_JOIN_COLUMN = "question_id";
    public static final String QUESTION_KEYWORDS = "question_keywords";
    public static final String KEYWORD =  "keyword";
    public static final String MIN_WORDS_COLUMN = "min_words";
    public static final String MAX_WORDS_COLUMN = "max_words";


    @ElementCollection
    @CollectionTable(name = QUESTION_KEYWORDS,
                    joinColumns = @JoinColumn(name = PRIMARY_KEY_JOIN_COLUMN))
    @Column(name = KEYWORD)
    private List<String> expectedKeywords = new ArrayList<>();

    @Column(name = MIN_WORDS_COLUMN)
    private Integer minWords;

    @Column(name = MAX_WORDS_COLUMN)
    private Integer maxWords;


    @Override
    public String display() {
        return String.format("[Essay - %f Score]\n%s\n(MIN %d Word)",
                getScore(), getText(), minWords);
    }

    @Override
    public boolean validateAnswer(Object answer) {
        if (!(answer instanceof String studentAnswer)) {
            return false;
        }

        int wordCount = studentAnswer.trim().split("\\s+").length;

        return wordCount >= minWords && wordCount <= maxWords;
    }


}
