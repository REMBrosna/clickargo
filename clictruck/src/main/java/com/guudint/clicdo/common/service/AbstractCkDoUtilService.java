package com.guudint.clicdo.common.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clicservice.model.TCkSvcAuth;
import com.guudint.clickargo.clicservice.model.TCkSvcAuthAttach;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.AttachmentTypes;
import com.guudint.clickargo.master.enums.AuthServiceStates;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.config.model.TCoreSysparam;

/**
 * Generic utility service that misc clicdo related can extend to get service
 * authorisation, generate invoice, etc.
 * 
 * Note: Place common methods which you think can use by other services.
 */
public abstract class AbstractCkDoUtilService {

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	@Autowired
	@Qualifier("ckSvcAuthDao")
	protected GenericDao<TCkSvcAuth, String> ckSvcAuthDao;

	@Autowired
	@Qualifier("ckSvcAuthAttachDao")
	protected GenericDao<TCkSvcAuthAttach, String> ckSvcAuthAttachDao;

	@Autowired
	@Qualifier("coreAccDao")
	protected GenericDao<TCoreAccn, String> coreAccDao;
	
	protected SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY");

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam != null) {
			return sysParam.getSysVal();
		}

		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkSvcAuth getSvcAuth(TCoreAccn authorizedAccn, TCoreAccn authorizerAccn, TCoreAccn slAccn)
			throws Exception {
		if (authorizedAccn == null)
			throw new ParameterException("param authorizedAccn is null");
		if (authorizerAccn == null)
			throw new ParameterException("param authorizerAccn is null");
		if (slAccn == null)
			throw new ParameterException("param slAccn is null");

		String hql = "FROM TCkSvcAuth o WHERE o.TCoreAccnBySvauAccnAuthorized.accnId = :authorizedAccn "
				+ " AND o.TCoreAccnBySvauAccnService.accnId=:slAccn "
				+ " AND o.TCoreAccnBySvauAccnAuthorizer.accnId=:authorizerAccn"
				+ " AND o.TCkMstAuthState.austId = :authState AND o.svauStatus = :svauStatus";
		Map<String, Object> params = new HashMap<>();
		params.put("authorizedAccn", authorizedAccn.getAccnId());
		params.put("slAccn", slAccn.getAccnId());
		params.put("authorizerAccn", authorizerAccn.getAccnId());
		params.put("authState", AuthServiceStates.AUTH.name());
		params.put("svauStatus", RecordStatus.ACTIVE.getCode());

		List<TCkSvcAuth> authSvc = ckSvcAuthDao.getByQuery(hql, params);
		if (authSvc != null && authSvc.size() > 0) {
			// expecting only one
			TCkSvcAuth auth = authSvc.get(0);
			Hibernate.initialize(auth.getTCkMstAuthState());
			Hibernate.initialize(auth.getTCoreAccnBySvauAccnAuthorized());
			Hibernate.initialize(auth.getTCoreAccnBySvauAccnAuthorizer());
			Hibernate.initialize(auth.getTCoreAccnBySvauAccnService());
			Hibernate.initialize(auth.getTCoreUsrBySvauUsrAuthorized());
			Hibernate.initialize(auth.getTCoreUsrBySvauUsrAuthorizer());
			return auth;
		}

		return null;
	}

	protected List<TCkSvcAuthAttach> getAuthAttachments(TCkSvcAuth svcAuth, AttachmentTypes attTypes)
			throws ParameterException, Exception {
		if (svcAuth == null)
			throw new ParameterException("param svcAuth is null");
		if (attTypes == null)
			throw new ParameterException("param attTypes is null");

		StringBuilder hql = new StringBuilder("FROM TCkSvcAuthAttach o WHERE o.TCkSvcAuth.svauId=:authId");
		hql.append(" AND o.attStatus=:attStatus AND o.TMstAttType.mattId=:attType");

		Map<String, Object> params = new HashMap<>();
		params.put("authId", svcAuth.getSvauId());
		params.put("attStatus", RecordStatus.ACTIVE.getCode());
		params.put("attType", attTypes.getId());

		List<TCkSvcAuthAttach> listAuthAtt = ckSvcAuthAttachDao.getByQuery(hql.toString(), params);
		return listAuthAtt;
	}

	/**
	 * 
	 */
	protected String copyFile(String srcPath, String targetJobId, String targetFilename)
			throws ParameterException, Exception {
		if (StringUtils.isBlank(srcPath))
			throw new ParameterException("param original null");
		if (StringUtils.isBlank(targetFilename))
			throw new ParameterException("param target null");
		if (StringUtils.isBlank(targetJobId))
			throw new ParameterException("param jobId null");

		String basePath = getSysParam(ICkConstant.KEY_CLICDO_ATTCH_BASE_LOCATION);
		if (StringUtils.isBlank(basePath))
			throw new ProcessingException("basePath is not configured");

		File jobDir = new File(basePath.concat(targetJobId));

		File fileTo = new File(jobDir.getAbsoluteFile(), targetFilename);
		FileUtils.copyFile(new File(srcPath), fileTo);
		return fileTo.getAbsolutePath();
	}

	/**
	 * Saves the specified file to the base location configured in SysParam
	 * 
	 * @param filename
	 * @param data     - byte
	 */
	protected String saveAttachment(String jobId, String filename, byte[] data) throws Exception {
		if (StringUtils.isBlank(filename))
			throw new ParameterException("param filename null or empty");

		if (data == null)
			throw new ParameterException("param data null or empty");

		String basePath = getSysParam(ICkConstant.KEY_CLICDO_ATTCH_BASE_LOCATION);
		if (StringUtils.isBlank(basePath))
			throw new ProcessingException("basePath is not configured");

		Path dir = Paths.get(basePath.concat(jobId));
		if (!Files.exists(dir)) {
			Files.createDirectories(dir);
		}

		File jobDir = new File(basePath.concat(jobId));
		File file = new File(jobDir.getAbsolutePath(), filename);
		FileOutputStream output = new FileOutputStream(file);
		output.write(data);
		output.close();
		return file.getAbsolutePath();
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCoreAccn getAccountDetails(String accnIdStr) throws Exception {
		if (StringUtils.isBlank(accnIdStr))
			throw new ParameterException("param accnIdStr null or empty");

		TCoreAccn accn = coreAccDao.find(accnIdStr);
		if (accn == null)
			throw new ProcessingException("accn not found");

		Hibernate.initialize(accn.getAccnAddr());
		Hibernate.initialize(accn.getAccnContact());
		Hibernate.initialize(accn.getAccnAddr().getAddrCtry());
		return accn;
	}
	
	protected String convertNullToString(String str) {
		if (str == null)
			return "";
		return str;
	}
}
