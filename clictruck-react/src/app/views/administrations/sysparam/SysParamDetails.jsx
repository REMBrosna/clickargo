import React from "react";
import Grid from "@material-ui/core/Grid";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";

import { isEditable } from "app/c1utils/utility";
import { useStyles } from "app/c1utils/styles";
import C1TextArea from "app/c1component/C1TextArea";

const SysParamDetails = ({ inputData, handleInputChange, viewType, isSubmitting, errors, locale }) => {
    let isDisabled = isEditable(viewType, isSubmitting);
    const classes = useStyles();

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale("administration:sysParam.details.tabs.recordDetails.sysKey")}
                            name="sysKey"
                            required
                            disabled
                            onChange={handleInputChange}
                            value={inputData.sysKey}
                        // error={errors && errors.sysKey ? true : false}
                        // helperText={errors && errors.sysKey ? errors.ntplDesc : null}
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1TextArea
                            label={locale("administration:sysParam.details.tabs.recordDetails.sysVal")}
                            name="sysVal"
                            textLimit={1024}
                            required
                            multiline
                            disabled={isDisabled}
                            onChange={handleInputChange}
                            value={inputData.sysVal}
                            error={errors && errors.sysVal ? true : false}
                            helperText={errors && errors.sysVal ? errors.sysVal : null}
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1TextArea
                            label={locale("administration:sysParam.details.tabs.recordDetails.sysDesc")}
                            name="sysDesc"
                            required
                            textLimit={1024}
                            multiline
                            disabled={isDisabled}
                            onChange={handleInputChange}
                            value={inputData.sysDesc}
                            error={errors && errors.sysDesc ? true : false}
                            helperText={errors && errors.sysDesc ? errors.sysDesc : null}
                        />
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default SysParamDetails;
