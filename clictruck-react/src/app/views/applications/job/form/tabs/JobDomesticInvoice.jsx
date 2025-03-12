import { Grid, IconButton, Typography } from "@material-ui/core";
import { EditOutlined, SpeedOutlined, Visibility, VisibilityOutlined } from "@material-ui/icons";
import EditLocationOutlinedIcon from '@material-ui/icons/EditLocationOutlined';
import React, { useContext, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1TabContainer from "app/c1component/C1TabContainer";
import useHttp from "app/c1hooks/http";
import { JobStates, Roles } from "app/c1utils/const";
import { getValue, idrCurrency } from "app/c1utils/utility";
//import C1PopUp from "app/c1component/C1PopUp";
import C1PopUp from "app/clictruckcomponent/JobPopUp";
import useAuth from "app/hooks/useAuth";

import JobTruckContext from "../JobTruckContext";
import JobInvoice from "./JobInvoice";
import cloneDeep from "lodash";

const JobDomesticInvoice = () => {
    const [showInvoicePopUp, setShowInvoicePopUp] = useState(false);

    const invoiceRef = useRef();
    // const [invoiceData, setInvoiceData] = useState({})

    const { t } = useTranslation(["cargoowners", "job"]);

    const { shipmentType, inputData, setInputData, handleDateChange
        , viewType, tcrData, setTcrData, invoiceData, setInvoiceData, isDisabled } = useContext(JobTruckContext);

    const [jobTripList, setJobTripList] = useState([]);
    //const [jobTripList, setJobTripList] = useState(inputData?.tckCtTripList || []);

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [loading, setLoading] = useState(true);

    const { user } = useAuth();

    const isToFinance = user?.authorities.some(item => item.authority === Roles?.FF_FINANCE?.code);
    // data for disbursement component
    //const tripId = inputData?.tckCtTripList?.length > 0 && inputData?.tckCtTripList[0]?.trId ? inputData?.tckCtTripList[0].trId : 0;
    const [trip, setTrip] = useState({ id: null, idx: 0 });
    const [totalTripAmt, setTotalTripAmt] = React.useState(0)

    const jobId = inputData?.jobId

    const deliveredJob = JobStates.DLV.code
    const billedJob = JobStates.BILLED.code

    const disabled = !(isToFinance && (inputData?.tckJob?.tckMstJobState?.jbstId === deliveredJob));

    const columnsDomesticJob = [
        {
            name: "trId",
            label: "Id",
            options: {
                sort: false,
                filter: false,
                display: false,
            }
        },
        {
            name: "tckCtTripLocationByTrFrom.tlocLocAddress",
            label: t("job:tripDetails.domestic.from")
        },
        {
            name: "tckCtTripLocationByTrTo.tlocLocAddress",
            label: t("job:tripDetails.domestic.to")
        },
        {
            name: "tckCtTripCharge.tcPrice",
            label: t("job:tripDetails.domestic.tripCharge"),
            options: {
                customBodyRender: (value) => {
                    return idrCurrency(value);
                }
            }
        },
        {
            name: "totalReimbursementCharge",
            label: t("job:tripDetails.domestic.reimbursementCharge"),
            options: {
                customBodyRender: (value) => {
                    return idrCurrency(value);
                }
            }
        },
        {
            name: "marksNo",
            label: t("job:tripDetails.domestic.action"),
            options: {
                filter: false,
                display: true,
                sort: false,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { textAlign: 'center' } } },
                customBodyRender: (value, tableMeta, updateValue) => {

                    const id = tableMeta.rowData[0];
                    return <C1DataTableActions>
                        <Grid container alignItems="center" justifyContent="center">
                            {!disabled ?
                                <C1LabeledIconButton
                                    tooltip={t("buttons:edit")}
                                    label={t("buttons:edit")}
                                    action={(e) => handleEditViewInvoice(e, id, tableMeta?.rowIndex, false)}>
                                    <EditOutlined />
                                </C1LabeledIconButton>
                                :
                                <C1LabeledIconButton
                                    tooltip={t("buttons:view")}
                                    label={t("buttons:view")}
                                    action={(e) => handleEditViewInvoice(e, id, tableMeta?.rowIndex, true)}>
                                    <VisibilityOutlined />
                                </C1LabeledIconButton>
                            }
                        </Grid>
                    </C1DataTableActions>
                }
            }
        },
    ]

    useEffect(() => {
        
        let tripNum = inputData?.tckCtTripList?.length;

        console.log("inputData?.tckCtTripList",inputData?.tckCtTripList, tripNum);

        if(tripNum === 1) {
            setJobTripList(inputData?.tckCtTripList)
        } else if (tripNum > 1) {
            //let mergeTrip = cloneDeep(inputData?.tckCtTripList[0]);
            //let mergeTrip = structuredClone(inputData?.tckCtTripList[0]);
            let mergeTrip = JSON.parse(JSON.stringify(inputData?.tckCtTripList[0]));
            //tckCtTripLocationByTrTo
            //last trip tckCtTripLocationByTrTo
            mergeTrip.tckCtTripLocationByTrTo = inputData?.tckCtTripList[tripNum-1].tckCtTripLocationByTrTo;
            //totalReimbursementCharge
            mergeTrip.totalReimbursementCharge = inputData?.tckCtTripList?.reduce((total, current)=> total + current.totalReimbursementCharge, 0);
            setJobTripList([mergeTrip]);
        }
        // eslint-disable-next-line
      }, [inputData, inputData?.tckCtTripList]);

    const handleEditViewInvoice = (e, tripId, idx, isDisable) => {

        setInvoiceData({});
        //let trip = jobTripList?.filter(e => e.trId === tripId)[0];
        console.log("handleEditViewInvoice tripId", tripId);
        setTrip({ id: tripId, idx: idx });
        setShowInvoicePopUp(true);
        //setIsDisalbeTrip(isDisable)
    }
    const handleSaveInvoiceData = (tripId) => {
        setShowInvoicePopUp(false);

        let data = { ...invoiceRef?.current?.getInvoiceData(), invJobId: inputData?.jobId };
        setInputData({ ...inputData, toInvoiceList: [invoiceRef?.current?.getInvoiceData()] });
        console.log("invoice data : ", data);
        if (data?.invId) {
            sendRequest(`/api/v1/clickargo/clictruck/truck/invoice/${data?.invId}`, "updateInvoice", "put", data);
        } else {
            sendRequest("/api/v1/clickargo/clictruck/truck/invoice", "saveInvoice", "post", data);
        }
    }

    const computeTotalCharge = () => {
        let tmpTotalTripAmt = 0;
        let tmpTotalReimbursementAmt = 0;

        for (const trip of inputData?.tckCtTripList) {
            tmpTotalTripAmt = tmpTotalTripAmt + parseInt(trip?.tckCtTripCharge?.tcPrice || 0);
        }
        for (const trip of inputData?.tckCtTripList) {
            tmpTotalReimbursementAmt = tmpTotalReimbursementAmt + parseInt(trip?.totalReimbursementCharge || 0);
        }
        //setTotalReimbursementAmt(tmpToalReimbursementAmt);
        inputData.jobTotalReimbursements = tmpTotalReimbursementAmt;
        setTotalTripAmt(tmpTotalTripAmt);
        return;
    }

    useEffect(() => {
        computeTotalCharge();
        //sendRequest(`/api/v1/clickargo/clictruck/invoice/multitrip-invoice?jobId=${inputData?.jobId}`, 'getInvoiceList', 'GET');
    }, [inputData, inputData?.tckCtTripList]);

    useEffect(() => {

        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);
            console.log("urlId", urlId);

            switch (urlId) {

                case "saveInvoice":
                case "updateInvoice": {
                    setInvoiceData(res?.data)
                    //sendRequest(`/api/v1/clickargo/clictruck/invoice/multitrip-invoice?jobId=${inputData?.jobId}`, 'getInvoiceList', 'GET');
                    break;
                }
                case "getInvoiceList":{
                    //setInputData((prevData)=>({...prevData, toInvoiceList: res?.data?.data}))
                    break;
                }
                default:
                    break;
            }
        }

        if (error) {
            //goes back to the screen
            setLoading(false);
        }

        //If validation has value then set to the errors
        if (validation) {

        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    return (
        <React.Fragment>
            <C1PopUp
                title={t("job:invoice.tripChargesDODetails")}
                openPopUp={showInvoicePopUp}
                setOpenPopUp={setShowInvoicePopUp}
                maxWidth={"lg"}
                maxHeight={"500px"}
                setSubmitButton={(!disabled) ? handleSaveInvoiceData : undefined}>
                  {showInvoicePopUp ?
                    <JobInvoice ref={invoiceRef}
                        tripId={trip?.id}
                        idx={trip?.idx}
                        isDomestic
                    />
                    : null
                  }
            </C1PopUp>

            <C1TabContainer>
                <Grid item xs={12}>
                    <C1CategoryBlock
                        icon={<EditLocationOutlinedIcon />}
                        title={t("job:tripDetails.tripCargoReimbursement")}
                    ></C1CategoryBlock>
                </Grid>
                { jobTripList && jobTripList.length > 0 && <C1DataTable
                    dbName={{ list: jobTripList }}
                    // url={"/api/v1/clickargo/attachments/job"}
                    isServer={false}
                    columns={columnsDomesticJob}
                    defaultOrder="attDtCreate"
                    defaultOrderDirection="desc"

                    isRefresh={false}
                    isShowDownload={false}
                    isShowToolbar={(viewType !== "view")}
                    isShowPrint={false}
                    isShowViewColumns={false}
                    isShowFilter={false}
                // guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
                /> }
                <Grid
                    style={{
                        paddingTop: 20,
                    }}
                    direction="row"
                    container>
                    <Grid item lg={7} xs={1} />
                    <Grid item lg={5} xs={12}>
                        <Grid
                            container
                            direction="row"
                            justifyContent="space-between">
                            <Typography variant="h6">{t("job:tripDetails.domestic.totalTripCharge")}</Typography>
                            <Typography variant="h6">{idrCurrency(totalTripAmt)}</Typography>
                        </Grid>
                        <Grid
                            container
                            direction="row"
                            justifyContent="space-between"

                        >
                            <Typography variant="h6">{t("job:tripDetails.domestic.totalReimbursement")}</Typography>
                            <Typography variant="h6">{idrCurrency(inputData?.jobTotalReimbursements)}</Typography>
                        </Grid>
                        <Grid
                            container
                            direction="row"
                            justifyContent="space-between">
                            <Typography variant="h6">{t("job:tripDetails.domestic.totalJobCharge")}</Typography>
                            <Typography
                                style={{
                                    color: "red",
                                }}
                                variant="h6"
                            > {idrCurrency(totalTripAmt + inputData?.jobTotalReimbursements)}
                            </Typography>
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>

        </React.Fragment>
    )

}

export default JobDomesticInvoice