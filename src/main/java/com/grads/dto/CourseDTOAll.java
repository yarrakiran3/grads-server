package com.grads.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// CourseDTO
@Data
public class CourseDTOAll {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Boolean isPremium;
    private LocalDateTime createdAt;
    private Integer totalVideos;
    private Long enrollmentCount;
}
