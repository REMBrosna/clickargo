package com.guudint.clickargo.clictruck.common.service.impl;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

import com.guudint.clickargo.clictruck.common.dto.NotificationTemplateName;
import com.guudint.clickargo.clictruck.notification.dao.impl.CkCtAlertDaoImpl;
import com.guudint.clickargo.clictruck.notification.dto.CkCtAlert;
import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.guudint.clickargo.clictruck.notification.service.CkCtAlertService;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.enums.ClickargoNotifTemplates;
import com.vcc.camelone.common.dao.GenericDao;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dao.CkCtVehExtDao;
import com.guudint.clickargo.clictruck.common.service.IMonitorService;
import com.guudint.clickargo.clictruck.planexec.job.service.IJobDeliveryService;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import com.vcc.camelone.scheduler.service.AbstractJob;
@Component
@EnableScheduling
@EnableAsync
public class DriverLicenseScheduler extends AbstractJob implements IMonitorService {

	private static final Logger LOG = Logger.getLogger(DriverLicenseScheduler.class);

	@Autowired
	private CkCtVehExtDao ckCtVehExtDao;

	@Autowired
	private TripMobileService tripMobileService;

	@Autowired
	private IJobDeliveryService jobDeliveryService;

	@Autowired
	private NotificationEmailServiceImpl notificationEmailServiceImpl;

	@Autowired
	private CkCtVehExtServiceImpl ckCtVehExtServiceImpl;
	@Autowired
	private CkCtAlertService ckCtAlertService;
	@Autowired
	private CkCtAlertDaoImpl ckCtAlertDaoImpl;
	@Override
	@Scheduled(cron = "0 59 23 * * ?") // EOD;
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void monitor() throws Exception {
		LOG.debug("monitor");
		LOG.info("Scheduler Monitoring Driver License running in " + InetAddress.getLocalHost().getHostName());
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog;
		try {
			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			LOG.info("Monitoring Driver License Started: " + Calendar.getInstance().getTime().toString());
			try {
				executeMonitoringDriverLicense();
				coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo,
						ServiceStatus.STATUS.COMPLETED.toString(), "SUCCESS");

			} catch (Exception ex) {
				coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, ServiceStatus.STATUS.FAILED.toString(),
						ex.getMessage());
			}

			super.logTask(coreScheduleJoblog);
			LOG.info("Monitoring Driver License Ended: " + Calendar.getInstance().getTime().toString());

		} catch (Exception ex) {
			LOG.error("monitor", ex);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(ex));
			super.logTask(coreScheduleJoblog);
		}

	}
	public void executeMonitoringDriverLicense() throws Exception {
		LOG.info("executeMonitoringDriverLicense");

		// Get the current date formatted
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentDate = new Date();
		List<String> listState = Arrays.asList(NotificationTemplateName.DRIVER_LICENSE_EXPIRED.getDesc(), ClickargoNotifTemplates.DRIVER_LICENSE.getId());
		// Find alerts based on the current date
		List<TCkCtAlert> listAlerts = ckCtVehExtDao.findByAlertCondition(currentDate, listState);

		if (listAlerts != null && !listAlerts.isEmpty()) {
			for (TCkCtAlert tCkCtAlert : listAlerts) {
				CkCtAlert ckCtAlert = ckCtAlertService.dtoFromEntity(tCkCtAlert);

				String notificationType = tCkCtAlert.gettCkCtMstAlert().getAltNotificationType();

				if ("EMAIL".equalsIgnoreCase(notificationType) && tCkCtAlert.gettCoreAccn() != null) {
					String email = tCkCtAlert.gettCoreAccn().getAccnContact().getContactEmail();
					if (email != null) {
						LOG.info("Sending email notification for alert ID: " + tCkCtAlert.getAltId() + " to " + email);
						notificationEmailServiceImpl.publishPostEvents(ckCtAlert, new CoreAccn(tCkCtAlert.gettCoreAccn()));
					}
				}

				if ("WHATSAPP".equalsIgnoreCase(notificationType) && tCkCtAlert.gettCoreAccn() != null) {
					String whatsappNumber = ckCtAlert.getAltRepCon();
					if (whatsappNumber != null) {
						String message = "Monitoring alert ID " + tCkCtAlert.getAltId();
						LOG.info("Sending WhatsApp notification for alert ID: " + tCkCtAlert.getAltId() + " to "
								+ tripMobileService.removeSpecialCharacters(whatsappNumber) + " , message: " + message);

						ArrayList<String> text = new ArrayList<>();
						text.add(tCkCtAlert.gettCoreAccn().getAccnName());
						jobDeliveryService.sendYCloudWhatAppMsg(
								null,
								tripMobileService.removeSpecialCharacters(whatsappNumber),
								text,
								null,
								NotificationTemplateName.DRIVER_LICENSE_EXPIRED.getDesc()
						);
					}
				}
				// Update the status to 'I' after sending the notification
				ckCtAlertDaoImpl.updateStatus(ckCtAlert.getAltId(), RecordStatus.INACTIVE.getCode());
			}
		}
	}
//	public void executeMonitoringDriverLicense() throws Exception {
//		LOG.info("executeMonitoringDriverLicense");
//
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date currentDate = new Date();
//		String formattedDate = dateFormat.format(currentDate);
//
//		List<TCkCtVehExt> listVehExt = ckCtVehExtDao.findByMonitoring(VehExtParamEnum.EXP_DRIVER_LICENSE.name(), 'D', formattedDate);
//		if (listVehExt != null && listVehExt.size() > 0) {
//			for (TCkCtVehExt tCkCtVehExt : listVehExt) {
//				CkCtVehExt ckCtVehExt = ckCtVehExtServiceImpl.dtoFromEntity(tCkCtVehExt);
//
//				if (tCkCtVehExt.getVextNotifyEmail() != null) {
//					LOG.info("Sending email notification to monitoring driver license: "
//							+ tCkCtVehExt.getVextNotifyEmail());
//
//					notificationEmailServiceImpl.publishPostEvents(ckCtVehExt, new CoreAccn(tCkCtVehExt.getTCkCtVeh().getTCoreAccn()));
//				}
//
//				if (tCkCtVehExt.getVextNotifyWhatsapp() != null) {
//					String message = "monitoring driver license " + tCkCtVehExt.getId().getVextId();
//					LOG.info("Sending whatsapp notification to monitoring driver license: "
//							+ tripMobileService.removeSpecialCharacters(tCkCtVehExt.getVextNotifyWhatsapp()) + " , "
//							+ message + " , " + tCkCtVehExt.getId().getVextId());
//
////					jobDeliveryService.sendWhatAppMsg(
////							tripMobileService.removeSpecialCharacters(tCkCtVehExt.getVextNotifyWhatsapp()), message,
////							tCkCtVehExt.getId().getVextId());
//
//					ArrayList<String> text = new ArrayList<>();
//					text.add(tCkCtVehExt.getTCkCtVeh().getTCoreAccn().getAccnName());
//					//send whatApp via YCloud
//					jobDeliveryService.sendYCloudWhatAppMsg(null,
//							tripMobileService.removeSpecialCharacters(tCkCtVehExt.getVextNotifyWhatsapp()), text,
//							null, YCloudTemplateName.DRIVER_LICENSE_EXPIRED.getDesc());
//				}
//			}
//		}
//	}

	@Override
	public void doJob() throws Exception {
		// TODO Auto-generated method stub

	}
}
