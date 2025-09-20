package com.grads.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/protected")
public class ProtectedTestController {

    @GetMapping("/test")
    public String check(){
        return "Protected route is working with auth";
    }
}
