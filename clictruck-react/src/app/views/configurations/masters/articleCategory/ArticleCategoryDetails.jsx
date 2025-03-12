import React from "react";
import Grid from "@material-ui/core/Grid";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import { isEditable } from "app/c1utils/utility";
import { useStyles } from "app/c1utils/styles";

const ArticleCategoryDetails = ({ inputData, handleInputChange, viewType, isSubmitting, errors, locale }) => {
    let isDisabled = isEditable(viewType, isSubmitting);
    const classes = useStyles();

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1InputField
                                label={locale("masters:articleCat.details.tabs.recordDetails.arcCode")}
                                name="arcCode"
                                required
                                disabled={viewType === "edit" || viewType === "view"}
                                onChange={handleInputChange}
                                value={inputData.arcCode}
                                error={!!(errors && errors.arcCode)}
                                helperText={errors && errors.arcCode ? errors.arcCode : null}
                                inputProps={{ maxLength: 35 }}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <C1InputField
                                required
                                disabled={isDisabled}
                                label={locale("masters:articleCat.details.tabs.recordDetails.arcName")}
                                name="arcName"
                                onChange={handleInputChange}
                                value={inputData.arcName}
                                error={!!(errors && errors.arcName)}
                                helperText={errors && errors.arcName ? errors.arcName : null}
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
                                label={locale("masters:articleCat.details.tabs.recordDetails.arcDesc")}
                                name="arcDesc"
                                onChange={handleInputChange}
                                value={inputData.arcDesc}
                                error={!!(errors && errors.arcDesc)}
                                helperText={errors && errors.arcDesc ? errors.arcDesc : null}
                            />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default ArticleCategoryDetails;
