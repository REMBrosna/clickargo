import { Button, CircularProgress, Grid, Snackbar, Tooltip } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { CK_MST_LOCATION_TYPE } from "app/c1utils/const";
import C1DateField from "app/c1component/C1DateField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1PopUp from "app/c1component/C1PopUp";
import { useTranslation } from "react-i18next";
import { dialogStyles } from "app/c1utils/styles";
import { deepUpdateState } from "app/c1utils/statusUtils";
import C1Alert from "app/c1component/C1Alert";
import { NearMeOutlined } from "@material-ui/icons";

const TripLocationPopup = (props) => {

    const {
        open,
        handleAfterClose
    } = props

    /** ---------------- Declare states ------------------- */
    const { t } = useTranslation(["cargoowners", "administration", "button","common"]);
    const dialogClasses = dialogStyles();
    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();
    const [isOpen, setIsOpen] = useState(false)
    const [errors, setErrors] = useState({})
    const [inputData, setInputData] = useState({});
    const [loading, setLoading] = useState(false);
    const [validationErrors, setValidationErrors] = useState({})
    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
        open: false
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    useEffect(() => {
        setIsOpen(open)
        if (open === true) {
            setErrors({})
            sendRequest("/api/v1/clickargo/clictruck/administrator/location/-", "getData", "get", null);
        }
    }, [open])

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "createData": {
                    setLoading(false)
                    setInputData({ ...res?.data });
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.saveSuccess"),
                        open: true
                    });
                    setIsOpen(false)
                    handleAfterClose(false)
                    break;
                }
                case "getData": {
                    setInputData({ ...res.data });
                    setLoading(false)
                    break;
                }
                default: break;
            }
        }

        if (error) {
            setLoading(false);
        }

        //If validation has value then set to the errors
        if (validation) {
            setErrors({ ...validation });
            setLoading(false);
        }

    }, [urlId, res, isLoading, error, validation]);

    const handleClosePopup = (val) => {
        setIsOpen(false)
        handleAfterClose(val)
    }

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
    };

    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) })
    };

    const handleSaveOnClick = () => {
        setLoading(true);
        setValidationErrors({});
        sendRequest("/api/v1/clickargo/clictruck/administrator/location", "createData", "post", { ...inputData });
    };

    const handleCloseSnackBar = () => {
        setSnackBarOptions({...snackBarOptions, open: false})
    }


    const actionEl = <Tooltip title={t("buttons:add")}>
        <Button disabled={loading} onClick={handleSaveOnClick} className={dialogClasses.dialogButtonSpace}>
            {
                loading ? <CircularProgress color="inherit" size={30} /> : <NearMeOutlined color="primary" fontSize="large" />
            }
        </Button>
    </Tooltip>

    let snackBar = <Snackbar
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        open={snackBarOptions.open}
        onClose={handleCloseSnackBar}
        autoHideDuration={3000}>
        <C1Alert severity={'success'}>
            {snackBarOptions.successMsg}
        </C1Alert>
    </Snackbar>;

    return (<>
        {snackBar}
        <C1PopUp
            title={"Add new location"}
            openPopUp={isOpen}
            setOpenPopUp={handleClosePopup}
            actionsEl={actionEl}>
            <Grid item xs={12}>
                <C1TabContainer>

                    <Grid item md={6} xs={12} >
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:locationManagement.locationDetails.generalDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:locationManagement.locationDetails.name")}
                                        name="locName"
                                        required
                                        onChange={handleInputChange}
                                        value={inputData?.locName || ''}
                                        error={errors['locName'] !== undefined}
                                        helperText={errors['locName'] || ''}
                                    />
                                    <C1SelectField
                                        name="tckCtMstLocationType.lctyId"
                                        label={t("administration:locationManagement.locationDetails.type")}
                                        value={inputData?.tckCtMstLocationType?.lctyId}
                                        onChange={e => handleInputChange(e)}
                                        isServer={true}
                                        required
                                        options={{
                                            url: CK_MST_LOCATION_TYPE,
                                            key: "lctyId",
                                            id: 'lctyId',
                                            desc: 'lctyDesc',
                                            isCache: true
                                        }}
                                        error={errors['TCkCtMstLocationType.lctyId'] !== undefined}
                                        helperText={errors['TCkCtMstLocationType.lctyId'] || ''}
                                    />
                                    <C1InputField
                                        label={t("administration:locationManagement.locationDetails.address")}
                                        name="locAddress"
                                        onChange={handleInputChange}
                                        multiline
                                        rows={2}
                                        value={inputData?.locAddress || ''} />
                                    <C1InputField
                                        label={t("administration:locationManagement.locationDetails.remarks")}
                                        name="locRemarks"
                                        multiline
                                        rows={2}
                                        onChange={handleInputChange}
                                        value={inputData?.locRemarks || ''} />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid item md={6} xs={12}>
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:locationManagement.locationDetails.valilidilityDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1DateField
                                        label={t("administration:locationManagement.locationDetails.startDate")}
                                        name="locDtStart"
                                        required
                                        value={inputData?.locDtStart}
                                        onChange={handleDateChange}
                                        error={errors['locDtStart'] !== undefined}
                                        helperText={errors['locDtStart'] || ''}
                                    />
                                    <C1DateField
                                        label={t("administration:locationManagement.locationDetails.endDate")}
                                        name="locDtEnd"
                                        required
                                        value={inputData?.locDtEnd}
                                        onChange={handleDateChange}
                                        error={errors['locDtEnd'] !== undefined}
                                        helperText={errors['locDtEnd'] || ''}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                </C1TabContainer>

            </Grid>
        </C1PopUp>
    </>
    );
};

export default TripLocationPopup;


