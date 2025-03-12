package com.guudint.clickargo.clictruck.scheduler.id.opm;

import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.opm.service.impl.OpmSftpService;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.master.dao.MstBankDao;
import com.vcc.camelone.master.model.TMstBank;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

/**
 * Process XML files when DSV Air/Sea put XML files in SFTP folder.
 *
 */
@Component
@EnableAsync
public class OpmLoadCsvFileScheduler extends AbstractClickTruckScheduler {

	private static Logger log = Logger.getLogger(OpmLoadCsvFileScheduler.class);

	@Autowired
	OpmSftpService opmSftpService;

	@Autowired
	MstBankDao mstBankDao;

	public OpmLoadCsvFileScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	String CONNECT_TYPE_SFTP = "SFTP";

	@Override
	@Scheduled(cron = "0 17 6 * * ?") // second, minute, hour, day, month, year // per 5 minutes;
	public synchronized void doJob() throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			log.info("OpmLoadCsvFileScheduler running in " + InetAddress.getLocalHost().getHostName());

			if (!super.isIndonesia()) {
				log.info("Not run OpmLoadCsvFileScheduler because not ID country.");
				return;
			}

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());

			// String financer = "OCBC";
			// String jobResult = opmSftpService.scanFromSftp(financer);
			String jobResult = this.loadExcelFromSftp();

			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					jobResult);

		} catch (Exception e) {
			log.error("OpmLoadCsvFileScheduler", e);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));
		} finally {
			super.logTask(coreScheduleJoblog);
		}
	}

	private String loadExcelFromSftp() throws Exception {

		List<TMstBank> bankList = mstBankDao.getAll();

		List<TMstBank> bankSFTPList = bankList.stream()
				.filter(b -> CONNECT_TYPE_SFTP.equalsIgnoreCase(b.getBankConnectionType()))
				.collect(Collectors.toList());

		String jobResult = "";

		for (TMstBank bank : bankSFTPList) {
			try {

				String rst = opmSftpService.scanFromSftp(bank.getBankId());

				jobResult = jobResult + " || " + rst;
			} catch (Exception e) {
				log.error("", e);
				jobResult = jobResult + " || " + e.getMessage();
			}
		}
		return jobResult;
	}

}
