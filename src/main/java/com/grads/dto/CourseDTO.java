package com.grads.dto;

import com.grads.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// CourseDTO
@Data
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Boolean isPremium;



}



