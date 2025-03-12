import { Divider, Grid, Paper, Tab, Tabs } from "@material-ui/core";
import { Assignment, FileCopy, Schedule } from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1Propertiestab from "app/c1component/C1PropertiesTab";
import useHttp from "app/c1hooks/http";
import { Status } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { previewPDF, Uint8ArrayToString } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import axios from 'axios.js';
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

import ContractDetails from "./ContractDetails";
import ContractSuppDocs from "./ContractSuppDocs";
import ShippingAgentDetails from "./ShippingAgentDetails";

const ContractFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, radio } = useParams();
    const { t } = useTranslation(['masters', 'common']);
    let history = useHistory();
    const { user } = useAuth();

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);

    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});
    const [openPopupAction, setOpenPopupAction] = useState(false);
    const [confirmAction, setConfirmAction] = useState("");

    // eslint-disable-next-line
    const [controlMsg, setControlsMsg] = useState("");
    const defaultSnackbarValue = {
        success: false,
        successMsg: null,
        error: false,
        errorMsg: null,
        redirectPath: null
    }
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    const [contractDocs, setContractDocs] = useState([]);
    const [docDeleteId, setDocDeleteId] = useState()

    const [inputData, setInputData] = useState({
        id: {
            aconShlId: "",
            aconSagId: "",
        },
        radio: "",
        aconShlName: "",
        aconSagName: "",
        accnExpiryDate: new Date(),
        aconStatus: "",
        aconRecStatus: "",
        aconDtCreate: "",
        aconUidCreate: "",
        aconDtLupd: "",
        aconUidLupd: "",
        tcoreAccnByAconShlId: {},
        tcoreAccnByAconSagId: {},
    });

    const accountType = user?.coreAccn?.TMstAccnType?.atypId;
    const isShippingLine = accountType === "ACC_TYPE_SHIP_LINE";
    const isShippingAgent = accountType === "ACC_TYPE_SHIP_AGENT";
    const isAdmin = accountType === "ACC_TYPE_MPWT";

    //for loading the supporting documents
    useEffect(() => {
        sendRequest(`/api/contracts/suppDocs/${radio}`, "suppDocs", "get")
        axios.get("/api/contracts/suppDocs/" + radio)
            .then((res) => setContractDocs([...res.data]));
    // eslint-disable-next-line
    }, []);

    //api request for the details here
    useEffect(() => {
        setLoading(false);
        if (viewType !== 'new') {
            sendRequest("/api/contracts/" + radio, "getContract", "get", {});
        }
        // eslint-disable-next-line
    }, [radio, viewType]);

    //executed when there are changes in the parameters
    useEffect(() => {
        setSnackBarOptions(defaultSnackbarValue);
        if (!isLoading && !error && !validation && res) {
            setLoading(isLoading);
            switch (urlId) {
                case "getContract":
                    setInputData({ ...inputData, ...res.data });
                    break;
                case "saveContract":
                    const { radio } = res.data;
                    setSnackBarOptions({
                        success: true,
                        successMsg: t("masters:contract.message.success.save"),
                        redirectPath: "/master/contract/edit/" + radio
                    })
                    setLoading(false);
                    setInputData({ ...inputData, ...res.data });
                    break;
                case "updateContract":
                case "submit":
                case "approve":
                case "activate":
                case "deactivate": {
                    let msg = t("masters:contract.message.success.submitted")
                    if (urlId === 'approve')
                        msg = t("masters:contract.message.success.approved")
                    if (urlId === 'activate')
                        msg = t("masters:contract.message.success.active")
                    if (urlId === 'deactivate')
                        msg = t("masters:contract.message.success.deactivated")

                    setSnackBarOptions({
                        success: true,
                        successMsg: msg,
                        redirectPath: "/master/contract/list"
                    })
                    setLoading(false);
                    break;
                }
                case 'upload':
                    const data = res.data;
                    setContractDocs(d => d.map(el => {
                        if (el.seq === data.seq) {
                            el.attData = data.attData;
                            el.attName = data.attName;
                            el.attSize = data.attSize;
                            el.attId = data.attId;
                            el.refId = data.attReferenceid;
                        }
                        return el;
                    }));
                    break;
                case 'delete':
                    setSnackBarOptions({
                        success: true,
                        successMsg: t("masters:contract.message.success.deleted")
                    })
                    setContractDocs(d => d.map(el => {
                        if (el.attId === docDeleteId) {
                            el.attData = null;
                            el.attName = null;
                            el.attSize = null;
                            el.attId = null;
                            el.refId = null;
                        }
                        return el;
                    }));
                    break;
                default:
                    break;
            }
        } else if (error) {
            setLoading(false);
            setSnackBarOptions({ error: true });
        }
        if (validation) {
            setLoading(false);
            setErrors({ validation });
        }
        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);


    //api request for the details here
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleSave = async (values) => {
        setLoading(true);
        switch (viewType) {
            case 'new':
                sendRequest("/api/contracts", "saveContract", "post", { ...inputData });
                break;

            case 'edit':
                setLoading(true);
                sendRequest("/api/contracts/" + radio, "saveContract", "put", { ...inputData });
                break;
            default:
                break;
        }
    };

    const handleValidate = () => {
        return errors;
    }

    const handleInputChange = (e) => {
        const { value } = e.target;
        if (value) {
            axios.get("api/co/ccm/entity/accn/" + value)
                .then(result => {
                    setInputData({
                        ...inputData,
                        tcoreAccnByAconShlId: result.data,
                    });
                }).catch((error) => {
                    console.log(error);
                });
        } else {
            setInputData({
                ...inputData,
                tcoreAccnByAconShlId: null
            })
        }
    };

    const handleDateChange = (name, date) => {
        setInputData({
            ...inputData,
            [name]: date
        });
    }

    const handleConfirmAction = () => {
        setOpenPopupAction(false);
        setLoading(true);
        setControlsMsg(t("masters:contract.message.action", { action: confirmAction }));

        sendRequest(`/api/contracts/${radio}/${confirmAction.toLowerCase()}`, confirmAction.toLowerCase(), "post", { ...inputData });
    }

    const eventHandler = (action) => {
        setOpenPopupAction(true);
        setConfirmAction(action);
    }

    // CPEDI-156
    const handleAutoCompleteSelect = (e, name, value) => {
        const temp = {
            ...inputData,
            tcoreAccnByAconShlId: {
                ...inputData.tcoreAccnByAconShlId,
                accnId: value?.value,
            },
        };
        setInputData({ ...temp, ...deepUpdateState(temp, name, value?.value) });
    };

    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.push("/master/contract/list")
        },
        save: {
            show: viewType === "new" || (viewType === "edit" && inputData.aconStatus === Status.DRF.code),
            eventHandler: () => handleSave()
        }
    }} />;

    let bcLabel = t("masters:contract.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = t("masters:contract.details.breadCrumbs.sub.view");
                formButtons = <C1FormButtons options={{
                    back: {
                        show: true,
                        eventHandler: () => history.push("/master/contract/list")
                    },
                    submitOnClick: {
                        show: isShippingAgent && inputData.aconStatus === Status.DRF.code,
                        eventHandler: () => eventHandler("submit")
                    },
                    approve: {
                        show: (isShippingLine || isAdmin) && inputData.aconStatus === Status.SUB.code && inputData.aconRecStatus === 'A',
                        eventHandler: () => eventHandler("approve")
                    },
                    activate: {
                        show: (isShippingAgent || isShippingLine || isAdmin) && inputData.aconRecStatus === 'I',
                        eventHandler: () => eventHandler("activate")
                    },
                    deactivate: {
                        show: (isShippingAgent || isShippingLine || isAdmin) && inputData.aconRecStatus === 'A',
                        eventHandler: () => eventHandler("deactivate")
                    },
                }} />;
                break;
            case 'new':
                bcLabel = t("masters:contract.details.breadCrumbs.sub.new");
                break;
            case 'edit':
                bcLabel = t("masters:contract.details.breadCrumbs.sub.edit");
                formButtons = <C1FormButtons options={{

                    approve: {
                        show: (isShippingLine || isAdmin) && inputData.aconStatus === Status.SUB.code && inputData.aconRecStatus === 'A',
                        eventHandler: () => eventHandler("approve")
                    },
                    activate: {
                        show: (isShippingAgent || isShippingLine || isAdmin) && inputData.aconRecStatus === 'I',
                        eventHandler: () => eventHandler("activate")
                    },
                    deactivate: {
                        show: (isShippingAgent || isShippingLine || isAdmin) && inputData.aconRecStatus === 'A',
                        eventHandler: () => eventHandler("deactivate")
                    },

                    back: {
                        show: true,
                        eventHandler: () => history.push("/master/contract/list")
                    },
                    save: {
                        show: (isShippingAgent || isAdmin) && (inputData.aconStatus === Status.SUB.code || inputData.aconStatus === Status.DRF.code
                            || inputData.aconStatus === Status.APP.code),
                        eventHandler: () => handleSave()
                    },
                    submitOnClick: {
                        show: isShippingAgent && inputData.aconStatus === Status.DRF.code,
                        eventHandler: () => eventHandler("submit")
                    }
                }} />;
                break;
            default:
                break;
        }

    }

    const handleFileChange = (e, index) => {
        e.preventDefault();

        let file = e.target.files[0];
        if (!file) {
            return;
        }
        const fileReader = new FileReader();
        fileReader.readAsArrayBuffer(e.target.files[0]);

        let type = e.target.name;
        fileReader.onload = e => {
            const uint8Array = new Uint8Array(e.target.result);

            let imgStr = Uint8ArrayToString(uint8Array);
            let base64Sign = btoa(imgStr);

            //upload file to server
            let json = {
                seq: index,
                attName: file.name,
                attType: type,
                attSize: file.size,
                attData: base64Sign,
                attReferenceid: radio,
            }

            setLoading(true);
            sendRequest("/api/contracts/attach/upload", "upload", "post", json);
        };
    }


    const viewFile = (fileName, data) => {
        previewPDF(fileName, data);
    };

    const handleDeleteFile = (e, attId) => {
        e.preventDefault();
        setDocDeleteId(attId);
        sendRequest(`/api/contracts/attach/${attId}`, "delete", "Delete");
    }

    const commonTabs = [
        {
            text: "common:recordDetails",
            icon: <FileCopy />
        },
        {
            text: "Shipping Agent Details",
            disabled: viewType === 'new',
            icon: <Assignment />
        },
        {
            text: "common:properties.title",
            disabled: viewType === 'new',
            icon: <Assignment />
        },
        {
            text: t("common:supportingDocs.title"),
            disabled: viewType === 'new',
            icon: <FileCopy />,
        },
        {
            text: "common:audits.title",
            disabled: viewType === 'new',
            icon: <Schedule />
        },
    ];

    return (
        loading ? <MatxLoading /> : <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("contract.details.breadCrumbs.main"), path: "/master/contract/list" },
                    { name: bcLabel },
                ]}

                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onValidate={handleValidate}
                isLoading={loading}
                snackBarOptions={snackBarOptions}>
                {(props) => (
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper className="p-3">
                                <Tabs
                                    className="mt-4"
                                    value={tabIndex}
                                    onChange={handleTabChange}
                                    indicatorColor="primary"
                                    textColor="primary">
                                    {commonTabs.map((item, ind) => (
                                        <Tab className="capitalize" value={ind} label={t(item.text)} key={ind}
                                            icon={item.icon} disabled={item.disabled} />
                                    ))}
                                </Tabs>
                                <Divider className="mb-6" />

                                {tabIndex === 0 && <ContractDetails
                                    inputData={inputData}
                                    handleInputChange={handleInputChange}
                                    handleDateChange={handleDateChange}
                                    handleAutoCompleteSelect={handleAutoCompleteSelect}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    errors={errors}
                                    isShippingAgent={isShippingAgent}
                                    isAdmin={isAdmin}
                                    handleValidate={handleValidate}
                                    locale={t} />}

                                {tabIndex === 1 && <ShippingAgentDetails
                                    inputData={inputData}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    locale={t} />}

                                {tabIndex === 2 &&
                                    <C1Propertiestab dtCreated={inputData.aconDtCreate} usrCreated={inputData.aconUidCreate}
                                        dtLupd={inputData.aconDtLupd} usrLupd={inputData.aconUidLupd} />}

                                {tabIndex === 3 && <ContractSuppDocs
                                    viewType={viewType}
                                    contractDocs={contractDocs}
                                    inputData={inputData}
                                    handleFileChange={handleFileChange}
                                    viewFile={viewFile}
                                    handleDeleteFile={handleDeleteFile}
                                    errors={errors} locale={t} />}

                                {tabIndex === 4 && <C1AuditTab
                                    filterId={radio} />}
                            </Paper>

                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
            <ConfirmationDialog
                open={openPopupAction}
                title={t("common:confirmMsgs.confirm.title")}
                text={t("masters:contract.message.action", { action: confirmAction })}
                onYesClick={() => handleConfirmAction()}
                onConfirmDialogClose={() => setOpenPopupAction(false)}
            />
        </React.Fragment>
    );
};

export default withErrorHandler(ContractFormDetails);