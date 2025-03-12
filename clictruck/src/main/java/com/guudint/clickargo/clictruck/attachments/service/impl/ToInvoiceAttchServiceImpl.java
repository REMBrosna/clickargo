package com.guudint.clickargo.clictruck.attachments.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import com.guudint.clickargo.clictruck.attachments.service.ICtAttachmentService;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtToInvoiceDao;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public class ToInvoiceAttchServiceImpl implements ICtAttachmentService<CkCtToInvoice> {

	// Static Attributes
	///////////////////
	private static Logger log = Logger.getLogger(ToInvoiceAttchServiceImpl.class);

	@Autowired
	private CkCtToInvoiceDao ckCtToInvoiceDao;

	@Override
	public String getAttachment(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		log.debug("getAttachment");
		try {
			if (StringUtils.isBlank(dtoId))
				throw new ParameterException("param dtoId null or empty");

			TCkCtToInvoice toInvoiceE = ckCtToInvoiceDao.find(dtoId);

			if (toInvoiceE == null)
				throw new EntityNotFoundException("entity not found: " + dtoId);
			if (!StringUtils.isBlank(toInvoiceE.getInvLoc())) {
				String base64ContentString = Base64Utils
						.encodeToString(IOUtils.toByteArray(Files.newInputStream(Paths.get(toInvoiceE.getInvLoc()))));

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
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Map<String, Object> getAttachmentByJobId(String jobId, Principal principal) throws ParameterException {
		log.debug("getAttachmentByJobId");
		if (StringUtils.isBlank(jobId)) {
			throw new ParameterException("param jobId null or empty");
		}
		Map<String, Object> attachment = new HashMap<>();
		try {
			List<File> files = new ArrayList<>();
			for (TCkCtToInvoice tCkCtToInvoice : ckCtToInvoiceDao.findByJobId(jobId)) {
				if (StringUtils.isNotBlank(tCkCtToInvoice.getInvLoc())) {
					files.add(new File(tCkCtToInvoice.getInvLoc()));
				}
			}
			String zipFilePath = FileUtil.zipFiles(files, jobId + ".zip");
			attachment.put("filename", FilenameUtils.getName(zipFilePath));
			attachment.put("data", FileUtil.toBase64(zipFilePath));
			FileUtil.delete(zipFilePath);
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
