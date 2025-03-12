package com.guudint.clickargo.clictruck.finacing.dto;

import com.guudint.clickargo.payment.dto.CkPaymentTxn;

/**
 * Extension of {@code CkPaymentTxn} class to add {@code JobPaymentDetails} for
 * transaction history retrieval.
 */
public class CkCtPaymentTxn extends CkPaymentTxn {

	private static final long serialVersionUID = 5511024561220272128L;
	private JobPaymentDetails jobPaymentDetails;

	public JobPaymentDetails getJobPaymentDetails() {
		return jobPaymentDetails;
	}

	public void setJobPaymentDetails(JobPaymentDetails jobPaymentDetails) {
		this.jobPaymentDetails = jobPaymentDetails;
	}

}
