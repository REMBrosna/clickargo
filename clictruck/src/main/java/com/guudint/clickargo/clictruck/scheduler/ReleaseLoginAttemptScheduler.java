package com.guudint.clickargo.clictruck.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@EnableScheduling
@EnableAsync
public class ReleaseLoginAttemptScheduler extends AbstractClickTruckScheduler {

    private static final Logger log = Logger.getLogger(ReleaseLoginAttemptScheduler.class);

    @Autowired
    @Qualifier("coreUserDao")
    private GenericDao<TCoreUsr, String> coreUserDao;

    public ReleaseLoginAttemptScheduler() {
        super.setTaskName(this.getClass().getSimpleName());
    }

    // @Scheduled(fixedRate = 3000) // Testing
    @Scheduled(cron = "0 0/15 * * * ?") //To run a task every 15 minutes
    @Transactional
    @Override
    @SchedulerLock(name = "ReleaseLoginAttemptScheduler", lockAtLeastFor = "60s")
    public void doJob() throws Exception {

		if (!super.isSingapore()) {
			log.info("Do dot run ReleaseLoginAttemptScheduler because not SG country.");
			return;
		}
		
        LockAssert.assertLocked();
        String taskNo = super.getTaskNo();
        CoreScheduleJoblog coreScheduleJoblog = null;
        try {
            coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
            log.debug("ReleaseLoginAttemptScheduler Started: " + Calendar.getInstance().getTime().toString());
            Map<String, Object> param = new HashMap<>();
            param.put("usrStatus", 'S');
            param.put("usrLoginInvcnt", 4);
            String sql = "SELECT o FROM TCoreUsr o WHERE o.usrStatus = :usrStatus AND o.usrLoginInvcnt >= :usrLoginInvcnt";
            List<TCoreUsr> tCoreUsrs = coreUserDao.getByQuery(sql, param);
            if (tCoreUsrs != null && !tCoreUsrs.isEmpty()) {
                for (TCoreUsr user: tCoreUsrs){
                    user.setUsrStatus(RecordStatus.ACTIVE.getCode());
                    user.setUsrLoginInvcnt(0);
                    coreUserDao.update(user);
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