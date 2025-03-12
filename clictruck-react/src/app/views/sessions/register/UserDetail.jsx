import Grid from "@material-ui/core/Grid";
import React from "react";

import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import { MST_CTRY_URL } from "app/c1utils/const";
import { useStyles } from "app/c1utils/styles";
import { getValue } from "app/c1utils/utility";

import Information from "../../../c1component/C1Information"

const UserDetail = ({
    inputData,
    handleInputChange,
    handleAutoComplete,
    errors,
    isSubmitting,
    locale }) => {

    const classes = useStyles();

    return (
        <Grid container alignItems="flex-start" spacing={3} className={classes.gridContainer}>
            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("userDetails.usrName")}
                            name="usrName"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 35
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrName)}
                            error={errors.coreUsr && errors.coreUsr['usrName'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrName']) || ''} />

                        <C1InputField
                            label={locale("userDetails.usrPassNid")}
                            name="usrPassNid"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 20
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrPassNid)}
                            error={errors.coreUsr && errors.coreUsr['usrPassNid'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrPassNid']) || ''} />

                        <C1InputField
                            label={locale("userDetails.usrTitle")}
                            name="usrTitle"
                            disabled={isSubmitting}
                            inputProps={{
                                maxLength: 35
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrTitle)}
                            error={errors.coreUsr && errors.coreUsr['usrTitle'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrTitle']) || ''} />
                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={"Contact Number"}
                            name="usrContact.contactTel"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 1024,
                                placeholder: locale("common:common.placeHolder.contactTel")
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrContact?.contactTel)}
                            error={errors.coreUsr && errors.coreUsr['usrContact.contactTel'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrContact.contactTel']) || ''} />

                        <C1InputField
                            label={"Email"}
                            name="usrContact.contactEmail"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 128
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrContact?.contactEmail)}
                            error={errors.coreUsr && errors.coreUsr['usrContact.contactEmail'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrContact.contactEmail']) || ''} />
                    </Grid>
                </Grid>
            </Grid>
            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label="Address Line 1"
                            name="usrAddr.addrLn1"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 64
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrAddr?.addrLn1)}
                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrLn1'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrLn1']) || ''} />
                        <C1InputField
                            label="Address Line 2"
                            name="usrAddr.addrLn2"
                            disabled={isSubmitting}
                            required={false}
                            inputProps={{
                                maxLength: 64
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrAddr?.addrLn2)}
                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrLn2'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrLn2']) || ''} />
                        <C1InputField
                            label="Address Line 3"
                            name="usrAddr.addrLn3"
                            disabled={isSubmitting}
                            required={false}
                            inputProps={{
                                maxLength: 64
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrAddr?.addrLn3)}
                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrLn3'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrLn3']) || ''} />
                    </Grid>
                </Grid>
            </Grid>
            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("userDetails.addrProv")}
                            name="usrAddr.addrProv"
                            disabled={isSubmitting}
                            inputProps={{
                                maxLength: 15
                            }}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrAddr?.addrProv)}
                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrProv'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrProv']) || ''} />

                        <C1InputField
                            label={locale("userDetails.addrCity")}
                            name="usrAddr.addrCity"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 15
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrAddr?.addrCity)}
                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrProv'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrProv']) || ''} />

                        <C1InputField
                            label={locale("userDetails.addrPcode")}
                            name="usrAddr.addrPcode"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 10
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrAddr?.addrPcode)}
                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrPcode'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrPcode']) || ''} />

                        <C1SelectAutoCompleteField
                            label={locale("companyDetails.ctyCode")}
                            name="usrAddr.addrCtry.ctyCode"
                            disabled={isSubmitting}
                            onChange={handleAutoComplete}
                            value={getValue(inputData?.usrAddr?.addrCtry?.ctyCode)}
                            isServer={true}
                            isShowCode={true}
                            options={{
                                url: MST_CTRY_URL,
                                key: "country",
                                id: 'ctyCode',
                                desc: 'ctyDescription',
                                isCache: true
                            }}
                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrCtry.ctyCode'] !== undefined}
                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrCtry.ctyCode']) || ''} />

                    </Grid>
                </Grid>
            </Grid>
            <Grid item lg={12} md={12} xs={12}>
                <Information information="userDetails" />
            </Grid>
        </Grid>
    );
};
export default UserDetail;