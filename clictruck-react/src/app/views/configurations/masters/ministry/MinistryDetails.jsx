import React from "react";
import Grid from "@material-ui/core/Grid";

import C1TabContainer from "app/c1component/C1TabContainer";
import C1InputField from "app/c1component/C1InputField"
import { useStyles } from "app/c1utils/styles";
import { isEditable } from "app/c1utils/utility";

const MinistryDetails = ({
    inputData,
    handleInputChange,
    viewType,
    isSubmitting,
    errors,
    locale }) => {

    const classes = useStyles();
    let isDisabled = isEditable(viewType, isSubmitting);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={6} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                label={locale("masters:ministry.details.tabs.recordDetails.minCode")}
                                name="minCode"
                                required
                                disabled={(viewType === 'edit' || viewType === 'view')}
                                onChange={handleInputChange}
                                value={inputData.minCode}
                                error={errors && errors.minCode ? true : false}
                                helperText={errors && errors.minCode ? errors.minCode : null} />
                            <C1InputField
                                required
                                disabled={isDisabled}
                                label={locale("masters:ministry.details.tabs.recordDetails.minRegNo")}
                                name="minRegNo"
                                onChange={handleInputChange}
                                value={inputData.minRegNo}
                                error={errors && errors.minRegNo ? true : false}
                                helperText={errors && errors.minRegNo ? errors.minRegNo : null} />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={6} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                required
                                disabled={isDisabled}
                                label={locale("masters:ministry.details.tabs.recordDetails.minDesc")}
                                name="minDesc"
                                onChange={handleInputChange}
                                value={inputData.minDesc}
                                error={errors && errors.minDesc ? true : false}
                                helperText={errors && errors.minDesc ? errors.minDesc : null} />
                            <C1InputField
                                disabled={isDisabled}
                                label={locale("masters:ministry.details.tabs.recordDetails.minDescOth")}
                                name="minDescOth"
                                onChange={handleInputChange}
                                value={inputData.minDescOth}
                                error={errors && errors.minDescOth ? true : false}
                                helperText={errors && errors.minDescOth ? errors.minDescOth : null} />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default MinistryDetails;