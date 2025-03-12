import React from "react";
import Grid from "@material-ui/core/Grid";

import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";
import C1InputField from "app/c1component/C1InputField";

import { isEditable } from "app/c1utils/utility";
import { useStyles } from "app/c1utils/styles";
import { MST_CTRY_URL, MST_PORT_TYPE_URL } from "app/c1utils/const";

const PortDetails = ({
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
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                label={locale("masters:ports.details.tabs.recordDetails.portCode")}
                                name="portCode"
                                required
                                disabled={(viewType === 'edit' || viewType === 'view') ? true : false}
                                onChange={handleInputChange}
                                value={inputData.portCode}
                                error={errors && errors.portCode ? true : false}
                                helperText={errors && errors.portCode ? errors.portCode : null}
                                inputProps={{ maxLength: 5 }}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <C1InputField
                                required
                                disabled={isDisabled}
                                label={locale("masters:ports.details.tabs.recordDetails.portDescription")}
                                name="portDescription"
                                onChange={handleInputChange}
                                value={inputData.portDescription}
                                error={errors && errors.portDescription ? true : false}
                                helperText={errors && errors.portDescription ? errors.portDescription : null} />
                        </Grid>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled={isDisabled}
                                label={locale("masters:ports.details.tabs.recordDetails.portDescriptionOth")}
                                name="portDescriptionOth"
                                onChange={handleInputChange}
                                value={inputData.portDescriptionOth}
                                error={errors && errors.portDescriptionOth ? true : false}
                                helperText={errors && errors.portDescriptionOth ? errors.portDescriptionOth : null} />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1SelectField
                                name="TMstCountry.ctyCode"
                                label={locale("masters:ports.details.tabs.recordDetails.TMstCountry")}
                                onChange={handleInputChange}
                                value={inputData.TMstCountry ? inputData.TMstCountry.ctyCode : ""}
                                disabled={isDisabled}
                                isServer={true}
                                isShowCode={true}
                                options={{
                                    url: MST_CTRY_URL,
                                    key: "country",
                                    id: 'ctyCode',
                                    desc: 'ctyDescription',
                                    isCache: true
                                }}
                                error={(errors && errors.TMstCountry ? true : false)}
                                helperText={(errors && errors.TMstCountry ? errors.TMstCountry : null)} />
                        </Grid>

                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1SelectField
                                name="TMstPortType.porttCode"
                                label={locale("masters:ports.details.tabs.recordDetails.TMstPortType")}
                                onChange={handleInputChange}
                                value={inputData.TMstPortType ? inputData.TMstPortType.porttCode : ""}
                                disabled={isDisabled}
                                isServer={true}
                                options={{
                                    url: MST_PORT_TYPE_URL,
                                    key: "portType",
                                    id: 'porttCode',
                                    desc: 'porttDescription',
                                    isCache: true
                                }}
                                error={(errors && errors.TMstPortType ? true : false)}
                                helperText={(errors && errors.TMstPortType ? errors.TMstPortType : null)} />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default PortDetails;