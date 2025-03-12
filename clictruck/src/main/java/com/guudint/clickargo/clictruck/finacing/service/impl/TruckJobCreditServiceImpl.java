package com.guudint.clickargo.clictruck.finacing.service.impl;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.finacing.service.ITruckJobCreditService;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCharge;
import com.guudint.clickargo.journal.dto.CkCreditJournal;
import com.guudint.clickargo.journal.service.IJournalService;
import com.guudint.clickargo.master.dto.CkMstJournalTxnType;
import com.guudint.clickargo.master.dto.CkMstServiceType;
import com.guudint.clickargo.master.enums.Currencies;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.master.dto.MstCurrency;

@Service
public class TruckJobCreditServiceImpl implements ITruckJobCreditService {

	private static Logger LOG = Logger.getLogger(TruckJobCreditServiceImpl.class);

	@Autowired
	@Qualifier("ckCtTripChargeDao")
	private GenericDao<TCkCtTripCharge, String> ckCtTripChargeDao;

	@Autowired
	private IJournalService creditJournalService;

	@Override
	public void reserveJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, BigDecimal amount,
			Principal principal) throws Exception {
		LOG.info("reserveJobTruckCredit");
		try {
			if (journalTxnType == null)
				new ParameterException("param journalTxnType null");
			if (jobTruck == null)
				new ParameterException("param jobTruck null");
			if (amount == null)
				new ParameterException("param amount null");

			LOG.info("Total Trip Charge to Reserve: " + amount);
			CkCreditJournal req = new CkCreditJournal();
			req.setTCoreAccn(jobTruck.getTCoreAccnByJobPartyCoFf());
			CkMstServiceType mstServiceType = new CkMstServiceType(ServiceTypes.CLICTRUCK.name(),
					ServiceTypes.CLICTRUCK.name());
			req.setTCkMstServiceType(mstServiceType);
			CkMstJournalTxnType mstJournalTxnType = new CkMstJournalTxnType(journalTxnType.name(),
					journalTxnType.getDesc());
			req.setTCkMstJournalTxnType(mstJournalTxnType);
			req.setCjnTxnRef(jobTruck.getJobId());
			MstCurrency idrCurr = new MstCurrency();
			idrCurr.setCcyCode(Currencies.IDR.getCode());
			req.setTMstCurrency(idrCurr);
			req.setCjnReserve(amount);

			creditJournalService.reserve(req, principal);

		} catch (Exception ex) {
			LOG.error("reserveJobTruckCredit", ex);
			throw ex;
		}

	}

	@Override
	public void reverseJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal)
			throws Exception {
		LOG.info("reverseJobTruckCredit");
		try {

			if (journalTxnType == null)
				new ParameterException("param journalTxnType null");
			if (jobTruck == null)
				new ParameterException("param jobTruck null");

			// Call clicCredit reverseJob api
			CkCreditJournal req = new CkCreditJournal();
			req.setTCoreAccn(jobTruck.getTCoreAccnByJobPartyCoFf());
			CkMstServiceType mstServiceType = new CkMstServiceType(ServiceTypes.CLICTRUCK.name(),
					ServiceTypes.CLICTRUCK.name());
			req.setTCkMstServiceType(mstServiceType);
			CkMstJournalTxnType mstJournalTxnType = new CkMstJournalTxnType(journalTxnType.name(),
					journalTxnType.getDesc());
			req.setTCkMstJournalTxnType(mstJournalTxnType);
			req.setCjnTxnRef(jobTruck.getJobId());
			MstCurrency idrCurr = new MstCurrency();
			idrCurr.setCcyCode(Currencies.IDR.getCode());
			req.setTMstCurrency(idrCurr);
			double amount = 0.0;
			if (journalTxnType == JournalTxnType.JOB_SUBMIT_REIMBURSEMENT) {
				amount = jobTruck.getJobTotalReimbursements() == null ? 0.0
						: jobTruck.getJobTotalReimbursements().doubleValue();
			} else {
				amount = (jobTruck.getJobTotalCharge() == null ? 0.0 : jobTruck.getJobTotalCharge().doubleValue())
						+ (jobTruck.getJobTotalReimbursements() == null ? 0.0
								: jobTruck.getJobTotalReimbursements().doubleValue());
			}

			req.setCjnReserve(new BigDecimal(amount));

			creditJournalService.reverse(req, principal);

		} catch (Exception ex) {
			LOG.error("reverseJobTruckCredit", ex);
			throw ex;
		}

	}

	@Override
	public void utilizeJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal)
			throws Exception {
		LOG.info("utilizeJobTruckCredit");
		try {
			if (journalTxnType == null)
				new ParameterException("param journalTxnType null");
			if (jobTruck == null)
				new ParameterException("param jobTruck null");

			// TODO call clicCredit reverseJob api
			LOG.info("Total charge to utilize: " + jobTruck.getJobTotalCharge().doubleValue()
					+ (null == jobTruck.getJobTotalReimbursements() ? 0.0
							: jobTruck.getJobTotalReimbursements().doubleValue()));

			CkCreditJournal req = new CkCreditJournal();
			req.setTCoreAccn(jobTruck.getTCoreAccnByJobPartyCoFf());
			CkMstServiceType mstServiceType = new CkMstServiceType(ServiceTypes.CLICTRUCK.name(),
					ServiceTypes.CLICTRUCK.name());
			req.setTCkMstServiceType(mstServiceType);
			CkMstJournalTxnType mstJournalTxnType = new CkMstJournalTxnType(journalTxnType.name(),
					journalTxnType.getDesc());
			req.setTCkMstJournalTxnType(mstJournalTxnType);
			req.setCjnTxnRef(jobTruck.getJobId());
			MstCurrency idrCurr = new MstCurrency();
			idrCurr.setCcyCode(Currencies.IDR.getCode());
			req.setTMstCurrency(idrCurr);
			double amount = (jobTruck.getJobTotalCharge() == null ? 0.0 : jobTruck.getJobTotalCharge().doubleValue())
					+ (jobTruck.getJobTotalReimbursements() == null ? 0.0
							: jobTruck.getJobTotalReimbursements().doubleValue());
			req.setCjnUtilized(new BigDecimal(amount));

			creditJournalService.utilize(req, principal);
		} catch (Exception ex) {
			LOG.error("utilizeJobTruckCredit", ex);
			throw ex;
		}

	}

	@Override
	public void reverseUtilized(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal)
			throws Exception {
		LOG.info("utilizeJobTruckCredit");
		try {
			if (journalTxnType == null)
				new ParameterException("param journalTxnType null");
			if (jobTruck == null)
				new ParameterException("param jobTruck null");

			// TODO call clicCredit reverseJob api
			LOG.info("Total charge to utilize: " + jobTruck.getJobTotalCharge().doubleValue()
					+ (null == jobTruck.getJobTotalReimbursements() ? 0.0
							: jobTruck.getJobTotalReimbursements().doubleValue()));

			CkCreditJournal req = new CkCreditJournal();
			req.setTCoreAccn(jobTruck.getTCoreAccnByJobPartyCoFf());
			CkMstServiceType mstServiceType = new CkMstServiceType(ServiceTypes.CLICTRUCK.name(),
					ServiceTypes.CLICTRUCK.name());
			req.setTCkMstServiceType(mstServiceType);
			CkMstJournalTxnType mstJournalTxnType = new CkMstJournalTxnType(journalTxnType.name(),
					journalTxnType.getDesc());
			req.setTCkMstJournalTxnType(mstJournalTxnType);
			req.setCjnTxnRef(jobTruck.getJobId());
			MstCurrency idrCurr = new MstCurrency();
			idrCurr.setCcyCode(Currencies.IDR.getCode());
			req.setTMstCurrency(idrCurr);
			double amount = (jobTruck.getJobTotalCharge() == null ? 0.0 : jobTruck.getJobTotalCharge().doubleValue())
					+ (jobTruck.getJobTotalReimbursements() == null ? 0.0
							: jobTruck.getJobTotalReimbursements().doubleValue());
			req.setCjnUtilized(new BigDecimal(amount));

			creditJournalService.pay(req, principal);
		} catch (Exception ex) {
			LOG.error("utilizeJobTruckCredit", ex);
			throw ex;
		}

	}

}
