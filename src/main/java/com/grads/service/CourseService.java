package com.grads.service;

import com.grads.dto.CourseDTO;
import com.grads.dto.CourseDTOAll;
import com.grads.dto.CourseDetailDTO;
import com.grads.dto.VideoDTO;
import com.grads.entity.Course;
import com.grads.entity.Video;
import com.grads.repository.CourseRepository;
import com.grads.repository.EnrollmentRepository;
import com.grads.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final VideoRepository videoRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseDTO createCourse(String title, String description, BigDecimal price, Boolean isPremium) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setPrice(price);
        course.setIsPremium(isPremium != null ? isPremium : false);

        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully: {}", savedCourse.getId());
        return new CourseDTO(savedCourse.getId(),savedCourse.getTitle()
        , savedCourse.getDescription(), savedCourse.getPrice(),savedCourse.getIsPremium());
    }
    public List<CourseDTOAll> getAllCourses() {
        List<Course> courses = courseRepository.findAllByOrderByCreatedAtDesc();
        return courses.stream()
                .map(this::convertToCourseDTOAll)
                .collect(Collectors.toList());
    }

    public List<CourseDTOAll> getFreeCourses() {
        List<Course> courses = courseRepository.findByIsPremiumFalse();
        return courses.stream()
                .map(this::convertToCourseDTOAll)
                .collect(Collectors.toList());
    }

    public List<CourseDTOAll> getPremiumCourses() {
        List<Course> courses = courseRepository.findByIsPremiumTrue();
        return courses.stream()
                .map(this::convertToCourseDTOAll)
                .collect(Collectors.toList());
    }

    public Optional<CourseDetailDTO> getCourseDetails(Long courseId) {
        Course course = courseRepository.findByIdWithVideos(courseId);
        if (course == null) {
            return Optional.empty();
        }

//        boolean isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
        List<Video> videos = videoRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        Long enrollmentCount = enrollmentRepository.countByCourseId(courseId);

        List<VideoDTO> videoDTOList=videos.stream().map(this::convertToVideoDTO).toList();

        CourseDetailDTO courseDetail = new CourseDetailDTO();
        courseDetail.setId(course.getId());
        courseDetail.setTitle(course.getTitle());
        courseDetail.setDescription(course.getDescription());
        courseDetail.setPrice(course.getPrice());
        courseDetail.setIsPremium(course.getIsPremium());
        courseDetail.setCreatedAt(course.getCreatedAt());
//        courseDetail.setEnrolled(isEnrolled);
        courseDetail.setTotalVideos(videos.size());
        courseDetail.setEnrollmentCount(enrollmentCount);
        courseDetail.setVideos(videoDTOList);

        return Optional.of(courseDetail);
    }



    public Optional<Course> updateCourse(Long courseId, String title, String description, BigDecimal price, Boolean isPremium) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return Optional.empty();
        }

        Course course = courseOpt.get();
        if (title != null) course.setTitle(title);
        if (description != null) course.setDescription(description);
        if (price != null) course.setPrice(price);
        if (isPremium != null) course.setIsPremium(isPremium);

        Course updatedCourse = courseRepository.save(course);
        log.info("Course updated successfully: {}", updatedCourse.getId());
        return Optional.of(updatedCourse);
    }

    public boolean deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            return false;
        }

        courseRepository.deleteById(courseId);
        log.info("Course deleted successfully: {}", courseId);
        return true;
    }

    public List<CourseDTOAll> searchCourses(String keyword) {
        List<Course> courses = courseRepository.findByTitleContainingIgnoreCase(keyword);
        return courses.stream()
                .map(this::convertToCourseDTOAll)
                .collect(Collectors.toList());
    }

    public boolean isCourseAccessible(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return false;
        }

        // Free courses are accessible to all
        if (!course.getIsPremium()) {
            return true;
        }

        // Premium courses require enrollment
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    private CourseDTOAll convertToCourseDTOAll(Course course) {
        CourseDTOAll dto = new CourseDTOAll();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setPrice(course.getPrice());
        dto.setIsPremium(course.getIsPremium());
        dto.setCreatedAt(course.getCreatedAt());

        // Get video count
        Long videoCount = videoRepository.countByCourseId(course.getId());
        dto.setTotalVideos(videoCount.intValue());

        // Get enrollment count
        Long enrollmentCount = enrollmentRepository.countByCourseId(course.getId());
        dto.setEnrollmentCount(enrollmentCount);

        return dto;
    }

    private VideoDTO convertToVideoDTO(Video video){
        VideoDTO dto = new VideoDTO();

        dto.setOrderIndex(video.getOrderIndex());
        dto.setCourseId(video.getCourseId());
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setS3ObjectKey(video.getS3ObjectKey());
        dto.setFileSizeBytes(video.getFileSizeBytes());

        return dto;
    }

}