package com.guudint.clickargo.clictruck.planexec.job.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTerm;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtJobTermDao extends GenericDao<TCkCtJobTerm, String>{

    public List<TCkCtJobTerm> findByReqId(String jtrId) throws Exception;
}
