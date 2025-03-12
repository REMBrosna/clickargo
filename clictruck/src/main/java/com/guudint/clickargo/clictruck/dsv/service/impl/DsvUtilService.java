package com.guudint.clickargo.clictruck.dsv.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.can.model.TCoreNotificationLog;
import com.vcc.camelone.can.service.impl.NotificationService;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.sftp.model.SFTPConfig;
import com.vcc.camelone.util.sftp.model.SFTPStatus;

@Service
public class DsvUtilService {

	private static Logger log = Logger.getLogger(DsvUtilService.class);
	private static SimpleDateFormat yyyyMMddHHmmssSDF = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired
	protected SysParam sysParam;

	@Autowired
	protected CkNotificationUtilService notificationUtilService;
	@Autowired
	protected NotificationService notificationService;

	@Transactional
	public void sendDSVEmailNotification(String file, String messageId, String dsvNotificationType) throws Exception {

		try {
			NotificationParam param = new NotificationParam();
			param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
			param.setTemplateId(ClicTruckNotifTemplates.DSV_POD_PHO.getId());

			String recipientStr = sysParam.getValString("CLICTRUCK_DSV_EMAIL_RECEIVER", "Zhang.Ji@guud.company");

			HashMap<String, String> contentFields = new HashMap<>();
			// contentFields.put(":sp_details", "'");

			param.setContentFeilds(contentFields);
			param.setAttachments(new ArrayList<>(Arrays.asList(file)));

			param.setRecipients(new ArrayList<>(Arrays.asList(recipientStr.split(","))));

			HashMap<String, String> subjectFields = new HashMap<>();
			subjectFields.put(":subject", dsvNotificationType + " " + messageId);
			param.setSubjectFields(subjectFields);

			TCoreNotificationLog notifLog = notificationUtilService.saveNotificationLog(param.toJson(), null, false);
			log.info("TCoreNotificationLog id: " + notifLog.getNlogId());
			notificationService.sendNotificationsByLogId(notifLog.getNlogId());
			// notificationService.notifySyn(param);

		} catch (Exception e) {
			log.error("Fail to send email." + file + " " + messageId, e);
			throw e;
		}

	}
	
    
	public static synchronized SFTPStatus getSftpFiles(SFTPConfig config, File folder, List<File> files,
			Integer maxDownloadFiles) throws Exception {
		log.info("getSftpFiles");

		// default is 100;
		if (null == maxDownloadFiles || maxDownloadFiles < 0) {
			maxDownloadFiles = 100;
		}

		SFTPStatus status = new SFTPStatus(SFTPStatus.STATE.COMPLETE, "");
		JSch jsch = new JSch();
		Session session = null;
		try {

			int port = (config.getPort() != null) ? config.getPort() : 22; // default is 22;

			if (StringUtils.isNotBlank(config.getPrivateKeyPath()) && new File(config.getPrivateKeyPath()).exists()) {
				jsch.addIdentity(config.getPrivateKeyPath());
			}

			session = jsch.getSession(config.getUserId(), config.getHost(), port);
			if (StringUtils.isNotBlank(config.getPassword())) {
				session.setPassword(config.getPassword());
			}
			
			session.setConfig("StrictHostKeyChecking", "no");

			log.info("Connecting session...");
			session.connect();
			log.info("Session connected.");

			log.info("Opening SFTP channel...");
			Channel channel = session.openChannel("sftp");
			log.info("SFTP channel opened.");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			
			List<File> fileList = downloadFile(sftpChannel, config, folder, maxDownloadFiles);
			files.addAll(fileList);
			
			sftpChannel.exit();
			session.disconnect();
		} catch (Exception ex) {
			log.error("Error in getSftpFiles", ex);
			status.setState(SFTPStatus.STATE.EXCEPTION);
			status.setMsg(ex.getMessage());
			throw ex;
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	private static List<File> downloadFile(ChannelSftp sftpChannel, SFTPConfig config, File folder, Integer maxDownloadFiles) throws SftpException, FileNotFoundException, IOException {

		List<File> files = new ArrayList<>();
		
		Vector<LsEntry> filelist = sftpChannel.ls(config.getOutpath());
		
		for (int i = 0; i < filelist.size(); i++) {

			if (i > maxDownloadFiles) {
				break;
			}
			LsEntry lsEntry = filelist.get(i);

			log.debug("remote: " + lsEntry.getFilename());
			if (lsEntry.getAttrs().isDir())
				continue; // not downloading directories

			String downloadFile = folder.getAbsolutePath() + "/" + lsEntry.getFilename();
			// clearFile(downloadFile);
			renameFile(downloadFile);
			File file = new File(downloadFile);

			try (FileOutputStream fos = new FileOutputStream(file);) {
				sftpChannel.get(config.getOutpath() + "/" + lsEntry.getFilename(), fos);
			}
			files.add(file);
		}
		
		return files;
	}

	public static synchronized SFTPStatus moveFileFromOut2HistoryFolder(SFTPConfig config, List<String> files,
			String subFolderName) {
		log.debug("rm");

		SFTPStatus status = new SFTPStatus(SFTPStatus.STATE.COMPLETE, "");
		JSch jsch = new JSch();
		Session session = null;
		try {
			int port = (config.getPort() != null) ? config.getPort() : 22; // default is 22;

			if (StringUtils.isNotBlank(config.getPrivateKeyPath()) && new File(config.getPrivateKeyPath()).exists()) {
				jsch.addIdentity(config.getPrivateKeyPath());
			}

			session = jsch.getSession(config.getUserId(), config.getHost(), port);
			if (StringUtils.isNotBlank(config.getPassword())) {
				session.setPassword(config.getPassword());
			}
			
			session.setConfig("StrictHostKeyChecking", "no");
			
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			@SuppressWarnings("unchecked")
			Vector<LsEntry> filelist = sftpChannel.ls(config.getOutpath());
			if (null != files && files.size() > 0) {

				for (LsEntry lsEntry : filelist) {
					log.debug("remote: " + lsEntry.getFilename());
					if (lsEntry.getAttrs().isDir())
						continue; // not downloading directories
					for (String file : files) {
						if (lsEntry.getFilename().equalsIgnoreCase(file)) {
							log.debug("rm: " + lsEntry.getFilename());
							// sftpChannel.rm(config.getOutpath() + "/" + lsEntry.getFilename());
							String originalFile = config.getOutpath() + "/" + lsEntry.getFilename();
							String desinationFile = config.getOutpath() + "/" + subFolderName + "/"
									+ lsEntry.getFilename();
							sftpChannel.rename(originalFile, desinationFile);
						}
					}
				}
			}
			sftpChannel.exit();
			session.disconnect();
		} catch (Exception ex) {
			log.error("rm", ex);
			status.setState(SFTPStatus.STATE.EXCEPTION);
			status.setMsg(ex.getMessage());
		}
		return status;
	}

	private static synchronized void clearFile(String downloadFile) {
		try {
			File file = new File(downloadFile);
			if (file.exists()) {
				log.debug("deleteing: " + file.getAbsolutePath());
				FileUtils.deleteQuietly(file);
			}
		} catch (Exception ex) {
			log.error("clearFile", ex);
		}
	}

	private static synchronized void renameFile(String downloadFile) {
		try {
			File file = new File(downloadFile);
			if (file.exists()) {
				log.debug("deleteing: " + file.getAbsolutePath());
				FileUtils.moveFile(file, new File(downloadFile + "_" + yyyyMMddHHmmssSDF.format(new Date())));
			}
		} catch (Exception ex) {
			log.error("clearFile", ex);
		}
	}
}
