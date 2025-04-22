package com.acleda.company.student.configuration.security;

public class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long ACCESS_TOKNE_EXPIRATION_TIME = 900_000; // 15 mins
    public static final long REFRESH_TOKNE_EXPIRATION_TIME = 4 * 900_000; // 15 mins
}
