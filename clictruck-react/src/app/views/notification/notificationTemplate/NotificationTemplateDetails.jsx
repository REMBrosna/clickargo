import React from "react";

import Grid from "@material-ui/core/Grid";

import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";

import {
    CAN_NOTIFICATION_CHANNEL_TYPE_URL,
    CAN_NOTIFICATION_CONTENT_TYPE_URL,
} from "app/c1utils/const";
import { isEditable } from "app/c1utils/utility";
import { useStyles } from "app/c1utils/styles";

const NotificationTemplateDetails = ({ inputData, handleInputChange, viewType, isSubmitting, errors, locale }) => {
    let isDisabled = isEditable(viewType, isSubmitting);
    const classes = useStyles();

    return (
        <React.Fragment>
            <C1TabContainer>
                {console.log("inputData:", inputData)}
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={6}>
                        <C1InputField
                            label={locale("masters:notificationTemplate.details.tabs.recordDetails.notifTemplateID")}
                            name="id.ntplId"
                            required
                            disabled={viewType === "edit" || viewType === "view" ? true : false}
                            onChange={handleInputChange}
                            value={inputData.id.ntplId}
                            error={errors && errors.ntplId ? true : false}
                            helperText={errors && errors.ntplId ? errors.ntplId : null}
                        />
                    </Grid>
                    <Grid item xs={6}>
                        <C1InputField
                            required
                            disabled={isDisabled}
                            type="number"
                            label={locale("masters:notificationTemplate.details.tabs.recordDetails.notifTemplateSeq")}
                            name="ntplSeq"
                            onChange={handleInputChange}
                            value={inputData.ntplSeq}
                            error={errors && errors.ntplSeq ? true : false}
                            helperText={errors && errors.ntplSeq ? errors.ntplSeq : null}
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1SelectField
                            label={locale(
                                "masters:notificationTemplate.details.tabs.recordDetails.notifTemplateChannel"
                            )}
                            name="TCoreNotificationChannelType.nchntypeId"
                            required
                            onChange={handleInputChange}
                            value={
                                inputData.TCoreNotificationChannelType
                                    ? inputData.TCoreNotificationChannelType.nchntypeId
                                    : ""
                            }
                            disabled={isDisabled}
                            isServer={true}
                            options={{
                                url: CAN_NOTIFICATION_CHANNEL_TYPE_URL,
                                key: "notificationChannel",
                                id: "nchntypeId",
                                desc: "nchntypeDesc",
                                isCache: true,
                            }}
                            error={errors && errors.TCoreNotificationChannelType ? true : false}
                            helperText={
                                errors && errors.TCoreNotificationChannelType
                                    ? errors.TCoreNotificationChannelType
                                    : null
                            }
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1SelectField
                            label={locale(
                                "masters:notificationTemplate.details.tabs.recordDetails.notifTemplateNotifContent"
                            )}
                            name="TCoreNotificationContentType.ncnttypeId"
                            required
                            onChange={handleInputChange}
                            value={
                                inputData.TCoreNotificationContentType
                                    ? inputData.TCoreNotificationContentType.ncnttypeId
                                    : ""
                            }
                            disabled={isDisabled}
                            isServer={true}
                            options={{
                                url: CAN_NOTIFICATION_CONTENT_TYPE_URL,
                                key: "notificationContent",
                                id: "ncnttypeId",
                                desc: "ncnttypeDesc",
                                isCache: true,
                            }}
                            error={errors && errors.TCoreNotificationContentType ? true : false}
                            helperText={
                                errors && errors.TCoreNotificationContentType
                                    ? errors.TCoreNotificationContentType
                                    : null
                            }
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale("masters:notificationTemplate.details.tabs.recordDetails.notifTemplateDesc")}
                            name="ntplDesc"
                            required
                            disabled={isDisabled}
                            onChange={handleInputChange}
                            value={inputData.ntplDesc}
                            error={errors && errors.ntplDesc ? true : false}
                            helperText={errors && errors.ntplDesc ? errors.ntplDesc : null}
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale("masters:notificationTemplate.details.tabs.recordDetails.notifTemplateSubj")}
                            name="ntplSubject"
                            required
                            disabled={isDisabled}
                            onChange={handleInputChange}
                            value={inputData.ntplSubject}
                            error={errors && errors.ntplSubject ? true : false}
                            helperText={errors && errors.ntplSubject ? errors.ntplSubject : null}
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale(
                                "masters:notificationTemplate.details.tabs.recordDetails.notifTemplateContent"
                            )}
                            name="ntplTempalte"
                            required
                            multiline
                            rows={10}
                            //rowsMax={8}
                            disabled={isDisabled}
                            onChange={handleInputChange}
                            value={inputData.ntplTempalte}
                            error={errors && errors.ntplTempalte ? true : false}
                            helperText={errors && errors.ntplTempalte ? errors.ntplTempalte : null}
                        />
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default NotificationTemplateDetails;
