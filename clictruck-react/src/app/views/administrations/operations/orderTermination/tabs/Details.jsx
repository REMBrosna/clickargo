import { Button, Checkbox, Grid, InputAdornment, Tooltip, Typography } from "@material-ui/core";
import { DeleteOutline, DescriptionOutlined, LocalShippingOutlined, MoneyOutlined, NearMeOutlined, PersonOutline, VisibilityOutlined } from "@material-ui/icons";
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import React from "react";
import { useState } from "react";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useLocation } from "react-router-dom";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1TextArea from "app/c1component/C1TextArea";
import { CK_ACCOUNT_CO_FF_ACCN_TYPE, JobStates } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { dialogStyles } from "app/c1utils/styles";
import { formatDate } from "app/c1utils/utility";
import C1PopUp from "app/clictruckcomponent/JobPopUp";
import NumFormat from "app/clictruckcomponent/NumFormat";

const Details = ({
    inputData,
    handleInputChange,
    viewType,
    errors,
    invoiceDetails,
    creditDetails,
    truckJobs,
    getTruckJobs,
    handleAddTruckJob,
    isApproval,
    refreshTable,
    handleDeleteJob,
    termDataList
}) => {

    const history = useHistory();
    const location = useLocation();
    const dialogClasses = dialogStyles();
    const { t } = useTranslation(["administration", "listing", "buttons"]);
    const [openAddPopUp, setOpenAddPopUp] = useState(false);
    const [selectedRowIds, setSelectedRowIds] = useState([]);
    // eslint-disable-next-line
    const [isRefresh, setIsRefresh] = useState(false);
    // eslint-disable-next-line
    const [termData, setTermData] = useState([]);
    const [totalFee, setTotalFee] = useState({});
    let rowData = [];

    const columns = [
        {
            name: "jtId",
            label: "JTID",
            options: {
                display: false,
            }
        },
        {
            name: "tckJobTruck.jobId",
            label: t("listing:orderTermination.jobId")
        },
        {
            name: "tckJobTruck.tckJob.tckMstJobType.jbtName",
            label: t("listing:orderTermination.jobType")
        },
        {
            name: "tckJobTruck.tckJob.tcoreAccnByJobToAccn.accnName",
            label: t("listing:orderTermination.truckingOperator")
        },
        {
            name: "tckJobTruck.jobDtDelivery",
            label: t("listing:orderTermination.deliveryDate"),
            options: {
                // filter: true,
                display: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true)
                },
                // filterType: 'custom',
                // filterOptions: {
                //     display: customFilterDateDisplay
                // },
            }
        },
        {
            name: "tckJobTruck.jobShipmentRef",
            label: t("listing:orderTermination.shipmentRef")
        },
        {
            name: "tckJobTruck.jobCustomerRef",
            label: t("listing:orderTermination.customerRef")
        },
        {
            name: "tckJobTruck.pickUp.tlocLocName",
            label: t("listing:orderTermination.pickUp")
        },
        {
            name: "tckJobTruck.lastDrop.tlocLocName",
            label: t("listing:orderTermination.lastDrop")
        },
        {
            name: "tckJobTruck.jobDtCreate",
            label: t("listing:orderTermination.dateCreated"),
            options: {
                // filter: true,
                display: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true)
                },
                // filterType: 'custom',
                // filterOptions: {
                //     display: customFilterDateDisplay
                // },
            }
        },
        {
            name: "tckJobTruck.jobDtLupd",
            label: t("listing:orderTermination.dateUpdated"),
            options: {
                // filter: true,
                display: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true)
                },
                // filterType: 'custom',
                // filterOptions: {
                //     display: customFilterDateDisplay
                // },
            }
        },
        {
            name: "tckJobTruck.tckJob.tckMstJobState.jbstId",
            label: t("listing:orderTermination.status"),
            options: {
                // filter: true,
                // filterType: 'dropdown',
                // filterOptions: {
                //     names: Object.keys(JobStates),
                //     renderValue: v => {
                //         return JobStates[v].desc;
                //     }
                // },
                // customFilterListOptions: {
                //     render: v => {
                //         return JobStates[v].desc;
                //     }
                // },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            },
        },
        {
            name: "",
            label: t("listing:orderTermination.action"),
            options: {
                filter: false,
                sort: false,
                viewColumns: false,
                display: true,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const id = tableMeta.rowData[0];
                    const jobId = tableMeta.rowData[1];
                    return <Grid container direction="row" justifyContent="center" alignItems="center" style={{ minWidth: 120 }}>
                        <Grid container direction="row" justifyContent="flex-end">
                            <Grid item>
                                <C1LabeledIconButton
                                    tooltip={t("buttons:view")}
                                    label={t("buttons:view")}
                                    action={() => history.push({ pathname: `/applications/services/job/truck/view/id`, state: { jobId, from: location.pathname } })}>
                                    <VisibilityOutlined />
                                </C1LabeledIconButton>

                            </Grid>
                            {
                                !isApproval && inputData?.jtrState === 'NEW' && <Grid item>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:delete")}
                                        label={t("buttons:delete")}
                                        action={() => handleDeleteJob(id)}>
                                        <DeleteOutline />
                                    </C1LabeledIconButton>

                                </Grid>
                            }
                        </Grid>
                    </Grid>
                },
            },
        },
    ]

    const popupColumns = [
        {
            name: "",
            label: "",
            options: {
                sort: false,
                filter: false,
                display: true,
                viewColumns: false,
                customHeadLabelRender: () => {
                    return <Checkbox checked={(selectedRowIds.length > 0 && selectedRowIds.length === rowData.length)}
                        onChange={({ target: { checked } }) =>
                            checked ? setSelectedRowIds(rowData) : setSelectedRowIds([])
                        } />
                },
                customBodyRender: (emptyStr, tableMeta, updateValue) => {
                    rowData = tableMeta.tableData.map((data) => data[1]);
                    const id = tableMeta.rowData[1];
                    return (
                        <React.Fragment>
                            <Checkbox
                                // disabled={selectedRowIds.length === 1 && !selectedRowIds.includes(id) ? true:false}
                                disableRipple={true}
                                checked={selectedRowIds.includes(id)}
                                onChange={({ target: { checked } }) => {
                                    try {
                                        if (checked === true) {
                                            setSelectedRowIds(
                                                selectedRowIds
                                                    .filter((rowId) => rowId !== id)
                                                    .concat(id)
                                            )

                                        } else {
                                            if (selectedRowIds.length === 1) {
                                                setSelectedRowIds(
                                                    selectedRowIds.filter((rowId) => rowId !== id)
                                                );
                                            } else {
                                                setSelectedRowIds(
                                                    selectedRowIds.filter((rowId) => rowId !== id)
                                                );

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
        {
            name: "jobId",
            label: t("listing:orderTermination.jobId")
        },
        {
            name: "tckJob.tckMstJobType.jbtName",
            label: t("listing:orderTermination.jobType"),
            options: {
                filter: false
            },
        },
        {
            name: "tckJob.tcoreAccnByJobToAccn.accnName",
            label: t("listing:orderTermination.truckingOperator"),
            options: {
                filter: false
            },
        },
        {
            name: "jobDtDelivery",
            label: t("listing:orderTermination.deliveryDate"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
            },
        },
        {
            name: "tckJob.tckMstJobState.jbstId",
            label: t("listing:orderTermination.status"),
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
    ];

    useEffect(() => {
        setIsRefresh(true)
    }, [])

    useEffect(() => {
        if (openAddPopUp === true) {
            // getTruckJobs()
        }
    }, [openAddPopUp])

    useEffect(() => {
        if (refreshTable) {
            setOpenAddPopUp(false)
        }
    }, [refreshTable])

    useEffect(() => {
        if (termDataList.length > 0) {
            setTermData(termDataList);
            const totalDN = termDataList.reduce((accumulator, currentValue) => accumulator + currentValue.jtJobDn, 0);
            const totalFe = termDataList.reduce((accumulator, currentValue) => accumulator + currentValue.jtJobPltfeeAmtCoff, 0);
            setTotalFee({ totalDN, totalFe, totalDefault: totalDN + totalFe });
            setSelectedRowIds([])
        } else {
            setTotalFee({ totalDN: 0, totalFe: 0, totalDefault: 0 });
        }
    }, [termDataList])

    useEffect(() => {
        if (selectedRowIds.length > 0) {
            handleInputChange({ target: { name: "tckJobTruck.jobId", value: selectedRowIds } })
        }
    // eslint-disable-next-line
    }, [selectedRowIds])

    let actionEl;
    actionEl = <Tooltip title={t("buttons:add")}>
        <Button onClick={handleAddTruckJob} className={dialogClasses.dialogButtonSpace} disabled={selectedRowIds.length === 0}>
            <NearMeOutlined color={selectedRowIds.length === 0 ? `disabled` : `primary`} fontSize="large" />
        </Button>
    </Tooltip>

    const addButton = viewType === 'edit' && !isApproval ? {
        type: "popUp",
        popUpHandler: () => setOpenAddPopUp(true),
    } : null;

    const handleClosePopup = () => {
        setOpenAddPopUp(false);
        setSelectedRowIds([])
    }

    return (
        <React.Fragment>
            <Grid item xs={12}>
                <C1TabContainer>
                    <Grid item xs={12}>
                        <C1CategoryBlock icon={<PersonOutline />} title={t("listing:orderTermination.details.accountDetails")} />
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("listing:orderTermination.details.companyDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1SelectAutoCompleteField
                                        name="tcoreAccn.accnId"
                                        label={t("listing:orderTermination.companyName")}
                                        value={inputData?.tcoreAccn?.accnId ? inputData?.tcoreAccn?.accnId : ""}
                                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                                        required
                                        isServer={true}
                                        options={{
                                            url: CK_ACCOUNT_CO_FF_ACCN_TYPE,
                                            key: "account",
                                            id: "accnId",
                                            desc: "accnName",
                                            isCache: false,
                                        }}
                                        error={errors['TcoreAccn.tcoreAccn'] !== undefined}
                                        helperText={errors['TcoreAccn.tcoreAccn'] || ''}
                                        disabled={viewType !== 'new'}
                                    />

                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
                        <C1CategoryBlock icon={<MoneyOutlined />} title={t("listing:orderTermination.details.creditDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("listing:orderTermination.creditLimit")}
                                        value={creditDetails?.tckCreditSummary?.crsAmt}
                                        name="crsAmt"
                                        disabled
                                        onChange={() => console.log()}
                                        inputProps={{ style: { textAlign: 'right' } }}
                                        InputProps={{
                                            inputComponent: NumFormat,
                                            startAdornment:
                                                <InputAdornment position="start" style={{ paddingRight: "8px" }}>
                                                    Rp
                                                </InputAdornment>
                                        }}
                                    />
                                    <C1InputField
                                        label={t("listing:orderTermination.creditBalance")}
                                        value={creditDetails?.tckCreditSummary?.crsBalance}
                                        name="crsBalance"
                                        disabled
                                        onChange={() => console.log()}
                                        inputProps={{ style: { textAlign: 'right' } }}
                                        InputProps={{
                                            inputComponent: NumFormat,
                                            startAdornment:
                                                <InputAdornment position="start" style={{ paddingRight: "8px" }}>
                                                    Rp
                                                </InputAdornment>
                                        }}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid container item lg={4} md={6} xs={12} direction="column">
                        <C1CategoryBlock icon={<DescriptionOutlined />} title={t("listing:orderTermination.details.invoiceDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("listing:orderTermination.openInvoice")}
                                        value={invoiceDetails?.totalInv}
                                        name="totalInv"
                                        disabled
                                        onChange={() => console.log()}
                                    />
                                    <C1InputField
                                        label={t("listing:orderTermination.amountOutstanding")}
                                        value={invoiceDetails?.sumAmount}
                                        name="vhUidCreate"
                                        disabled
                                        onChange={() => console.log()}
                                        inputProps={{ style: { textAlign: 'right' } }}
                                        InputProps={{
                                            inputComponent: NumFormat,
                                            startAdornment:
                                                <InputAdornment position="start" style={{ paddingRight: "8px" }}>
                                                    Rp
                                                </InputAdornment>
                                        }}
                                    />
                                    <C1DateField
                                        label={t("listing:orderTermination.paymentDueDate")}
                                        value={invoiceDetails?.earlistDate}
                                        name="invoiceDetails"
                                        disabled
                                        onChange={() => console.log()}
                                    />

                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>


                    <Grid item xs={12}>
                        <C1CategoryBlock icon={<WorkOutlineOutlinedIcon />} title={t("listing:orderTermination.jobForTermination")}>
                            <C1DataTable
                                isServer={false}
                                dbName={{ list: termDataList }}
                                columns={columns}
                                defaultOrder="jtDtCreate"
                                defaultOrderDirection="asc"
                                // isRefresh={isRefresh}
                                isShowDownload={false}
                                isShowToolbar
                                isShowPrint={false}
                                isShowViewColumns={false}
                                isShowFilter={false}
                                showAdd={addButton}
                                guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
                            />

                            <Grid container justifyContent="flex-end" alignItems="center">
                                <Grid item sm={2}><Typography align="left">Total Platform Fee:</Typography></Grid>
                                <Grid item><C1InputField
                                    value={totalFee?.totalFe}
                                    name="jtrJobsPltfeeAmt"
                                    onChange={() => console.log()}
                                    disabled
                                    inputProps={{ style: { textAlign: 'right', fontWeight: 500 } }}
                                    InputProps={{
                                        inputComponent: NumFormat,
                                        startAdornment:
                                            <InputAdornment position="start" style={{ paddingRight: "8px" }}>
                                                Rp
                                            </InputAdornment>
                                    }}
                                />
                                </Grid>
                            </Grid>
                            <Grid container justifyContent="flex-end" alignItems="center" style={{ marginTop: -15 }}>
                                <Grid item sm={2}><Typography align="left">Total Debit Notes:</Typography></Grid>
                                <Grid item><C1InputField
                                    value={totalFee?.totalDN}
                                    name="jtrJobsDnAmt"
                                    disabled
                                    onChange={() => console.log()}
                                    inputProps={{ style: { textAlign: 'right', fontWeight: 500 } }}
                                    InputProps={{
                                        inputComponent: NumFormat,
                                        startAdornment:
                                            <InputAdornment position="start" style={{ paddingRight: "8px" }}>
                                                Rp
                                            </InputAdornment>
                                    }}
                                />
                                </Grid>
                            </Grid>
                            <Grid container justifyContent="flex-end" alignItems="center" style={{ marginTop: -15 }}>
                                <Grid item sm={2}><Typography align="left">Total Default:</Typography></Grid>
                                <Grid item><C1InputField
                                    value={totalFee?.totalDefault}
                                    name="jtrJobsDefault"
                                    onChange={() => console.log()}
                                    disabled
                                    inputProps={{ style: { textAlign: 'right', color: 'red', fontWeight: 500 } }}
                                    InputProps={{
                                        inputComponent: NumFormat,
                                        startAdornment:
                                            <InputAdornment position="start" style={{ paddingRight: "8px" }}>
                                                Rp
                                            </InputAdornment>
                                    }}
                                />
                                </Grid>
                            </Grid>
                            <Grid container justifyContent="space-between">
                                <Grid item md={4} xs={12}>
                                    <C1CategoryBlock icon={<DescriptionOutlined />} title={t("listing:orderTermination.details.requestor")}>
                                        <Grid container alignItems="center" spacing={3}>
                                            <Grid item xs={12} >
                                                <C1InputField
                                                    label={t("listing:orderTermination.username")}
                                                    value={inputData?.jtrUidCreate}
                                                    name="jtrUidCreate"
                                                    disabled
                                                    error={errors['TCkDoi.doiBlNo'] !== undefined}
                                                    helperText={errors['TCkDoi.doiBlNo'] || ''}
                                                    onChange={() => console.log()}
                                                />
                                                <C1TextArea
                                                    label={t("listing:orderTermination.comments")}
                                                    value={inputData?.jtrCommentRequestor}
                                                    name="jtrCommentRequestor"
                                                    onChange={handleInputChange}
                                                    error={errors['jtrCommentRequestor'] !== undefined}
                                                    helperText={errors['jtrCommentRequestor'] || ''}
                                                    multiline
                                                    textLimit={512}
                                                    disabled={viewType === 'view' || isApproval}

                                                />
                                                <C1DateField
                                                    label={t("listing:orderTermination.creationDate")}
                                                    value={inputData?.jtrDtCreate}
                                                    name="jtrDtCreate"
                                                    disabled
                                                    error={errors['TCkDoi.doiBlNo'] !== undefined}
                                                    helperText={errors['TCkDoi.doiBlNo'] || ''}
                                                    onChange={() => console.log()}
                                                />
                                                <C1DateField
                                                    label={t("listing:orderTermination.submitDate")}
                                                    value={inputData?.jtrState !== 'NEW' ? inputData?.jtrDtSubmit : ""}
                                                    name="jtrDtSubmit"
                                                    disabled
                                                    error={errors['TCkDoi.doiBlNo'] !== undefined}
                                                    helperText={errors['TCkDoi.doiBlNo'] || ''}
                                                    onChange={() => console.log()}
                                                />

                                            </Grid>
                                        </Grid>
                                    </C1CategoryBlock>
                                </Grid>
                                <Grid item md={4} xs={12}>
                                    <C1CategoryBlock icon={<DescriptionOutlined />} title={t("listing:orderTermination.details.approver")}>
                                        <Grid container alignItems="center" spacing={3}>
                                            <Grid item xs={12} >
                                                <C1InputField
                                                    label={t("listing:orderTermination.username")}
                                                    value={inputData?.jtrUidApprover}
                                                    name="jtrUidApprover"
                                                    disabled
                                                    onChange={handleInputChange}
                                                    error={errors['jtrUidApprover'] !== undefined}
                                                    helperText={errors['jtrUidApprover'] || ''}
                                                />
                                                <C1TextArea
                                                    label={t("listing:orderTermination.comments")}
                                                    value={inputData?.jtrCommentApprover}
                                                    name="jtrCommentApprover"
                                                    disabled={!isApproval || inputData?.jtrState !== 'SUB'}
                                                    onChange={handleInputChange}
                                                    error={errors['jtrCommentApprover'] !== undefined}
                                                    helperText={errors['jtrCommentApprover'] || ''}
                                                    multiline
                                                    textLimit={512}
                                                />
                                                <C1DateField
                                                    label={t("listing:orderTermination.approveRejectDate")}
                                                    value={inputData?.jtrState === 'REJ' || inputData?.jtrState === 'APP' ? inputData?.jtrDtApproveReject : ""}
                                                    name="jtrDtApproveReject"
                                                    disabled
                                                    onChange={handleInputChange}
                                                    error={errors['jtrDtApproveReject'] !== undefined}
                                                    helperText={errors['jtrDtApproveReject'] || ''}
                                                />

                                            </Grid>
                                        </Grid>
                                    </C1CategoryBlock>
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                </C1TabContainer>

            </Grid>

            <C1PopUp
                title={"Add Jobs"}
                openPopUp={openAddPopUp}
                setOpenPopUp={handleClosePopup}
                overflowY
                maxHeight={`70vh`}
                maxWidth={'lg'}
                actionsEl={actionEl}>
                <C1CategoryBlock icon={<LocalShippingOutlined />} title={t("listing:orderTermination.details.approver")}>
                    <C1DataTable
                        isServer={true}
                        // dbName={{ list: truckJobs ? truckJobs : [] }}
                        url={"/api/v1/clickargo/clictruck/job/truck"}
                        columns={popupColumns}
                        defaultOrder="jobDtCreate"
                        defaultOrderDirection="desc"
                        // isRefresh={isRefresh}
                        isShowDownload={false}
                        isShowToolbar
                        isShowFilterChip
                        isShowPrint={false}
                        isShowViewColumns={false}
                        isShowFilter={true}
                        guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
                        filterBy={[
                            { attribute: "jobOutPaymentState", value: "NEW,PENDING" },
                            { attribute: "TCkJob.TCkMstJobState.jbstId", value: "ACP,APP,ASG,BILLED,CON,CLM,COM,DLV,ONGOING,PENDING,PYG,PMV,PROG,REJ,STRTD,SUB,VER,VER_BILL,APP_BILL,ACK_BILL,REJ_BILL" },
                            { attribute: "TCoreAccnByJobPartyCoFf.accnId", value: inputData?.tcoreAccn?.accnId }
                        ]}
                    />
                </C1CategoryBlock>
            </C1PopUp>

        </React.Fragment>
    );
};

export default Details;