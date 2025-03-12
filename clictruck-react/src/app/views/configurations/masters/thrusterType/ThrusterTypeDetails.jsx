import React from "react";
import Grid from "@material-ui/core/Grid";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import { isEditable } from "app/c1utils/utility";
import { useStyles } from "app/c1utils/styles";

const NumberEngineDetails = ({ inputData, handleInputChange, viewType, isSubmitting, errors, locale }) => {
    let isDisabled = isEditable(viewType, isSubmitting);
    const classes = useStyles();
    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1InputField
                                label={locale("masters:thrusterType.details.tabs.recordDetails.ttCode")}
                                name="ttCode"
                                required={true}
                                disabled={viewType === "edit" || viewType === "view"}
                                onChange={handleInputChange}
                                value={inputData.ttCode}
                                error={!!(errors && errors.ttCode)}
                                helperText={errors && errors.ttCode ? errors.ttCode : null}
                                inputProps={{ maxLength: 35 }}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <C1InputField
                                required={true}
                                disabled={isDisabled}
                                label={locale("masters:thrusterType.details.tabs.recordDetails.ttName")}
                                name="ttName"
                                onChange={handleInputChange}
                                value={inputData.ttName}
                                error={!!(errors && errors.ttName)}
                                helperText={errors && errors.ttName ? errors.ttName : null}
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
                                    label={locale("masters:thrusterType.details.tabs.recordDetails.ttDesc")}
                                    name="ttDesc"
                                    onChange={handleInputChange}
                                    value={inputData.ttDesc}
                                    error={!!(errors && errors.ttDesc)}
                                    helperText={errors && errors.ttDesc ? errors.ttDesc : null}
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default NumberEngineDetails;
