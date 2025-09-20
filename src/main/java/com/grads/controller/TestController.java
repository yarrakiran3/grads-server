package com.grads.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {


    @GetMapping("/check")
    public String isWorking(){
        return "GradS backend is working";
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(){
        System.out.println("Clicked Login");
        Map<String, Object> dynamicObject = new HashMap<>();
        dynamicObject.put("id", "1");
        dynamicObject.put("email", "kiranyarra3@gmail.com");
        dynamicObject.put("name", "Kiran");
        dynamicObject.put("token","Dummy-Token-From-Backend");
        return ResponseEntity.ok(dynamicObject);
    };

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(){
        System.out.println("Clicked Register");

        Map<String, Object> dynamicObject = new HashMap<>();
        dynamicObject.put("id", "1");
        dynamicObject.put("email", "kiranyarra3@gmail.com");
        dynamicObject.put("name", "Kiran");
        dynamicObject.put("token","Dummy-Token-From-Backend");
        return ResponseEntity.ok(dynamicObject);
    }

}
