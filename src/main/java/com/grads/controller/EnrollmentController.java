package com.grads.controller;

import com.grads.dto.ApiResponseDTO;
import com.grads.dto.EnrollmentDTO;
import com.grads.service.AuthService;
import com.grads.service.EnrollmentService;
import com.grads.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/course/{courseId}")
    public ResponseEntity<ApiResponseDTO<EnrollmentDTO>> enrollInCourse(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            EnrollmentDTO enrollment = enrollmentService.enrollUserInCourse(userId, courseId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success(enrollment, "Successfully enrolled in course"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error enrolling user in course ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to enroll in course"));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponseDTO<List<EnrollmentDTO>>> getMyEnrollments(
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            List<EnrollmentDTO> enrollments = enrollmentService.getUserEnrollments(userId);
            return ResponseEntity.ok(ApiResponseDTO.success(enrollments));
        } catch (Exception e) {
            log.error("Error fetching user enrollments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to fetch enrollments"));
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponseDTO<EnrollmentDTO>> getEnrollmentStatus(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            Optional<EnrollmentDTO> enrollment = enrollmentService.getEnrollment(userId, courseId);

            if (enrollment.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Not enrolled in this course"));
            }

            return ResponseEntity.ok(ApiResponseDTO.success(enrollment.get()));
        } catch (Exception e) {
            log.error("Error fetching enrollment status for course ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to fetch enrollment status"));
        }
    }

    @GetMapping("/course/{courseId}/check")
    public ResponseEntity<ApiResponseDTO<Boolean>> checkEnrollmentStatus(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            boolean isEnrolled = enrollmentService.isUserEnrolled(userId, courseId);
            return ResponseEntity.ok(ApiResponseDTO.success(isEnrolled));
        } catch (Exception e) {
            log.error("Error checking enrollment status for course ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to check enrollment status"));
        }
    }

    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<ApiResponseDTO<Void>> unenrollFromCourse(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            boolean unenrolled = enrollmentService.unenrollUser(userId, courseId);

            if (!unenrolled) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Enrollment not found"));
            }

            return ResponseEntity.ok(ApiResponseDTO.success(null, "Successfully unenrolled from course"));
        } catch (Exception e) {
            log.error("Error unenrolling user from course ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to unenroll from course"));
        }
    }

    @GetMapping("/course/{courseId}/students")
    public ResponseEntity<ApiResponseDTO<List<EnrollmentDTO>>> getCourseEnrollments(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            getUserIdFromRequest(request); // Validate authentication
            List<EnrollmentDTO> enrollments = enrollmentService.getCourseEnrollments(courseId);
            return ResponseEntity.ok(ApiResponseDTO.success(enrollments));
        } catch (Exception e) {
            log.error("Error fetching enrollments for course ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to fetch course enrollments"));
        }
    }

    @GetMapping("/stats/my")
    public ResponseEntity<ApiResponseDTO<Long>> getMyEnrollmentCount(
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            Long count = enrollmentService.getUserEnrollmentCount(userId);
            return ResponseEntity.ok(ApiResponseDTO.success(count));
        } catch (Exception e) {
            log.error("Error fetching user enrollment count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to fetch enrollment count"));
        }
    }

    @GetMapping("/stats/course/{courseId}")
    public ResponseEntity<ApiResponseDTO<Long>> getCourseEnrollmentCount(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            getUserIdFromRequest(request); // Validate authentication
            Long count = enrollmentService.getCourseEnrollmentCount(courseId);
            return ResponseEntity.ok(ApiResponseDTO.success(count));
        } catch (Exception e) {
            log.error("Error fetching course enrollment count for course ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to fetch course enrollment count"));
        }
    }

    @GetMapping("/access/course/{courseId}")
    public ResponseEntity<ApiResponseDTO<Boolean>> checkCourseAccess(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            boolean hasAccess = enrollmentService.canAccessCourse(userId, courseId);
            return ResponseEntity.ok(ApiResponseDTO.success(hasAccess));
        } catch (Exception e) {
            log.error("Error checking course access for course ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to check course access"));
        }
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String email= jwtUtil.extractUsername(token);

        return authService.findUser(email).getId();

    }
}
