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

const DoRemarkPopUp = ({
    inputData,
    handleDateChange,
    handlePopupInputChange,
    isDisabled,
    errors,
    locale,
    view
}) => {

    const { user } = useAuth();

    const isShippingLine = user.coreAccn.TMstAccnType.atypId = AccountTypes.ACC_TYPE_SL.code;

    return (<React.Fragment>
        <div>
            <Grid container spacing={3} alignItems="center">
                <Grid container item lg={12} md={12} xs={12}>
                    <Grid item xs={12}>
                        {/* <Grid container item spacing={1} alignItems="center">
                            <Grid item xs={6}>
                                <C1InputField
                                    label={locale("common:remarks.fields.userName")}
                                    name="tcoreUsrByQryRequester.usrName"
                                    disabled={isDisabled || isShippingLine}
                                    value={inputData?.tcoreUsrByQryRequester?.usrName || ''}
                                    onChange={handlePopupInputChange}
                                />
                            </Grid>
                            <Grid item xs={6}>
                                <C1InputField
                                    label={locale("common:remarks.fields.dateTime")}
                                    name="date time"
                                    disabled={isDisabled || isShippingLine}
                                    value={inputData?.qryDtQuery ? moment(inputData?.qryDtQuery).format("DD/MM/YYYY hh:mm:ss") : ""}
                                    onChange={handleDateChange}
                                />
                            </Grid>
                        </Grid> */}
                    </Grid>
                    <C1TextArea
                        label={locale("common:remarks.fields.remark")}
                        name="attRemarksVerifier"
                        disabled={isDisabled}
                        value={inputData?.attRemarksVerifier || ''}
                        textLimit={2048}
                        onChange={handlePopupInputChange}
                        error={errors?.attRemarksVerifier ? true : false}
                        helperText={errors?.attRemarksVerifier}
                    />
                </Grid>
            </Grid>
        </div>
    </React.Fragment>
    );
};

export default DoRemarkPopUp;