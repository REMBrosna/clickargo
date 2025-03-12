import { Box, Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Divider, Grid, MenuItem, Paper, Tooltip } from "@material-ui/core";
import moment from "moment";
import React from "react";

import C1DateField from "app/c1component/C1DateField";
import C1DateTimeField from "app/c1component/C1DateTimeField";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TextArea from "app/c1component/C1TextArea";
import { AccountTypes } from "app/c1utils/const";
import useAuth from "app/hooks/useAuth";

const DoQueryPopUp = ({
    inputData,
    handleDateChange,
    handlePopupInputChange,
    isDisabled,
    errors,
    locale
}) => {

    const { user } = useAuth();

    const isShippingLine = user.coreAccn.TMstAccnType.atypId = AccountTypes.ACC_TYPE_SL.code;

    return (<React.Fragment>
        <div>
            <Grid container spacing={3} alignItems="center">
                <Grid container item lg={12} md={12} xs={12}>
                    <Grid item xs={12}>
                        <Grid container item spacing={1} alignItems="center">
                            <Grid item xs={6}>
                                <C1InputField
                                    label={locale("common:queries.fields.requester")}
                                    name="tcoreUsrByQryRequester.usrName"
                                    disabled={isDisabled || isShippingLine}
                                    value={inputData?.tcoreUsrByQryRequester?.usrName || ''}
                                    onChange={handlePopupInputChange}
                                />
                            </Grid>
                            <Grid item xs={6}>
                                <C1InputField
                                    label={locale("common:queries.fields.dtQuery")}
                                    name="date time"
                                    disabled={isDisabled || isShippingLine}
                                    value={inputData?.qryDtQuery ? moment(inputData?.qryDtQuery).format("DD/MM/YYYY hh:mm:ss") : ""}
                                    onChange={handleDateChange}
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                    <C1TextArea
                        label={locale("common:queries.fields.query")}
                        name="qryQuery"
                        disabled={isDisabled}
                        value={inputData?.qryQuery || ''}
                        textLimit={1024}
                        onChange={handlePopupInputChange}
                        error={errors?.qryQuery ? true : false}
                        helperText={errors?.qryQuery}
                    />
                    <Grid item xs={12}>
                        <Grid container item spacing={1} alignItems="center">
                            <Grid item xs={6}>
                                <C1InputField
                                    label={locale("common:queries.fields.responder")}
                                    name="tcoreUsrByQryResponder.usrName"
                                    disabled={isDisabled || isShippingLine}
                                    value={inputData?.tcoreUsrByQryResponder?.usrName || ''}
                                    onChange={handlePopupInputChange}
                                />
                            </Grid>
                            <Grid item xs={6}>
                                <C1InputField
                                    label={locale("common:queries.fields.dtResponse")}
                                    name="date time"
                                    disabled={isDisabled || isShippingLine}
                                    value={inputData?.qryDtResponse ? moment(inputData?.qryDtResponse).format("DD/MM/YYYY hh:mm:ss") : ""}
                                    onChange={handleDateChange}
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                    <C1TextArea
                        label={locale("common:queries.fields.response")}
                        name="qryResponse"
                        disabled={isDisabled || isShippingLine}
                        value={inputData?.qryResponse || ''}
                        textLimit={1024}
                        onChange={handlePopupInputChange}
                    />
                </Grid>
            </Grid>
        </div>
    </React.Fragment>
    );
};

export default DoQueryPopUp;