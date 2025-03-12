import React from "react";
import Grid from "@material-ui/core/Grid";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";

import { MST_PORT_KH } from "app/c1utils/const";
import { isEditable } from "app/c1utils/utility";
import { useStyles } from "app/c1utils/styles";

const PortTerminalDetails = ({ inputData, handleInputChange, viewType, isSubmitting, errors, locale }) => {
    let isDisabled = isEditable(viewType, isSubmitting);
    const classes = useStyles();

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1InputField
                                label={locale("masters:portTerminal.details.tabs.recordDetails.portTermId")}
                                name="portTeminalId"
                                required
                                disabled={viewType === "edit" || viewType === "view" ? true : false}
                                onChange={handleInputChange}
                                value={inputData.portTeminalId}
                                error={errors && errors.portTeminalId ? true : false}
                                helperText={errors && errors.portTeminalId ? errors.portTeminalId : null}
                                inputProps={{ maxLength: 35 }}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <C1InputField
                                required
                                disabled={isDisabled}
                                label={locale("masters:portTerminal.details.tabs.recordDetails.portTermName")}
                                name="portTeminalName"
                                onChange={handleInputChange}
                                value={inputData.portTeminalName}
                                error={errors && errors.portTeminalName ? true : false}
                                helperText={errors && errors.portTeminalName ? errors.portTeminalName : null}
                            />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1InputField
                                required
                                disabled={isDisabled}
                                label={locale("masters:portTerminal.details.tabs.recordDetails.portTermDesc")}
                                name="portTeminalDesc"
                                onChange={handleInputChange}
                                value={inputData.portTeminalDesc}
                                error={errors && errors.portTeminalDesc ? true : false}
                                helperText={errors && errors.portTeminalDesc ? errors.portTeminalDesc : null}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <C1SelectField
                                name="mstPort.portCode"
                                label={locale("masters:portTerminal.details.tabs.recordDetails.port")}
                                onChange={handleInputChange}
                                value={inputData.mstPort ? inputData.mstPort.portCode : ""}
                                disabled={isDisabled}
                                isServer={true}
                                isShowCode={true}
                                options={{
                                    url: MST_PORT_KH,
                                    key: "port",
                                    id: "portCode",
                                    desc: "portDescription",
                                    isCache: true,
                                }}
                                error={errors && errors.mstPort ? true : false}
                                helperText={errors && errors.mstPort ? errors.mstPort : null}
                            />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default PortTerminalDetails;
