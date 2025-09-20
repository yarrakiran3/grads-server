package com.grads.controller;


import com.grads.dto.LoginRequest;
import com.grads.dto.RegisterRequest;
import com.grads.dto.UserDto;
import com.grads.entity.User;
import com.grads.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public final AuthService userService;

    @GetMapping("/test")
    public String getAuthTest(){
        return "AuthTestWorking";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req){
        System.out.println(req.email);
        System.out.println(req.password);

        return userService.login(req.email,req.password);

    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisterRequest req){
        System.out.println(req.email);
        System.out.println(req.password);
        System.out.println(req.firstname);
        System.out.println(req.lastname);

        return  userService.createUser(req.email,req.password,req.firstname,req.lastname);



    }

}
