package com.guudint.clickargo.clictruck.planexec.trip.constant;

public class CkCtTripReimbursementConstant {
    
    public class Prefix {

        public static final String PREFIX_CK_CT_TRP_REM = "CKCTTRREIM";
        public static final String AUDIT_TAG = "CKCT TRIP REIMURSEMENT";
    }

    public class Table {

        public static final String NAME = "T_CK_CT_TRIP_REIMBURSEMENT";
        public static final String NAME_DAO = "ckCtTripReimbursementDao";
        public static final String NAME_ENTITY = "TCkCtTripReimbursement";
    }

    public class ColumnParam {
        
        public static final String TR_ID = "trId";
        public static final String TR_TRIP = "trTrip";
        public static final String TR_JOB_ID = "trJobId";
        public static final String TR_TYPE = "trType";
        public static final String TR_RECEIPT_NAME = "trReceiptName";
        public static final String TR_RECEIPT_LOC = "trReceiptLoc";
        public static final String TR_REMARKS = "trRemarks";
        public static final String TR_PRICE = "trPrice";
        public static final String TR_TAX = "trTax";
        public static final String TR_TOTAL = "trTotal";
        public static final String TR_STATUS = "trStatus";
        public static final String TR_DT_CREATE = "trDtCreate";
        public static final String TR_UID_CREATE = "trUidCreate";
        public static final String TR_DT_LUPD = "trDtLupd";
        public static final String TR_UID_LUPD = "trUidLupd";
    }

    public class Column {

        public static final String TR_ID = "o.trId";
        public static final String TR_TRIP = "o.TCkCtTrip.trId";
        public static final String TR_TRIP_JOBID = "o.TCkCtTrip.TCkJobTruck.jobId";
        public static final String TR_TYPE = "o.TCkCtMstReimbursementType.rbtypId";
        public static final String TR_RECEIPT_NAME = "o.trReceiptName";
        public static final String TR_RECEIPT_LOC = "o.trReceiptLoc";
        public static final String TR_REMARKS = "o.trRemarks";
        public static final String TR_PRICE = "o.trPrice";
        public static final String TR_TAX = "o.trTax";
        public static final String TR_TOTAL = "o.trTotal";
        public static final String TR_STATUS = "o.trStatus";
        public static final String TR_DT_CREATE = "o.trDtCreate";
        public static final String TR_UID_CREATE = "o.trUidCreate";
        public static final String TR_DT_LUPD = "o.trDtLupd";
        public static final String TR_UID_LUPD = "o.trUidLupd";
    }
}
