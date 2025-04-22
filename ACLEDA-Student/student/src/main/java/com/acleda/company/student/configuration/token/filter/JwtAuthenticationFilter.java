package com.acleda.company.student.configuration.token.filter;

import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.configuration.security.SecurityConstants;
import com.acleda.company.student.configuration.token.JwtTokenBody;
import com.acleda.company.student.configuration.token.payload.request.AuthTokenRequest;
import com.acleda.company.student.configuration.token.payload.response.AuthTokenResponse;
import com.acleda.company.student.configuration.token.payload.response.MockUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/oauth/token", "POST"));
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 1. Get credentials from request
        String username = this.obtainUsername(request);
        username = username != null ? username : "";
        username = username.trim();
        String password = this.obtainPassword(request);
        password = password != null ? password : "";

        AuthTokenRequest authTokenRequest = AuthTokenRequest.builder()
                .username(username)
                .password(password)
                .build();

        // 2. Create auth object (contains credentials) which will be used by auth manager
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                authTokenRequest.getUsername(), authTokenRequest.getPassword(), Collections.emptyList());

        // 3. Authentication manager authenticate the user, and use UserDetialsServiceImpl::loadUserByUsername() method to load the user.
        return authenticationManager.authenticate(authToken);
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Long now = System.currentTimeMillis();
        TAppUser appUser = (TAppUser) authResult.getPrincipal();
        JwtTokenBody jwtAccessToken = JwtTokenBody.builder()
                .id(appUser.getId())
                .username(appUser.getUsername())
                .email(appUser.getEmail())
                .roles(appUser.getStrAuthorities())
                .issuedAt(new Date(now))
                .expiredAt(new Date(now + SecurityConstants.ACCESS_TOKNE_EXPIRATION_TIME))
                .build();

        JwtTokenBody jwtRefreshToken = JwtTokenBody.builder()
                .id(appUser.getId())
                .username(appUser.getUsername())
                .email(appUser.getEmail())
                .roles(appUser.getStrAuthorities())
                //TODO Will uncomment when code is ready
                //.groupPosition(appUser.getGroupPosition().getValue())
                .issuedAt(new Date(now))
                .expiredAt(new Date(now + SecurityConstants.REFRESH_TOKNE_EXPIRATION_TIME))
                .build();

        AuthTokenResponse authTokenResponse = AuthTokenResponse.builder()
                .jti(UUID.randomUUID().toString())
                .accessToken(generateJwtToken(jwtAccessToken))
                .refreshToken(generateJwtToken(jwtRefreshToken))
                .expiredAt(new Date(now + 15 * 1000))
                .tokenType("bearer")
                .user(bindingMockUser(appUser))
                .build();

        // Response AuthToken
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        try {
            response.getWriter().write(new ObjectMapper().writeValueAsString(authTokenResponse));
            response.getWriter().flush();
        } finally {
            response.getWriter().close();
        }
    }

    private MockUser bindingMockUser(TAppUser appUser){
        long now = System.currentTimeMillis();
        MockUser mockUser = new MockUser();
        mockUser.setId(appUser.getId());
        mockUser.setUsername(appUser.getUsername());
        mockUser.setRoles(appUser.getRoles());
        mockUser.setEmail(appUser.getEmail());
        mockUser.setIssuedAt(new Date(now));
        mockUser.setExpiredAt(new Date(now + 15 * 1000));
        return mockUser;
    }

    public String generateJwtToken(JwtTokenBody tokenBody) {
        SecretKey key = Keys.hmacShaKeyFor("3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b".getBytes());

        return Jwts.builder()
                .setSubject(tokenBody.getUsername())
                .claim("id", tokenBody.getId())
                .claim("email", tokenBody.getEmail())
                .claim("roles", tokenBody.getRoles())
                .setIssuedAt(tokenBody.getIssuedAt())
                .setExpiration(tokenBody.getExpiredAt())
                .signWith(key, SignatureAlgorithm.HS512) // ✅ valid HMAC-SHA512 signature
                .compact();
    }
}