package ir.oliateaching.repositories;

import ir.oliateaching.domains.*;
import ir.oliateaching.enums.CourseStatus;
import ir.oliateaching.repositories.base.AbstractCrudRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CourseRepositoryImpl extends AbstractCrudRepository<Course, Long>
        implements CourseRepository{
    public CourseRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Optional<Course> findByCourseCode(String courseCode) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> query = cb.createQuery(getEntityClass());
        Root<Course> tRoot = query.from(getEntityClass());
        query.where(cb.equal(tRoot.get(Course_.courseCode), courseCode));
        return Optional.ofNullable(entityManager.createQuery(query).getSingleResultOrNull());
    }

    @Override
    public List<Course> findByTitle(String title) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> query = cb.createQuery(getEntityClass());
        Root<Course> tRoot = query.from(getEntityClass());
        query.where(cb.equal(tRoot.get(Course_.title), title));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Course> findByStatus(CourseStatus status) {
        return List.of();
    }

    @Override
    public List<Course> findByTeacher(Long teacherId) {
        return List.of();
    }

    @Override
    public List<Course> findByStudent(Long studentId) {
        return List.of();
    }

    @Override
    public List<Course> findActiveCourses() {
        return List.of();
    }

    @Override
    public List<Course> findUpcomingCourses() {
        return List.of();
    }

    @Override
    public List<Course> searchCourses(String title, CourseStatus status, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public void addStudentToCourse(Long courseId, Long studentId) {

    }

    @Override
    public void removeStudentFromCourse(Long courseId, Long studentId) {

    }

    @Override
    public List<Student> getCourseStudents(Long courseId) {
        return List.of();
    }

    @Override
    public boolean isStudentEnrolled(Long courseId, Long studentId) {
        return false;
    }

    @Override
    public void assignTeacherToCourse(Long courseId, Long teacherId) {

    }

    @Override
    public void removeTeacherFromCourse(Long courseId) {

    }

    @Override
    public Optional<Teacher> getCourseTeacher(Long courseId) {
        return Optional.empty();
    }

    @Override
    protected Class<Course> getEntityClass() {
        return null;
    }
}
