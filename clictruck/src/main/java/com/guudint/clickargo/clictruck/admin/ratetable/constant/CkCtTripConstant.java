package com.guudint.clickargo.clictruck.admin.ratetable.constant;

public class CkCtTripConstant {

    public class Prefix {

        public static final String PREFIX_CK_CT_RATE = "CKCTTRIP";
        public static final String AUDIT_TAG = "CKCT TRIP";
    }

    public class Table {

        public static final String NAME = "T_CK_CT_TRIP_RATE";
        public static final String NAME_DAO = "ckCtTripRateDao";
        public static final String NAME_ENTITY = "TCkCtTripRate";
    }

    public class ColumnParam {

        public static final String TR_ID = "trId";
        public static final String TR_RATE_TABLE = "trRateTable";
        public static final String TR_LOC_FROM = "trLocFrom";
        public static final String TR_LOC_TO = "trLocTo";
        public static final String TR_VEH_TYPE = "trVehType";
        public static final String TR_CHARGE = "trCharge";
        public static final String TR_STATUS = "trStatus";
        public static final String TR_DT_CREATE = "trDtCreate";
        public static final String TR_UID_CREATE = "trUidCreate";
        public static final String TR_DT_LUPD = "trDtLupd";
        public static final String TR_UID_LUPD = "trUidLupd";
    }

    public class Column {

        public static final String TR_ID = "o.trId";
        public static final String TR_RATE_TABLE = "o.TCkCtRateTable.rtId";
        public static final String TR_LOC_FROM = "o.TCkCtLocationByTrLocFrom.locId";
        public static final String TR_LOC_TO = "o.TCkCtLocationByTrLocTo.locId";
        public static final String TR_VEH_TYPE = "o.TCkCtMstVehType.vhtyId";
        public static final String TR_CHARGE = "o.trCharge";
        public static final String TR_STATUS = "o.trStatus";
        public static final String TR_DT_CREATE = "o.trDtCreate";
        public static final String TR_UID_CREATE = "o.trUidCreate";
        public static final String TR_DT_LUPD = "o.trDtLupd";
        public static final String TR_UID_LUPD = "o.trUidLupd";
    }

    public class PropertyName {

        public static final String TR_ID = "trId";
        public static final String TR_RATE_TABLE = "TCkCtRateTable";
        public static final String TR_LOC_FROM = "TCkCtLocationByTrLocTo";
        public static final String TR_LOC_TO = "TCkCtLocationByTrLocFrom";
        public static final String TR_VEH_TYPE = "TCkCtMstVehType";
        public static final String TR_CHARGE = "trCharge";
        public static final String TR_STATUS = "trStatus";
        public static final String TR_DT_CREATE = "trDtCreate";
        public static final String TR_UID_CREATE = "trUidCreate";
        public static final String TR_DT_LUPD = "trDtLupd";
        public static final String TR_UID_LUPD = "trUidLupd";
    }
}
