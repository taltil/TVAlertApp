package com.example.tvalert.DTO;


public class Authentication {

    private String apiKey;
    private String userName;
    private String userKey;

    public Authentication(String apiKeyStr, String userNameStr, String userKeyStr) {
        apiKey = apiKeyStr;
        userName = userNameStr;
        userKey = userKeyStr;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "apiKey='" + apiKey + '\'' +
                ", userName='" + userName + '\'' +
                ", userKey='" + userKey + '\'' +
                '}';
    }
}

