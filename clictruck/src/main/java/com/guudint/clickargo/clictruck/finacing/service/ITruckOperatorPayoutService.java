package com.guudint.clickargo.clictruck.finacing.service;

import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.finacing.dto.CkCtToPayment;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToPayment;
import com.guudint.clickargo.payment.dto.CkPaymentTxn;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;

public interface ITruckOperatorPayoutService {

	/**
	 * Creates an record for payout to trucking operator by job.
	 */
	public CkCtToPayment createTruckOperatorPayment(CkPaymentTxn txn, String payload, Principal principal)
			throws ParameterException, EntityNotFoundException, Exception;

	/**
	 * Retrieves the active truck operator payment by job.
	 */
	public CkCtToPayment getTruckOperatorPayment(String paymentTxn)
			throws ParameterException, EntityNotFoundException, Exception;

	/**
	 * Retrieves active payment records for payout.
	 */
	public List<TCkCtToPayment> getRecordsForPayout(Date dtFundTransfer, Character... status)
			throws ParameterException, Exception;

	/**
	 * Updates the active truck operator payment.
	 */
	public TCkCtToPayment executeFundsTransfer(TCkCtToPayment dto)
			throws ParameterException, EntityNotFoundException, Exception;

	/**
	 * Update payment result
	 * 
	 * @param isPaymentSuccessful
	 * @param entity
	 * @throws Exception
	 */
	public void updateFundsTransferResult(boolean isPaymentSuccessful, String tCkCtToPaymentId, Date paymentDate)
			throws Exception;

	/**
	 * Update payment result
	 * 
	 * @param isPaymentSuccessful
	 * @param entity
	 * @throws Exception
	 */
	public void updateFundsTransferResult(boolean isPaymentSuccessful, TCkCtToPayment entity, Date paymentDate)
			throws Exception;
	
	public void updateFundsTransferResultSuccessfulByPaymentTxn(TCkPaymentTxn txn, Date paymentDate) throws Exception;
	/**
	 * Get TCkCtToPayment by accnId
	 * 
	 * @param accnId
	 * @return
	 * @throws Exception
	 */
	public List<TCkCtToPayment> getByAccnId(String accnId) throws Exception;

	/**
	 * Iterates through the T_CK_PAYMENT_TXN jobs associated to the
	 * T_CK_CT_TO_PAYMENT record in process.
	 * 
	 * @return list of account ids that are suspended.
	 */
	public List<String> checkJobsPayoutSuspendedAccount(TCkCtToPayment toPayment) throws Exception;
}
