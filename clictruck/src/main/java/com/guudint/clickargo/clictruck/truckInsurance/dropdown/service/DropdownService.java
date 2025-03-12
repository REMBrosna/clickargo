package com.guudint.clickargo.clictruck.truckInsurance.dropdown.service;

import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.notification.model.TCkCtMstAlert;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.util.PrincipalUtilService;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DropdownService {

    private static final Logger log = LoggerFactory.getLogger(DropdownService.class);

    @Autowired
    protected PrincipalUtilService principalUtilService;

    @Autowired
    @Qualifier("ckCtVehDao")
    private GenericDao<TCkCtVeh, String> ckCtVehGenericDao;
    @Autowired
    @Qualifier("ckCtMstAlertDao")
    private GenericDao<TCkCtMstAlert, String> ckCtMstAlertDao;
    @Autowired
    protected SessionFactory sessionFactory;

    public ResponseEntity<Object> getCkCtVehicles() {
        Principal principal = principalUtilService.getPrincipal();
        ServiceStatus serviceStatus = new ServiceStatus();
        try {
            String hql = "SELECT c from TCkCtVeh c WHERE c.TCoreAccn.accnId =:accId AND c.vhStatus = 'A' ";
            Map<String, Object> param = new HashMap<>();
            param.put("accId", principal.getUserAccnId());
            List<TCkCtVeh> list = ckCtVehGenericDao.getByQuery(hql, param);
            if (list == null){
                throw new Exception("getCkCtVehicles");
            }
            List<CkCtVeh> dtoList = new ArrayList<>();
            for (TCkCtVeh obj : list){
                CkCtVeh dto = new CkCtVeh();
                dto.setVhPlateNo(obj.getVhPlateNo());
                dtoList.add(dto);
            }
            return ResponseEntity.ok(dtoList);
        } catch (Exception ex) {
            log.error("getCkCtVehicles", ex);
            serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
            serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
            return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_GATEWAY);
        }
    }
    public ResponseEntity<Object> getAlert(String module) {
        log.debug("getAlert");

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("module", module);
            param.put("status", 'A');
            String query = "SELECT o FROM TCkCtMstAlert o WHERE o.altStatus = :status AND LOWER(o.altModule) = LOWER(:module) ORDER BY o.altDtCreate DESC";
            List<TCkCtMstAlert> list = this.ckCtMstAlertDao.getByQuery(query, param);

            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("No alerts found", HttpStatus.NOT_FOUND);
            }

            // Filter unique alerts based on altName
//            List<TCkCtMstAlert> uniqueAlerts = new ArrayList<>(list.stream()
//                    .collect(Collectors.toMap(
//                            TCkCtMstAlert::getAltName, // Key by altName
//                            alert -> alert,            // Value is the alert itself
//                            (existing, replacement) -> existing)) // In case of duplicates, keep the existing one
//                    .values());

            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            log.error("getAlert", ex);
            ServiceStatus serviceStatus = new ServiceStatus();
            serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
            serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<Object> getAlertType(String module, String alt) {
        log.debug("Fetching alerts for module: {} and alert: {}", module, alt);

        try {
            // Decode parameters if necessary
            String decodedModule = URLDecoder.decode(module, StandardCharsets.UTF_8.toString());
            String decodedAlt = URLDecoder.decode(alt, StandardCharsets.UTF_8.toString());

            Map<String, Object> param = new HashMap<>();
            param.put("module", decodedModule.toLowerCase());
            param.put("altName", decodedAlt);
            param.put("status", 'A');

            String query = "SELECT o FROM TCkCtMstAlert o WHERE o.altStatus = :status AND LOWER(o.altModule) = :module AND LOWER(o.altName) = :altName ORDER BY o.altDtCreate DESC";
            List<TCkCtMstAlert> list = this.ckCtMstAlertDao.getByQuery(query, param);

            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("No alerts found", HttpStatus.NOT_FOUND);
            }

            // Filter unique alerts based on altName
            List<TCkCtMstAlert> uniqueAlerts = new ArrayList<>(list.stream()
                    .collect(Collectors.toMap(
                            TCkCtMstAlert::getAltNotificationType, // Key by altName
                            alert -> alert,            // Value is the alert itself
                            (existing, replacement) -> existing)) // In case of duplicates, keep the existing one
                    .values());

            return ResponseEntity.ok(uniqueAlerts);
        } catch (Exception ex) {
            log.error("Error fetching alerts", ex);
            ServiceStatus serviceStatus = new ServiceStatus();
            serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
            serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<Object> getConditionType(String module, String alt,String altNotificationType) {
        log.debug("Fetching alerts for module: {} and alert: {}", module, alt);

        try {
            // Decode parameters if necessary
            String decodedModule = URLDecoder.decode(module, StandardCharsets.UTF_8.toString());
            String decodedAlt = URLDecoder.decode(alt, StandardCharsets.UTF_8.toString());

            Map<String, Object> param = new HashMap<>();
            param.put("module", decodedModule.toLowerCase());
            param.put("altNotificationType", altNotificationType.toLowerCase());
            param.put("altName", decodedAlt);
            param.put("status", 'A');

            String query = "SELECT o FROM TCkCtMstAlert o WHERE o.altStatus = :status AND LOWER(o.altModule) = :module AND LOWER(o.altName) = :altName AND o.altNotificationType= :altNotificationType ORDER BY o.altDtCreate DESC";
            List<TCkCtMstAlert> list = this.ckCtMstAlertDao.getByQuery(query, param);

            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("No alerts found", HttpStatus.NOT_FOUND);
            }

            // Filter unique alerts based on altName
            List<TCkCtMstAlert> uniqueAlerts = new ArrayList<>(list.stream()
                    .collect(Collectors.toMap(
                            TCkCtMstAlert::getAltConditionType, // Key by altName
                            alert -> alert,            // Value is the alert itself
                            (existing, replacement) -> existing)) // In case of duplicates, keep the existing one
                    .values());

            return ResponseEntity.ok(uniqueAlerts);
        } catch (Exception ex) {
            log.error("Error fetching alerts", ex);
            ServiceStatus serviceStatus = new ServiceStatus();
            serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
            serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
