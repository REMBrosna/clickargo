package com.guudint.clicdo.common.service;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.config.model.TCoreSysparam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.util.email.SysParam;

@Service
public class CkCtCommonService {

	private static SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyyMMdd");

	@Autowired
	SysParam sysParam;

	/**
	 * All CT2 attachments under this folder
	 * 
	 * @return
	 */
	public String getCkCtAttachmentPathRoot() {

		return sysParam.getValString(CtConstant.KEY_ATTCH_BASE_LOCATION, "/home/vcc/appAttachments/clictruck/");

	}

	/**
	 * Store attachment this folder by jobTruckId and current time.
	 * 
	 * @return
	 * @throws ParameterException
	 */
	public String getCkCtAttachmentPathJob(String subFolderName, boolean isCreateFolder) throws ParameterException {
		// Validation
		if (StringUtils.isBlank(subFolderName)) {
			throw new ParameterException("Parameter subFolderName is empty or blank.");
		}

		// Root path
		String rootPath = this.getCkCtAttachmentPathRoot(); // "/home/vcc/appAttachments/clictruck/"
		String datePath = yyyyMMddSDF.format(new Date()); // e.g., "20241230"

		// Construct the path
		String jobTruckAttachPath = Paths.get(rootPath, datePath, subFolderName).toString();

		// Ensure forward slashes (for Unix/Linux format)
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
}
