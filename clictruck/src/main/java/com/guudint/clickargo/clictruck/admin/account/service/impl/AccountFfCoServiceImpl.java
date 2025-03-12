package com.guudint.clickargo.clictruck.admin.account.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.manageaccn.dao.CkCtFfCoDao;
import com.guudint.clickargo.manageaccn.model.TCkCtFfCo;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;

@Service
public class AccountFfCoServiceImpl {

	private static Logger log = Logger.getLogger(AccountFfCoServiceImpl.class);

	@Autowired
	CkCtFfCoDao ckCtFfCoDao;

	@Transactional
	public List<CoreAccn> getFfCoAccn(String ffAcountId) throws Exception {

		try {
			List<TCkCtFfCo> ffCoList = ckCtFfCoDao.getAccnByFfAccnId(ffAcountId);

			List<TCoreAccn> tAccnList = ffCoList.stream().map(TCkCtFfCo::getTCoreAccnByFfcoCo)
					.collect(Collectors.toList());

			List<CoreAccn> accnList = tAccnList.stream().map(entity -> new CoreAccn(entity))
					.collect(Collectors.toList());

			return accnList;

		} catch (Exception e) {
			log.error("Fail to fetch FFCO via FF ", e);
			throw e;
		}
	}

}
