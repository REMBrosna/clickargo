package com.guudint.clickargo.clictruck.admin.contract.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.contract.constant.CkCtContractConstant;
import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtContractDaoImpl extends GenericDaoImpl<TCkCtContract, String> implements CkCtContractDao {

    @Override
    @Transactional
    public Optional<TCkCtContract> findByName(String name) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtContract.class);
        criteria.add(Restrictions.eq(CkCtContractConstant.PropertyName.CON_NAME, name));
        return Optional.ofNullable(getOne(criteria));
    }

	@Override
	public List<TCkCtContract> findValidContract(String ConCoFf, String ConTo) throws Exception {
		// TODO Auto-generated method stub
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String hql = "from TCkCtContract o WHERE o.TCoreAccnByConCoFf.accnId = :conCoFfId "
				+ "AND o.TCoreAccnByConTo.accnId = :conToId "
				+ "AND o.conStatus = :conStatus "
				+ "AND :now BETWEEN DATE_FORMAT(o.conDtStart, '%Y-%m-%d') AND DATE_FORMAT(o.conDtEnd, '%Y-%m-%d')";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conCoFfId", ConCoFf);
		params.put("conToId", ConTo);
		params.put("conStatus", RecordStatus.ACTIVE.getCode());
		params.put("now", sdf.format(Calendar.getInstance().getTime()));
		return getByQuery(hql, params);
	}
	
	@Override
	@Transactional
	public List<TCkCtContract> findNotValidContract() throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = sdf.format(Calendar.getInstance().getTime());
		String hql = "from TCkCtContract o WHERE o.conStatus = 'A' "
				+ "AND :now NOT BETWEEN DATE_FORMAT(o.conDtStart, '%Y-%m-%d') "
				+ "AND DATE_FORMAT(o.conDtEnd, '%Y-%m-%d')";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("now",formattedDate);
		return getByQuery(hql, params);
		
	}
	
	@Override
	public List<TCkCtContract> findValidContractByCoFf(String ConCoFf) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String hql = "from TCkCtContract o WHERE o.TCoreAccnByConCoFf.accnId = :conCoFfId "
				+ "AND o.conStatus = :conStatus "
				+ "AND :now BETWEEN DATE_FORMAT(o.conDtStart, '%Y-%m-%d') AND DATE_FORMAT(o.conDtEnd, '%Y-%m-%d')";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("conCoFfId", ConCoFf);
		params.put("conStatus", RecordStatus.ACTIVE.getCode());
		params.put("now", sdf.format(Calendar.getInstance().getTime()));
		return getByQuery(hql, params);
	}

	@Override
	public List<TCkCtContract> findValidContractByTo (String toAccnId) throws Exception {

		String hql = "from TCkCtContract o WHERE o.TCoreAccnByConTo.accnId = :toAccnId "
				+ "AND o.conStatus = :conStatus "
				+ "AND (:now BETWEEN o.conDtStart AND o.conDtEnd )";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("toAccnId", toAccnId);
		params.put("conStatus", RecordStatus.ACTIVE.getCode());
		params.put("now", new Date());
		return getByQuery(hql, params);
	}
	@Override
	@Transactional
	public Optional<TCkCtContract> findByConId(String id) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtContract.class);
		criteria.add(Restrictions.eq(CkCtContractConstant.PropertyName.CON_ID, id));
		return Optional.ofNullable(getOne(criteria));
	}
}
