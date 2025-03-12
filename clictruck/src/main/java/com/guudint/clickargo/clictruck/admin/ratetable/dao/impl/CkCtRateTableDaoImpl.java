package com.guudint.clickargo.clictruck.admin.ratetable.dao.impl;

import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.admin.ratetable.constant.CkCtRateConstant;
import com.guudint.clickargo.clictruck.admin.ratetable.dao.CkCtRateTableDao;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtRateTableDaoImpl extends GenericDaoImpl<TCkCtRateTable, String> implements CkCtRateTableDao {

    @Override
    public Optional<TCkCtRateTable> findByNameAndCompany(String name, String companyId) throws Exception{
        TCoreAccn tCoreAccn = new TCoreAccn();
        tCoreAccn.setAccnId(companyId);
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtRateTable.class);
        criteria.add(Restrictions.eq(CkCtRateConstant.PropertyName.RT_NAME, name));
        criteria.add(Restrictions.eq(CkCtRateConstant.PropertyName.RT_COMPANY, tCoreAccn));
        return Optional.ofNullable(getOne(criteria));
    }

	@Override
	public Optional<TCkCtRateTable> findById(String id) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtRateTable.class);
        criteria.add(Restrictions.eq(CkCtRateConstant.PropertyName.RT_ID, id));
        return Optional.ofNullable(getOne(criteria));
	}

}
