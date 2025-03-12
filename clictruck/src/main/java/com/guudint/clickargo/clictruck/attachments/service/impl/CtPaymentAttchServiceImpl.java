package com.guudint.clickargo.clictruck.attachments.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.ResourceUtils;

import com.guudint.clickargo.clictruck.attachments.service.ICtAttachmentService;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.dto.InvoiceFields;
import com.guudint.clickargo.clictruck.dto.InvoiceSubLine;
import com.guudint.clickargo.clictruck.finacing.service.AbstractPaymentUtil;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPaymentDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPayment;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPayment;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.docs.util.JasperDocsUtil;
import com.guudint.clickargo.payment.dao.CkPaymentTxnDao;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.guudint.clickargo.tax.dao.CkTaxInvoiceDao;
import com.guudint.clickargo.tax.model.TCkTaxInvoice;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.embed.TCoreAddress;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class CtPaymentAttchServiceImpl extends AbstractPaymentUtil implements ICtAttachmentService<CkCtPayment> {

	// Static Attributes
	///////////////////
	private static Logger log = Logger.getLogger(CtPaymentAttchServiceImpl.class);

	@Autowired
	private CkCtPaymentDao ckCtPaymentDao;
	@Autowired
	private CkTaxInvoiceDao ckTaxInvoiceDao;
	@Autowired
	private CkPaymentTxnDao ckPaymentTxnDao;

	@Override
	@Transactional
	public String getAttachment(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		log.debug("getAttachment");
		try {
			if (StringUtils.isBlank(dtoId))
				throw new ParameterException("param dtoId null or empty");

			TCkCtPayment ctPayment = ckCtPaymentDao.find(dtoId);

			if (ctPayment == null)
				throw new EntityNotFoundException("entity not found: " + dtoId);
			if (!StringUtils.isBlank(ctPayment.getCtpAttach())) {
				String base64ContentString = Base64Utils
						.encodeToString(IOUtils.toByteArray(Files.newInputStream(Paths.get(ctPayment.getCtpAttach()))));

				return base64ContentString;
			}
		} catch (Exception ex) {
			throw ex;
		}

		return null;
	}

	@Override
	public CkCtPayment getAttachmentObj(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getAttachmentByJobId(String jobId, Principal principal) throws ParameterException {
		return new HashMap<>();
	}

	@Override
	@Transactional
	public Map<String, Object> getAttachment2(String param) throws ParameterException {
		if (StringUtils.isBlank(param)) {
			throw new ParameterException("Param null");
		}
		Map<String, Object> attachment = new HashMap<>();
		String tempDirectory = "", piPath = "", zipFilePath = "";
		try {
			List<TCkCtPayment> tCkCtPayments = ckCtPaymentDao.findByPtxId(param);
			List<File> files = new ArrayList<>();
			for (TCkCtPayment tCkCtPayment : tCkCtPayments) {
				if (StringUtils.isNotBlank(tCkCtPayment.getCtpAttach())) {
					files.add(new File(tCkCtPayment.getCtpAttach()));
				}
				List<TCkTaxInvoice> tCkTaxInvoices = ckTaxInvoiceDao.findByJobIdAndInvNo(tCkCtPayment.getCtpJob(),
						tCkCtPayment.getCtpRef());
				for (TCkTaxInvoice tCkTaxInvoice : tCkTaxInvoices) {
					if (StringUtils.isNotBlank(tCkTaxInvoice.getTiDoc())) {
						files.add(new File(tCkTaxInvoice.getTiDoc()));
					}
				}
			}
			tempDirectory = FileUtil.getBaseAttachmentLocation() + param;
			FileUtil.createDirectory(tempDirectory);
			for (File file : files) {
				FileUtil.copy(file.getAbsolutePath(), tempDirectory + "/" + file.getName());
			}
			files.clear();
			piPath = generatePaymentInstruction(param);
			if (!piPath.isEmpty()) {
				files.add(new File(piPath));
				files.add(new File(tempDirectory));
			}
			zipFilePath = FileUtil.zipFiles(files, param + ".zip");
			attachment.put("filename", FilenameUtils.getName(zipFilePath));
			attachment.put("data", FileUtil.toBase64(zipFilePath));
		} catch (Exception e) {
			attachment.put("filename", "");
			attachment.put("data", "");
			log.error("Error getAttachment2", e);
		}
		try {
			FileUtil.delete(zipFilePath);
			FileUtil.delete(piPath);
			FileUtil.delete(tempDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return attachment;
	}

	private String generatePaymentInstruction(String ptxId) {
		try {
			Map<String, Object> param = new HashMap<>();
			param.put("ptx_id", ptxId);
			TCkPaymentTxn tCkPaymentTxn = ckPaymentTxnDao.find(ptxId);
			if (tCkPaymentTxn != null) {
				TCoreAccn recipientAccn = tCkPaymentTxn.getTCoreAccnByPtxPayer();
				param.put("recipient_name", recipientAccn.getAccnName());
				String address = "";
				TCoreAddress coreAddress = recipientAccn.getAccnAddr();
				if (coreAddress != null) {
					address += Optional.ofNullable(coreAddress.getAddrLn1()).orElse("");
					address += Optional.ofNullable(coreAddress.getAddrLn2()).orElse("");
					address += Optional.ofNullable(coreAddress.getAddrLn3()).orElse("");
				}
				param.put("recipient_address", address);
				param.put("inv_va_transid", tCkPaymentTxn.getPtxPayerBankAccn());
				param.put("svcName", "CLICTRUCK");
				param.put("ptx_amount", NumberUtil.currencyFormat("Rp.", tCkPaymentTxn.getPtxAmount()));
				DateUtil dateUtil = new DateUtil(tCkPaymentTxn.getPtxDtCreate());
				param.put("ptx_dt_create", dateUtil.toStringFormat(DateFormat.Java.DD_MMMM_YYYY));
				Date dueDate = ckPaymentTxnDao.findDueDate(ptxId);
				if(dueDate != null){
					dateUtil.setDate(dueDate);
					param.put("ptx_due_create", dateUtil.toStringFormat(DateFormat.Java.DD_MMMM_YYYY));
				}
				String jrxmlBasePath = coreSysparamDao.find(CtConstant.KEY_JRXML_BASE_PATH).getSysVal();
				String jrxmlPIPath = coreSysparamDao.find(CtConstant.KEY_JRXML_PI_PATH).getSysVal();
				String jrxmlPISubPath = coreSysparamDao.find(CtConstant.KEY_JRXML_PI_SUB_PATH).getSysVal();
				String basePath = coreSysparamDao.find(CtConstant.KEY_ATTCH_BASE_LOCATION).getSysVal();
				String logoPath = coreSysparamDao.find(CtConstant.CLICTRUCK_JRXML_LOGO_PATH).getSysVal();
				File subReportFile = ResourceUtils.getFile(jrxmlBasePath.concat(jrxmlPISubPath));
				param.put("gli_logo", jrxmlBasePath + logoPath);
				param.put("invoiceSub", subReportFile);
				Map<String, Object> mapParam = new HashMap<>();
				mapParam.put("PARAM_INFO", param);
				InvoiceFields invoiceFields = new InvoiceFields();
				int seq = 1;
				for (TCkCtPayment tCkCtPayment : ckCtPaymentDao.findByPtxId(ptxId)) {
					InvoiceSubLine invoiceItem = new InvoiceSubLine();
					invoiceItem.setSeqNo(seq++ + "");
					invoiceItem.setCurrency("Rp.");
					invoiceItem.setAmount(NumberUtil.currencyFormat("Rp.", tCkCtPayment.getCtpAmount()));
					invoiceItem.setItemDesc(tCkCtPayment.getCtpItem());
					invoiceFields.getInvoiceLines().add(invoiceItem);
				}
				List<InvoiceFields> invoiceLines = Arrays.asList(invoiceFields);
				JRBeanCollectionDataSource collectionDataSource = new JRBeanCollectionDataSource(invoiceLines);
				String filename = ptxId + ".pdf";
				byte[] data = JasperDocsUtil.createPDF(jrxmlBasePath, jrxmlPIPath, mapParam, collectionDataSource);
				return FileUtil.saveAttachment("", basePath, filename, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
		return "";
	}
}
