import React from "react";
import Grid from "@material-ui/core/Grid";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";

import { MST_CTRY_URL } from "app/c1utils/const";
import { isEditable } from "app/c1utils/utility";
import { useStyles } from "app/c1utils/styles";

const ProvinceDetails = ({ inputData, handleInputChange, viewType, isSubmitting, errors, locale }) => {
    let isDisabled = isEditable(viewType, isSubmitting);
    const classes = useStyles();

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1InputField
                                label={locale("masters:province.details.tabs.recordDetails.provId")}
                                name="provinceId"
                                required
                                disabled={viewType === "edit" || viewType === "view" ? true : false}
                                onChange={handleInputChange}
                                value={inputData.provinceId}
                                error={errors && errors.provinceId ? true : false}
                                helperText={errors && errors.provinceId ? errors.provinceId : null}
                                inputProps={{ maxLength: 35 }}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <C1InputField
                                required
                                disabled={isDisabled}
                                label={locale("masters:province.details.tabs.recordDetails.provDesc")}
                                name="provinceDescription"
                                onChange={handleInputChange}
                                value={inputData.provinceDescription}
                                error={errors && errors.provinceDescription ? true : false}
                                helperText={errors && errors.provinceDescription ? errors.provinceDescription : null}
                            />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1SelectField
                                name="mstCountry.ctyCode"
                                label={locale("masters:province.details.tabs.recordDetails.country")}
                                onChange={handleInputChange}
                                value={inputData.mstCountry ? inputData.mstCountry.ctyCode : ""}
                                disabled={isDisabled}
                                isServer={true}
                                isShowCode={true}
                                options={{
                                    url: MST_CTRY_URL,
                                    key: "country",
                                    id: "ctyCode",
                                    desc: "ctyDescription",
                                    isCache: true,
                                }}
                                error={errors && errors.mstCountry ? true : false}
                                helperText={errors && errors.mstCountry ? errors.mstCountry : null}
                            />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default ProvinceDetails;
