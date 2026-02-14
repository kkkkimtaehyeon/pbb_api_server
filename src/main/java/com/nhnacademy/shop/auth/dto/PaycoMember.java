package com.nhnacademy.shop.auth.dto;

import lombok.Getter;

@Getter
public class PaycoMember {

    private String idNo;
    private String email;
    private String maskedEmail;
    private String name;
    private GenderCode genderCode;
    private String birthdayMMdd;
    private String ageGroup;
}

enum GenderCode {
    MALE,
    FEMALE
}