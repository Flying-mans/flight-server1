package com.jejuro.server1.entity;

import com.jejuro.server1.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Alarm {

    @Id
    @Column(name = "alarm_id")
    private Long id;

    private int price;

    private String flightCode;

    private int status;

    private String depDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;
}
