import { Backdrop, Button, Checkbox, CircularProgress, Grid, Popover, Typography } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from "@material-ui/core/styles";
import { CancelOutlined, VisibilityOutlined } from "@material-ui/icons";
import AddCircleIcon from "@material-ui/icons/AddCircleOutlineOutlined";
import BlockIcon from "@material-ui/icons/Block";
import BusinessIcon from "@material-ui/icons/Business";
import ChatBubbleIcon from "@material-ui/icons/ChatBubble";
import ExploreOutlinedIcon from '@material-ui/icons/ExploreOutlined';
import GridOnIcon from '@material-ui/icons/GridOn';
import moment from "moment";
import NearMeOutlinedIcon from '@material-ui/icons/NearMeOutlined';
import ZoomInIcon from "@material-ui/icons/ZoomIn";
import React, { useEffect, useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Button from "app/c1component/C1Button";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1IconButton from "app/c1component/C1IconButton";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import {
    AccountTypes,
    Actions,
    JobStates,
    Roles,
    ShipmentTypes,
    T_CK_CT_DRV,
    T_CK_CT_VEH
} from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate, getValue, isArrayNotEmpty, isEmpty, previewPDF } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

import AssignDriverPopup from "../../popups/AssignDriverPopup";
import JobTrackPopup from "../../popups/JobTrackPopup";
import LocationDashboardPopUp from "../../popups/LocationDashboardPopUp";
import JobUpload from "../../upload/JobUpload";
import {fetchCkAccnData} from "app/views/applications/job/upload/FetchCkAccn"
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import LocalShippingIcon from '@material-ui/icons/LocalShipping';
import useInterval from "../../../../../c1hooks/useInterval";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: "#fff",
    },
}));

