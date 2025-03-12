package com.guudint.clickargo.clictruck.finacing.event;

import org.springframework.context.ApplicationEvent;

import com.guudint.clickargo.clictruck.finacing.service.IPaymentService.CkPaymentTypes;
import com.guudint.clickargo.payment.enums.PaymentStates;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.cac.model.Principal;

public class PaymentStateChangeEvent extends ApplicationEvent {

	private static final long serialVersionUID = -166963565593271832L;
	private PaymentStates paymentStates;
	private CkPaymentTypes paymentType;
	private TCkPaymentTxn txn;
	private Principal principal;

	public PaymentStateChangeEvent(Object source, CkPaymentTypes paymentType, PaymentStates paymentStates,
			TCkPaymentTxn txn, Principal principal) {
		super(source);
		this.txn = txn;
		this.paymentType = paymentType;
		this.paymentStates = paymentStates;
		this.principal = principal;

	}

	/**
	 * @return the paymentStates
	 */
	public PaymentStates getPaymentStates() {
		return paymentStates;
	}

	/**
	 * @param paymentStates the paymentStates to set
	 */
	public void setPaymentStates(PaymentStates paymentStates) {
		this.paymentStates = paymentStates;
	}

	/**
	 * @return the txn
	 */
	public TCkPaymentTxn getTxn() {
		return txn;
	}

	/**
	 * @param txn the txn to set
	 */
	public void setTxn(TCkPaymentTxn txn) {
		this.txn = txn;
	}

	/**
	 * @return the principal
	 */
	public Principal getPrincipal() {
		return principal;
	}

	/**
	 * @param principal the principal to set
	 */
	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	/**
	 * @return the paymentType
	 */
	public CkPaymentTypes getPaymentType() {
		return paymentType;
	}

	/**
	 * @param paymentType the paymentType to set
	 */
	public void setPaymentType(CkPaymentTypes paymentType) {
		this.paymentType = paymentType;
	}

}
