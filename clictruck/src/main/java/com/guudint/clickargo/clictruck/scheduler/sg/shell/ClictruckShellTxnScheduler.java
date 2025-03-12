package com.guudint.clickargo.clictruck.scheduler.sg.shell;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.admin.shell.dto.response.TransactionDetailsResponseDto;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellBatchWindow;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCardTruck;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellTxn;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.common.CkUtil;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@EnableScheduling
@EnableAsync
public class ClictruckShellTxnScheduler extends AbstractShellScheduler {

    private static final Logger log = Logger.getLogger(ClictruckShellTxnScheduler.class);
    private static final String ENDPOINT = "/fleetmanagement/v1/transaction/pricedtransactions";
    private static final String PREFIX_KEY = "ST";
    protected static ObjectMapper mapper = new ObjectMapper();

    public ClictruckShellTxnScheduler() {
        super.setTaskName(this.getClass().getSimpleName());
    }

//    @Scheduled(fixedRate = 3000) // Testing
    @Scheduled(cron = "0 59 23 * * *") // Runs daily at 11:59 PM
    @Transactional
    @Override
    @SchedulerLock(name = "ClictruckShellTxnScheduler", lockAtLeastFor = "60s")
    public void doJob() throws Exception {

		if (!super.isSingapore()) {
			log.info("Do dot run ClictruckShellTxnScheduler because not SG country.");
			return;
		}

        LockAssert.assertLocked();
        String taskNo = super.getTaskNo();
        CoreScheduleJoblog coreScheduleJoblog = null;
        try {
            coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
            log.debug("ClictruckShellTxnScheduler Started: " + Calendar.getInstance().getTime().toString());
            ResponseEntity<String> response = super.makeApiRequest(ENDPOINT, CtConstant.KEY_CLICTRUCK_SHEL_CREDENTIAL_TYPE, "PRICE_TRANSACTION", false);
            JSONObject kiosk = new JSONObject(response.getBody());
            JSONArray kioskList = kiosk.getJSONArray("Transactions");
            if (Objects.nonNull(kioskList) && !kioskList.isEmpty()){
                for (int i = 0; i < kioskList.length(); i++) {
                    JSONObject kioskItem = kioskList.getJSONObject(i);
                    TCkCtShellTxn existingTxn = super.ckCtShellTxnService.findByTxn(kioskItem.optString("TrnIdentifier", ""));
                    if (Objects.nonNull(existingTxn)) {
                        super.ckCtShellTxnService.update(updateExistingTransaction(kioskItem, existingTxn));
                    } else {
                        TCkCtShellTxn entity = updateEntity(kioskItem);
                        TCkCtShellCard card = super.ckCtShellCardService.find(entity.getTCkCtShellCard().getScId());
                        TCkCtShellCardTruck assignedCard = super.findAssignedTruckCardById(kioskItem.optString("CardPAN", ""));
                        if (Objects.nonNull(card)) {
                            if(Objects.nonNull(assignedCard.getTCoreAccn())){
                                TCoreAccn tCoreAccn = new TCoreAccn();
                                tCoreAccn.setAccnId(assignedCard.getTCoreAccn().getAccnId());
                                entity.setTCoreAccn(tCoreAccn);
                                super.ckCtShellTxnService.save(entity);
                            }
                        }
                    }
                }
                // Batch update transaction daily
                TCkCtShellBatchWindow entity = super.ckCtShellBatchWindowService.find(CtConstant.SHELL_CARD_TXN);
                Date now = Calendar.getInstance().getTime();
                entity.setSbDtWindow(now);
                super.ckCtShellBatchWindowService.update(entity);
            }
            coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(), "SUCCESS");
        } catch (Exception ex) {
            log.error("doJob", ex);
            coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, ServiceStatus.STATUS.FAILED.toString(), ex.getMessage());
            super.logTask(coreScheduleJoblog);
        }
        super.logTask(coreScheduleJoblog);
    }

    private TCkCtShellTxn updateExistingTransaction(JSONObject kioskItem, TCkCtShellTxn existingTxn) throws ParseException {
        TransactionDetailsResponseDto details = parseTransactionDetails(kioskItem);

        TCkCtShellCard cardEntity = new TCkCtShellCard();
        cardEntity.setScId(details.getTxnCardPan());

        Date now = Calendar.getInstance().getTime();
        existingTxn.setStTxnId(details.getTxnId());
        existingTxn.setTCkCtShellCard(cardEntity);
        existingTxn.setStProduct(details.getTxnProductName());
        existingTxn.setStTxnDate(details.getTxnDateTime());
        existingTxn.setStQty(details.getTxnQty());
        existingTxn.setStInvNet(details.getTxnInvNetAmount());
        existingTxn.setStInvGross(details.getTxnInvGrossAmount());
        existingTxn.setStCrvNet(details.getTxnCustomerRetailValueTotalNet());
        existingTxn.setStCrvGross(details.getTxnCustomerRetailValueTotalGross());
        existingTxn.setSbDtLupd(now);
        return existingTxn;
    }

    private TCkCtShellTxn updateEntity(JSONObject kioskItem) throws EntityNotFoundException, ParseException {
        if (kioskItem == null) {
            throw new EntityNotFoundException("entity is null");
        }

        TransactionDetailsResponseDto details = parseTransactionDetails(kioskItem);

        TCkCtShellTxn entity = new TCkCtShellTxn();
        TCkCtShellCard cardEntity = new TCkCtShellCard();
        cardEntity.setScId(details.getTxnCardPan());

        Date now = Calendar.getInstance().getTime();
        entity.setStId(CkUtil.generateId(PREFIX_KEY));
        entity.setStTxnId(details.getTxnId());
        entity.setTCkCtShellCard(cardEntity);
        entity.setStProduct(details.getTxnProductName());
        entity.setStTxnDate(details.getTxnDateTime());
        entity.setStQty(details.getTxnQty());
        entity.setStInvNet(details.getTxnInvNetAmount());
        entity.setStInvGross(details.getTxnInvGrossAmount());
        entity.setStCrvNet(details.getTxnCustomerRetailValueTotalNet());
        entity.setStCrvGross(details.getTxnCustomerRetailValueTotalGross());
        entity.setSbUidCreate(Constant.DEFAULT_USR);
        entity.setSbDtCreate(now);
        entity.setSbDtLupd(now);
        entity.setSbUidLupd(Constant.DEFAULT_USR);
        entity.setSbStatus(STATUS_ACTIVE);
        return entity;
    }

    private TransactionDetailsResponseDto parseTransactionDetails(JSONObject kioskItem) throws ParseException {
        String txnId = kioskItem.optString("TrnIdentifier", "");
        String txnCardPan = kioskItem.optString("CardPAN", "");
        String txnProductName = kioskItem.optString("ProductName", "");
        String txnDate = kioskItem.optString("TransactionDate", "");
        String txnTime = kioskItem.optString("TransactionTime", "");
        BigDecimal txnQty = kioskItem.optBigDecimal("Quantity", BigDecimal.ZERO);
        BigDecimal txnInvNetAmount = kioskItem.optBigDecimal("InvoiceNetAmount", BigDecimal.ZERO);
        BigDecimal txnInvGrossAmount = kioskItem.optBigDecimal("InvoiceGrossAmount", BigDecimal.ZERO);
        BigDecimal txnCustomerRetailValueTotalNet = kioskItem.optBigDecimal("CustomerRetailValueTotalNet", BigDecimal.ZERO);
        BigDecimal txnCustomerRetailValueTotalGross = kioskItem.optBigDecimal("CustomerRetailValueTotalGross", BigDecimal.ZERO);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
        Date txnDateTime = sdf.parse(txnDate + txnTime);

        return new TransactionDetailsResponseDto(
                txnId,
                txnCardPan,
                txnProductName,
                txnDateTime,
                txnQty,
                txnInvNetAmount,
                txnInvGrossAmount,
                txnCustomerRetailValueTotalNet,
                txnCustomerRetailValueTotalGross
        );
    }
}