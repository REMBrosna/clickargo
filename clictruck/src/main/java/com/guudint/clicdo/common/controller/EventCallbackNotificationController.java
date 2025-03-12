package com.guudint.clicdo.common.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.common.dto.EventCallbackNotification;
import com.guudint.clickargo.clictruck.common.service.impl.EventCallbackNotificationServiceImpl;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.util.email.SysParam;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/truck/odometer/notification")
public class EventCallbackNotificationController extends AbstractPortalController {

    private static Logger log = Logger.getLogger(EventCallbackNotificationController.class);

    @Autowired
    private EventCallbackNotificationServiceImpl eventCallbackNotificationServiceImpl;
    
    @Autowired
	SysParam sysParam;

    @PostMapping("")
    public ResponseEntity<Object> eventNotification(@RequestHeader("Authorization") String authorization,
                                                    @RequestBody EventCallbackNotification dto) {
        log.debug("eventNotification");
        ServiceStatus serviceStatus = new ServiceStatus();
        try {
            String[] credentials = extractCredentials(authorization);
            String username = credentials[0];
            String password = credentials[1];
            if (!isValidCredentials(username, password)) {
                throw new Exception("Invalid username or password");
            }            
            eventCallbackNotificationServiceImpl.updateNotify(dto);
            serviceStatus.setStatus(STATUS.SUCCESS);
            return new ResponseEntity<>(serviceStatus, HttpStatus.OK);
        } catch (Exception e) {
            log.error("eventNotification", e);
            serviceStatus.setStatus(STATUS.EXCEPTION);
            serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
            return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
        }
    }

    private String[] extractCredentials(String authorizationHeader) {
        try {
            String base64Credentials = authorizationHeader;
            if(authorizationHeader.contains("Basic")) {
            	base64Credentials = authorizationHeader.substring("Basic".length()).trim();
            }
            byte[] decodedBytes = Base64.getUrlDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);
            return credentials.split(":", 2);
        } catch (Exception e) {
            log.error("Error extracting credentials", e);
            return new String[0];
        }
    }

    private boolean isValidCredentials(String username, String password) {
        String expectedCredentials = sysParam.getValString("CLICTRUCK_ASCEND_NOTIFICATION", "dXNlck5hbWU6UGFzc3dvcmQ=");
        String[] credentials = extractCredentials(expectedCredentials);
        if (credentials.length < 2) {
            return false;
        }
        String expectedUsername = credentials[0];
        String expectedPassword = credentials[1];
        return username.equals(expectedUsername) && password.equals(expectedPassword);
    }
}