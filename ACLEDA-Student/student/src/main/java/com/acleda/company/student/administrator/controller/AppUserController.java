package com.acleda.company.student.administrator.controller;

import com.acleda.company.student.administrator.dto.ChangePassword;
import com.acleda.company.student.administrator.dto.RegisterUser;
import com.acleda.company.student.administrator.dto.ResetPassword;
import com.acleda.company.student.administrator.dto.TokenResponse;
import com.acleda.company.student.administrator.enums.NotificationTemplateName;
import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.administrator.repository.impl.UserDetailsServiceImpl;
import com.acleda.company.student.common.exceptions.ParameterException;
import com.acleda.company.student.common.exceptions.ValidationException;
import com.acleda.company.student.configuration.exception.UserAlreadyExistsException;
import com.acleda.company.student.configuration.principal.AccountPrincipalService;
import com.acleda.company.student.configuration.token.JwtUtil;
import com.acleda.company.student.infrastructure.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RequestMapping
@CrossOrigin
@RestController
@Log4j2
public class AppUserController {

    private final UserDetailsServiceImpl userService;
    @Autowired
    private AccountPrincipalService principalService;
    @Autowired
    private JwtUtil jwtService;

    public AppUserController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUser registerUserDto) {
        try {
            TAppUser registeredUser = userService.signup(registerUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (UserAlreadyExistsException ex) {
            log.warn("User registration failed: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
        } catch (ValidationException e) {
            log.error("Validation error: {} ", e.getValidationErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getValidationErrors());
        } catch (Exception ex) {
            log.error("Unexpected error during user registration: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred. Please try again later."));
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Object> getUserByName(@PathVariable String username) {
        ApiResponse<Object> serviceStatus = new ApiResponse<>();

        try {
            UserDetails user = userService.loadUserByUsername(username);
            if (Objects.isNull(user)) {
                serviceStatus.setMessage("User doesn't exist");
                serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            TAppUser accountPrincipal = principalService.getAccountPrincipal();
            if (Objects.isNull(accountPrincipal)) {
                serviceStatus.setMessage("AccountPrincipal doesn't exist");
                serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (StringUtils.isBlank(username)) {
                serviceStatus.setMessage("UserId is null");
                serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            serviceStatus.setCode(ApiResponse.OK);
            serviceStatus.setData(userService.loadUserByUsername(username));
            return new ResponseEntity<>(serviceStatus, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("getUserByName error", ex);
            serviceStatus.setMessage(ex.getMessage());
            serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> body) {
        log.info("refreshAccessToken");
        String refreshToken = body.get("refreshToken");

        if (refreshToken != null && jwtService.validateRefreshToken(refreshToken)) {
            String username = jwtService.getUsernameFromToken(refreshToken);
            String newAccessToken = jwtService.generateAccessToken(username); // Generate new access token
            long expiry = jwtService.getExpiryForAccessToken(newAccessToken); // Get expiry time
            return ResponseEntity.ok(new TokenResponse(newAccessToken, expiry));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePassword dto) {
        log.debug("changePassword");
        ApiResponse<Object> serviceStatus = new ApiResponse<>();
        try {
            userService.changePassword(dto);
            // Success response
            serviceStatus.setMessage("Password has been changed successfully");
            serviceStatus.setData(null);  // You can set any additional data if needed.
            return ResponseEntity.ok(serviceStatus);
        } catch (ParameterException ex) {
            // Handle parameter-related errors (e.g., invalid input)
            log.error("Invalid input for password reset: {}", ex.getMessage(), ex);
            serviceStatus.setMessage("Invalid input: " + ex.getMessage());
            serviceStatus.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);

        } catch (UsernameNotFoundException ex) {
            // Handle user not found specific errors
            log.error("User not found: {}", ex.getMessage(), ex);
            serviceStatus.setMessage("User not found: " + ex.getMessage());
            serviceStatus.setCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(serviceStatus, HttpStatus.NOT_FOUND);

        } catch (Exception ex) {
            // Catch any other unexpected exceptions
            log.error("Unexpected error during password reset: {}", ex.getMessage(), ex);
            serviceStatus.setMessage("An unexpected error occurred: " + ex.getMessage());
            serviceStatus.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<Object> changePassword(@RequestBody ResetPassword dto) {
        log.debug("reset password");
        ApiResponse<Object> serviceStatus = new ApiResponse<>();
        try {
            // Call service to reset user password
            userService.resetUserPassword(dto.getUserNameOrEmail(), NotificationTemplateName.RESET_PASSWORD.name());

            // Success response
            serviceStatus.setMessage("Password has been reset successfully");
            serviceStatus.setData(null);  // You can set any additional data if needed.
            return ResponseEntity.ok(serviceStatus);

        } catch (ParameterException ex) {
            // Handle parameter-related errors (e.g., invalid input)
            log.error("Invalid input for password reset: {}", ex.getMessage(), ex);
            serviceStatus.setMessage("Invalid input: " + ex.getMessage());
            serviceStatus.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);

        } catch (UsernameNotFoundException ex) {
            // Handle user not found specific errors
            log.error("User not found: {}", ex.getMessage(), ex);
            serviceStatus.setMessage("User not found: " + ex.getMessage());
            serviceStatus.setCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(serviceStatus, HttpStatus.NOT_FOUND);

        } catch (Exception ex) {
            // Catch any other unexpected exceptions
            log.error("Unexpected error during password reset: {}", ex.getMessage(), ex);
            serviceStatus.setMessage("An unexpected error occurred: " + ex.getMessage());
            serviceStatus.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}