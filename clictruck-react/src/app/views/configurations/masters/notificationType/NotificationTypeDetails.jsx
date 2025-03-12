import { Grid, IconButton, InputLabel, Select } from "@material-ui/core";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import React from "react";
import { useTranslation } from "react-i18next";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import {useStyles} from "../../../../c1utils/styles";
import C1SelectField from "../../../../c1component/C1SelectField";
import {CCM_MINISTRY_URL} from "../../../../c1utils/const";

export default function NotificationTypeDetails({
      inputData,
      handleInputChange,
      translator,
      handleChangeMultiple,
      handleDateChange,
      viewType,
      isDisabled,
      errors,
      selectedAccnUsers,
      selectedAccnVehs,
      selectedUsers,
      selectedVehs,
      handleSelectedToAvail,
      handleAvailToSelected,
  }) {
    const classes = useStyles();
    return (
        <React.Fragment>
            <Grid item xs={12}>
                <C1TabContainer>
                    <Grid item lg={6} md={6} xs={12} >
                        <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                            <Grid item xs={12} >
                                {/*<C1InputField*/}
                                {/*    label={translator("masters:notificationType.details.tabs.recordDetails.altId")}*/}
                                {/*    name="altId"*/}
                                {/*    disabled*/}
                                {/*    onChange={handleInputChange}*/}
                                {/*    value={inputData?.altId || ""}*/}
                                {/*    error={errors.altId !== undefined}*/}
                                {/*    helperText={errors.altId || ""}*/}
                                {/*/>*/}
                                <C1InputField
                                    required
                                    label={translator("masters:notificationType.details.tabs.recordDetails.altName")}
                                    name="altName"
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    value={inputData?.altName || ""}
                                    error={errors.altName !== undefined}
                                    helperText={errors.altName || ""}
                                />
                                <C1InputField
                                    required
                                    label={translator("masters:notificationType.details.tabs.recordDetails.altModule")}
                                    name="altModule"
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    value={inputData?.altModule || ""}
                                    error={errors.altModule !== undefined}
                                    helperText={errors.altModule || ""}
                                />
                                <C1InputField
                                    required
                                    label={translator("masters:notificationType.details.tabs.recordDetails.altNotificationType")}
                                    name="altNotificationType"
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    value={inputData?.altNotificationType || ""}
                                    error={errors.altNotificationType !== undefined}
                                    helperText={errors.altNotificationType || ""}
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                    <Grid item lg={6} md={6} xs={12} >
                        <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                            <Grid item xs={12} >
                                <C1InputField
                                    required
                                    label={translator("masters:notificationType.details.tabs.recordDetails.altTemplateId")}
                                    name="altTemplateId"
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    value={inputData?.altTemplateId || ""}
                                    error={errors.altTemplateId !== undefined}
                                    helperText={errors.altTemplateId || ""}
                                />
                                <C1InputField
                                    label={translator("masters:notificationType.details.tabs.recordDetails.altConditionType")}
                                    name="altConditionType"
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    value={inputData?.altConditionType || ""}
                                    error={errors.altConditionType !== undefined}
                                    helperText={errors.altConditionType || ""}
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                </C1TabContainer>
            </Grid>
        </React.Fragment>
    );
}
