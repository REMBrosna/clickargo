import React, { useState, useEffect } from "react";
import {
    Grid,
    Paper,
    Tabs,
    Tab,
    Divider
} from "@material-ui/core";

import { useTranslation } from "react-i18next";
import { useParams, useHistory } from "react-router-dom";
import { MatxLoading } from "matx";

import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";
import AgencyOfficeDetails from "./AgencyOfficeDetails";
import C1Propertiestab from "app/c1component/C1PropertiesTab";
import C1AuditTab from "app/c1component/C1AuditTab";

import { deepUpdateState } from "app/c1utils/stateUtils";
import { commonTabs } from "app/c1utils/const";
import useHttp from "app/c1hooks/http";
import { isEmpty, generateId } from "app/c1utils/utility";


const AgencyOfficeFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, id } = useParams();

    const { t } = useTranslation(['masters', 'common']);

    let history = useHistory();

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);

    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    //flag for errors in submit
    const [isSubmitError, setSubmitError] = useState(false);

    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({
        id: {
            agoCode: '',
            agoAgyCode: ''
        },
        agoDesc: '',
        TCoreAgency: {
            agyCode: ''
        },
        agoDescOth: '',
        TCoreAgencyOfficeAddrList: []
    });

    const [officeAddress, setOfficeAddress] = useState({});
    const [addrType, setAddrType] = useState('PRIMARY');

    //api request for the details here
    useEffect(() => {
        if (viewType !== 'new') {
            sendRequest("/api/co/ccm/entity/agencyOffice/" + id, "getAgencyOffice", "get", {});
        }

    }, [sendRequest, id, viewType]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);

            switch (urlId) {
                case "getAgencyOffice":
                    break;
                case "saveAgencyOffice":
                case "updateAgencyOffice":
                    setSubmitSuccess(true);
                    setInputDataWrap(res.data);
                    break;
                default: break;
            }

            setInputData({ ...inputData, ...res.data });

            if (validation) {
                console.log("validation in useEffect....", validation);
                //setValidationErrors({ ...validation });
            }
        } else if (error) {
            //set loading to false to display back to the screen if error is encountered
            setLoading(false);
            //even though there is error, setting this to false to not display the snackbar
            setSubmitError(false);
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleSubmit = async (values) => {
        setLoading(true);

        switch (viewType) {
            case 'new':
                sendRequest("/api/co/ccm/portalagency/", "saveAgencyOffice", "post", { ...values, agoStatus: 'A' });
                break;

            case 'edit':
                sendRequest("/api/co/ccm/portalagency/" + id, "updateAgencyOffice", "put", { ...values });
                break;
            default: break;
        }
    };

    const handleValidate = () => {
        const errors = {};

        return errors;
    }


    const setInputDataWrap = (dataParam) => {
        setInputData(dataParam);
        handleAddrTypeChangeSub(addrType, dataParam);
    }

    const getOffAddr = (addrTypeParam, inputDataParam) => {
        for (var i = 0, len = inputDataParam.TCoreAgencyOfficeAddrList.length; i < len; i++) {
            if (addrTypeParam === inputDataParam.TCoreAgencyOfficeAddrList[i].id.agoaAddrType) {
                return inputDataParam.TCoreAgencyOfficeAddrList[i];
            }
        }
    }

    const handleAddrTypeChange = (e) => {
        setAddrType(e.target.value);
        handleAddrTypeChangeSub(e.target.value, inputData);
    }

    const handleAddrTypeChangeSub = (addrTypeTmp, inputDataParam) => {
        var officeAddrssTmp = getOffAddr(addrTypeTmp, inputDataParam);
        if (officeAddrssTmp) {
            setOfficeAddress(officeAddrssTmp);
        } else {
            const newOfficeAddr = {
                id: { agoaCode: inputDataParam.id.agoCode, agoaAgyCode: inputDataParam.id.agoAgyCode, agoaAddrType: addrTypeTmp }
                , agoaStatus: "A", TCoreAddr: { adrId: generateId() }
            };
            setOfficeAddress(newOfficeAddr);
            inputDataParam.TCoreAgencyOfficeAddrList.push(newOfficeAddr);
        }
    }

    const copyOffrAddrToInput = (officeAddressParam) => {
        const addrTypeTmp = officeAddressParam.id.agoaAddrType;
        for (var i = 0, len = inputData.TCoreAgencyOfficeAddrList.length; i < len; i++) {
            if (addrTypeTmp === inputData.TCoreAgencyOfficeAddrList[i].id.agoaAddrType) {
                inputData.TCoreAgencyOfficeAddrList[i] = officeAddressParam;
                break;
            }
        }
    }

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
    };


    const handleAddrDetailChange = (e) => {
        if (!officeAddress || isEmpty(officeAddress)) {
            console.log(`officeAddress is empty!`);
            handleAddrTypeChangeSub(addrType, inputData);
        }
        if (officeAddress && !isEmpty(officeAddress)) {
            const officeAddressTmp = { ...officeAddress, TCoreAddr: { ...officeAddress.TCoreAddr, [e.currentTarget.name]: e.currentTarget.value } };
            setOfficeAddress(officeAddressTmp);
            copyOffrAddrToInput(officeAddressTmp);
        }
    }

    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.goBack()
        },
        submit: true
    }} />;
    let bcLabel = t("masters:agencyOffice.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = t("masters:agencyOffice.details.breadCrumbs.sub.view");
                formButtons = <C1FormButtons options={{
                    back: { show: true, eventHandler: () => history.goBack() }
                }} />;
                break;
            case 'new':
                bcLabel = t("masters:agencyOffice.details.breadCrumbs.sub.new");
                break;
            default: break;
        }


    }

    return (
        loading ? <MatxLoading /> : <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("masters:agencyOffice.details.breadCrumbs.main"), path: "/master/agencyOffice/list" },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={{
                    success: isSubmitSuccess, error: isSubmitError, redirectPath: "/master/agencyOffice/list"
                }}
                isLoading={loading} >
                {(props) => (
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper className="p-3">
                                <Tabs
                                    className="mt-4"
                                    value={tabIndex}
                                    onChange={handleTabChange}
                                    indicatorColor="primary"
                                    textColor="primary"                                >
                                    {commonTabs.map((item, ind) => (
                                        <Tab className="capitalize" value={ind} label={t(item.text)} key={ind} icon={item.icon} />
                                    ))}
                                </Tabs>
                                <Divider className="mb-6" />

                                {tabIndex === 0 && <AgencyOfficeDetails
                                    inputData={inputData}
                                    addrType={addrType}
                                    officeAddress={officeAddress}
                                    handleInputChange={handleInputChange}
                                    handleAddrTypeChange={handleAddrTypeChange}
                                    handleAddrDetailChange={handleAddrDetailChange}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    errors={props.errors}
                                    locale={t} />}
                                {tabIndex === 1 && <C1Propertiestab dtCreated={inputData.agoDtCreate} usrCreated={inputData.agoUidCreate}
                                    dtLupd={inputData.agoDtLupd} usrLupd={inputData.agoUidLupd} />}
                                {tabIndex === 2 && <C1AuditTab filterId={id} />}
                            </Paper>

                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};


export default withErrorHandler(AgencyOfficeFormDetails);