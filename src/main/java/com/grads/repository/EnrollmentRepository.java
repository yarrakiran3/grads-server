package com.grads.repository;

import com.grads.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Find all enrollments for a specific user
    List<Enrollment> findByUserIdOrderByEnrolledAtDesc(Long userId);

    // Find enrollment by user and course
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    // Check if user is enrolled in a course
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    // Get enrollment count for a specific course
    Long countByCourseId(Long courseId);

    // Get enrollment count for a specific user
    Long countByUserId(Long userId);

    // Find enrollments with course details
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course WHERE e.userId = :userId ORDER BY e.enrolledAt DESC")
    List<Enrollment> findByUserIdWithCourse(@Param("userId") Long userId);

    // Find enrollments for a course with user details
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user WHERE e.courseId = :courseId ORDER BY e.enrolledAt DESC")
    List<Enrollment> findByCourseIdWithUser(@Param("courseId") Long courseId);
}