import React from "react";
import Grid from "@material-ui/core/Grid";
import { isEditable } from "app/c1utils/utility";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";

const CurrencyDetails = ({
    data,
    handleInputChange,
    viewType,
    isSubmitting,
    errors,
    locale }) => {

    let isDisabled = isEditable(viewType, isSubmitting)

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3} alignItems="center">
                    <Grid container item direction="column">
                        <Grid item xs={12}>
                            <C1InputField
                                label={locale("masters:currency.details.tabs.recordDetails.ccyCode")}
                                name="ccyCode"
                                required
                                disabled={(viewType === 'edit' || viewType === 'view')}
                                onChange={handleInputChange}
                                value={data.ccyCode}
                                error={errors && errors.ccyCode ? true : false}
                                helperText={errors && errors.ccyCode ? errors.ccyCode : null}
                                inputProps={{ maxLength: 3 }}
                            />

                            <C1InputField
                                label={locale("masters:currency.details.tabs.recordDetails.ccyDescription")}
                                name="ccyDescription"
                                required
                                disabled={isDisabled}
                                onChange={handleInputChange}
                                value={data.ccyDescription}
                                error={errors && errors.ccyDescription ? true : false}
                                helperText={errors && errors.ccyDescription ? errors.ccyDescription : null}
                                inputProps={{ maxLength: 255 }}
                            />

                            <C1InputField
                                label={locale("masters:currency.details.tabs.recordDetails.ccyDescriptionOth")}
                                name="ccyDescriptionOth"
                                disabled={isDisabled}
                                onChange={handleInputChange}
                                value={data.ccyDescriptionOth}
                                inputProps={{ maxLength: 512 }}
                            />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default CurrencyDetails;