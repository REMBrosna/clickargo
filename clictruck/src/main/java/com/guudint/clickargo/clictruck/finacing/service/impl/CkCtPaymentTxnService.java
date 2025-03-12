package com.guudint.clickargo.clictruck.finacing.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.finacing.dto.CkCtPaymentTxn;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentDetails;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentDetails.InvoiceDetails;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPayment;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.payment.dto.CkPaymentTxn;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.guudint.clickargo.payment.service.impl.CkPaymentTxnService;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.master.dto.MstAccnType;
import com.vcc.camelone.master.dto.MstBank;
import com.vcc.camelone.master.model.TMstBank;
import com.vcc.camelone.util.PrincipalUtilService;

/**
 * Extension of {@code CkPaymentTxnService} to perform additional retrieval to
 * populate lists of payment details associated to this transaction. This is use
 * for the transaction history details.
 */
@Service
public class CkCtPaymentTxnService extends CkPaymentTxnService {

	private static Logger log = Logger.getLogger(CkCtPaymentTxnService.class);
	@Autowired
	@Qualifier("ckCtPaymentDao")
	private GenericDao<TCkCtPayment, String> ckCtPaymentDao;

	@Autowired
	private PrincipalUtilService principalUtilService;

	@Autowired
	@Qualifier("mstBankService")
	private IEntityService<TMstBank, String, MstBank> mstBankService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkCtPaymentTxn dtoFromEntity(TCkPaymentTxn entity) throws ParameterException, ProcessingException {

		CkCtPaymentTxn doDto = new CkCtPaymentTxn();
		try {
			CkPaymentTxn dto = super.dtoFromEntity(entity);
			BeanUtils.copyProperties(doDto, dto);
			JobPaymentDetails jobPayDetails = new JobPaymentDetails();
			jobPayDetails.setBillingDate(entity.getPtxDtCreate());
			jobPayDetails.setPaidDate(entity.getPtxDtPaid());
			jobPayDetails.setPaymentRef(entity.getPtxId());
			jobPayDetails.setTotalIdr(entity.getPtxAmount().doubleValue());

			Principal principal = principalUtilService.getPrincipal();
			Optional<MstAccnType> opAccnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType());
			// There should only one service provider though
			if (opAccnType.isPresent()
					&& opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
				jobPayDetails.setVaIdr(entity.getPtxPayeeBankAccn());
				jobPayDetails.setBankAccnName(entity.getPtxPayeeBankAccnName());
			} else {
				jobPayDetails.setVaIdr(entity.getPtxPayerBankAccn());
			}

			// Get the do payments by txn
			String hql = "from TCkCtPayment o where o.TCkPaymentTxn.ptxId=:ptxId and o.ctpStatus=:ctpStatus";
			Map<String, Object> params = new HashMap<>();
			params.put("ptxId", entity.getPtxId());
			params.put("ctpStatus", RecordStatus.ACTIVE.getCode());

			List<TCkCtPayment> listCtPayments = ckCtPaymentDao.getByQuery(hql, params);
			if (listCtPayments != null && listCtPayments.size() > 0) {
				List<InvoiceDetails> dopList = new ArrayList<>();
				int seq = 1;
				for (TCkCtPayment ctpE : listCtPayments) {
					Hibernate.initialize(ctpE.getTCkPaymentTxn());
					Hibernate.initialize(ctpE.getTMstCurrency());
					InvoiceDetails inv = new InvoiceDetails();
					inv.setId(ctpE.getCtpId());
					inv.setSeq(seq);
					inv.setJobId(ctpE.getCtpJob());
					inv.setInvDesc(ctpE.getCtpItem());
					inv.setQty(ctpE.getCtpQty());
					inv.setInvNo(ctpE.getCtpRef());
					inv.setInvCurrency(ctpE.getTMstCurrency().getCcyCode());
					inv.setInvAmt(ctpE.getCtpAmount());
					inv.setFileLocation(ctpE.getCtpAttach());
					dopList.add(inv);
					
					seq++;
				}
				jobPayDetails.setInvoiceDetails(dopList);
			}
			doDto.setJobPaymentDetails(jobPayDetails);

		} catch (Exception ex) {
			log.error("dtoFromEntity", ex);
		}

		return doDto;
	}
}
