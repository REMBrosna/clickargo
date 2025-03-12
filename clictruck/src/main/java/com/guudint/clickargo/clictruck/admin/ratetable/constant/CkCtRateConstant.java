package com.guudint.clickargo.clictruck.admin.ratetable.constant;

public class CkCtRateConstant {

    public class Prefix {

        public static final String PREFIX_CK_CT_RATE = "CKCTRT";
        public static final String AUDIT_TAG = "CKCT RATE";
    }

    public class Table {

        public static final String NAME = "T_CK_CT_RATE_TABLE";
        public static final String NAME_DAO = "ckCtRateTableDao";
        public static final String NAME_ENTITY = "TCkCtRateTable";
    }

    public class ColumnParam {

        public static final String RT_ID = "rtId";
        public static final String RT_COMPANY_ID = "rtCompanyId";
        public static final String RT_CO_FF_ID = "rtCoFFId";
        public static final String RT_COMPANY_NAME = "rtCompanyName";
        public static final String RT_CO_FF_NAME = "rtCoFFName";
        public static final String RT_NAME = "rtName";
        public static final String RT_DESCRIPTION = "rtDescription";
        public static final String RT_CCY = "rtCcy";
        public static final String RT_DT_START = "rtDtStart";
        public static final String RT_DT_END = "rtDtEnd";
        public static final String RT_REMARKS = "rtRemarks";
        public static final String RT_STATUS = "rtStatus";
        public static final String RT_DT_CREATE = "rtDtCreate";
        public static final String RT_UID_CREATE = "rtUidCreate";
        public static final String RT_DT_LUPD = "rtDtLupd";
        public static final String RT_UID_LUPD = "rtUidLupd";
    }

    public class Column {

        public static final String RT_ID = "o.rtId";
        public static final String RT_COMPANY_ID = "o.TCoreAccnByRtCompany.accnId";
        public static final String RT_CO_FF_ID = "o.TCoreAccnByRtCoFf.accnId";
        public static final String RT_COMPANY_NAME = "o.TCoreAccnByRtCompany.accnName";
        public static final String RT_CO_FF_NAME = "o.TCoreAccnByRtCoFf.accnName";
        public static final String RT_NAME = "o.rtName";
        public static final String RT_DESCRIPTION = "o.rtDescription";
        public static final String RT_CCY = "o.TMstCurrency.rtCcy";
        public static final String RT_DT_START = "o.rtDtStart";
        public static final String RT_DT_END = "o.rtDtEnd";
        public static final String RT_REMARKS = "o.rtRemarks";
        public static final String RT_STATUS = "o.rtStatus";
        public static final String RT_DT_CREATE = "o.rtDtCreate";
        public static final String RT_UID_CREATE = "o.rtUidCreate";
        public static final String RT_DT_LUPD = "o.rtDtLupd";
        public static final String RT_UID_LUPD = "o.rtUidLupd";
    }

    public class PropertyName {

        public static final String RT_ID = "rtId";
        public static final String RT_COMPANY = "TCoreAccnByRtCompany";
        public static final String RT_CO_FF = "TCoreAccnByRtCoFf";
        public static final String RT_NAME = "rtName";
        public static final String RT_DESCRIPTION = "rtDescription";
        public static final String RT_CCY = "TMstCurrency.rtCcy";
        public static final String RT_DT_START = "rtDtStart";
        public static final String RT_DT_END = "rtDtEnd";
        public static final String RT_REMARKS = "rtRemarks";
        public static final String RT_STATUS = "rtStatus";
        public static final String RT_DT_CREATE = "rtDtCreate";
        public static final String RT_UID_CREATE = "rtUidCreate";
        public static final String RT_DT_LUPD = "rtDtLupd";
        public static final String RT_UID_LUPD = "rtUidLupd";
    }
    
    public enum TripType {

    	S("Single Trip"), 
    	M("Multi-Drop"), 
    	C("Children");
    	
    	private String desc;

    	private TripType(String desc) {
    		this.desc = desc;
    	}

    	public String getDesc() {
    		return desc;
    	}
    }
}