/** TruckJobList for Truck Operator */
const TruckJobList = ({
  roleId,
  filterStatus,
  onFilterChipClose,
  onFilterChange
}) => {

    const bdClasses = useStyles();

    const { t } = useTranslation(["buttons", "listing", "ffclaims", "common", "status", "job"]);

    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();

    const [confirm, setConfirm] = useState({ id: null });
    const [lastUpdated, setLastUpdated] = useState(new Date());
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [blLoadDlOpen, setBlLoadDlOpen] = useState(false);
    const [isRefresh, setRefresh] = useState(false);

    const [success, setSuccess] = useState(false);
    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });

    const [shipmentType, setShipmentType] = useState();
    const [showUploadTemplatePopUp, setShowUploadTemplatePopUp] = useState(false);

    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);

    const [openRemarkDialog, setOpenRemarkDialog] = useState(false);
    const [openBlDialog, setOpenBlDialog] = useState(false);
    const [openClaimDialog, setOpenClaimDialog] = useState(false);

    const popupDefaultValue = { jobRemarks: "" };

    const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);
    const [validationError, setValidationError] = useState({});
    const [isDisabled, setDisabled] = useState(false);

    const [fileUploaded, setFileUploaded] = useState(false);

    const [inputData, setInputData] = useState({});
    const [tripListData, setTripListData] = useState([]);
    const [showLocationPopUp, setShowLocationPopUp] = useState(false);
    const [showAssignDriverPopUp, setShowAssignDriverPopUp] = useState(false);
    const [trackingPopup, setTrackingPopup] = useState({ jobId: null, open: false, trips: [], tripIds: [] });

    const [rejectRemarks, setRejectRemarks] = useState({ open: false, msg: null });

    const { user } = useAuth();
    const isCargoOwner = user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_CO.code;
    const isForwarder = user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code;
    const isTruckingOperator =
        user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code;
    const filterByTo = { attribute: "TcoreAccnByJobPartyTo.accnId", value: user?.coreAccn?.accnId };

    const [authorised, setAuthorised] = useState(false);
    const [confirmationObj, setConfirmationObj] = useState({});

    let rowData = [];
    let statusData = []
    const [selectedRowIds, setSelectedRowIds] = useState([]);
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "" });
    const [elementPick, setElementPick] = useState(null)
    const [anchorEl, setAnchorEl] = React.useState(null);
    const openPopover = Boolean(anchorEl);
    const [showMultiSelectActionPopup, setShowMultiSelectActionPopup] = useState(false);

    const [multiOptions, setMultiOptions] = useState([])
    const [multiSelectResponseData, setMultiSelectResponseData] = useState(null)

    const [validationErrors, setValidationErrors] = useState({});

    // state for start & delivered job state
    const [filterPickedState, setFilterPickedState] = useState([])

    //for jobstate filter
    const [stateFilter, setStateFilter] = useState([]);
    const [openJobUploadPopUp, setOpenJobUploadPopUp ] = useState(false);
    const jobUploadRef = useRef(null);
    const [isEnableUpload, setIsEnableUpload] = useState(false);
    let sagawaTo = user?.coreAccn?.accnId;
    const popupColumns = [
        {
            name: "tckCtTripLocationByTrFrom",
            label: t("job:popup.from"),
            options:{
                customBodyRender: (value, tableMeta, updateValue) => value?.tlocLocAddress || value?.locName,
            }
        },
        {
            name: "tckCtTripLocationByTrTo",
            label: t("job:popup.to"),
            options:{
                customBodyRender: (value, tableMeta, updateValue) => value?.tlocLocAddress || value?.locName,
            }
        },
    ];

    let truckCols = [
        {
            name: "checkbox",
            label: "",
            options: {
                sort: false,
                filter: false,
                display: !showHistory,
                viewColumns: false,
                customHeadLabelRender: (columnMeta, handleToggleColumn, sortOrder) => {
                    // let uniqueStatusData = [...new Set(statusData)]
                    return (
                        <Checkbox
                            // disabled={uniqueStatusData.length> 1 ? true:false}
                            disableRipple={true}
                            checked={
                                selectedRowIds.length > 0 &&
                                selectedRowIds.length === rowData.length
                            }
                            onChange={({ target: { checked } }) =>
                                checked ? setSelectedRowIds(rowData) : setSelectedRowIds([])
                            }
                        />
                    );
                },
                customBodyRender: (emptyStr, tableMeta, updateValue) => {

                    const id = tableMeta.rowData[1];
                    const jobStatus = tableMeta.rowData[12];
                    const jobSource = tableMeta.rowData[15];

                    rowData = tableMeta.tableData.map((data) => {
                        if (data[12] === JobStates.ACP.code) {
                            return data[1];
                        }
                        return 0;
                    });

                    rowData = rowData.filter((row) => row != 0);
                    statusData = tableMeta.tableData.map((data) => data[12]);

                    let trip = tableMeta.rowData[14]?.length > 0 ? tableMeta.rowData[14][0] : ""

                    return (
                        <React.Fragment>
                            <Checkbox
                                //disabled={(jobSource !== 'XML' && jobStatus === JobStates.ACP.code) ? true : false}
                                disabled={jobStatus !== JobStates.ACP.code}
                                // disableRipple={true}
                                checked={selectedRowIds.includes(id)}
                                onChange={({ target: { checked } }) => {
                                    try {
                                        if (checked === true) {

                                            setSelectedRowIds(
                                                selectedRowIds
                                                    .filter((rowId) => rowId !== id)
                                                    .concat(id)
                                            )

                                            if (jobStatus === JobStates.ASG.code && trip?.tckCtTripDo?.doNo === null) {
                                                setFilterPickedState(
                                                    filterPickedState
                                                        .filter((rowId) => rowId !== id)
                                                        .concat(id)
                                                )
                                            }
                                            // else if (jobStatus === JobStates.ONGOING.code && trip?.tckCtTripDo?.doSigned === null) {
                                            //     setFilterPickedState(
                                            //         filterPickedState
                                            //             .filter((rowId) => rowId !== id)
                                            //             .concat(id)
                                            //     )
                                            // }
                                        } else {
                                            if (selectedRowIds.length === 1) {
                                                setSelectedRowIds(
                                                    selectedRowIds.filter((rowId) => rowId !== id)
                                                );
                                                setFilterPickedState(
                                                    filterPickedState.filter((rowId) => rowId !== id)
                                                )
                                            } else {
                                                setSelectedRowIds(
                                                    selectedRowIds.filter((rowId) => rowId !== id)
                                                );
                                                setFilterPickedState(
                                                    filterPickedState.filter((rowId) => rowId !== id)
                                                )
                                            }
                                        }
                                    } catch (e) {
                                        console.log(e);
                                    }
                                }}
                            />
                        </React.Fragment>
                    );
                },
            },
        },
        // 1 DO NOT REMOVE, USED FOR FILTER ETC tableMeta.rowData[0]
        {
            name: "jobId",
            label: t("listing:trucklist.jobid"),
        },
        {
            name: "tckJob.tckMstShipmentType.shtId",
            label: t("listing:trucklist.type"),
            options: {
                filterType: "dropdown",
                filterOptions: {
                    names: Object.keys(ShipmentTypes),
                    renderValue: (v) => {
                        return ShipmentTypes[v].desc;
                    },
                },
            },
        },
        {
            name: "tcoreAccnByJobPartyCoFf.accnName",
            label: t("listing:trucklist.coff"),
        },
        {
            name: "jobDtDelivery",
            label: t("listing:trucklist.dtdelivery"),
            options: {
                filter: showHistory,
                filterType: "custom",
                display: showHistory ? true : "excluded",
                customFilterListOptions: {
                    render: (v) => v.map((l) => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    },
                },
                filterOptions: {
                    display: customFilterDateDisplay,
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
            },
        },
        {
            name: "jobShipmentRef",
            label: t("listing:trucklist.shipref"),
        },
        {
            name: "pickUp",
            label: t("listing:trucklist.pickup"),
            options: {
                sort: false,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => value?.tlocLocAddress || "",
            },
        },
        {
            name: "lastDrop.tlocLocAddress",
            label: t("listing:trucklist.lastdrop"),
            options: {
                sort: false,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    const jobId = tableMeta.rowData[1];
                    const jobList = tableMeta?.rowData[14]?.length > 1;

                    return (
                        <React.Fragment>
                            {jobList ? (
                                <C1LabeledIconButton
                                    tooltip={t("buttons:viewLoc")}
                                    label={t("buttons:view")}
                                    action={() => handleLocationPopUpShow(jobId)}
                                >
                                    <ZoomInIcon />
                                </C1LabeledIconButton>
                            ) : (
                                <span>{value}</span>
                            )}
                        </React.Fragment>
                    );
                },
            },
        },
        {
            name : "tckCtDrv.drvName",
            label : "Assigned Driver",
            options:{
                filter : true
            }
        },
        {
            name : "tckCtVeh.vhPlateNo",
            label : "Assigned Truck",
            options:{
                filter : true
            }
        },
        {
            name: "jobDtCreate",
            label: t("listing:trucklist.dtCreate"),
            options: {
                filter: isCargoOwner,
                filterType: "custom",
                display: isCargoOwner ? true : "excluded",
                customFilterListOptions: {
                    render: (v) => v.map((l) => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    },
                },
                filterOptions: {
                    display: customFilterDateDisplay,
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
            },
        },
        {
            name: "jobDtLupd",
            label: t("listing:trucklist.dtLupd"),
            options: {
                filter: user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code,
                filterType: "custom",
                display:
                    user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code
                        ? true
                        : "excluded",
                customFilterListOptions: {
                    render: (v) => v.map((l) => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    },
                },
                filterOptions: {
                    display: customFilterDateDisplay,
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
            },
        },

        {
            name: "tckJob.tckMstJobState.jbstId",
            label: t("listing:trucklist.status"),
            options: {
                filter: true,
                filterType: "dropdown",
                filterOptions: {
                    names: isArrayNotEmpty(stateFilter) ? stateFilter : Object.keys(JobStates),
                    renderValue: (v) => {
                        return JobStates[v].desc;
                    },
                },
                customFilterListOptions: {
                    render: (v) => {
                        return JobStates[v].desc;
                    },
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                },
            },
        },
        {
            name: "action",
            label: t("listing:common.action"),
            options: {
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => {
                    return { style: { textAlign: "center" } };
                },
                customBodyRender: (value, tableMeta, updateValue) => {

                    const jobId = tableMeta.rowData[1];
                    const status = tableMeta.rowData[12];
                    const trips = tableMeta?.rowData[14];
                    return (
                        <Grid container direction="row" justifyContent='center' alignItems="center" style={{ minWidth: "150px" }}>
                            <Grid container direction="row" justifyContent="flex-end" alignItems="center" spacing={2}>
                                <Grid item xs={4}>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:view")}
                                        label={t("buttons:view")}
                                        action={() =>
                                            history.push({ pathname: `/applications/services/job/truck/view`, state: { jobId } })
                                        }
                                    >
                                        <VisibilityOutlined />
                                    </C1LabeledIconButton>
                                </Grid>
                                <Grid item xs={4}>
                                    {(status === JobStates.DRF.code ||
                                        status === JobStates.NEW.code) && (
                                            <C1LabeledIconButton
                                                tooltip={t("buttons:delete")}
                                                label={t("buttons:delete")}
                                                action={(e) => handleCancelConfirm(e, jobId)}
                                            >
                                                <CancelOutlined />
                                            </C1LabeledIconButton>
                                        )}
                                    {status === JobStates.SUB.code && (
                                        <C1LabeledIconButton
                                            tooltip={t("listing:billOfLading.action.reject")}
                                            label={t("listing:billOfLading.action.reject")}
                                            action={(e) => handleRejectConfirm(e, jobId)}
                                        >
                                            <BlockIcon />
                                        </C1LabeledIconButton>
                                    )}
                                    {([JobStates.ONGOING.code, JobStates.DLV.code, JobStates.BILLED.code, JobStates.VER.code, JobStates.APP.code].includes(status)) &&
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:track")}
                                            label={t("buttons:track")}
                                            action={(e) => handleTrackingPopup(e, jobId, trips)}>
                                            <ExploreOutlinedIcon />
                                        </C1LabeledIconButton>}
                                </Grid>
                            </Grid>
                        </Grid>
                    );
                },
            },
        },
        {
            name: "tckCtTripList",
            label: "",
            options: {
                display: "excluded",
                filter: false
            },
        },
        {//13
            name: "jobSource",
            label: "",
            options: {
                display: "excluded",
                filter: false
            },
        }
    ];
    if (sagawaTo === 'SSA'){
        truckCols = [
            {
                name: "checkbox",
                label: "",
                options: {
                    sort: false,
                    filter: false,
                    display: !showHistory,
                    viewColumns: false,
                    customHeadLabelRender: (columnMeta, handleToggleColumn, sortOrder) => {
                        // let uniqueStatusData = [...new Set(statusData)]
                        return (
                            <Checkbox
                                // disabled={uniqueStatusData.length> 1 ? true:false}
                                disableRipple={true}
                                checked={
                                    selectedRowIds.length > 0 &&
                                    selectedRowIds.length === rowData.length
                                }
                                onChange={({ target: { checked } }) =>
                                    checked ? setSelectedRowIds(rowData) : setSelectedRowIds([])
                                }
                            />
                        );
                    },
                    customBodyRender: (emptyStr, tableMeta, updateValue) => {

                        const id = tableMeta.rowData[1];
                        const jobStatus = tableMeta.rowData[17];
                        console.log("tableMeta", tableMeta)
                        console.log("status", jobStatus)
                        rowData = tableMeta.tableData.map((data) => {
                            if (data[17] === JobStates.ACP.code) {
                                return data[1];
                            }
                            return 0;
                        });

                        rowData = rowData.filter((row) => row != 0);
                        statusData = tableMeta.tableData.map((data) => data[17]);
                        let trip = tableMeta.rowData[17]?.length > 0 ? tableMeta.rowData[17][0] : ""

                        return (
                            <React.Fragment>
                                <Checkbox
                                    //disabled={(jobSource !== 'XML' && jobStatus === JobStates.ACP.code) ? true : false}
                                    disabled={jobStatus !== JobStates.ACP.code}
                                    // disableRipple={true}
                                    checked={selectedRowIds.includes(id)}
                                    onChange={({ target: { checked } }) => {
                                        try {
                                            if (checked === true) {

                                                setSelectedRowIds(
                                                    selectedRowIds
                                                        .filter((rowId) => rowId !== id)
                                                        .concat(id)
                                                )

                                                if (jobStatus === JobStates.ASG.code && trip?.tckCtTripDo?.doNo === null) {
                                                    setFilterPickedState(
                                                        filterPickedState
                                                            .filter((rowId) => rowId !== id)
                                                            .concat(id)
                                                    )
                                                }
                                                // else if (jobStatus === JobStates.ONGOING.code && trip?.tckCtTripDo?.doSigned === null) {
                                                //     setFilterPickedState(
                                                //         filterPickedState
                                                //             .filter((rowId) => rowId !== id)
                                                //             .concat(id)
                                                //     )
                                                // }
                                            } else {
                                                if (selectedRowIds.length === 1) {
                                                    setSelectedRowIds(
                                                        selectedRowIds.filter((rowId) => rowId !== id)
                                                    );
                                                    setFilterPickedState(
                                                        filterPickedState.filter((rowId) => rowId !== id)
                                                    )
                                                } else {
                                                    setSelectedRowIds(
                                                        selectedRowIds.filter((rowId) => rowId !== id)
                                                    );
                                                    setFilterPickedState(
                                                        filterPickedState.filter((rowId) => rowId !== id)
                                                    )
                                                }
                                            }
                                        } catch (e) {
                                            console.log(e);
                                        }
                                    }}
                                />
                            </React.Fragment>
                        );
                    },
                },
            },
            // 1 DO NOT REMOVE, USED FOR FILTER ETC tableMeta.rowData[0]
            {
                name: "jobId",
                label: t("listing:trucklist.jobid"),
            },
            {
                name: "jobDtPlan",
                label: t("listing:trucklist.jobDtPlan"),
                options: {
                    filter: true,
                    filterType: 'custom',
                    display: true,
                    filterOptions: {
                        display: customFilterDateDisplay
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, false);

                    }
                }
            },
            {
                name: "mawb",
                label: "MAWB",
                options: {
                    sort: false,
                    filter: true,
                    customBodyRender: (value, tableMeta, updateValue) => value || "-",
                },
            },
            {
                name: "hawb",
                label: "HAWB",
                options: {
                    sort: false,
                    filter: true,
                    customBodyRender: (value, tableMeta, updateValue) => value || "-",
                },
            },
            {
                name: "tckJob.tckMstShipmentType.shtId",
                label: t("listing:trucklist.type"),
                options: {
                    filterType: "dropdown",
                    filterOptions: {
                        names: Object.keys(ShipmentTypes),
                        renderValue: (v) => {
                            return ShipmentTypes[v].desc;
                        },
                    },
                },
            },
            {
                name: "tcoreAccnByJobPartyCoFf.accnName",
                label: t("listing:trucklist.coff"),
            },
            {
                name: "jobDtDelivery",
                label: t("listing:trucklist.dtdelivery"),
                options: {
                    filter: showHistory,
                    filterType: "custom",
                    display: showHistory ? true : "excluded",
                    customFilterListOptions: {
                        render: (v) => v.map((l) => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        },
                    },
                    filterOptions: {
                        display: customFilterDateDisplay,
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, true);
                    },
                },
            },
            {
                name: "jobShipmentRef",
                label: t("listing:trucklist.shipref"),
            },
            {
                name: "sumQty",
                label: "Qty ",
            },
            {
                name: "sumWeight",
                label: "Weight ",
            },
            {
                name: "pickUp.tlocLocAddress",
                label: t("listing:trucklist.pickup"),
                options: {
                    sort: false,
                    filter: true,
                },
            },
            {
                name: "lastDrop.tlocLocAddress",
                label: t("listing:trucklist.lastdrop"),
                options: {
                    sort: false,
                    filter: true,
                    customBodyRender: (value, tableMeta, updateValue) => {
                        const jobId = tableMeta.rowData[1];
                        const jobType = tableMeta.rowData[5];
                        const jobList = tableMeta?.rowData[13]?.length > 1;
                        console.log("tableMeta?.rowData", tableMeta.rowData)
                        return (
                            <React.Fragment>
                                {jobType === "DOMESTIC" && jobList ? (
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:viewLoc")}
                                        label={t("buttons:view")}
                                        action={() => handleLocationPopUpShow(jobId)}
                                    >
                                        <ZoomInIcon />
                                    </C1LabeledIconButton>
                                ) : (
                                    <span>{value}</span>
                                )}
                            </React.Fragment>
                        );
                    },
                },
            },
            {
                name : "tckCtDrv.drvName",
                label : "Assigned Driver",
                options:{
                    filter : true
                }
            },
            {
                name : "tckCtVeh.vhPlateNo",
                label : "Assigned Truck",
                options:{
                    filter : true
                }
            },
            {
                name: "jobDtCreate",
                label: t("listing:trucklist.dtCreate"),
                options: {
                    filter: isCargoOwner,
                    filterType: "custom",
                    display: isCargoOwner ? true : "excluded",
                    customFilterListOptions: {
                        render: (v) => v.map((l) => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        },
                    },
                    filterOptions: {
                        display: customFilterDateDisplay,
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, true);
                    },
                },
            },
            {
                name: "jobDtLupd",
                label: t("listing:trucklist.dtLupd"),
                options: {
                    filter: user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code,
                    filterType: "custom",
                    display:
                        user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code
                            ? true
                            : "excluded",
                    customFilterListOptions: {
                        render: (v) => v.map((l) => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        },
                    },
                    filterOptions: {
                        display: customFilterDateDisplay,
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, true);
                    },
                },
            },

            {
                name: "tckJob.tckMstJobState.jbstId",
                label: t("listing:trucklist.status"),
                options: {
                    filter: true,
                    filterType: "dropdown",
                    filterOptions: {
                        names: isArrayNotEmpty(stateFilter) ? stateFilter : Object.keys(JobStates),
                        renderValue: (v) => {
                            return JobStates[v].desc;
                        },
                    },
                    customFilterListOptions: {
                        render: (v) => {
                            return JobStates[v].desc;
                        },
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return getStatusDesc(value);
                    },
                },
            },
            {
                name: "action",
                label: t("listing:common.action"),
                options: {
                    filter: false,
                    sort: false,
                    display: true,
                    viewColumns: false,
                    setCellHeaderProps: () => {
                        return { style: { textAlign: "center" } };
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {

                        const jobId = tableMeta.rowData[1];
                        const status = tableMeta.rowData[17];
                        const trips = tableMeta?.rowData[19];
                        return (
                            <Grid container direction="row" justifyContent='center' alignItems="center" style={{ minWidth: "150px" }}>
                                <Grid container direction="row" justifyContent="flex-end" alignItems="center" spacing={2}>
                                    <Grid item xs={4}>
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:view")}
                                            label={t("buttons:view")}
                                            action={() =>
                                                history.push({ pathname: `/applications/services/job/truck/view`, state: { jobId } })
                                            }
                                        >
                                            <VisibilityOutlined />
                                        </C1LabeledIconButton>
                                    </Grid>
                                    <Grid item xs={4}>
                                        {(status === JobStates.DRF.code ||
                                            status === JobStates.NEW.code) && (
                                            <C1LabeledIconButton
                                                tooltip={t("buttons:delete")}
                                                label={t("buttons:delete")}
                                                action={(e) => handleCancelConfirm(e, jobId)}
                                            >
                                                <CancelOutlined />
                                            </C1LabeledIconButton>
                                        )}
                                        {status === JobStates.SUB.code && (
                                            <C1LabeledIconButton
                                                tooltip={t("listing:billOfLading.action.reject")}
                                                label={t("listing:billOfLading.action.reject")}
                                                action={(e) => handleRejectConfirm(e, jobId)}
                                            >
                                                <BlockIcon />
                                            </C1LabeledIconButton>
                                        )}
                                        {([JobStates.ONGOING.code, JobStates.DLV.code, JobStates.BILLED.code, JobStates.VER.code, JobStates.APP.code].includes(status)) &&
                                            <C1LabeledIconButton
                                                tooltip={t("buttons:track")}
                                                label={t("buttons:track")}
                                                action={(e) => handleTrackingPopup(e, jobId, trips)}>
                                                <ExploreOutlinedIcon />
                                            </C1LabeledIconButton>}
                                    </Grid>
                                </Grid>
                            </Grid>
                        );
                    },
                },
            },
            {
                name: "tckCtTripList",
                label: "",
                options: {
                    display: "excluded",
                    filter: false
                },
            },
            {//13
                name: "jobSource",
                label: "",
                options: {
                    display: "excluded",
                    filter: false
                },
            }
        ];
    }
    const handleInputChange = (e) => {
        const elName = e.target.name;
        if (elName === "tckCtDrv.drvName") {
            sendRequest(`${T_CK_CT_DRV}&mDataProp_1=drvLicenseNo&sSearch_1=${e.target.value}`, "getDriver")
        } else if (elName === "tckCtVeh.vhPlateNo") {
            sendRequest(`${T_CK_CT_VEH}&mDataProp_1=vhPlateNo&sSearch_1=${e.target.value}`, "getTruck")
        } else {
            setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
        }

    };

    const handleDeleteConfirm = (e, id) => {
        e.preventDefault();
        setLoading(false);
        setRefresh(false);
        setConfirm({ ...confirm, id });
        setOpen(true);
        setOpenActionConfirm({ ...openActionConfirm, action: "DELETE", open: true });
    };
    // <====== multi select, pop over & warning message ======>

    const handleMultiSelectedJob = (e, type) => {
        setAnchorEl(null);
        if (type === 'REJECT') {
            handleRejectConfirm(e, selectedRowIds);
        } else if (type === 'ASSIGN') {
            setValidationErrors({})
            setInputData({})
            setShowAssignDriverPopUp(true);
        } else {
            setLoading(true)
            let reqBody = {
                action: type,
                accType: AccountTypes.ACC_TYPE_TO.code,
                role: Roles.OFFICER.code,
                id: selectedRowIds,
            };
            sendRequest("/api/v1/clickargo/clictruck/jobs", "multiSelect", "post", reqBody)
        }

    };

    /** Handles reject job for both multi-select and individual reject */
    const handleRejectJob = (e) => {
        setRejectRemarks({ ...rejectRemarks, open: false })
        if (selectedRowIds && selectedRowIds.length > 0) {
            setLoading(true);

            let reqBody = {
                action: "REJECT",
                remarks: rejectRemarks?.msg,
                accType: AccountTypes.ACC_TYPE_TO.code,
                role: Roles.OFFICER.code,
                id: selectedRowIds,
            };
            sendRequest("/api/v1/clickargo/clictruck/jobs", "multiSelect", "post", reqBody);

        } else {
            setLoading(true);
            sendRequest(`/api/v1/clickargo/clictruck/job/truck/${confirm.id}/reject`, "rejectFromList", "PUT", rejectRemarks.msg);
        }
    }

    const handleAssignDriver = (e) => {
        setLoading(true);

        const obj = selectedRowIds;
        const id = Object.values(obj);

        let reqBody = {
            ...inputData,
            action: "ASSIGN",
            id: id,
            accType: user?.coreAccn?.TMstAccnType?.atypId,
            role: Roles.OFFICER.code,
        };

        sendRequest("/api/v1/clickargo/clictruck/jobs/multi-assign", "assignDriver", "post", reqBody);
    }

    const handleOpenPopover = (e) => {
        let query = selectedRowIds.join(';')
        setElementPick(e.currentTarget)
        if (selectedRowIds.length == 0) {
            setWarningMessage({ open: true, msg: t("listing:payments.errorNoSelectTitle") });
        } else if (filterPickedState.length > 0) {
            setWarningMessage({ open: true, msg: t("listing:payments.errorCompletedReq") });
        } else {
            sendRequest(`api/v1/clickargo/clictruck/jobs/action?accnType=${AccountTypes.ACC_TYPE_TO.code}&role=${Roles.OFFICER.code}&jobId=${query}`, "getMultiOptions", "get")
        }
    };

    const handleOpenAssignPopup = (e) => {
        let query = selectedRowIds.join(';')
        if (selectedRowIds.length == 0) {
            setWarningMessage({ open: true, msg: t("listing:payments.errorNoSelectTitle") });
        } else {
            setShowAssignDriverPopUp(true)
        }
    }

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleMultiSelectActionPopup = () => {
        if (showMultiSelectActionPopup === true) {
            setAnchorEl(null);
            setRefresh(false)
            setSelectedRowIds([]);
            setShowMultiSelectActionPopup(false);
        } else {
            setShowMultiSelectActionPopup(true);
        }
    };

    const messagePopup = React.useMemo(() => {
        let data = multiSelectResponseData?.failed;
        const keyMappings = {
            "jobDtPlan": "Plan Date",
            "jobShipmentRef": "Shipment Ref",
            "tcoreAccnByJobPartyTo.accnId": "Party Details",
            "tckCtTripLocationByTrDepot.tckCtLocation.locId": "Depot",
            "tckCtTripLocationByTrFrom.tckCtLocation.locId": "From",
            "tckCtTripLocationByTrTo.tckCtLocation.locId": "To",
            "tckCtTripLocationByTrFrom.tlocDtLoc": "Schedule Details",
            "tckCtTripLocationByTrTo.tlocDtLoc": "Schedule Details",
            "tckCtTripLocationByTrTo.tlocMobileNo": "Mobile Number",
            "tckCtMstVehType.vhtyId": "Truck Type",
        };

        if (data?.length > 0) {
            return data.map((item) => {
                let reasonText = item.reason;
                try {
                    const parsedReason = JSON.parse(item.reason);
                    reasonText = Object.entries(parsedReason).map(([key, value]) => {
                        if (key === "invalidTabs.jobDetails" || key === "invalidTabs.fmTrip") {
                            return null;  // Skip the key you don't want to render
                        }
                        const displayKey = keyMappings[key] || key;
                        return (
                            <div key={key}>
                                <strong>{displayKey}</strong>: <span style={{ color: "red" }}>{value}</span>
                            </div>
                        );
                    }).filter(Boolean); // Filter out null/empty values
                } catch (e) {
                    reasonText = item.reason; // Fall back to string if parsing fails
                }

                return (
                    <div key={item.id}>
                        <strong>Job ID</strong>: {item.id}
                        <br />
                        <strong>Job Details</strong>:
                        <div>{reasonText}</div>
                        <br />
                    </div>
                );
            });
        } else {
            if (multiSelectResponseData) {
                return <div style={{ color: "green" }}>All processes are successful!</div>;
            } else {
                return null;
            }
        }
    }, [multiSelectResponseData]);
    // <====== multi select, pop over & warning message ======>

    const handleCancelConfirm = (e, id) => {
        e.preventDefault();
        setLoading(false);
        setRefresh(false);
        setConfirm({ ...confirm, id });
        setOpen(true);
        setOpenActionConfirm({ ...openActionConfirm, action: "CANCEL", open: true });
    };

    // TODO: No status REJECTED, not used at this time
    const handleRejectConfirm = (e, id) => {
        e.preventDefault();
        setLoading(false);
        setRefresh(false);
        if (Array.isArray(id)) {
            id = id.join("\n");
        }

        setConfirm({ ...confirm, id });
        setOpen(true);
        setOpenActionConfirm({ ...openActionConfirm, action: "REJECT", open: true });
    };

    const handleTrackingPopup = (e, id, trips) => {
        e.preventDefault();
        const tripIds = trips?.map(val => val?.trId);
        setTrackingPopup({ ...trackingPopup, open: true, jobId: id, trips: trips, tripIds: tripIds});
    }

    const handleActionHandler = (e) => {
        if (confirm && !confirm.id) return;

        setLoading(true);
        if (openActionConfirm && openActionConfirm.action === "DELETE") {
            setOpen(false);
        } else if (openActionConfirm && openActionConfirm.action === "CANCEL") {
            setOpen(false);
            sendRequest(
                "/api/v1/clickargo/clictruck/job/truck/" + confirm.id,
                "cancelJob",
                "get",
                null
            );
        } else if (openActionConfirm && openActionConfirm.action === "REJECT") {
            setOpen(false);
            setRejectRemarks({ ...rejectRemarks, open: true })
            setLoading(false)
        }
    };

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === "history");
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    };

    const handlePopUpBtnAddClick = (e) => {
        let validateErr = validateFields();
        setValidationError(validateErr);
        if (isEmpty(validateErr)) {
            setOpenRemarkDialog(false);
            setRefresh(false);
            if (openActionConfirm && openActionConfirm.action === "REJECT") {
                sendRequest(
                    "/api/v1/clickargo/clicdo/job/doiCo/" + confirm.id,
                    "rejectJob",
                    "get",
                    null
                );
            }
        }
    };


    // validate required field used for remark popup
    const validateFields = () => {
        let errors = {};
        if (!popUpDetails?.jobRemarks) {
            errors.jobRemarks = t("common:validationMsgs.required");
        }
        return errors;
    };


    useEffect(() => {
        const filter = filterBy;
        if (isTruckingOperator) {
            filter.push(filterByTo);
        }
        setFilterBy(filter);

        //fetch the jobtruckfilter
        sendRequest("/api/v1/clickargo/clictruck/accnconfig/truckstatefilter",
            "getStateFilter",
            "get");
    }, []);

    useEffect(() => {
        if (showHistory) {
            const filter = [{ attribute: "history", value: "history" }];
            if (isTruckingOperator) {
                filter.push(filterByTo);
            }
            setFilterBy(filter);
        } else {
            const filter = [{ attribute: "history", value: "default" }];
            if (isTruckingOperator) {
                filter.push(filterByTo);
            }
            setFilterBy(filter);
        }
    }, [showHistory]);
    const [delay, setDelay] = useState(1000 * 60 * 5);
    const callback = () => {
        console.log(`[${new Date().toISOString()}] Refresh triggered: Fetching server data`);
        setLastUpdated(new Date());
        const URI = "/api/v1/clickargo/clictruck/job/truck/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=jobDtLupd&mDataProp_1=history&sSearch_1=default&iColumns=2";
        sendRequest(URI, '', 'get', null);
    };

    useInterval(callback, delay);

    // useEffect(() => {
    //
    //         const intervalId = setInterval(() => {
    //             setRefresh(prev => !prev);
    //             setLastUpdated(new Date());
    //         }, 300000);
    //
    //         return () => clearInterval(intervalId);
    //
    // }, []);

    useEffect(() => {
        setTimeout(() => setSnackBarOptions(defaultSnackbarValue), 100);
        if (!isLoading && !error && !validation && res) {
            switch (urlId) {
                case "cancelJob":
                    sendRequest(
                        "/api/v1/clickargo/clictruck/job/truck/" + confirm.id,
                        "cancelled",
                        "put",
                        { ...res.data, action: "CANCEL" }
                    );
                    break;
                case "cancelled":
                    setRefresh(true);
                    setFilterBy([{ attribute: "history", value: "default" }]);
                    setSuccess(true);
                    setSnackBarState({
                        ...snackBarState,
                        msg: t("listing:coJob.msg.cancelSuccess"),
                        open: true,
                    });
                    setLoading(false);
                    break;
                case "getJob": {
                    setLoading(false);
                    break;
                }
                case "getTripList": {
                    if (res?.data?.tckCtTripList != null) {
                        setTripListData(res?.data?.tckCtTripList);
                    } else {
                        setTripListData([]);
                    }

                    break;
                }
                case "getMultiOptions": {
                    let dataOpt = res?.data
                    if (dataOpt?.actions?.length > 0) {
                        setMultiOptions(dataOpt?.actions);
                        setAnchorEl(elementPick);
                    } else {
                        setElementPick(null);
                        setAnchorEl(null);
                        setSelectedRowIds([]);
                        setWarningMessage({
                            open: true,
                            msg: t("listing:payments.errorThereIsNoOption"),
                        });
                    }
                    break;
                }
                case "multiSelect": {
                    if (res?.data) {
                        if (res?.data?.suspended === true) {
                            setWarningMessage({ open: true, msg: t("listing:payments.errorYourAccountIsSuspended") });
                            setLoading(false)
                        } else {
                            setMultiSelectResponseData(res?.data)
                            setRefresh(true);
                            setSuccess(true);
                            setLoading(false)
                            setTimeout(() => setShowMultiSelectActionPopup(true), 500);
                            setSelectedRowIds([]);
                            setRejectRemarks({ ...rejectRemarks, msg: null })
                        }
                    }
                    break;
                }
                case "rejectFromList": {
                    setConfirm({ id: null })
                    setRejectRemarks({ ...rejectRemarks, msg: null, open: false });
                    setRefresh(true);
                    setTimeout(() => {
                        setLoading(false);
                        setRefresh(false);
                    }, 500)
                    break;

                }
                case "assignDriver": {
                    setRefresh(true);
                    setElementPick(null);
                    setAnchorEl(null);
                    setSelectedRowIds([]);
                    setSuccess(true);
                    setSnackBarState({
                        ...snackBarState,
                        msg: t("listing:coJob.msg.assignSuccess"),
                        open: true,
                    });
                    setLoading(false);
                    setShowAssignDriverPopUp(false)
                    setValidationErrors({});
                    break;
                }
                case "getStateFilter": {
                    setStateFilter([...res?.data]);
                    break;
                }
                case "downloadExcelTempate": {
                  if (res?.data) {
                    previewPDF(user?.coreAccn.accnId + "_Template.xlsx", res?.data);
                  }
                  break;
                }
                default:
                    break;
            }
        }
        // eslint-disable-next-line

        if (error) {
            setLoading(false);
        }

        if (validation) {

            setValidationErrors({ ...validation });
            setLoading(false);
        }

    }, [isLoading, res, error, urlId, validation]);

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: t("common:common.msg.deleted"),
        severity: "success",
    });

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    const popUpAddHandler = () => {
        setInputData({});
        setOpenBlDialog(true);
        setValidationError({});
    };

    const handleLocationPopUpShow = (id) => {
        sendRequest(`/api/v1/clickargo/clictruck/job/truck/${id}`, "getTripList", "get");
        setShowLocationPopUp(true);
    };

    const setOpenJobUploadPopUpWrap = (isOpenJobUploadPopup) => {
        setOpenJobUploadPopUp(isOpenJobUploadPopup);
        // set Refresh to false when popup dialog
        // set Refresh to true when close dialog
        setRefresh(!isOpenJobUploadPopup);
    }


    const downloadExcelTempate = () => {

        sendRequest(
          `/api/v1/clickargo/clictruck/jobUpload/downloadExcelTemplate`,
          "downloadExcelTempate"
        );
    }

    useEffect(()=> {

        let ckAccn;
        const fetchData = async () => {
          ckAccn = await fetchCkAccnData();
          
          if ( ckAccn?.caccnExcelTemplate &&
            ckAccn?.caccnExcelTemplate?.length > 0) {
            setIsEnableUpload(true);
          }
        };
        fetchData();
    
      }, [])

    let snackBar = null;
    if (success) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = (
            <Snackbar
                anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
                open={snackBarState.open}
                onClose={handleCloseSnackBar}
                autoHideDuration={3000}
                key={anchorOriginV + anchorOriginH}
            >
                <C1Alert onClose={handleCloseSnackBar} severity={snackBarState.severity}>
                    {snackBarState.msg}
                </C1Alert>
            </Snackbar>
        );
    }

    let elAction = (
        <C1IconButton tooltip={t("common:tooltip.rmkAdd")} childPosition="right">
            <AddCircleIcon
                color="primary"
                fontSize="large"
                onClick={( e ) => handlePopUpBtnAddClick(e)}
            />
        </C1IconButton>
    );

    const lastUpdate = <h6 style={{position:"absolute",color: "rgba(0, 0, 0, 0.54)", paddingTop: "10px"}}>Last Updated: {moment(lastUpdated).format("DD/MM/YYYY HH:mm:ss")}</h6>;

    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            {snackBar}

            {/* Confirmation Popup */}
            {confirm && confirm.id && (
                <ConfirmationDialog
                    open={open}
                    title={t("listing:coJob.popup.confirmation")}
                    text={t("listing:coJob.msg.confirmation", {
                        action: openActionConfirm?.action,
                        id: confirm.id,
                    })}
                    onYesClick={() => handleActionHandler()}
                    onConfirmDialogClose={() => {
                        setOpen(false)
                        setConfirm({ id: null })
                    }}
                />
            )}

            <C1PopUp
                title={t("common:remarks.title")}
                openPopUp={openRemarkDialog}
                setOpenPopUp={setOpenRemarkDialog}
                actionsEl={isDisabled ? null : elAction}
            />

            <C1PopUp
                maxWidth={"md"}
                title={`Rejection Remarks`}
                openPopUp={rejectRemarks?.open}
                setOpenPopUp={setRejectRemarks}
                actionsEl={<C1IconButton disabled={!rejectRemarks?.msg} tooltip={t("buttons:submit")} childPosition="right">
                    <NearMeOutlinedIcon
                        color="primary"
                        fontSize="large"
                        onClick={(e) => { handleRejectJob(e) }}
                    >
                    </NearMeOutlinedIcon>
                </C1IconButton>}>
                <C1InputField required name="rejectRemarks.msg" value={getValue(rejectRemarks?.msg)} onChange={(e) => setRejectRemarks({ ...rejectRemarks, msg: e?.target?.value })} />
            </C1PopUp>

            <C1PopUp
                title={"Upload Template"}
                openPopUp={showUploadTemplatePopUp}
                setOpenPopUp={setShowUploadTemplatePopUp}
                maxWidth={"sm"}
            >
                <React.Fragment>
                    <Grid container spacing={3}>
                        <Grid container item xs={12}>
                            <C1FileUpload
                                name={"UploadTemplate"}
                                label={"Upload Template"}
                                inputLabel={"Template Picked"}
                            />
                        </Grid>
                    </Grid>
                    <Grid container alignItems="center" item xs={12}>
                        <C1Button
                            text={t("listing:coJob.button.create")}
                            color="primary"
                            type="submit"
                            onClick={() => setShowUploadTemplatePopUp(false)}
                        />
                    </Grid>
                </React.Fragment>
            </C1PopUp>

            <LocationDashboardPopUp
                openPopUp={showLocationPopUp}
                setOpenPopUp={setShowLocationPopUp}
                tripListData={tripListData}
                title={t("job:tripDetails.domestic.location")}
                columns={popupColumns} />

            <AssignDriverPopup
                openPopUp={showAssignDriverPopUp}
                setOpenPopUp={setShowAssignDriverPopUp}
                title={t("job:tabs.driver")}
                handleAssignDriver={handleAssignDriver}
                inputData={inputData}
                setInputData={setInputData}
                errors={validationErrors} />

            <Popover
                open={openPopover}
                anchorEl={anchorEl}
                onClose={handleClose}
                anchorOrigin={{
                    vertical: "bottom",
                    horizontal: "left",
                }}
            >
                <Grid
                    container
                    direction={"column"}
                    // alignItems={"flex-start"}
                    style={{ minWidth: "100px", padding: "10px" }}
                >
                    {multiOptions.map((item, i) => {
                        return (
                            <Grid item key={i}>
                                <Button
                                    style={{ textTransform: "none" }}
                                    onClick={(e) => handleMultiSelectedJob(e, item)}
                                >
                                    <Typography>{Actions[item].text}</Typography>
                                </Button>
                            </Grid>
                        )
                    })}
                </Grid>
            </Popover>

            <C1PopUp
                title={`Multi-records Request: ${multiSelectResponseData?.action ? multiSelectResponseData?.action : ""}`}
                openPopUp={showMultiSelectActionPopup}
                setOpenPopUp={handleMultiSelectActionPopup}
                maxWidth={"md"}
            >
                <C1TabContainer>
                    <Grid item lg={6} md={6} xs={12}>
                        <C1CategoryBlock icon={<BusinessIcon />} title={"Request Details"}>
                            <C1InputField disabled value={multiSelectResponseData?.id?.length} label="No. Records" />
                            <C1InputField disabled value={multiSelectResponseData?.success?.length} label="No. Success" />
                            <C1InputField disabled value={multiSelectResponseData?.failed?.length} label="No. Failed" />
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item lg={6} md={6} xs={12}>
                        <C1CategoryBlock icon={<ChatBubbleIcon/>} title={"Exceptions"}>
                            <div style={{whiteSpace: 'pre-wrap', paddingTop: '10px'}}>
                                {messagePopup}
                            </div>
                        </C1CategoryBlock>
                    </Grid>
                </C1TabContainer>
            </C1PopUp>

            {trackingPopup?.open && <C1PopUp
                title={`Track Details`}
                openPopUp={trackingPopup?.open}
                setOpenPopUp={() => setTrackingPopup({ ...trackingPopup, open: false, jobId: null })}
                maxWidth={"lg"}>
                <JobTrackPopup
                    jobId={trackingPopup?.jobId}
                    trips={trackingPopup?.trips}
                    tripIds={trackingPopup?.tripIds}
                />
            </C1PopUp>}
 
            <C1PopUp
                maxWidth={"lg"}
                title={"Upload Excel File"}
                openPopUp={openJobUploadPopUp}
                setOpenPopUp={setOpenJobUploadPopUpWrap} >
                <JobUpload ref={jobUploadRef}/>
            </C1PopUp>

            <DataTable
                url="/api/v1/clickargo/clictruck/job/truck"
                columns={truckCols}
                title=""
                showSubTile={!showHistory}
                subTile={lastUpdate}
                defaultOrder="jobDtLupd"
                defaultOrderDirection="desc"
                isServer={true}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={true}
                isRefresh={isRefresh}
                isShowFilterChip
                filterBy={filterBy}
                guideId={""}
                customRowsPerPage={[10, 20]}
                showActiveHistoryButton={toggleHistory}
                showMultiSelectActionButton={[
                    {
                        show: true,
                        label: t("listing:common.uploadJob").toUpperCase(),
                        action: () => setOpenJobUploadPopUpWrap(true),
                        icon: <GridOnIcon />,
                    },
                    {
                        show: true,
                        label: t("listing:common.downloadExcelTempate").toUpperCase(),
                        action: () => downloadExcelTempate(true),
                        icon: <DescriptionOutlinedIcon />,
                    },
                    {
                         show: true,
                         label: "ASSIGN",
                         action: handleOpenAssignPopup,
                         icon: <LocalShippingIcon />,
                     },
                    // {
                    //     show: true,
                    //     label: t("listing:common.action").toUpperCase(),
                    //     action: handleOpenPopover,
                    //     icon: <ListIcon />,
                    // },
                ]}
            />

            {/* For downloading of BL */}
            <Backdrop open={blLoadDlOpen} className={bdClasses.backdrop}>
                {" "}
                <CircularProgress color="inherit" />
            </Backdrop>
            {/* Warning message when there is no selected job */}
            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />
        </React.Fragment>
    );
};

export default withErrorHandler(TruckJobList);
