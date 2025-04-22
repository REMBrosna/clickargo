package com.acleda.company.student.configuration.token.filter;

import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.configuration.security.SecurityConstants;
import com.acleda.company.student.configuration.token.JwtTokenBody;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Base64;

@Log4j2
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);
        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.replace(SecurityConstants.TOKEN_PREFIX, "");
        try {
            // exceptions might be thrown in creating the claims if for example the token is expired
            // 4. Validate the token
            JwtTokenBody jwtTokenBody = validateJwtToken(token);
            if (jwtTokenBody != null) {
                // 5. Create auth object
                // UsernamePasswordAuthenticationToken: A built-in object, used by spring to represent the current authenticated / being authenticated user.
                // It needs a list of authorities, which has type of GrantedAuthority interface, where SimpleGrantedAuthority is an implementation of that interface
                TAppUser appUser = new TAppUser();
                appUser.setId(jwtTokenBody.getId());
                appUser.setUsername(jwtTokenBody.getUsername());
                appUser.setEmail(jwtTokenBody.getEmail());
                appUser.setStrRoles(jwtTokenBody.getRoles());
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(appUser, null, appUser.getAuthorities());

                // 6. Authenticate the user
                // Now, user is authenticated
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            log.error("doFilterInternal", e);
            // In case of failure. Make sure it's clear; so guarantee user won't be authenticated
            SecurityContextHolder.clearContext();
        }

        // go to the next filter in the filter chain
        filterChain.doFilter(request, response);
    }

    public JwtTokenBody validateJwtToken(String jwtToken) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        ObjectMapper objectMapper = new ObjectMapper();
        String[] strJwtTokens = jwtToken.split("\\.");
        if (strJwtTokens.length != 3) {
            throw new RuntimeException("InvalidTokenFormat");
        }
        Security.addProvider(new BouncyCastleProvider());
        return objectMapper.readValue(Base64.getUrlDecoder().decode(strJwtTokens[1]), JwtTokenBody.class);
    }
}
