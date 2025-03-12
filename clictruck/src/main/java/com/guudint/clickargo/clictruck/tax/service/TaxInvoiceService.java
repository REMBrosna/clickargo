package com.guudint.clickargo.clictruck.tax.service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import com.guudint.clickargo.clictruck.attachments.service.ICtAttachmentService;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.event.AbstractJobEvent;
import com.guudint.clickargo.job.service.AbstractJobService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.tax.dao.CkTaxInvoiceDao;
import com.guudint.clickargo.tax.dao.CkTaxReportDao;
import com.guudint.clickargo.tax.dto.CkTaxInvoice;
import com.guudint.clickargo.tax.dto.CkTaxReport;
import com.guudint.clickargo.tax.model.TCkTaxInvoice;
import com.guudint.clickargo.tax.model.TCkTaxReport;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.embed.TCoreAddress;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;

@Service
public class TaxInvoiceService extends AbstractJobService<CkTaxInvoice, TCkTaxInvoice, String>
		implements ICkConstant, ICtAttachmentService<CkTaxInvoice> {
	private static Logger LOG = Logger.getLogger(TaxInvoiceService.class);

	public TaxInvoiceService() {
		super("ckTaxInvoiceDao", "CK TAX INVOICE", "TCkTaxInvoice", "T_CK_TAX_INVOICE");
	}

	public static char TAX_INVOICE_STATUS_EXPORTED = 'E';
	public static char TAX_INVOICE_STATUS_COMPLETED = 'C';
	public static char TAX_INVOICE_STATUS_NEW = 'N';

	@Autowired
	protected CkTaxInvoiceDao ckTaxInvoiceDao;
	@Autowired
	private CkCtPlatformInvoiceDao ckCtPlatformInvoiceDao;
	@Autowired
	private CkTaxReportDao ckTaxReportDao;

	public String uploadTaxInvoice(MultipartFile file, Principal principal) throws Exception {
		String fileName = file.getOriginalFilename();
		String pattern = "\\d{15}-\\d{16}-\\d{15}-\\d{14}\\.\\w+";

		String fakturNumber = null;
		String jobId = null;
		String savePath = null;

		if (!Pattern.matches(pattern, fileName)) {
			throw new Exception("File name is incorrect!");
		}

		// Split by dash and file extension using regular expression
		String[] parts = fileName.split("-|\\.");

		// Extract individual parts
		// String part1 = parts[0]; // GLI tax number
		fakturNumber = parts[1]; // Faktur number
		// String part3 = parts[2]; // Customer tax's number
		// String part4 = parts[3]; // Datetime

		List<TCkTaxInvoice> tCkTaxInvoices = ckTaxInvoiceDao.findByFakturNumber(fakturNumber);
		if (tCkTaxInvoices.isEmpty()) {
			throw new Exception("Tax Invoice " + fakturNumber + " not found");
		}

		for (TCkTaxInvoice tCkTaxInvoice : tCkTaxInvoices) {
			if (!tCkTaxInvoice.getTiStatus().equals(TAX_INVOICE_STATUS_EXPORTED)) {
				throw new Exception("Tax Invoice " + fakturNumber + " not found or already completed");
			}
			jobId = tCkTaxInvoice.getTiJobNo();
			if (jobId != null) {
				try {
					// Save the file to the desired location
					savePath = coreSysparamDao.find(CtConstant.KEY_ATTCH_BASE_LOCATION).getSysVal() + jobId + "/"
							+ fileName;
					byte[] fileData = file.getBytes();
					FileUtil.saveAttachment(jobId, fileName, fileData);
					tCkTaxInvoice.setTiDoc(savePath);
					tCkTaxInvoice.setTiStatus(TAX_INVOICE_STATUS_COMPLETED);
					tCkTaxInvoice.setTiDtLupd(new Date());
					tCkTaxInvoice.setTiUidLupd(principal.getUserAccnId());
					ckTaxInvoiceDao.update(tCkTaxInvoice);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		}

		return savePath;
	}

	@Override
	public CkTaxInvoice newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.info("newObj");
		CkTaxInvoice ckTaxInvoice = new CkTaxInvoice();
		ckTaxInvoice.setTCoreAccn(new CoreAccn());
		return ckTaxInvoice;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CkTaxInvoice deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isBlank(id)) {
			throw new ParameterException("param id null or empty");
		}
		if (principal == null) {
			throw new ParameterException("param principal null or empty");
		}
		try {
			TCkTaxInvoice tckCkTaxInvoice = dao.find(id);
			if (tckCkTaxInvoice == null) {
				throw new EntityNotFoundException("id::" + id);
			}
			CkTaxInvoice ckTaxInvoice = dtoFromEntity(tckCkTaxInvoice);
			return delete(ckTaxInvoice, principal);
		} catch (Exception e) {
			LOG.error(e);
			throw new ProcessingException(e);
		}

	}

	@Override
	public List<CkTaxInvoice> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkTaxInvoice ckTaxInvoice = whereDto(filterRequest);
		if (ckTaxInvoice == null) {
			throw new ProcessingException("whereDto null");
		}
		filterRequest.setTotalRecords(countByAnd(ckTaxInvoice));
		List<CkTaxInvoice> ckTaxInvoices = new ArrayList<>();
		try {
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkTaxInvoice> tCkTaxInvoices = findEntitiesByAnd(ckTaxInvoice, "from TCkTaxInvoice o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkTaxInvoice tCkTaxInvoice : tCkTaxInvoices) {
				CkTaxInvoice dto = dtoFromEntity(tCkTaxInvoice);
				if (dto != null) {
					ckTaxInvoices.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckTaxInvoices;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = true)
	public CkTaxInvoice findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.info("findById");
		if (StringUtils.isBlank(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkTaxInvoice tCkTaxInvoice = dao.find(id);
			if (tCkTaxInvoice == null) {
				throw new EntityNotFoundException("id::" + id);
			}
			initEnity(tCkTaxInvoice);
			return dtoFromEntity(tCkTaxInvoice);
		} catch (Exception e) {
			LOG.error(e);
			throw new ProcessingException(e);
		}
	}

	@Override
	protected Class<?>[] _validateGroupClass(JobEvent jobEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void _auditEvent(JobEvent jobEvent, CkTaxInvoice dto, Principal principal) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void _auditError(JobEvent jobEvent, CkTaxInvoice ckTaxInvoice, Exception ex, Principal principal) {
		// TODO Auto-generated method stub

	}

	@Override
	protected AbstractJobEvent<CkTaxInvoice> _getJobEvent(JobEvent jobEvent, CkTaxInvoice ckTaxInvoice,
			Principal principal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkTaxInvoice _newJob(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkTaxInvoice _createJob(CkTaxInvoice ckTaxInvoice, CkJob parentJob, Principal principal)
			throws ParameterException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkTaxInvoice _submitJob(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkTaxInvoice _rejectJob(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkTaxInvoice _cancelJob(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkTaxInvoice _confirmJob(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkTaxInvoice _payJob(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkTaxInvoice _paidJob(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkTaxInvoice _completeJob(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String formatOrderBy(String attributes) throws Exception {
        LOG.debug("formatOrderBy");
        String attribute = attributes;

        if (StringUtils.contains(attribute, "tcoreAccn"))
            attribute = attribute.replace("tcoreAccn", "TCoreAccn");

        return attribute;
	}

	@Override
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return LOG;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkTaxInvoice dtoFromEntity(TCkTaxInvoice tCkTaxInvoice) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkTaxInvoice == null) {
			throw new ParameterException("param entity null");
		}
		CkTaxInvoice ckTaxInvoice = new CkTaxInvoice(tCkTaxInvoice);
		ckTaxInvoice
				.setTCoreAccn(tCkTaxInvoice.getTCoreAccn() == null ? null : new CoreAccn(tCkTaxInvoice.getTCoreAccn()));
		return ckTaxInvoice;
	}

	@Override
	protected TCkTaxInvoice entityFromDTO(CkTaxInvoice ckTaxInvoice) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckTaxInvoice == null) {
			throw new ParameterException("param dto null");
		}
		TCkTaxInvoice tCkTaxInvoice = new TCkTaxInvoice(ckTaxInvoice);
		ckTaxInvoice
				.setTCoreAccn(ckTaxInvoice.getTCoreAccn() == null ? null : new CoreAccn(tCkTaxInvoice.getTCoreAccn()));
		return tCkTaxInvoice;
	}

	@Override
	protected String entityKeyFromDTO(CkTaxInvoice ckTaxInvoice) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckTaxInvoice == null) {
			throw new ParameterException("dto param null");
		}
		return ckTaxInvoice.getTiId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkTaxInvoice ckTaxInvoice)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		if (ckTaxInvoice == null) {
			throw new ParameterException("param dto null");
		}
		if (ckTaxInvoice.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocal null");
		}
		return ckTaxInvoice.getCoreMstLocale();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkTaxInvoice ckTaxInvoice)
			throws ParameterException, ProcessingException {
		if (ckTaxInvoice == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();

		if (StringUtils.isNotBlank(ckTaxInvoice.getTiId())) {
			parameters.put("tiId", "%" + ckTaxInvoice.getTiId() + "%");
		}

		if (StringUtils.isNotBlank(ckTaxInvoice.getTiService())) {
			parameters.put("tiService", "%" + ckTaxInvoice.getTiService() + "%");
		}

		if (StringUtils.isNotBlank(ckTaxInvoice.getTiInvNo())) {
			parameters.put("tiInvNo", ckTaxInvoice.getTiInvNo());
		}

		if (StringUtils.isNotBlank(ckTaxInvoice.getTiNo())) {
			parameters.put("tiNo", ckTaxInvoice.getTiNo());
		}

		Optional<CoreAccn> opCoreAccn = Optional.of(ckTaxInvoice.getTCoreAccn());
		if (opCoreAccn.isPresent()) {
			if (StringUtils.isNotBlank(opCoreAccn.get().getAccnId())) {
				parameters.put("toAccnId", opCoreAccn.get().getAccnId());
			}
			if (StringUtils.isNotBlank(opCoreAccn.get().getAccnName())) {
				parameters.put("toAccnName", "%" + opCoreAccn.get().getAccnName() + "%");
			}
		}

		if (ckTaxInvoice.getTiInvDtIssue() != null) {
			parameters.put("tiInvDtIssue", sdf.format(ckTaxInvoice.getTiInvDtIssue()));
		}

		if (ckTaxInvoice.getTiDtCreate() != null) {
			parameters.put("tiDtCreate", sdf.format(ckTaxInvoice.getTiDtCreate()));
		}

		if (ckTaxInvoice.getTiDtLupd() != null) {
			parameters.put("tiDtLupd", sdf.format(ckTaxInvoice.getTiDtLupd()));
		}

		if (ckTaxInvoice.getTiStatus() != null) {
			parameters.put("tiStatus", ckTaxInvoice.getTiStatus());
			parameters.put("validStatus", ckTaxInvoice.getTiStatus());
		} else {
			if (ckTaxInvoice.getHistory() != null && ckTaxInvoice.getHistory().equalsIgnoreCase("default")) {
				parameters.put("validStatus", Arrays.asList(TAX_INVOICE_STATUS_EXPORTED));
			} else if (ckTaxInvoice.getHistory() != null && ckTaxInvoice.getHistory().equalsIgnoreCase("history")) {
				parameters.put("validStatus", Arrays.asList(TAX_INVOICE_STATUS_COMPLETED));
			}
		}

		return parameters;
	}

	@Override
	protected String getWhereClause(CkTaxInvoice ckTaxInvoice, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (ckTaxInvoice == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();

		if (StringUtils.isNotBlank(ckTaxInvoice.getTiId())) {
			condition.append(getOperator(wherePrinted) + "o.tiId" + CONTAIN + "tiId");
			wherePrinted = true;
		}

		if (StringUtils.isNotBlank(ckTaxInvoice.getTiService())) {
			condition.append(getOperator(wherePrinted) + "o.tiService" + CONTAIN + "tiService");
			wherePrinted = true;
		}

		if (StringUtils.isNotBlank(ckTaxInvoice.getTiInvNo())) {
			condition.append(getOperator(wherePrinted) + "o.tiInvNo" + EQUAL + "tiInvNo");
			wherePrinted = true;
		}

		if (StringUtils.isNotBlank(ckTaxInvoice.getTiNo())) {
			condition.append(getOperator(wherePrinted) + "o.tiNo" + EQUAL + "tiNo");
			wherePrinted = true;
		}

		Optional<CoreAccn> opCoreAccn = Optional.ofNullable(ckTaxInvoice.getTCoreAccn());
		if (opCoreAccn.isPresent()) {
			if (StringUtils.isNotBlank(opCoreAccn.get().getAccnId())) {
				condition.append(getOperator(wherePrinted)).append("o.TCoreAccn.accnId = :toAccnId");
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(opCoreAccn.get().getAccnName())) {
				condition.append(getOperator(wherePrinted)).append("o.TCoreAccn.accnName LIKE :toAccnName");
				wherePrinted = true;
			}
		}

		if (ckTaxInvoice.getTiInvDtIssue() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.tiInvDtIssue" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "tiInvDtIssue");
			wherePrinted = true;
		}

		if (ckTaxInvoice.getTiDtCreate() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.tiDtCreate" + ",'" + DateFormat.MySql.D_M_Y
					+ "')" + EQUAL + "tiDtCreate");
			wherePrinted = true;
		}

		if (ckTaxInvoice.getTiDtLupd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.tiDtLupd" + ",'" + DateFormat.MySql.D_M_Y
					+ "')" + EQUAL + "tiDtLupd");
			wherePrinted = true;
		}

		if (ckTaxInvoice.getTiStatus() != null) {
			condition.append(getOperator(wherePrinted) + "o.tiStatus" + CONTAIN + "tiStatus");
			wherePrinted = true;
		}

		condition.append(getOperator(wherePrinted) + "o.tiStatus" + " IN :validStatus");
		
		wherePrinted = true;
		condition.append(getOperator(wherePrinted) + "o.tiService = '" + ServiceTypes.CLICTRUCK.name() + "'");

		return condition.toString();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkTaxInvoice initEnity(TCkTaxInvoice tCkTaxInvoice) throws ParameterException, ProcessingException {
		if (tCkTaxInvoice != null) {
			Hibernate.initialize(tCkTaxInvoice.getTCoreAccn());
		}
		return tCkTaxInvoice;
	}

	@Override
	protected CkTaxInvoice preSaveUpdateDTO(TCkTaxInvoice tCkTaxInvoice, CkTaxInvoice ckTaxInvoice)
			throws ParameterException, ProcessingException {
		if (tCkTaxInvoice == null) {
			throw new ParameterException("param entity null");
		}
		if (ckTaxInvoice == null) {
			throw new ParameterException("param dto null");
		}
		ckTaxInvoice.setTiDtCreate(tCkTaxInvoice.getTiDtCreate());
		ckTaxInvoice.setTiUidCreate(tCkTaxInvoice.getTiUidCreate());
		return ckTaxInvoice;
	}

	@Override
	protected void preSaveValidation(CkTaxInvoice arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'preSaveValidation'");
	}

	@Override
	protected ServiceStatus preUpdateValidation(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkTaxInvoice setCoreMstLocale(CoreMstLocale coreMstLocale, CkTaxInvoice ckTaxInvoice)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		ckTaxInvoice.setCoreMstLocale(coreMstLocale);
		return ckTaxInvoice;
	}

	@Override
	protected TCkTaxInvoice updateEntity(ACTION action, TCkTaxInvoice tCkTaxInvoice, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.info("updateEntity");
		if (tCkTaxInvoice == null) {
			throw new ParameterException("param entity null");
		}
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		if (date == null) {
			throw new ParameterException("param date null");
		}
		Optional<String> optUserId = Optional.ofNullable(principal.getUserId());
		String userId = optUserId.isPresent() ? optUserId.get() : "SYS";
		switch (action) {
			case CREATE:
				tCkTaxInvoice.setTiUidCreate(userId);
				tCkTaxInvoice.setTiDtCreate(date);
				tCkTaxInvoice.setTiUidLupd(userId);
				tCkTaxInvoice.setTiDtLupd(date);
				break;
			case MODIFY:
				tCkTaxInvoice.setTiUidLupd(userId);
				tCkTaxInvoice.setTiDtLupd(date);
			default:
				break;
		}
		return tCkTaxInvoice;
	}

	@Override
	protected TCkTaxInvoice updateEntityStatus(TCkTaxInvoice tCkTaxInvoice, char status)
			throws ParameterException, ProcessingException {
		LOG.info("updateEntityStatus");
		if (tCkTaxInvoice == null) {
			throw new ParameterException("param entity null");
		}
		tCkTaxInvoice.setTiStatus(status);
		return tCkTaxInvoice;
	}

	@Override
	protected CkTaxInvoice whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (filterRequest == null) {
				throw new ParameterException("param filterRequest null");
			}
			SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
			CkTaxInvoice dto = new CkTaxInvoice();
			CoreAccn tCoreAccn = new CoreAccn();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;
				String attribute = "o." + entityWhere.getAttribute();
				if (attribute.equalsIgnoreCase("o.tiService"))
					dto.setTiService(opValue.get());
				else if (attribute.equalsIgnoreCase("o.tiInvNo"))
					dto.setTiInvNo(opValue.get());
				else if (attribute.equalsIgnoreCase("o.tiInvDtIssue"))
					dto.setTiInvDtIssue(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.tiDtCreate"))
					dto.setTiDtCreate(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.tiDtLupd"))
					dto.setTiDtLupd(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.tiNo"))
					dto.setTiNo(opValue.get());
				else if (attribute.equalsIgnoreCase("o.tiJobNo"))
					dto.setTiJobNo(opValue.get());
				else if (attribute.equalsIgnoreCase("o.tiStatus"))
					dto.setTiStatus((opValue.get() == null) ? null : opValue.get().charAt(0));
				else if (attribute.equalsIgnoreCase("o.history"))
					dto.setHistory(opValue.get());
				else if (attribute.equalsIgnoreCase("o.TCoreAccn.accnId"))
					tCoreAccn.setAccnId(opValue.get());
				else if (attribute.equalsIgnoreCase("o.TCoreAccn.accnName"))
					tCoreAccn.setAccnName(opValue.get());
			}

			dto.setTCoreAccn(tCoreAccn);

			return dto;
		} catch (ParameterException ex) {
			LOG.error("whereDto", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("whereDto", ex);
			throw new ProcessingException(ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void generateTaxReport(String date) throws Exception {
		List<TCkCtPlatformInvoice> tCkCtPlatformInvoices = ckCtPlatformInvoiceDao.findByInvDtIssue(date);
		Date now = new Date();
		for (TCkCtPlatformInvoice tCkCtPlatformInvoice : tCkCtPlatformInvoices) {
			TCkTaxInvoice tCkTaxInvoice = new TCkTaxInvoice();
			tCkTaxInvoice.setTiId(CkUtil.generateId(CkTaxInvoice.PREFIX_ID));
			tCkTaxInvoice.setTCoreAccn(tCkCtPlatformInvoice.getTCoreAccnByInvTo());
			tCkTaxInvoice.setTiAmt(tCkCtPlatformInvoice.getInvAmt());
			tCkTaxInvoice.setTiDtCreate(now);
			tCkTaxInvoice.setTiDtLupd(now);
			tCkTaxInvoice.setTiInvDtIssue(tCkCtPlatformInvoice.getInvDtIssue());
			tCkTaxInvoice.setTiInvNo(tCkCtPlatformInvoice.getInvNo());
			tCkTaxInvoice.setTiJobNo(tCkCtPlatformInvoice.getInvJobId());
			tCkTaxInvoice.setTiNo(tCkCtPlatformInvoice.getInvSageTaxNo());
			tCkTaxInvoice.setTiService(ServiceTypes.CLICTRUCK.getId());
			tCkTaxInvoice.setTiStatus('N');
			tCkTaxInvoice.setTiDoc("");
			tCkTaxInvoice.setTiUidCreate("SYS");
			tCkTaxInvoice.setTiUidLupd("SYS");
			ckTaxInvoiceDao.add(tCkTaxInvoice);
		}
		if (!tCkCtPlatformInvoices.isEmpty())
			generateReport();
	}

	public void generateReport() {
		try {
			DateUtil dateUtil = new DateUtil(new Date());
			List<TCkTaxInvoice> ckTaxInvoices = ckTaxInvoiceDao.findByServiceAndStatus(ServiceTypes.CLICTRUCK.name(), TAX_INVOICE_STATUS_NEW);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("efaktur");
			setHeaderTaxReport(sheet);
			setDetailTaxReport(sheet, ckTaxInvoices);
			File filePath = new File(FileUtil.getBaseAttachmentLocation() + dateUtil.toStringFormat(DateFormat.Java.DDMMYYYY) + ".xlsx");
			FileOutputStream outputStream = new FileOutputStream(filePath);
			workbook.write(outputStream);
			workbook.close();
			TCkTaxReport tCkTaxReport = new TCkTaxReport(CkUtil.generateId(CkTaxReport.PREFIX_ID));
			
			tCkTaxReport.setTrService(ServiceTypes.CLICTRUCK.name());
			tCkTaxReport.setTrDoc(filePath.getAbsolutePath());
			tCkTaxReport.setTrDtCreate(dateUtil.getDate());
			tCkTaxReport.setTrDtLupd(dateUtil.getDate());
			tCkTaxReport.setTrName(filePath.getName());
			tCkTaxReport.setTrUidCreate("SYS");
			tCkTaxReport.setTrUidLupd("SYS");
			tCkTaxReport.setTrStatus('A');
			tCkTaxReport.setTrNumRecords(ckTaxInvoices.size());
			ckTaxReportDao.add(tCkTaxReport);
			for (TCkTaxInvoice tCkTaxInvoice : ckTaxInvoices) {
				tCkTaxInvoice.setTiStatus('E');
				ckTaxInvoiceDao.update(tCkTaxInvoice);
			}
		} catch (Exception e) {
			LOG.error("Error generateReport", e);
		}
	}

	private void setHeaderTaxReport(XSSFSheet sheet) {
		List<String> FK_COLUMN_NAME = Arrays.asList("FK", "KD_JENIS_TRANSAKSI", "FG_PENGGANTI", "NOMOR_FAKTUR",
				"MASA_PAJAK", "TAHUN_PAJAK", "TANGGAL_FAKTUR", "NPWP", "NAMA", "ALAMAT_LENGKAP", "JUMLAH_DPP",
				"JUMLAH_PPN", "JUMLAH_PPNBM", "ID_KETERANGAN_TAMBAHAN", "FG_UANG_MUKA", "UANG_MUKA_DPP",
				"UANG_MUKA_PPN", "UANG_MUKA_PPNBM", "REFERENSI", "KODE_DOKUMEN_PENDUKUNG");
		List<String> LT_COLUMN_NAME = Arrays.asList("LT", "NPWP", "NAMA", "JALAN", "BLOK", "NOMOR", "RT", "RW",
				"KECAMATAN", "KELURAHAN", "KABUPATEN", "PROPINSI", "KODE_POS", "NOMOR_TELEPON");
		List<String> OF_COLUMN_NAME = Arrays.asList("OF", "KODE_OBJEK", "NAMA", "HARGA_SATUAN", "JUMLAH_BARANG",
				"HARGA_TOTAL", "DISKON", "DPP", "PPN", "TARIF_PPNBM", "PPNBM");
		int rowNumber = 0, columnNumber = 0;
		Row rowHeaderFk = sheet.createRow(rowNumber++);
		for (String columnName : FK_COLUMN_NAME) {
			rowHeaderFk.createCell(columnNumber++).setCellValue(columnName);
		}
		columnNumber = 0;
		Row rowHeaderLt = sheet.createRow(rowNumber++);
		for (String columnName : LT_COLUMN_NAME) {
			rowHeaderLt.createCell(columnNumber++).setCellValue(columnName);
		}
		columnNumber = 0;
		Row rowHeaderOf = sheet.createRow(rowNumber++);
		for (String columnName : OF_COLUMN_NAME) {
			rowHeaderOf.createCell(columnNumber++).setCellValue(columnName);
		}
	}

	private void setDetailTaxReport(XSSFSheet sheet, List<TCkTaxInvoice> tCkTaxInvoices) {
		int rowNumber = 3;
		for (TCkTaxInvoice tCkTaxInvoice : tCkTaxInvoices) {
			TCoreAccn tCoreAccn = Optional.ofNullable(tCkTaxInvoice.getTCoreAccn()).orElse(new TCoreAccn());
			BigDecimal amount = Optional.ofNullable(tCkTaxInvoice.getTiAmt()).orElse(BigDecimal.ZERO);
			BigDecimal totalItem = BigDecimal.ONE;
			String address = "";
			TCoreAddress tCoreAddress = Optional.ofNullable(tCoreAccn.getAccnAddr()).orElse(new TCoreAddress());
			if (StringUtils.isNotBlank(tCoreAddress.getAddrLn1())) {
				address += tCoreAddress.getAddrLn1() + " ";
			}
			// if (StringUtils.isNotBlank(tCoreAddress.getAddrLn2())) {
			// address += tCoreAddress.getAddrLn2() + " ";
			// }
			// if (StringUtils.isNotBlank(tCoreAddress.getAddrLn3())) {
			// address += tCoreAddress.getAddrLn3() + " ";
			// }
			// if (StringUtils.isNotBlank(tCoreAddress.getAddrCity())) {
			// address += tCoreAddress.getAddrCity() + " ";
			// }
			// if (StringUtils.isNotBlank(tCoreAddress.getAddrProv())) {
			// address += tCoreAddress.getAddrProv() + " ";
			// }
			fk(sheet, rowNumber++, tCkTaxInvoice, tCoreAccn, address.trim(), amount, totalItem);
			lt(sheet, rowNumber++, tCkTaxInvoice, tCoreAccn, address.trim(), tCoreAddress.getAddrPcode());
			of(sheet, rowNumber++, tCkTaxInvoice, amount, totalItem);
		}
	}

	private void fk(XSSFSheet sheet, int rowNumber, TCkTaxInvoice tCkTaxInvoice, TCoreAccn tCoreAccn, String address,
			BigDecimal amount, BigDecimal totalItem) {
		DateUtil invoiceDate = new DateUtil(tCkTaxInvoice.getTiInvDtIssue());
		Row rowFk = sheet.createRow(rowNumber);
		int columnNumber = 0;
		rowFk.createCell(columnNumber++).setCellValue("FK");
		Cell transactionType = rowFk.createCell(columnNumber++);
		transactionType.setCellValue("01");
		Cell replacementFlag = rowFk.createCell(columnNumber++);
		replacementFlag.setCellValue("0");
		Cell fakturNumber = rowFk.createCell(columnNumber++);
		String tiNo = Optional.ofNullable(tCkTaxInvoice.getTiNo()).orElse("").substring(3).replaceAll("\\.", "");
		fakturNumber.setCellValue(StringUtils.leftPad(tiNo, 13, "0"));
		Cell taxPeriod = rowFk.createCell(columnNumber++);
		taxPeriod.setCellValue(invoiceDate.getMonth());
		Cell taxYear = rowFk.createCell(columnNumber++);
		taxYear.setCellValue(invoiceDate.getYear());
		Cell fakturDate = rowFk.createCell(columnNumber++);
		fakturDate.setCellValue(invoiceDate.toStringFormat(DateFormat.Java.DD_MM_YYYY));
		Cell taxNumber2 = rowFk.createCell(columnNumber++);
		taxNumber2.setCellValue(tCoreAccn.getAccnCoyRegn());
		Cell customerName2 = rowFk.createCell(columnNumber++);
		customerName2.setCellValue(tCoreAccn.getAccnName());
		Cell customerAddress2 = rowFk.createCell(columnNumber++);
		customerAddress2.setCellValue(address);
		Cell totalAmountBeforeTax = rowFk.createCell(columnNumber++);
		totalAmountBeforeTax.setCellValue(String.valueOf(amount.multiply(totalItem).longValue()));
		Cell totalVat = rowFk.createCell(columnNumber++);
		totalVat.setCellValue(amount.multiply(totalItem).multiply(NumberUtil.toBigDecimal(0.11)).longValue());
		Cell totalPpnbm = rowFk.createCell(columnNumber++);
		totalPpnbm.setCellValue("0");
		Cell additionalRemark = rowFk.createCell(columnNumber++);
		additionalRemark.setCellValue("");
		Cell downPaymentFlag = rowFk.createCell(columnNumber++);
		downPaymentFlag.setCellValue("0");
		Cell totalDownPayment = rowFk.createCell(columnNumber++);
		totalDownPayment.setCellValue("0");
		Cell vatDownPayment = rowFk.createCell(columnNumber++);
		vatDownPayment.setCellValue("0");
		Cell ppnbmDownPayment = rowFk.createCell(columnNumber++);
		ppnbmDownPayment.setCellValue("0");
		Cell reference = rowFk.createCell(columnNumber++);
		reference.setCellValue(tCkTaxInvoice.getTiInvNo());
		Cell documentCode = rowFk.createCell(columnNumber++);
		documentCode.setCellValue("");
	}

	private void lt(XSSFSheet sheet, int rowNumber, TCkTaxInvoice tCkTaxInvoice, TCoreAccn tCoreAccn, String address,
			String addrPosCode) {
		Row rowLt = sheet.createRow(rowNumber);
		int columnNumber = 0;
		rowLt.createCell(columnNumber++).setCellValue("LT");
		Cell taxNumber = rowLt.createCell(columnNumber++);
		String npwp = Optional.ofNullable(tCoreAccn.getAccnCoyRegn()).orElse("");
		taxNumber.setCellValue(StringUtils.leftPad(npwp, 15, "0"));
		Cell customerName = rowLt.createCell(columnNumber++);
		customerName.setCellValue(tCoreAccn.getAccnName());
		Cell customerAddress = rowLt.createCell(columnNumber++);
		customerAddress.setCellValue(address);
		Cell block = rowLt.createCell(columnNumber++);
		block.setCellValue("-");
		Cell number = rowLt.createCell(columnNumber++);
		number.setCellValue("-");
		Cell rt = rowLt.createCell(columnNumber++);
		rt.setCellValue("-");
		Cell rw = rowLt.createCell(columnNumber++);
		rw.setCellValue("-");
		Cell district = rowLt.createCell(columnNumber++);
		district.setCellValue("-");
		Cell village = rowLt.createCell(columnNumber++);
		village.setCellValue("-");
		Cell regency = rowLt.createCell(columnNumber++);
		regency.setCellValue("-");
		Cell province = rowLt.createCell(columnNumber++);
		province.setCellValue("-");
		Cell posCode = rowLt.createCell(columnNumber++);
		posCode.setCellValue(addrPosCode);
		Cell phoneNumber = rowLt.createCell(columnNumber++);
		phoneNumber.setCellValue("-");
		rowLt.createCell(columnNumber++).setCellValue("");
		rowLt.createCell(columnNumber++).setCellValue("");
		rowLt.createCell(columnNumber++).setCellValue("");
		rowLt.createCell(columnNumber++).setCellValue("");
		rowLt.createCell(columnNumber++).setCellValue("");
		rowLt.createCell(columnNumber++).setCellValue("");
	}

	private void of(XSSFSheet sheet, int rowNumber, TCkTaxInvoice tCkTaxInvoice, BigDecimal amount,
			BigDecimal totalItem) {
		Row rowOf = sheet.createRow(rowNumber);
		int columnNumber = 0;
		rowOf.createCell(columnNumber++).setCellValue("OF");
		Cell objectCode = rowOf.createCell(columnNumber++);
		objectCode.setCellValue("0");
		Cell serviceName = rowOf.createCell(columnNumber++);
		serviceName.setCellValue("PLATFORM FEE");
		Cell unitPrice = rowOf.createCell(columnNumber++);
		unitPrice.setCellValue(amount.longValue());
		Cell quantity = rowOf.createCell(columnNumber++);
		quantity.setCellValue(totalItem.longValue());
		Cell totalAmount = rowOf.createCell(columnNumber++);
		totalAmount.setCellValue(amount.multiply(totalItem).longValue());
		Cell discount = rowOf.createCell(columnNumber++);
		discount.setCellValue("0");
		Cell taxBasis = rowOf.createCell(columnNumber++);
		taxBasis.setCellValue(amount.longValue());
		Cell vat = rowOf.createCell(columnNumber++);
		vat.setCellValue(amount.multiply(NumberUtil.toBigDecimal(0.11)).longValue());
		Cell ppnbmTariff = rowOf.createCell(columnNumber++);
		ppnbmTariff.setCellValue("0");
		Cell ppnbm = rowOf.createCell(columnNumber++);
		ppnbm.setCellValue("0");
		rowOf.createCell(columnNumber++).setCellValue("");
		rowOf.createCell(columnNumber++).setCellValue("");
		rowOf.createCell(columnNumber++).setCellValue("");
		rowOf.createCell(columnNumber++).setCellValue("");
		rowOf.createCell(columnNumber++).setCellValue("");
		rowOf.createCell(columnNumber++).setCellValue("");
		rowOf.createCell(columnNumber++).setCellValue("");
		rowOf.createCell(columnNumber++).setCellValue("");
		rowOf.createCell(columnNumber++).setCellValue("");
	}

	@Override
	public String getAttachment(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		LOG.debug("getAttachment");
		try {
			if (StringUtils.isBlank(dtoId))
				throw new ParameterException("param dtoId null or empty");

			TCkTaxInvoice tCkTaxInvoice = ckTaxInvoiceDao.find(dtoId);
			if (tCkTaxInvoice == null)
				throw new EntityNotFoundException("entity not found: " + dtoId);
			if (!StringUtils.isBlank(tCkTaxInvoice.getTiDoc())) {

				String base64ContentString = Base64Utils
						.encodeToString(IOUtils.toByteArray(Files.newInputStream(Paths.get(tCkTaxInvoice.getTiDoc()))));

				return base64ContentString;
			}
		} catch (Exception ex) {
			throw ex;
		}

		return null;

	}

	@Override
	public CkTaxInvoice getAttachmentObj(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Map<String, Object> getAttachmentByJobId(String jobId, Principal principal) throws ParameterException {
		LOG.debug("getAttachmentByJobId");
		if (StringUtils.isBlank(jobId)) {
			throw new ParameterException("param jobId null or empty");
		}

		Map<String, Object> attachment = new HashMap<>();
		try {
			CoreAccn coreAccn = Optional.ofNullable(principal.getCoreAccn()).orElse(new CoreAccn());
			for (TCkTaxInvoice tCkTaxInvoice : ckTaxInvoiceDao.findByJobId(jobId)) {
				if (StringUtils.isNotBlank(tCkTaxInvoice.getTiDoc())
						&& tCkTaxInvoice.getTCoreAccn().getAccnId().equals(coreAccn.getAccnId())) {
					attachment.put("filename", FilenameUtils.getName(tCkTaxInvoice.getTiDoc()));
					attachment.put("data", FileUtil.toBase64(tCkTaxInvoice.getTiDoc()));
					break;
				}
			}
		} catch (Exception e) {
			LOG.error("Error getAttachmentByJobId", e);
		}
		return attachment;
	}

	@Override
	public Map<String, Object> getAttachment2(String param) throws ParameterException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAttachment2'");
	}
}
