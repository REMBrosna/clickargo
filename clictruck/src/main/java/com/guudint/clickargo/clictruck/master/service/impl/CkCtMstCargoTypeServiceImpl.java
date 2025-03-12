package com.guudint.clickargo.clictruck.master.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guudint.clickargo.clictruck.finacing.dto.CkCtToPayment;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.common.RecordStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.dao.CkCtMstCargoTypeDao;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstCargoType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstCargoType;
import com.guudint.clickargo.clictruck.master.service.MasterService;

@Transactional(readOnly = true)
public class CkCtMstCargoTypeServiceImpl implements MasterService<CkCtMstCargoType> {

    private static Logger LOG = Logger.getLogger(CkCtMstCargoTypeServiceImpl.class);

    @Autowired
    private CkCtMstCargoTypeDao ckCtMstCargoTypeDao;

    @Override
    public List<CkCtMstCargoType> listAll() {
        List<CkCtMstCargoType> ckCtMstCargoTypes = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("crtypStatus", RecordStatus.ACTIVE.getCode());
        String sql = "SELECT o FROM TCkCtMstCargoType o WHERE o.crtypStatus = :crtypStatus ORDER BY o.crtypSeq ASC";
        try {
            for (TCkCtMstCargoType tCkCtMstCargoType : ckCtMstCargoTypeDao.getByQuery(sql, params)) {
                ckCtMstCargoTypes.add(new CkCtMstCargoType(tCkCtMstCargoType));
            }
        } catch (Exception e) {
            LOG.error("TCkCtMstCargoType -> listAll: "+e);
        }
        return ckCtMstCargoTypes;
    }

    @Override
    public List<CkCtMstCargoType> listByStatus(Character status) {
        List<CkCtMstCargoType> ckCtMstCargoTypes = new ArrayList<>();
        try {
            for (TCkCtMstCargoType tCkCtMstCargoType : ckCtMstCargoTypeDao.findByCrtypStatus(status)) {
                ckCtMstCargoTypes.add(new CkCtMstCargoType(tCkCtMstCargoType));
            }
        } catch (Exception e) {
            LOG.error("TCkCtMstCargoType -> listByStatus: "+e);
        }
        return ckCtMstCargoTypes;
    }

}
