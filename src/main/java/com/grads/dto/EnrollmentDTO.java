package com.grads.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// EnrollmentDTO
@Data
public class EnrollmentDTO {
    private Long id;
    private Long userId;
    private Long courseId;
    private LocalDateTime enrolledAt;
    private String courseTitle;
    private String courseDescription;
    private Boolean isPremium;
    private BigDecimal coursePrice;
}
