package com.guudint.clickargo.clictruck.dsv.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clicdo.common.service.CkCtCommonService;
import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService.TripAttachTypeEnum;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.job.model.TCkJob;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

@Service
public class DsvJobPhotoService {

	private static Logger log = Logger.getLogger(TripMobileService.class);

	@Autowired
	private CkCtTripAttachDao ckCtTripAttachDao;

	@Autowired
	private CkCtTripDao ckCtTripDao;

	@Autowired
	private CkCtCommonService ckCtCommonService;
	@Autowired
	CkCtShipmentDao shipmentDao;

	public List<TCkCtTripAttach> listImages(String jobTruckId) throws Exception {

		// TCkCtTrip
		List<TCkCtTrip> tripList = ckCtTripDao.findByJobId(jobTruckId);

		if (tripList == null || tripList.size() == 0) {
			throw new Exception("Fail to find trip by jobTruckId: " + jobTruckId);
		}
		TCkCtTrip trip = tripList.get(0);

		// TCkCtTripAttach
		List<TCkCtTripAttach> tripPickUpAttachList = ckCtTripAttachDao.findByTrIdAndAtyId(trip.getTrId(),
				TripAttachTypeEnum.PHOTO_PICKUP.name());

		List<TCkCtTripAttach> tripDropOffAttachList = ckCtTripAttachDao.findByTrIdAndAtyId(trip.getTrId(),
				TripAttachTypeEnum.PHOTO_DROPOFF.name());

		// merge to 1 list
		List<TCkCtTripAttach> imageList = new ArrayList<>();

		if (tripPickUpAttachList != null) {
			imageList.addAll(tripPickUpAttachList);
		}

		if (tripDropOffAttachList != null) {
			imageList.addAll(tripDropOffAttachList);
		}
		log.info("jobTruckId: " + jobTruckId + " imageList.size() " + imageList.size());
		return imageList;
	}

	public String mergeImage2Pdf(List<String> imagePaths, TCkJobTruck jobTruck, String messageId) throws Exception {


		Date deliverDate = new Date();
		
		TCkJob ckJob = jobTruck.getTCkJob();
		if(ckJob != null && ckJob.getTCkRecordDate() != null 
				&& ckJob.getTCkRecordDate().getRcdDtComplete() != null ) {
			deliverDate = ckJob.getTCkRecordDate().getRcdDtComplete();
		}
		
		String phonePdfPath = ckCtCommonService.getCkCtAttachmentPathJob(jobTruck.getJobId(), true);
		String phonePdfFileName = this.getPhonePdfFileName(messageId, deliverDate);

		String file = phonePdfPath + File.separator + phonePdfFileName;

		this.mergeImage2Pdf(imagePaths, file);

		return file;
	}

	/**
	 * 20211005_122447_SWRO0007149_POD.pdf 20211105_111844_SSIN0252132-NSH2_POD.pdf
	 * 
	 * @param messageId
	 * @return
	 */
	private String getPhonePdfFileName(String messageId, Date date) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

		return String.format("%s_%s_PHO.pdf", dateFormat.format(date), messageId);
	}

	/**
	 * Merge images to PDF file
	 * 
	 * @param sourceImagesPath
	 * @param targePdfPath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void mergeImage2Pdf(List<String> sourceImagesPath, String targePdfPath)
			throws FileNotFoundException, IOException {

		try (PdfWriter writer = new PdfWriter(targePdfPath);
				PdfDocument pdf = new PdfDocument(writer);
				Document document = new Document(pdf)) {

			for (String imagePath : sourceImagesPath) {
				Image img = new Image(ImageDataFactory.create(imagePath));
				document.add(img);
			}
		}
	}
}
