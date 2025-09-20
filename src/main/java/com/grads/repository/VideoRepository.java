package com.grads.repository;

import com.grads.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    // Find all videos for a specific course ordered by order_index
    List<Video> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    // Find video by id and course id
    Optional<Video> findByIdAndCourseId(Long id, Long courseId);

    // Get video count for a specific course
    Long countByCourseId(Long courseId);

    // Find next video in sequence
    @Query("SELECT v FROM Video v WHERE v.courseId = :courseId AND v.orderIndex > :currentIndex ORDER BY v.orderIndex ASC LIMIT 1")
    Optional<Video> findNextVideo(@Param("courseId") Long courseId, @Param("currentIndex") Integer currentIndex);

    // Find previous video in sequence
    @Query("SELECT v FROM Video v WHERE v.courseId = :courseId AND v.orderIndex < :currentIndex ORDER BY v.orderIndex DESC LIMIT 1")
    Optional<Video> findPreviousVideo(@Param("courseId") Long courseId, @Param("currentIndex") Integer currentIndex);

    // Get highest order index for a course
    @Query("SELECT MAX(v.orderIndex) FROM Video v WHERE v.courseId = :courseId")
    Optional<Integer> findMaxOrderIndexByCourseId(@Param("courseId") Long courseId);
}