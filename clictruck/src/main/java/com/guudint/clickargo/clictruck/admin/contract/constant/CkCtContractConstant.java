package com.guudint.clickargo.clictruck.admin.contract.constant;

public class CkCtContractConstant {

    public class Prefix {

        public static final String PREFIX_CK_CT_CONTRACT = "CKCTCONC";
        public static final String AUDIT_TAG = "CKCT CONTRACT";
    }

    public class Table {

        public static final String NAME = "T_CK_CT_CONTRACT";
        public static final String NAME_DAO = "ckCtContractDao";
        public static final String NAME_ENTITY = "TCkCtContract";
    }

    public class ColumnParam {

        public static final String CON_ID = "conId";
        public static final String CON_NAME = "conName";
        public static final String CON_DESCRIPTION = "conDescription";
        public static final String CON_CCY = "conCcy";
        public static final String CON_DT_START = "conDtStart";
        public static final String CON_DT_END = "conDtEnd";
        public static final String CON_TO_ID = "conToId";
        public static final String CON_TO_NAME = "conToName";
        public static final String CON_CHARGE_TO = "conChargeTo";
        public static final String CON_CO_FF_ID = "conCoFfId";
        public static final String CON_CO_FF_NAME = "conCoFfName";
        public static final String CON_CHARGE_CO_FF = "conChargeCoFf";
        public static final String CON_RATE_TABLE = "conRateTable";
        public static final String CON_STATUS = "conStatus";
        public static final String CON_DT_CREATE = "conDtCreate";
        public static final String CON_UID_CREATE = "conUidCreate";
        public static final String CON_DT_LUPD = "conDtLupd";
        public static final String CON_UID_LUPD = "conUidLupd";
    }

    public class Column {

        public static final String CON_ID = "o.conId";
        public static final String CON_NAME = "o.conName";
        public static final String CON_DESCRIPTION = "o.conDescription";
        public static final String CON_CCY = "o.TMstCurrency.ccyCode";
        public static final String CON_DT_START = "o.conDtStart";
        public static final String CON_DT_END = "o.conDtEnd";
        public static final String CON_TO_ID = "o.TCoreAccnByConTo.accnId";
        public static final String CON_TO_NAME = "o.TCoreAccnByConTo.accnName";
        public static final String CON_CHARGE_TO_ID = "o.TCkCtContractChargeByConChargeTo.concId";
        public static final String CON_CHARGE_TO_PLATFORM_FEE = "o.TCkCtContractChargeByConChargeTo.concPltfeeAmt";
        public static final String CON_CHARGE_TO_ADDITIONAL_TAX = "o.TCkCtContractChargeByConChargeTo.concAddtaxAmt";
        public static final String CON_CO_FF_ID = "o.TCoreAccnByConCoFf.accnId";
        public static final String CON_CO_FF_NAME = "o.TCoreAccnByConCoFf.accnName";
        public static final String CON_CHARGE_CO_FF_ID = "o.TCkCtContractChargeByConChargeCoFf.concId";
        public static final String CON_CHARGE_CO_FF_PLATFORM_FEE = "o.TCkCtContractChargeByConChargeCoFf.concPltfeeAmt";
        public static final String CON_CHARGE_CO_FF_ADDITIONAL_TAX = "o.TCkCtContractChargeByConChargeCoFf.concAddtaxAmt";
        public static final String CON_RATE_TABLE = "o.TCkCtRateTable.rtId";
        public static final String CON_STATUS = "o.conStatus";
        public static final String CON_DT_CREATE = "o.conDtCreate";
        public static final String CON_UID_CREATE = "o.conUidCreate";
        public static final String CON_DT_LUPD = "o.conDtLupd";
        public static final String CON_UID_LUPD = "o.conUidLupd";
    }

    public class PropertyName {

        public static final String CON_ID = "conId";
        public static final String CON_NAME = "conName";
        public static final String CON_DESCRIPTION = "conDescription";
        public static final String CON_CCY = "TMstCurrency.ccyCode";
        public static final String CON_DT_START = "conDtStart";
        public static final String CON_DT_END = "conDtEnd";
        public static final String CON_TO = "TCoreAccnByConTo";
        public static final String CON_CHARGE_TO = "TCkCtContractChargeByConChargeTo";
        public static final String CON_CO_FF = "TCoreAccnByConCoFf";
        public static final String CON_CHARGE_CO_FF = "TCkCtContractChargeByConChargeCoFf";
        public static final String CON_RATE_TABLE = "TCkCtRateTable";
        public static final String CON_STATUS = "conStatus";
        public static final String CON_DT_CREATE = "conDtCreate";
        public static final String CON_UID_CREATE = "conUidCreate";
        public static final String CON_DT_LUPD = "conDtLupd";
        public static final String CON_UID_LUPD = "conUidLupd";
    }

    public class State {

        public static final String ASSIGNED = "ASSIGNED";
        public static final String UNASSIGNED = "UNASSIGNED";
        public static final String MAINTENANCE = "MAINTENANCE";
    }
}
