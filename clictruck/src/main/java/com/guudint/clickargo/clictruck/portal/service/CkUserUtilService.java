package com.guudint.clickargo.clictruck.portal.service;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dao.CkCtDeptUsrDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtDept;
import com.guudint.clickargo.clictruck.common.model.TCkCtDeptUsr;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreUsr;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;

/***
 * This class is created as utility service for manage account so that existing
 * implementation will not be affected.
 */
@Service
public class CkUserUtilService {

	@Autowired
	private CkCtDeptUsrDao usrDeptDao;

	// This will be use to update the user when it is assigned with a department
	@Autowired
	private GenericDao<TCoreUsr, String> userDao;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public String getUserDepartment(CoreUsr usr) throws ParameterException, Exception {
		if (usr == null)
			throw new ParameterException("param usr null");

		// if usrDept is blank, if not then let the previous value be used, tckctdept
		// service will update this
		// once user is assigned to a department in separate module
		if (StringUtils.isBlank(usr.getUsrDept())) {
			// query from dept user table if the user exist, means it has been assigned with
			// a department
			TCkCtDeptUsr usrDept = usrDeptDao.getUser(usr.getUsrUid());
			if (usrDept != null) {
				Hibernate.initialize(usrDept.getTCkCtDept());
				if (usrDept != null) {
					return usrDept.getTCkCtDept().getDeptName();
				}
			}

		}

		return usr.getUsrDept();
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateUserDepartment(CoreUsr usr, CkCtDept dept, Principal principal)
			throws ParameterException, Exception {

		if (usr == null)
			throw new ParameterException("param usr null");

		if (dept == null)
			throw new ParameterException("param dept null");

		if (principal == null)
			throw new ParameterException("param principal null");

		TCoreUsr usrEntity = userDao.find(usr.getUsrUid());
		if (usrEntity != null) {
			usrEntity.setUsrDept(dept.getDeptName());
			userDao.update(usrEntity);
		}

	}
}
