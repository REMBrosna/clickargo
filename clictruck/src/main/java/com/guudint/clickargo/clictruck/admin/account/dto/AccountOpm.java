package com.guudint.clickargo.clictruck.admin.account.dto;

import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;

public class AccountOpm extends CoreAccn {

	private static final long serialVersionUID = -9170838169201414167L;

	// place holder to flag if account is opm financing in the account config. The
	// value can be determined in T_CK_ACCN_OPM
	boolean isAccountOpm;

	public AccountOpm() {

	}

	public AccountOpm(TCoreAccn entity) {
		super(entity);
	}

	/**
	 * @return the isAccountOpm
	 */
	public boolean isAccountOpm() {
		return isAccountOpm;
	}

	/**
	 * @param isAccountOpm the isAccountOpm to set
	 */
	public void setAccountOpm(boolean isAccountOpm) {
		this.isAccountOpm = isAccountOpm;
	}

}
