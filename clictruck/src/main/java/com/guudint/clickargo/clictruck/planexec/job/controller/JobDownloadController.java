package com.guudint.clickargo.clictruck.planexec.job.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckDownloadService;

/**
 * Arianto
 */
@RequestMapping(value = "/api/v1/clickargo/clictruck/job/")
@CrossOrigin
public class JobDownloadController {
	
	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(JobDownloadController.class);
	
	@Autowired
	private CkJobTruckDownloadService ckJobTruckDownloadService;
	
	
	/**
	 * @param response
	 * @param entity
	 * @param params
	 * @throws IOException
	 */
	@GetMapping("{entity}/download")
	public void downloadData(HttpServletResponse response, @PathVariable String entity, @RequestParam Map<String, String> params)throws IOException{
		log.debug("Download Excel");
		
			try {
				Workbook workbook = ckJobTruckDownloadService.getDownloadData(entity, params);
	            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	            response.setHeader("Content-Disposition", "attachment; filename=example.xlsx");
	            workbook.write(response.getOutputStream());
			}  catch (Exception ex) {
				log.error("Download Excel", ex);
				return;
			}
	}

}
