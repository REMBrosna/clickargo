package com.guudint.clickargo.clictruck.scheduler.sg;

import java.net.InetAddress;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.jobupload.service.SendEpodService;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

/**
 * Send epod file for SG
 *
 */
@Component
@EnableAsync
public class SendEpodScheduler extends AbstractClickTruckScheduler {

	private static Logger log = Logger.getLogger(SendEpodScheduler.class);

	@Autowired
	private SendEpodService sendEpodService;

	public SendEpodScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	@Override
	@Scheduled(cron = "0 */9 * * * ?") // second, minute, hour, day, month, year // per 5 minutes;
	public synchronized void doJob() throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			log.info("SendEpodScheduler running in " + InetAddress.getLocalHost().getHostName());

			if (!super.isSingapore()) {
				log.info("Not run SendEpodScheduler because not SG country.");
				return;
			}

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());

			String jobResult = sendEpodService.sendePodEmail();

			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					jobResult);

		} catch (Exception e) {
			log.error("SendEpodScheduler", e);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));
		} finally {
			super.logTask(coreScheduleJoblog);
		}
	}
}