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
@Table(name = MultipleChoiceQuestion.TABLE_NAME)
@EqualsAndHashCode(callSuper=true)
@PrimaryKeyJoinColumn(name = MultipleChoiceQuestion.PRIMARY_KEY__JOIN_COLUMN)
public class MultipleChoiceQuestion extends Question{

    public static final String TABLE_NAME = "multiple_choice_question";
    public static final String PRIMARY_KEY__JOIN_COLUMN = "question_id";
    public static final String QUESTION_OPTION = "question_options";
    public static final String OPTION_ORDER_COLUMN = "option_order";
    public static final String OPTION_TEXT_COLUMN = "option_text";
    public static final String CORRECT_ANSWER_COLUMN = "correct_answer";


    @ElementCollection
    @CollectionTable(name = QUESTION_OPTION,
                    joinColumns = @JoinColumn(name = PRIMARY_KEY__JOIN_COLUMN))
    @OrderColumn(name = OPTION_ORDER_COLUMN)
    @Column(name = OPTION_TEXT_COLUMN)
    private List<String> options = new ArrayList<>();

    @Column(name = CORRECT_ANSWER_COLUMN, nullable = false)
    private Integer correctAnswerIndex;


    @Override
    public String display() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[Multiple Choice - %f Score]\n%s\n", getScore(), getText()));

        for (int i = 0; i < options.size(); i++) {
            sb.append(String.format("   %d. %s\n", i + 1, options.get(i)));
        }

        return sb.toString();
    }

    @Override
    public boolean validateAnswer(Object answer) {
        if (!(answer instanceof Integer selectedOption)) {
            return false;
        }

        return selectedOption >= 1 && selectedOption <= options.size();
    }

    public boolean isCorrect(Integer studentAnswer) {
        return studentAnswer != null &&
                studentAnswer.equals(correctAnswerIndex + 1);
    }
}
