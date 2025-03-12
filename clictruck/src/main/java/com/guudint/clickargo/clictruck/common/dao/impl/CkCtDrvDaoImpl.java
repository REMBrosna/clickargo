package com.guudint.clickargo.clictruck.common.dao.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.common.constant.CkCtDrvConstant;
import com.guudint.clickargo.clictruck.common.dao.CkCtDrvDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtDrvDaoImpl extends GenericDaoImpl<TCkCtDrv, String> implements CkCtDrvDao {

	@Override
	public Optional<TCkCtDrv> findByLicenseNo(String licenseNo) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtDrv.class);
		criteria.add(Restrictions.eq(CkCtDrvConstant.PropertyName.DRV_LICENSE_NO, licenseNo));
		return Optional.ofNullable(getOne(criteria));
	}

	@Override
	public Optional<TCkCtDrv> findByEmail(String email) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtDrv.class);
		criteria.add(Restrictions.eq(CkCtDrvConstant.PropertyName.DRV_EMAIL, email));
		return Optional.ofNullable(getOne(criteria));
	}

	@Override
	public TCkCtDrv findByMobileUserId(String drvMobileId) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtDrv.class);
		criteria.add(Restrictions.eq(CkCtDrvConstant.PropertyName.DRV_MOBILE_ID, drvMobileId));
		criteria.add(Restrictions.eq(CkCtDrvConstant.PropertyName.DRV_STATUS, RecordStatus.ACTIVE.getCode()));
		return getOne(criteria);
	}
	
	@Override
	public TCkCtDrv findByMobileUserId(String drvMobileId, List<Character> status) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtDrv.class);
		criteria.add(Restrictions.eq(CkCtDrvConstant.PropertyName.DRV_MOBILE_ID, drvMobileId));
		criteria.add(Restrictions.in(CkCtDrvConstant.PropertyName.DRV_STATUS, status));
		return getOne(criteria);
	}

	@Override
	public boolean isPasswordChanged(String mobilePwd) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtDrv.class);
		criteria.add(Restrictions.eq(CkCtDrvConstant.PropertyName.DRV_MOBILE_PASSWORD, mobilePwd));
		// Should also search inactive, for editing password for records with status 'I'
		criteria.add(Restrictions.in(CkCtDrvConstant.PropertyName.DRV_STATUS, Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.INACTIVE.getCode())));
		//if something is returned, it means password in db and the arguments matched
		return getOne(criteria) != null ? false : true;
	}

	@Override
	public TCkCtDrv findByDriverNameAccnId(String accnId, String drvName) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtDrv.class);
		criteria.add(Restrictions.eq("TCoreAccn.accnId", accnId));
		criteria.add(Restrictions.eq(CkCtDrvConstant.PropertyName.DRV_NAME, drvName));
		criteria.add(Restrictions.eq(CkCtDrvConstant.PropertyName.DRV_STATUS, RecordStatus.ACTIVE.getCode()));
		return getOne(criteria);
	}
}
