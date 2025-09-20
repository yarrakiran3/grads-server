package com.grads.exception;



public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(String message) {
        super(message);
    }

    public VideoNotFoundException(Long videoId) {
        super("Video not found with ID: " + videoId);
    }
}


