package com.grads.dto;

import lombok.Data;

import java.math.BigDecimal;

// Course Create/Update Request DTO
@Data
public class CourseRequestDTO {
    private String title;
    private String description;
    private BigDecimal price;
    private Boolean isPremium;
}
