package com.guudint.clickargo.clictruck.scheduler.sg;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;
import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;
import com.guudint.clickargo.clictruck.dsv.service.impl.DSVProcesAfterDeliverdService;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

/**
 * After DSV Air/Sea jobs are delivered, A: Upload stateMessage.xml to SFTP B:
 * Send ePOD PDF file email to DSV C: Send photo PDF file email to DSV
 * 
 *
 */
@Component
@EnableAsync
public class DSVAsProcessAfterDeliverScheduler extends AbstractClickTruckScheduler {

	private static Logger log = Logger.getLogger(DSVAsProcessAfterDeliverScheduler.class);

	@Autowired
	private DSVProcesAfterDeliverdService procesAfterDeliverdService;
	@Autowired
	private CkCtShipmentDao ckCtShipmentDao;

	public DSVAsProcessAfterDeliverScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	@Override
	@Scheduled(cron = "0 */7 * * * ?") // second, minute, hour, day, month, year // per 5 minutes;
	public synchronized void doJob() throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			log.info("DSVProcesAfterDeliverScheduler running in " + InetAddress.getLocalHost().getHostName());

			if (!super.isSingapore()) {
				log.info("Not run DSVProcesAfterDeliverScheduler because not SG country.");
				return;
			}

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());

			String jobResult = this.doTask();

			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					jobResult);

		} catch (Exception e) {
			log.error("DSVProcesAfterDeliverScheduler", e);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));
		} finally {
			super.logTask(coreScheduleJoblog);
		}
	}

	private String doTask() throws Exception {

		List<TCkCtShipment> shipmentList = ckCtShipmentDao.fetchUnprocessedJob(JobStates.DLV);

		int maxProcessShipment = 20;

		List<String> successList = new ArrayList<>();
		List<String> failList = new ArrayList<>();

		if (null != shipmentList) {

			// only process 20
			shipmentList = shipmentList.stream().limit(maxProcessShipment).collect(Collectors.toList());

			for (TCkCtShipment shipment : shipmentList) {
				// process \
				try {
					log.info("Process shipmentId : " + shipment.getTCkJob().getJobId());

					procesAfterDeliverdService.afterDsvJobIsDelivered(shipment.getShId());
					// success
					successList.add(shipment.getShId());

				} catch (Exception e) {
					// failed
					log.error("Fail to process " + shipment.getShId(), e);
					failList.add(shipment.getShId() + " " + e.getMessage());
				}
			}
		}
		if (failList.size() > 0) {
			throw new Exception(failList.toString() + " ;;; " + successList.toString());
		}
		return successList.size() + " ;;; " + successList.toString();

	}

}
