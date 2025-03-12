import Grid from "@material-ui/core/Grid";
import MenuItem from "@material-ui/core/MenuItem";
import React from "react";

import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1SelectField from "app/c1component/C1SelectField";
import { MST_ACCN_TYPE_URL, MST_CTRY_URL } from "app/c1utils/const";
import { AccountTypes } from "app/c1utils/const";
import { useStyles } from "app/c1utils/styles";
import { getValue } from "app/c1utils/utility";

import Information from "../../../c1component/C1Information";

const CompanyDetails = ({
    inputData,
    handleInputChange,
    handleAutoComplete,
    errors,
    isSubmitting,
    locale }) => {


    const classes = useStyles();

    const accountTypes = [
        { atypId: AccountTypes.CARGO_OWNER.code, atypDescription: AccountTypes.CARGO_OWNER.desc },
        { atypId: AccountTypes.FORWARDER.code, atypDescription: AccountTypes.FORWARDER.desc },
    ]

    return (
        <Grid container alignItems="flex-start" spacing={3} className={classes.gridContainer}>
            <Grid item lg={3} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1SelectAutoCompleteField
                            label={locale("companyDetails.atypId")}
                            name="accnDetails.TMstAccnType.atypId"
                            disabled={isSubmitting}
                            onChange={handleAutoComplete}
                            value={getValue(inputData?.accnDetails?.TMstAccnType?.atypId)}
                            isServer={true}
                            options={{
                                // url: MST_ACCN_TYPE_URL,
                                url: `/api/accountTypes/all`,
                                key: "accn",
                                id: 'atypId',
                                // desc: 'atypDesc',
                                desc: 'atypDescription',
                                isCache: true
                            }}
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
                            label={"Tax Registration No."}
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
                            label={"Company Phone"}
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
                            label={"Company Fax"}
                            name="accnDetails.accnContact.contactEmail"
                            disabled={isSubmitting}
                            inputProps={{
                                maxLength: 128
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnDetails?.accnContact?.contactEmail)}
                            error={errors.accnDetails && errors.accnDetails['accnContact.contactEmail'] !== undefined}
                            helperText={(errors.accnDetails && errors.accnDetails['accnContact.contactEmail']) || ''} />
                        <C1InputField
                            label={"Company Email"}
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
                            disabled={isSubmitting}
                            onChange={handleAutoComplete}
                            value={getValue(inputData?.accnDetails?.accnAddr?.addrCtry?.ctyCode)}
                            isServer={true}
                            isShowCode={true}
                            options={{
                                // url: MST_CTRY_URL,
                                url: `/api/country/all`,
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

            {/* <Grid item lg={12} md={12} xs={12}>
                <Grid container alignItems="flex-start" spacing={1} className={classes.gridContainer}>
                    <C1TextArea
                        label="Information"
                        name=" "
                        textLimit={512}
                        type="input"
                        disabled={true}
                        value={" * To view any of the Bill of Ladings, click on the EYE button. To reject any Bill of Ladings, clikc on the REJECT button."}
                        error={" "}
                        helperText={" "}
                        inputProps={{ maxLength: 512 }}
                    />
                </Grid>
            </Grid> */}
            <Grid item lg={12} md={12} xs={12}>
                <Information information="companyDetails" />
            </Grid>
        </Grid>
    );
};
export default CompanyDetails;