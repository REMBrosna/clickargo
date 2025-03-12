import { Button, CircularProgress, Grid, Snackbar, Tooltip } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import useHttp from "app/c1hooks/http";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1PopUp from "app/c1component/C1PopUp";
import { useTranslation } from "react-i18next";
import { dialogStyles } from "app/c1utils/styles";
import C1Alert from "app/c1component/C1Alert";
import { NearMeOutlined } from "@material-ui/icons";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import NumFormat from "app/clictruckcomponent/NumFormat";

const TripLocationRatePopup = (props) => {

    const {
        indexRate,
        action,
        open,
        handleAfterClose,
        inputData,
        locationState,
        handleInputChange,
        handleActionOnClick,
        locationRate,
        errors
    } = props

    /** ---------------- Declare states ------------------- */
    const { t } = useTranslation(["cargoowners", "administration", "button","common"]);
    const dialogClasses = dialogStyles();
    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();
    const [isOpen, setIsOpen] = useState(false)
    const [loading, setLoading] = useState(false);
    const [validationErrors, setValidationErrors] = useState({})
    const [listRate, setListRate] = useState();

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
            sendRequest("/api/v1/clickargo/clictruck/administrator/location/-", "getData", "get", null);
            
        }

        let filterList = [];
        locationRate.map(item => {
            filterList.push(item.tckCtLocationByTrLocFrom.locId)
            filterList.push(item.tckCtLocationByTrLocTo.locId)
            })
        
        if(action === 'UPDATE'){
            filterList = filterList.filter((item) => item !== inputData?.tckCtLocationByTrLocFrom?.locId && item !== inputData?.tckCtLocationByTrLocTo?.locId)
        }
        setListRate(filterList);

    }, [open])

    const handleClosePopup = (val) => {
        setIsOpen(false)
        handleAfterClose(val)
    }

    const handleCloseSnackBar = () => {
        setSnackBarOptions({...snackBarOptions, open: false})
    }

    const onlyNumber = (e) => {
        if (e.charCode < 48) {
            return e.preventDefault();
        }
    }

    const actionEl = <Tooltip title={t("buttons:add")}>
        <Button disabled={loading} onClick={handleActionOnClick} className={dialogClasses.dialogButtonSpace}>
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
    console.log('inputData',inputData)
    return (<>
        {snackBar}
        <C1PopUp
            maxWidth="sm"
            title={action === 'NEW' ? locationRate.length > 0 ? "Location (Multi Drop)" : "Location and Charge" : indexRate > 0 ? "Location (Multi Drop)" : "Location and Charge"}
            openPopUp={isOpen}
            setOpenPopUp={handleClosePopup}
            actionsEl={actionEl}>
            <Grid item xs={12}>
                <C1TabContainer>
                {action === 'NEW' &&
                <Grid container item xs={12} sm={12} direction="column">
                    {locationRate.length === 0 &&
                    <C1CategoryBlock>
                    <C1SelectAutoCompleteField
                        name="tckCtLocationByTrLocFrom.locId"
                        label={t("administration:rateTableManagement.tripRates.locFrom")}
                        value={inputData?.tckCtLocationByTrLocFrom?.locId || ''}
                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                        required
                        disabled={false}
                        isServer={false}
                        optionsMenuItemArr={
                            locationState
                            .filter((item) => item.locId !== inputData?.tckCtLocationByTrLocTo?.locId)
                            .map((item) => {
                                return {
                                    value: item.locId,
                                    desc: item.locName
                                }
                            })}
                        error={!!errors.fromLocId}
                        helperText={errors.fromLocId ?? null}
                    />
                    </C1CategoryBlock>
                    }
                    {locationRate.length === 0 &&
                    <C1CategoryBlock>
                    <C1SelectAutoCompleteField
                        name="tckCtLocationByTrLocTo.locId"
                        label={t("administration:rateTableManagement.tripRates.locTo")}
                        value={inputData?.tckCtLocationByTrLocTo?.locId || ''}
                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                        required
                        disabled={false}
                        isServer={false}
                        optionsMenuItemArr={
                            locationState
                                .filter((item) => item.locId !== inputData?.tckCtLocationByTrLocFrom?.locId)
                                .map((item) => {
                                    return {
                                        value: item.locId,
                                        desc: item.locName
                                    }
                                })}
                        error={!!errors.toLocId}
                        helperText={errors.toLocId ?? null}
                    />
                    </C1CategoryBlock>
                    }
                    {locationRate.length > 0 &&
                    <C1CategoryBlock>
                    <C1SelectAutoCompleteField
                        name="tckCtLocationByTrLocTo.locId"
                        label={t("administration:rateTableManagement.tripRates.locTo")}
                        value={inputData?.tckCtLocationByTrLocTo?.locId || ''}
                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                        required
                        disabled={false}
                        isServer={false}
                        optionsMenuItemArr={
                            locationState
                                .filter((item) => locationRate.some(itemA => !listRate.includes(item.locId)))
                                .map((item) => {
                                    return {
                                        value: item.locId,
                                        desc: item.locName
                                    }
                                })}
                        error={!!errors.toLocId}
                        helperText={errors.toLocId ?? null}
                    />
                    </C1CategoryBlock>
                    }
                    {locationRate.length === 0 &&
                    <C1InputField
                            name="trCharge"
                            label={t("administration:rateTableManagement.tripRates.charge")}
                            value={inputData?.trCharge || ''}
                            onChange={handleInputChange}
                            required
                            disabled={false}
                            isServer={true}
                            error={!!errors.trCharge}
                            helperText={errors.trCharge ?? null}
                            InputProps={{
                                inputComponent: NumFormat
                            }}
                            inputProps={{
                                onKeyPress: onlyNumber
                            }}
                        />
                    }
            
                </Grid>
                }

                {action === 'UPDATE' &&
                <Grid container item xs={12} sm={12} direction="column">
                    {indexRate === 0 &&
                    <C1CategoryBlock>
                    <C1SelectAutoCompleteField
                        name="tckCtLocationByTrLocFrom.locId"
                        label={t("administration:rateTableManagement.tripRates.locFrom")}
                        value={inputData?.tckCtLocationByTrLocFrom?.locId || ''}
                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                        required
                        disabled={false}
                        isServer={false}
                        optionsMenuItemArr={
                            locationState
                                .filter((item) => (locationRate.some(itemA => !listRate.includes(item.locId)) && item.locId !== inputData?.tckCtLocationByTrLocTo?.locId) || item.locId === inputData?.tckCtLocationByTrLocFrom?.locId)
                                .map((item) => {
                                    return {
                                        value: item.locId,
                                        desc: item.locName
                                    }
                                })}
                        error={!!errors.fromLocId}
                        helperText={errors.fromLocId ?? null}
                    />
                    </C1CategoryBlock>
                    }
                    {indexRate === 0 &&
                    <C1CategoryBlock>
                    <C1SelectAutoCompleteField
                        name="tckCtLocationByTrLocTo.locId"
                        label={t("administration:rateTableManagement.tripRates.locTo")}
                        value={inputData?.tckCtLocationByTrLocTo?.locId || ''}
                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                        required
                        disabled={false}
                        isServer={false}
                        optionsMenuItemArr={
                            locationState
                                .filter((item) => (locationRate.some(itemA => !listRate.includes(item.locId)) && item.locId !== inputData?.tckCtLocationByTrLocFrom?.locId) || item.locId === inputData?.tckCtLocationByTrLocTo?.locId)
                                .map((item) => {
                                    return {
                                        value: item.locId,
                                        desc: item.locName
                                    }
                                })}
                        error={!!errors.toLocId}
                        helperText={errors.toLocId ?? null}
                    />
                    </C1CategoryBlock>
                    }
                    {indexRate > 0 &&
                    <C1CategoryBlock>
                    <C1SelectAutoCompleteField
                        name="tckCtLocationByTrLocTo.locId"
                        label={t("administration:rateTableManagement.tripRates.locTo")}
                        value={inputData?.tckCtLocationByTrLocTo?.locId || ''}
                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                        required
                        disabled={false}
                        isServer={false}
                        optionsMenuItemArr={
                            locationState
                                .filter((item) => (locationRate.some(itemA => !listRate.includes(item.locId)) && item.locId !== inputData?.tckCtLocationByTrLocFrom?.locId) || item.locId === inputData?.tckCtLocationByTrLocTo?.locId)
                                .map((item) => {
                                    return {
                                        value: item.locId,
                                        desc: item.locName
                                    }
                                })}
                        error={!!errors.toLocId}
                        helperText={errors.toLocId ?? null}
                    />
                    </C1CategoryBlock>
                    }
                    {indexRate === 0 &&
                    <C1InputField
                            name="trCharge"
                            label={t("administration:rateTableManagement.tripRates.charge")}
                            value={inputData?.trCharge || ''}
                            onChange={handleInputChange}
                            required
                            disabled={false}
                            isServer={true}
                            error={!!errors.trCharge}
                            helperText={errors.trCharge ?? null}
                            InputProps={{
                                inputComponent: NumFormat
                            }}
                            inputProps={{
                                onKeyPress: onlyNumber
                            }}
                        />
                    }
            
                </Grid>
                }
                </C1TabContainer>

            </Grid>
        </C1PopUp>
    </>
    );
};

export default TripLocationRatePopup;


