package com.guudint.clickargo.clictruck.notification.dao.impl;

import com.guudint.clickargo.clictruck.notification.dao.CkCtAlertDao;
import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

public class CkCtAlertDaoImpl extends GenericDaoImpl<TCkCtAlert, String> implements CkCtAlertDao {
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = { Exception.class })
    public void updateStatus(String alertId, char status) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("altId", alertId);
        parameters.put("status", status);
        this.executeUpdate("UPDATE TCkCtAlert o SET o.altStatus = :status WHERE o.altId = :altId", parameters);
    }
}
