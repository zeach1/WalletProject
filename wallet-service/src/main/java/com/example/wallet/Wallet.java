package com.example.wallet;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long userId;

    private String phoneNumber;

    private Double balance;

    @Enumerated(value = EnumType.STRING)
    private UserIdentifier userIdentifier;

    private String identifierValue;



}
