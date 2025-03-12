package com.guudint.clickargo.clictruck.sage.dao;

import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.sage.model.VCkSageBooking;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkSageBookingDao extends GenericDao<VCkSageBooking, String> {
	
	public List<VCkSageBooking> findByDate(Date beginDate, Date endDate) throws Exception;
	
}
