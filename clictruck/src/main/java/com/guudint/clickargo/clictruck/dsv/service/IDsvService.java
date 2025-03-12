package com.guudint.clickargo.clictruck.dsv.service;

import java.io.File;
import java.util.List;

import com.vcc.camelone.util.sftp.model.SFTPConfig;

public interface IDsvService {
	
	/*-
	public void afterDsvJobIsDelivered(String shId) throws Exception;

	/**
	 * Load file from SFTP, store file to local
	 * 
	 * @return local File
	 */
	public List<File> loadFilesFromSftp(SFTPConfig sftpConfig, String parentPath) throws Exception;

	/**
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void processDsvFile(File file) throws Exception;

	/**
	 * 
	 * @param sftpConfig
	 * @param file
	 * @throws Exception
	 */
	public void mvFile2HisotoryFolder(SFTPConfig sftpConfig, File file) throws Exception;
	
	
	
	public void auxiliaryRefreshJobTruckExt(String shId) throws Exception ;

}
