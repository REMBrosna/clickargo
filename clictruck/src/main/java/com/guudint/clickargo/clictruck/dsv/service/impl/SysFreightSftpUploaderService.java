package com.guudint.clickargo.clictruck.dsv.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clicdo.common.service.CkCtCommonService;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.job.model.TCkJob;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.jcraft.jsch.*;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.util.sftp.model.SFTPConfig;
import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import sun.net.ftp.FtpDirEntry;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SysFreightSftpUploaderService {
    private static final SimpleDateFormat yyyyMMdd_HHmmssSDF = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SysFreightSftpUploaderService.class);
    @Autowired
    @Qualifier("coreSysparamDao")
    protected GenericDao<TCoreSysparam, String> coreSysparamDao;
    @Autowired
    private CkCtTripAttachDao ckCtTripAttachDao;
    @Autowired
    private CkCtTripDao ckCtTripDao;
    @Autowired
    private CkJobTruckDao jobTruckDao;
    @Autowired
    private CkCtCommonService ckCtCommonService;

    public void processSysFreightUploadFiles(TCkJobTruck jobTruck, String ePODFilePath) throws Exception {

        // Update EPod Date in TCkJobTruck
        Date now = new Date();
        jobTruck.setJobDtEpod(now);
        jobTruck.setJobDtLupd(now);
        jobTruckDao.saveOrUpdate(jobTruck);

        // Get list of images for the job
        List<TCkCtTripAttach> imagesList = imagesList(jobTruck.getJobId());
        List<String> imagePaths = imagesList.stream()
                .map(TCkCtTripAttach::getAtLoc)
                .collect(Collectors.toList());

        if (imagePaths.isEmpty()) {
            throw new ParameterException("No photos available to generate Photo PDF.");
        }

        // Generate Photo PDFs (multiple PDFs for the images)
        String photoPdfPaths = generatePhotoToPdf(imagePaths, jobTruck);

        // Get SFTP configuration
        SFTPConfig sftpConfig = getSysFreightSftpConfig();

        try {
            String folderNamePhoto = extractFolderName(photoPdfPaths);
            uploadFile(photoPdfPaths, folderNamePhoto, sftpConfig);
            System.out.println("Uploaded Photo PDF to SFTP: " + photoPdfPaths);
        } catch (Exception e) {
            System.err.println("Failed to upload Photo PDF: " + photoPdfPaths + ". Error: " + e.getMessage());
            throw e;
        }

        // Upload ePOD PDF
        try {
            String folderNameEPod = extractFolderName(ePODFilePath);
            uploadFile(ePODFilePath, folderNameEPod, sftpConfig);
            System.out.println("Uploaded ePOD PDF to SFTP: " + ePODFilePath);
        } catch (Exception e) {
            System.err.println("Failed to upload ePOD PDF: " + ePODFilePath + ". Error: " + e.getMessage());
            throw e;
        }
    }

    // Helper method to extract folder name from file path
    private String extractFolderName(String filePath) {
        File file = new File(filePath);
        String folderPath = file.getParent();
        // Extract just the folder name (last part of the folder path)
        return folderPath.substring(folderPath.lastIndexOf(File.separator) + 1);
    }

    //Specified for SysFreight generate EPod File name
    public String generateEPodFileNameSysFreight(TCkJobTruck jobTruck) throws Exception {
        // Generate the output file path for the job
        String outputFolderPath = getCkCtAttachmentPathJobSysFreight(jobTruck, true);
        // Example: /home/vcc/appAttachments/clictruck/SFTP/UAT/HUB/inbound/<CT Job No>_<Sysfreight job no>
        Date deliverDate = new Date();
        return outputFolderPath + File.separator + getEPodFileName(jobTruck, deliverDate);
    }
    public String getEPodFileName(TCkJobTruck jobTruck, Date date) {
        // Format the file name as YYYYMMDD_HHMMSS_JobId_customeRef_EPOD.PDF
        String jobId = jobTruck.getJobId();
        String customerRef = jobTruck.getJobCustomerRef();
        return String.format("%s_%s_EPOD.pdf", yyyyMMdd_HHmmssSDF.format(date),
                StringUtils.isBlank(customerRef) ? jobId : jobId + "_" + customerRef
        );
    }
    public SFTPConfig getSysFreightSftpConfig() {
        try {
            String sftpDsv = getSysParam("CLICTRUCK_SFTP_SYSFREIGHT");
            return (new ObjectMapper()).readValue(sftpDsv, SFTPConfig.class);
        } catch (Exception e) {
            Log.error("", e);
        }
        return null;
    }
    public String getSysParam(String key) throws Exception {
        if (StringUtils.isBlank(key))
            throw new ParameterException("param key null or empty");

        TCoreSysparam sysParam = coreSysparamDao.find(key);
        if (sysParam == null)
            throw new EntityNotFoundException("sysParam " + key + " not configured");

        return sysParam.getSysVal();

    }
    public String getCkCtAttachmentPathJobSysFreight(TCkJobTruck jobTruck, boolean isCreateFolder) throws Exception {
        // Validation
        if (StringUtils.isBlank(jobTruck.getJobId())) {
            throw new ParameterException("Parameter subFolderName is empty or blank.");
        }
//		String sysFreight = "SFTP/UAT/HUB/inbound/";
        String sysFreight = getSysParam(CtConstant.SYSFREIGHT_CLICTRUCK_PATH);

        // Root path
        String rootPath = ckCtCommonService.getCkCtAttachmentPathRoot(); // "/home/vcc/appAttachments/clictruck/"
        String concatPath = "";
        if (StringUtils.isNotBlank(jobTruck.getJobCustomerRef())){
            concatPath = jobTruck.getJobId().concat("_").concat(jobTruck.getJobCustomerRef());
        }else {
            concatPath = jobTruck.getJobId();
        }

        String jobTruckAttachPath = Paths.get(rootPath.concat(sysFreight), concatPath).toString();
        jobTruckAttachPath = jobTruckAttachPath.replace("\\", "/");

        // Create folder if needed
        if (isCreateFolder) {
            File outputDirectory = new File(jobTruckAttachPath);
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
        }

        return jobTruckAttachPath;
    }
    public List<TCkCtTripAttach> imagesList(String jobTruckId) throws Exception {
        List<TCkCtTrip> tripList = ckCtTripDao.findByJobId(jobTruckId);
        List<TCkCtTripAttach> imageList = new ArrayList<>();
        for (TCkCtTrip trip : tripList) {
            List<TCkCtTripAttach> tripPickUpAttachList = ckCtTripAttachDao.findByTrIdAndAtyId(
                    trip.getTrId(), TripMobileService.TripAttachTypeEnum.PHOTO_PICKUP.name()
            );
            List<TCkCtTripAttach> tripDropOffAttachList = ckCtTripAttachDao.findByTrIdAndAtyId(
                    trip.getTrId(), TripMobileService.TripAttachTypeEnum.PHOTO_DROPOFF.name()
            );
            if (tripPickUpAttachList != null) {
                imageList.addAll(tripPickUpAttachList);
            }

            if (tripDropOffAttachList != null) {
                imageList.addAll(tripDropOffAttachList);
            }
        }
        return imageList;
    }


    public String generatePhotoToPdf(List<String> imagePaths, TCkJobTruck jobTruck) throws Exception {
        Date deliverDate = new Date();
        TCkJob ckJob = jobTruck.getTCkJob();
        if (ckJob != null && ckJob.getTCkRecordDate() != null
                && ckJob.getTCkRecordDate().getRcdDtComplete() != null) {
            deliverDate = ckJob.getTCkRecordDate().getRcdDtComplete();
        }

        String photoPdfPath = getCkCtAttachmentPathJobSysFreight(jobTruck, true);
        String photoPdfFileName = getPhotoFileName(jobTruck, deliverDate);
        String file = photoPdfPath + File.separator + photoPdfFileName;
        mergeImageToPdf(imagePaths, file);
        return file;
    }

    public String getPhotoFileName(TCkJobTruck jobTruck, Date date) {
        // Format the file name as YYYYMMDD_HHMMSS_JobId_customerRef_PHO.pdf
        String jobId = jobTruck.getJobId();
        String customerRef = jobTruck.getJobCustomerRef();
        return String.format("%s_%s_PHO.pdf", yyyyMMdd_HHmmssSDF.format(date),
                StringUtils.isBlank(customerRef) ? jobId : jobId + "_" + customerRef
        );
    }

    public void mergeImageToPdf(List<String> sourceImagesPath, String targetPdfPath) throws IOException {
        try (PdfWriter writer = new PdfWriter(targetPdfPath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Add each image to the same PDF document
            for (String imagePath : sourceImagesPath) {
                Image img = new Image(ImageDataFactory.create(imagePath));
                document.add(img);
            }
        }
    }
      public void uploadFile(String localFilePath, String folderName, SFTPConfig config) throws Exception {
        if (localFilePath == null || localFilePath.isEmpty()) {
            throw new ParameterException("Local file path cannot be null or empty.");
        }
        if (config == null || config.getHost() == null || config.getUserId() == null || config.getPassword() == null) {
            throw new ParameterException("SFTP configuration is missing required fields.");
        }

        File file = new File(localFilePath);
        if (!file.exists()) {
            throw new ParameterException("Local file does not exist: " + localFilePath);
        }

        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            JSch jsch = new JSch();
            int port = config.getPort() != null ? config.getPort() : 22;
            String privateKeyPath = config.getPrivateKeyPath();
            if (privateKeyPath != null) {
                File privateKeyFile = new File(privateKeyPath);
                if (privateKeyFile.exists()) {
                    jsch.addIdentity(privateKeyPath);
                }
            }

            session = jsch.getSession(config.getUserId(), config.getHost(), port);
            session.setPassword(config.getPassword());
            java.util.Properties sessionConfig = new java.util.Properties();
            sessionConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sessionConfig);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            String remoteDir = config.getPath();
            channelSftp.cd(remoteDir);
            try {
                channelSftp.cd(folderName);
            } catch (SftpException e) {
                channelSftp.mkdir(folderName);
                channelSftp.cd(folderName);
            }

            // Set folder permissions to 770
            String fullFolderPath = remoteDir + folderName;
            channelSftp.chmod(0770, fullFolderPath);

            // Upload the file
            String remoteFileName = file.getName();
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                channelSftp.put(fileInputStream, remoteFileName);
                System.out.println("File uploaded successfully: " + remoteFileName);
            }
        } catch (SftpException e) {
            System.err.println("SFTP error: " + e.getMessage());
            throw e;  // Rethrow if needed
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;  // Rethrow if needed
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
