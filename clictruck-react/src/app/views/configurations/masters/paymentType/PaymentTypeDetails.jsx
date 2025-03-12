import React from "react";
import Grid from "@material-ui/core/Grid";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import { isEditable } from "app/c1utils/utility";
import { useStyles } from "app/c1utils/styles";

const PaymentTypeDetails = ({ inputData, handleInputChange, viewType, isSubmitting, errors, locale }) => {

    let isDisabled = isEditable(viewType, isSubmitting);
    const classes = useStyles();

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1InputField
                                label={locale("masters:paymentType.details.tabs.recordDetails.ptCode")}
                                name="ptCode"
                                required={true}
                                disabled={viewType === "edit" || viewType === "view"}
                                onChange={handleInputChange}
                                value={inputData.ptCode}
                                error={!!(errors && errors.ptCode)}
                                helperText={errors && errors.ptCode ? errors.ptCode : null}
                                inputProps={{ maxLength: 35 }}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <C1InputField
                                required={true}
                                disabled={isDisabled}
                                label={locale("masters:paymentType.details.tabs.recordDetails.ptName")}
                                name="ptName"
                                onChange={handleInputChange}
                                value={inputData.ptName}
                                error={!!(errors && errors.ptName)}
                                helperText={errors && errors.ptName ? errors.ptName : null}
                            />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <Grid item xs={12}>
                                <C1InputField
                                    required={true}
                                    disabled={isDisabled}
                                    label={locale("masters:paymentType.details.tabs.recordDetails.ptDesc")}
                                    name="ptDesc"
                                    onChange={handleInputChange}
                                    value={inputData.ptDesc}
                                    error={!!(errors && errors.ptDesc)}
                                    helperText={errors && errors.ptDesc ? errors.ptDesc : null}
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default PaymentTypeDetails;
