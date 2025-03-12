import { Button, CircularProgress, Grid, Tooltip } from "@material-ui/core";
import { makeStyles } from '@material-ui/core/styles';
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import PrintOutlinedIcon from '@material-ui/icons/PrintOutlined';
import React, { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useReactToPrint } from 'react-to-print';

import DataTable from "app/atomics/organisms/DataTable";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import useHttp from "app/c1hooks/http";
import { AccountTypes, DriverStates, RecordStatus } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { dialogStyles } from "app/c1utils/styles";
import OrderDetail from "app/clictruckcomponent/OrderDetail";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
}));

const DriverAvailability = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {

    const { user } = useAuth();
    const { t } = useTranslation(["buttons", "listing", "ffclaims", "common", "status", "job"]);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const toPrintRef = useRef();

    const [loading, setLoading] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [popUp, setPopUp] = useState(false);
    const [orderData, setOrderData] = useState([]);
    const [orderlogo, setOrderLogo] = useState("");
    const isCargoOwner = user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_CO.code;
    const driverCols =
        [
            // 1 DO NOT REMOVE, USED FOR FILTER ETC tableMeta.rowData[1]
            {
                name: "drvId",
                label: t("listing:common.id"),
                options: {
                    display: "excluded",
                    filter: false
                }
            },
            {
                name: "drvName",
                label: t("listing:common.name")
            },
            {
                name: "tcoreAccn.accnName",
                label: t("listing:trucklist.truckoperator"),
                options: {
                    display: isCargoOwner ? true : "excluded",
                    filter: false,
                    sort: false
                }
            },
            {
                name: "jobsAllocated",
                label: t("listing:driver.jobsAllocated"),
                options: { sort: false, filter: false, }
            },
            {
                name: "jobsRemaining",
                label: t("listing:driver.jobsRemaining"),
                options: { sort: false, filter: false, }
            },
            {
                name: "jobsCompleted",
                label: t("listing:driver.jobsCompleted"),
                options: { sort: false, filter: false, }
            },
            {
                name: "drvState",
                label: t("listing:driver.state"),
                options: {
                    filter: true,
                    display: true,
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return getStatusDesc(value);
                    },
                    filterType: "dropdown",
                    filterOptions: {
                        names: [DriverStates.ASSIGNED.code, DriverStates.MAINTENANCE.code, DriverStates.UNASSIGNED.code],
                        renderValue: (v) => {
                            switch (v) {
                                case DriverStates.ASSIGNED.code: return DriverStates.ASSIGNED.desc;
                                case DriverStates.MAINTENANCE.code: return DriverStates.MAINTENANCE.desc;
                                case DriverStates.UNASSIGNED.code: return DriverStates.UNASSIGNED.desc;
                                default: break
                            }
                        }
                    },
                    customFilterListOptions: {
                        render: (v) => {
                            switch (v) {
                                case DriverStates.ASSIGNED.code: return DriverStates.ASSIGNED.desc;
                                case DriverStates.MAINTENANCE.code: return DriverStates.MAINTENANCE.desc;
                                case DriverStates.UNASSIGNED.code: return DriverStates.UNASSIGNED.desc;
                                default: break
                            }
                        }
                    },
                }
            },
            {
                name: "drvStatus",
                label: t("listing:driver.status"),
                options: {
                    filter: true,
                    display: true,
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return getStatusDesc(value);
                    },
                    filterType: "dropdown",
                    filterOptions: {
                        names: [RecordStatus.ACTIVE.code, RecordStatus.INACTIVE.code, RecordStatus.SUSPENDED.code],
                        renderValue: (v) => {
                            switch (v) {
                                case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                                case RecordStatus.INACTIVE.code: return RecordStatus.INACTIVE.desc;
                                case RecordStatus.SUSPENDED.code: return RecordStatus.SUSPENDED.desc;
                                default: break
                            }
                        }
                    },
                    customFilterListOptions: {
                        render: (v) => {
                            switch (v) {
                                case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                                case RecordStatus.INACTIVE.code: return RecordStatus.INACTIVE.desc;
                                case RecordStatus.SUSPENDED.code: return RecordStatus.SUSPENDED.desc;
                                default: break
                            }
                        }
                    },
                }
            },
            {
                name: "",
                label: "",
                options: {
                    filter: false,
                    sort: false,
                    customBodyRender: (value, tableMeta, updateValue) => {
                        const drvId = tableMeta.rowData[0];
                        return (
                            <Grid container direction="row" justifyContent="center" alignItems="center">
                                <Grid container direction="row" justifyContent="flex-end" spacing={2}>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:view")}
                                        label={"ORDERS"}
                                        action={() => {
                                            setPopUp(true);
                                            setLoading(true);
                                            sendRequest("/api/v1/clickargo/clictruck/attach/byParam/driver/" + drvId, "getOrders");
                                        }}>
                                        <DescriptionOutlinedIcon />
                                    </C1LabeledIconButton>
                                </Grid>
                            </Grid>
                        )
                    }
                }
            },
        ];



    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: t("common:common.msg.deleted"),
        severity: 'success'
    });

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    const handlePrint = useReactToPrint({
        content: () => toPrintRef.current
    });

    const dialogClasses = dialogStyles();
    const printOrder =
        <Tooltip title={t("buttons:submit")} className={dialogClasses.dialogButtonSpace} >
            <Button onClick={handlePrint} >
                <PrintOutlinedIcon color="primary" fontSize="large" />
            </Button>
        </Tooltip>
        ;

    useEffect(() => {

        if (!isLoading && !error && res) {
            setLoading(isLoading);
            switch (urlId) {
                case "getOrders":
                    setOrderData(res.data);
                    sendRequest(`/api/v1/clickargo/manageaccn/logo/${user?.coreAccn?.accnId}`, "getAccnLogo");
                    break;
                case "getAccnLogo":
                    setOrderLogo(res.data);
                    break;
                default:
                    break;
            }
        }
    }, [isLoading, res, error, urlId])

    return (
        <React.Fragment>
            <DataTable
                url="/api/v1/clickargo/clictruck/edb/driverAvail"
                columns={driverCols}
                title=""
                defaultOrder="drvId"
                isServer={true}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={true}
                isRefresh={isRefresh}
                isShowFilterChip
                guideId={""}
            />

            <C1PopUp
                title={"Print Orders Detail"}
                openPopUp={popUp}
                setOpenPopUp={setPopUp}
                maxWidth={"md"}
                actionsEl={printOrder}
            >
                {loading ? <div style={{ display: "flex", alignItems: "center", justifyContent: "center" }}><CircularProgress /></div> : <OrderDetail
                    ref={toPrintRef}
                    resdata={orderData}
                    accnlogo={orderlogo}
                />}
            </C1PopUp>

        </React.Fragment >
    );

};

export default withErrorHandler(DriverAvailability);