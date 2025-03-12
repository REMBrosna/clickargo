package com.guudint.clickargo.clictruck.common.service;

import java.util.List;

import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public interface CkCtDrvService {

	public CkCtDrv updateStatus(String id, String status)
            throws Exception;

	public boolean isDriverFree(String id, boolean isMobileJob, List<String> validStates) throws ParameterException, EntityNotFoundException, ProcessingException, Exception;
	
	// In the case of driver, display password only inside the form
	public CkCtDrv findById(String id, Character showPassword) throws ParameterException, EntityNotFoundException, ProcessingException, Exception;
	
	public String resetDriverPassword(String drvId, String action) throws Exception;
	
	public String encryptDriverPwd(String pwd);
}
