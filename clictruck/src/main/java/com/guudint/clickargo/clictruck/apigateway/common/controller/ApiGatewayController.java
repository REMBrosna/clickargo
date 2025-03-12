package com.guudint.clickargo.clictruck.apigateway.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.apigateway.common.ApiGatewayService;
import com.guudint.clickargo.clictruck.apigateway.services.AuthenticationHelper;
import com.guudint.clickargo.clictruck.apigateway.common.services.JobServicesImpl;
import com.guudint.clickargo.clictruck.apigateway.dto.*;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.PermissionException;
import com.vcc.camelone.common.exception.ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
@RestController
@RequestMapping("/api/v2")
@CrossOrigin
public class ApiGatewayController extends AuthenticationHelper {
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ApiGatewayController.class);

    private static final String ACC_TYPE_TO = "ACC_TYPE_TO";
    private static final String ACC_TYPE_CO = "ACC_TYPE_CO";
    private static final String ACC_TYPE_FF = "ACC_TYPE_FF";
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    protected ICkSession ckSession;
    private Map<String, String> apiGatewayService;
    private HashMap<String, String> entityDTOs;
    protected ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JobServicesImpl jobServices;

    @GetMapping("/{entity}")
    public ResponseEntity<?> getEntity(@PathVariable String entity, @RequestParam Map<String, String> params) {
        try {
            // Validate Principal
            Principal principal = ckSession.getPrincipal();
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new MessageResponse("Invalid token"));
            }
            String serviceName = apiGatewayService.get(entity);
            if (serviceName == null) {
                throw new Exception("Internal server error");
            }
            ApiGatewayService<?> services = (ApiGatewayService<?>) applicationContext.getBean(serviceName);
            Optional<Object> result = services.getList(params);
            return ResponseEntity.ok(result.orElse(Collections.emptyList()));
        } catch (ParameterException e) {
            log.error("Invalid parameters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(e.getMessage()));
        }
    }
    @GetMapping("/{accnId}/{entity}")
    public ResponseEntity<?> getEntity(@PathVariable String accnId, @PathVariable String entity, @RequestParam Map<String, String> params) {
        String serviceName = apiGatewayService.get(entity);
        try {
            validatePrincipalAndAccnId(accnId);
            if (serviceName == null) {
                throw new Exception("Internal server error");
            }
            ApiGatewayService<?> services = (ApiGatewayService<?>) applicationContext.getBean(serviceName);
            Optional<Object> result = services.getListByAccnId(accnId, params);
            return ResponseEntity.ok(result.orElse(Collections.emptyList()));
        } catch (ProcessingException e) {
            log.error("ProcessingException: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        } catch (PermissionException e) {
            log.error("PermissionException: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        } catch (ParameterException e) {
            log.error("Invalid parameters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    @PostMapping(value = "/{accnId}/{entity}")
    public ResponseEntity<?> postEntity(@PathVariable String accnId, @PathVariable String entity, @RequestBody String reqBody) {
        try {
            validatePrincipalAndAccnId(accnId);
            String serviceName = apiGatewayService.get(entity);
            if (serviceName == null) {
                log.warn("Service not found for entity: {}", entity);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new MessageResponse("Internal server error"));
            }
            Object bean = applicationContext.getBean(serviceName);
            ApiGatewayService<?> gatewayService = (ApiGatewayService<?>) bean;
            String dtoClassName = entityDTOs.get(entity);
            if (dtoClassName == null) {
                log.warn("DTO class not configured for entity: {}", entity);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("DTO class not configured for entity: " + entity);
            }
            Class<?> entityClass = Class.forName(dtoClassName);
            Object dto = objectMapper.readValue(reqBody, entityClass);
            Object obj = gatewayService.create(accnId, dto);
            return ResponseEntity.ok(obj);
        } catch (ProcessingException e) {
            log.error("ProcessingException: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        } catch (PermissionException e) {
            log.error("PermissionException: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        } catch (ParameterException e) {
            log.error("Invalid parameters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (ValidationException e) {
            log.error("Validation error: {} ", e.getValidationErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getValidationErrors());
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/{accnId}/job/{jobid}/assign")
    public ResponseEntity<Object> assignJob(@PathVariable String accnId, @RequestBody AssignToDriver dto, @PathVariable String jobid) {
        log.debug("Request received to assign job with Job ID: {} and Account ID: {}", jobid, accnId);
        try {
            validatePrincipalAndAccnId(accnId);
            Job assignedJob = jobServices.assignJobToDriver(dto, jobid);
            return ResponseEntity.ok().body(assignedJob);
        } catch (ProcessingException e) {
            log.error("ProcessingException: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        } catch (PermissionException e) {
            log.error("PermissionException: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        } catch (ParameterException e) {
            log.error("Invalid parameters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    @DeleteMapping("/{accnId}/job/{jobid}")
    public ResponseEntity<Object> rejectedJob(@PathVariable String accnId, @PathVariable String jobid,
            @RequestParam(value = "rejectionReason") String rejectionReason) {
        log.debug("Request to reject job with ID: {} for account: {}", jobid, accnId);
        try {
            validatePrincipalAndAccnId(accnId);
            Principal principal = ckSession.getPrincipal();
            String accnType = principal.getCoreAccn().getTMstAccnType().getAtypId();
            Job job = null;
            switch (accnType) {
                case ACC_TYPE_TO:
                    job = jobServices.rejectedOrCancel(jobid, JobActions.REJECT, rejectionReason, principal);
                    break;
                case ACC_TYPE_CO:
                case ACC_TYPE_FF:
                    job = jobServices.rejectedOrCancel(jobid, JobActions.DELETE, rejectionReason, principal);
                    break;
                default:
                    log.debug("Invalid account type: {}", accnType);
                    throw new ParameterException("Unauthorized account type for action");
            }
            return ResponseEntity.ok().body(job);
        } catch (ProcessingException e) {
            log.error("ProcessingException: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        } catch (PermissionException e) {
            log.error("PermissionException: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        } catch (ParameterException e) {
            log.error("Invalid parameters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    public Map<String, String> getApiGatewayService() {
        return apiGatewayService;
    }

    public void setApiGatewayService(Map<String, String> apiGatewayService) {
        this.apiGatewayService = apiGatewayService;
    }

    public HashMap<String, String> getEntityDTOs() {
        return entityDTOs;
    }

    public void setEntityDTOs(HashMap<String, String> entityDTOs) {
        this.entityDTOs = entityDTOs;
    }
}
