package com.guudint.clickargo.clictruck.finacing.listener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;

import com.guudint.clickargo.clictruck.finacing.event.PaymentStateChangeEvent;
import com.vcc.camelone.common.exception.COException;
import com.vcc.camelone.common.exception.ErrorCodes;
import com.vcc.camelone.common.exception.ParameterException;

public class ClictruckPaymentEventListener implements ApplicationListener<PaymentStateChangeEvent> {

	// Static Attributes
	/////////////////////
	private static Logger log = Logger.getLogger(ClictruckPaymentEventListener.class);

	@Autowired
	@Qualifier("paymentListenerService")
	private IPaymentPostEventListenerService paymentListenerService;

	@Override
	public void onApplicationEvent(PaymentStateChangeEvent event) {
		log.debug("onApplicationEvent");

		try {

			if (event == null)
				throw new ParameterException("param event null");

			if (event.getTxn() == null)
				throw new ParameterException("param txn null");

			if (event.getPaymentStates() == null)
				throw new ParameterException("param paymentStates null");

			switch (event.getPaymentStates().getCode()) {
			case "NEW":
				paymentListenerService.processTxnCreated(event.getTxn(), event.getPaymentType(), event.getPrincipal());
				;
				break;
			case "VER_BILL":
				paymentListenerService.processTxnVerified(event.getTxn(), event.getPaymentType(), event.getPrincipal());
				;
				break;
			case "PAID":
				paymentListenerService.processTxnCompleted(event.getTxn(), event.getPaymentType(),
						event.getPrincipal());
				;
				break;
			case "FAILED":
				paymentListenerService.processTxnFailed(event.getTxn(), event.getPaymentType(), event.getPrincipal());
				break;
			default:
				break;
			}

		} catch (Exception ex) {
			log.error("onApplicationEvent", ex);
			COException.create(COException.ERROR, ErrorCodes.ERR_GEN_UNKNOWN, ErrorCodes.MSG_GEN_UNKNOWN,
					"ClictruckPaymentEventListener", ex);
		}
	}

}
