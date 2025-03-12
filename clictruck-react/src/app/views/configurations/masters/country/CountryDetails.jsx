import React from "react";
import Grid from "@material-ui/core/Grid";

import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";

import { isEditable } from "app/c1utils/utility";

const CountryDetails = ({
    data,
    handleInputChange,
    viewType,
    isSubmitting,
    errors,
    locale }) => {

    let isDisabled = isEditable(viewType, isSubmitting);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3} alignItems="flex-start">
                    <Grid item lg={12} md={8} xs={12} sm={12}>
                        <Grid container alignItems="flex-start">
                            <Grid item xs={12}>
                                <C1InputField
                                    label={locale("masters:country.details.tabs.recordDetails.ctryCode")}
                                    name="ctyCode"
                                    required
                                    disabled={(viewType === 'edit' || viewType === 'view')}
                                    onChange={handleInputChange}
                                    value={data.ctyCode}
                                    error={errors && errors.ctyCode ? true : false}
                                    helperText={errors && errors.ctyCode ? errors.ctyCode : null}
                                    inputProps={{ maxLength: 2 }}
                                />

                                <C1InputField
                                    label={locale("masters:country.details.tabs.recordDetails.ctryDesc")}
                                    name="ctyDescription"
                                    required
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    value={data.ctyDescription}
                                    error={errors && errors.ctyDescription ? true : false}
                                    helperText={errors && errors.ctyDescription ? errors.ctyDescription : null}
                                    inputProps={{ maxLength: 255 }}
                                />

                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default CountryDetails;