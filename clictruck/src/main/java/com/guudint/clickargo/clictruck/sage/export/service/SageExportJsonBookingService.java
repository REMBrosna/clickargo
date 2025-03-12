package com.guudint.clickargo.clictruck.sage.export.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportBookingJson;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportJson.ConsumerProviderBooking;

@Deprecated
@Service
public class SageExportJsonBookingService extends SageExportJsonService{

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(SageExportJsonBookingService.class);

	protected List<SageExportBookingDto> createSageExportDto(String dateFolderName, Date beginTime, Date endTime) throws Exception {

		// 1: fetch approved job at yesterday
		List<TCkJobTruck> jobTruckList = ctMiscService.getJobTruckList(beginTime, endTime);
		LOG.info("jobTruckList:" + jobTruckList);
		
		List<SageExportBookingDto> sageExportBookingDtoList = new ArrayList<>();

		if (jobTruckList != null && jobTruckList.size() > 0) {

			for (int id = 0; id < jobTruckList.size(); id++) {

				TCkJobTruck jobTruck = jobTruckList.get(id);
				String fileName = dateFolderName + "BK-" + String.format("%03d", id) + ".json";
				//
				String approvedDate = jobApproveDateTimeFormat
						.format(jobTruck.getTCkJob().getTCkRecordDate().getRcdDtBillApproved());

				List<TCkCtPlatformInvoice> invList = ckCtPlatformInvoiceDao.findByJobId(jobTruck.getJobId());
				List<TCkCtDebitNote> debitNoteList = ckCtDebitNoteDao.findByJobId(jobTruck.getJobId());

				/////// CO ///////
				String coAccnId = jobTruck.getTCkJob().getTCoreAccnByJobCoAccn().getAccnId();
				ConsumerProviderBooking consumer = generateConsumerProviderBooking(coAccnId, invList,
						debitNoteList, false);

				/////// TO ///////
				String toAccnId = jobTruck.getTCkJob().getTCoreAccnByJobToAccn().getAccnId();
				ConsumerProviderBooking provider = generateConsumerProviderBooking(toAccnId, invList,
						debitNoteList, true);

				CkCtSageExportBookingJson sageExportBookingJson = new CkCtSageExportBookingJson(
						SAGE_EXPORT_JSON_SERVICE, SAGE_EXPORT_JSON_TYPE_BOOK, jobTruck.getJobId(), approvedDate,
						consumer, provider);

				//
				SageExportBookingDto bookingDto = new SageExportBookingDto(id, fileName,
						sageExportBookingJson);

				// convert to Json String
				bookingDto.jsonStr = objectMapping.writeValueAsString(sageExportBookingJson);

				sageExportBookingDtoList.add(bookingDto);
			}
		}
		return sageExportBookingDtoList;
	}
}
