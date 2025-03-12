import React from "react";
import { Grid, IconButton, InputAdornment } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import PropTypes from "prop-types";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1TextArea from "app/c1component/C1TextArea";
import C1DateTimeField from "app/c1component/C1DateTimeField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import MapOutlinedIcon from "@material-ui/icons/MapOutlined";
import BusinessOutlinedIcon from "@material-ui/icons/BusinessOutlined";

const LocationGroupTripChargesDomestic = ({
    time,
    type,
    mobile,
    errors,
    details,
    remarks,
    cargoRec,
    isDisabled,
    locationArr,
    isMobileEnabled,
    handleTimeChange,
    handleInputChange,
    ignoreIsDisable = false,
    isCargoOwnerEnabled = false,
  }) => {

    const { t } = useTranslation(["job"]);

    const renderInputField = (field, label, placeholder, required) => (
        <C1InputField
            name={field?.name}
            label={label}
            value={field?.value}
            inputProps={{ placeholder: isDisabled ? "" : placeholder }}
            onChange={handleInputChange}
            disabled={field?.disabled}
            required={required}
            error={!!errors[field?.name]}
            helperText={errors[field?.name] || ""}
        />
    );

    const renderTextArea = (field, label, extraProps = {}) => (
        <C1TextArea
            name={field?.name}
            label={label}
            value={field?.value}
            multiline
            textLimit={1024}
            onChange={handleInputChange}
            disabled={field?.disabled}
            required={field?.required}
            error={!!errors[field?.name]}
            helperText={errors[field?.name] || ""}
            {...extraProps}
        />
    );

    return (
        <C1CategoryBlock icon={<BusinessOutlinedIcon />} title={type?.label}>
            <Grid container spacing={1} alignItems={"baseline"}>
                <Grid item xs={12}>
                    <C1SelectAutoCompleteField
                        optionsMenuItemArr={locationArr.map(({ locId, locName }) => ({
                            value: locId,
                            desc: locName,
                        }))}
                        required
                        name={type?.name}
                        value={type?.value}
                        disabled={type?.disabled}
                        onChange={(e, name, value) =>
                            handleInputChange({ name, target: { name, value: value?.value } })
                        }
                        error={!!errors[type?.name]}
                        helperText={errors[type?.name] || ""}
                    />
                </Grid>
                <Grid item xs={12}>
                    {renderTextArea(details, t("job:tripDetails.locationDetail"), {
                        InputProps: details?.value
                            ? {
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <IconButton
                                            aria-label="open-map"
                                            onClick={() =>
                                                window.open(
                                                    `https://www.google.com/maps/search/${details?.value}/`,
                                                    "_blank"
                                                )
                                            }
                                        >
                                            <MapOutlinedIcon />
                                        </IconButton>
                                    </InputAdornment>
                                ),
                            }
                            : null,
                        style: { minHeight: "174px" },
                    })}
                </Grid>
                <Grid item xs={12}>
                    <C1DateTimeField
                        disablePast
                        ampm={false}
                        name={time?.name}
                        value={time?.value}
                        disabled={time?.disabled}
                        onChange={handleTimeChange}
                        required={time?.isMandatory}
                        error={!!errors[time?.name]}
                        helperText={errors[time?.name] || ""}
                        label={t("job:tripDetails.scheduleDetails")}
                    />
                </Grid>
                {isCargoOwnerEnabled && (
                    <Grid item md={6} xs={12}>
                        {renderInputField(
                            cargoRec,
                            t("job:tripDetails.cargoRec"),
                            t("job:tripDetails.cargoRec"),
                            cargoRec?.isMandatory
                        )}
                    </Grid>
                )}
                {isMobileEnabled && (
                    <Grid item md={isCargoOwnerEnabled ? 6 : 12} xs={12}>
                        {renderInputField(
                            mobile,
                            t("job:tripDetails.mobileNo"),
                            t("job:tripDetails.mobileFormat"),
                            mobile?.isMandatory
                        )}
                    </Grid>
                )}

                <Grid item xs={12}>
                    {renderTextArea(remarks, t("job:tripDetails.remarks"))}
                </Grid>
            </Grid>
        </C1CategoryBlock>
    );
};

LocationGroupTripChargesDomestic.propTypes = {
    type: PropTypes.exact({
        name: PropTypes.string,
        label: PropTypes.string,
        value: PropTypes.string,
        disabled: PropTypes.bool,
    }).isRequired,
    details: PropTypes.exact({
        name: PropTypes.string,
        value: PropTypes.string,
        disabled: PropTypes.bool,
        required: PropTypes.bool,
    }).isRequired,
    time: PropTypes.exact({
        name: PropTypes.string,
        isMandatory: PropTypes.bool,
        value: PropTypes.string,
        disabled: PropTypes.bool,
    }).isRequired,
    mobile: PropTypes.exact({
        name: PropTypes.string,
        value: PropTypes.string,
        isMandatory: PropTypes.bool,
        disabled: PropTypes.bool,
    }),
    cargoRec: PropTypes.exact({
        name: PropTypes.string,
        value: PropTypes.string,
        isMandatory: PropTypes.bool,
        disabled: PropTypes.bool,
    }),
    remarks: PropTypes.exact({
        name: PropTypes.string,
        value: PropTypes.string,
    }).isRequired,
    locationArr: PropTypes.arrayOf(
        PropTypes.shape({
            locId: PropTypes.string.isRequired,
            locName: PropTypes.string.isRequired,
        })
    ).isRequired,
    handleInputChange: PropTypes.func.isRequired,
    handleTimeChange: PropTypes.func.isRequired,
    isMobileEnabled: PropTypes.bool,
    isCargoOwnerEnabled: PropTypes.bool,
    isDisabled: PropTypes.bool,
    ignoreIsDisable: PropTypes.bool,
    isDisableForMultiDrop: PropTypes.bool,
    isEnableForToAccept: PropTypes.bool,
    errors: PropTypes.object.isRequired,
};

export default LocationGroupTripChargesDomestic;
