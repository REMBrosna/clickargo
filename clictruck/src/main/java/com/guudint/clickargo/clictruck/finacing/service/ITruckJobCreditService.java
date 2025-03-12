package com.guudint.clickargo.clictruck.finacing.service;

import java.math.BigDecimal;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.vcc.camelone.cac.model.Principal;

/**
 * ClicCredit integration services. This serves as the placeholder for the
 * {@code clicpay} integration in clictruck.
 */
public interface ITruckJobCreditService {

	/**
	 * Calculates the total charges of the truck job trips and calls
	 * {@code clicCredit.reserveCredit}.
	 * 
	 * @throws Exception - if anything goes wrong it will throw exception and will
	 *                   not proceed to change the job status to Submitted.
	 */
	public void reserveJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, BigDecimal amount, Principal principal)
			throws Exception;

	/**
	 * Reverses the credit reservation of the specified job truck.
	 */
	public void reverseJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal) throws Exception;

	/**
	 * Utilizes the total credit (total trip charges + total reimbursements).
	 */
	public void utilizeJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal) throws Exception;

	/**
	 * Release the utilized credit by calling {@code payCredit}. 
	 */
	public void reverseUtilized(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal) throws Exception;
}
