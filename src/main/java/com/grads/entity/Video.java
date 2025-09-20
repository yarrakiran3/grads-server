package com.grads.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "videos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(nullable = false, length = 255)
    private String title;

    // Store S3 object key instead of full URL
    @Column(name = "s3_object_key", nullable = false, length = 500)
    private String s3ObjectKey; // e.g., "courses/123/videos/video-intro.mp4"

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;



    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;


}