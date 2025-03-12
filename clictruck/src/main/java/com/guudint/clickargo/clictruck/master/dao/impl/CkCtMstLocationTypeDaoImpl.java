package com.guudint.clickargo.clictruck.master.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.master.constant.CkCtMstLocationTypeConstant;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstLocationTypeDao;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstLocationType;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtMstLocationTypeDaoImpl extends GenericDaoImpl<TCkCtMstLocationType, String> implements CkCtMstLocationTypeDao {

    @Override
    public List<TCkCtMstLocationType> findByLctyStatus(Character status) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstLocationType.class);
        criteria.add(Restrictions.eq(CkCtMstLocationTypeConstant.PropertyName.LCTY_STATUS, criteria));
        return getByCriteria(criteria);
    }

}
