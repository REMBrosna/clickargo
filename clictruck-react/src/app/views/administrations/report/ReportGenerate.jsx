import { Box, Button, Checkbox, FormControl, FormControlLabel, Grid, MenuItem, Paper, Radio, RadioGroup } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1DateField from "app/c1component/C1DateField";
import C1Dialog from "app/c1component/C1Dialog";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import { titleTab, useStyles } from "app/c1component/C1Styles";
import useHttp from "app/c1hooks/http";
import { MST_PORT_BY_COUNTRY, MST_PORT_TERMINAL_BY_PORT, PEDI_ACCN_APPTYPE_ASSOC } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { downloadFile, getValue, isArrayNotEmpty } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { MatxLoading } from "matx";
import CloseOutlinedIcon from '@material-ui/icons/CloseOutlined';
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import SearchOutlinedIcon from '@material-ui/icons/SearchOutlined';

const useStylesPreview = makeStyles((theme) => ({
    root: {
        height: '100%',
        width: '100%'
    },
    headerTitle: {
        width: '300px',
        marginBottom: '20px'
    },
    pdfContent: {
        height: '100%',
        width: '95%',
        marginTop: '10px',
        margin: '0px auto'
    },
    iframe: {
        width: '100%',
        height: '100%',
        minHeight: '768px'
    }

}));

