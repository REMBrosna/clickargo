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
import DocRepoDetails from "./DocRepoDetails";
import C1Propertiestab from "app/c1component/C1PropertiesTab";
import C1AuditTab from "app/c1component/C1AuditTab";


import { Assignment, FileCopy } from "@material-ui/icons";
import { Icon } from "@material-ui/core";
import { docRepoDB } from "fake-db/db/docRepo";

const tabList = [{ text: "General Details", icon: <FileCopy /> }, { text: "Properties", icon: < Assignment /> }, { text: "Audits", icon: <Icon>schedule</Icon> }];

const DocRepoFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, accnID } = useParams();
    const history = useHistory();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);
    const [isAlive, setIsAlive] = useState(true);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState([]);
    const [inputData, setInputData] = useState({
        docType: '',
        shippingLine: '',
        vesselID: '',
        accnID: '',
        uploadedBy: ''
    });

    const [snackBarState, setSnackBarState] = useState({ open: false, vertical: 'top', horizontal: 'center', msg: '', severity: 'success' });

    //api request for the details here
    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            // axios.get("/api/co/master/entity/country/" + accnID).then(({ data }) => {
            const data = docRepoDB.list.find((doc) => doc.accnID === accnID);
            // if (isAlive) {
            setData(data);
            setInputData({ ...data });
            // }

            // }).catch((error) => {
            //     console.log(error);
            // });
        }

        return () => setIsAlive(false);

    }, [isAlive, accnID, viewType]);


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

                axios.put("/api/co/master/entity/country/" + accnID, { ...inputData })
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
            if (!inputData.docType)
                errors.docType = 'Required';
            if (inputData.docType && inputData.docType.length > 8)
                errors.docType = "Maximum of 8 characters.";
        }

        if (!inputData.docUpload) {
            errors.docUpload = 'Required'
        }


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

    let viewBack = <C1FormButtons showSubmit="true" showCancel="true" />;
    let bcLabel = 'Edit Document Repository Code';
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = 'View Document Repository Code';
                viewBack = <Grid item xs={12}>
                    <C1FormButtons showBack={true} />
                </Grid>;
                break;
            case 'new':
                bcLabel = 'New Document Repository Code';
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
                    { name: "Document Repository List", path: "/configuration/docRepo/list" },
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

                                {tabIndex === 0 && <DocRepoDetails handleSubmit={handleSubmit}
                                    data={data} inputData={inputData}
                                    handleInputChange={handleInputChange}
                                    handleValidate={handleValidate}
                                    viewType={viewType}
                                    isSubmitting={loading} />}
                                {tabIndex === 1 && <C1Propertiestab
                                    dtCreated={data.ctyDtCreate}
                                    usrCreated={data.ctyUidCreate}
                                    dtLupd={data.ctyDtLupd}
                                    usrLupd={data.ctyUidLupd} />}
                                {tabIndex === 2 && <C1AuditTab
                                    filterId={data.ctyCode} />}
                            </Paper>

                        </Grid>
                        {/* {viewBack} */}

                    </Grid>
                )}
            </C1FormDetailsPanel >
        </React.Fragment>

    );
};


export default withErrorHandler(DocRepoFormDetails, axios);