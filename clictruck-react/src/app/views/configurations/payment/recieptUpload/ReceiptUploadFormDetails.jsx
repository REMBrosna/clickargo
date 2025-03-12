import React, { useState, useEffect } from "react";
import {
    Grid,
    Paper,
    Snackbar,
    Tabs,
    Tab,
    Divider
} from "@material-ui/core";

import axios from 'axios.js';
import { useParams, useHistory } from "react-router-dom";
import { MatxLoading } from "matx";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1Alert from "app/c1component/C1Alert";
import PilotOrder from "./ReceiptUploadForm";
import C1Propertiestab from "app/c1component/C1PropertiesTab";



import { Assignment, FileCopy } from "@material-ui/icons";
import { Icon } from "@material-ui/core";
import { paymentAdviceGenDB } from "fake-db/db/paymentAdviceGen";
import { useLocation } from "react-router-dom";

const tabList = [{ text: "Payment", icon: <FileCopy /> }];

const CommonApplicationFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, adviceRefNoNo } = useParams();
    const history = useHistory();

    let location = useLocation();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);
    const [isAlive, setIsAlive] = useState(true);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState([]);
    const [inputData, setInputData] = useState({
        appId: "NIL20201220123",
        adviceRefNoNo: "",
        paymentAdviceDate: "22/12/2020",
        version: "1",
        submitDate: "20-Nov-20",
        status: "Pending",

        list: [
            {
                appId: "CAM20201112",
                paymentRefNo: "INVCAM20201112",
                feeType: "Service Charge",
                paymentAmount: "$100",

            },
        ],


    });

    const [snackBarState, setSnackBarState] = useState({ open: false, vertical: 'top', horizontal: 'center', msg: '', severity: 'success' });

    //api request for the details here
    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            // axios.get("/api/co/master/entity/country/" + accnID).then(({ data }) => {
            const data = paymentAdviceGenDB.list.find((doc) => doc.adviceRefNoNo === adviceRefNoNo);
            // if (isAlive) {
            setData(data);
            setInputData({ ...data });
            // }

            // }).catch((error) => {
            //     console.log(error);
            // });
        }

        return () => setIsAlive(false);

    }, [isAlive, adviceRefNoNo, viewType]);


    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };


    const handleSubmit = async (values) => {
        setLoading(true);

        switch (viewType) {
            case 'new':
                console.log("handlesubmit", inputData);
                axios.post("/api/co/master/entity/country", { ...inputData })
                    .then((res) => {

                        if (isAlive) {
                            setSubmitSuccess(true);
                            setLoading(false);
                            setData(res.data);
                            setInputData({ ...res.data });
                            setSnackBarState({ ...snackBarState, open: true, msg: 'Changes submitted successfully!', severity: 'success' });
                        }

                    }).catch((error) => {
                        setLoading(false);
                        setSubmitSuccess(false);
                        setSnackBarState({ ...snackBarState, open: true, msg: 'Error encountered whilte trying to submit the changes!', severity: 'error' });
                        console.log(error);
                    });
                break;
            case 'edit':
                setLoading(true);

                axios.put("/api/co/master/entity/country/" + adviceRefNoNo, { ...inputData })
                    .then((res) => {

                        if (isAlive) {
                            setSubmitSuccess(true);
                            setLoading(false);
                            setData(res.data);
                            setInputData({ ...res.data });
                            setSnackBarState({ ...snackBarState, open: true, msg: 'Changes submitted successfully!', severity: 'success' });
                        }

                    }).catch((error) => {
                        setLoading(false);
                        setSubmitSuccess(false);
                        setSnackBarState({ ...snackBarState, open: true, msg: 'Error encountered whilte trying to submit the changes!', severity: 'error' });
                        console.log(error);
                    });
                break;
            default: break;
        }
    };

    const handleValidate = () => {
        const errors = {};
        if (viewType === 'new') {

        }

        return errors;
    }

    const handleInputChange = (e) => {
        setInputData({ ...inputData, [e.currentTarget.name]: e.currentTarget.value });
    };

    const handleInputChangeSwitch = (e) => {
        console.log(`Input data: [${e.currentTarget.name}], [${e.target.name}], ${e.target.value}`, inputData);
        setInputData({ ...inputData, [e.currentTarget.name]: (e.currentTarget.checked) ? 'Y' : 'N' });
    };

    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
        if (viewType === 'new')
            history.goBack();
    };

    let viewBack = <C1FormButtons showsubmitapp="true" showCancel="true" />;
    let bcLabel = ' Receipt Upload Form';
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = 'View Receipt Upload';
                viewBack = <Grid item xs={12}>
                    <C1FormButtons showBack={true} />
                </Grid>;
                break;
            case 'new':
                bcLabel = 'Receipt Upload Form';
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


    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            {snackBar}
            <C1FormDetailsPanel
                isForm="true"
                routeSegments={[
                    { name: "Receipt Upload List", path: "/payment/recieptUpload/list" },
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
                                    indicatorColor="primary"
                                    textColor="primary"
                                >
                                    {tabList.map((item, ind) => (
                                        <Tab className="capitalize" value={ind} label={item.text} key={ind} icon={item.icon} />
                                    ))}
                                </Tabs>
                                <Divider className="mb-6" />

                                {tabIndex === 0 && <PilotOrder handleSubmit={handleSubmit}
                                    data={data} inputData={inputData}
                                    handleInputChange={handleInputChange}
                                    handleInputChangeSwitch={handleInputChangeSwitch}
                                    handleValidate={handleValidate}
                                    viewType={viewType}
                                    isSubmitting={loading} />}





                            </Paper>

                        </Grid>

                    </Grid>
                )}
            </C1FormDetailsPanel >
        </React.Fragment>

    );
};


export default withErrorHandler(CommonApplicationFormDetails, axios);