const ReportGenerate = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { id } = useParams();
    const history = useHistory();
    const { t } = useTranslation(['administration', "report"]);

    //useState with initial value of 0 for tabIndex
    const [loading, setLoading] = useState(false);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [inputData, setInputData] = useState({});
    const [filters, setFilters] = useState({});


    //states for generating the report
    const [isFormatChooseOpen, setFormatChooseOpen] = useState(false);
    const [isGenerating, setGenerating] = useState(false);
    const [reportFormat, setReportFormat] = useState();
    const [previewData, setPreviewData] = useState();

    const fieldClass = useStyles();
    const classes1 = titleTab();
    const classes = useStylesPreview();

    //some filter states for dropdown
    const [nxtPortArr, setNxtPortArr] = useState([]);
    const [lstPortArr, setLstPortArr] = useState([]);
    const [terminalArr, setTerminalArr] = useState([]);
    const [appTypeArr, setAppTypeArr] = useState([]);

    //state for label change based on Job State selection
    const [jobStateDtLabel, setJobStateDtLabel] = useState("SUBMITTED");
    const [dtJobStateFieldName, setJobstateFieldName] = useState("RCD_DT_SUBMIT");


    //api request for the details here
    useEffect(() => {
        setLoading(false);
        if (id) {
            setLoading(true);
            if(id.includes('RPT_JOBS_DISCREPANCY')){
                setJobStateDtLabel("COMPLETED");
                setJobstateFieldName("RCD_DT_COMPLETE");
            }
                
            sendRequest(`/api/app/report/filter/${id}`, 'filter', "get", null);
        }
        return () => { };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [id]);

    useEffect(() => {
        if ((!isLoading && !error && res)) {
            setLoading(isLoading);
            switch (urlId) {
                case "filter": {
                    setFilters({ ...filters, ...res.data });
                    //create inputData object from the filters
                    setInputData(input => {
                        res.data.rptFilterColumns.forEach(el => {
                           
                            if (el.filColType === 'R') {  
                                    //add in _FROM and _TO for date range fields, initialize as current date
                                    input[el.filColCode + "_FROM"] = new Date();
                                    input[el.filColCode + "_TO"] = new Date();
                               
                            } else {
                                input[el.filColCode] = '';
                            }

                            //for KH ports that needs to be filter and only have one option select trigger the handleSelectevent here
                            if (el.filColCode.includes("_PORT") && !el.filColCode.includes("_PORT_")
                                && el.filColType === 'L' && el.value !== '') {
                                sendRequest(MST_PORT_TERMINAL_BY_PORT + el.value, "getTerminalPort", "get");
                            }

                            if (el.filColCode.includes("GOV_AGENCY") && el.filColType === 'L' && el.value !== '') {
                                sendRequest(PEDI_ACCN_APPTYPE_ASSOC + el.value, "getAppTypeAssoc", "get");
                            }

                            if (el.filColCode.includes("PSG_VOY_TYP") && el.filColType === 'L') {
                                el.value = 'INWARD'
                            }

                            if ((id.includes("RPT_VSLCALL_PRT") || id.includes("RPT_CNTVSLORGN")
                                || id.includes("RPT_INTL_PSNGR") || id.includes("RPT_STATVSLCOM")
                                || id.includes("RPT_IM_CNTVSLORGN") || id.includes("RPT_IM_STATVSLCOM")
                                || id.includes("RPT_APP_PERF"))
                                && (el.filColCode.includes("_PORT") || el.filColCode.includes("GOV_AGENCY"))) {
                                input[el.filColCode] = el.value;
                            }
                        });

                        return input;
                    });
                    break;
                }
                case "getNxtCtyPort": {
                    setNxtPortArr([...res.data]);
                    break;
                }
                case "getLstCtyPort": {
                    setLstPortArr([...res.data]);
                    break;
                }
                case "getTerminalPort": {
                    setTerminalArr([...res.data.aaData]);
                    break;
                }
                case "getAppTypeAssoc": {
                    setAppTypeArr([...res.data]);
                    break;
                }
                case "previewPdf": {
                    setPreviewData(res.data.reportBody);
                    break;
                }
                case "download": {
                    downloadFile(res.data.reportName, res.data.reportBody);
                    handleCloseDialog();
                    break
                }
                default: break;
            }
        }
        if (error) {
            // goes back to the screen
            setLoading(false);
            handleCloseDialog();
        }

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);


    const handleSubmit = async (values) => {
        setLoading(true);

    };

    let formButtons;
    if (!loading) {
        formButtons = <C1FormButtons options={{
            back: {
                show: true,
                eventHandler: () => history.push("/reports/list")
            },
        }}>
        </C1FormButtons>
    }

    const handleCancel = (e) => {
        history.push("/reports/list")
    }

    //Call API to generate the report based on format
    const handlePreviewReport = () => {
        const data = { ...inputData };
        // PORTEDI-1469
        // Check Specific Vessel Call Report (RPT_VSLCALL_PRT) Estimate time based on Voyage Type. if type INWARD it should be ETA, else ETD
        if (id.includes("RPT_VSLCALL_PRT")) {
            // If voyage type all, we added ETD
            if (data.VC_VOY_TYP === "" || data.VC_VOY_TYP === "ALL") {
                data.VC_ETD_FROM = data.VC_ETA_FROM;
                data.VC_ETD_TO = data.VC_ETA_TO;
            } else if (data.VC_VOY_TYP === "OUTWARD") {
                data.VC_ETD_FROM = data.VC_ETA_FROM;
                data.VC_ETD_TO = data.VC_ETA_TO;
                data.VC_ETA_FROM = "";
                data.VC_ETA_TO = "";
            }
        }
        if (data?.PSG_VOY_TYP === "") {
            data.PSG_VOY_TYP = 'INWARD'
        }
        sendRequest(`/api/app/report/report/download/${id}?format=Pdf`, "previewPdf", "post", data);
    }

    const handleGenerateReport = () => {
        setGenerating(true);
        const data = { ...inputData };
        // PORTEDI-1469
        // Check Specific Vessel Call Report (RPT_VSLCALL_PRT) Estimate time based on Voyage Type. if type INWARD it should be ETA, else ETD
        if (id.includes("RPT_VSLCALL_PRT")) {
            // If voyage type all, we added ETD
            if (data.VC_VOY_TYP === "" || data.VC_VOY_TYP === "ALL") {
                data.VC_ETD_FROM = data.VC_ETA_FROM;
                data.VC_ETD_TO = data.VC_ETA_TO;
            } else if (data.VC_VOY_TYP === "OUTWARD") {
                data.VC_ETD_FROM = data.VC_ETA_FROM;
                data.VC_ETD_TO = data.VC_ETA_TO;
                data.VC_ETA_FROM = "";
                data.VC_ETA_TO = "";
            }
        }
        if (data?.PSG_VOY_TYP === "") {
            data.PSG_VOY_TYP = 'INWARD'
        }
        sendRequest(`/api/app/report/report/download/${id}?format=${reportFormat}`, "download", "post", data);
    }

    const handleCloseDialog = () => {
        setGenerating(false);
        setReportFormat("");
        setFormatChooseOpen(false);
    }

    const displayFilterFields = (filterColumn) => {
        if (filterColumn.filColType === 'C') {
            return <FormControlLabel
                className="mb-4"
                key={filterColumn.filColCode}
                name={filterColumn.filColCode}
                onChange={(e) =>
                    handleCheckChange({
                        target: { name: filterColumn.filColCode, value: e.target.checked },
                    })
                }
                control={
                    <Checkbox
                        required={filterColumn.filColMandatory === 'Y'}
                        size="big"
                        checked={getValue(inputData[filterColumn?.filColCode]) || false}
                    />
                }
                label={filterColumn.filColCaption}
            />
        }
        if (filterColumn.filColType === 'D') {
            return <C1DateField
                key={filterColumn.filColCode}
                label={filterColumn.filColCaption}
                name={filterColumn.filColCode}
                required={filterColumn.filColMandatory === 'Y'}
                value={getValue(inputData[filterColumn?.filColCode])}
                onChange={handleDateChange}
            />
        }
        if (filterColumn.filColType === 'L') {

            let objArr = null;
            //for list that require customization in displaying the options
            //Add in the filter name if needed.
            if (['VC_PORT', 'DD_PORT', 'ADDD_PORT', 'AD_PORT', 'VC_NXT_CTY', 'VC_LST_CTY', 'VC_VOY_TYP', 'ADDD_VOY_TYP',
                'VC_SHIP_TYP', 'ADDD_TERMINAL_CRG', 'GOV_AGENCY', 'CERT_TYPE', 'STATUS', 'PAYMENT_STATE'].includes(filterColumn.filColCode)
                || filterColumn.filColCode.includes('SHIP_TYP')
                // Added filter for Shipment Type
                || filterColumn.filColCode.includes('SHIPMENT_TYPE')) {
                objArr = Object.keys(filterColumn.options).map((k, idx) => {
                    let val = filterColumn.options[k];
                    //this is for contry and port to display the code
                    if (filterColumn.filColCode.includes('PORT') || filterColumn.filColCode.includes('CTY')) {
                        val = `${k} - ${filterColumn.options[k]}`
                    }

                    return <MenuItem value={k} key={idx}>{val}</MenuItem>
                });
            } else if ('PSG_VOY_TYP'.includes(filterColumn.filColCode)) {
                objArr = Object.keys(filterColumn.options).map((k, idx) => {
                    if (filterColumn.options[k] === 'ALL') {
                        return
                    } else {
                        return <MenuItem value={k} key={idx}>{filterColumn.options[k]}</MenuItem>
                    }
                });
            } else {
                //for list that requires filtering based from the other field's value
                //add checks if needed
                if ('VC_NXT_PORT_CALL' === filterColumn.filColCode) {
                    objArr = nxtPortArr && isArrayNotEmpty(nxtPortArr) ? nxtPortArr.map((k, idx) => {
                        let val = `${k.portCode} - ${k.portDescription}`;
                        if (idx === 0) {
                            return <MenuItem value='ALL' key={idx}>ALL - ALL</MenuItem>
                        } else {
                            return <MenuItem value={k.portCode} key={idx}>{val}</MenuItem>
                        }
                    }) : [<MenuItem value='ALL' key="0">ALL - ALL</MenuItem>];
                } else if ('VC_LST_PORT_CALL' === filterColumn.filColCode) {
                    objArr = lstPortArr && isArrayNotEmpty(lstPortArr) ? lstPortArr.map((k, idx) => {
                        let val = `${k.portCode} - ${k.portDescription}`;
                        if (idx === 0) {
                            return <MenuItem value='ALL' key={idx}>ALL - ALL</MenuItem>
                        } else {
                            return <MenuItem value={k.portCode} key={idx}>{val}</MenuItem>
                        }
                    }) : [<MenuItem value='ALL' key="0">ALL - ALL</MenuItem>];
                } else if (filterColumn.filColCode.includes('TERM')) {
                    if (terminalArr && isArrayNotEmpty(terminalArr)) {
                        objArr = [];
                        objArr.push(<MenuItem value='ALL' key="#">ALL - ALL</MenuItem>);
                        objArr.push(terminalArr.map((k, idx) => {
                            let val = `${k.portTeminalId} - ${k.portTeminalName}`;
                            return <MenuItem value={k.portTeminalId} key={idx}>{val}</MenuItem>
                        }));
                    } else {
                        objArr = [<MenuItem value='ALL' key="0">ALL - ALL</MenuItem>];
                    }

                } else if (filterColumn.filColCode === 'APP_TYPE') {
                    if (appTypeArr && isArrayNotEmpty(appTypeArr)) {
                        objArr = [];
                        objArr.push(<MenuItem value='ALL' key="#">ALL</MenuItem>);
                        objArr.push(appTypeArr.map((k, idx) => {
                            return <MenuItem value={k.appTypeId} key={idx}>{k.appTypeName}</MenuItem>

                        }));
                    } else {
                        objArr = [<MenuItem value='ALL' key="0">ALL - ALL</MenuItem>];
                    }
                }  else {
                    //others that don't require any customization or filtering should fall here
                    objArr = Object.keys(filterColumn.options).map((k, idx) => {
                        let val = filterColumn.options[k]; 
                        return <MenuItem value={k} key={idx}>{val}</MenuItem>
                    });
                }
            }

            return <C1SelectField
                key={filterColumn.filColCode}
                label={filterColumn.filColCaption}
                name={filterColumn.filColCode}
                value={getValue(inputData[filterColumn?.filColCode]) === ''
                    ? (filterColumn.value === null ? '' : filterColumn.value)
                    : getValue(inputData[filterColumn?.filColCode])}
                disabled={false}
                isServer={false}
                required={filterColumn.filColMandatory === 'required'}
                onChange={handleSelectFieldChange}
                optionsMenuItemArr={objArr} />
        }

        const genericDateRange = (
            <Grid key={filterColumn.filColCode} container item alignItems="center">
                <Grid item lg={6} md={4} sm={12}>
                    <C1DateField
                        required={filterColumn.filColMandatory === 'required'}
                        key={filterColumn.filColCode + "_FROM"}
                        label={filterColumn.filColCaption + " FROM"}
                        name={filterColumn.filColCode + "_FROM"}
                        value={getValue(inputData[filterColumn?.filColCode + "_FROM"])}
                        onChange={handleDateChange} />
                </Grid>
                <Grid item lg={6} md={4} sm={12} style={{ paddingLeft: "5px" }}>
                    <C1DateField
                        required={filterColumn.filColMandatory === 'required'}
                        key={filterColumn.filColCode + "_TO"}
                        label={filterColumn.filColCaption + " TO"}
                        minDate={getValue(inputData[filterColumn?.filColCode + "_FROM"])}
                        name={filterColumn.filColCode + "_TO"}
                        value={getValue(inputData[filterColumn?.filColCode + "_TO"])}
                        onChange={handleDateChange} />
                </Grid>
            </Grid>
        );

        if (filterColumn.filColType === 'R') {
            if (filterColumn.filColCode === 'VC_ETA') {
                return <>
                    <Box className={fieldClass.gridContainer} >
                        <Box className={classes1.root}>
                            {t("report:vc.section.timePeriod")}
                        </Box>
                    </Box>
                    <Grid key={filterColumn.filColCode} container item alignItems="center">
                        <Grid item lg={6} md={4} sm={12}>
                            <C1DateField
                                required={filterColumn.filColMandatory === 'required'}
                                key={filterColumn.filColCode + "_FROM"}
                                label="From"
                                name={filterColumn.filColCode + "_FROM"}
                                value={getValue(inputData[filterColumn?.filColCode + "_FROM"])}
                                onChange={handleDateChange} />
                        </Grid>
                        <Grid item lg={6} md={4} sm={12} style={{ paddingLeft: "5px" }}>
                            <C1DateField
                                required={filterColumn.filColMandatory === 'required'}
                                key={filterColumn.filColCode + "_TO"}
                                label="To"
                                minDate={getValue(inputData[filterColumn?.filColCode + "_FROM"])}
                                name={filterColumn.filColCode + "_TO"}
                                value={getValue(inputData[filterColumn?.filColCode + "_TO"])}
                                onChange={handleDateChange} />
                        </Grid>
                    </Grid>
                </>
            } else if(['RCD_DT_CANCEL','RCD_DT_COMPLETE','RCD_DT_COMPLETE','RCD_DT_START','RCD_DT_SUBMIT'].includes(filterColumn.filColCode)){
                  return  <Grid key={filterColumn.filColCode} container item alignItems="center">
                        <Grid item lg={6} md={4} sm={12}>
                            <C1DateField
                                required={filterColumn.filColMandatory === 'required'}
                                key={filterColumn.filColCode + "_FROM"}
                                label={"Date " + jobStateDtLabel + " FROM"}
                                name={dtJobStateFieldName + "_FROM"}
                                value={getValue(inputData[dtJobStateFieldName + "_FROM"])}
                                onChange={handleDateChange} />
                        </Grid>
                        <Grid item lg={6} md={4} sm={12} style={{ paddingLeft: "5px" }}>
                            <C1DateField
                                required={filterColumn.filColMandatory === 'required'}
                                key={"FIL_RCD_DT_TO"}
                                label={"Date " + jobStateDtLabel + " TO"}
                                minDate={getValue(inputData[dtJobStateFieldName + "_FROM"])}
                                name={dtJobStateFieldName + "_TO"}
                                value={getValue(inputData[dtJobStateFieldName + "_TO"])}
                                onChange={handleDateChange} />
                        </Grid>
                    </Grid>
            }else {
                return genericDateRange;
            }
        }

        if (filterColumn.filColType === 'T') {
            return <C1InputField
                required={filterColumn.filColMandatory === 'required'}
                disabled={false}
                key={filterColumn.filColCode}
                label={filterColumn.filColCaption}
                name={filterColumn.filColCode}
                value={getValue(inputData[filterColumn?.filColCode]) === ''
                    ? (filterColumn.value === null ? '' : filterColumn.value)
                    : getValue(inputData[filterColumn?.filColCode])}
                onChange={handleInputChange} />
        }
        return null;
    }

    const handleCheckChange = ({ target: { name, value } }) => {
        setInputData({
            ...inputData,
            [name]: value,
        });
    }

    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
    }

    const handleSelectFieldChange = (e) => {
        setInputData({ ...inputData, ...deepUpdateState(inputData, e.target.name, e.target.value) });
        //call api for fields that require filtering based from the current target
        //Add in new field checks if needed.
        if (e.target.name === 'VC_NXT_CTY' && e.target.value !== 'ALL') {
            sendRequest(MST_PORT_BY_COUNTRY + e.target.value, "getNxtCtyPort", "get");
        } else if (e.target.name === 'VC_LST_CTY' && e.target.value !== 'ALL') {
            sendRequest(MST_PORT_BY_COUNTRY + e.target.value, "getLstCtyPort", "get");
        } else if ((e.target.name === 'VC_PORT' || e.target.name === 'DD_PORT'
            || e.target.name === 'ADDD_PORT' || e.target.name === 'AD_PORT') && (e.target.value !== 'ALL')) {
            sendRequest(MST_PORT_TERMINAL_BY_PORT + e.target.value, "getTerminalPort", "get");
        } else if (e.target.name === 'GOV_AGENCY' && e.target.value !== 'ALL') {
            sendRequest(PEDI_ACCN_APPTYPE_ASSOC + e.target.value, "getAppTypeAssoc", "get");
        } else if(e.target.name === 'JOB_STATE'){
            if(e.target.value === 'CAN'){
                setJobstateFieldName("RCD_DT_CANCEL");
                setJobStateDtLabel("CANCELLED");
            } else if(e.target.value==='DLV'){
                setJobStateDtLabel("DELIVERED");
                setJobstateFieldName("RCD_DT_COMPLETE");
            } else if(e.target.value==='ACP'){
                setJobStateDtLabel("ACCEPTED");
                setJobstateFieldName("RCD_DT_ACCEPTED");
            } else if(e.target.value==='ONGOING'){
                setJobStateDtLabel("STARTED");
                setJobstateFieldName("RCD_DT_START");
            } else if(e.target.value==='ALL'){
                setJobStateDtLabel("ALL");
                setJobstateFieldName("RCD_DT_SUBMIT");
            } else if(e.target.value==='SUB'){
                setJobStateDtLabel("SUBMITTED");
                setJobstateFieldName("RCD_DT_SUBMIT");
            }
        }
    };

    const handleInputChange = (e) => {
        setInputData({ ...inputData, [e.target.name]: e.target.value });
    };

    const handleReportFormatChange = (e) => {
        setReportFormat(e.target.value);

    }

    return (
        loading ? <MatxLoading /> : <React.Fragment>
            <C1Dialog
                title={t("report:rpt.choose")}
                isOpen={isFormatChooseOpen}
                actionsEl={
                    <Button variant="contained"
                        color={isGenerating ? "default" : "primary"}
                        disabled={isGenerating ? true : (reportFormat ? false : true)}
                        size="small"
                        fullWidth
                        className="hover-bg-primary"
                        style={{ backgroundColor: isGenerating ? "#37b7ff" : (reportFormat ? "#37b7ff" : "#e0e0e0") }}
                        onClick={(e) => handleGenerateReport(e)}>
                        {t("report:rpt.generate")}
                    </Button>}
                handleCloseEvent={handleCloseDialog} >
                <FormControl component="fieldset">
                    <RadioGroup row aria-label="reportFormat" name="reportFormat" onChange={handleReportFormatChange}>
                        <FormControlLabel value="Excel" control={<Radio />} label="Excel" />
                        <FormControlLabel value="Pdf" control={<Radio />} label="PDF" />
                    </RadioGroup>
                </FormControl>
            </C1Dialog>

            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("administration:report.title.reports"), path: "/reports/list" },
                    { name: t("administration:report.title.report") },
                ]}
                title={t("administration:report.title.report") + ": " + filters.rptTitle}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                isLoading={loading}>
                {(props) => (
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper>
                                <Grid item xs={4}>
                                    <Box display="flex" p={1}>
                                        <Box p={1}>
                                            <Button disabled={false} variant="contained" color="primary" size="small"
                                                className="hover-bg-primary"
                                                style={{ backgroundColor: "#37b7ff" }}
                                                onClick={(e) => handlePreviewReport()}>
                                                <SearchOutlinedIcon />
                                                {t("administration:report.button.previewReport").toUpperCase()}{" "}
                                            </Button>
                                        </Box>
                                        <Box p={1}>
                                            <Button disabled={false} variant="contained" color="primary" size="small"
                                                className="hover-bg-primary"
                                                style={{ backgroundColor: "#37b7ff" }}
                                                onClick={(e) => setFormatChooseOpen(true)}>
                                                <DescriptionOutlinedIcon />
                                                {t("administration:report.button.generateReport").toUpperCase()}{" "}
                                            </Button>
                                        </Box>
                                    </Box>
                                </Grid>
                            </Paper>
                        </Grid>
                        <Grid item xs={12}>
                            <Paper>
                                <Grid item xs={3}>
                                    <Box display="flex" p={1}>
                                        <Box p={1}>
                                            <font size="5">{t("administration:report.title.reportFilters")}</font>
                                        </Box>
                                    </Box>
                                </Grid>
                            </Paper>
                        </Grid>
                        <Grid item xs={12}>
                            <Paper>
                                <Grid item lg={12} md={12} xs={12}>
                                    <Grid container alignItems="flex-start"  spacing={3} className={fieldClass.gridContainer}>
                                        <Grid item lg={4} md={8} sm={12}>
                                            {
                                                filters && filters.rptFilterColumns && filters.rptFilterColumns.map((filterColumn) => {
                                                    if (filterColumn.filColDisplay === 1) {
                                                        return displayFilterFields(filterColumn);
                                                    }
                                                    return null;
                                                })
                                            }
                                        </Grid>
                                        <Grid item lg={4} md={8} sm={12} >
                                            {
                                                filters && filters.rptFilterColumns && filters.rptFilterColumns.map((filterColumn) => {
                                                    if (filterColumn.filColDisplay === 2) {
                                                        return displayFilterFields(filterColumn);
                                                    }
                                                    return null;
                                                })
                                            }
                                        </Grid>
                                        <Grid item lg={4} md={8} sm={12}>
                                            {
                                                filters && filters.rptFilterColumns && filters.rptFilterColumns.map((filterColumn) => {
                                                    if (filterColumn.filColDisplay === 3) {
                                                        return displayFilterFields(filterColumn);
                                                    }
                                                    return null;
                                                })
                                            }
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Paper>
                        </Grid>
                        {previewData && <React.Fragment>
                            <Grid item xs={12}>
                                <Paper>
                                    <Grid item xs={3}>
                                        <Box display="flex" p={1}>
                                            <Box p={1}>
                                                <font size="4">{t("administration:report.title.reportPreview")}</font>
                                            </Box>
                                        </Box>
                                    </Grid>
                                </Paper>
                            </Grid>
                            <Grid item xs={12}>
                                <Grid item lg={12} md={12} xs={12}>
                                    <div className={classes.pdfContent}>
                                        <iframe className={classes.iframe} src={`data:application/pdf;base64,${previewData}`} frameBorder="0" />
                                    </div>
                                </Grid>
                            </Grid>
                        </React.Fragment>}
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};

export default withErrorHandler(ReportGenerate);