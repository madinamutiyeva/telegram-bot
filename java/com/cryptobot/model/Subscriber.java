package com.cryptobot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "subscribers")
@Getter
@Setter
@ToString
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    private String userUUID;

    @Column(name = "chat_id")
    private Long chatId;

    private Double price;

    @Column(name = "sms_limit")
    private int smsLimit;

    @Column(name = "sms_count")
    private int smsCount;

}
