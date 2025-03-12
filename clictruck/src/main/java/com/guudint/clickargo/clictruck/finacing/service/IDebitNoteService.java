package com.guudint.clickargo.clictruck.finacing.service;

import java.util.Calendar;
import java.util.List;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote.DebitNoteStates;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNoteItem;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.vcc.camelone.ccm.dto.CoreAccn;

/**
 * Interface for debit note related services.
 */
public interface IDebitNoteService {

	public static final String DN_ITEM_DESC_TRIP_CHARGES = "Total Trip Charges for %s";
	public static final String DN_ITEM_DESC_REIM_CHRAGES = "Total Reimbursement Charges for %s";

	/**
	 * Creates new Debit Note record by job.
	 */
	public CkCtDebitNote createDebitNote(CkJobTruck jobTruck, CkCtContract contract, CoreAccn from, CoreAccn to,
			boolean isApplyStampDuty) throws Exception;
	

	public void computeDebitNoteAmount(TCkCtDebitNote dnEntity, CkJobTruck jobTruck, boolean isApplyStampDuty);
	
	public void afterPaid2TO(CkJobTruck jobTruck, Calendar paidDate) throws Exception;

	/**
	 * Returns the debit note based on truck job, the recipient and state. Since
	 * debit note is generated two for each job. One is from TO to GLI and GLI to
	 * CO/FF. So in case the CO/FF is to pay the DN to GLI, record should be
	 * retrieved from the recipient account.
	 */
	public CkCtDebitNote getDebitNote(CkJobTruck jobTruck, CoreAccn toAccn, DebitNoteStates state) throws Exception;

	/**
	 * Generates the pdf and store in file location. Returns the location of the
	 * file
	 */
	public String generateDebitNotePdf(CkCtDebitNote debitNote, List<CkCtDebitNoteItem> debitNoteItems, boolean isDraft)
			throws Exception;

	public TCkCtDebitNote getByTruckJobIdAndAccn(String truckJobId, String accnId) throws Exception;
	
}
