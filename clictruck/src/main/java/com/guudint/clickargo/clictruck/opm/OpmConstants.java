package com.guudint.clickargo.clictruck.opm;

public class OpmConstants {

	public final static String OPM_CSVFILE_PREFIX_APPROVAL = "CA";
	public final static String OPM_CSVFILE_PREFIX_UTILIZATION = "CU";
	public final static String OPM_CSVFILE_PREFIX_DISBURSEMENT = "CD";
	public final static String OPM_CSVFILE_PREFIX_REPAYMENT = "CR";
	public final static String OPM_CSVFILE_PREFIX_TERMINATION = "CT";
	public final static String OPM_CSVFILE_PREFIX_SUSPENSION = "CS";
	public final static String OPM_CSVFILE_PREFIX_UNSUSPENSION = "CE";

	public final static String OPM_SFTP_DIRECTION_OUT = "OUT";
	public final static String OPM_SFTP_DIRECTION_IN = "IN";

	public final static String OPM_ACTION_DISBURSE = "DISBURSE";

	public enum OPM_OPT {
		OC, OT
	}

	public enum OPM_TRACK_RADIUS {

		RADIUS_30(30), RADIUS_100(100);

		int radius;

		private OPM_TRACK_RADIUS() {
		}

		private OPM_TRACK_RADIUS(int radius) {
			this.radius = radius;
		}

		public int getRadius() {
			return radius;
		}

		public void setRadius(int radius) {
			this.radius = radius;
		}
	}

	public enum Opm_Validation {

		// Credit Approve
		UNKNOW("500", ""), NULL("501", "{0} is empty"), MULTI("502", "Multiple {0} found."),
		NOT_FIND("503", "{0} not found."),

		NOT_NUMBER("504", "{0} is not number"), NOT_LONG_NUMBER("505", "{0} is not long number"),
		LESS_THAN_ZERO("506", "{0} Less than or equal to zero"),
		GREATER_THAN_MAX("507", "{0} Greater than maximum limit"),

		NOT_DATE("507", "{0} is not date "), AFTER_TODAY("508", "{0} after toady "),
		LESS_TODAY("509", "{0} less than today."),

		EXPIRY_LESS_APPROVAL_DATE("510", "{0} earlier than the Approval Date"),

		NO_APPROVED_CREDIT("511", "No {0} found"),
		EXPIRED_SUSPENDED_TERMINATED_CREDIT("512", "{0} is either expired, suspended or terminated"),
		REF_NO_INVALID("513", "{0} does not belong to specified tax_no"),

		CREDIT_TERMINATED_ALREADY("514", "Already terminated for this reference number"),
		CREDIT_SUSPENDED_ALREADY("515", "Already suspended for this reference number"),
		CREDIT_UNSUSPENDED_ALREADY("516", "Already suspended for this reference number"),

		CREDIT_APPROVED_ALREADY("521", "Already approved for this reference number "),

		DISBURSEMENT_REFERENCE_NUMBER_ALREADY("522", "Already disbursement for this reference number "),

		REPAY_REFERENCE_NUMBER_ALREADY("523", "Already paid for this reference number "),

		ACTION_SHOULD_BE("531", "Action should be {0}"),

		AMOUNT_NOT_CORRECT("551", "{0} amount not correct.");

		String code;
		String msg;

		private Opm_Validation(String code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
