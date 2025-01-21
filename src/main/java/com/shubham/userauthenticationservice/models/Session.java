package com.shubham.userauthenticationservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Session extends BaseModel{

    @Column(length = 512)
    private String token;

    @ManyToOne
//    @JoinColumn(name = "user_id")
    private User user;
}

//1         m
//user     session
//1         1
//
//
//1        m
