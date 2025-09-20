package com.grads.repository;

import com.grads.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find all free courses
    List<Course> findByIsPremiumFalse();

    // Find all premium courses
    List<Course> findByIsPremiumTrue();

    // Find courses by title containing keyword (case insensitive)
    List<Course> findByTitleContainingIgnoreCase(String title);

    // Get course with videos count
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.videos WHERE c.id = :courseId")
    Course findByIdWithVideos(@Param("courseId") Long courseId);

    // Get all courses ordered by creation date
    List<Course> findAllByOrderByCreatedAtDesc();

    // Check if course exists and is premium
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c WHERE c.id = :courseId AND c.isPremium = true")
    boolean isCourseExistsAndPremium(@Param("courseId") Long courseId);
}