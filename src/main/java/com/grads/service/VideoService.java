package com.grads.service;

import com.grads.dto.VideoDTO;
import com.grads.entity.Course;
import com.grads.entity.Video;
import com.grads.repository.CourseRepository;
import com.grads.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VideoService {

    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;
    private final S3Service s3Service;
    private final CourseService courseService;


    public String testUpload( String title, MultipartFile file) throws IOException {


        // Upload file to S3
        String s3Key = s3Service.uploadVideo(file, 12L,"Dummy");





        return "Video Uploaded Successfully";
    }

    public VideoDTO uploadVideoToCourse(Long courseId, String title, MultipartFile file, Integer orderIndex) throws IOException {
        // Validate course exists
        System.out.println("Cheking For course ");

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        System.out.println("Found course and uploading");
        // Upload file to S3
        String s3Key = s3Service.uploadVideo(file, course.getId(),course.getTitle());

        // Set order index if not provided
        if (orderIndex == null) {
            Optional<Integer> maxOrder = videoRepository.findMaxOrderIndexByCourseId(courseId);
            orderIndex = maxOrder.orElse(0) + 1;
        }

        // Create video entity
        Video video = new Video();
        video.setCourseId(courseId);
        video.setTitle(title);
        video.setS3ObjectKey(s3Key);
        video.setOrderIndex(orderIndex);

        Video savedVideo = videoRepository.save(video);
        log.info("Video uploaded successfully: {} for course: {}", savedVideo.getId(), courseId);
        return convertToVideoDTO(savedVideo);
    }


    public List<VideoDTO> getVideosByCourse(Long courseId, Long userId) throws IllegalAccessException {
        // Check if user has access to the course
        if (!courseService.isCourseAccessible(courseId, userId)) {
            throw new IllegalAccessException("User does not have access to this course");
        }

        List<Video> videos = videoRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        return videos.stream()
                .map(this::convertToVideoDTO)
                .collect(Collectors.toList());
    }

//    public Optional<VideoDTO> getVideoWithAccess(Long videoId, Long courseId, Long userId) throws IllegalAccessException {
//        // Check if user has access to the course
//        if (!courseService.isCourseAccessible(courseId, userId)) {
//            throw new IllegalAccessException("User does not have access to this course");
//        }
//
//        Optional<Video> videoOpt = videoRepository.findByIdAndCourseId(videoId, courseId);
//        if (videoOpt.isEmpty()) {
//            return Optional.empty();
//        }
//
//        Video video = videoOpt.get();
//        VideoDTO videoDTO = convertToVideoDTO(video);
//
//        // Generate signed URL for secure access
//        String signedUrl = s3Service.generateSignedUrl(video.getVideoUrl(), 60); // 1 hour expiration
//        videoDTO.setSignedUrl(signedUrl);
//
//        return Optional.of(videoDTO);
//    }

//

    public Optional<Video> updateVideo(Long videoId, String title, Integer orderIndex) {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isEmpty()) {
            return Optional.empty();
        }

        Video video = videoOpt.get();
        if (title != null) video.setTitle(title);
        if (orderIndex != null) video.setOrderIndex(orderIndex);

        Video updatedVideo = videoRepository.save(video);
        log.info("Video updated successfully: {}", updatedVideo.getId());
        return Optional.of(updatedVideo);
    }

//    public boolean deleteVideo(Long videoId) {
//        Optional<Video> videoOpt = videoRepository.findById(videoId);
//        if (videoOpt.isEmpty()) {
//            return false;
//        }
//
//        Video video = videoOpt.get();
//
//        // Delete from S3
//        boolean s3Deleted = s3Service.deleteVideo(video.getVideoUrl());
//        if (!s3Deleted) {
//            log.warn("Failed to delete video from S3: {}", video.getVideoUrl());
//        }
//
//        // Delete from database
//        videoRepository.deleteById(videoId);
//        log.info("Video deleted successfully: {}", videoId);
//        return true;
//    }

//    public Optional<VideoDTO> getNextVideo(Long courseId, Long currentVideoId, Long userId) throws IllegalAccessException {
//        if (!courseService.isCourseAccessible(courseId, userId)) {
//            throw new IllegalAccessException("User does not have access to this course");
//        }
//
//        Video currentVideo = videoRepository.findById(currentVideoId).orElse(null);
//        if (currentVideo == null || !currentVideo.getCourseId().equals(courseId)) {
//            return Optional.empty();
//        }
//
//        Optional<Video> nextVideo = videoRepository.findNextVideo(courseId, currentVideo.getOrderIndex());
//        return nextVideo.map(this::convertToVideoDTO);
//    }

//    public Optional<VideoDTO> getPreviousVideo(Long courseId, Long currentVideoId, Long userId) throws IllegalAccessException {
//        if (!courseService.isCourseAccessible(courseId, userId)) {
//            throw new IllegalAccessException("User does not have access to this course");
//        }
//
//        Video currentVideo = videoRepository.findById(currentVideoId).orElse(null);
//        if (currentVideo == null || !currentVideo.getCourseId().equals(courseId)) {
//            return Optional.empty();
//        }
//
//        Optional<Video> previousVideo = videoRepository.findPreviousVideo(courseId, currentVideo.getOrderIndex());
//        return previousVideo.map(this::convertToVideoDTO);
//    }

    public String generateVideoStreamingUrl(String videoKey) {
        // Generate CloudFront URL for optimized streaming
        return s3Service.generateCloudFrontUrl(videoKey);
    }

    private VideoDTO convertToVideoDTO(Video video) {
        VideoDTO dto = new VideoDTO();
        dto.setId(video.getId());
        dto.setCourseId(video.getCourseId());
        dto.setTitle(video.getTitle());
        dto.setS3ObjectKey(video.getS3ObjectKey());
        dto.setFileSizeBytes(video.getFileSizeBytes());
        dto.setOrderIndex(video.getOrderIndex());

        return dto;
    }
}