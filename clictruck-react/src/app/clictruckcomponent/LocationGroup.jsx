import React from "react";

import { Grid } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import PropTypes from 'prop-types';
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1TextArea from "app/c1component/C1TextArea";
import { IconButton, InputAdornment } from "@material-ui/core";
import MapOutlinedIcon from '@material-ui/icons/MapOutlined';
import BusinessOutlinedIcon from '@material-ui/icons/BusinessOutlined';
import C1DateTimeField from "app/c1component/C1DateTimeField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";


/**  parameter list
 * @param locationType - "from" | "to" | "depo"
 */

const LocationGroup = ({
    type,
    details,
    time,
    mobile,
    cargoRec,
    remarks,
    locationArr,
    handleInputChange,
    handleTimeChange,
    locale,
    isMobileEnabled,
    isCargoOwnerEnabled = false,
    isDisabled,
    ignoreIsDisable = false,
    isDisableForMultiDrop = false,
    isEnableForToAccept = false, // is Enable for TO and Accept status
    errors
}) => {

    const { t } = useTranslation(["job"]);

    const isExcludeExisting = type.exclude && type.exclude.length > 0;
    const locations = locationArr.filter((item, i) => {
        return isExcludeExisting ? !type?.exclude?.includes(item.locId) : true;
    });

    return (
        <C1CategoryBlock icon={<BusinessOutlinedIcon />} title={type?.label}>
            <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12} >
                    <C1SelectAutoCompleteField
                        optionsMenuItemArr={locationArr.map((item, i) => {
                            return {
                                value: item.locId,
                                desc: item.locName
                            }
                        })}
                        required
                        name={type?.name}
                        label={""}
                        value={type?.value}
                        disabled={type?.disable}
                        onChange={(e, name, value) => handleInputChange({ name, target: { name, value: value?.value } })}
                        error={errors[type?.name] !== undefined}
                        helperText={errors[type?.name] || ''}
                    />
                    {/* <C1SelectField
                        name={type?.name}
                        label={""}
                        required
                        value={type?.value}
                        disabled={type?.disable}
                        isServer={false}
                        optionsMenuItemArr={locationArr.map((loc, ind) => (
                            <MenuItem value={loc.locId} key={loc.locId}>
                                {loc.locName}
                            </MenuItem>
                        ))}
                        onChange={handleInputChange}
                        error={errors[type?.name] !== undefined}
                        helperText={errors[type?.name] || ''}
                    /> */}
                    <C1TextArea
                        name={details?.name}
                        label={t("job:tripDetails.locationDetail")}
                        disabled={details?.disable  || (ignoreIsDisable ? false : isDisabled)}
                        value={details?.value}
                        multiline
                        required={details?.required}
                        textLimit={1024}
                        onChange={handleInputChange}
                        InputProps=
                        {details?.value ?
                            {
                                endAdornment:
                                    <InputAdornment position="end" style={{ paddingRight: "8px" }}>
                                        <IconButton
                                            aria-label="map"
                                            size="medium"
                                            onClick={() => {
                                                const url = `https://www.google.com/maps/search/${details?.value}/`;
                                                window.open(url, '_blank');
                                            }}
                                        >
                                            <MapOutlinedIcon />
                                        </IconButton>
                                    </InputAdornment>
                            } : null}
                        style={{ minHeight: '174px' }} //this minHeight is used to give initial space when textfield is enabled 
                        error={errors[details?.name] !== undefined}
                        helperText={errors[details?.name] || ''}
                    />
                    <C1DateTimeField
                        name={time?.name}
                        label={`${t("job:tripDetails.scheduleDetails")}`}
                        disabled={isEnableForToAccept ? (time?.disabled || !isEnableForToAccept) : (isDisabled || !type?.value || type?.disable || isDisableForMultiDrop) && !isEnableForToAccept}
                        value={time?.value}
                        onChange={handleTimeChange}
                        required={time?.isMandatory}
                        ampm={false}
                        error={errors[time?.name] !== undefined}
                        helperText={errors[time?.name] || ''}
                        disablePast={true}
                    />

                    <Grid container alignItems="stretch">
                        <Grid item md={6} xs={12} style={{paddingRight: "10px"}}  >
                            {isCargoOwnerEnabled &&
                                <C1InputField
                                    name={cargoRec?.name}
                                    label={t("job:tripDetails.cargoRec")}
                                    value={cargoRec?.value}
                                    inputProps={{ placeholder: isDisabled === true ? "" : t("job:tripDetails.cargoRec") }}
                                    onChange={handleInputChange}
                                    disabled={isEnableForToAccept ? (time?.disabled || !isEnableForToAccept) : (isDisabled || !type?.value || type?.disable || isDisableForMultiDrop) && !isEnableForToAccept}
                                    required={cargoRec?.isMandatory}
                                    error={errors[cargoRec?.name] !== undefined}
                                    helperText={errors[cargoRec?.name] || ''}
                                />
                            }
                        </Grid>
                        <Grid item md={isCargoOwnerEnabled ? 6 : 12} xs={12}>
                            {isMobileEnabled &&
                                <C1InputField
                                    name={mobile?.name}
                                    label={t("job:tripDetails.mobileNo")}
                                    value={mobile?.value}
                                    inputProps={{ placeholder: isDisabled === true ? "" : t("job:tripDetails.mobileFormat") }}
                                    onChange={handleInputChange}
                                    disabled={isEnableForToAccept ? (time?.disabled || !isEnableForToAccept) : (isDisabled || !type?.value || type?.disable || isDisableForMultiDrop) && !isEnableForToAccept}
                                    required={mobile?.isMandatory}
                                    error={errors[mobile?.name] !== undefined}
                                    helperText={errors[mobile?.name] || ''}
                                />
                            }
                        </Grid>
                    </Grid>
                    <C1TextArea
                        name={remarks?.name}
                        label={t("job:tripDetails.remarks")}
                        multiline
                        textLimit={1024}
                        value={remarks?.value}
                        onChange={handleInputChange}
                        disabled={isEnableForToAccept ? (time?.disabled || !isEnableForToAccept) : (isDisabled || !type?.value || type?.disable || isDisableForMultiDrop) && !isEnableForToAccept}
                        error={errors[remarks?.name] !== undefined}
                        helperText={errors[remarks?.name] || ''}
                    />

                </Grid>
            </Grid>
        </C1CategoryBlock>
    )
}

LocationGroup.propTypes = {
    type: PropTypes.exact({
        name: PropTypes.string,
        label: PropTypes.string,
        value: PropTypes.string,
        disable: PropTypes.bool
    }),
    details: PropTypes.exact({
        name: PropTypes.string,
        value: PropTypes.string,
        disable: PropTypes.bool
    }),
    time: PropTypes.exact({
        name: PropTypes.string,
        isMandatory: PropTypes.bool,
        value: PropTypes.string
    }),
    remarks: PropTypes.exact({
        name: PropTypes.string,
        value: PropTypes.string
    }),
    handleTimeChange: PropTypes.func,

}

export default LocationGroup;