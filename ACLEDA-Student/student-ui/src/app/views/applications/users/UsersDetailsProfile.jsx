import { Grid,Tabs } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import useHttp from "app/c1hooks/http";
import { MatxLoading } from "matx";
import UsersDetails from "./UserDetail";
import useAuth from "../../../hooks/useAuth";

const UsersDetailsProfile = () => {
    const { user } = useAuth();
    const { t } = useTranslation(["user", "common"]);
    let { viewType, id } = useParams();
    const history = useHistory();
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState({});
    const [openDeleteAction, setOpenDeletePopupAction] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [inputData, setInputData] = useState({});
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [tabIndex, setTabIndex] = useState(0);
    const [errors, setErrors] = useState({});
    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: ""
    }
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    const url = "api/v1/library/mst/entity/itm";
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: '',
        severity: 'success'
    });
    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, [name]: e });
    }
    const handleValidate = () => {
        const errors = {};
        if (inputData?.itmQty === null) {
            errors.itmQty = "Required";
        }
        return errors;
    };
    useEffect(() => {
        sendRequest(`/api/v1/users/${user.id}`, 'doFetch', 'GET', null);
        // eslint-disable-next-line
    }, [id, viewType]);
    useEffect(() => {
        setSnackBarOptions(defaultSnackbarValue);
        if (!isLoading && !error && res && !validation) {
            setLoading(false);
            switch (urlId) {
                case "doFetch":{
                    setInputData(res.data?.data);
                    setData(res.data)
                    break;
                }
                case "doCreate": {
                    setInputData(res.data);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: `Application Student has been created.`,
                        redirectPath: `/student/applicationStudent/list`
                    });
                    break;
                }
                case "doUpdate": {
                    setInputData(res.data);
                    if (res.data) {
                        setSnackBarOptions({
                            ...snackBarOptions,
                            success: true,
                            successMsg: `Application Student has been save!`,
                            redirectPath: `/student/applicationStudent/list/`
                        })
                    }
                    break;
                }
                default: break;
            }
            setRefresh(true);
            setOpenDeletePopupAction(false);
        }
        if (validation) {
            setErrors(validation);
            setLoading(false);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, isFormSubmission, validation, error]);
    const handleSave = async (e) => {
        e.preventDefault();
        setLoading(true);
        setErrors({});
        sendRequest(`api/v1/users/${id}`, "doUpdate", "put", { ...inputData, id: user.id });
    }

    const handleInputChange = (e, key) => {
        setInputData({ ...inputData, [e.target.name]: e.target.value });
    };

    let bcLabel = t("user.profile.userProfile");
    if (viewType) {
        switch (viewType) {
            case 'view':
                break;
            case 'new':
                bcLabel = 'New ' + bcLabel;
                break;
            default: break;
        }
    }
    console.log("tabIndex", tabIndex)
    let formButtons = <C1FormButtons options={{
        save: { show:true, eventHandler: (e) => handleSave(e) },
        // submitOnClick: { show:  viewType === 'edit',  eventHandler: handleSubmit},
        back: { show: true, eventHandler: () => history.push("/student/applicationStudent/list") }
    }}>
    </C1FormButtons>
    return (
        loading ? <MatxLoading /> :
            <React.Fragment>
                <C1FormDetailsPanel
                    //isForm="true"
                    breadcrumbs={[
                        { name: t("user.profile.userProfile"), path: "/student/applicationStudent/list" },
                        { name: bcLabel },
                    ]}
                    title={bcLabel}
                    formButtons={formButtons}
                    snackBarOptions={snackBarOptions}
                    // onSubmit={(values, actions) => handleSubmit(values, actions)}
                    formInitialValues={{ ...inputData }}
                    formValues={{ ...inputData }}
                    onValidate={handleValidate}
                    isLoading={loading}
                >
                    {() => (
                        <Grid container spacing={3}>
                            <Grid item xs={10}>
                                {tabIndex === 0 && <UsersDetails
                                    errors={errors}
                                    viewType={viewType}
                                    inputData={inputData}
                                    handleDateChange={handleDateChange}
                                    handleInputChange={handleInputChange}
                                />}
                            </Grid>
                        </Grid>
                    )}
                </C1FormDetailsPanel >
            </React.Fragment>
    );
};
export default UsersDetailsProfile;