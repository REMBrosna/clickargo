package com.guudint.clickargo.clictruck.scheduler.sg.shell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.admin.shell.dto.response.CardResponseDto;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Component
@EnableScheduling
@EnableAsync
public class ClictruckShellCardsScheduler extends AbstractShellScheduler {

    private static final Logger log = Logger.getLogger(ClictruckShellCardsScheduler.class);
    private static final String ENDPOINT = "/fleetmanagement/v1/card/search";
    protected static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    protected CkCtShellCardServiceImpl ckCtShellCardService;

    public ClictruckShellCardsScheduler() {
        super.setTaskName(this.getClass().getSimpleName());
    }

    //	@Scheduled(fixedRate = 3000) // Testing
    @Scheduled(cron = "0 0 9 * * ?") // Runs daily at 4:00 AM
    @Transactional
    @Override
    @SchedulerLock(name = "ClictruckShellCardsScheduler", lockAtLeastFor = "60s")
    public void doJob() throws Exception {

		if (!super.isSingapore()) {
			log.info("Do dot run ClictruckShellCardsScheduler because not SG country.");
			return;
		}
		
        LockAssert.assertLocked();
        String taskNo = super.getTaskNo();
        CoreScheduleJoblog coreScheduleJoblog = null;
        try {
            coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
            log.debug("ClictruckShellCardsScheduler Started: " + Calendar.getInstance().getTime().toString());

            ResponseEntity<String> response = super.makeApiRequest(ENDPOINT, CtConstant.KEY_CLICTRUCK_SHEL_CREDENTIAL_TYPE, "", false);
            JSONObject cards = new JSONObject(response.getBody());
            JSONArray cardsList = cards.getJSONArray("Cards");
            if (Objects.nonNull(cardsList) && !cardsList.isEmpty()){
                for (int i = 0; i < cardsList.length(); i++) {
                    CardResponseDto card = mapper.readValue(cardsList.getJSONObject(i).toString(), CardResponseDto.class);
                    TCkCtShellCard entity = new TCkCtShellCard();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    Date expiryDate = sdf.parse(card.getExpiryDate());
                    Date now = Calendar.getInstance().getTime();
                    boolean isCardExist = super.checkExistingCard(String.valueOf(card.getCardId()));
                    if (isCardExist) {
                        boolean isExpired = isCardExpired(card.getExpiryDate());
                        if (isExpired){
                            ckCtShellCardService.updateStatus(card.getPan(),STATUS_EXPIRED);
                        }
                    } else {
                        entity.setScId(String.valueOf(card.getPan()));
                        entity.setScDtExpiry(expiryDate);
                        entity.setScUidCreate(Constant.DEFAULT_USR);
                        entity.setScDtCreate(now);
                        entity.setScDtLupd(now);
                        entity.setScUidLupd(Constant.DEFAULT_USR);
                        entity.setScStatus(STATUS_ACTIVE);
                        ckCtShellCardService.saveCard(entity);
                    }
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