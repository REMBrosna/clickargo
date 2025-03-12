package com.guudint.clickargo.clictruck.master.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.dao.CkCtMstReimbursementTypeDao;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstReimbursementType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstReimbursementType;
import com.guudint.clickargo.clictruck.master.service.MasterService;

@Transactional(readOnly = true)
public class CkCtMstReimbursementTypeServiceImpl implements MasterService<CkCtMstReimbursementType> {

    private static Logger LOG = Logger.getLogger(CkCtMstReimbursementTypeServiceImpl.class);

    @Autowired
    private CkCtMstReimbursementTypeDao ckCtMstReimbursementTypeDao;

    @Override
    public List<CkCtMstReimbursementType> listAll() {
        List<CkCtMstReimbursementType> ckCtMstReimbursementTypes = new ArrayList<>();
        try {
            for (TCkCtMstReimbursementType tCkCtMstReimbursementType : ckCtMstReimbursementTypeDao.getAll()) {
                ckCtMstReimbursementTypes.add(new CkCtMstReimbursementType(tCkCtMstReimbursementType));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return ckCtMstReimbursementTypes;
    }

    @Override
    public List<CkCtMstReimbursementType> listByStatus(Character status) {
        List<CkCtMstReimbursementType> ckCtMstReimbursementTypes = new ArrayList<>();
        try {
            for (TCkCtMstReimbursementType tCkCtMstReimbursementType : ckCtMstReimbursementTypeDao
                    .findByRbtyStatus(status)) {
                ckCtMstReimbursementTypes.add(new CkCtMstReimbursementType(tCkCtMstReimbursementType));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return ckCtMstReimbursementTypes;
    }

}
