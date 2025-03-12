package com.guudint.clickargo.clictruck.scheduler.sg.shell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.admin.shell.dto.response.InvoicingDiscount;
import com.guudint.clickargo.clictruck.admin.shell.model.*;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.service.impl.CkSeqNoServiceImpl;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@EnableScheduling
@EnableAsync
public class ClictruckTxnInvoicingScheduler  extends AbstractShellScheduler {

    private static final Logger LOG = Logger.getLogger(ClictruckTxnInvoicingScheduler.class);
    protected static ObjectMapper mapper = new ObjectMapper();
    private final List<String> accnIds = new ArrayList<>();
    private final List<TCkCtShellTxn> tempTxns = new ArrayList<>();

    public ClictruckTxnInvoicingScheduler() {
        super.setTaskName(this.getClass().getSimpleName());
    }

//     @Scheduled(fixedRate = 2000) // Testing
    @Scheduled(cron = "0 59 23 28,29,30,31 * ?")// Run at 11:59 PM on the 28th, 29th, 30th, and 31st day of each month
    @Transactional
    @Override
    @SchedulerLock(name = "ClictruckTxnInvoicingScheduler", lockAtLeastFor = "60s")
    public void doJob() throws Exception {

		if (!super.isSingapore()) {
			LOG.info("Do dot run ClictruckTxnInvoicingScheduler because not SG country.");
			return;
		}
		
        LockAssert.assertLocked();
        String taskNo = super.getTaskNo();
        CoreScheduleJoblog coreScheduleJoblog = null;
        try {
            coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
            LOG.debug("ClictruckTxnInvoicingScheduler Started: " + Calendar.getInstance().getTime().toString());
            List<TCkCtShellTxn> tCkCtShellTxns = super.ckCtShellTxnService.getAllShellTxn();
            if(Objects.nonNull(tCkCtShellTxns) && !tCkCtShellTxns.isEmpty()){
                for (TCkCtShellTxn txn: tCkCtShellTxns){
                    if(Objects.nonNull(txn.getTCoreAccn().getAccnId())){
                        String accnId = txn.getTCoreAccn().getAccnId();
                        boolean isPresent = accnIds.stream().anyMatch(val -> val.equals(accnId));
                        tempTxns.add(txn);
                        if (!isPresent){
                            accnIds.add(accnId);
                        }
                    }
                }
            } else {
                // init invoice by user account
                for (String accnId : accnIds){
                    if (Objects.nonNull(accnId)){
                        try {
                            boolean isAccnPresent = super.isExistingAccn(accnId);
                            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
                            String invId = CkUtil.generateId("INV");
                            if (isAccnPresent){
                                Date now = Calendar.getInstance().getTime();
                                TCkCtShellInvoice entity = new TCkCtShellInvoice();
                                TCoreAccn tCoreAccn = new TCoreAccn();
                                String noSeq = seqnoService.getNextSequence(CkSeqNoServiceImpl.SeqNoCode.CT_SHELL_INV_NO.name());
                                entity.setInvNo(noSeq.replaceAll("YYMMDD", sdf.format(now)));
                                entity.setInvId(invId);
                                tCoreAccn.setAccnId(accnId);
                                entity.setTCoreAccn(tCoreAccn);
                                entity.setInvAmt(BigDecimal.ZERO);
                                entity.setInvBalanceAmt(BigDecimal.ZERO);
                                entity.setInvPaymentAmt(BigDecimal.ZERO);
                                entity.setInvStatus('U');
                                entity.setInvDtLupd(now);
                                entity.setInvDtCreate(now);
                                entity.setInvUidLupd("");
                                entity.setInvUidCreate(Constant.DEFAULT_USR);
                                super.ckCtShellInvoiceService.save(entity);

                                // make calculation for discount for each transactions to invoice items
                                for (TCkCtShellTxn txn: tempTxns) {
                                    if(Objects.nonNull(txn)) {
                                        List<InvoicingDiscount> discountList = super.discountList();
                                        if (Objects.nonNull(discountList) && !discountList.isEmpty()) {
                                            if(Objects.nonNull(txn.getTCoreAccn()) && Objects.nonNull(txn.getTCoreAccn().getAccnId())){
                                                // calculate the discount base on sysParams
                                                for (InvoicingDiscount invoicingDiscount : discountList) {
                                                    // if the same TO account, it will be calculate those transaction belong to TO
                                                    if (invoicingDiscount.getAccn().equals(txn.getTCoreAccn().getAccnId())){
                                                        TCkCtShellCard card = super.findCardById(txn.getTCkCtShellCard().getScId());
                                                        if (Objects.nonNull(card) && Objects.nonNull(txn.getTCoreAccn())) {
                                                            TCkCtShellCardTruck truck = super.findAssignedTruckCardById(card.getScId(), txn.getTCoreAccn().getAccnId());
                                                            TCkCtShellInvoiceItem invoiceItem = new TCkCtShellInvoiceItem();

                                                            TCkCtShellInvoice tCkCtShellInvoice = new TCkCtShellInvoice();
                                                            tCkCtShellInvoice.setInvId(invId);
                                                            invoiceItem.setTCkCtShellInvoice(tCkCtShellInvoice);

                                                            TCkCtShellCard tCkCtShellCard = new TCkCtShellCard();
                                                            tCkCtShellCard.setScId(card.getScId());
                                                            invoiceItem.setTCkCtShellCard(tCkCtShellCard);
                                                            if (Objects.nonNull(truck.getTCkCtVeh())) {
                                                                TCkCtVeh tCkCtVeh = new TCkCtVeh();
                                                                tCkCtVeh.setVhId(truck.getTCkCtVeh().getVhId());
                                                                invoiceItem.setTCkCtVeh(tCkCtVeh);
                                                            }
                                                            invoiceItem.setItmDesc(txn.getStProduct());
                                                            invoiceItem.setItmDtTxn(txn.getStTxnDate());

                                                            BigDecimal percentage = getPercentage(txn.getStProduct(), invoicingDiscount);
                                                            BigDecimal itemCost = calculateInvoiceAmount(txn.getStCrvGross(), percentage, GST_TAX, txn.getStProduct(), txn.getStQty());
                                                            BigDecimal itemDisCost = calculateDiscount(txn.getStCrvGross(), percentage);
                                                            BigDecimal itemTax = calculateTax(txn.getStCrvGross(),GST_TAX);
                                                            BigDecimal itemCO2Fee = calculateCO2Fee(txn.getStProduct(), txn.getStQty());
                                                            invoiceItem.setItmTax(itemTax);
                                                            invoiceItem.setItmTotal(itemCost);
                                                            invoiceItem.setItmCo2(itemCO2Fee);
                                                            invoiceItem.setItmDiscount(itemDisCost);
                                                            invoiceItem.setItmCost(txn.getStCrvGross());

                                                            invoiceItem.setItmId(CkUtil.generateId("INV-I"));
                                                            invoiceItem.setItmDtCreate(now);
                                                            invoiceItem.setItmUidCreate(Constant.DEFAULT_USR);
                                                            invoiceItem.setItmDtLupd(now);
                                                            invoiceItem.setItmUidLupd(Constant.DEFAULT_USR);
                                                            invoiceItem.setItmStatus('A');
                                                            super.ckCtShellInvoiceItemService.save(invoiceItem);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                // update total amount to invoice
                                TCkCtShellInvoice existInv = super.ckCtShellInvoiceService.find(invId);
                                if (Objects.nonNull(existInv)){
                                    BigDecimal totalInvItemsAmount = super.ckCtShellInvoiceItemService.getTotalAmountByInvId(invId);
                                    existInv.setInvAmt(totalInvItemsAmount);
                                    entity.setInvDt(now);
                                    super.ckCtShellInvoiceService.update(existInv);
                                    tempTxns.clear();
                                    accnIds.clear();
                                }
                            }
                        } catch (Exception e) {
                            LOG.error("isExistingAccn", e);
                        }
                    }
                };
            }

            coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(), "SUCCESS");
        } catch (Exception ex) {
            LOG.error("doJob", ex);
            coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, ServiceStatus.STATUS.FAILED.toString(), ex.getMessage());
            super.logTask(coreScheduleJoblog);
        }
        super.logTask(coreScheduleJoblog);
    }

    // Method to calculate the discounted price
    private static BigDecimal calculateDiscount(BigDecimal actualCost, BigDecimal discountPercentage) {
        BigDecimal discountDecimal = discountPercentage.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        return actualCost.multiply(BigDecimal.ONE.subtract(discountDecimal)).setScale(2, RoundingMode.HALF_UP);
    }

    // Method to calculate the CO2 Compensation Fee
    private static BigDecimal calculateCO2Fee(String fuelType, BigDecimal quantity) {
        switch (fuelType) {
            case "Petrol":
            case "Diesel":
                return CO2_COMPENSATION_FEE.multiply(quantity);
            default:
                break;
        }
        return BigDecimal.ZERO;
    }

    private static BigDecimal calculateTax(BigDecimal amountBeforeTax, BigDecimal gstPercentage) {
        BigDecimal gstDecimal = gstPercentage.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        return amountBeforeTax.multiply(gstDecimal);
    }

    public static BigDecimal calculateInvoiceAmount(BigDecimal actualCost, BigDecimal discountPercentage, BigDecimal gstPercentage, String fuelType, BigDecimal quantity) {
        // Calculate discounted price
        BigDecimal discountedPrice = calculateDiscount(actualCost, discountPercentage);

        // Calculate CO2 Compensation Fee
        BigDecimal co2Fee = calculateCO2Fee(fuelType, quantity);

        // Calculate amount before tax
        BigDecimal amountBeforeTax = discountedPrice.add(co2Fee);

        // Calculate tax amount
        BigDecimal taxAmount = calculateTax(amountBeforeTax, gstPercentage);

        // Calculate final invoice amount
        BigDecimal invoiceAmount = amountBeforeTax.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        return invoiceAmount;
    }

    protected static BigDecimal getPercentage(String fuelType, InvoicingDiscount discount) {
        BigDecimal discountPercentage;
        switch (fuelType) {
            case "Petrol":
                discountPercentage = discount.getDiscount().getPetrol();
                break;
            case "Diesel":
                discountPercentage = discount.getDiscount().getDissels();
                break;
            case "Lubricant":
                discountPercentage = discount.getDiscount().getLubricant();
                break;
            case "Electric Charge":
                discountPercentage = discount.getDiscount().getElectric();
                break;
            default:
                throw new IllegalArgumentException("Invalid fuel type");
        }
        return discountPercentage;
    }
}
