package com.grads.controller;

import com.grads.dto.*;
import com.grads.entity.Course;
import com.grads.entity.User;
import com.grads.repository.UserRepository;
import com.grads.service.AuthService;
import com.grads.service.CourseService;
import com.grads.security.JwtUtil;
import com.grads.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    private final CourseService courseService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponseDTO<CourseDTO>> createCourse(
            @Valid @RequestBody CourseRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            // Only admin users should be able to create courses
            // For now, we'll allow any authenticated user

            Collection<? extends GrantedAuthority> collection =
                    getUserFromRequest(httpRequest).getAuthorities();

            boolean isAdmin = collection.stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            if (isAdmin) {
                System.out.println("Is role admin");

                CourseDTO course = courseService.createCourse(
                        request.getTitle(),
                        request.getDescription(),
                        request.getPrice(),
                        request.getIsPremium()
                );

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponseDTO.success(course, "Course created successfully"));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error("Invalid User"));


        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to create course"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO<List<CourseDTOAll>>> getAllCourses() {
        try {
            List<CourseDTOAll> courses = courseService.getAllCourses();
            return ResponseEntity.ok(ApiResponseDTO.success(courses));
        } catch (Exception e) {
            log.error("Error fetching all courses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to fetch courses"));
        }
    }

    @GetMapping("/free")
    public ResponseEntity<ApiResponseDTO<List<CourseDTOAll>>> getFreeCourses() {
        try {
            List<CourseDTOAll> courses = courseService.getFreeCourses();
            return ResponseEntity.ok(ApiResponseDTO.success(courses));
        } catch (Exception e) {
            log.error("Error fetching free courses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to fetch free courses"));
        }
    }

    @GetMapping("/premium")
    public ResponseEntity<ApiResponseDTO<List<CourseDTOAll>>> getPremiumCourses() {
        try {
            List<CourseDTOAll> courses = courseService.getPremiumCourses();
            return ResponseEntity.ok(ApiResponseDTO.success(courses));
        } catch (Exception e) {
            log.error("Error fetching premium courses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to fetch premium courses"));
        }
    }

    @GetMapping("/id/{courseId}")
    public ResponseEntity<ApiResponseDTO<CourseDetailDTO>> getCourseDetails(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            Optional<CourseDetailDTO> courseDetail = courseService.getCourseDetails(courseId);

            if (courseDetail.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Course not found"));
            }

            return ResponseEntity.ok(ApiResponseDTO.success(courseDetail.get()));
        } catch (Exception e) {
            log.error("Error fetching course details for ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to fetch course details"));
        }
    }


    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponseDTO<Course>> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            getUserFromRequest(httpRequest).getId(); // Validate authentication

            Optional<Course> updatedCourse = courseService.updateCourse(
                    courseId,
                    request.getTitle(),
                    request.getDescription(),
                    request.getPrice(),
                    request.getIsPremium()
            );

            if (updatedCourse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Course not found"));
            }

            return ResponseEntity.ok(ApiResponseDTO.success(updatedCourse.get(), "Course updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating course with ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to update course"));
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCourse(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            getUserFromRequest(request).getId(); // Validate authentication

            boolean deleted = courseService.deleteCourse(courseId);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Course not found"));
            }

            return ResponseEntity.ok(ApiResponseDTO.success(null, "Course deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting course with ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to delete course"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<List<CourseDTOAll>>> searchCourses(@RequestParam String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Search keyword is required"));
            }

            List<CourseDTOAll> courses = courseService.searchCourses(keyword.trim());
            return ResponseEntity.ok(ApiResponseDTO.success(courses));
        } catch (Exception e) {
            log.error("Error searching courses with keyword: " + keyword, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to search courses"));
        }
    }

    @GetMapping("/{courseId}/access")
    public ResponseEntity<ApiResponseDTO<Boolean>> checkCourseAccess(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            Long userId = getUserFromRequest(request).getId();
            boolean hasAccess = courseService.isCourseAccessible(courseId, userId);
            return ResponseEntity.ok(ApiResponseDTO.success(hasAccess));
        } catch (Exception e) {
            log.error("Error checking course access for course ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to check course access"));
        }
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String email= jwtUtil.extractUsername(token);

        return  authService.findUser(email);




    }
}