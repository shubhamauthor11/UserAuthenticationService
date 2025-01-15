package com.shubham.userauthenticationservice.dtos;


import com.shubham.userauthenticationservice.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private int phoneNo;
    private List<Role> roles = new ArrayList<>();
}
