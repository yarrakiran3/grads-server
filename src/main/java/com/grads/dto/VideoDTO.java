package com.grads.dto;

import lombok.Data;

// VideoDTO
@Data
public class VideoDTO {
    private Long id;
    private Long courseId;
    private String title;
    private String s3ObjectKey;
    private Integer orderIndex;
    private Long fileSizeBytes;
}
