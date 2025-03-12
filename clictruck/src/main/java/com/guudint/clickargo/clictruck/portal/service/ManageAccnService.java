package com.guudint.clickargo.clictruck.portal.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.dto.ChangePassword;
import com.guudint.clickargo.admin.event.ClickargoPostUserUpdateEvent;
import com.guudint.clickargo.admin.event.ClickargoPostUserUpdateEvent.PostUserUpdateAction;
import com.vcc.camelone.cac.dto.CoreRole;
import com.vcc.camelone.cac.dto.CoreRoleId;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.cac.model.TCoreRole;
import com.vcc.camelone.cac.model.TCoreRoleId;
import com.vcc.camelone.cac.model.TCoreUsrRole;
import com.vcc.camelone.cac.model.TCoreUsrRoleId;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.can.service.INotify;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreContact;
import com.vcc.camelone.ccm.dto.CoreUsr;
import com.vcc.camelone.ccm.dto.PortalAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.ccm.model.TCoreUsrReset;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.attach.dto.CoreAttach;
import com.vcc.camelone.common.attach.model.TCoreAttach;
import com.vcc.camelone.common.attach.service.IPortalAttachService;
import com.vcc.camelone.common.attach.service.impl.AttachmentServiceImpl;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityFilterResponse;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityOrderBy.ORDERED;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.COException;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ErrorCodes;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.constant.COConstant;
import com.vcc.camelone.constant.SysParamConstant;
import com.vcc.camelone.core.dto.CoreApps;
import com.vcc.camelone.core.model.TCoreApps;
import com.vcc.camelone.master.controller.PathNotFoundException;
import com.vcc.camelone.util.PrincipalUtilService;
import com.vcc.camelone.util.crypto.PasswordEncryptor;
import com.vcc.camelone.util.crypto.PasswordGenerator;
import com.vcc.camelone.util.email.SysParam;

import io.jsonwebtoken.lang.Collections;

/**
 * 
 * Do not use this one. Use CkAccnServiceImpl
 *
 */
