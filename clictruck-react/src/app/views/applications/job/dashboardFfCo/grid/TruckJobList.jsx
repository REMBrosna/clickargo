import {Button, Grid} from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from '@material-ui/core/styles';
import { VisibilityOutlined } from "@material-ui/icons";
import ZoomInIcon from '@material-ui/icons/ZoomIn';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import C1Alert from "app/c1component/C1Alert";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import useHttp from "app/c1hooks/http";
import { AccountTypes, JobStates, Roles, ShipmentTypes } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { MatxLoading } from "matx";
import LocationDashboardPopUp from "../../popups/LocationDashboardPopUp";
import DataTable from "app/atomics/organisms/DataTable";
import C1Warning from "app/c1component/C1Warning";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
    customIconButton: {
        '&:hover': {
            color: "#3f51b5",
        },
    },

}));

const TruckJobList = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {

    const { t } = useTranslation(["buttons", "listing", "ffclaims", "common", "status", "job"]);

    const { sendRequest } = useHttp();
    const [loading, setLoading] = useState(false);
    
    const [isRefresh, setRefresh] = useState(false);

    // eslint-disable-next-line
    const [success, setSuccess] = useState(false);
    // eslint-disable-next-line
    const [shipmentType, setShipmentType] = useState();


    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);
    // eslint-disable-next-line
    const [openShipmentDialog, setOpenShipmentDialog] = useState(false);

    // eslint-disable-next-line
    const [tripListData, setTripListData] = useState([])
    const [showLocationPopUp, setShowLocationPopUp] = useState(false)

    const { user } = useAuth();

    const isCargoOwner = user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_CO.code;

    const [selectedRowIds, setSelectedRowIds] = useState([]);
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "" });
    // eslint-disable-next-line
    const [anchorEl, setAnchorEl] = React.useState(null);

    const popupColumns = [
        {
            name: "tckCtTripLocationByTrFrom.tckCtLocation.locAddress",
            label: t("job:popup.from")
        },
        {
            name: "tckCtTripLocationByTrTo.tckCtLocation.locAddress",
            label: t("job:popup.to")
        },
    ]

    const truckCols =
        [
            // 1 DO NOT REMOVE, USED FOR FILTER ETC tableMeta.rowData[0]
            {
                name: "jobId",
                label: t("listing:trucklist.jobid"),
                options: {
                    display: false
                }
            },
            {
                name: "tckJob.tckMstShipmentType.shtId",
                label: t("listing:trucklist.type"),
                options: {
                    filterType: 'dropdown',
                    filterOptions: {
                        names: Object.keys(ShipmentTypes),
                        renderValue: v => {
                            return ShipmentTypes[v].desc;
                        }
                    },
                }
            },
            {
                name: "tcoreAccnByJobPartyCoFf.accnName",
                label: t("listing:trucklist.freightForwader")
            },
            {
                name: "tcoreAccnByJobPartyTo.accnName",
                label: t("listing:trucklist.truckoperator")
            },
            {
                name: "jobDtDelivery",
                label: t("listing:trucklist.dtdelivery"),
                options: {
                    filter: true,
                    filterType: 'custom',
                    display: true,
                    customFilterListOptions: {
                        render: v => v.map(l => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        }
                    },
                    filterOptions: {
                        display: customFilterDateDisplay
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, true);
                    }
                },
            },
            {
                name: "jobShipmentRef",
                label: t("listing:trucklist.shipref")
            },
            {
                name: "pickUp.tckCtLocation.locAddress",
                label: t("listing:trucklist.pickup"),
                options: {
                    sort: false,
                    filter: false
                }
            },
            {
                name: "lastDrop.tckCtLocation.locName",
                label: t("listing:trucklist.lastdrop"),
                options: {
                    sort: false,
                    filter: false,
                    customBodyRender: (value, tableMeta, updateValue) => {
                        const jobId = tableMeta.rowData[1]
                        const jobList = tableMeta?.rowData[13]?.length > 1;

                        return (
                            <React.Fragment>
                                {jobList ?
                                    (<C1LabeledIconButton
                                        tooltip={t("buttons:viewLoc")}
                                        label={t("buttons:view")}
                                        action={() => handleLocationPopUpShow(jobId)}
                                    >
                                        <ZoomInIcon />
                                    </C1LabeledIconButton>)
                                    :
                                    (<span>{value}</span>)
                                }

                            </React.Fragment>
                        )
                    }
                }
            },
            {
                name: "jobDtCreate",
                label: t("listing:trucklist.dtCreate"),
                options: {
                    filter: isCargoOwner,
                    filterType: 'custom',
                    display: isCargoOwner ? true : 'excluded',
                    customFilterListOptions: {
                        render: v => v.map(l => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        }
                    },
                    filterOptions: {
                        display: customFilterDateDisplay
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, true);
                    }
                },
            },
            {
                name: "tckJob.tckMstJobState.jbstId",
                label: t("listing:trucklist.status"),
                options: {
                    filter: true,
                    filterType: 'dropdown',
                    filterOptions: {
                        names: Object.keys(JobStates),
                        renderValue: v => {
                            return JobStates[v].desc;
                        }
                    },
                    customFilterListOptions: {
                        render: v => {
                            return JobStates[v].desc;
                        }
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return getStatusDesc(value);
                    }
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
                    customHeadLabelRender: (columnMeta) => {
                        return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        const jobId = tableMeta.rowData[0];

                        return <Grid container direction="row"
                            justifyContent="flex-start" alignItems="center" style={{ marginRight: "10px" }}>
                            <Grid container item justifyContent="center" spacing={3}>
                                <Grid item xs={4}>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:view")}
                                        label={t("buttons:view")}
                                        action={() => history.push({pathname:`/applications/services/job/truck/view`,state:{jobId}})}>
                                        <VisibilityOutlined />
                                    </C1LabeledIconButton>
                                </Grid>
                            </Grid>
                        </Grid >
                    },
                },
            },
            // ADDED BELOW TO IMPROVE SPACING AFTER THE ICON BUTTONS
            {
                name: '', label: '',
                options: {
                    filter: false,
                    viewColumns: false,
                    // display: (showHistory && !loading) || (!showHistory && loading),
                    display: true,
                    sort: false,
                }
            },
        ];

    // <====== multi select, pop over & warning message ======>
    
    // const handleMultiSelectedJob = (type) => {
    //     setAnchorEl(null);
    //     setLoading(true)
    //     let reqBody = {
    //         action: type,
    //         accType: AccountTypes.ACC_TYPE_CO.code,
    //         role: Roles.OFFICER.code,
    //         id: selectedRowIds,
    //     };
    //     sendRequest("/api/v1/clickargo/clictruck/jobs", "multiSelect", "post", reqBody)
    // };


    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
    };




    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }


    useEffect(() => {
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }])
        }
    }, [showHistory]);



    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: t("common:common.msg.deleted"),
        severity: 'success'
    });



    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    const popUpAddHandler = () => {
        setShipmentType("");
        setOpenShipmentDialog(true);
    };

    const handleLocationPopUpShow = (id) => {
        sendRequest(`/api/v1/clickargo/clictruck/job/truck/${id}`, "getTripList", 'get')
        setShowLocationPopUp(true)
    }


    let snackBar = null;
    if (success) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = <Snackbar
            anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
            open={snackBarState.open}
            onClose={handleCloseSnackBar}
            autoHideDuration={3000}
            key={anchorOriginV + anchorOriginH
            }>
            <C1Alert onClose={handleCloseSnackBar} severity={snackBarState.severity}>
                {snackBarState.msg}
            </C1Alert>
        </Snackbar>;
    }

    // let elAddAction = <Button style={{
    //     backgroundColor: "#13B1ED", color: "#fff",
    //     padding: '10px 20px 10px 20px', fontWeight: "bold"
    // }} onClick={() => popUpAddHandler()}>{t("listing:coJob.button.newJob")}</Button>


    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            {snackBar}

            <LocationDashboardPopUp
                openPopUp={showLocationPopUp}
                setOpenPopUp={setShowLocationPopUp}
                tripListData={tripListData}
                title={"Location"}
                columns={popupColumns}
            />



            <DataTable
                url="/api/v1/clickargo/clictruck/job/truck"
                columns={truckCols}
                title=""
                defaultOrder="jobDtCreate"
                defaultOrderDirection="desc"
                isServer={true}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={true}
                isRefresh={isRefresh}
                isShowToolbar={true}
                isShowFilterChip
                filterBy={filterBy}
                guideId={""}
                showActiveHistoryButton={toggleHistory}
                customRowsPerPage={[10, 20]}
            />

            {/* Warning message when there is no selected job */}
            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />

        </React.Fragment >
    );

};

export default withErrorHandler(TruckJobList);