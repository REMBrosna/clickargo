package com.guudint.clickargo.clictruck.admin.ratetable.service;

import java.util.List;

import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.service.impl.CkCtRateTableServiceImpl.TruckOperatorOptions;
import com.guudint.clickargo.master.enums.Currencies;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

public interface ICkCtRateTableService {

	CkCtRateTable updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;
	List<TruckOperatorOptions> loadOperators(Principal principal)
			throws ParameterException, EntityNotFoundException, Exception;

	CkCtRateTable getRateTableByAccounts(CoreAccn rtComAccn, CoreAccn coFfAccn, Currencies currency)
			throws ParameterException, EntityNotFoundException, Exception;
	/**
	 * Loads the accounts that has contract with the specific {@code principal} be
	 * it CO/FF or TO.
	 */
	List<CoreAccn> loadAccnsRateTableByContract(Principal principal, boolean isFilterByRateTableExistence)
			throws ParameterException, EntityNotFoundException, Exception;
}
