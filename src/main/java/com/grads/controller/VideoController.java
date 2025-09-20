package com.grads.controller;

import com.grads.dto.*;
import com.grads.entity.Video;
import com.grads.security.JwtUtil;
import com.grads.service.AuthService;
import com.grads.service.VideoService;
import com.grads.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class VideoController {

    private final VideoService videoService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @GetMapping("/test")
    public  String checkVideoAPI(){
        return "Video test working";
    }

    @PostMapping("/testupload")
    public String testUploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title
    ) throws IOException {
        return videoService.testUpload(title,file);
    }

    @PostMapping("/upload/{courseId}")
    public ResponseEntity<ApiResponseDTO<VideoDTO>> uploadVideo(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "orderIndex", required = false) Integer orderIndex,
            HttpServletRequest request) {
        try {
//            getUserIdFromRequest(request); // Validate authentication
            System.out.println("Started Uploading video for course id "+courseId);

            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("File cannot be empty"));
            }

            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Video title is required"));
            }

            VideoDTO video = videoService.uploadVideoToCourse(courseId, title.trim(), file, orderIndex);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success(video, "Video uploaded successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.valueOf(431))
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading video to course ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to upload video"));
        }
    }
//
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponseDTO<List<VideoDTO>>> getVideosByCourse(
            @PathVariable Long courseId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            List<VideoDTO> videos = videoService.getVideosByCourse(courseId, userId);
            return ResponseEntity.ok(ApiResponseDTO.success(videos));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDTO.error("Access denied: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching videos for course ID: " + courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to fetch videos"));
        }
    }
//
//    @GetMapping("/{videoId}/course/{courseId}")
//    public ResponseEntity<ApiResponseDTO<VideoDTO>> getVideoWithAccess(
//            @PathVariable Long videoId,
//            @PathVariable Long courseId,
//            HttpServletRequest request) {
//        try {
//            Long userId = getUserIdFromRequest(request);
//            Optional<VideoDTO> video = videoService.getVideoWithAccess(videoId, courseId, userId);
//
//            if (video.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(ApiResponseDTO.error("Video not found"));
//            }
//
//            return ResponseEntity.ok(ApiResponseDTO.success(video.get()));
//        } catch (IllegalAccessException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(ApiResponseDTO.error("Access denied: " + e.getMessage()));
//        } catch (Exception e) {
//            log.error("Error fetching video with ID: " + videoId + " for course: " + courseId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponseDTO.error("Failed to fetch video"));
//        }
//    }
//

//
//    @PutMapping("/{videoId}")
//    public ResponseEntity<ApiResponseDTO<Video>> updateVideo(
//            @PathVariable Long videoId,
//            @RequestBody VideoUploadRequestDTO request,
//            HttpServletRequest httpRequest) {
//        try {
//            getUserIdFromRequest(httpRequest); // Validate authentication
//
//            Optional<Video> updatedVideo = videoService.updateVideo(
//                    videoId,
//                    request.getTitle(),
//                    request.getOrderIndex()
//            );
//
//            if (updatedVideo.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(ApiResponseDTO.error("Video not found"));
//            }
//
//            return ResponseEntity.ok(ApiResponseDTO.success(updatedVideo.get(), "Video updated successfully"));
//        } catch (Exception e) {
//            log.error("Error updating video with ID: " + videoId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponseDTO.error("Failed to update video"));
//        }
//    }
//
//    @DeleteMapping("/{videoId}")
//    public ResponseEntity<ApiResponseDTO<Void>> deleteVideo(
//            @PathVariable Long videoId,
//            HttpServletRequest request) {
//        try {
//            getUserIdFromRequest(request); // Validate authentication
//
//            boolean deleted = videoService.deleteVideo(videoId);
//            if (!deleted) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(ApiResponseDTO.error("Video not found"));
//            }
//
//            return ResponseEntity.ok(ApiResponseDTO.success(null, "Video deleted successfully"));
//        } catch (Exception e) {
//            log.error("Error deleting video with ID: " + videoId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponseDTO.error("Failed to delete video"));
//        }
//    }
//
//    @GetMapping("/{videoId}/course/{courseId}/next")
//    public ResponseEntity<ApiResponseDTO<VideoDTO>> getNextVideo(
//            @PathVariable Long videoId,
//            @PathVariable Long courseId,
//            HttpServletRequest request) {
//        try {
//            Long userId = getUserIdFromRequest(request);
//            Optional<VideoDTO> nextVideo = videoService.getNextVideo(courseId, videoId, userId);
//
//            if (nextVideo.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(ApiResponseDTO.error("No next video found"));
//            }
//
//            return ResponseEntity.ok(ApiResponseDTO.success(nextVideo.get()));
//        } catch (IllegalAccessException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(ApiResponseDTO.error("Access denied: " + e.getMessage()));
//        } catch (Exception e) {
//            log.error("Error fetching next video for video ID: " + videoId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponseDTO.error("Failed to fetch next video"));
//        }
//    }
//
//    @GetMapping("/{videoId}/course/{courseId}/previous")
//    public ResponseEntity<ApiResponseDTO<VideoDTO>> getPreviousVideo(
//            @PathVariable Long videoId,
//            @PathVariable Long courseId,
//            HttpServletRequest request) {
//        try {
//            Long userId = getUserIdFromRequest(request);
//            Optional<VideoDTO> previousVideo = videoService.getPreviousVideo(courseId, videoId, userId);
//
//            if (previousVideo.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(ApiResponseDTO.error("No previous video found"));
//            }
//
//            return ResponseEntity.ok(ApiResponseDTO.success(previousVideo.get()));
//        } catch (IllegalAccessException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(ApiResponseDTO.error("Access denied: " + e.getMessage()));
//        } catch (Exception e) {
//            log.error("Error fetching previous video for video ID: " + videoId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponseDTO.error("Failed to fetch previous video"));
//        }
//    }
//
//    @GetMapping("/{videoKey}/streaming-url")
//    public ResponseEntity<ApiResponseDTO<String>> getStreamingUrl(
//            @PathVariable String videoKey,
//            HttpServletRequest request) {
//        try {
//            getUserIdFromRequest(request); // Validate authentication
//            String streamingUrl = videoService.generateVideoStreamingUrl(videoKey);
//            return ResponseEntity.ok(ApiResponseDTO.success(streamingUrl));
//        } catch (Exception e) {
//            log.error("Error generating streaming URL for video key: " + videoKey, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponseDTO.error("Failed to generate streaming URL"));
//        }
//    }
//
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