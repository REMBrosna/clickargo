package com.guudint.clickargo.clictruck.scheduler.sg.shell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellKiosk;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellCardServiceImpl;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@EnableAsync
public class ClictruckShellKioskScheduler extends AbstractShellScheduler {

	private static final Logger log = Logger.getLogger(ClictruckShellKioskScheduler.class);
	private static final String ENDPOINT = "/locations/v1/fuel";
	protected static ObjectMapper mapper = new ObjectMapper();
	List<String> ids = new ArrayList<>();

	@Autowired
	protected CkCtShellCardServiceImpl ckCtShellCardService;

	public ClictruckShellKioskScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

//	@Scheduled(fixedRate = 3000) // Testing
	@Scheduled(cron = "0 0 4 * * ?") // Runs daily at 4:00 AM
	@Transactional
	@Override
	@SchedulerLock(name = "ClictruckShellKioskScheduler", lockAtLeastFor = "60s")
	public void doJob() throws Exception {

		if (!super.isSingapore()) {
			log.info("Do dot run ClictruckShellKioskScheduler because not SG country.");
			return;
		}

		LockAssert.assertLocked();
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {
			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			log.debug("ClictruckShellKioskScheduler Started: " + Calendar.getInstance().getTime().toString());
			ResponseEntity<String> response = super.makeApiRequest(ENDPOINT, CtConstant.KEY_CLICTRUCK_SHEL_CREDENTIAL_TYPE, "", true);
			JSONObject kiosk = new JSONObject(response.getBody());
			JSONArray kioskList = kiosk.getJSONArray("data");
			if (Objects.nonNull(kioskList) && !kioskList.isEmpty()){
				for (int i = 0; i < kioskList.length(); i++) {
					JSONObject kioskItem = kioskList.getJSONObject(i);
					String name = kioskItem.optString("name", "");
					String id = kioskItem.optString("id", "");
					ids.add(id);
					TCkCtShellKiosk entity = new TCkCtShellKiosk();
					Date now = Calendar.getInstance().getTime();
					boolean existingKiosk = super.checkExistingKiosk(id);
					entity.setSkId(id);
					entity.setSkName(name);
					entity.setSkUidCreate(Constant.DEFAULT_USR);
					entity.setSkDtCreate(now);
					entity.setSkDtLupd(now);
					entity.setSkUidLupd(Constant.DEFAULT_USR);
					entity.setSkStatus(STATUS_ACTIVE);
					if (existingKiosk) {
						TCkCtShellKiosk existing = super.ckCtShellKioskService.find(id);
						existing.setSkName(name);
						super.ckCtShellKioskService.update(existing);
					} else {
						super.ckCtShellKioskService.save(entity);
					}
				}
				// Remove unfound record from Kiosks
				List<TCkCtShellKiosk> allKiosks = super.ckCtShellKioskService.getAllKiosks();
				if (Objects.nonNull(allKiosks) && !allKiosks.isEmpty()) {
					List<String> unfoundKiosks = allKiosks.stream()
							.map(TCkCtShellKiosk::getSkId)
							.filter(kioskId -> !ids.contains(kioskId))
							.collect(Collectors.toList());
					if (!unfoundKiosks.isEmpty()) {
						super.ckCtShellKioskService.removeUnfoundKiosks(unfoundKiosks);
					}
					ids.clear();
				}
			}
			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(), "SUCCESS");
		} catch (Exception ex) {
			log.error("doJob", ex);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, ServiceStatus.STATUS.FAILED.toString(), ex.getMessage());
			super.logTask(coreScheduleJoblog);
		}
		super.logTask(coreScheduleJoblog);
	}
}