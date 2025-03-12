package com.guudint.clickargo.clictruck.attachments.service.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import com.guudint.clickargo.clictruck.attachments.service.ICtAttachmentService;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public class InvoiceAttchServiceImpl implements ICtAttachmentService<CkCtToInvoice> {

	// Static Attributes
	///////////////////
	private static Logger log = Logger.getLogger(InvoiceAttchServiceImpl.class);

	@Autowired
	private GenericDao<TCkCtToInvoice, String> ckCtToInvoiceDao;

	@Override
	public String getAttachment(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		log.debug("getAttachment");
		try {
			if (StringUtils.isBlank(dtoId))
				throw new ParameterException("param dtoId null or empty");

			TCkCtToInvoice invE = ckCtToInvoiceDao.find(dtoId);
			if (invE == null)
				throw new EntityNotFoundException("entity not found: " + dtoId);
			if (!StringUtils.isBlank(invE.getInvLoc())) {

				String base64ContentString = Base64Utils
						.encodeToString(IOUtils.toByteArray(Files.newInputStream(Paths.get(invE.getInvLoc()))));

				return base64ContentString;
			}
		} catch (Exception ex) {
			throw ex;
		}

		return null;

	}

	@Override
	public CkCtToInvoice getAttachmentObj(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getAttachmentByJobId(String jobId, Principal principal) throws ParameterException {
		return new HashMap<>();
	}

	@Override
	public Map<String, Object> getAttachment2(String param) throws ParameterException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAttachment2'");
	}

}
