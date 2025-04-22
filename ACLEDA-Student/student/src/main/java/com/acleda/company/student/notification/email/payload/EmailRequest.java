package com.acleda.company.student.notification.email.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EmailRequest {

    @Email
    @NotNull
    @Size(min = 1, message = "Please, set an email address to send the message to it")
    private String recipient;
    private String subject;
    private String content;
}
