package com.guudint.clickargo.clictruck.common.service;

import com.vcc.camelone.common.exception.ValidationException;

/**
 * Service implementation for download/upload template. 
 */
public interface ITemplateService {

	/**
	 * Generates the template upon request. 
	 */
	public byte[] download() throws Exception;
	
	/**
	 * Process the content of the template.
	 */
	public void upload(byte[] data) throws ValidationException, Exception;
}
