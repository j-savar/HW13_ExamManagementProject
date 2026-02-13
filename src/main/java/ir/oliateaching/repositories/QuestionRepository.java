package ir.oliateaching.repositories;

import ir.oliateaching.domains.Question;
import ir.oliateaching.enums.QuestionType;
import ir.oliateaching.repositories.base.CrudRepository;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {

    List<Question> findByExamId(long examId);

    List<Question> findByQuestionType(QuestionType questionType);
}
