package com.acleda.company.student.websocket.config;

import com.acleda.company.student.configuration.token.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtService;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            String token = httpServletRequest.getParameter("token"); // 👈 from query string

            if (token != null) {
                String username = jwtService.getUsernameFromToken(token);
                attributes.put("username", username);
                return true;
            }
        }

        return false; // Reject the connection if token is missing
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {
        // nothing
    }
    private String getTokenFromRequest(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}

