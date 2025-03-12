package com.guudint.clickargo.clictruck.scheduler.id.opm;

import java.net.InetAddress;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.opm.service.impl.OpmUtilizeService;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

/**
 * Process XML files when DSV Air/Sea put XML files in SFTP folder.
 *
 */
@Component
@EnableAsync
public class OpmPushUtilize2BankScheduler extends AbstractClickTruckScheduler {

	private static Logger log = Logger.getLogger(OpmPushUtilize2BankScheduler.class);

	@Autowired
	OpmUtilizeService opmUtilizeService;
	
	public OpmPushUtilize2BankScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	@Override
	@Scheduled(cron = "0 35 6 * * ?") // second, minute, hour, day, month, year // 06:35 every morning
	public synchronized void doJob() throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			log.info("OpmPushUtilize2OcbcScheduler running in " + InetAddress.getLocalHost().getHostName());

			if (!super.isIndonesia()) {
				log.info("Not run OpmPushUtilize2BankScheduler because not ID country.");
				return;
			}

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());

			String jobResult = opmUtilizeService.pushUtilizeFile2Bank(null);

			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					jobResult);

		} catch (Exception e) {
			log.error("OpmPushUtilize2OcbcScheduler", e);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));
		} finally {
			super.logTask(coreScheduleJoblog);
		}
	}

}
