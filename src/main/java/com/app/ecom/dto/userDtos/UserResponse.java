package com.app.ecom.dto.userDtos;

import com.app.ecom.utility.UserRole;
import lombok.Data;


@Data
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;


    private UserRole role;

}
