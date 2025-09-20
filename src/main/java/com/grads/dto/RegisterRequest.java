package com.grads.dto;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    public String email;
    public String password;
    public String firstname;
    public String lastname;

}
