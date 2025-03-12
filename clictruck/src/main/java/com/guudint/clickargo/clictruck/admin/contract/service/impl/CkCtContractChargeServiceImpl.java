package com.guudint.clickargo.clictruck.admin.contract.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractChargeDao;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.contract.service.CkCtContractChargeService;

@Service
public class CkCtContractChargeServiceImpl implements CkCtContractChargeService{

    @Autowired
    private CkCtContractChargeDao ckCtContractChargeDao;

    private static Logger LOG = Logger.getLogger(CkCtContractChargeServiceImpl.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void update(CkCtContractCharge ckCtContractCharge) {
        TCkCtContractCharge tCkCtContractCharge = new TCkCtContractCharge(ckCtContractCharge);
        try {
            ckCtContractChargeDao.update(tCkCtContractCharge);
        } catch (Exception e) {
            LOG.error("Error update contract charge", e);
        }
    }
    
}
