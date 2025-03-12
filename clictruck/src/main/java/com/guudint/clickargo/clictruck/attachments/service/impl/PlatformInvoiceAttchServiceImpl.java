package com.guudint.clickargo.clictruck.attachments.service.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import com.guudint.clickargo.clictruck.attachments.service.ICtAttachmentService;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public class PlatformInvoiceAttchServiceImpl implements ICtAttachmentService<CkCtPlatformInvoice> {

	// Static Attributes
	///////////////////
	private static Logger log = Logger.getLogger(PlatformInvoiceAttchServiceImpl.class);

	@Autowired
	private CkCtPlatformInvoiceDao ckCtPlatformInvoiceDao;

	@Override
	public String getAttachment(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		log.debug("getAttachment");
		try {
			if (StringUtils.isBlank(dtoId))
				throw new ParameterException("param dtoId null or empty");

			TCkCtPlatformInvoice platformInvoiceE = ckCtPlatformInvoiceDao.find(dtoId);

			if (platformInvoiceE == null)
				throw new EntityNotFoundException("entity not found: " + dtoId);
			if (!StringUtils.isBlank(platformInvoiceE.getInvLoc())) {
				String base64ContentString = Base64Utils.encodeToString(
						IOUtils.toByteArray(Files.newInputStream(Paths.get(platformInvoiceE.getInvLoc()))));

				return base64ContentString;
			}
		} catch (Exception ex) {
			throw ex;
		}

		return null;

	}

	@Override
	public CkCtPlatformInvoice getAttachmentObj(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Map<String, Object> getAttachmentByJobId(String jobId, Principal principal) throws ParameterException {
		if (StringUtils.isBlank(jobId)) {
			throw new ParameterException("param jobId null or empty");
		}
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		String accnType = principal.getCoreAccn().getTMstAccnType().getAtypId();
		Map<String, Object> attachment = new HashMap<>();
		try {
			for (TCkCtPlatformInvoice tCkCtPlatformInvoice : ckCtPlatformInvoiceDao.findByJobId(jobId)) {
				Hibernate.initialize(tCkCtPlatformInvoice.getTCoreAccnByInvTo());
				Hibernate.initialize(tCkCtPlatformInvoice.getTCoreAccnByInvTo().getTMstAccnType());
				if (AccountTypes.ACC_TYPE_SP.getDesc().equals(accnType) && AccountTypes.ACC_TYPE_TO.getDesc()
						.equals(tCkCtPlatformInvoice.getTCoreAccnByInvTo().getTMstAccnType().getAtypId())) {
					if (StringUtils.isNotBlank(tCkCtPlatformInvoice.getInvLoc())) {
						attachment.put("filename", tCkCtPlatformInvoice.getInvName());
						attachment.put("data", FileUtil.toBase64(tCkCtPlatformInvoice.getInvLoc()));
					}
				} else if (tCkCtPlatformInvoice.getTCoreAccnByInvTo().getTMstAccnType().getAtypId().equals(accnType)) {
					if (StringUtils.isNotBlank(tCkCtPlatformInvoice.getInvLoc())) {
						attachment.put("filename", tCkCtPlatformInvoice.getInvName());
						attachment.put("data", FileUtil.toBase64(tCkCtPlatformInvoice.getInvLoc()));
					}
				}
			}
		} catch (Exception e) {
			log.error("Error getAttachmentByJobId", e);
		}
		return attachment;
	}

	@Override
	public Map<String, Object> getAttachment2(String param) throws ParameterException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAttachment2'");
	}

}
