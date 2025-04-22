import React from "react";
import Grid from "@material-ui/core/Grid";
import MenuItem from "@material-ui/core/MenuItem";
import { useStyles } from "app/c1utils/styles";
import C1InputField from "app/c1component/C1InputField";
import { MST_CTRY_URL } from "app/c1utils/const";
import { getValue } from "app/c1utils/utility";
import { AccountTypes } from "app/c1utils/const";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1SelectField from "app/c1component/C1SelectField";


const CompanyDetails = ({
    inputData,
    handleInputChange,
    handleAutoComplete,
    errors,
    isSubmitting,
    locale }) => {


    const classes = useStyles();

    const accountTypes = [
        { atypId: AccountTypes.ACC_TYPE_SHIP_LINE.code, atypDescription: AccountTypes.ACC_TYPE_SHIP_LINE.desc },
        { atypId: AccountTypes.ACC_TYPE_SHIP_AGENT.code, atypDescription: AccountTypes.ACC_TYPE_SHIP_AGENT.desc },
    ]

    return (
        <Grid container alignItems="flex-start" spacing={3} className={classes.gridContainer}>
            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1SelectField
                            label={locale("companyDetails.atypId")}
                            name="accnDetails.TMstAccnType.atypId"
                            disabled={isSubmitting}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.TMstAccnType?.atypId)}
                            isServer={false}
                            optionsMenuItemArr={
                                accountTypes.map((item, ind) => (
                                    <MenuItem value={item.atypId} key={ind}>{item.atypDescription}</MenuItem>
                                ))

                            }
                            error={errors.accnDetails && errors.accnDetails['TMstAccnType.atypId'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['TMstAccnType.atypId']) || ''} />

                        <C1InputField
                            label={locale("companyDetails.accnName")}
                            name="accnDetails.accnName"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 256
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnName)}
                            error={errors.accnDetails && errors.accnDetails['accnName'] !== undefined}
                            helperText={(errors.accnDetails && locale(errors.accnDetails['accnName'])) || ''} />

                        <C1InputField
                            label={locale("companyDetails.accnCoyRegn")}
                            name="accnDetails.accnCoyRegn"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 20
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnCoyRegn)}
                            error={errors.accnDetails && errors.accnDetails['accnCoyRegn'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnCoyRegn']) || ''} />
                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("companyDetails.contactTel")}
                            name="accnDetails.accnContact.contactTel"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 1024,
                                placeholder: locale("common:common.placeHolder.contactTel")
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnContact?.contactTel)}
                            error={errors.accnDetails && errors.accnDetails['accnContact.contactTel'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnContact.contactTel']) || ''} />

                        <C1InputField
                            label={locale("companyDetails.contactEmail")}
                            name="accnDetails.accnContact.contactEmail"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 128
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnContact?.contactEmail)}
                            error={errors.accnDetails && errors.accnDetails['accnContact.contactEmail'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnContact.contactEmail']) || ''} />
                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("companyDetails.addrLn1")}
                            name="accnDetails.accnAddr.addrLn1"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 64
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnAddr?.addrLn1)}
                            error={errors.accnDetails && errors.accnDetails['accnAddr.addrLn1'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnAddr.addrLn1']) || ''}
                        />
                        <C1InputField
                            label={locale("companyDetails.addrLn2")}
                            name="accnDetails.accnAddr.addrLn2"
                            disabled={isSubmitting}
                            required={false}
                            inputProps={{
                                maxLength: 64
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnAddr?.addrLn2)}
                            error={errors.accnDetails && errors.accnDetails['accnAddr.addrLn2'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnAddr.addrLn2']) || ''} />

                        <C1InputField
                            label={locale("companyDetails.addrLn3")}
                            name="accnDetails.accnAddr.addrLn3"
                            disabled={isSubmitting}
                            required={false}
                            inputProps={{
                                maxLength: 64
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnAddr?.addrLn3)}
                            error={errors.accnDetails && errors.accnDetails['accnAddr.addrLn3'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnAddr.addrLn3']) || ''} />
                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("companyDetails.addrProv")}
                            name="accnDetails.accnAddr.addrProv"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 15
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnAddr?.addrProv)}
                            error={errors.accnDetails && errors.accnDetails['accnAddr.addrProv'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnAddr.addrProv']) || ''} />

                        <C1InputField
                            label={locale("companyDetails.addrCity")}
                            name="accnDetails.accnAddr.addrCity"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 15
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnAddr?.addrCity)}
                            error={errors.accnDetails && errors.accnDetails['accnAddr.addrCity'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnAddr.addrCity']) || ''} />

                        <C1InputField
                            label={locale("companyDetails.addrPcode")}
                            name="accnDetails.accnAddr.addrPcode"
                            disabled={isSubmitting}
                            required
                            inputProps={{
                                maxLength: 10
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnAddr?.addrPcode)}
                            error={errors.accnDetails && errors.accnDetails['accnAddr.addrPcode'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnAddr.addrPcode']) || ''} />

                        <C1SelectAutoCompleteField
                            label={locale("companyDetails.ctyCode")}
                            name="accnDetails.accnAddr.addrCtry.ctyCode"
                            required
                            disabled={isSubmitting}
                            onChange={handleAutoComplete}
                            value={getValue(inputData?.accnDetails?.accnAddr?.addrCtry?.ctyCode)}
                            isServer={true}
                            isShowCode={true}
                            options={{
                                url: MST_CTRY_URL,
                                key: "country",
                                id: 'ctyCode',
                                desc: 'ctyDescription',
                                isCache: true
                            }}
                            error={errors.accnDetails && errors.accnDetails['accnAddr.addrCtry.ctyCode'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnAddr.addrCtry.ctyCode']) || ''} />
                    </Grid>
                </Grid>
            </Grid>
        </Grid>
    );
};
export default CompanyDetails;