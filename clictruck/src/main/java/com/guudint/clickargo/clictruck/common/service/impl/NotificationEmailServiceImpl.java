package com.guudint.clickargo.clictruck.common.service.impl;

import com.guudint.clickargo.clictruck.notification.dto.CkCtAlert;
import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.guudint.clickargo.common.enums.WorkflowTypeEnum;
import com.guudint.clickargo.common.event.MonitoringEvent;
import com.vcc.camelone.ccm.dto.CoreAccn;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

public class NotificationEmailServiceImpl {

	private static final Logger LOG = Logger.getLogger(NotificationEmailServiceImpl.class);

	@Autowired
	private ApplicationEventPublisher eventPublisher;
	public void publishPostEvents(CkCtAlert dto, CoreAccn coreAccn) throws Exception {
		LOG.info("publishPostEvents");

		WorkflowTypeEnum workflowType = null;
		String alertType = dto.getCkCtMstAlert().getAltName();

		switch (alertType) {
			case "Vehicle Maintenance":
				if (dto.getAltConditionType().equalsIgnoreCase("DISTANCE")) {  // Assuming 'O' is a type for a distance-based alert
					workflowType = WorkflowTypeEnum.VEH_DISTANCE;
				} else if (dto.getAltConditionType().equalsIgnoreCase("DAYS_BEFORE")) {  // Assuming 'D' is for date-based alerts
					workflowType = WorkflowTypeEnum.VEH_EXPIRY;
				}
				break;
			case "Driver License Expired":
				workflowType = WorkflowTypeEnum.DRIVER_LICENSE;
				break;
			case "Vehicle Parking Cert Expired":
				workflowType = WorkflowTypeEnum.VPC_EXPIRY;
				break;
			case "Vehicle Insurance Expired":
				workflowType = WorkflowTypeEnum.INSURANCE;
				break;
			default:
				LOG.warn("Unknown Alert Type: " + alertType);
				return; // No event to publish
		}

		if (workflowType != null) {
			ApplicationEvent appEvent = new MonitoringEvent<TCkCtAlert, CkCtAlert>(this, workflowType, dto);
			eventPublisher.publishEvent(appEvent);
		}
	}


//	public void publishPostEvents(CkCtVehExt dto, CoreAccn coreAccn) throws Exception {
//		LOG.info("publishPostEvents");
//
//		WorkflowTypeEnum workflowType = null;
//		switch (dto.getId().getVextParam()) {
//			case "VEHICLE_MAINTENANCE":
//				if (dto.getVextMonitorMthd().equals('O')) {
//					workflowType = WorkflowTypeEnum.VEH_DISTANCE;
//				} else if (dto.getVextMonitorMthd().equals('D')) {
//					workflowType = WorkflowTypeEnum.VEH_EXPIRY;
//				}
//				break;
//			case "EXP_DRIVER_LICENSE":
//				workflowType = WorkflowTypeEnum.DRIVER_LICENSE;
//				break;
//			case "VPC_EXPIRY":
//				workflowType = WorkflowTypeEnum.VPC_EXPIRY;
//				break;
//			case "INSURANCE":
//				workflowType = WorkflowTypeEnum.INSURANCE;
//				break;
//			default:
//				LOG.warn("Unknown VextParam: " + dto.getId().getVextParam());
//				return; // No event to publish
//		}
//
//		if (workflowType != null) {
//			ApplicationEvent appEvent = new MonitoringEvent<TCkCtVehExt, CkCtVehExt>(this, workflowType, dto);
//			eventPublisher.publishEvent(appEvent);
//		}
//	}


}
