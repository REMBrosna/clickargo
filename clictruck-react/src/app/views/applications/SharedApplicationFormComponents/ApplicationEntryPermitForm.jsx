import React, { useState, useEffect } from "react";
import {
    Grid,
    Paper,
    Snackbar,
    Tabs,
    Divider,
    Tab
} from "@material-ui/core";

import axios from 'axios.js';
import { useParams, useHistory } from "react-router-dom";
import { MatxLoading } from "matx";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1Alert from "app/c1component/C1Alert";

import { processApplicationDB } from "fake-db/db/processApplication";
import HeaderDetails from "./SharedSubMenuComponents/HeaderDetails";
import VesselDetails from "./SharedSubMenuComponents/VesselDetails";
import C1AuditTab from "app/c1component/C1AuditTab";
import SupportingDocs from "./SharedSubMenuComponents/SupportingDocsEP";
import { tabScroll } from "app/c1utils/styles";

import C1Query from "app/c1component/C1Query"
import C1Remark from "app/c1component/C1Remark"

import moment from "moment";
import useHttp from "app/c1hooks/http";

function ApplicationForm({ template }) {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, appId } = useParams();

    /* ------------- States ---------------------------------*/
    /**----------------------------------------------------------- */
    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, isFormSubmission, res, error, urlId, sendRequest } = useHttp();

    const history = useHistory();
    //const classes = useStyles();
    let { Title, DYTabs, Url, Fields } = template;
    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);
    const [isAlive, setIsAlive] = useState(true);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState([]);
    const [inputData, setInputData] = useState(Fields);

    let { header, falForm, others, supportingDocs } = inputData;

    const [snackBarState, setSnackBarState] = useState({ open: false, vertical: 'top', horizontal: 'center', msg: '', severity: 'success' });

    /* ------------- Side Effects (e.g. server calls)---------------------------------*/
    /**----------------------------------------------------------- */
    useEffect(() => {
        setSubmitSuccess(false);

        if (viewType === 'new') {
            setTimeout(() => { sendRequest("/api/app/ep/new", "", "post", {}); }, 2000);

        }
        return () => { };
    }, [appId, viewType]);

    // //api request for the details here
    // useEffect(() => {
    //     setLoading(false);
    //     setSubmitSuccess(false);
    //     if (viewType !== 'new') {
    //         let nav = processApplicationDB.list;
    //         const response = nav.find((app) => app.appId === appId);
    //         if (isAlive) {
    //             setData(response);
    //             setInputData({ ...response });
    //         }
    //     }

    //     //For new application
    //     if (viewType === 'new') {
    //         //retrieve the ucr details
    //         let vc = ucrDB.list.find(vc => vc.ucrNo === appId);
    //         //update header > voyage  when required
    //         let { voyageDetails, shipDetails } = header;
    //         voyageDetails.vcrNo = vc.ucrNo;
    //         voyageDetails.shipOwner = 'Jhon';
    //         voyageDetails.arrivalPort = vc.port;
    //         voyageDetails.departurePort = vc.port;
    //         //voyageDetails.shipName = vc.shipName;
    //         // voyageDetails.departureDate = vc.etd;

    //         let sd = shipFleetDB.list.find(sf => sf.imoNo === vc.imoNo);
    //         voyageDetails.imoNo = sd.imoNo;
    //         voyageDetails.shipName = sd.vesselName;
    //         voyageDetails.callSign = sd.callsign;
    //         voyageDetails.flageState = sd.vesselCountry;
    //         // shipDetails.shipType = sd.vesselType;

    //         // setInputData(inD => {
    //         //     if (inD.type === 'PAN') {
    //         //         inD.appId = "PAN" + moment(new Date()).format('YYYYMMDDhhmmss');
    //         //     }

    //         //     return inD;
    //         // });
    //     }

    //     return () => setIsAlive(false);

    // }, [isAlive, appId, viewType]);


    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };


    const handleSubmit = async (values) => {
        setLoading(true);

        switch (viewType) {
            case 'new':
                let appId = 'EP' + moment(new Date()).format('YYYYMMDDhhmmss');
                inputData.appId = appId;
                let submittedDate = moment(new Date()).format('DD-MMM-YYYY')
                inputData.submitDate = submittedDate;
                inputData.Status = "Submitted";

                processApplicationDB.list.push(inputData);
                setTimeout(() => {
                    setSubmitSuccess(true);
                    setLoading(false);
                    setSnackBarState({ ...snackBarState, open: true, msg: 'Changes submitted successfully!', severity: 'success' });
                    setInputData({ ...inputData });
                }, 5000);

                break;
            case 'edit':
                setLoading(true);
                processApplicationDB.list.map(app => {
                    let newApp = inputData;
                    if (app.appId == appId) {
                        Object.assign(app, newApp);
                        return newApp;
                    }
                });

                setSubmitSuccess(true);
                setLoading(false);
                setSnackBarState({ ...snackBarState, open: true, msg: 'Changes submitted successfully!', severity: 'success' });
                setInputData({ ...inputData });
                break;
                break;
            default: break;
        }
    };

    const handleValidate = () => {
        const errors = {};

        return errors;
    }

    const handleInputChange = (e) => {
        setInputData({ ...inputData, [e.target.name]: e.target.value });
    };

    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
        if (viewType === 'new')
            history.goBack();
    };

    let viewBack = <C1FormButtons showSubmit="true" showsubmitapp="true" showCancel="true" />;
    let bcLabel = 'Edit ' + Title;
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = Title;
                viewBack = <Grid item xs={12}>
                    {/* showDownload="false" fileName="EntryPermitEg.pdf" */}
                    <C1FormButtons showBack={true}
                        preview={
                            {
                                show: true,
                                appType: 'EntryPermitEg.pdf'
                            }}

                        ins={
                            {
                                show: true,
                                appType: 'entryPermit.pdf'
                            }}
                    />
                </Grid>;
                break;
            case 'new':
                bcLabel = 'New ' + Title;
                break;
            default: break;
        }
    }

    let snackBar = null;
    if (isSubmitSuccess) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = <Snackbar
            anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
            open={snackBarState.open}
            onClose={handleClose}
            autoHideDuration={3000}
            key={anchorOriginV + anchorOriginH
            }>
            <C1Alert onClose={handleClose} severity={snackBarState.severity}>
                {snackBarState.msg}
            </C1Alert>
        </Snackbar>;

    }

    let { voyageDetails, shipDetails } = header;

    let sectionDb = {
        moduleName: "EP",
        isDisplaySubSection: false,
        sections: [
        ]
    };
    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            {snackBar}
            <C1FormDetailsPanel
                isForm="true"
                routeSegments={[
                    { name: "Vessel Calls", path: Url },
                    { name: bcLabel },
                ]}
                cardHeader={bcLabel}
                actionBtns={viewBack}
                formInitialValues={{ ...inputData }}
                formValues={{ ...inputData }}
                formOnSubmit={(values, isSubmitting) => handleSubmit(values, isSubmitting)}
                formValidate={handleValidate}>
                {(props) => (
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper>

                                <Tabs
                                    className="mt-4"
                                    value={tabIndex}
                                    onChange={handleTabChange}
                                    //orientation="vertical"
                                    variant="scrollable"
                                    scrollButtons="on"
                                    indicatorColor="primary"
                                    textColor="primary"
                                >
                                    {DYTabs.map((item, ind) => {
                                        return <Tab className="capitalize" value={ind} label={item.text} key={ind} icon={item.icon} {...tabScroll(ind)} />
                                    })}
                                </Tabs>
                                <Divider className="mb-6" />
                                {tabIndex === 0 && <HeaderDetails handleSubmit={handleSubmit}
                                    data={data} inputData={voyageDetails}
                                    handleInputChange={handleInputChange}
                                    handleValidate={handleValidate}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    props={props} />}
                                {tabIndex === 1 && <VesselDetails handleSubmit={handleSubmit}
                                    data={data} inputData={shipDetails}
                                    handleInputChange={handleInputChange}
                                    handleValidate={handleValidate}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    props={props} />}
                                {tabIndex === 2 && <SupportingDocs handleSubmit={handleSubmit}
                                    data={data} inputData={inputData}
                                    handleInputChange={handleInputChange}
                                    handleValidate={handleValidate}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    props={props} />}
                                {tabIndex === 3 && <C1Query handleSubmit={handleSubmit}
                                    inputData={inputData}
                                    handleInputChange={handleInputChange}
                                    handleValidate={handleValidate}
                                    sectionDb={sectionDb}
                                    isDisabled={"Approved" === inputData.Status}
                                    appId={appId}
                                    props={props} />}
                                {tabIndex === 4 && <C1Remark handleSubmit={handleSubmit}
                                    inputData={inputData}
                                    handleInputChange={handleInputChange}
                                    handleValidate={handleValidate}
                                    sectionDb={sectionDb}
                                    isDisabled={"Approved" === inputData.Status}
                                    appId={appId}
                                    props={props} />}
                                {tabIndex === 5 && <C1AuditTab appStatus={inputData.Status} />}

                            </Paper>
                        </Grid>

                    </Grid>
                )}
            </C1FormDetailsPanel >
        </React.Fragment>

    );
};


export default withErrorHandler(ApplicationForm, axios);