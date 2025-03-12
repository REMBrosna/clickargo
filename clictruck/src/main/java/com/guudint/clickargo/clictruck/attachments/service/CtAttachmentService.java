package com.guudint.clickargo.clictruck.attachments.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public class CtAttachmentService {

	// Attributes
	/////////////
	private Map<String, String> attachServices;

	@Autowired
	protected ApplicationContext applicationContext;

	public String getAttachment(String key, String dtoId)
			throws ParameterException, ProcessingException, EntityNotFoundException, Exception {
		if (!attachServices.containsKey(key))
			throw new ProcessingException("service not found for " + key);

		if (StringUtils.isBlank(dtoId))
			throw new ParameterException("param dto null");

		Object bean = applicationContext.getBean(attachServices.get(key));
		ICtAttachmentService<?> service = (ICtAttachmentService<?>) bean;

		return service.getAttachment(dtoId);
	}

	public Object getAttachmentsObj(String key, String dtoId)
			throws ParameterException, ProcessingException, EntityNotFoundException, Exception {
		if (!attachServices.containsKey(key))
			throw new ProcessingException("service not found for " + key);

		if (StringUtils.isBlank(dtoId))
			throw new ParameterException("param dto null");

		Object bean = applicationContext.getBean(attachServices.get(key));
		ICtAttachmentService<?> service = (ICtAttachmentService<?>) bean;

		return service.getAttachmentObj(dtoId);
	}

}
