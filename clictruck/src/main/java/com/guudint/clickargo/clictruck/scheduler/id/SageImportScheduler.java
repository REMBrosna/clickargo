package com.guudint.clickargo.clictruck.scheduler.id;

import java.net.InetAddress;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.sage.export.excel.service.SageImportExcelService;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import com.vcc.camelone.util.email.SysParam;

@Component
@EnableAsync
public class SageImportScheduler extends AbstractSageExportScheduler {

	private static Logger LOG = Logger.getLogger(SageImportScheduler.class);

	// Attributes
	/////////////////
	@Autowired
	protected SysParam sysParam;
	
	@Autowired
	private SageImportExcelService sageImportExcelService;

	public SageImportScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	@Override
	@Scheduled(cron = "0 */5 * * * ?") // second, minute, hour, day, month, year // per 5 minutes;
	public void doJob() throws Exception {

		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		
		try {

			LOG.info("SageScheduler running in " + InetAddress.getLocalHost().getHostName());
			if (!super.isIndonesia()) {
				LOG.info("Skipping SageImportScheduler because the country is not Indonesia.");
				coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
						"SUCCESS");
				return;
			}
			LOG.info("Task starting: " + taskNo);
			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());

			sageImportExcelService.process();
			LOG.info("Task completed: " + taskNo + " with SageImportExcelService");
			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					"SUCCESS");
		} catch (Exception e) {
			LOG.error("TruckOperatorPayoutScheduler", e);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));
		} finally {
			LOG.info("Logging task state for taskNo: " + taskNo + " with state: " + coreScheduleJoblog);
			super.logTask(coreScheduleJoblog);
		}
	}
}
