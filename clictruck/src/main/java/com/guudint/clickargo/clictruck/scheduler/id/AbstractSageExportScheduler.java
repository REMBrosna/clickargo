package com.guudint.clickargo.clictruck.scheduler.id;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.vcc.camelone.util.email.SysParam;

@Component
@EnableAsync
public abstract class AbstractSageExportScheduler extends AbstractClickTruckScheduler {

	private static Logger log = Logger.getLogger(AbstractSageExportScheduler.class);

	// Attributes
	/////////////////
	@Autowired
	protected SysParam sysParam;
	
	protected Date getBeginDate(int d) {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		if (1 == d) {
			// from 16th last month
			cal.add(Calendar.MONTH, -1);
			cal.set(Calendar.DAY_OF_MONTH, 16);
			
		} else if (16 == d) {
			// from 1st this month
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		
		return cal.getTime();
	}

	protected Date getEndDate() {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		cal.add(Calendar.MILLISECOND, -1);
		
		return cal.getTime();
	}
}
