package ru.yandex.prakticum.dto;

import lombok.Data;

@Data
public class UserLoginResponse {
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private UserData user;

    @Data
    public static class UserData {
        private String email;
        private String name;
    }
}