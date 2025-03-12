import { Grid, IconButton, Tooltip } from "@material-ui/core";
import { Snackbar } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import Typography from '@material-ui/core/Typography';
import DeleteIcon from "@material-ui/icons/DeleteOutlined";
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import SendOutlinedIcon from '@material-ui/icons/SendOutlined';
import VisibilityIcon from '@material-ui/icons/VisibilityOutlined';
import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1DateField from "app/c1component/C1DateField";
import C1IconButton from "app/c1component/C1IconButton";
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectField from "app/c1component/C1SelectField";
import { titleTab, useStyles } from "app/c1component/C1Styles";
import C1TabContainer from "app/c1component/C1TabContainer";
import useHttp from "app/c1hooks/http";
import { AccountTypes } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { formatDate, isEditable, isEmpty } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

import DoQueryPopUp from "./DoQueryPopUp";

const DoQuery = ({
    inputData,
    handleInputChange,
    filter,
    qryJobListToDate,
    qryReadOnlyData,
    handleQueryDateChange,
    handleQueryInputChange,
    qryRecords,
    setQryRecords,
    handleDeleteQuery,
    selectedFfJob,
    records,
    queries,
    handleInputAccnIdChange,
    errors, viewType }) => {

    const { t } = useTranslation(["buttons", "common", "listing", "verification"]);

    const { user } = useAuth();
    const isShippingLine = user.coreAccn.TMstAccnType.atypId = AccountTypes.ACC_TYPE_SL.code;

    const classes = useStyles();
    const title = titleTab();
    const [rows, setRows] = useState([]);
    const [isDisabled, setDisabled] = useState(isEditable(viewType));
    const [isRefresh, setRefresh] = useState(false);

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();

    const [loading, setLoading] = useState(false);
    const [view, setView] = useState(false);
    const [openPopUp, setOpenPopUp] = useState(false);

    const [isQueryDisabled, setQueryDisabled] = useState(false);

    const [deleteConfirm, setDeleteConfirm] = useState({ qryId: null });
    const [open, setOpen] = useState(false);

    // const [qryId, setQryId] = useState("");
    const popupDefaultValue = {
        qryId: null,
        tckJob: {
            jobId: qryReadOnlyData?.parentJob,
        },
        tcoreUsrByQryRequester: {
            usrUid: user.id,
            usrName: user.name,
        },
        qryQuery: null,
        qryDtQuery: null,
        tcoreUsrByQryResponder: {
            usrUid: null,
        },
        qryResponse: null,
        qryDtResponse: null,
        qryStatus: null,
        qryDtCreate: null,
        qryDtLupd: null,
        qryUidLupd: null,
    };
    const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);

    const [validationError, setValidationError] = useState({});

    const [isAddSuccess, setAddSuccess] = useState(false);
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: t("common:common.msg.created"),
        severity: 'success'
    });

    const cols = [
        // 0
        {
            name: "qryId", // field name in the row object
            label: t("listing:query.queryId"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        // 1
        {
            name: "tcoreUsrByQryRequester.usrName",
            label: t("listing:query.requestor"),
            options: {
                filter: true,
            },
        },
        // 2
        {
            name: "qryQuery",
            label: t("listing:query.query"),
            options: {
                filter: true,
            },
        },
        // 3
        {
            name: "qryDtQuery",
            label: t("listing:query.dtQuery"),
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        // 4
        {
            name: "tcoreUsrByQryResponder.usrName",
            label: t("listing:query.responder"),
            options: {
                filter: true,
            },
        },
        // 5
        {
            name: "qryResponse",
            label: t("listing:query.response"),
            options: {
                filter: true,
            },
        },
        // 6
        {
            name: "qryDtResponse",
            label: t("listing:query.dtResponse"),
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        // 7
        {
            name: "action",
            label: t("listing:query.action"),
            options: {
                filter: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { paddingLeft: '5%' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    let qryId = tableMeta.rowData[0];
                    let isQryOwner = tableMeta.rowData[1] === user.name;
                    let hasQuery = tableMeta.rowData[2] != null;
                    let responded = tableMeta.rowData[5] !== null;
                    return <C1DataTableActions>
                        <Grid container alignItems="flex-start" justifyContent="flex-end">
                            <span style={{ minWidth: '48px' }}>
                                <Tooltip title={t("verification:tooltip.viewquery")}>
                                    <IconButton onClick={() => viewPopUpHandler(qryId, hasQuery)} >
                                        <VisibilityIcon color="primary" />
                                    </IconButton>
                                </Tooltip>
                            </span>
                            {responded || !isQryOwner ? <span style={{ minWidth: '48px' }}></span> :
                                <span style={{ minWidth: '48px' }}>
                                    <Tooltip title={t("verification:tooltip.deletequery")}>
                                        <IconButton>
                                            <DeleteIcon onClick={() => handleDeleteConfirm(qryId)} color="primary" />
                                        </IconButton>
                                    </Tooltip>
                                </span>
                            }
                        </Grid>
                    </C1DataTableActions>
                }
            },
        }
    ]

    // popup action form
    const popUpHandler = () => {
        setQueryDisabled(false);
        setDisabled(false)
        setRefresh(false);
        setView(false);
        setOpenPopUp(true);
        setValidationError({})
        setPopUpDetails(popupDefaultValue);
    };

    const viewPopUpHandler = (qryId, hasQuery) => {
        setValidationError({})
        setDisabled(true)
        setQueryDisabled(hasQuery)
        sendRequest(`/api/v1/clickargo/query/job/${qryId}`, "getQuery", "GET");
    };

    const handlePopupInputChange = (e) => {
        let elName = e.target.name;
        let elSelectedValue = e.target.value;
        setPopUpDetails({ ...popUpDetails, ...deepUpdateState(popUpDetails, elName, elSelectedValue) });
    };

    const validateFields = () => {
        let errors = {};
        if (!popUpDetails?.qryQuery) {
            errors.qryQuery = t("common:validationMsgs.required")
        }
        return errors;
    }

    let queryUrl = "/api/v1/clickargo/query/job";
    const handlePopUpBtnAddClick = () => {
        let validateErr = validateFields();
        setValidationError(validateErr);
        if (isEmpty(validateErr)) {
            setOpenPopUp(false);
            setLoading(true)
            sendRequest(`${queryUrl}`, "addQuery", "post", popUpDetails);
        }
    }

    const handleDeleteConfirm = (qryId) => {
        setDeleteConfirm({ ...deleteConfirm, qryId });
        setOpen(true);
    }

    const handleDeleteHandler = (e) => {
        if (deleteConfirm && !deleteConfirm.qryId)
            return;
        setLoading(true);
        // setQryId(deleteConfirm.qryId);
        handleDeleteQuery(deleteConfirm.qryId);
        setTimeout(() => setLoading(false), 1);
        setOpen(false);
    }

    const addQuery = (e) => {
        popUpHandler();
    }

    let documentsSubTitle = <Typography variant="body2" className="m-0 mt-1 text-primary font-medium"> </Typography>;

    useEffect(() => {
        if (!isLoading && res) {
            switch (urlId) {
                case "addQuery": {
                    setOpenPopUp(false);
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        successMsg: t("verification:msg.qryCreated")
                    });
                    setAddSuccess(true)
                    setRefresh(false);
                    let url = "/api/v1/clickargo/query/job/list/" + selectedFfJob.tckJob?.jobId;
                    sendRequest(url, "getQryRecords", "get");
                    break;
                }
                case "getQuery": {
                    setRefresh(false);
                    setView(false);
                    setOpenPopUp(true);
                    setPopUpDetails(res?.data);
                    break;
                }
                case "getQryRecords": {
                    setQryRecords({ list: [...res?.data] })
                    setRefresh(true);
                    setLoading(false);
                    break;
                }
                default: break;
            }
        }
        // eslint-disable-next-line
    }, [res, isLoading, urlId])

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    let snackBar = null;

    if (isAddSuccess) {
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
                {snackBarState.successMsg}
            </C1Alert>
        </Snackbar>;
    }

    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            {snackBar}
            {deleteConfirm && deleteConfirm.qryId && (
                <ConfirmationDialog
                    open={open}
                    title={t("verification:msg.confirmTitle")}
                    text={t("verification:msg.confirmAsk")}
                    onYesClick={() => handleDeleteHandler()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )}
            <Grid item lg={12} md={12} xs={12}>
                <C1TabContainer>
                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<DescriptionOutlinedIcon />} title={t("verification:header.gendetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1DateField
                                        label={t("verification:details.date")}
                                        name="confirmedDate"
                                        required
                                        value={filter?.confirmedDate === undefined ? "" : filter.confirmedDate}
                                        onChange={handleQueryDateChange}
                                        disableFuture />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                    {/* <Grid item lg={1} md={1} xs={1} ></Grid> */}
                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<WorkOutlineOutlinedIcon />} title={t("verification:header.jobpartydetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1SelectField
                                        name="queryJobId"
                                        label={t("verification:details.confirmedjobs")}
                                        value={filter?.queryJobId}
                                        onChange={handleQueryInputChange}
                                        isServer={false}
                                        required
                                        disabled={qryJobListToDate?.length <= 0 ? true : false}
                                        optionsMenuItemArr={Object.values(qryJobListToDate).map((job) => {
                                            return <MenuItem value={job.doiFfJobIdKey} key={job.doiFfJobIdKey}>{job.doiFfJobIdDesc}</MenuItem>

                                        })}
                                    />
                                    <C1InputField
                                        name="selectedFfName"
                                        label={t("verification:details.authparty")}
                                        value={filter?.queryJobId ? qryReadOnlyData?.selectedFfName : ""}
                                        disabled />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <C1CategoryBlock icon={<DescriptionOutlinedIcon />} title={t("verification:header.querylist")}>
                        <C1DataTable
                            dbName={qryRecords}
                            isServer={false}
                            columns={cols}
                            title=""
                            isRefresh={isRefresh}
                            isShowFilter={false}
                            isShowViewColumns={false}
                            isShowPrint={false}
                            isShowDownload={false}
                            isShowToolbar={filter?.queryJobId !== '' && isShippingLine ? true : false}
                            showAdd={filter?.queryJobId !== '' && isShippingLine ? {
                                type: "popUp",
                                popUpHandler: (e) => addQuery(e),
                            } : null}
                            guideId="clicdo.doi.sl.claim.jobs.tabs.query.table"
                        />
                    </C1CategoryBlock>
                </C1TabContainer>
            </Grid>

            <C1PopUp
                title={t("verification:header.popuptitle")}
                openPopUp={openPopUp}
                setOpenPopUp={setOpenPopUp}
                actionsEl={isQueryDisabled ? null :
                    <C1IconButton tooltip={t("verification:tooltip.query")} childPosition="right">
                        <SendOutlinedIcon color="primary" fontSize="large" onClick={(e) => handlePopUpBtnAddClick(e)}></SendOutlinedIcon>
                    </C1IconButton>}>
                <DoQueryPopUp
                    inputData={popUpDetails}
                    qryRecords={qryRecords}
                    isDisabled={isDisabled}
                    handlePopupInputChange={handlePopupInputChange}
                    handleBtnAddClick={handlePopUpBtnAddClick}
                    errors={validationError}
                    locale={t}
                />
            </C1PopUp>
        </React.Fragment >
    );
};
export default DoQuery;