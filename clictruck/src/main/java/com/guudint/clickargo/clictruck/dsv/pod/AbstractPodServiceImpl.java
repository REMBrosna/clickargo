package com.guudint.clickargo.clictruck.dsv.pod;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clicdo.common.service.CkCtCommonService;
import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;
import com.guudint.clickargo.clictruck.dsv.service.IPodService;
import com.guudint.clickargo.clictruck.dsv.service.impl.DsvShipmentService;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

@Service
public abstract class AbstractPodServiceImpl implements IPodService{

	private static Logger log = Logger.getLogger(AbstractPodServiceImpl.class);

	protected static SimpleDateFormat yyyyMMdd_HHmmssSDF = new SimpleDateFormat("yyyyMMdd_HHmmss");
	protected static SimpleDateFormat yyyy_MM_dd_HHmmSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	protected static SimpleDateFormat ddMMMyySDF = new SimpleDateFormat("dd-MMM-yy");
	protected static SimpleDateFormat HHmmSDF = new SimpleDateFormat("HH:mm");
	
	@Autowired
	protected CkJobTruckExtDao ckJobTruckExtDao;
	@Autowired
	protected DsvShipmentService auxiliary;
	@Autowired
	protected CkCtTripAttachDao attachDao;
	@Autowired
	protected CkJobTruckDao ckJobTruckDao;
	@Autowired
	protected CkCtShipmentDao shipmentDao;
	@Autowired
	protected CkCtCommonService ckCtCommonService;
	
	@Autowired
	protected CkCtTripDao ckCtTripDao;
	
	
	protected List<TCkJobTruckExt> loadJObTruckExt(String jobId) throws Exception {
		
		List<TCkJobTruckExt> ckJobTruckExt = ckJobTruckExtDao.findAllByJobId(jobId);

		return ckJobTruckExt;
	}
	
	
	protected JasperReport getReportTemplate(String basePath, String jrXmlString) throws Exception {
		return JasperCompileManager.compileReport(basePath.concat(jrXmlString));
	}
	
}
