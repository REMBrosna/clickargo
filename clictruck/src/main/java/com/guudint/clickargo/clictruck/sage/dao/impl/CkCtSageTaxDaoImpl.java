package com.guudint.clickargo.clictruck.sage.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.guudint.clickargo.clictruck.sage.dao.CkCtSageTaxDao;
import com.guudint.clickargo.clictruck.sage.model.TCkCtSageTax;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Service
public class CkCtSageTaxDaoImpl extends GenericDaoImpl<TCkCtSageTax, String>
		implements CkCtSageTaxDao {
	
	public List<TCkCtSageTax> findActiveSageTax() throws Exception {

		String hql = "from TCkCtSageTax st " 
				+ "where st.stStatus IN :stStatus "
				+ "order by st.stDtCreate asc";
		

		Map<String, Object> params = new HashMap<>();
		params.put("stStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.INACTIVE.getCode()));
		
		return getByQuery(hql, params, 2, 0);
	}
	
}
