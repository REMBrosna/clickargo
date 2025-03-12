package com.guudint.clickargo.clictruck.attachments.service.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import com.guudint.clickargo.clictruck.attachments.service.ICtAttachmentService;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public class PlatformInvoiceByInvNoAttchServiceImpl implements ICtAttachmentService<CkCtPlatformInvoice> {

	// Static Attributes
	///////////////////
	private static Logger log = Logger.getLogger(PlatformInvoiceAttchServiceImpl.class);

	@Autowired
	private CkCtPlatformInvoiceDao ckCtPlatformInvoiceDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public String getAttachment(String invNo) 
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		log.debug("getAttachment");
		if (StringUtils.isBlank(invNo)) {
			throw new ParameterException("param jobId null or empty");
		}
		
		try {
			List<TCkCtPlatformInvoice> platformInvoiceE = ckCtPlatformInvoiceDao.findByInvoiceNumber(invNo);

			if (platformInvoiceE.isEmpty())
				throw new EntityNotFoundException("entity not found: " + invNo);
			if (!StringUtils.isBlank(platformInvoiceE.get(0).getInvLoc())) {
				String base64ContentString = Base64Utils.encodeToString(
						IOUtils.toByteArray(Files.newInputStream(Paths.get(platformInvoiceE.get(0).getInvLoc()))));

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
		return null;
	}

	@Override
	public Map<String, Object> getAttachment2(String param) throws ParameterException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAttachment2'");
	}

}
