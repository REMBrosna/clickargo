package com.acleda.company.student.configuration.principal;

import com.acleda.company.student.administrator.model.TAppUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccountPrincipalService {

    public TAppUser getAccountPrincipal() {
        return (TAppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
