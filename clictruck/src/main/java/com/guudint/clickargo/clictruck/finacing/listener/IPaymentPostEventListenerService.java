package com.guudint.clickargo.clictruck.finacing.listener;

import com.guudint.clickargo.clictruck.finacing.service.IPaymentService.CkPaymentTypes;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.cac.model.Principal;

public interface IPaymentPostEventListenerService {

	public void processTxnCreated(TCkPaymentTxn txn, CkPaymentTypes paymentType, Principal principal) throws Exception;

	public void processTxnVerified(TCkPaymentTxn txn, CkPaymentTypes paymentType, Principal principal) throws Exception;

	public void processTxnCompleted(TCkPaymentTxn txn, CkPaymentTypes paymentType, Principal principal) throws Exception;

	public void processTxnFailed(TCkPaymentTxn txn, CkPaymentTypes paymentType, Principal principal) throws Exception;

}
