package com.grads.dto;


import com.grads.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String email;
    private String firstname;
    private String lastname;
    private String profileimageurl;

    public UserDto(User user) {
        this.email=user.getEmail();
        this.firstname=user.getFirstName();
        this.lastname=user.getLastName();
        this.profileimageurl=user.getProfileImageUrl();
    }


//    private String createdAt;
//    private boolean isActive;

    public String getFullName() {
        return firstname + " " + lastname;
    }
}