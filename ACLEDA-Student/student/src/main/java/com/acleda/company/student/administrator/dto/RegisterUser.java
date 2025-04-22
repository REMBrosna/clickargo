package com.acleda.company.student.administrator.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUser {

    private String email;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String fullName;
    private String gender;
    private Date dtOfBirth;
    private String address;
    private String conNumber;
    private String status;
    private Date usrDtCreate;
    private String usrUidCreate;
    private Date usrDtLupd;
    private String usrUidLupd;
}
