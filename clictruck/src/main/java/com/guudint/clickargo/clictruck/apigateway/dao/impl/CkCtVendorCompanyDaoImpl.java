package com.guudint.clickargo.clictruck.apigateway.dao.impl;

import com.guudint.clickargo.clictruck.apigateway.dao.CkCtVendorCompanyDao;
import com.guudint.clickargo.clictruck.apigateway.model.TCkCtVendorCompany;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CkCtVendorCompanyDaoImpl extends GenericDaoImpl<TCkCtVendorCompany, String> implements CkCtVendorCompanyDao {
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor={Exception.class})
    public TCkCtVendorCompany hasVendorAuthorization(String accnId, String vendorId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtVendorCompany.class);
        criteria.add(Restrictions.eq("venVendorAccn.accnId", vendorId));
        criteria.add(Restrictions.eq("venCompanyAccn.accnId", accnId));
        criteria.add(Restrictions.eq("venStatus", RecordStatus.ACTIVE.getCode()));
        return getOne(criteria);
    }

}
