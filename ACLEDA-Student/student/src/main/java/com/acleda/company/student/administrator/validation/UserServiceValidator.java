package com.acleda.company.student.administrator.validation;


import com.acleda.company.student.administrator.dto.RegisterUser;
import com.acleda.company.student.common.exceptions.ParameterException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserServiceValidator {

    public void validateUserRegister(RegisterUser dto) {
        // validation logic
        if (StringUtils.isBlank(dto.getUsername())) {
            throw new ParameterException("Username cannot be null or empty.");
        }
        if (StringUtils.isBlank(dto.getEmail()) || !isValidEmail(dto.getEmail())) {
            throw new ParameterException("Invalid email format.");
        }
    }
    private boolean isValidEmail(String email) {
        // Simple regex for email validation
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}
