package ir.oliateaching.repositories;

import ir.oliateaching.domains.*;
import ir.oliateaching.enums.CourseStatus;
import ir.oliateaching.repositories.base.AbstractCrudRepository;
import ir.oliateaching.utils.ApplicationContext;
import ir.oliateaching.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> query = cb.createQuery(getEntityClass());
        Root<Course> tRoot = query.from(getEntityClass());
        query.where(cb.equal(tRoot.get(Course_.status), status));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Course> findByTeacher(Long teacherId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> query = cb.createQuery(getEntityClass());
        Root<Course> tRoot = query.from(getEntityClass());
        query.where(cb.equal(tRoot.get(Course_.teacher).get(Teacher.ID_COLUMN), teacherId));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Course> findByStudent(Long studentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> query = cb.createQuery(getEntityClass());
        Root<Course> tRoot = query.from(getEntityClass());
        Join<Course, Student> studentJoin = tRoot.join(Course_.students);
        query.where(cb.equal(studentJoin.get(Student_.id), studentId));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Course> findActiveCourses() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> query = cb.createQuery(Course.class);
        Root<Course> courseRoot = query.from(Course.class);
        LocalDate today = LocalDate.now();
        Predicate statusActive = cb.equal(courseRoot.get(Course_.status), "ACTIVE");
        Predicate startDateCondition = cb.lessThanOrEqualTo(courseRoot.get(Course_.startDate), today);
        Predicate endDateCondition = cb.greaterThanOrEqualTo(courseRoot.get(Course_.endDate), today);
        Predicate finalCondition = cb.and(statusActive, startDateCondition, endDateCondition);
        query.where(finalCondition);
        query.orderBy(cb.asc(courseRoot.get(Course_.startDate)));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Course> findUpcomingCourses() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> query = cb.createQuery(Course.class);
        Root<Course> courseRoot = query.from(Course.class);
        LocalDate today = LocalDate.now();
        Predicate statusPlanned = cb.equal(courseRoot.get(Course_.status), "PLANNED");
        Predicate startDateFuture = cb.greaterThan(courseRoot.get(Course_.startDate), today);
        query.where(cb.and(statusPlanned, startDateFuture));
        query.orderBy(cb.asc(courseRoot.get(Course_.startDate)));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Course> searchCourses(String title, CourseStatus status, LocalDate startDate, LocalDate endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> query = cb.createQuery(Course.class);
        Root<Course> courseRoot = query.from(Course.class);
        List<Predicate> predicates = new ArrayList<>();
        if (title != null && !title.trim().isEmpty()) {
            String searchPattern = "%" + title.trim().toLowerCase() + "%";
            Predicate titlePredicate = cb.like(
                    cb.lower(courseRoot.get(Course_.title)),
                    searchPattern
            );
            predicates.add(titlePredicate);
        }
        if (status != null) {
            Predicate statusPredicate = cb.equal(courseRoot.get(Course_.status), status);
            predicates.add(statusPredicate);
        }
        if (startDate != null) {
            Predicate startDatePredicate = cb.greaterThanOrEqualTo(
                    courseRoot.get(Course_.startDate),
                    startDate
            );
            predicates.add(startDatePredicate);
        }
        if (endDate != null) {
            Predicate endDatePredicate = cb.lessThanOrEqualTo(
                    courseRoot.get(Course_.endDate),
                    endDate
            );
            predicates.add(endDatePredicate);
        }
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        query.orderBy(cb.desc(courseRoot.get(Course_.startDate)));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public void addStudentToCourse(Long courseId, Long studentId) {
        //EntityManager entityManager = ApplicationContext.getEntityManager();
        JpaUtil.executeInTransaction(entityManager, () -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> checkQuery = cb.createQuery(Long.class);
            Root<Course> courseRoot = checkQuery.from(Course.class);
            Join<Course, Student> studentJoin = courseRoot.join(Course_.students);
            checkQuery.select(cb.count(courseRoot))
                    .where(cb.and(
                            cb.equal(courseRoot.get(Course_.id), courseId),
                            cb.equal(studentJoin.get(Student_.id), studentId)
                    ));
            Long existingRelationCount = entityManager.createQuery(checkQuery).getSingleResult();

            if (existingRelationCount > 0) {
                System.out.println("Student " + studentId + " is already enrolled in course " + courseId);
                return null;
            }
            Course course = entityManager.find(Course.class, courseId);
            Student student = entityManager.find(Student.class, studentId);
            if (course == null) {
                throw new EntityNotFoundException("Course not found with id: " + courseId);
            }
            if (student == null) {
                throw new EntityNotFoundException("Student not found with id: " + studentId);
            }
            course.addStudent(student);
            entityManager.merge(course);
            return null;
        });
    }

    @Override
    public void removeStudentFromCourse(Long courseId, Long studentId) {
        //EntityManager entityManager = ApplicationContext.getEntityManager();
        JpaUtil.executeInTransaction(entityManager, () -> {
            Course course = entityManager.find(Course.class, courseId);
            Student student = entityManager.find(Student.class, studentId);
            if (course == null) {
                throw new EntityNotFoundException("Course not found with id: " + courseId);
            }
            if (student == null) {
                throw new EntityNotFoundException("Student not found with id: " + studentId);
            }
            if (course.getStudents().contains(student)) {
                course.removeStudent(student);
                entityManager.merge(course);
            }
            return null;
        });
    }

    @Override
    public List<Student> getCourseStudents(Long courseId) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Student> query = cb.createQuery(Student.class);
            Root<Course> courseRoot = query.from(Course.class);
            Join<Course, Student> studentJoin = courseRoot.join(Course_.students);
            query.where(cb.equal(courseRoot.get(Course_.id), courseId));
            query.orderBy(
                    cb.asc(studentJoin.get(Student_.lastName)),
                    cb.asc(studentJoin.get(Student_.firstName))
            );
            query.select(studentJoin);
            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            System.err.println("Error getting students for course " + courseId + ": " + e.getMessage());
            throw new RuntimeException("Failed to get course students", e);
        }
    }

    @Override
    public boolean isStudentEnrolled(Long courseId, Long studentId) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Root<Course> courseRoot = query.from(Course.class);
            Join<Course, Student> studentJoin = courseRoot.join(Course_.students);
            query.where(cb.and(
                    cb.equal(courseRoot.get(Course_.id), courseId),
                    cb.equal(studentJoin.get(Student_.id), studentId)
            ));
            query.select(cb.count(courseRoot));
            Long count = entityManager.createQuery(query).getSingleResult();
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error checking enrollment for student " + studentId +
                    " in course " + courseId + ": " + e.getMessage());
            throw new RuntimeException("Failed to check student enrollment", e);
        }
    }

    @Override
    public void assignTeacherToCourse(Long courseId, Long teacherId) {
        JpaUtil.executeInTransaction(entityManager, () -> {
            Course course = entityManager.find(Course.class, courseId);
            Teacher teacher = entityManager.find(Teacher.class, teacherId);
            if (course == null) {
                throw new EntityNotFoundException("Course not found with id: " + courseId);
            }
            if (teacher == null) {
                throw new EntityNotFoundException("Teacher not found with id: " + teacherId);
            }
            course.setTeacher(teacher);
            entityManager.merge(course);
            return null;
        });
    }

    @Override
    public void removeTeacherFromCourse(Long courseId) {
        JpaUtil.executeInTransaction(entityManager, () -> {
            Course course = entityManager.find(Course.class, courseId);
            if (course == null) {
                throw new EntityNotFoundException("Course not found with id: " + courseId);
            }
            if (course.getTeacher() == null) {
                System.out.println("Course " + courseId + " does not have a teacher assigned");
                return null;
            }
            Teacher previousTeacher = course.getTeacher();
            course.setTeacher(null);
            entityManager.merge(course);
            System.out.println("Teacher " + previousTeacher.getId() + " removed from course " + courseId);
            return null;
        });
    }

    @Override
    public Optional<Teacher> getCourseTeacher(Long courseId) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Teacher> query = cb.createQuery(Teacher.class);
            Root<Course> courseRoot = query.from(Course.class);
            Join<Course, Teacher> teacherJoin = courseRoot.join(Course_.teacher, JoinType.LEFT);
            query.where(cb.equal(courseRoot.get(Course_.id), courseId));
            query.select(teacherJoin);
            List<Teacher> results = entityManager.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            System.err.println("Error getting teacher for course " + courseId + ": " + e.getMessage());
            throw new RuntimeException("Failed to get course teacher", e);
        }
    }

    @Override
    protected Class<Course> getEntityClass() {
        return  Course.class;
    }
}
