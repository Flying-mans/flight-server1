package com.jejuro.server1.entity;

import com.jejuro.server1.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Member{

    @Id
    @Column(name = "member_id")
    private Long id;

    private String email;

    private int isAdmin;

    private String nickName;

    private String password;

    private boolean status;

    private String phoneNum;
}
