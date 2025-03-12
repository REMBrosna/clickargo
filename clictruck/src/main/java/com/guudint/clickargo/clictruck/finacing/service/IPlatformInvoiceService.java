package com.guudint.clickargo.clictruck.finacing.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractCharge;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice.PlatformInvoiceStates;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoiceItem;
import com.vcc.camelone.ccm.dto.CoreAccn;

/**
 * Interface for platform fee related services. 
 */
public interface IPlatformInvoiceService {

	public static final String PF_ITEM_DESC = "Platform Fee for job %s";
	/**
	 *  Creates new platform fee invoice record by job.
	 */
	public CkCtPlatformInvoice createPlatFormInvoice(CkJobTruck jobTruck, Integer addDate, CkCtContractCharge contractCharge,
			CoreAccn pfInvFrom, CoreAccn pfInvTo) throws Exception;
	
	public TCkCtPlatformInvoice computePlatformFee4COff(CkJobTruck jobTruck) throws Exception ;
	
	public TCkCtPlatformInvoice computePlatformFee4TO(CkJobTruck jobTruck) throws Exception;
	
	public void afterPaid2TO(CkJobTruck jobTruck, Calendar paidDate) throws Exception;

	/**
	 * Returns the platform invoice based on truck job, the recipient and state.
	 * Since platform invoice is generated two for each job. One for CO/FF, the
	 * other is for TO.
	 */
	public CkCtPlatformInvoice getPlatformInvoice(CkJobTruck jobTruck, CoreAccn pfInvTo,
			PlatformInvoiceStates pfInvState) throws Exception;

	/**
	 * Generates the pdf. 
	 */
	public String generatePlatformInvoicePdf(CkCtPlatformInvoice pfInv, List<CkCtPlatformInvoiceItem> pfInvItems, boolean isDraft) throws Exception;
	
	/**
	 * Get a list of platform invoices with due date of current date
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<CkCtPlatformInvoice> getDueInvoicesToDate(Date suspendDate, String... accnTypes) throws Exception;
	
	/**
	 * Get a list of platform invoices by Account ID
	 * @param accnId
	 * @return
	 * @throws Exception
	 */
	public List<CkCtPlatformInvoice> getExpiredInvByAccn(Date suspendDate, String accnId) throws Exception;
	
	/**
	 * 
	 * @param truckJobId
	 * @param accnId
	 * @return
	 * @throws Exception
	 */
	public TCkCtPlatformInvoice getByTruckJobIdAndAccn(String truckJobId, String accnId) throws Exception;
}
