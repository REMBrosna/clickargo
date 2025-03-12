package com.guudint.clickargo.clictruck.master.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.master.constant.CkCtMstReimbursementTypeConstant;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstReimbursementTypeDao;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstReimbursementType;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtMstReimbursementTypeDaoImpl extends GenericDaoImpl<TCkCtMstReimbursementType, String>
        implements CkCtMstReimbursementTypeDao {

    @Override
    public List<TCkCtMstReimbursementType> findByRbtyStatus(Character status) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstReimbursementType.class);
        criteria.add(Restrictions.eq(CkCtMstReimbursementTypeConstant.PropertyName.RBTYP_STATUS, status));
        return getByCriteria(criteria);
    }

}
