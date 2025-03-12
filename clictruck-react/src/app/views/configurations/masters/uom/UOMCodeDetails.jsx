import React from "react";
import Grid from "@material-ui/core/Grid";

import C1TabContainer from "app/c1component/C1TabContainer";
import C1InputField from "app/c1component/C1InputField";

import { isEditable } from "app/c1utils/utility";
import { useStyles } from "app/c1utils/styles";

const UOMCodeDetails = ({
    inputData,
    handleInputChange,
    viewType,
    isSubmitting,
    errors,
    locale }) => {

    let isDisabled = isEditable(viewType, isSubmitting);
    const classes = useStyles();

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1InputField
                                label={locale("masters:uom.details.tabs.recordDetails.uomCode")}
                                name="uomCode"
                                required
                                disabled={(viewType === 'edit' || viewType === 'view') ? true : false}
                                onChange={handleInputChange}
                                value={inputData.uomCode}
                                error={errors.uomCode ? true : false}
                                helperText={errors.uomCode ? errors.uomCode : null}
                                inputProps={{ maxLength: 3 }} />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1InputField
                                required
                                disabled={isDisabled}
                                label={locale("masters:uom.details.tabs.recordDetails.uomDescription")}
                                name="uomDescription"
                                onChange={handleInputChange}
                                value={inputData.uomDescription}
                                error={errors.uomDescription ? true : false}
                                helperText={errors.uomDescription ? errors.uomDescription : null}
                                inputProps={{ maxLength: 255 }}  />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled={isDisabled}
                                label={locale("masters:uom.details.tabs.recordDetails.otherLangDesc")}
                                name="uomDescriptionOth"
                                onChange={handleInputChange}
                                value={inputData.uomDescriptionOth}
                                error={errors.uomDescriptionOth ? true : false}
                                helperText={errors.uomDescriptionOth ? errors.uomDescriptionOth : null}
                                inputProps={{ maxLength: 512 }}  />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default UOMCodeDetails;