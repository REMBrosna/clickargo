package com.acleda.company.student.administrator.dto;

import com.acleda.company.student.administrator.model.Role;
import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.common.AbstractDTO;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUser extends AbstractDTO<AppUser, TAppUser> {
    private String id;
    private String email;
    private String username;
    private String firstname;
    private String lastname;
    private String fullName;
    private String gender;
    private Date dtOfBirth;
    private String address;
    private String conNumber;
    private Set<Role> roles = new HashSet<>();
    private int messageCount;
    private Character status;
    private Date usrDtCreate;
    private String usrUidCreate;
    private Date usrDtLupd;
    private String usrUidLupd;
    public AppUser(TAppUser entity) {
        super(entity);
    }

    @Override
    public void init() {

    }
    @Override
    public int compareTo(AppUser o) {
        return 0;
    }
}
