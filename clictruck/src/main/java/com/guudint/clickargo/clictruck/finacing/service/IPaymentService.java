package com.guudint.clickargo.clictruck.finacing.service;

import java.util.List;
import java.util.Map;

import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentDetails;
import com.guudint.clickargo.clictruck.finacing.service.impl.TruckPaymentService.PayoutRequest;
import com.guudint.clickargo.payment.dto.CkPaymentTxn;
import com.guudint.clickargo.payment.dto.PaymentCallbackResponse;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ValidationException;

public interface IPaymentService {

	public static enum CkPaymentTypes {
		INBOUND, OUTBOUND
	}

	public static enum InvoiceTypes {
		DEBIT_NOTE, PLATFORM_FEE
	}

	public static final String KEY_CLICTRUCK_VAT_PERCENTAGE = "CLICTRUCK_VAT_PERCENTAGE";
	public static final String KEY_CLICTRUCK_STAMP_DUTY_LIMIT = "CLICTRUCK_STAMP_DUTY_LIMIT";
	public static final String KEY_CLICTRUCK_STAMP_DUTY_VALUE = "CLICTRUCK_STAMP_DUTY_VALUE";
	public static final String KEY_CLICTRUCK_DEFAULT_CURRENCY = "CLICTRUCK_DEFAULT_CURRENCY";
	public static final String KEY_CLICTRUCK_DEFAULT_PAYTERMS_TO = "CLICTRUCK_DEFAULT_PAYTERMS_TO";
	public static final String KEY_CLICTRUCK_STAMP_DUTY_ON = "CLICTRUCK_STAMP_DUTY_ON";

	public JobPaymentDetails getPaymentDetails(String reqBody, boolean isSubmit)
			throws ParameterException, EntityNotFoundException, ValidationException, Exception;

	/**
	 * Executes the inbound pay based on the request body which consists of list of
	 * jobIds. This is for CO/FF to pay to GLI.
	 */
	public void executeInboundPay(String reqBody) throws ParameterException, EntityNotFoundException, Exception;

	/**
	 * Executes the inbound pay based on the request body which consists of list of
	 * jobIds. This is for GLI to pay to TO (Trucking Owner).
	 */
	public void executeOutboundPay(String reqBody) throws ParameterException, EntityNotFoundException, Exception;

	/**
	 * Executes the payment cancelation. Transaction will be Inactivated, call
	 * {@code clicpay.cancellation} request. Revert the inbound/outbound payment
	 * state of the job truck.
	 */
	public void executeCancelPay(String txnId, CkPaymentTypes ckPaymentType)
			throws ParameterException, EntityNotFoundException, Exception;

	public void executeTerminatePay(String txnId, List<String> terminalJobIdlist) throws Exception;

	public void executeRevertCancelPay(String txnId, CkPaymentTypes ckPaymentType)
			throws ParameterException, EntityNotFoundException, Exception;

	/**
	 * Executed when callback is triggered from {@code clicpay}.
	 */
	public PaymentCallbackResponse executeCallBack(String reqBody)
			throws ParameterException, EntityNotFoundException, Exception;

	public Map<String, String> downloadTempInvoicePdf(Map<String, Object> pdfDetails)
			throws ParameterException, EntityNotFoundException, Exception;

	public CkPaymentTxn payoutAction(String ptxId, PayoutRequest request)
			throws ParameterException, EntityNotFoundException, Exception;

}
