package com.nhnacademy.shop.auth.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class PaycoMemberResponse {

    private Header header;
    private Data data;

    @Getter
    public static class Header {
        private boolean isSuccessful;
        private int resultCode;
        private String resultMessage;
    }

    @Getter
    public static class Data {
        private PaycoMember member;
    }

    public String getEmail() {
        return data.member.getEmail();
    }

    public String getName() {
        return data.member.getName();
    }

    public String getMemberId() {
        return data.member.getIdNo();
    }
}
