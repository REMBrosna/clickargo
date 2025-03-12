package com.guudint.clickargo.clictruck.constant;

import com.guudint.clickargo.clictruck.jobupload.service.JobUploadUtilService;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CtConstant {

	private static Logger log = Logger.getLogger(CtConstant.class);

	public static final String KEY_ATTCH_BASE_LOCATION = "KEY_CLICTRUCK_ATTCH_BASE_LOCATION";
	public static final String KEY_JRXML_BASE_PATH = "CLICTRUCK_JRXML_BASE_PATH";
	public static final String KEY_JRXML_DEBIT_NOTE_PATH = "CLICTRUCK_JRXML_DEBIT_NOTE_PATH";
	public static final String KEY_JRXML_DN_PATH = "CLICTRUCK_JRXML_DRAFT_DN_PATH";
	public static final String KEY_JRXML_INVOICE_PATH = "CLICTRUCK_JRXML_INVOICE_PATH";
	public static final String KEY_JRXML_DRAFT_INVOICE_PATH = "CLICTRUCK_JRXML_DRAFT_INVOICE_PATH";
	public static final String KEY_TRUCKS_RENTAL_PROVIDERS_PATH = "CLICTRUCK_TRUCKS_RENTAL_PROVIDERS";

	public static final String KEY_JRXML_DSV_SHIPMENT_AIR_PATH = "CLICTRUCK_JRXML_DSV_SHIPMENT_PATH";
	public static final String KEY_JRXML_DSV_SHIPMENT_SEA_PATH = "CLICTRUCK_JRXML_DSV_SEA_PATH";

	public static final String KEY_JRXML_PI_PATH = "CLICTRUCK_JRXML_PI_PATH";
	public static final String KEY_JRXML_PI_SUB_PATH = "CLICTRUCK_JRXML_PI_SUB_PATH";
	public static final String CLICTRUCK_JRXML_LOGO_PATH = "CLICTRUCK_JRXML_LOGO_PATH";
	public static final String SYSFREIGHT_CLICTRUCK_PATH = "SYSFREIGHT_CLICTRUCK_PATH";
	public class JobProcessVia {

		public static final String WEB = "WEB";
		public static final String MOBILE = "MOBILE";
	}

	public static final String KEY_CLICTRUCK_DEFAULT_TABS = "CLICTRUCK_DEFAULT_TABS";
	public static final String KEY_CLICTRUCK_DEFAULT_DASHBOARD = "CLICTRUCK_DEFAULT_DASHBOARD";
	public static final String KEY_CLICTRUCK_DEFAULT_STATE_FILTER = "CLICTRUCK_DEFAULT_STATE_FILTER";
	public static final String KEY_CLICTRUCK_DEFAULT_HIDE_FIELDS = "CLICTRUCK_DEFAULT_HIDE_FIELDS";
	public static final String KEY_CLICTRUCK_SHEL_CREDENTIAL_TYPE = "CLICTRUCK_SHEL_CREDENTIAL_TYPE";
	public static final String KEY_CLICTRUCK_SHELL_INVOICING_DISCOUNT = "CLICTRUCK_SHELL_INVOICING_DISCOUNT";
	public static final String CLICTRUCK_DEFAULT_DASHBOARD_TRUACK_WJ = "CLICTRUCK_DEFAULT_DASHBOARD_TRUACK_WJ";
	public static final String SHELL_CARD_TXN = "SHELL_CARD_TXN";

	public enum JobRecordFieldEnum {

		CONTRACT_ID("contract_id"),
		SHIPMENT_REF("shipment_ref"),
		JOB_SUB_TYPE("job_sub_type"),
		CUSTOMER_REF("customer_Ref"),
		LOADING("Loading"),
		BOOKING_DATE("Booking_Date"),
		PLAN_DATE("Plan_Date"),
		CARGO_TRUCK_TYPE("Truck_Type"),
		START_FROM_LOCATION("Start_From_Location"),
		FROM_LOCATION_NAME("From_Location_Name"),
		FROM_LOCATION_DETAILS("From_Location_Details"),
		TO_LOCATION_NAME("To_Location_Name"),
		TO_LOCATION_DETAILS("To_Location_Details"),
		FROM_LOCATION_DATE_TIME("From_Location_Date_Time"),
		FROM_LOCATION_MOBILE_NUMBER("From_Location_Mobile_Number"),
		FROM_LOCATION_REMARKS("From_Location_Remarks"),
		TO_LOCATION("To_Location"),
		TO_LOCATION_DATE_TIME("To_Location_Date_Time"),
		TO_LOCATION_MOBILE_NUMBER("To_Location_Mobile_Number"),
		TO_LOCATION_RECIPIENT_NAME("to_location_recipient_name"),
		TO_LOCATION_REMARKS("To_Location_Remarks"),
		CARGO_TYPE("Cargo_Type"),
		CARGO_QTY("cargo_qty"),
		CARGO_QTY_UOM("cargo_qty_uom"),
		CARGO_MAKES_NO("cargo_makes_no"),
		CARGO_LENGTH("cargo_length"),
		CARGO_WIDTH("cargo_width"),
		CARGO_HEIGHT("cargo_height"),
		CARGO_SIZE_UOM("cargo_size_uom"),
		CARGO_WEIGHT("cargo_weight"),
		CARGO_WEIGHT_UOM("cargo_weight_uom"),
		CARGO_VOLUME("cargo_volume"),
		CARGO_VOLUME_UOM("cargo_volume_uom"),
		DESCRIPTION_DEFAULT("description_default"),
		SPECIAL_INSTRUCTION("special_instruction"),
		COUNTRY("country"),
		START_LOCATION("start_location"),
		END_LOCATION("end_location"),
		DATE_OF_DELIVERY("date_of_delivery"),
		TIME_OF_DELIVERY("time_of_delivery"),
		EMAIL_NOTIFICATION("email_notification"),
		PAYMENT_METHOD("payment_method"),
		TRUCK_LICENSE_PLATE_NUMBER("truck_license_plate_number"),
		DRIVER_USERNAME("driver_username"),
		JOB_LINKING_NUMBER("job_linking_number"),
		DROPOFF_REMARK("dropoff_remark"),
		DROPOFF_POSTAL_CODE("dropoff_postal_code"),
		CUSTOMER_INVOICE_NUMBER("customer_invoice_number"),
		CASES_QTY("cases_qty"),
		BOOKING_DATE_CSQ("booking_date_csq"),
		LOADING_CSQ("loading_csq"),
		DESCRIPTION_CLASQUIN("description"),
		CARGO_TYPE_CLASQUIN("cargo_type"),
		CARGO_WEIGHT_CLASQUIN("cargo_weight"),
		CARGO_TRUCK_TYPE_CLASQUIN("cargo_truck_type"),
		SUBSCRIBE_NOTIFICATIONS("subscribe_notifications"),
		PHONE_NUMBER_COUNTRY("phone_number_country"),
		PHONE_NUMBER_NOTIFICATION("phone_number_notification"),
		SHIPMENT_REF_NO("shipment_ref_no"),

		// Sagawa Dashboard Excel
		JOB_DATE("job_date"),
		CUSTOMER("customer"),
		SHIPMENT_NO("shipment_no"),
		DESC("desc"),
		QTY("qty"),
		WEIGHT("weight"),
		VOLUME("volume"),
		PICKUP_ADDRESS("pickup_address"),
		DROPOFF_ADDRESS("dropoff_address"),
		TRUCK_TYPE("TRUCK_TYPE"),
		DROPOFF_DATETIME("DROPOFF_DATETIME"),
		PICKUP_DATETIME("PICKUP_DATETIME"),
		TO_LOCATION_MOBILE("TO_LOCATION_MOBILE"),
		REMARK("remark"),

		// Sagawa TVH FF
		PICK_UP_ADDRESS("pick_up_address"),
		DELIVERY_ADDRESS("delivery_address"),
		INVOICE_NUMBER("invoice_number"),
		CONTACT_NUMBER("contact_number"),
		ITEM_DESCRIPTION("item_description"),
		QUANTITY("quantity"),
		ITEM_WEIGHT("item_weight"),
		LENGTH_CM("length_cm"),
		WIDTH_CM("width_cm"),
		HEIGHT_CM("height_cm"),
		ADDITIONAL_INFO("additional_info"),
		REMARK_TVH("remark_tvh"),

		// Clasquin
		BOOKING_DATE_CSP("booking_date_csp"),
		CARGO_OWNER_CSP("cargo_owner_csp"),
		DATE_OF_DELIVERY_CSP("date_of_delivery_csp"),
		PLAN_DATE_CSP("plan_date_csp"),
		;

		String code;

		private JobRecordFieldEnum(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		public static JobRecordFieldEnum isValidEnum(String value) {
			try {
				return JobRecordFieldEnum.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException e) {
				log.error("Error isValidEnum: "+e.getMessage());
				return null;
			}
		}

	}

	public enum AccountTypeEnum {
		CRW("CWL"),
		DSV("DSVAS"),
		LOG("LAP"),
		AWO("AWOT"),
		SGW("CK0007"),
		MMD("MMD"),
		TVH("TVH"),
		SCH("DSCH");

		private final String value;

		// Constructor
		AccountTypeEnum(String value) {
			this.value = value;
		}

		// Getter for the value
		public String getValue() {
			return value;
		}

		// Static map for reverse lookup
		private static final Map<String, AccountTypeEnum> lookup = new HashMap<>();

		static {
			for (AccountTypeEnum type : AccountTypeEnum.values()) {
				lookup.put(type.getValue(), type);
			}
		}

		// Method to get enum by value
		public static AccountTypeEnum getByValue(String value) {
			return lookup.get(value);
		}

		// Check if a value is valid
		public static boolean isValid(String value) {
			return lookup.containsKey(value);
		}
	}
}
