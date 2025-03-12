import Grid from "@material-ui/core/Grid";
import React from "react";

import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import { MST_CTRY_URL } from "app/c1utils/const";
import { useStyles } from "app/c1utils/styles";
import { getValue } from "app/c1utils/utility";

import Information from "app/c1component/C1Information"

const UserDetail = ({
    inputData,
    handleInputChange,
    handleAutoComplete,
    errors,
    viewType,
    locale }) => {

    const classes = useStyles();
    let isDisabled = true;
    if (viewType === 'new')
        isDisabled = false;

    return (
        <Grid container alignItems="flex-start" spacing={3} className={classes.gridContainer}>
            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("userDetails.usrName")}
                            name="coreUsr.usrName"
                            disabled={isDisabled}
                            required
                            inputProps={{
                                maxLength: 35
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrName)}
                            error={errors['coreUsr.usrName'] !== undefined}
                            helperText={errors['coreUsr.usrName'] !== undefined
                                ? locale(`validations.${errors['coreUsr.usrName']}`) : ''} />

                        <C1InputField
                            label={locale("userDetails.usrPassNid")}
                            name="coreUsr.usrPassNid"
                            disabled={isDisabled}
                            inputProps={{
                                maxLength: 20
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrPassNid)} />

                        <C1InputField
                            label={locale("userDetails.usrTitle")}
                            name="coreUsr.usrTitle"
                            disabled={isDisabled}
                            inputProps={{
                                maxLength: 35
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrTitle)} />
                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={"Contact Number"}
                            name="coreUsr.usrContact.contactTel"
                            disabled={isDisabled}
                            required
                            inputProps={{
                                maxLength: 1024,
                                placeholder: locale("common:common.placeHolder.contactTel")
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrContact?.contactTel)}
                            error={errors['coreUsr.usrContact.contactTel'] !== undefined}
                            helperText={errors['coreUsr.usrContact.contactTel'] !== undefined
                                ? locale(`validations.${errors['coreUsr.usrContact.contactTel']}`) : ''} />

                        <C1InputField
                            label={"Email"}
                            name="coreUsr.usrContact.contactEmail"
                            disabled={isDisabled}
                            required
                            inputProps={{
                                maxLength: 128
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrContact?.contactEmail)}
                            error={errors['coreUsr.usrContact.contactEmail'] !== undefined 
                                || errors['coreUsr.usrContact.emailInvalid'] !== undefined
                                || errors['coreUsr.usrContact.emailDuplicate'] !== undefined}
                            helperText={(errors['coreUsr.usrContact.contactEmail'] !== undefined
                            ? locale(`validations.${errors['coreUsr.usrContact.contactEmail']}`) : '')
                                + (errors['coreUsr.usrContact.emailInvalid'] !== undefined
                                     ? locale(`validations.${errors['coreUsr.usrContact.emailInvalid']}`) : '')
                                + (errors['coreUsr.usrContact.emailDuplicate'] !== undefined
                                    ? locale(`validations.${errors['coreUsr.usrContact.emailDuplicate']}`) : '')} />
                    </Grid>
                </Grid>
            </Grid>
            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label="Address Line 1"
                            name="coreUsr.usrAddr.addrLn1"
                            disabled={isDisabled}
                            required
                            inputProps={{
                                maxLength: 64
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrAddr?.addrLn1)}
                            error={errors['coreUsr.usrAddr.addrLn1'] !== undefined}
                            helperText={errors['coreUsr.usrAddr.addrLn1'] !== undefined
                                ? locale(`validations.${errors['coreUsr.usrAddr.addrLn1']}`) : ''} />
                        <C1InputField
                            label="Address Line 2"
                            name="coreUsr.usrAddr.addrLn2"
                            disabled={isDisabled}
                            required={false}
                            inputProps={{
                                maxLength: 64
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrAddr?.addrLn2)} />
                        <C1InputField
                            label="Address Line 3"
                            name="coreUsr.usrAddr.addrLn3"
                            disabled={isDisabled}
                            required={false}
                            inputProps={{
                                maxLength: 64
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrAddr?.addrLn3)}/>
                    </Grid>
                </Grid>
            </Grid>
            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("userDetails.addrProv")}
                            name="coreUsr.usrAddr.addrProv"
                            disabled={isDisabled}
                            inputProps={{
                                maxLength: 15
                            }}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrAddr?.addrProv)}
                            error={errors['coreUsr.usrAddr.addrProv'] !== undefined}
                            helperText={errors['coreUsr.usrAddr.addrProv'] !== undefined
                                ? locale(`validations.${errors['coreUsr.usrAddr.addrProv']}`) : ''} />

                        <C1InputField
                            label={locale("userDetails.addrCity")}
                            name="coreUsr.usrAddr.addrCity"
                            disabled={isDisabled}
                            required
                            inputProps={{
                                maxLength: 15
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrAddr?.addrCity)}
                            error={errors['coreUsr.usrAddr.addrCity'] !== undefined}
                            helperText={errors['coreUsr.usrAddr.addrCity'] !== undefined
                                ? locale(`validations.${errors['coreUsr.usrAddr.addrCity']}`) : ''} />

                        <C1InputField
                            label={locale("userDetails.addrPcode")}
                            name="coreUsr.usrAddr.addrPcode"
                            disabled={isDisabled}
                            required
                            inputProps={{
                                maxLength: 10
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.coreUsr?.usrAddr?.addrPcode)}
                            error={errors['coreUsr.usrAddr.addrPcode'] !== undefined}
                            helperText={errors['coreUsr.usrAddr.addrPcode'] !== undefined
                                ? locale(`validations.${errors['coreUsr.usrAddr.addrPcode']}`) : ''} />

                        <C1SelectAutoCompleteField
                            label={locale("companyDetails.ctyCode")}
                            name="coreUsr.usrAddr.addrCtry.ctyCode"
                            disabled={isDisabled}
                            onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                            value={getValue(inputData?.coreUsr?.usrAddr?.addrCtry?.ctyCode)}
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
        </Grid>
    );
};
export default UserDetail;