package com.grads.service;

import com.grads.dto.EnrollmentDTO;
import com.grads.entity.Course;
import com.grads.entity.Enrollment;
import com.grads.repository.CourseRepository;
import com.grads.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentDTO enrollUserInCourse(Long userId, Long courseId) {
        // Check if course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Check if user is already enrolled
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new IllegalStateException("User is already enrolled in this course");
        }

        // For premium courses, additional validation can be added here
        // (e.g., payment verification - will be handled in payment integration)

        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("User {} enrolled in course {} successfully", userId, courseId);

        return convertToEnrollmentDTO(savedEnrollment, course);
    }

    public List<EnrollmentDTO> getUserEnrollments(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdWithCourse(userId);
        return enrollments.stream()
                .map(enrollment -> convertToEnrollmentDTO(enrollment, enrollment.getCourse()))
                .collect(Collectors.toList());
    }

    public Optional<EnrollmentDTO> getEnrollment(Long userId, Long courseId) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (enrollmentOpt.isEmpty()) {
            return Optional.empty();
        }

        Enrollment enrollment = enrollmentOpt.get();
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return Optional.empty();
        }

        return Optional.of(convertToEnrollmentDTO(enrollment, course));
    }

    public boolean isUserEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    public boolean unenrollUser(Long userId, Long courseId) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (enrollmentOpt.isEmpty()) {
            return false;
        }

        enrollmentRepository.delete(enrollmentOpt.get());
        log.info("User {} unenrolled from course {} successfully", userId, courseId);
        return true;
    }

    public List<EnrollmentDTO> getCourseEnrollments(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdWithUser(courseId);
        Course course = courseRepository.findById(courseId).orElse(null);

        return enrollments.stream()
                .map(enrollment -> convertToEnrollmentDTO(enrollment, course))
                .collect(Collectors.toList());
    }

    public Long getUserEnrollmentCount(Long userId) {
        return enrollmentRepository.countByUserId(userId);
    }

    public Long getCourseEnrollmentCount(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    public boolean canAccessCourse(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return false;
        }

        // Free courses are accessible to all users
        if (!course.getIsPremium()) {
            return true;
        }

        // Premium courses require enrollment
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    private EnrollmentDTO convertToEnrollmentDTO(Enrollment enrollment, Course course) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setUserId(enrollment.getUserId());
        dto.setCourseId(enrollment.getCourseId());
        dto.setEnrolledAt(enrollment.getEnrolledAt());

        if (course != null) {
            dto.setCourseTitle(course.getTitle());
            dto.setCourseDescription(course.getDescription());
            dto.setIsPremium(course.getIsPremium());
            dto.setCoursePrice(course.getPrice());
        }

        return dto;
    }
}