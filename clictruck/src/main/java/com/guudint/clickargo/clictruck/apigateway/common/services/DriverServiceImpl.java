package com.guudint.clickargo.clictruck.apigateway.common.services;

import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.apigateway.common.AbstractApiGatewayService;
import com.guudint.clickargo.clictruck.apigateway.dto.Driver;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ProcessingException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
@Service
public class DriverServiceImpl extends AbstractApiGatewayService<Driver> {
    private static Logger log = Logger.getLogger(DriverServiceImpl.class);

    @Autowired
    @Qualifier("ckCtDrvDao")
    private GenericDao<TCkCtDrv, String> ckCtDrvDao;

    @Override
    public Optional<Object> getListByAccnId(String accnId, Map<String, String> params) throws Exception {
        log.info("getListByAccnId");
        List<Driver> drvList = new ArrayList<>();
        List<TCkCtDrv> driverList = getDriverList(accnId);
        for (TCkCtDrv entity: driverList){
            Driver drv = new Driver();
            drv.setId(entity.getDrvId());
            drv.setCompanyId(accnId);
            drv.setName(entity.getDrvName());
            drv.setLicenseNo(entity.getDrvLicenseNo());
            drv.setEmail(entity.getDrvEmail());
            drv.setMobileNo(entity.getDrvPhone());
            drvList.add(drv);
        }
        return Optional.of(drvList);
    }
    private List<TCkCtDrv> getDriverList(String accnId) throws Exception {
        log.info("getDriverList");
        Map<String, Object> params = new HashMap<>();
        params.put("accnId", accnId);
        String hql = "SELECT d FROM TCkCtDrv d WHERE d.TCoreAccn.accnId = :accnId AND d.drvStatus = 'A' ORDER BY d.drvId ASC";
        return ckCtDrvDao.getByQuery(hql, params);
    }
}
