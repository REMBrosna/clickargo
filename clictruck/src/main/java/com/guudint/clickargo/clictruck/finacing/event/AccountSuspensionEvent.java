package com.guudint.clickargo.clictruck.finacing.event;

import org.springframework.context.ApplicationEvent;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.vcc.camelone.ccm.dto.CoreAccn;

public class AccountSuspensionEvent extends ApplicationEvent {

	// Static attributes
		private static final long serialVersionUID = 2702400960489167617L;
		
	    // Attributes
		private CoreAccn coreAccn;
		private CkCtContract ckCtContract;
		private String due;
	    
	    // Constructor
		public AccountSuspensionEvent(Object source, CoreAccn coreAccn, CkCtContract ckCtContract, String due) {
			super(source);
			this.coreAccn = coreAccn;
			this.ckCtContract = ckCtContract;
			this.due = due;
		}

		// Properties
		/////////////
		public CoreAccn getCoreAccn() {
			return coreAccn;
		}

		public void setCoreAccn(CoreAccn coreAccn) {
			this.coreAccn = coreAccn;
		}

		public CkCtContract getCkCtContract() {
			return ckCtContract;
		}

		public void setCkCtContract(CkCtContract ckCtContract) {
			this.ckCtContract = ckCtContract;
		}

		public String getDue() {
			return due;
		}

		public void setDue(String due) {
			this.due = due;
		}

}
