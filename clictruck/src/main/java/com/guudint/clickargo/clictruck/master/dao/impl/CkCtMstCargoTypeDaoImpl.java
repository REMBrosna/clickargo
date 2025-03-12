package com.guudint.clickargo.clictruck.master.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.master.constant.CkCtMstCargoTypeConstant;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstCargoTypeDao;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstCargoType;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtMstCargoTypeDaoImpl extends GenericDaoImpl<TCkCtMstCargoType, String> implements CkCtMstCargoTypeDao{

    @Override
    public List<TCkCtMstCargoType> findByCrtypStatus(Character status) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstCargoType.class);
        criteria.add(Restrictions.eq(CkCtMstCargoTypeConstant.PropertyName.CRTYP_STATUS, status));
        return getByCriteria(criteria);
    }
    
}
