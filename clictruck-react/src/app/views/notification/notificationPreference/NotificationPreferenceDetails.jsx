import React from "react";

import { Grid, Tooltip, Box, Button } from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";

import { PEDI_NOTIF_PREF_APPTYPES_URL, PEDI_NOTIF_PREF_ACTIONS_BY_APPTYPES_URL } from "app/c1utils/const";
import { useStyles, titleTab } from "app/c1utils/styles";

const NotificationPreferenceDetails = ({
    inputData,
    handleInputChange,
    handleTemplateDataChange,
    fetchTemplateSettings,
    errors,
    locale,
}) => {
    const classes = useStyles();
    const titleClass = titleTab();

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid C1TabContainer xs={12} className={titleClass.root}>
                    {/* <Grid item xs={12} className={titleClass.root}> */}
                    {locale("configuration:notificationPreferences.details.tabs.subTitle1")}
                    {/* </Grid> */}
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={5} spacing={9}>
                        <C1SelectField
                            label={locale("configuration:notificationPreferences.details.tabs.recordDetails.appType")}
                            name="sypfAppType"
                            required
                            onChange={handleInputChange}
                            value={inputData?.sypfAppType ?? ""}
                            isServer={true}
                            options={{
                                url: PEDI_NOTIF_PREF_APPTYPES_URL,
                                key: "appType",
                                id: "appTypeId",
                                desc: "appTypeDesc",
                                isCache: false,
                            }}
                            error={errors && errors.sypfAppType ? true : false}
                            helperText={errors && errors.sypfAppType ? errors.sypfAppType : null}
                        />
                    </Grid>
                    <Grid item xs={5}>
                        <C1SelectField
                            label={locale("configuration:notificationPreferences.details.tabs.recordDetails.action")}
                            name="sypfAction"
                            required
                            onChange={handleInputChange}
                            value={inputData.sypfAction ?? ""}
                            isServer={true}
                            options={{
                                url: inputData?.sypfAppType
                                    ? `${PEDI_NOTIF_PREF_ACTIONS_BY_APPTYPES_URL}/${inputData.sypfAppType}`
                                    : null,
                                key: "actions",
                                id: "actnCode",
                                desc: "actnDesc",
                                isCache: false,
                            }}
                            error={errors && errors.sypfAction ? true : false}
                            helperText={errors && errors.sypfAction ? errors.sypfAction : null}
                        />
                    </Grid>
                    <Grid item xs={2}>
                        <Box mt={2}>
                            <Tooltip title={locale("buttons:add")} aria-label="add">
                                <Button
                                    color="primary"
                                    variant="contained"
                                    size="large"
                                    onClick={fetchTemplateSettings}
                                >
                                    <SearchIcon viewBox="1 -1 30 30"></SearchIcon>
                                    {locale(
                                        "configuration:notificationPreferences.details.tabs.recordDetails.selectTemplate"
                                    )}
                                </Button>
                            </Tooltip>
                        </Box>
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={1}>
                    <Grid item xs={12} className={titleClass.root}>
                        {locale("configuration:notificationPreferences.details.tabs.subTitle2")}
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale(
                                "configuration:notificationPreferences.details.tabs.recordDetails.emailNotifTemplSubject"
                            )}
                            name="emailTemplate.ntplSubject"
                            //disabled={!inputData?.emailTemplate?.ntplSubject && inputData.sypfEmailReq === "N"}
                            disabled={true}
                            required
                            onChange={handleTemplateDataChange}
                            value={inputData?.emailTemplate?.ntplSubject ?? ""}
                            // error={errors && errors.emailTemplate.ntplSubject ? true : false}
                            // helperText={
                            //     errors && errors.emailTemplate.ntplSubject ? errors.emailTemplate.ntplSubject : null
                            // }
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale(
                                "configuration:notificationPreferences.details.tabs.recordDetails.emailNotifTemplContent"
                            )}
                            name="emailTemplate.ntplTempalte"
                            required
                            multiline
                            rows={10}
                            //rowsMax={8}
                            disabled={!inputData?.emailTemplate?.ntplTempalte && inputData.sypfEmailReq === "N"}
                            onChange={handleTemplateDataChange}
                            value={inputData?.emailTemplate?.ntplTempalte ?? ""}
                            // error={errors && errors.emailTemplate.ntplTempalte ? true : false}
                            // helperText={
                            //     errors && errors.emailTemplate.ntplTempalte ? errors.emailTemplate.ntplTempalte : null
                            // }
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale(
                                "configuration:notificationPreferences.details.tabs.recordDetails.smsNotifTemplSubject"
                            )}
                            name="smsTemplate.ntplSubject"
                            required
                            //disabled={!inputData?.smsTemplate?.ntplSubject && inputData.sypfSmsReq === "N"}
                            disabled={true}
                            onChange={handleTemplateDataChange}
                            value={inputData?.smsTemplate?.ntplSubject ?? ""}
                            // error={errors && errors.smsTemplate.ntplSubject ? true : false}
                            // helperText={
                            //     errors && errors.smsTemplate.ntplSubject ? errors.smsTemplate.ntplSubject : null
                            // }
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale(
                                "configuration:notificationPreferences.details.tabs.recordDetails.smsNotifTemplContent"
                            )}
                            name="smsTemplate.ntplTempalte"
                            required
                            multiline
                            rows={10}
                            //rowsMax={8}
                            disabled={!inputData?.smsTemplate?.ntplTempalte && inputData.sypfSmsReq === "N"}
                            onChange={handleTemplateDataChange}
                            value={inputData?.smsTemplate?.ntplTempalte ?? ""}
                            // error={errors && errors.smsTemplate.ntplTempalte ? true : false}
                            // helperText={
                            //     errors && errors.smsTemplate.ntplTempalte ? errors.smsTemplate.ntplTempalte : null
                            // }
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale(
                                "configuration:notificationPreferences.details.tabs.recordDetails.telegramNotifTemplSubject"
                            )}
                            name="telegramTemplate.ntplSubject"
                            required
                            //disabled={!inputData?.telegramTemplate?.ntplSubject && inputData.sypfTlgmReq === "N"}
                            disabled={true}
                            onChange={handleTemplateDataChange}
                            value={inputData?.telegramTemplate?.ntplSubject ?? ""}
                            // error={errors && errors.telegramTemplate.ntplSubject ? true : false}
                            // helperText={
                            //     errors && errors.telegramTemplate.ntpl   Subject
                            //         ? errors.telegramTemplate.ntplSubject
                            //         : null
                            // }
                        />
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={9} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale(
                                "configuration:notificationPreferences.details.tabs.recordDetails.telegramNotifTemplContent"
                            )}
                            name="telegramTemplate.ntplTempalte"
                            required
                            multiline
                            rows={10}
                            disabled={(!inputData?.telegramTemplate?.ntplTempalte || inputData?.telegramTemplate?.ntplTempalte === "") && inputData.sypfTlgmReq === "N"}
                            onChange={handleTemplateDataChange}
                            value={inputData?.telegramTemplate?.ntplTempalte ?? ""}
                            // error={errors && errors.telegramTemplate.ntplTempalte ? true : false}
                            // helperText={
                            //     errors && errors.telegramTemplate.ntplTempalte
                            //         ? errors.telegramTemplate.ntplTempalte
                            //         : null
                            // }
                        />
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default NotificationPreferenceDetails;
