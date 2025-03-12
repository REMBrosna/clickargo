package com.guudint.clickargo.clictruck.opm.service;

import java.math.BigDecimal;
import java.util.List;

import com.guudint.clickargo.clictruck.opm.dto.CkOpmJournal;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public interface IOpmService {

	/**
	 * Calculates the total charges of the truck job trips and calls
	 * {@code clicCredit.reserveCredit}.
	 * 
	 * @throws Exception - if anything goes wrong it will throw exception and will
	 *                   not proceed to change the job status to Submitted.
	 */
	public void reserveOpmJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, BigDecimal amount,
			Principal principal) throws Exception;

	/**
	 * Reverses the credit reservation of the specified job truck.
	 */
	public void reverseOpmJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal)
			throws Exception;


	/**
	 * Utilizes the total credit (total trip charges + total reimbursements).
	 */

	public void utilizeOpmJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception;;

	public void convertResever2utilizeOpmJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck) throws Exception;


	/**
	 * Release the utilized credit by calling {@code payCredit}.
	 */

	public void reverseOpmUtilized(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal)
			throws Exception;;

	int countByAnd(CkOpmJournal dto)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception;

	List<CkOpmJournal> filterBy(EntityFilterRequest paramEntityFilterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception;

}
