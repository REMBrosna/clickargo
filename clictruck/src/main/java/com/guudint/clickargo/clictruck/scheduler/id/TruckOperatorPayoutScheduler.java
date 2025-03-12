package com.guudint.clickargo.clictruck.scheduler.id;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.finacing.dto.CkCtToPayment;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToPayment;
import com.guudint.clickargo.clictruck.finacing.service.ITruckOperatorPayoutService;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

@Component
@EnableAsync
@EnableScheduling
public class TruckOperatorPayoutScheduler extends AbstractClickTruckScheduler {

	private static Logger LOG = Logger.getLogger(TruckOperatorPayoutScheduler.class);

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	@Autowired
	private ITruckOperatorPayoutService toPayoutService;

	public TruckOperatorPayoutScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	// Executes from 2pm-8pm
	@Override
//	@Scheduled(cron = "0 */5 * * * *")
//	@Scheduled(cron = "0 0 14,15,16,17,18,19,20 * * ?")
	@Scheduled(cron = "0 0 14,18,22 * * ?") // in prod
	public void doJob() throws Exception {

		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			if (!super.isIndonesia()) {
				LOG.info("Do not run TruckOperatorPayoutScheduler because not ID country.");
				return;
			}

			LOG.info("TruckOperatorPayoutScheduler running in " + InetAddress.getLocalHost().getHostName());

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());

			this.executeFundsTransfer();

			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					"SUCCESS");

		} catch (Exception e) {
			LOG.error("TruckOperatorPayoutScheduler", e);

			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));

			// throw e;

		} finally {
			super.logTask(coreScheduleJoblog);
		}
	}

	private void executeFundsTransfer() throws ParameterException, Exception {

		Date now = Calendar.getInstance().getTime();
		// Fetch records that match the following criteria: DT_TRANSFER = NOW; status =
		// N
		List<TCkCtToPayment> listForPayout = toPayoutService.getRecordsForPayout(now,
				CkCtToPayment.Status.NEW.getCode());
		// CkCtToPayment.Status.NEW.getCode(), CkCtToPayment.Status.FAILED.getCode());
		if (listForPayout != null && listForPayout.size() > 0) {
			// Do funds transfer
			for (TCkCtToPayment entity : listForPayout) {

				// Check if one of the accounts of the jobs under TOP_REFERENCE is suspended.
				List<String> suspendedAccns = toPayoutService.checkJobsPayoutSuspendedAccount(entity);

				if (null != suspendedAccns && !suspendedAccns.isEmpty()) {
					// if the lists is not empty, just log
					// TODO where to audit?
					LOG.info("Cancelled for entity: " + entity.getTopId());
					String strAccnsSuspended = StringUtils.join(suspendedAccns, ",");
					entity.setTopException("Accounts from transaction " + entity.getTopReference()
							+ " is/are suspended: " + strAccnsSuspended);
					toPayoutService.updateFundsTransferResult(false, entity, now);

				} else {
					toPayoutService.executeFundsTransfer(entity);
				}

				// clicPay is busy. sleep 10 seconds.
				Thread.sleep(10 * 1000);
			}
		}
	}

	protected String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam == null)
			throw new EntityNotFoundException("sysParam " + key + " not configured");

		return sysParam.getSysVal();

	}

}
