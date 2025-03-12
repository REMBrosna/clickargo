import { Grid } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { VisibilityOutlined } from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import { JobStates, TruckJobTypes } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { MatxLoading } from "matx";

const DocumentVerifications = ({ }) => {
    const history = useHistory()
    const { t } = useTranslation(["buttons", "listing", "ffclaims", "common", "status", "payments"]);
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([
        { attribute: "history", value: "default" }
    ]);
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "" });

    const columns = [
        {
            name: "jobId",
            label: t("listing:billVerification.jobId"),
        },
        {
            name: "tckJob.tckMstJobType.jbtName",
            label: t("listing:billVerification.jobType"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: Object.keys(TruckJobTypes),
                    renderValue: v => {
                        return TruckJobTypes[v].desc;
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        return TruckJobTypes[v].desc;
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return value;
                }
            },
        },
        {
            name: "tckJob.tcoreAccnByJobOwnerAccn.accnName",
            label: t("listing:billVerification.coFfName"),
        },
        {
            name: "tckJob.tcoreAccnByJobToAccn.accnName",
            label: t("listing:billVerification.toName"),
        },
        {
            name: "acknowledgedDate",
            label: t("listing:billVerification.ackDate"),
            options: {
                sort: true,
                filter: true,
                filterType: 'custom',
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
            },
        },
        {
            name: "tckJob.tckMstJobState.jbstId",
            label: t("listing:billVerification.status"),
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
                setCellHeaderProps: () => { return { style: { justifyContent: 'center' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const status = tableMeta.rowData[9];
                    const id = tableMeta.rowData[0];

                    return <Grid container justifyContent='flex-start' spacing={1} alignItems="center" style={{ minWidth: 50 }}>
                        <Grid item >
                            <C1LabeledIconButton
                                tooltip={t("buttons:view")}
                                label={t("buttons:view")}
                                action={() => history.push({
                                    pathname: `/applications/services/job/truck/view`,
                                    state: { from: '/opadmin/docverification', jobId: id }
                                })}
                            >
                                <VisibilityOutlined />
                            </C1LabeledIconButton>
                        </Grid>
                    </Grid>
                }
            }
        },
        {
            name: "accnId",
            options: {
                filter: false,
                sort: false,
                display: "excluded",
            },
        },
        {
            name: "vhId",
            options: {
                filter: false,
                sort: false,
                display: "excluded",
            },
        }
    ]

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" })
    };

    useEffect(() => {
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }])
        }
    }, [showHistory]);


    useEffect(() => {
        if (!isLoading && !error && res) {
        }

    }, [urlId, isLoading, res, error]);

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

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

    return (
        <>
            {loading && <MatxLoading />}
            {snackBar}
            <DataTable
                url="/api/v1/clickargo/clictruck/invoice/job/acknowledged"
                columns={columns}
                title=""
                defaultOrder="acknowledgedDate"
                defaultOrderDirection="asc"
                isServer={true}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={true}
                isRefresh={isRefresh}
                isShowFilterChip
                filterBy={filterBy}
                guideId={""}
                showActiveHistoryButton={toggleHistory}
                customRowsPerPage={[10, 20]}
            />
            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />
        </>
    )
}

export default withErrorHandler(DocumentVerifications)