@Service
@Deprecated
public class ManageAccnService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ManageAccnService.class);
	public static final String appCode = "CONE";
	public static final String OPERATOR_ADMIN = "RAEO_OPERATOR_ADMIN";
	public static final String ACCN_CREATION = "SCT_NT_03_ACCN_REG_APPROVE";
	public static final String USER_CREATION = "SCT_NT_05_CREATE_FIRST_USER";

	public static enum UserAction {
		ACTIVATE("activate"), RESET_PASSWORD("resetPassword");

		String action;

		UserAction(String action) {
			this.action = action;
		}

		public String getAction() {
			return this.action;
		}
	}

	@Autowired
	@Qualifier("ccmUserService")
	private IEntityService<TCoreUsr, String, CoreUsr> ccmUserService;

	@Autowired
	@Qualifier("ccmAccnService")
	private IEntityService<TCoreAccn, String, CoreAccn> ccmAccnService;

	@Autowired
	@Qualifier("attachService")
	private IEntityService<TCoreAttach, String, CoreAttach> attachService;

	@Autowired
	@Qualifier("portalAttachService")
	private IPortalAttachService portalAttachService;

	@Autowired
	// @Qualifier("coreRoleDao")
	private GenericDao<TCoreRole, TCoreRoleId> coreRoleDao;

	@Autowired
	// @Qualifier("coreUsrRoleDao")
	private GenericDao<TCoreUsrRole, TCoreUsrRoleId> coreUsrRoleDao;

	@Autowired
	@Qualifier("coreAppsDao")
	private GenericDao<TCoreApps, String> coreAppsDao;

	@Autowired
	@Qualifier("coreUserDao")
	private GenericDao<TCoreUsr, String> coreUserDao;

	@Autowired
	@Qualifier("coreAccDao")
	private GenericDao<TCoreAccn, String> coreAccDao;

	@Autowired
	@Qualifier("coreUsrResetDao")
	private GenericDao<TCoreUsrReset, String> coreUsrResetDao;

	@Autowired
	protected PrincipalUtilService principalUtilService;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	@Autowired
	private INotify notificationService;

	@Autowired
	protected SysParam sysParam;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public PortalAccn createNewAccn(PortalAccn portalAccn) throws Exception {
		log.debug("createNewAccn");
		try {

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new Exception("principal is null");

			if (StringUtils.isEmpty(principal.getAppsCode()))
				throw new Exception("principal appsCode is null or empty");

			if (isExistingUserEmail(portalAccn.getAccnDetails().getAccnContact().getContactEmail())) {
				throw new Exception("Email Already Exists in System");
			}

			if (isExistingAccnID(portalAccn.getAccnDetails().getAccnId())) {
				throw new Exception("Account ID Already Exists in System");
			}
			CoreAccn accnDtls = portalAccn.getAccnDetails();
			accnDtls.setAccnDtReg(Calendar.getInstance().getTime());
			accnDtls.setAccnId(accnDtls.getAccnId().toUpperCase());

			CoreAccn accnDetails = ccmAccnService.add(accnDtls, principal);
			portalAccn.setAccnDetails(accnDetails);

			createOrUpdateAccnTabs(portalAccn, principal);
		} catch (Exception e) {
			log.error("createNewAccn", e);
			throw e;
		}
		return portalAccn;

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public PortalAccn updateAccn(String accnId, PortalAccn portalAccn)
			throws ParameterException, ProcessingException, EntityNotFoundException {
		log.debug("updateAccn");
		try {
			Principal principal = principalUtilService.getPrincipal();
			if (null == principal)
				throw new ParameterException("param principal null");
			if (StringUtils.isEmpty(accnId))
				throw new ParameterException("param accnId null");
			if (null == portalAccn)
				throw new ParameterException("param portalAccn null");

			CoreAccn dbAccn = ccmAccnService.findById(accnId);
			if (null == dbAccn)
				throw new EntityNotFoundException("account not found");

			CoreAccn accnDetails = ccmAccnService.update(portalAccn.getAccnDetails(), principal);
			portalAccn.setAccnDetails(accnDetails);

			createOrUpdateAccnTabs(portalAccn, principal);
			sendCreateAccnNotification(accnDetails);

		} catch (ParameterException | EntityNotFoundException ex) {
			log.error("getAccn", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("getAccn", ex);
			throw new ProcessingException(ex);
		}

		return portalAccn;
	}

	private void createOrUpdateAccnTabs(PortalAccn portalAccn, Principal principal) throws Exception {
		log.debug("createOrUpdateAccnTabs");
		try {
			if (portalAccn.getAccnLogo() != null) {
				if (portalAccn.getAccnLogo().getAttId() != null && !portalAccn.getAccnLogo().getAttId().isEmpty()) {
					CoreAttach attLogo = attachService.update(portalAccn.getAccnLogo(), principal);
					portalAccn.setAccnLogo(attLogo);
				} else {
					initCoreAttach(portalAccn.getAccnLogo(), portalAccn.getAccnDetails(),
							AttachmentServiceImpl.ATTACH_TYPE_ACCN_LOGO, principal);
					portalAttachService.createAttach(portalAccn.getAccnLogo(), principal);
				}
			}

			if (portalAccn.getAccnStamp() != null) {
				if (portalAccn.getAccnStamp().getAttId() != null && !portalAccn.getAccnStamp().getAttId().isEmpty()) {
					CoreAttach attStamp = attachService.update(portalAccn.getAccnStamp(), principal);
					portalAccn.setAccnStamp(attStamp);
				} else {
					initCoreAttach(portalAccn.getAccnStamp(), portalAccn.getAccnDetails(),
							AttachmentServiceImpl.ATTACH_TYPE_ACCN_STAMP, principal);
					portalAttachService.createAttach(portalAccn.getAccnStamp(), principal);
				}
			}

			List<CoreAttach> opAccnDocs = portalAccn.getAccnDocs();
			if (portalAccn.getAccnDocs() != null) {
				for (CoreAttach opAccnDoc : opAccnDocs) {
					if (opAccnDoc.getAttId() != null && !opAccnDoc.getAttId().isEmpty()) {
						CoreAttach attDoc = attachService.update(opAccnDoc, principal);
						opAccnDoc = attDoc;
					} else {
						initCoreAttach(opAccnDoc, portalAccn.getAccnDetails(),
								AttachmentServiceImpl.ATTCH_TYPE_REFID_DOC, principal);
						portalAttachService.createAttach(opAccnDoc, principal);
					}
				}
			}

		} catch (Exception e) {
			log.error("createOrUpdateAccnTabs", e);
			throw e;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void createAccnAdmin(CoreAccn accnDtls) throws Exception {
		log.debug("createAccnAdmin");
		try {
			Principal principal = principalUtilService.getPrincipal();
			if (null == principal)
				throw new ParameterException("param principal null");
			if (null == accnDtls)
				throw new ParameterException("param accnDtls null");

			CoreUsr coreUsr = new CoreUsr();
			coreUsr.setTCoreAccn(accnDtls);
			coreUsr.setUsrUid(accnDtls.getAccnId() + "0001");
			String pswd = PasswordGenerator.generatePassword(8);
			String pswdEncrypt = PasswordEncryptor.encrypt(coreUsr.getUsrUid(), pswd);
			coreUsr.setUsrPwd(pswdEncrypt);
			coreUsr.setUsrContact(accnDtls.getAccnContact());
			coreUsr.setUsrAddr(accnDtls.getAccnAddr());
			coreUsr.setUsrName(accnDtls.getAccnName());
			coreUsr.setUsrPassNid(accnDtls.getAccnCoyRegn());

			initUser(coreUsr, principal);
			ccmUserService.add(coreUsr, principal);

			String appsCodeByPrincipal = getCoreAppsCode(principal);
			// 4: Delete all roles;
			this.deleteUserRole(coreUsr.getUsrUid(), appsCodeByPrincipal);

			// 5 add new user role default OPERATOR_ADMIN;
			if (accnDtls.getTMstAccnType().getAtypId().equalsIgnoreCase("ACC_TYPE_REVENUE_AUTHORITY")) {
				this.addUsrRole(coreUsr.getUsrUid(), appsCodeByPrincipal, "RA_OFFICER_ADMIN");
			} else if (accnDtls.getTMstAccnType().getAtypId().equalsIgnoreCase("ACC_TYPE_PORT_AUTHORITY")) {
				this.addUsrRole(coreUsr.getUsrUid(), appsCodeByPrincipal, "PA_OFFICER_ADMIN");
			} else if (accnDtls.getTMstAccnType().getAtypId().equalsIgnoreCase("ACC_TYPE_RAEO_REGULATORY")) {
				this.addUsrRole(coreUsr.getUsrUid(), appsCodeByPrincipal, "RAEO_REGULATORY_AGENCY_ADMIN");
			} else {
				this.addUsrRole(coreUsr.getUsrUid(), appsCodeByPrincipal, OPERATOR_ADMIN);
			}
			// 6 notiification
			this.sendCreateAdminNotification(coreUsr, coreUsr.getUsrUid(), pswd);

		} catch (Exception e) {
			log.error("createAccnAdmin", e);
			throw e;
		}
	}

	private void initUser(CoreUsr coreUsr, Principal principal) {
		coreUsr.setUsrStatus('A');
		coreUsr.setUsrDtReg(new Date());
		coreUsr.setUsrDtComm(new Date());
		coreUsr.setUsrDtPwdLupd(new Date());
		coreUsr.setUsrMboxId("Default");
		coreUsr.setUsrTypeMbox("N");
		coreUsr.setUsrTypeOnline("Y");
		coreUsr.setUsrUidCreate(principal.getUserId());
		coreUsr.setUsrDtCreate(new Date());
		coreUsr.setUsrPwdForce("Y");
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Optional<Object> getEntitiesByProxy(Map<String, String> params)
			throws ParameterException, PathNotFoundException, ProcessingException {
		log.debug("getEntitiesProxy");

		try {

			if (Collections.isEmpty(params))
				throw new ParameterException("param params null or empty");

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null) {
				throw new ProcessingException("principal is null");
			}

			EntityFilterRequest filterRequest = new EntityFilterRequest();
			// start and length parameter extraction
			filterRequest.setDisplayStart(
					params.containsKey("iDisplayStart") ? Integer.valueOf(params.get("iDisplayStart")).intValue() : -1);
			filterRequest.setDisplayLength(
					params.containsKey("iDisplayLength") ? Integer.valueOf(params.get("iDisplayLength")).intValue()
							: -1);
			// where parameters extraction
			ArrayList<EntityWhere> whereList = new ArrayList<>();
			List<String> searches = params.keySet().stream().filter(x -> x.contains("sSearch_"))
					.collect(Collectors.toList());
			for (int nIndex = 1; nIndex <= searches.size(); nIndex++) {
				String searchParam = params.get("sSearch_" + String.valueOf(nIndex));
				String valueParam = params.get("mDataProp_" + String.valueOf(nIndex));
				log.info("searchParam: " + searchParam + " valueParam: " + valueParam);
				whereList.add(new EntityWhere(valueParam, searchParam));
			}

			// add in the account ID in whereList based on the principal
			whereList.add(new EntityWhere("TCoreAccn.accnId", principal.getUserAccnId()));

			filterRequest.setWhereList(whereList);
			// order by parameters extraction
			Optional<String> opSortAttribute = Optional.ofNullable(params.get("mDataProp_0"));
			Optional<String> opSortOrder = Optional.ofNullable(params.get("sSortDir_0"));
			if (opSortAttribute.isPresent() && opSortOrder.isPresent()) {
				EntityOrderBy orderBy = new EntityOrderBy();
				orderBy.setAttribute(opSortAttribute.get());
				orderBy.setOrdered(opSortOrder.get().equalsIgnoreCase("desc") ? ORDERED.DESC : ORDERED.ASC);
				filterRequest.setOrderBy(orderBy);
			}

			if (!filterRequest.isValid())
				throw new ProcessingException("Invalid request: " + filterRequest.toJson());

			List<CoreUsr> en = ccmUserService.filterBy(filterRequest);
			List<Object> entities = List.class.cast(en);
			EntityFilterResponse filterResponse = new EntityFilterResponse();
			filterResponse.setiTotalRecords(entities.size());
			filterResponse.setiTotalDisplayRecords(filterRequest.getTotalRecords());
			filterResponse.setAaData((ArrayList<Object>) entities);

			return Optional.of(filterResponse);
		} catch (ParameterException | PathNotFoundException | ProcessingException ex) {
			log.error("getEntitiesProxy", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("getEntitiesProxy", ex);
			throw new ProcessingException(ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<TCoreRole> findRoles(String roleAppscode) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("id.roleAppscode", roleAppscode);

		List<TCoreRole> roleList = coreRoleDao.getByCriteria(params, null, null, 0, 0);

		return roleList;

	}

	/**
	 * 
	 * @param tCoreRoleList
	 * @return
	 * @throws Exception
	 */
	public List<CoreRole> convertToRoleDTO(List<TCoreRole> tCoreRoleList) throws Exception {

		if (null == tCoreRoleList || tCoreRoleList.size() == 0) {
			return null;
		}

		for (TCoreRole coreRole : tCoreRoleList) {

			Hibernate.initialize(coreRole.getId());
			Hibernate.initialize(coreRole.getTCoreApps());
		}

		List<CoreRole> coreRoleList = tCoreRoleList.stream().map(entity -> {

			CoreRole dto = new CoreRole(entity);
			dto.setId(null == entity.getId() ? null : new CoreRoleId(entity.getId()));
			dto.setTCoreApps(null == entity.getTCoreApps() ? null : new CoreApps(entity.getTCoreApps()));

			return dto;

		}).collect(Collectors.toList());

		return coreRoleList;

	}

	public void forgotPassword(String email) throws Exception {
		TCoreUsr tCoreUsr = getUserByEmail(email);
		if (tCoreUsr == null)
			throw new EntityNotFoundException("user with email does not exist. please try again");

		CoreUsr coreUsr = new CoreUsr();
		BeanUtils.copyProperties(tCoreUsr, coreUsr);
		coreUsr.setUsrContact(new CoreContact(tCoreUsr.getUsrContact()));

		String token = PasswordEncryptor.encrypt(
				coreUsr.getUsrUid().concat(":").concat(coreUsr.getUsrContact().getContactEmail()),
				PasswordGenerator.generatePassword(16));
		TCoreUsrReset tCoreUsrReset = new TCoreUsrReset();
		tCoreUsrReset.setUsrId(coreUsr.getUsrUid());
		tCoreUsrReset.setUsrEmail(coreUsr.getUsrContact().getContactEmail());
		tCoreUsrReset.setUsrToken(token);
		tCoreUsrReset.setUsrExpiredDate(DateUtils.addDays(new Date(), 1));
		tCoreUsrReset.setUsrStatus('A');
		tCoreUsrReset.setUsrDtCreate(Calendar.getInstance().getTime());
		tCoreUsrReset.setUsrUidCreate(coreUsr.getUsrUid());
		tCoreUsrReset.setUsrDtLupd(Calendar.getInstance().getTime());
		tCoreUsrReset.setUsrUidLupd(Constant.DEFAULT_USR);
		coreUsrResetDao.saveOrUpdate(tCoreUsrReset);

		// publish event
		eventPublisher.publishEvent(
				new ClickargoPostUserUpdateEvent(this, coreUsr, false, PostUserUpdateAction.RESET_PWD, token));
	}

	public void changePassword(ChangePassword dto) throws Exception {
		if (StringUtils.isBlank(dto.getNewPassword()))
			throw new ParameterException("param newPassword is null");

		if (dto.getNewPassword().length() < 8)
			throw new ParameterException("New Password length should be more than or equal to 8 characters");

		if (StringUtils.isBlank(dto.getConfirmPassword()))
			throw new ParameterException("param confirmPassword is null");

		if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
			throw new ParameterException("Passwords do not match");

		if (dto.isFromManageUser()) {
			if (StringUtils.isBlank(dto.getCurrentPassword()))
				throw new ParameterException("param currentPassword is null");

			if (StringUtils.isBlank(dto.getUserId()))
				throw new ParameterException("param userId is null");

			Principal principal = principalUtilService.getPrincipal();

			CoreUsr coreUsr = ccmUserService.findById(dto.getUserId());
			if (coreUsr == null)
				throw new EntityNotFoundException("user does not exist");

			String currentEncryptPwd = PasswordEncryptor.encrypt(coreUsr.getUsrUid(), dto.getCurrentPassword());
			if (!coreUsr.getUsrPwd().equals(currentEncryptPwd))
				throw new ParameterException("Current password do not match");

			// Set user new password
			coreUsr.setUsrPwd(PasswordEncryptor.encrypt(coreUsr.getUsrUid(), dto.getNewPassword()));
			coreUsr.setUsrDtPwdLupd(new Date());
			ccmUserService.update(coreUsr, principal);
		} else {
			if (StringUtils.isBlank(dto.getToken()))
				throw new ParameterException("query param key is null");

			TCoreUsrReset tCoreUsrReset = getUserResetByToken(dto.getToken());
			if (tCoreUsrReset == null)
				throw new EntityNotFoundException("Sorry, this change password link is not valid.");

			if (tCoreUsrReset.getUsrStatus().equals('E'))
				throw new EntityNotFoundException("Sorry, this change password link is expired.");

			if (new Date().after(tCoreUsrReset.getUsrExpiredDate())) {
				tCoreUsrReset.setUsrStatus('E');
				tCoreUsrReset.update(tCoreUsrReset);
				throw new EntityNotFoundException("Sorry, this change password link is expired.");
			}

			TCoreUsr tCoreUsr = getUserByEmail(tCoreUsrReset.getUsrEmail());
			if (tCoreUsr == null)
				throw new EntityNotFoundException("user with email does not exist");

			// Updated record to inactive after password changed
			tCoreUsrReset.setUsrStatus('I');
			coreUsrResetDao.update(tCoreUsrReset);

			// Set user new password
			tCoreUsr.setUsrPwd(PasswordEncryptor.encrypt(tCoreUsr.getUsrUid(), dto.getNewPassword()));
			tCoreUsr.setUsrDtPwdLupd(new Date());
			coreUserDao.update(tCoreUsr);
		}
	}

	/// Helper Methods
	///////////////////////
	private TCoreUsr getUserByEmail(String email) throws EntityNotFoundException {
		try {
			TCoreUsr tCoreUsr = new TCoreUsr();
			Map<String, Object> param = new HashMap<>();
			param.put("email", email);
			String sql = "SELECT o FROM TCoreUsr o WHERE o.usrContact.contactEmail = :email AND o.usrStatus = 'A'";
			List<TCoreUsr> tCoreUsrs = coreUserDao.getByQuery(sql, param);
			if (!tCoreUsrs.isEmpty()) {
				tCoreUsr = tCoreUsrs.get(0);
				return tCoreUsr;
			}
			return null;
		} catch (Exception ex) {
			log.error("getUserByEmail", ex);
			throw new EntityNotFoundException(ex);
		}
	}

	private TCoreUsrReset getUserResetByToken(String token) throws EntityNotFoundException {
		try {
			TCoreUsrReset tCoreUsrReset = new TCoreUsrReset();
			Map<String, Object> param = new HashMap<>();
			param.put("token", token);
			String sql = "SELECT o FROM TCoreUsrReset o WHERE o.usrToken = :token AND o.usrStatus = 'A'";
			List<TCoreUsrReset> tCoreUsrResets = coreUsrResetDao.getByQuery(sql, param);
			if (!tCoreUsrResets.isEmpty()) {
				tCoreUsrReset = tCoreUsrResets.get(0);
				return tCoreUsrReset;
			}
			return null;
		} catch (Exception ex) {
			log.error("getUserByEmail", ex);
			throw new EntityNotFoundException(ex);
		}
	}

	private void initCoreAttach(CoreAttach coreAttach, CoreAccn accn, String fileType, Principal principal) {
		coreAttach.setAttId(System.nanoTime() + "");
		coreAttach.setAttUid(accn.getAccnId());
		coreAttach.setAttType(fileType);
		coreAttach.setAttAppscode(Constant.CONE_APPS_CODE);
		coreAttach.setAttAccnId(accn.getAccnId());
		coreAttach.setAttReferenceid(accn.getAccnId());
		coreAttach.setAttUidCreate(principal.getUserId());
		coreAttach.setAttDtCreate(new Date());
	}

	private void deleteUserRole(String urourolUid, String urolAppscode) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("urourolUid", urourolUid);
		// params.put("urolAppscode", urolAppscode);

		int deleteRst = coreUsrRoleDao.executeUpdate("DELETE from TCoreUsrRole where TCoreUsr.usrUid = :urourolUid ",
				params);
		// coreUsrRoleDao.executeNativeSQL("delete FROM covsew.T_CORE_USR_ROLE where
		// UROL_APPSCODE= '"+urolAppscode + "' and UROL_UID ='"+urourolUid+"'");
		log.info("delete user roles: " + deleteRst);
	}

	private String getCoreAppsCode(Principal principal) throws Exception {
		try {

			if (principal == null)
				throw new Exception("principal is null");

			String hql = "FROM TCoreApps o WHERE o.appsUriBasePath=:appsUriBasePath AND o.appsStatus='A'";
			Map<String, Object> params = new HashMap<>();
			params.put("appsUriBasePath", principal.getContextUrl());
			List<TCoreApps> coreAppsList = coreAppsDao.getByQuery(hql, params);
			if (coreAppsList != null && coreAppsList.size() > 0) {
				// should only return one
				TCoreApps app = coreAppsList.get(0);
				return app.getAppsCode();
			}

		} catch (Exception e) {
			log.error("getCoreAppsCode", e);
			throw e;
		}

		// return default CONE
		return appCode;

	}

	private void addUsrRole(String urourolUid, String urolAppscode, String urolRoleid) throws Exception {
		TCoreUsrRole usrRole = new TCoreUsrRole(new TCoreUsrRoleId(urourolUid, urolAppscode, urolRoleid, 'A'), null,
				null, 'A', new Date(), "Sys", new Date(), "Sys");
		coreUsrRoleDao.saveOrUpdate(usrRole);
	}

	private void sendCreateAccnNotification(CoreAccn accnDetails) {
		NotificationParam param = new NotificationParam();
		try {
			param.setAppsCode(COConstant.DEFAULT_APP);
			param.setTemplateId(ACCN_CREATION);

			ArrayList<String> recipients = new ArrayList<String>();
			if (accnDetails != null && accnDetails.getAccnContact() != null
					&& accnDetails.getAccnContact().getContactEmail() != null) {
				recipients.add(accnDetails.getAccnContact().getContactEmail());
				param.setRecipients(recipients);
			} else {
				throw new Exception("Account Contact Email is null");
			}

			notificationService.notifyAsyn(param);
		} catch (Exception e) {
			log.error("", e);
			COException.create(COException.ERROR, ErrorCodes.ERR_GEN_UNKNOWN, ErrorCodes.MSG_GEN_UNKNOWN,
					"ManageAccnService: sendNotification " + param + " " + ACCN_CREATION, e);
		}
	}

	private void sendCreateAdminNotification(CoreUsr coreUsr, String userId, String pswdDecrypt) {
		NotificationParam param = new NotificationParam();
		try {
			param.setAppsCode(COConstant.DEFAULT_APP);
			param.setTemplateId(USER_CREATION);

			HashMap<String, String> emailContent = new HashMap<>();
			emailContent.put("username", coreUsr.getUsrName());
			emailContent.put("userId", coreUsr.getUsrUid());
			emailContent.put("password", pswdDecrypt);
			String sctLink = sysParam.getValString(SysParamConstant.APP_HOST_URL, null);
			emailContent.put("sctLink", sctLink); // TODO
			param.setContentFeilds(emailContent);

			ArrayList<String> recipients = new ArrayList<String>();
			if (coreUsr.getTCoreAccn() != null && coreUsr.getTCoreAccn().getAccnContact() != null
					&& coreUsr.getTCoreAccn().getAccnContact().getContactEmail() != null) {
				recipients.add(coreUsr.getTCoreAccn().getAccnContact().getContactEmail());
				param.setRecipients(recipients);
			} else {
				throw new Exception("Account Contact Email is null");
			}

			notificationService.notifyAsyn(param);
		} catch (Exception e) {
			log.error("", e);
			COException.create(COException.ERROR, ErrorCodes.ERR_GEN_UNKNOWN, ErrorCodes.MSG_GEN_UNKNOWN,
					"ManageAccnService: sendNotification " + param + " " + ACCN_CREATION, e);
		}
	}

	private boolean isExistingUserEmail(String emailID) {
		boolean exists = false;
		try {

			Map<String, Object> param = new HashMap<>();
			param.put("email", emailID);
			String sql = "SELECT o FROM TCoreUsr o WHERE o.usrContact.contactEmail = :email ";
			List<TCoreUsr> tCoreUsrs = coreUserDao.getByQuery(sql, param);
			if (tCoreUsrs != null && tCoreUsrs.size() > 0) {
				exists = true;
			}
		} catch (Exception e) {
		}
		return exists;
	}

	private boolean isExistingAccnID(String accnID) {
		boolean exists = false;
		try {

			Map<String, Object> param = new HashMap<>();
			param.put("accnID", accnID);
			String sql = "SELECT o FROM TCoreAccn o WHERE o.accnId = :accnID ";
			List<TCoreAccn> tCoreaccn = coreAccDao.getByQuery(sql, param);
			if (tCoreaccn != null && tCoreaccn.size() > 0) {
				exists = true;
			}
		} catch (Exception e) {
		}
		return exists;
	}

}
