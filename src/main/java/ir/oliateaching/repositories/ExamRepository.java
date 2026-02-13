package ir.oliateaching.repositories;

import ir.oliateaching.domains.Exam;
import ir.oliateaching.repositories.base.CrudRepository;

import java.util.List;


public interface ExamRepository extends CrudRepository<Exam, Long> {

    List<Exam> findByTitleContaining(String title);
    List<Exam> findByCourseId(long courseId);
}
