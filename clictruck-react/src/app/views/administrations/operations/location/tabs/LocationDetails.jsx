import { Grid } from "@material-ui/core";
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import BorderColorOutlinedIcon from '@material-ui/icons/BorderColorOutlined';
import React from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";
import { CK_MST_LOCATION_TYPE } from "app/c1utils/const";
import C1TextArea from "app/c1component/C1TextArea";

const LocationDetails = ({
    inputData,
    handleInputChange,
    handleDateChange,
    viewType,
    isDisabled,
    errors,
}) => {

    const { t } = useTranslation(["administration"]);

    return (
        <React.Fragment>
            <Grid item xs={12}>
                <C1TabContainer>

                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:locationManagement.locationDetails.generalDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:locationManagement.locationDetails.id")}
                                        name="locId"
                                        disabled
                                        onChange={handleInputChange}
                                        value={inputData?.locId || ''} />
                                    <C1InputField
                                        label={t("administration:locationManagement.locationDetails.name")}
                                        name="locName"
                                        required
                                        onChange={handleInputChange}
                                        value={inputData?.locName || ''}
                                        error={errors['locName'] !== undefined}
                                        helperText={errors['locName'] || ''}
                                        disabled={isDisabled}
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
                                        disabled={isDisabled}
                                    />
                                    <C1TextArea
                                        label={t("administration:locationManagement.locationDetails.address")}
                                        name="locAddress"
                                        onChange={handleInputChange}
                                        multiline
                                        textLimit={1024}
                                        disabled={isDisabled}
                                        value={inputData?.locAddress || ''} />
                                    <C1TextArea
                                        label={t("administration:locationManagement.locationDetails.remarks")}
                                        name="locRemarks"
                                        multiline
                                        textLimit={1024}
                                        onChange={handleInputChange}
                                        disabled={isDisabled}
                                        value={inputData?.locRemarks || ''} />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
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
                                        disabled={isDisabled}
                                    />
                                    <C1DateField
                                        label={t("administration:locationManagement.locationDetails.endDate")}
                                        name="locDtEnd"
                                        required
                                        value={inputData?.locDtEnd}
                                        onChange={handleDateChange}
                                        minDate={inputData?.locDtStart}
                                        error={errors['locDtEnd'] !== undefined}
                                        helperText={errors['locDtEnd'] || ''}
                                        disabled={isDisabled}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                        <div style={{ marginTop: 39 }}>
                            <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:locationManagement.locationDetails.coordinates")}>
                                <Grid container alignItems="center" spacing={3}>
                                    <Grid item xs={12} >
                                        <C1InputField
                                            label={t("administration:locationManagement.locationDetails.gps")}
                                            name="locGps"
                                            onChange={handleInputChange}
                                            value={inputData?.locGps || ''}
                                            error={errors['locGps'] !== undefined}
                                            helperText={errors['locGps'] || ''}
                                            disabled
                                        />
                                    </Grid>
                                </Grid>
                            </C1CategoryBlock>
                        </div>
                    </Grid>

                    <Grid container item lg={4} md={6} xs={12} direction="column">
                        <C1CategoryBlock icon={<BorderColorOutlinedIcon />} title={t("administration:locationManagement.locationDetails.properties")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:locationManagement.locationDetails.createdBy")}
                                        value={inputData?.locUidCreate}
                                        name="locUidCreate"
                                        required
                                        disabled
                                        onChange={handleInputChange}
                                        error={errors['TCkDoi.doiBlNo'] !== undefined}
                                        helperText={errors['TCkDoi.doiBlNo'] || ''}
                                    />

                                    <C1DateField
                                        label={t("administration:locationManagement.locationDetails.createdDate")}
                                        name="locDtCreate"
                                        required
                                        value={inputData?.locDtCreate}
                                        disabled
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    />

                                    <C1InputField
                                        label={t("administration:locationManagement.locationDetails.updatedBy")}
                                        value={inputData?.locUidLupd}
                                        name="locUidLupd"
                                        required
                                        disabled
                                        onChange={handleInputChange}
                                        error={errors['TCkDoi.doiContainerNo'] !== undefined}
                                        helperText={errors['TCkDoi.doiContainerNo'] || ''}
                                    />

                                    <C1DateField
                                        label={t("administration:locationManagement.locationDetails.updatedDate")}
                                        name="locDtLupd"
                                        required
                                        value={inputData?.locDtLupd}
                                        disabled
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    />

                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                </C1TabContainer>

            </Grid>
        </React.Fragment>
    );
};

export default LocationDetails;