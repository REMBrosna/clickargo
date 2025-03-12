package com.guudint.clickargo.clictruck.planexec.job.event.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.admin.service.ClicTruckAccnService;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.TCoreUsrRole;
import com.vcc.camelone.cac.model.TCoreUsrRoleId;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreContact;
import com.vcc.camelone.ccm.dto.CoreUsr;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.ccm.model.embed.TCoreContact;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.core.dto.CoreApps;
import com.vcc.camelone.core.model.TCoreApps;

public abstract class AbstractJobPostEventListenerService implements IJobPostEventListenerService {

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	@Qualifier("ccmUserService")
	protected IEntityService<TCoreUsr, String, CoreUsr> userService;

	@Autowired
	@Qualifier("ccmAccnService")
	protected IEntityService<TCoreAccn, String, CoreAccn> ccmAccnService;

	@Autowired
	protected GenericDao<TCoreUsrRole, TCoreUsrRoleId> coreUsrRoleDao;

	@Autowired
	@Qualifier("ccmAppsService")
	protected IEntityService<TCoreApps, String, CoreApps> ccmAppsService;

	@Autowired
	protected ClicTruckAccnService ckAccnService;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	protected CoreApps getCoreApps(ServiceTypes serviceType) throws Exception {
		if (serviceType == null)
			throw new ParameterException("param serviceType null");

		return ccmAppsService.findById(serviceType.getAppsCode());
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CoreUsr getUser(String usrUid) throws Exception {
		if (StringUtils.isBlank(usrUid))
			throw new ParameterException("param usrUid null or empty");
		return userService.findById(usrUid);
	}

	/**
	 * Retrieve valid recipients by account. This will fetch the company email
	 * first, if there is not company email set, then it will fetch from individual
	 * users under the account.
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected List<String> getRecipients(String accnId, Roles roleType) throws Exception {
		if (StringUtils.isBlank(accnId))
			throw new ParameterException("param accnId null or empty");

		List<String> emailsList = new ArrayList<>();
		CoreAccn accn = ccmAccnService.findById(accnId);
		if (accn != null) {
			Optional<CoreContact> opAccnContact = Optional.ofNullable(accn.getAccnContact());
			if (opAccnContact.isPresent() && StringUtils.isNotBlank(opAccnContact.get().getContactEmail())) {
				emailsList.add(opAccnContact.get().getContactEmail());
			} else {
				// If account level don't have email set and roleType is set, get the users
				// (specified roles) and add in the recipients.
				if (roleType != null) {
					String hql = "from TCoreUsrRole o where o.TCoreUsr.TCoreAccn.accnId=:accnId and o.TCoreUsr.usrStatus=:status and o.id.urolRoleid=:roleTypeId and o.id.urolAppscode=:appsCode";
					Map<String, Object> params = new HashMap<>();
					params.put("accnId", accnId);
					params.put("status", RecordStatus.ACTIVE.getCode());
					params.put("roleTypeId", roleType.name());
					params.put("appsCode", ServiceTypes.CLICTRUCK.getAppsCode());

					List<TCoreUsrRole> usrList = coreUsrRoleDao.getByQuery(hql, params);
					if (usrList != null && usrList.size() > 0) {
						for (TCoreUsrRole usr : usrList) {
							Hibernate.initialize(usr.getTCoreUsr());
							Hibernate.initialize(usr.getTCoreUsr().getUsrContact());
							Optional<TCoreContact> opUsrContact = Optional
									.ofNullable(usr.getTCoreUsr().getUsrContact());
							if (opUsrContact.isPresent()
									&& StringUtils.isNotBlank(opUsrContact.get().getContactEmail())) {
								if (!emailsList.contains(opUsrContact.get().getContactEmail()))
									emailsList.add(opUsrContact.get().getContactEmail());
							}

						}
					}
				}

			}
		}

		return emailsList;
	}

	protected String getSysParam(String key, String defValue) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam == null)
			return defValue;

		return sysParam.getSysVal();

	}
}
