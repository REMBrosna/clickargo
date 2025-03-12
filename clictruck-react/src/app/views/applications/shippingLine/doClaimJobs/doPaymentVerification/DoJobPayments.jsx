import { Grid} from "@material-ui/core";
import Typography from '@material-ui/core/Typography';
import React, { useEffect, useState } from "react";

import C1DataTable from 'app/c1component/C1DataTable';
import C1Information from "app/c1component/C1Information";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import useHttp from "app/c1hooks/http";
import { useStyles } from "app/c1utils/styles";
import { formatDate } from "app/c1utils/utility";

import { doPaymentDtlsList1, doPaymentDtlsList2, doPaymentList } from "../../../../../../fake-db/db/clickDOClaimJobs";

const DoJobPayments = ({
    inputData,
    handleInputChange,
    handleDateChange,
    handleViewTask,
    handleInputFileChange,
    handleViewFile,
    viewType,
    isDisabled
}) => {

    const classes = useStyles();

    const [openPopUp, setOpenPopUp] = useState(false);
    const [popUpFieldError, setPopUpFieldError] = useState({});
    const [view, setView] = useState(false);

    const [total, setTotal] = useState(0);

    const doColumns = [
        // 0
        {
            name: "doNo", // field name in the row object
            label: "DO No.", // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        // 1
        {
            name: "blNo",
            label: "BL No.",
            options: {
                filter: true,
            },
        },
        // 2
        {
            name: "authoriser",
            label: "Authoriser",
            options: {
                filter: true,
            },
        },
        // 3
        {
            name: "dtDocVerified",
            label: "Document Verified Date",
            options: {
                filter: true,
            },
        },
        // 4
        {
            name: "dtPayVerified",
            label: "Payment Verified Date",
            options: {
                filter: true,
            },
        },
    ]

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [loading, setLoading] = useState(true);
    const [jobId, setJobId] = useState(null);

    useEffect(() => {
        if (jobId !== null) {
            // sendRequest("/api/jobs/new", "newJob", "post", {});
            // sendRequest(`/api/mockgetdo/` + `${taskId}`, "getTask", "get", null)
        } else {
            setLoading(true);
            // sendRequest("/api/mockgetdoPaymentList1/all", "getAll1", "get", null);
            // sendRequest("/api/mockgetdoPaymentList2/all", "getAll2", "get", null);
        }

    }, [jobId]);

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);
            switch (urlId) {
                case "getAll1": {
                    let data = res.data
                    console.log("DO List for Payment 1 ", data)
                    break;
                }
                case "getAll2": {
                    let data = res.data
                    console.log("DO List for Payment 2 ", data)
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    const paymentCols = [
        // 0
        {
            name: "doNo", // field name in the row object
            label: "DO No.", // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        // 1
        {
            name: "blNo",
            label: "BL No.",
            options: {
                filter: true,
            },
        },
        // 2
        {
            name: "dtVerified",
            label: "Verified Date",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    let date = formatDate(value, false);
                    if (tableMeta.rowData[2] !== undefined) {
                        date = date + " " + formatDate(tableMeta.rowData[2], false, true);
                    }
                    return date;
                }
            },
        },
        // 3
        {
            name: "invoiceAmt",
            label: "Invoice Amount (IDR)",
            options: {
                filter: true,
            }
        },
    ]

    let paymentDetailsSubTitle1 = <Typography variant="body2" className="m-0 mt-1 text-primary font-medium">Authoriser Payment Details - GoodTyre Pte Ltd Logistics</Typography>;
    let paymentDetailsSubTitle2 = <Typography variant="body2" className="m-0 mt-1 text-primary font-medium">Authoriser Payment Details - P.T. Cargo Owner Wong</Typography>;
    let dosSubTitle = <Typography variant="body2" className="m-0 mt-1 text-primary font-medium">DOs</Typography>;

    return (
        <React.Fragment>
            <Grid container className={classes.gridContainer} justifyContent="center">
                <Grid item xs={12}>
                    {/* <C1OutlinedDiv label="DOs"> */}
                    <C1DataTable url="/api/process/all"
                        columns={doColumns}
                        title={dosSubTitle}
                        defaultOrder="appId"
                        dbName={doPaymentList}
                        showAdd={!isDisabled}
                        isServer={false}
                        isShowViewColumns={false}
                        isShowDownload={false}
                        isShowPrint={false}
                        isShowFilter={false}
                    />
                    <C1TabContainer>
                        <Grid item lg={12} md={12} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer} justifyContent="flex-end">
                                <Grid item xs={2} >
                                    <C1InputField
                                        label="Extension Charges (IDR)"
                                        name="extCharges"
                                        required
                                        value={550000000}
                                        disabled={true}
                                        onChange={handleInputChange} />
                                </Grid>
                            </Grid>
                        </Grid>
                    </C1TabContainer>
                    {/* </C1OutlinedDiv> */}
                </Grid>
                <Grid item xs={12}>
                    {/* <C1OutlinedDiv label="Authoriser Payment Details - GoodTyre Pte Ltd Logistics"> */}
                    <C1DataTable url="/api/mockgetdoList"
                        columns={paymentCols}
                        title={paymentDetailsSubTitle1}
                        defaultOrder="appId"
                        dbName={doPaymentDtlsList1}
                        showAdd={false}
                        isServer={false}
                        isShowViewColumns={false}
                        isShowDownload={false}
                        isShowPrint={false}
                        isShowFilter={false}
                    />
                    <C1TabContainer>
                        <Grid item lg={6} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                <Grid item xs={4} >
                                    <C1InputField
                                        label="Status"
                                        name="status"
                                        disabled
                                        onChange={handleInputChange}
                                        value={"COMPLETED"} />
                                </Grid>
                            </Grid>
                        </Grid>
                        <Grid item lg={6} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer} justifyContent="flex-end">
                                <Grid item xs={4} >
                                    <C1InputField
                                        label="Total (IDR)"
                                        name="totalIdr"
                                        required
                                        value={400000000}
                                        disabled={true}
                                        onChange={handleInputChange} />
                                </Grid>
                            </Grid>
                        </Grid>
                    </C1TabContainer>
                    {/* </C1OutlinedDiv> */}
                </Grid>

                <Grid item xs={12}>
                    {/* <C1OutlinedDiv label="Authoriser Payment Details - GoodTyre Pte Ltd Logistics"> */}
                    <C1DataTable url="/api/mockgetdoList"
                        columns={paymentCols}
                        title={paymentDetailsSubTitle2}
                        defaultOrder="appId"
                        dbName={doPaymentDtlsList2}
                        showAdd={false}
                        isServer={false}
                        isShowViewColumns={false}
                        isShowDownload={false}
                        isShowPrint={false}
                        isShowFilter={false}
                    />
                    <C1TabContainer>
                        <Grid item lg={6} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                <Grid item xs={4} >
                                    <C1InputField
                                        label="Status"
                                        name="status"
                                        disabled
                                        onChange={handleInputChange}
                                        value={"WITH PLATFORM"} />
                                </Grid>
                            </Grid>
                        </Grid>
                        <Grid item lg={6} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer} justifyContent="flex-end">
                                <Grid item xs={4} >
                                    <C1InputField
                                        label="Total (IDR)"
                                        name="totalIdr"
                                        required
                                        value={150000000}
                                        disabled={true}
                                        onChange={handleInputChange} />
                                </Grid>
                            </Grid>
                        </Grid>
                    </C1TabContainer>
                    {/* </C1OutlinedDiv> */}
                </Grid>

                <Grid item xs={12}>
                    <C1Information information="doJobPayments" />
                </Grid>
            </Grid>
        </React.Fragment >
    );
};

export default DoJobPayments;