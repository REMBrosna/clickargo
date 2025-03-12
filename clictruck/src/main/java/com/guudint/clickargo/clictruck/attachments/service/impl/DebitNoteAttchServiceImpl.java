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
import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteDao;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public class DebitNoteAttchServiceImpl implements ICtAttachmentService<CkCtDebitNote> {

	// Static Attributes
	///////////////////
	private static Logger log = Logger.getLogger(DebitNoteAttchServiceImpl.class);

	@Autowired
	private CkCtDebitNoteDao ckCtDebitNoteDao;

	@Override
	public String getAttachment(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		log.debug("getAttachment");
		try {
			if (StringUtils.isBlank(dtoId))
				throw new ParameterException("param dtoId null or empty");

			TCkCtDebitNote debitNoteE = ckCtDebitNoteDao.find(dtoId);

			if (debitNoteE == null)
				throw new EntityNotFoundException("entity not found: " + dtoId);
			if (!StringUtils.isBlank(debitNoteE.getDnLoc())) {
				String base64ContentString = Base64Utils
						.encodeToString(IOUtils.toByteArray(Files.newInputStream(Paths.get(debitNoteE.getDnLoc()))));

				return base64ContentString;
			}
		} catch (Exception ex) {
			throw ex;
		}

		return null;

	}

	@Override
	public CkCtDebitNote getAttachmentObj(String dtoId)
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
		String accnType = principal.getCoreAccn().getTMstAccnType().getAtypId();
		Map<String, Object> attachment = new HashMap<>();
		try {
			for (TCkCtDebitNote tCkCtDebitNote : ckCtDebitNoteDao.findByJobId(jobId)) {
				Hibernate.initialize(tCkCtDebitNote.getTCoreAccnByDnTo());
				Hibernate.initialize(tCkCtDebitNote.getTCoreAccnByDnTo().getTMstAccnType());
				if (AccountTypes.ACC_TYPE_TO.getDesc().equals(accnType) && AccountTypes.ACC_TYPE_TO.getDesc()
						.equals(tCkCtDebitNote.getTCoreAccnByDnFrom().getTMstAccnType().getAtypId())) {
					if (StringUtils.isNotBlank(tCkCtDebitNote.getDnLoc())) {
						attachment.put("filename", tCkCtDebitNote.getDnNo() + ".pdf");
						attachment.put("data", FileUtil.toBase64(tCkCtDebitNote.getDnLoc()));
					}
				} else if (accnType.equals(tCkCtDebitNote.getTCoreAccnByDnTo().getTMstAccnType().getAtypId())) {
					if (StringUtils.isNotBlank(tCkCtDebitNote.getDnLoc())) {
						attachment.put("filename", tCkCtDebitNote.getDnNo() + ".pdf");
						attachment.put("data", FileUtil.toBase64(tCkCtDebitNote.getDnLoc()));
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
