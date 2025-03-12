package com.guudint.clickargo.clictruck.opm.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.opm.dao.CkOpmDao;
import com.guudint.clickargo.clictruck.opm.model.TCkOpm;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkAccnOpmDao;
import com.guudint.clickargo.common.model.TCkAccnOpm;

@Service
public class OpmUpdateAccnStatusService {

	private static Logger log = Logger.getLogger(OpmUpdateAccnStatusService.class);

	@Autowired
	protected CkAccnOpmDao ckCtAccnOpmDao;

	@Autowired
	protected CkOpmDao ckOpmDao;

	boolean isApproved = false;
	boolean isTermiate = false;
	boolean isSuspend = false;
	boolean isUnSuspend = false;
	boolean isExpired = false;

	@Transactional
	public String updateOpmAccnStatus() throws Exception {

		String rst = "";

		try {
			List<TCkAccnOpm> accnOpmList = ckCtAccnOpmDao.getAll();

			for (TCkAccnOpm accnOpm : accnOpmList) {
				TCkAccnOpm accnOpmResult = this.updateOpmAccnStatusByAccn(accnOpm);

				TCkOpm ckOpm = ckOpmDao.findByAccnId(accnOpm.getCaoId());
				if (ckOpm != null) {
					ckOpm.setOpmStatus(accnOpmResult.getCaoStatus());
					ckOpmDao.update(ckOpm);
				}
			}

		} catch (Exception e) {
			log.error("", e);
			rst = rst + e.getMessage();
		}
		return rst;
	}

	@Transactional
	public TCkAccnOpm updateOpmAccnStatusByAccnId(String accnId) throws Exception {

		return this.updateOpmAccnStatusByAccn(ckCtAccnOpmDao.find(accnId));
	}

	@Transactional
	public TCkAccnOpm updateOpmAccnStatusByAccn(TCkAccnOpm accnOpm) throws Exception {

//		String rst = "";

		try {

			this.setAll2False();

			Date latestDate = new Date();
			Date now = new Date();

			// if the dt_approve is less than or equal to today, automatic approve
			if (accnOpm.getCaoDtApprove() != null && accnOpm.getCaoDtApprove().compareTo(now) <= 0) {
				latestDate = accnOpm.getCaoDtApprove();
				this.setAll2False();
				isApproved = true;
			}

			if (accnOpm.getCaoDtClose() != null && latestDate.before(accnOpm.getCaoDtClose())
					&& now.after(accnOpm.getCaoDtClose())) {

				latestDate = accnOpm.getCaoDtClose();
				this.setAll2False();
				isTermiate = true;
			}

			if (accnOpm.getCaoDtSuspend() != null && latestDate.before(accnOpm.getCaoDtSuspend())
					&& now.after(accnOpm.getCaoDtSuspend())) {

				latestDate = accnOpm.getCaoDtSuspend();
				this.setAll2False();
				isSuspend = true;
			}

			if (accnOpm.getCaoDtUnsuspend() != null && latestDate.before(accnOpm.getCaoDtUnsuspend())
					&& now.after(accnOpm.getCaoDtUnsuspend())) {
				latestDate = accnOpm.getCaoDtUnsuspend();
				this.setAll2False();
				isUnSuspend = true;
			}

			// Expiry
			if (accnOpm.getCaoDtExpiry() != null && accnOpm.getCaoDtExpiry().before(now)) {
				isExpired = true;
			}

			// Process
			if (isExpired) {
				if (RecordStatus.EXPIRED.getCode() != accnOpm.getCaoStatus()) {
					this.updateOpmAccnStatus(accnOpm, RecordStatus.EXPIRED);
				}
			} else {
				if (isApproved) {
					if (RecordStatus.ACTIVE.getCode() != accnOpm.getCaoStatus()) {
						this.updateOpmAccnStatus(accnOpm, RecordStatus.ACTIVE);
					}
				} else if (isTermiate) {
					if (RecordStatus.INACTIVE.getCode() != accnOpm.getCaoStatus()) {
						this.updateOpmAccnStatus(accnOpm, RecordStatus.INACTIVE);
					}
				} else if (isSuspend) {
					if (RecordStatus.SUSPENDED.getCode() != accnOpm.getCaoStatus()) {
						this.updateOpmAccnStatus(accnOpm, RecordStatus.SUSPENDED);
					}
				} else if (isUnSuspend) {
					if (RecordStatus.ACTIVE.getCode() != accnOpm.getCaoStatus()) {
						this.updateOpmAccnStatus(accnOpm, RecordStatus.ACTIVE);
					}
				}
			}

			//
		} catch (Exception e) {
			log.error("", e);
		}

		return accnOpm;
	}

	private void setAll2False() {
		isApproved = false;
		isExpired = false;
		isTermiate = false;
		isSuspend = false;
		isUnSuspend = false;
	}

	private void updateOpmAccnStatus(TCkAccnOpm accnOpm, RecordStatus recordStatus) throws Exception {
		accnOpm.setCaoStatus(recordStatus.getCode());
		accnOpm.setCaoDtLupd(new Date());
		ckCtAccnOpmDao.saveOrUpdate(accnOpm);
	}
}
