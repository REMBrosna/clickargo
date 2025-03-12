package com.guudint.clickargo.clictruck.scheduler.id;

import java.net.InetAddress;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.guudint.clickargo.clictruck.tax.service.TaxInvoiceService;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

@Component
@EnableAsync
@EnableScheduling
public class TaxReportScheduler extends AbstractClickTruckScheduler {

	private static Logger LOG = Logger.getLogger(TaxReportScheduler.class);

	@Autowired
	private TaxInvoiceService taxInvoiceService;

	@Async
	@Scheduled(cron = "0 0 1 * * ?")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void doJob() throws Exception {

		String taskNo = super.getTaskNo();

		CoreScheduleJoblog coreScheduleJoblog = null;

		try {

			LOG.info("SageScheduler running in " + InetAddress.getLocalHost().getHostName());

			if (!super.isIndonesia()) {
				LOG.info("Not run TaxReportScheduler because not ID country.");
				return;
			}

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());

			taxInvoiceService.generateReport();

			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					"SUCCESS");

		} catch (Exception e) {
			LOG.error("TruckOperatorPayoutScheduler", e);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));
		} finally {
			super.logTask(coreScheduleJoblog);
		}

	}

}
