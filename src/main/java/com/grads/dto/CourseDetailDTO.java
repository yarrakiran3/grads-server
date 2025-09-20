package com.grads.dto;

import com.grads.entity.Video;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// CourseDetailDTO
@Data
public class CourseDetailDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Boolean isPremium;
    private LocalDateTime createdAt;
    private boolean isEnrolled;
    private Integer totalVideos;
    private Long enrollmentCount;
    private List<VideoDTO> videos;
}
