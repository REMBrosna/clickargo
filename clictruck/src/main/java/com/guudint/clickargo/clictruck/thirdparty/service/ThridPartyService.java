package com.guudint.clickargo.clictruck.thirdparty.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.common.dto.CkCtCo2x;
import com.guudint.clickargo.clictruck.common.service.CkCtCo2xService;
import com.guudint.clickargo.clictruck.common.service.impl.CkCtCo2xServiceImpl;
import com.guudint.clickargo.clictruck.thirdparty.dto.ThirdPartyDto;
import com.guudint.clickargo.clictruck.thirdparty.dto.ThirdPartyEnum;
import com.guudint.clickargo.common.dto.CkAccn;
import com.guudint.clickargo.common.service.impl.CkCoreAccnService;
import com.vcc.camelone.util.email.SysParam;

@Service
public class ThridPartyService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ThridPartyService.class);

	@Autowired
	private CkCtCo2xService co2xService;
	@Autowired
	private CkCoreAccnService coreAccnService;
	@Autowired
	private SysParam sysParam;

	public List<ThirdPartyDto> getThirdpartList(String accnId) {

		List<ThirdPartyDto> partyList = new ArrayList<>();
		partyList.add(this.getCo2(accnId));
		partyList.add(this.getISPOT(accnId));

		return partyList;
	}

	public ThirdPartyDto getCo2(String accnId) {

		ThirdPartyDto thirdParty = new ThirdPartyDto(ThirdPartyEnum.CO2X);

		try {

			CkCtCo2x co2x = co2xService.findByAccount(accnId);
			if (co2x != null && StringUtils.isNoneBlank(co2x.getCo2xUid())) {

				Map<String, String> map = new HashMap<>();
				map.put("url", co2x.getSsoUrl());
				map.put("email", co2x.getCo2xUid());
				map.put("password", co2x.getCo2xPwd());

				thirdParty.setParameters(map);
			}

			String ssoUrl = sysParam.getValString(CkCtCo2xServiceImpl.KEY_CO2_CONNECT_URL, null);
			thirdParty.setUrl(ssoUrl);

		} catch (Exception e) {
			log.error("", e);
		}

		return thirdParty;
	}

	public ThirdPartyDto getISPOT(String accnId) {

		ThirdPartyDto thirdParty = new ThirdPartyDto(ThirdPartyEnum.ISPOT);

		try {

			String ascentKey = sysParam.getValString("CLICTRUCK_ASCENT_KEY",
					"$2a$11$ZdWBAfrm6X4cNsE9PAhHzONeilbEWIa3yjpq8V8fiVs8S9UidM3TO");

			CkAccn ckAccn = coreAccnService.getCkAccn(accnId);

			if (ckAccn != null && StringUtils.isNoneBlank(ckAccn.getCaccnIspotSubAccn())) {

				Map<String, String> map = new HashMap<>();

				map.put("key", ascentKey);
				map.put("sact", ckAccn.getCaccnIspotSubAccn());

				thirdParty.setParameters(map);
			}
			String ssoUrl = sysParam.getValString("CLICTRUCK_ASCENT_ISPOT_URL",
					"https://ext.logistics.myascents.net/login2");
			thirdParty.setUrl(ssoUrl);

		} catch (Exception e) {
			log.error("", e);
		}

		return thirdParty;
	}

}
