package com.guudint.clickargo.clictruck.common.constant;

public class CkCtLocationConstant {

    public class Prefix {

        public static final String PREFIX_CK_CT_LOCATION = "CKCTLOC";
        public static final String AUDIT_TAG = "CKCT LOCATION";
    }

    public class Table {

        public static final String NAME = "T_CK_CT_LOCATION";
        public static final String NAME_DAO = "ckCtLocationDao";
        public static final String NAME_ENTITY = "TCkCtLocation";
    }

    public class ColumnParam {

        public static final String LOC_ID = "locId";
        public static final String LOC_TYPE = "locType";
        public static final String LOC_COMPANY = "locCompany";
        public static final String LOC_NAME = "locName";
        public static final String LOC_ADDRESS = "locAddress";
        public static final String LOC_DT_START = "locDtStart";
        public static final String LOC_DT_END = "locDtEnd";
        public static final String LOC_REMARKS = "locRemarks";
        public static final String LOC_STATUS = "locStatus";
        public static final String LOC_DT_CREATE = "locDtCreate";
        public static final String LOC_UID_CREATE = "locUidCreate";
        public static final String LOC_DT_LUPD = "locDtLupd";
        public static final String LOC_UID_LUPD = "locUidLupd";
        public static final String LOC_LATITUDE = "locLatitude";
        public static final String LOC_LONGITUDE = "locLongitude";
        public static final String LOC_DEFAULT = "Default";
    }

    public class Column {

        public static final String LOC_ID = "o.locId";
        public static final String LOC_TYPE = "o.TCkCtMstLocationType.lctyId";
        public static final String LOC_COMPANY = "o.TCoreAccn.accnId";
        public static final String LOC_NAME = "o.locName";
        public static final String LOC_ADDRESS = "o.locAddress";
        public static final String LOC_DT_START = "o.locDtStart";
        public static final String LOC_DT_END = "o.locDtEnd";
        public static final String LOC_REMARKS = "o.locRemarks";
        public static final String LOC_STATUS = "o.locStatus";
        public static final String LOC_DT_CREATE = "o.locDtCreate";
        public static final String LOC_UID_CREATE = "o.locUidCreate";
        public static final String LOC_DT_LUPD = "o.locDtLupd";
        public static final String LOC_UID_LUPD = "o.locUidLupd";
        public static final String LOC_LATITUDE = "o.locLatitude";
        public static final String LOC_LONGITUDE = "o.locLongitude";
    }

    public class PropertyName {

        public static final String LOC_ID = "locId";
        public static final String LOC_TYPE = "TCkCtMstLocationType";
        public static final String LOC_COMPANY = "TCoreAccn";
        public static final String LOC_NAME = "locName";
        public static final String LOC_ADDRESS = "locAddress";
        public static final String LOC_DT_START = "locDtStart";
        public static final String LOC_DT_END = "locDtEnd";
        public static final String LOC_REMARKS = "locRemarks";
        public static final String LOC_GPS = "locGps";
        
        public static final String LOC_STATUS = "locStatus";
        public static final String LOC_DT_CREATE = "locDtCreate";
        public static final String LOC_UID_CREATE = "locUidCreate";
        public static final String LOC_DT_LUPD = "locDtLupd";
        public static final String LOC_UID_LUPD = "locUidLupd";
        public static final String LOC_LATITUDE = "locLatitude";
        public static final String LOC_LONGITUDE = "locLongitude";
    }

    public class LocationId {
        public static final String ADDRESS = "ADDRESS";
        public static final String REGION = "REGION";
    }
}
