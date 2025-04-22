package com.acleda.company.student.administrator.repository.impl;

import com.acleda.company.student.administrator.dto.ChangePassword;
import com.acleda.company.student.administrator.dto.RegisterUser;
import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.administrator.model.GroupPosition;
import com.acleda.company.student.administrator.model.Role;
import com.acleda.company.student.administrator.repository.AppUserRepository;
import com.acleda.company.student.administrator.repository.RoleRepository;
import com.acleda.company.student.administrator.validation.UserServiceValidator;
import com.acleda.company.student.common.exceptions.ParameterException;
import com.acleda.company.student.common.exceptions.ValidationException;
import com.acleda.company.student.configuration.principal.AccountPrincipalService;
import com.acleda.company.student.event.AppUserEvent;
import com.acleda.company.student.utils.RandomNumberPasswordGenerator;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private AccountPrincipalService principalService;
    @Autowired
    private UserServiceValidator userServiceValidator;


    public UserDetailsServiceImpl(AppUserRepository appUserRepository,
                                  PasswordEncoder passwordEncoder,
                                  RoleRepository roleRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user with username: {}", username);
        TAppUser appUser = appUserRepository.findAppUserByUsernameOrEmail(username);
        if (appUser == null) {
            log.warn("User with username '{}' not found.", username);
            throw new UsernameNotFoundException(username);
        }
        log.info("User with username '{}' found successfully.", username);
        return appUser;
    }

    public TAppUser signup(RegisterUser dto) throws Exception {
        log.info("signup");

        Map<String, Object> validationErrors = new HashMap<>();

        TAppUser existingUserByUsername = appUserRepository.findAppUserByUsernameOrEmail(dto.getUsername());
        if (Objects.nonNull(existingUserByUsername)) {
            validationErrors.put("username", "user with the provided username already exists.");
        }

        TAppUser existingUserByEmail = appUserRepository.findAppUserByUsernameOrEmail(dto.getEmail());
        if (Objects.nonNull(existingUserByEmail)) {
            validationErrors.put("email", "user with the provided email already exists.");
        }

        // Call additional custom validator
        userServiceValidator.validateUserRegister(dto);
        Map<String, Object> additionalErrors = validator(dto);
        if (!additionalErrors.isEmpty()) {
            validationErrors.putAll(additionalErrors);
        }

        // Throw only if there are validation issues
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        // Proceed to register
        Role defaultRole = roleRepository.findRoleByName(GroupPosition.STUDENT.getCode());
        if (Objects.isNull(defaultRole)) {
            throw new ParameterException("Role not found");
        }

        TAppUser user = new TAppUser();
        user.setUsername(dto.getUsername());
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        user.setConNumber(dto.getConNumber());
        user.setGender(dto.getGender());
        user.setDtOfBirth(dto.getDtOfBirth());
        user.setUsrDtCreate(dto.getUsrDtCreate());
        user.setStatus('A');
        user.setUsrUidCreate("SYS");
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(defaultRole));

        return appUserRepository.save(user);
    }


    private Map<String, Object> validator(RegisterUser dto) throws Exception {
        Date now = Calendar.getInstance().getTime();
        Map<String, Object> validationErrors = new HashMap<>();
        // Validate contractId
        if (StringUtils.isBlank(dto.getUsername())) {
            validationErrors.put("username", "A user with the provided username already exists.");
        }
        return validationErrors;
    }

    public void changePassword(ChangePassword dto) throws Exception {
        if (StringUtils.isBlank(dto.getNewPassword()))
            throw new ParameterException("param newPassword is null");

        if (dto.getNewPassword().length() < 8)
            throw new ParameterException("New Password length should be more than or equal to 8 characters");

        if (StringUtils.isBlank(dto.getConfirmPassword()))
            throw new ParameterException("param confirmPassword is null");

        if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new ParameterException("Passwords do not match");
        if (StringUtils.isBlank(dto.getCurrentPassword()))
            throw new ParameterException("param currentPassword is null");

        if (StringUtils.isBlank(dto.getUsername()))
            throw new ParameterException("param username is null");

        TAppUser principal = principalService.getAccountPrincipal();

        if (!principal.getUsername().equalsIgnoreCase(dto.getUsername())) {
            throw new ParameterException("You only can change yourself password.");
        }

        TAppUser appUser = appUserRepository.findAppUserByUsername(dto.getUsername());
        if (appUser == null)
            throw new UsernameNotFoundException("user does not exist");

        if (!passwordEncoder.matches(dto.getCurrentPassword(), appUser.getPassword()))
            throw new ParameterException("Current password does not match");

        appUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        appUser.setUsrDtLupd(new Date());
        appUserRepository.save(appUser);
        // 📨 Publish event for sending email
        AppUserEvent event = new AppUserEvent(this);
        event.setTAppUser(appUser);
        eventPublisher.publishEvent(event);
    }

    public void resetUserPassword(String usernameOrEmail, String action) throws Exception {
        log.info("resetUserPassword");
        try {
            if (StringUtils.isBlank(usernameOrEmail)) {
                throw new ParameterException("username or email is empty");
            }

            // Find user by username or email
            TAppUser user = appUserRepository.findAppUserByUsernameOrEmail(usernameOrEmail);
            if (user == null) {
                throw new UsernameNotFoundException("User with username or email not found: " + usernameOrEmail);
            }

            // Generate random password and encrypt it
            String generateRandomPassword = RandomNumberPasswordGenerator.generateRandomPassword();
            String encryptedPassword = passwordEncoder.encode(generateRandomPassword);

            // Update user details
            user.setPassword(encryptedPassword);
            user.setUsrDtLupd(new Date());
            appUserRepository.save(user);
            // Create and publish the event
            AppUserEvent event = new AppUserEvent(this);
            event.setTAppUser(user);
            event.setNewPassword(generateRandomPassword);
            eventPublisher.publishEvent(event);

        } catch (ParameterException e) {
            log.error("Invalid parameter provided for resetting password: {}", e.getMessage(), e);
            throw e;
        } catch (UsernameNotFoundException e) {
            log.error("User not found while resetting password: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while resetting password: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while resetting password", e);
        }
    }

}
