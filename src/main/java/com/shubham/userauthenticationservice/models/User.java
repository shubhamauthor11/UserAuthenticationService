package com.shubham.userauthenticationservice.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class User extends BaseModel{
    private String email;
    private String password;
    private String name;
    private int phoneNo;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Role> roles = new ArrayList();
}

//1       m
//user    role
//m        1
//
//m     :  m