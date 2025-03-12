package com.guudint.clickargo.clictruck.planexec.job.event.listener;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.vcc.camelone.cac.model.Principal;

/**
 * Interface for processing different job events listener pertaining to each
 * actions specified.
 */
public interface IJobPostEventListenerService {

	public void processSubmit(CkJobTruck dto, Principal principal) throws Exception;

	public void processAccepted(CkJobTruck dto, Principal principal) throws Exception;

	public void processAssigned(CkJobTruck dto, Principal principal) throws Exception;

	public void processRejected(CkJobTruck dto, Principal principal) throws Exception;

	public void processStarted(CkJobTruck dto, Principal principal) throws Exception;

	public void processDelivered(CkJobTruck dto, Principal principal) throws Exception;

	public void proccessBilled(CkJobTruck dto, Principal principal) throws Exception;

	public void processBillVerified(CkJobTruck dto, Principal principal) throws Exception;
	
	public void processBillAcknowledged(CkJobTruck dto, Principal principal) throws Exception;

	public void processBillApproved(CkJobTruck dto, Principal principal) throws Exception;

	public void processBillRejected(CkJobTruck dto, Principal principal) throws Exception;

	public void processInPaid(CkJobTruck dto, Principal principal) throws Exception;

	public void processOutPaid(CkJobTruck dto, Principal principal) throws Exception;
}
