import { Checkbox, Grid, IconButton, MenuItem, Tooltip } from "@material-ui/core";
import { Snackbar } from "@material-ui/core";
import AddCircleIcon from '@material-ui/icons/AddCircleOutlineOutlined';
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import GetAppOutlinedIcon from '@material-ui/icons/GetAppOutlined';
import RateReviewOutlinedIcon from '@material-ui/icons/RateReviewOutlined';
import SpeakerNotesOutlinedIcon from '@material-ui/icons/SpeakerNotesOutlined';
import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import C1Alert from "app/c1component/C1Alert";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1DateField from "app/c1component/C1DateField";
import C1IconButton from "app/c1component/C1IconButton";
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import useHttp from "app/c1hooks/http";
import { DocumentTypes, DocumentVerifyStatus } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate, isEmpty } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { MatxLoading } from "matx";

import DoRemarkPopUp from "../popup/DoRemarkPopUp";

const TasksFormDocVerification = ({
    filter,
    handleInputChange,
    handleDateChange,
    readOnlyData,
    records,
    setDocsRecords,
    jobListToDate,
    handleViewFile,
    loading,
    isDocsVerified,
    isDocsPendingReturn,
    setHasPendingReturn,
    selectedRowIds,
    setSelectedRowIds,
    selectedFfJob }) => {

    const { t } = useTranslation(["buttons", "listing", "common"]);

    const [validationError, setValidationError] = useState({});
    const [openPopUp, setOpenPopUp] = useState(false);
    const [rmkView, setRmkView] = useState(false);

    const [attachId, setAttachId] = useState('');

    const popupDefaultValue = {
        attId: attachId,
        attRemarksVerifier: ""
    };

    const [isAddSuccess, setAddSuccess] = useState(false);
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: t("common:common.msg.created"),
        severity: 'success'
    });

    const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();

    useEffect(() => {
        if (!isLoading && res) {
            switch (urlId) {
                case "getAttRecord": {
                    setRmkView(res?.data?.attRemarksVerifier)
                    setOpenPopUp(true);
                    setPopUpDetails(res?.data);
                    break;
                }
                case "addAttRemark": {
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        successMsg: t("verification:msg.rmkCreated")
                    });
                    setAddSuccess(true)
                    let url = `/api/v1/clickargo/clicdo/job/doiFf/${filter?.ffJobId}`
                    sendRequest(url, "getRecords", "get");
                    break;
                }
                case "getRecords": {
                    let jobAttachList = [...res?.data?.jobAttchList];
                    setDocsRecords({ list: jobAttachList });
                    break;
                }
                default: break;
            }
        }
        // eslint-disable-next-line
    }, [res, isLoading, urlId])

    let attUrl = `/api/v1/clickargo/attachments/job/`;
    const viewPopUpHandler = (attId) => {
        setValidationError({})
        setAttachId(attId);
        sendRequest(`${attUrl}${attId}`, "getAttRecord", "GET")
    };

    const handlePopUpBtnAddClick = () => {
        let validateErr = validateFields();
        setValidationError(validateErr);
        if (isEmpty(validateErr)) {
            setOpenPopUp(false);
            sendRequest(`${attUrl}${attachId}`, "addAttRemark", "PUT", popUpDetails);
        }
    }

    const handlePopupInputChange = (e) => {
        let elName = e.target.name;
        let elSelectedValue = e.target.value;
        setPopUpDetails({ ...popUpDetails, ...deepUpdateState(popUpDetails, elName, elSelectedValue) });
    };

    const validateFields = () => {
        let errors = {};
        if (!popUpDetails?.attRemarksVerifier) {
            errors.attRemarksVerifier = t("common:validationMsgs.required")
        }
        return errors;
    }

    const columns = [
        // 0
        // DO NOT REMOVE USED IN FILTER
        {
            name: "attId",
            label: "",
            options: {
                display: 'excluded',
                filter: false
            }
        },
        // 1
        // DO NOT REMOVE USED IN FILTER
        {
            name: "attReturnFlag",
            label: "",
            options: {
                display: 'excluded',
                filter: false
            }
        },
        // 2
        // DO NOT REMOVE USED IN FILTER
        {
            name: "attDtReturned",
            label: "",
            options: {
                display: 'excluded',
                filter: false
            }
        },
        // 3
        // DO NOT REMOVE USED IN FILTER
        {
            name: "attUidCreate",
            label: "",
            options: {
                display: 'excluded',
                filter: false
            }
        },
        // 4
        // DO NOT REMOVE
        {
            name: "attRemarksVerifier",
            label: " ",
            options: {
                display: 'excluded',
                filter: false
            }
        },
        // 5 
        // DO NOT REMOVE
        {
            name: "doiBlType",
            label: "",
            options: {
                display: 'excluded',
                filter: false
            }
        },
        // 6
        // DO NOT REMOVE
        {
            name: "tmstAttType.mattId",
            label: "",
            options: {
                display: 'excluded',
                filter: false
            }
        },
        // 7
        {
            name: "",
            label: t("listing:docVerification.requireReturn"),
            options: {
                sort: false,
                filter: false,
                customBodyRender: (emptyStr, tableMeta, updateValue) => {
                    const attId = tableMeta.rowData[0]
                    const attReturnFlag = tableMeta.rowData[1]
                    const uidCreate = tableMeta.rowData[3]
                    const isExpress = tableMeta.rowData[5]
                    const docType = tableMeta.rowData[6]

                    return <Checkbox disabled={attReturnFlag !== null
                        // workaround for now
                        || uidCreate !== 'SYS' || (isExpress === 'EXPRESS' && docType === 'BL')}
                        checked={selectedRowIds.includes(attId) || attReturnFlag === 'Y'} onChange={({ target: { checked } }) =>
                            checked ? setSelectedRowIds(selectedRowIds.concat(attId)) :
                                setSelectedRowIds(selectedRowIds.filter(rowId => rowId !== attId))
                        } />
                }
            },
        },
        // 8
        {
            name: "tmstAttType.mattName",
            label: t("listing:docVerification.docType"),
            options: {
                filter: true,
                filterType: "dropdown",
                filterOptions: {
                    names: [DocumentTypes.ACR.name, DocumentTypes.BL.name, DocumentTypes.CGA.name,
                    DocumentTypes.CLO.name, DocumentTypes.CUS.name, DocumentTypes.ETI.name,
                    DocumentTypes.LAS.name, DocumentTypes.LOA.name, DocumentTypes.LOI.name,
                    DocumentTypes.LOU.name, DocumentTypes.PFI.name, DocumentTypes.PHO.name],
                    renderValue: v => {
                        switch (v) {
                            case DocumentTypes.ACR.name: return DocumentTypes.ACR.desc;
                            case DocumentTypes.BL.name: return DocumentTypes.BL.desc;
                            case DocumentTypes.CGA.name: return DocumentTypes.CGA.desc;
                            case DocumentTypes.CLO.name: return DocumentTypes.CLO.desc;
                            case DocumentTypes.CUS.name: return DocumentTypes.CUS.desc;
                            case DocumentTypes.ETI.name: return DocumentTypes.ETI.desc;
                            case DocumentTypes.LAS.name: return DocumentTypes.LAS.desc;
                            case DocumentTypes.LOA.name: return DocumentTypes.LOA.desc;
                            case DocumentTypes.LOI.name: return DocumentTypes.LOI.desc;
                            case DocumentTypes.LOU.name: return DocumentTypes.LOU.desc;
                            case DocumentTypes.PFI.name: return DocumentTypes.PFI.desc;
                            case DocumentTypes.PHO.name: return DocumentTypes.PHO.desc;
                            default: break;
                        }
                    },
                },
            }
        },
        // 9
        {
            name: "attBlNo",
            label: t("listing:docVerification.refNo"),
        },
        // 10
        {
            name: "doNo",
            label: t("listing:docVerification.doNo"),
        },
        // 11
        {
            name: "attDtCreate",
            label: t("listing:docVerification.dtCreate"),
            options: {
                filter: true,
                // filterType: 'custom',
                // customFilterListOptions: {
                //     render: v => v.map(l => l),
                //     update: (filterList, filterPos, index) => {
                //         filterList[index].splice(filterPos, 1);
                //         return filterList;
                //     }
                // },
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        // 12
        {
            name: "",
            label: t("listing:docVerification.status"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    const attReturnFlag = tableMeta.rowData[1]
                    const attDtReturned = tableMeta.rowData[2]
                    if (attReturnFlag === 'N' || (attReturnFlag === 'Y' && attDtReturned !== null)) {
                        return getStatusDesc(DocumentVerifyStatus.VER.name);
                    }
                    if (isDocsPendingReturn) {
                        setHasPendingReturn(true)
                        return getStatusDesc(DocumentVerifyStatus.PEN.name);
                    } if (isDocsVerified === undefined || isDocsVerified === null) {
                        return getStatusDesc(DocumentVerifyStatus.NOV.name);
                    }
                }
            }
        },
        // 13
        // {
        //     name: "",
        //     label: t("listing:docVerification.payStatus"),
        //     options: {
        //         filter: false,
        //         customBodyRender: (value, tableMeta, updateValue) => {
        //             let payState = selectedFfJob?.tckJob?.tckMstJobState?.jbstId;
        //             if (payState === JobStates.PYG.code)
        //                 return getStatusDesc(JobStates.PYG.desc)
        //             else if (payState === JobStates.PAID.code)
        //                 return getStatusDesc(JobStates.PAID.desc)
        //             else if (payState === JobStates.PMV.code)
        //                 return getStatusDesc(JobStates.PMV.desc)
        //             else if (payState === JobStates.CON.code)
        //                 return getStatusDesc(JobStates.PEN.desc)
        //         }
        //     }
        // },
        // 14
        {
            name: "action",
            label: t("listing:docVerification.action"),
            options: {
                filter: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { paddingLeft: '5%' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const attId = tableMeta.rowData[0];
                    const attRemark = tableMeta.rowData[4];
                    return <C1DataTableActions>
                        <Grid container alignItems="flex-start" justifyContent="flex-end">
                            <span style={{ minWidth: '48px' }}>
                                {/* <Tooltip title={t("listing:ffJobClaim.action.view")}>
                                    <Link to={`/deliveryOrder/jobs/claim/view/${selectedFfJob?.jobId}`}>
                                        <IconButton>
                                            <VisibilityOutlinedIcon color="primary" />
                                        </IconButton>
                                    </Link>
                                </Tooltip> */}
                            </span>
                            <span style={{ minWidth: '48px' }}>
                                <Tooltip title={attRemark ? t("verification:tooltip.rmkView") : t("verification:tooltip.rmkAdd")}>
                                    <IconButton onClick={(e) => viewPopUpHandler(attId)}>
                                        {attRemark ?
                                            <SpeakerNotesOutlinedIcon color="primary" /> : <RateReviewOutlinedIcon color="primary" />
                                        }
                                    </IconButton>
                                </Tooltip>
                            </span>
                            <span style={{ minWidth: '48px' }}>
                                <Tooltip title={t("verification:tooltip.download")}>
                                    <IconButton onClick={(e) => handleViewFile(e, attId)} >
                                        <GetAppOutlinedIcon color="primary" />
                                    </IconButton>
                                </Tooltip>
                            </span>
                        </Grid>
                    </C1DataTableActions>
                }
            }
        },
    ];

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

    let elAction = <C1IconButton tooltip={t("verification:tooltip.rmkAdd")} childPosition="right">
        <AddCircleIcon color="primary" fontSize="large" onClick={(e) => handlePopUpBtnAddClick(e)}></AddCircleIcon>
    </C1IconButton>

    return (<React.Fragment>
        {loading && <MatxLoading />}
        {snackBar}
        <Grid item xs={12}>
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
                                    onChange={handleDateChange}
                                    disableFuture />
                                <C1InputField label="Verified Jobs"
                                    value={readOnlyData?.verifiedJobs}
                                    name="verifiedJobs"
                                    disabled
                                    onChange={handleInputChange} />
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
                                    name="ffJobId"
                                    label={t("verification:details.confirmedjobs")}
                                    value={filter?.ffJobId}
                                    onChange={handleInputChange}
                                    isServer={false}
                                    required
                                    disabled={jobListToDate?.length <= 0 ? true : false}
                                    optionsMenuItemArr={Object.values(jobListToDate).map((job) => {
                                        return <MenuItem value={job.doiFfJobIdKey} key={job.doiFfJobIdKey}>{job.doiFfJobIdDesc}</MenuItem>

                                    })}
                                />
                                <C1InputField
                                    name="selectedFfName"
                                    label={t("verification:details.authparty")}
                                    value={filter?.ffJobId ? readOnlyData?.selectedFfName : ''}
                                    onChange={handleInputChange}
                                    disabled />
                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>

                <Grid item lg={12} md={12} xs={12}>
                    <C1CategoryBlock icon={<DescriptionOutlinedIcon />} title={t("verification:header.docslist")}>
                        <C1DataTable
                            dbName={records}
                            isServer={false}
                            columns={columns}
                            title=""
                            isRefresh={records}
                            isShowFilter={true}
                            isShowViewColumns={true}
                            isShowPrint={false}
                            isShowDownload={false}
                            isShowToolbar={true}
                            defaultOrder="doNo"
                        />
                    </C1CategoryBlock>
                </Grid>
            </C1TabContainer>
        </Grid>

        <C1PopUp
            title={t("common:remarks.title")}
            openPopUp={openPopUp}
            setOpenPopUp={setOpenPopUp}
            actionsEl={rmkView ? null : elAction}>
            <DoRemarkPopUp
                inputData={popUpDetails}
                handlePopupInputChange={handlePopupInputChange}
                isDisabled={rmkView}
                handleBtnAddClick={handlePopUpBtnAddClick}
                errors={validationError}
                locale={t}
            />
        </C1PopUp>

    </React.Fragment >);
};

export default withErrorHandler(TasksFormDocVerification);