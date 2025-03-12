package com.guudint.clickargo.clictruck.finacing.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteItemDao;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNoteItem;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.service.IDebitNoteService;
import com.guudint.clickargo.clictruck.finacing.service.IPlatformInvoiceService;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstToInvoiceState;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceItemDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoiceItem;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreAddress;
import com.vcc.camelone.common.dao.GenericDao;

/**
 * Misc service used in {@code TruckInvoiceGenController} to regenerate the
 * PDFs.
 */
@Service
public class TruckRegenPdfMiscService {

	private static final Logger LOG = Logger.getLogger(TruckRegenPdfMiscService.class);

	@Autowired
	private IDebitNoteService debitNoteService;

	@Autowired
	private IPlatformInvoiceService platformFeeService;

	@Autowired
	private GenericDao<TCkCtDebitNote, String> ckCtDebitNoteDao;

	@Autowired
	private CkCtDebitNoteItemDao ckCtDebitNoteItemDao;

	@Autowired
	private GenericDao<TCkCtPlatformInvoice, String> ckCtPlatformInvDao;

	@Autowired
	private CkCtPlatformInvoiceItemDao ckCtPlatformInvItemDao;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void regeneratePdf() throws Exception {
		LOG.info("regeneratePdf");
		// Search for records in PlatformFee with inv_refresh = 1
		processPlatformInvoicePdf();

		// Search for records in Debit Note with dn_refresh = 1
		processDebitNotePdf();
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processPlatformInvoicePdf() throws Exception {
		LOG.info("processPlatformInvoicePdf");
		String hql = "from TCkCtPlatformInvoice o where o.invRefresh=:invRefresh and o.invStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("invRefresh", (short) 1);
		params.put("status", RecordStatus.ACTIVE.getCode());

		List<TCkCtPlatformInvoice> invList = ckCtPlatformInvDao.getByQuery(hql, params);
		if (invList != null && invList.size() > 0) {
			for (TCkCtPlatformInvoice inv : invList) {
				LOG.info("Regenerating " + inv.getInvId());
				Hibernate.initialize(inv.getTCkCtMstToInvoiceState());
				Hibernate.initialize(inv.getTCoreAccnByInvTo());
				if (inv.getTCoreAccnByInvTo() != null) {
					Hibernate.initialize(inv.getTCoreAccnByInvTo().getAccnAddr());
				}
				Hibernate.initialize(inv.getTCoreAccnByInvFrom());
				CkCtPlatformInvoice dtoInv = new CkCtPlatformInvoice(inv);
				dtoInv.setTCkCtMstToInvoiceState(new CkCtMstToInvoiceState(inv.getTCkCtMstToInvoiceState()));
				CoreAccn invToAccn = new CoreAccn(inv.getTCoreAccnByInvTo());
				if (inv.getTCoreAccnByInvTo() != null) {
					invToAccn.setAccnAddr(new CoreAddress(inv.getTCoreAccnByInvTo().getAccnAddr()));
				}
				dtoInv.setTCoreAccnByInvTo(invToAccn);
				dtoInv.setTCoreAccnByInvFrom(new CoreAccn(inv.getTCoreAccnByInvFrom()));

				// Fetch the items
				List<CkCtPlatformInvoiceItem> itmList = ckCtPlatformInvItemDao.getPlatformFeeItems(dtoInv.getInvId());
				inv.setInvLoc(platformFeeService.generatePlatformInvoicePdf(dtoInv, itmList, false));
				inv.setInvDtLupd(new Date());
				inv.setInvRefresh((short) 0);
				ckCtPlatformInvDao.update(inv);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processDebitNotePdf() throws Exception {
		String hql = "from TCkCtDebitNote o where o.dnRefresh=:dnRefresh and o.dnStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("dnRefresh", (short) 1);
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCkCtDebitNote> dnList = ckCtDebitNoteDao.getByQuery(hql, params);
		if (dnList != null && dnList.size() > 0) {
			for (TCkCtDebitNote dn : dnList) {
				LOG.info("Regenerating " + dn.getDnId());
				Hibernate.initialize(dn.getTCkCtMstDebitNoteState());
				Hibernate.initialize(dn.getTCoreAccnByDnTo());
				if (dn.getTCoreAccnByDnTo() != null) {
					Hibernate.initialize(dn.getTCoreAccnByDnTo().getAccnAddr());
				}
				Hibernate.initialize(dn.getTCoreAccnByDnFrom());

				CkCtDebitNote dnDto = new CkCtDebitNote(dn);
				dnDto.setTCoreAccnByDnFrom(new CoreAccn(dn.getTCoreAccnByDnFrom()));

				CoreAccn accnDnTo = new CoreAccn(dn.getTCoreAccnByDnTo());
				if (dn.getTCoreAccnByDnTo() != null) {
					accnDnTo.setAccnAddr(new CoreAddress(dn.getTCoreAccnByDnTo().getAccnAddr()));
				}
				dnDto.setTCoreAccnByDnTo(accnDnTo);

				// Fetch the items
				List<CkCtDebitNoteItem> itmList = ckCtDebitNoteItemDao.getDebitNoteitems(dnDto.getDnId());
				dn.setDnLoc(debitNoteService.generateDebitNotePdf(dnDto, itmList, false));
				dn.setDnDtLupd(new Date());
				dn.setDnRefresh((short) 0);
				ckCtDebitNoteDao.update(dn);
			}
		}

	}



}
