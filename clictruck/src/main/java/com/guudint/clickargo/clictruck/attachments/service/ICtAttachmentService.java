package com.guudint.clickargo.clictruck.attachments.service;

import java.util.Map;

import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

/**
 * Interface to get the attachments of the specified entity.
 */
public interface ICtAttachmentService<D> {

	/**
	 * Returns the base64 string of the attachment.
	 */
	public String getAttachment(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception;

	/**
	 * Returns the object of the attachment.
	 **/
	public D getAttachmentObj(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception;

	public Map<String, Object> getAttachmentByJobId(String jobId, Principal principal) throws ParameterException;

	public Map<String, Object> getAttachment2(String param) throws ParameterException;
}
