import React from "react";
import Grid from "@material-ui/core/Grid";

import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";

import { useStyles } from "app/c1utils/styles";
import { isEditable } from "app/c1utils/utility";
import { CCM_MINISTRY_URL } from "app/c1utils/const";

const AgencyDetails = ({
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
                                label={locale("masters:agency.details.tabs.recordDetails.agyCode")}
                                name="agyCode"
                                required
                                disabled={(viewType === 'edit' || viewType === 'view')}
                                onChange={handleInputChange}
                                value={inputData.agyCode}
                                error={errors && errors.agyCode ? true : false}
                                helperText={errors && errors.agyCode ? errors.agyCode : null} />
                            <C1InputField
                                required
                                disabled={isDisabled}
                                label={locale("masters:agency.details.tabs.recordDetails.agyRegNo")}
                                name="agyRegNo"
                                onChange={handleInputChange}
                                value={inputData.agyRegNo}
                                error={errors && errors.agyRegNo ? true : false}
                                helperText={errors && errors.agyRegNo ? errors.agyRegNo : null} />

                            <C1SelectField
                                name="TCoreMinistry.minCode"
                                required
                                label={locale("masters:agency.details.tabs.recordDetails.TCoreMinistry")}
                                onChange={handleInputChange}
                                value={inputData.TCoreMinistry && inputData.TCoreMinistry.minCode ? inputData.TCoreMinistry.minCode : ""}
                                disabled={isDisabled}
                                isServer={true}
                                options={{
                                    url: CCM_MINISTRY_URL,
                                    id: 'minCode',
                                    desc: 'minDesc',
                                    isCache: false
                                }} />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={6} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                required
                                disabled={isDisabled}
                                label={locale("masters:agency.details.tabs.recordDetails.agyDesc")}
                                name="agyDesc"
                                onChange={handleInputChange}
                                value={inputData.agyDesc}
                                error={errors && errors.agyDesc ? true : false}
                                helperText={errors && errors.agyDesc ? errors.agyDesc : null} />
                            <C1InputField
                                disabled={isDisabled}
                                label={locale("masters:agency.details.tabs.recordDetails.agyDescOth")}
                                name="agyDescOth"
                                onChange={handleInputChange}
                                value={inputData.agyDescOth}
                                error={errors && errors.agyDescOth ? true : false}
                                helperText={errors && errors.agyDescOth ? errors.agyDescOth : null} />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default AgencyDetails;