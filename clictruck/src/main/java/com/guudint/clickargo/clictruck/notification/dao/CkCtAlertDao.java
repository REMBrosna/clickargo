package com.guudint.clickargo.clictruck.notification.dao;

import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.vcc.camelone.common.dao.GenericDao;

import java.util.ArrayList;
import java.util.List;

public interface CkCtAlertDao extends GenericDao<TCkCtAlert, String> {
    void updateStatus(String alertId, char status) throws Exception;
}
