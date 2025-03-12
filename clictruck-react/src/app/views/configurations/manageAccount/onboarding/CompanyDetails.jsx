import Grid from "@material-ui/core/Grid";
import React from "react";

import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import { useStyles } from "app/c1component/C1Styles";
import C1TabContainer from "app/c1component/C1TabContainer";
import { MST_ACCN_TYPE_URL, MST_CTRY_URL, RegistrationStatus } from "app/c1utils/const";
import { getValue } from "app/c1utils/utility";

const AccountDetail = ({
    inputData,
    handleInputChange,
    handleInputAccnIdChange,
    errors, locale }) => {

    const classes = useStyles();

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={3} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                label={locale("companyDetails.accnrCoIntial")}
                                name="accnrCoIntial"
                                disabled={inputData && inputData.accnrStatus !== RegistrationStatus.PENDING_APPROVAL.code}
                                required
                                inputProps={{
                                    maxLength: 5
                                }}
                                onChange={handleInputAccnIdChange}
                                value={inputData.accnrCoIntial || ""}
                                error={errors && errors.accnrCoIntial ? true : false}
                                helperText={errors && errors.accnrCoIntial ? errors && errors.accnrCoIntial : null} />

                            <C1SelectField
                                label={locale("companyDetails.atypId")}
                                name="accnDetails.TMstAccnType.atypId"
                                disabled={true}
                                required
                                onChange={handleInputChange}
                                value={getValue(inputData?.TMstAccnType?.atypId)}
                                isServer={true}
                                options={{
                                    url: MST_ACCN_TYPE_URL,
                                    key: "accnType",
                                    id: 'atypId',
                                    desc: 'atypDescription',
                                    isCache: true
                                }} />

                            <C1InputField
                                label={locale("companyDetails.accnName")}
                                name="accnrCompName"
                                disabled={true}
                                required
                                onChange={handleInputChange}
                                value={inputData.accnrCompName || ""} />

                            <C1InputField
                                label={locale("companyDetails.accnCoyRegn")}
                                name="accnrCompReg"
                                disabled={true}
                                required
                                onChange={handleInputChange}
                                value={inputData.accnrCompReg || ""} />

                        </Grid>
                    </Grid>
                </Grid>

                <Grid item lg={3} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                label={locale("companyDetails.contactTel")}
                                name="accnrTel"
                                disabled={true}
                                required
                                onChange={handleInputChange}
                                value={inputData.accnrTel || ""}
                                inputProps={{
                                    placeholder: locale("common:common.placeHolder.contactTel")
                                }} />

                            <C1InputField
                                label={locale("companyDetails.contactEmail")}
                                name="accnrEmail"
                                disabled={true}
                                required
                                onChange={handleInputChange}
                                value={inputData.accnrEmail || ""} />
                        </Grid>
                    </Grid>
                </Grid>

                <Grid item lg={3} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                label={locale("companyDetails.addrLn1")}
                                name="accnrAddressLine1"
                                disabled={true}
                                required
                                onChange={handleInputChange}
                                value={inputData.accnrAddressLine1 || ""} />

                            <C1InputField
                                label={locale("companyDetails.addrLn2")}
                                name="accnrAddressLine2"
                                disabled={true}
                                required={false}
                                onChange={handleInputChange}
                                value={inputData.accnrAddressLine2 || ""} />

                            <C1InputField
                                label={locale("companyDetails.addrLn3")}
                                name="accnrAddressLine3"
                                disabled={true}
                                required={false}
                                onChange={handleInputChange}
                                value={inputData.accnrAddressLine3 || ""} />
                        </Grid>
                    </Grid>
                </Grid>

                <Grid item lg={3} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                label={locale("companyDetails.addrProv")}
                                name="accnrProv"
                                disabled={true}
                                required
                                onChange={handleInputChange}
                                value={inputData.accnrProv || ""} />

                            <C1InputField
                                label={locale("companyDetails.addrCity")}
                                name="accnrCity"
                                disabled={true}
                                required
                                onChange={handleInputChange}
                                value={inputData.accnrCity || ""} />

                            <C1InputField
                                label={locale("companyDetails.addrPcode")}
                                name="accnrPcode"
                                disabled={true}
                                required
                                onChange={handleInputChange}
                                value={inputData.accnrPcode || ""} />

                            <C1SelectField
                                label={locale("companyDetails.ctyCode")}
                                name="TMstCountry.ctyCode"
                                required
                                disabled={true}
                                onChange={handleInputChange}
                                value={getValue(inputData?.TMstCountry?.ctyCode)}
                                isServer={true}
                                isShowCode={true}
                                options={{
                                    url: MST_CTRY_URL,
                                    key: "country",
                                    id: 'ctyCode',
                                    desc: 'ctyDescription',
                                    isCache: true
                                }} />

                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};
export default AccountDetail;