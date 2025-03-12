package com.guudint.clickargo.clictruck.planexec.job.event.listener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;

import com.guudint.clickargo.clictruck.planexec.job.event.TruckJobStateChangeEvent;
import com.vcc.camelone.common.exception.COException;
import com.vcc.camelone.common.exception.ErrorCodes;
import com.vcc.camelone.common.exception.ParameterException;

/**
 * This is for job related event listener.
 */
public class ClictruckJobEventListener implements ApplicationListener<TruckJobStateChangeEvent> {

	// Static Attributes
	/////////////////////
	private static Logger log = Logger.getLogger(ClictruckJobEventListener.class);

	@Autowired
	@Qualifier("truckJobEventListenerService")
	private IJobPostEventListenerService jobEventListener;

	@Override
	public void onApplicationEvent(TruckJobStateChangeEvent event) {
		log.debug("onApplicationEvent");
		try {

			if (event == null)
				throw new ParameterException("param event null");

			if (event.getActions() == null)
				throw new ParameterException("param action null");

			if (event.getDto() == null)
				throw new ParameterException("param dto null");

			if (event.getPrincipal() == null)
				throw new ParameterException("param principal null");

			switch (event.getActions()) {
			case SUBMIT:
				jobEventListener.processSubmit(event.getDto(), event.getPrincipal());
				break;
			case ACCEPT:
				jobEventListener.processAccepted(event.getDto(), event.getPrincipal());
				break;
			case ASSIGN:
				jobEventListener.processAssigned(event.getDto(), event.getPrincipal());
				break;
			case REJECT:
				jobEventListener.processRejected(event.getDto(), event.getPrincipal());
				break;
			case START:
				jobEventListener.processStarted(event.getDto(), event.getPrincipal());
				break;
			case STOP:
				jobEventListener.processDelivered(event.getDto(), event.getPrincipal());
				break;
			case BILLJOB:
				jobEventListener.proccessBilled(event.getDto(), event.getPrincipal());
				break;
			case VERIFY_BILL:
				jobEventListener.processBillVerified(event.getDto(), event.getPrincipal());
				break;
			case ACKNOWLEDGE_BILL:
				jobEventListener.processBillAcknowledged(event.getDto(), event.getPrincipal());
				break;
			case APPROVE_BILL:
				jobEventListener.processBillApproved(event.getDto(), event.getPrincipal());
				break;
			case REJECT_BILL:
				jobEventListener.processBillRejected(event.getDto(), event.getPrincipal());
				break;
			case PAID:
				jobEventListener.processInPaid(event.getDto(), event.getPrincipal());
				break;
			default:
				break;
			}

		} catch (Exception ex) {
			log.error("onApplicationEvent", ex);
			COException.create(COException.ERROR, ErrorCodes.ERR_GEN_UNKNOWN, ErrorCodes.MSG_GEN_UNKNOWN,
					"CkEventListener", ex);
		}

	}

}
