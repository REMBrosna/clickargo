import React, {useState, useEffect} from "react";
import {
    Grid,
    Paper,
    Tabs,
    Tab,
    Divider,
} from "@material-ui/core";

import {useParams, useHistory} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {MatxLoading} from "matx";

import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";

import C1Propertiestab from "app/c1component/C1PropertiesTab";
import C1AuditTab from "app/c1component/C1AuditTab";
import {commonTabs} from "app/c1utils/const";
import useHttp from "app/c1hooks/http";

import DocAssociateDetails from "./DocAssociateDetails";


const DocAssociateFormDetails = () => {

    let {viewType, id} = useParams();
    const {t} = useTranslation(['masters', 'common']);
    let history = useHistory();
    const {isLoading, isFormSubmission, res, validation, error, urlId, sendRequest} = useHttp();

    const [tabIndex, setTabIndex] = useState(0);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isSubmitError, setSubmitError] = useState(false);
    const [errors, setErrors] = useState({});

    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({
        suppDocAssId: '',
        suppDocAssMandatory: 'N',
        suppDocAssStatus: 'A',
        suppDocShipType: '',
        suppDocParentPort: '',
        mstAttType: null,
        pediMstAppType: null,
    });

    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            sendRequest("/api/co/pedi/mst/entity/pediMstSuppDocAssociation/" + id, "getDocumentAssociation", "get", {});
        }
        // eslint-disable-next-line
    }, [id, viewType]);

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);

            switch (urlId) {
                case "getDocumentAssociation":
                    break;
                case "saveDocumentAssociation":
                case "updateDocumentAssociation":
                case "deActive":
                case "active":
                    setSubmitSuccess(true);
                    break;
                default:
                    break;
            }
            setInputData({...inputData, ...res.data});

        } else if (error) {
            //set loading to false to display back to the screen if error is encountered
            setLoading(false);
            //even though there is error, setting this to false to not display the snackbar
            setSubmitError(false);
        }

        if (validation) {
            setErrors({ ...validation });
            setLoading(false);
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);


    //api request for the details here
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleSubmit = async (values) => {
        setLoading(true);

        switch (viewType) {
            case 'new':
                if(inputData && ((inputData?.suppDocShipType && inputData?.suppDocShipType === "ALL") 
                    || (inputData?.suppDocParentPort && inputData?.suppDocParentPort === "ALL"))){
                    sendRequest("/api/co/pedi/mst/entity/pediMstSuppDocAssociation/all", "saveDocumentAssociation", "post", {...inputData});
                }else{
                    sendRequest("/api/co/pedi/mst/entity/pediMstSuppDocAssociation", "saveDocumentAssociation", "post", {...inputData});
                }
                break;

            case 'edit':
                setLoading(true);
                if(inputData && ((inputData?.suppDocShipType && inputData?.suppDocShipType === "ALL") 
                    || (inputData?.suppDocParentPort && inputData?.suppDocParentPort === "ALL"))){
                    sendRequest("/api/co/pedi/mst/entity/pediMstSuppDocAssociation/all/" + id, "updateDocumentAssociation", "put", {...inputData});
                }else{
                    sendRequest("/api/co/pedi/mst/entity/pediMstSuppDocAssociation/" + id, "updateDocumentAssociation", "put", {...inputData});
                }
                break;
            default:
                break;
        }
    };

    const handleValidate = () => {
        const errors = {};

        if (!inputData.mstAttType.mattId) {
            errors.mattName = t("masters:contract.suppDocs.required")
        }
        if (!inputData.pediMstAppType.appTypeId) {
            errors.mattDesc = t("masters:contract.suppDocs.required")
        }
        return errors;
    }

    const handleInputChange = (e) => {
        const {name, value} = e.target;
        console.log(`${e.target.name}, ${e.target.value}`);

        if (name === "mattId") {
            const newValues = {...inputData, mstAttType: {[name]: value}};
            // No mandatory if Doc Type is other
            if (value === "OTH") {
                newValues.suppDocAssMandatory = "N"
            }
            setInputData(newValues);
        } else if (name === "appTypeId") {
            setInputData({...inputData, pediMstAppType: {[name]: value}});
        } else {
            setInputData({...inputData, [e.target.name]: e.target.value});
        }
    };

    const handleRadioChange = (e) => {
        const {name, value} = e.currentTarget;
        setInputData({
            ...inputData,
            [name] : value
        });
    };

    const handleActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/pedi/mst/entity/pediMstSuppDocAssociation/"+ id +"/activate", "active", "put", inputData), 1000);
    }

    const handleDeActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/pedi/mst/entity/pediMstSuppDocAssociation/"+ id, "deActive", "delete", {}), 1000);
    }

    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.goBack()
        },
        submit: true,
    }}/>;

    let bcLabel = t("masters:docAssociate.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = t("masters:docAssociate.details.breadCrumbs.sub.view");
                formButtons = <C1FormButtons options={{
                    back: {
                        show: true, eventHandler: () => history.goBack()
                    },
                    activate: {
                        show: inputData.suppDocAssStatus === 'I',
                        eventHandler: () => handleActiveChange()
                    },
                }} />;
                break;
            case 'edit':
                bcLabel = t("masters:docAssociate.details.breadCrumbs.sub.edit");
                formButtons = <C1FormButtons options={{
                    back: {
                        show: true, eventHandler: () => history.goBack()
                    },
                    activate: {
                        show: inputData.suppDocAssStatus === 'I',
                        eventHandler: () => handleActiveChange()
                    },
                    deactivate: {
                        show: inputData.suppDocAssStatus === 'A',
                        eventHandler: () => handleDeActiveChange()
                    },
                    submit: true
                }} />;
                break;
            case 'new':
                bcLabel = t("masters:docAssociate.details.breadCrumbs.sub.new");
                break;
            default:
                break;
        }

    }

    return (
        loading ? <MatxLoading/> : <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    {name: t("masters:docAssociate.details.breadCrumbs.main"), path: "/master/docAssociate/list"},
                    {name: bcLabel},
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{...inputData}}
                values={{...inputData}}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={{
                    success: isSubmitSuccess, error: isSubmitError, redirectPath: "/master/docAssociate/list"
                }}
                isLoading={loading}>
                {(props) => (    
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper className="p-3">
                                <Tabs
                                    className="mt-4"
                                    value={tabIndex}
                                    onChange={handleTabChange}
                                    indicatorColor="primary"
                                    textColor="primary"
                                >
                                    {commonTabs.map((item, ind) => (
                                        <Tab className="capitalize" value={ind} label={t(item.text)} key={ind}
                                             icon={item.icon}/>
                                    ))}
                                </Tabs>
                                <Divider className="mb-6"/>

                                {tabIndex === 0 && <DocAssociateDetails
                                    handleInputChange={handleInputChange}
                                    handleRadioChange={(e) => handleRadioChange(e)}
                                    inputData={inputData}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    errors={{...props.errors, ...errors}}
                                    locale={t}/>}
                                {tabIndex === 1 &&
                                <C1Propertiestab dtCreated={inputData.suppDocAssDtCreate} usrCreated={inputData.suppDocAssUidCreate}
                                                 dtLupd={inputData.suppDocAssDtLupd} usrLupd={inputData.suppDocAssUidLupd}/>}
                                {tabIndex === 2 && <C1AuditTab filterId={inputData.suppDocAssId}/>}
                            </Paper>

                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};


export default withErrorHandler(DocAssociateFormDetails);