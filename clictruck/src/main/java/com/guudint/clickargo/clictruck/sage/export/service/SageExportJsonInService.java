package com.guudint.clickargo.clictruck.sage.export.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportJson.ConsumerProviderBooking;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportJson.Item;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportOutJson;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportOutJson.Provider;
import com.guudint.clickargo.payment.dao.CkPaymentTxnDao;
import com.guudint.clickargo.payment.enums.PaymentTypes;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.ccm.util.Constant;

@Deprecated
@Service
public class SageExportJsonInService extends SageExportJsonService {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(SageExportJsonInService.class);

	@Autowired
	CkPaymentTxnDao ckPaymentTxnDao;

	protected List<SageExportBookingDto> createSageExportDto(String dateFolderName, Date beginTime, Date endTime)
			throws Exception {

		//
		List<TCkPaymentTxn> payTxtList = ckPaymentTxnDao.getByPaymentTypePaidAndStatus(
				PaymentTypes.VIRTUAL_ACCOUNT.getId(), beginTime, endTime, Constant.ACTIVE_STATUS);

		//LOG.info("payTxtList:" + payTxtList);

		List<SageExportBookingDto> sageExportBookingDtoList = new ArrayList<>();

		if (payTxtList != null && payTxtList.size() > 0) {

			for (int id = 0; id < payTxtList.size(); id++) {

				TCkPaymentTxn payTxn = payTxtList.get(id);
				String fileName = dateFolderName + "PI-" + String.format("%03d", id) + ".json";
				//
				String paiddDate = jobApproveDateTimeFormat.format(payTxn.getPtxDtPaid());

				Provider provider = getProvider(payTxn);

				long totalCompute = getTotal(provider);
				long total = payTxn.getPtxAmount().longValue();
				
				LOG.info("isEuqal total: " + payTxn.getPtxId() + "  " + total + "  =  " + totalCompute + " ??? "
						+ (total == totalCompute));

				CkCtSageExportOutJson sageExportBookingJson = new CkCtSageExportOutJson(SAGE_EXPORT_JSON_SERVICE,
						SAGE_EXPORT_JSON_TYPE_PAY_IN, payTxn.getPtxId(), paiddDate, total, provider);

				//
				SageExportBookingDto bookingDto = new SageExportBookingDto(id, fileName, sageExportBookingJson);

				// convert to Json String
				bookingDto.jsonStr = objectMapping.writeValueAsString(sageExportBookingJson);

				sageExportBookingDtoList.add(bookingDto);
			}
		}
		return sageExportBookingDtoList;
	}

	private Provider getProvider(TCkPaymentTxn payTxt) throws Exception {

		String srvRef = payTxt.getPtxSvcRef();
		String[] jobTruckIds = srvRef.split(",");

		String toSageId = null;

		List<Item> itemList = new ArrayList<>();

		for (String jobTruckId : jobTruckIds) {

			List<TCkCtPlatformInvoice> invList = ckCtPlatformInvoiceDao.findByJobId(jobTruckId);
			List<TCkCtDebitNote> debitNoteList = ckCtDebitNoteDao.findByJobId(jobTruckId);

			/////// CO ///////
			// String coAccnId = jobTruck.getTCkJob().getTCoreAccnByJobCoAccn().getAccnId();
			String coAccnId = payTxt.getTCoreAccnByPtxPayer().getAccnId();
			ConsumerProviderBooking consumer = generateConsumerProviderBooking(coAccnId, invList, debitNoteList, false);

			/////// TO ///////
			// String toAccnId = jobTruck.getTCkJob().getTCoreAccnByJobToAccn().getAccnId();
			// String toAccnId = payTxt.getTCoreAccnByPtxPayee().getAccnId();
			// ConsumerProviderBooking provider = generateConsumerProviderBooking(toAccnId,
			/////// invList, debitNoteList);

			toSageId = consumer.id;

			itemList.add(new Item(consumer.debitNote, consumer.invoice));
		}

		return new Provider(toSageId, itemList);
	}

}
