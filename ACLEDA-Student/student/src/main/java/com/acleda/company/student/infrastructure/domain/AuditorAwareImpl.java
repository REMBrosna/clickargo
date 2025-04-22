package com.acleda.company.student.infrastructure.domain;

import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.administrator.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<TAppUser> {

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public Optional<TAppUser> getCurrentAuditor() {
        Optional<TAppUser> currentUser;
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            final Authentication authentication = securityContext.getAuthentication();
            try {
                if (authentication != null) {
                    currentUser = Optional.ofNullable((TAppUser) authentication.getPrincipal());
                } else {
                    currentUser = retrieveSuperUser();
                }
            }catch (Exception ex) {
                // ex.printStackTrace();
                currentUser = retrieveSuperUser();
            }
        } else {
            currentUser = retrieveSuperUser();
        }
        return currentUser;
    }

    private Optional<TAppUser> retrieveSuperUser() {
        return this.userRepository.findById(Long.valueOf("1"));
    }
}
