package com.guudint.clickargo.clictruck.common.constant;

public class CkCtVehConstant {

    public class Prefix {

        public static final String PREFIX_CK_CT_VEH = "CKCTVEH";
        public static final String AUDIT_TAG = "CKCT VEHICLE";
    }

    public class Table {

        public static final String NAME = "T_CK_CT_VEH";
        public static final String NAME_DAO = "ckCtVehDao";
        public static final String NAME_ENTITY = "TCkCtVeh";
    }

    public class ColumnParam {

        public static final String VH_ID = "vhId";
        public static final String VH_STATE = "vhState";
        public static final String VH_TYPE = "vhType";
        public static final String VH_COMPANY = "vhCompany";
        public static final String VH_PLATE_NO = "vhPlateNo";
        public static final String VH_CLASS = "vhClass";
        public static final String VH_PHOTO_NAME = "vhPhotoName";
        public static final String VH_PHOTO_LOC = "vhPhotoLoc";
        public static final String VH_LENGTH = "vhLength";
        public static final String VH_WIDTH = "vhWidth";
        public static final String VH_HEIGHT = "vhHeight";
        public static final String VH_WEIGHT = "vhWeight";
        public static final String VH_VOLUME = "vhVolume";
        public static final String VH_CHASSIS_TYPE = "vhChassiType";
        public static final String VH_CHASSIS_NO = "vhChassiNo";
        public static final String VH_IS_MAINTENANCE = "vhIsMaintenance";
        public static final String VH_REMARKS = "vhRemark";
        public static final String VH_GPS_IMEI = "vhGpsImei";
        public static final String VH_ASSIGNED_JOB = "vhAssignedJob";
        public static final String VH_STATUS = "vhStatus";
        public static final String VH_DT_CREATE = "vhDtCreate";
        public static final String VH_UID_CREATE = "vhUidCreate";
        public static final String VH_DT_LUPD = "vhDtLupd";
        public static final String VH_UID_LUPD = "vhUidLupd";
    }

    public class Column {

        public static final String VH_ID = "o.vhId";
        public static final String VH_STATE = "o.TCkCtMstVehState.vhstId";
        public static final String VH_TYPE = "o.TCkCtMstVehType.vhtyId";
        public static final String VH_COMPANY = "o.TCoreAccn.accnId";
        public static final String VH_COMPANY_ACCN_TYPE = "o.TCoreAccn.TMstAccnType.atypId";
        public static final String VH_PLATE_NO = "o.vhPlateNo";
        public static final String VH_CLASS = "o.vhClass";
        public static final String VH_PHOTO_NAME = "o.vhPhotoName";
        public static final String VH_PHOTO_LOC = "o.vhPhotoLoc";
        public static final String VH_LENGTH = "o.vhLength";
        public static final String VH_WIDTH = "o.vhWidth";
        public static final String VH_HEIGHT = "o.vhHeight";
        public static final String VH_WEIGHT = "o.vhWeight";
        public static final String VH_VOLUME = "o.vhVolume";
        public static final String VH_CHASSIS_TYPE = "o.TCkCtMstChassisType.chtyId";
        public static final String VH_CHASSIS_NO = "o.vhChassiNo";
        public static final String VH_IS_MAINTENANCE = "o.vhIsMaintenance";
        public static final String VH_REMARKS = "o.vhRemark";
        public static final String VH_GPS_IMEI = "o.vhGpsImei";
        public static final String VH_ASSIGNED_JOB = "o.TCkJob.jobId";
        public static final String VH_STATUS = "o.vhStatus";
        public static final String VH_DT_CREATE = "o.vhDtCreate";
        public static final String VH_UID_CREATE = "o.vhUidCreate";
        public static final String VH_DT_LUPD = "o.vhDtLupd";
        public static final String VH_UID_LUPD = "o.vhUidLupd";
    }

    public class PropertyName {

        public static final String VH_ID = "vhId";
        public static final String VH_STATE = "TCkCtMstVehState";
        public static final String VH_TYPE = "TCkCtMstVehType";
        public static final String VH_COMPANY = "TCoreAccn";
        public static final String VH_PLATE_NO = "vhPlateNo";
        public static final String VH_CLASS = "vhClass";
        public static final String VH_PHOTO_NAME = "vhPhotoName";
        public static final String VH_PHOTO_LOC = "vhPhotoLoc";
        public static final String VH_LENGTH = "vhLength";
        public static final String VH_WIDTH = "vhWidth";
        public static final String VH_HEIGHT = "vhHeight";
        public static final String VH_WEIGHT = "vhWeight";
        public static final String VH_VOLUME = "vhVolume";
        public static final String VH_CHASSIS_TYPE = "TCkCtMstChassisType";
        public static final String VH_CHASSIS_NO = "vhChassiNo";
        public static final String VH_IS_MAINTENANCE = "vhIsMaintenance";
        public static final String VH_REMARKS = "vhRemark";
        public static final String VH_GPS_IMEI = "vhGpsImei";
        public static final String VH_ASSIGNED_JOB = "TCkJob";
        public static final String VH_STATUS = "vhStatus";
        public static final String VH_DT_CREATE = "vhDtCreate";
        public static final String VH_UID_CREATE = "vhUidCreate";
        public static final String VH_DT_LUPD = "vhDtLupd";
        public static final String VH_UID_LUPD = "vhUidLupd";
    }

    public class State {

        public static final String ASSIGNED = "ASSIGNED";
        public static final String UNASSIGNED = "UNASSIGNED";
        public static final String MAINTENANCE = "MAINTENANCE";
    }
}
