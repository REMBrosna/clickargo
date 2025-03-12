package com.guudint.clickargo.clictruck.finacing.constant;

public class CkCtToInvoiceConstant {

    public class Prefix {

        public static final String PREFIX_CK_CT_TO_INVOICE = "CKCTTOINV";
        public static final String AUDIT_TAG = "CKCT TO INV";
    }

    public class Table {

        public static final String NAME = "T_CK_CT_TO_INVOICE";
        public static final String NAME_DAO = "ckCtToInvoiceDao";
        public static final String NAME_ENTITY = "TCkCtToInvoice";
    }

    public class ColumnParam {

        public static final String INV_ID = "invId";
        public static final String INV_STATE = "invState";
        public static final String INV_JOB_ID = "invJobId";
        public static final String INV_TRIP = "invTrip";
        public static final String INV_NO = "invNo";
        public static final String INV_DT_ISSUE = "invDtIssue";
        public static final String INV_FROM = "invFrom";
        public static final String INV_TO = "invTo";
        public static final String INV_NAME = "invName";
        public static final String INV_LOC = "invLoc";
        public static final String INV_INVOCIER_COMMENT = "invInvoicerComment";
        public static final String INV_INVOCIEE_REMARKS = "invInvocieeRemarks";
        public static final String INV_STATUS = "invStatus";
        public static final String INV_DT_CREATE = "invDtCreate";
        public static final String INV_UID_CREATE = "invUidCreate";
        public static final String INV_DT_LUPD = "invDtLupd";
        public static final String INV_UID_LUPD = "invUidLupd";
    }

    public class Column {

        public static final String INV_ID = "o.invId";
        public static final String INV_STATE = "o.TCkCtMstToInvoiceState.instId";
        public static final String INV_JOB_ID = "o.invJobId";
        public static final String INV_TRIP = "o.invTrip";
        public static final String INV_NO = "o.invNo";
        public static final String INV_DT_ISSUE = "o.invDtIssue";
        public static final String INV_FROM = "o.TCoreAccnByInvFrom.accnId";
        public static final String INV_TO = "o.TCoreAccnByInvTo.accnId";
        public static final String INV_NAME = "o.invName";
        public static final String INV_LOC = "o.invLoc";
        public static final String INV_INVOCIER_COMMENT = "o.invInvoicerComment";
        public static final String INV_INVOCIEE_REMARKS = "o.invInvocieeRemarks";
        public static final String INV_STATUS = "o.invStatus";
        public static final String INV_DT_CREATE = "o.invDtCreate";
        public static final String INV_UID_CREATE = "o.invUidCreate";
        public static final String INV_DT_LUPD = "o.invDtLupd";
        public static final String INV_UID_LUPD = "o.invUidLupd";
    }
}
