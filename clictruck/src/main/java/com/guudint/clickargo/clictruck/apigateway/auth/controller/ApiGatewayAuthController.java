package com.guudint.clickargo.clictruck.apigateway.auth.controller;

import com.guudint.clickargo.clictruck.apigateway.auth.services.ApiGatewayAuthService;
import com.guudint.clickargo.clictruck.apigateway.dto.MessageResponse;
import com.guudint.clickargo.clictruck.apigateway.dto.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ProcessingException;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
@RestController
@RequestMapping("/api/v2/auth")
@CrossOrigin
public class ApiGatewayAuthController {
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ApiGatewayAuthController.class);
    @Autowired
    protected MessageSource messageSource;
    @Autowired
    protected ApiGatewayAuthService apiGatewayAuthService;
    @PostMapping(value = "/token", headers = "Accept=application/json")
    public ResponseEntity<?> login(@RequestBody UserCredentials userCredentials, HttpServletRequest request,
                                   HttpServletResponse response) {
        log.debug("Starting login api gateway process");
        try {
            return apiGatewayAuthService.handleLogin(userCredentials, request, response);
        } catch (UsernameNotFoundException | ProcessingException ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ex.getMessage()));
        } catch (Exception ex) {
            log.error("Error during login", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(ex.getMessage()));
        }
    }
}

