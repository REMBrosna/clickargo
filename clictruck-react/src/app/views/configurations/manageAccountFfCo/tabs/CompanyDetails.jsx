import Grid from "@material-ui/core/Grid";
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import PhoneInTalkOutlinedIcon from '@material-ui/icons/PhoneInTalkOutlined';
import RoomOutlinedIcon from '@material-ui/icons/RoomOutlined';
import React from "react";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1SelectField from "app/c1component/C1SelectField";
import { useStyles } from "app/c1component/C1Styles";
import C1TabContainer from "app/c1component/C1TabContainer";
import { AccountTypes, MST_ACCN_TYPE_URL, MST_CTRY_URL } from "app/c1utils/const";
import { getValue, isEditable } from "app/c1utils/utility";
import { ACCOUNT_BY_TYPEID_URL } from "app/c1utils/const";

import useAuth from 'app/hooks/useAuth';

const AccountDetail = ({
    inputData,
    handleInputChange,
    viewType,
    errors,
    locale }) => {

    const classes = useStyles();
    let isDisabled = isEditable(viewType, false);

    const { user } = useAuth();

    let isSpAccn = (user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_SP.code);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={3} md={6} xs={12}>
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <C1CategoryBlock icon={<DescriptionIcon />} title={locale("companyDetails.generalDetails")}>
                            <Grid item xs={12} >
                                
                                {(isSpAccn) &&
                                    <C1SelectAutoCompleteField
                                        required
                                        name="ffAccnId"
                                        label={locale("admin:account.list.accnTypeFF")}
                                        value={getValue(inputData?.ffAccnId)}
                                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                                        isServer={true}
                                        disabled={('new'!== viewType)}
                                        options={{
                                            url: `${ACCOUNT_BY_TYPEID_URL}ACC_TYPE_FF`,
                                            key: "accn",
                                            id: 'accnId',
                                            desc: 'accnName',
                                            isCache: false
                                        }}
                                        error={errors?.ffAccnId ? true : false}
                                        helperText={errors?.ffAccnId ? errors.ffAccnId : null}
                                    />
                                }
                                <C1InputField
                                        label={locale("admin:account.list.accnTypeCO")}
                                    name="coreAccn.accnId"
                                    disabled
                                    required
                                    onChange={handleInputChange}
                                    value={getValue(inputData?.coreAccn?.accnId)}
                                    error={errors['coreAccn.accnId'] !== undefined}
                                    helperText={errors['coreAccn.accnId'] !== undefined
                                        ? locale(`validations.${errors['coreAccn.accnId']}`) : ''} />

                                <C1SelectAutoCompleteField
                                    label={locale("companyDetails.atypId")}
                                    name="coreAccn.TMstAccnType.atypId"
                                    disabled={true}
                                    required
                                    onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                                    value={getValue(inputData?.coreAccn?.TMstAccnType?.atypId) || ''}
                                    isServer={true}
                                    options={{
                                        url: MST_ACCN_TYPE_URL,
                                        id: 'atypId',
                                        desc: 'atypDescription',
                                        isCache: true
                                    }}
                                    error={errors['coreAccn.TMstAccnType.atypId'] !== undefined}
                                    helperText={errors['coreAccn.TMstAccnType.atypId'] !== undefined
                                        ? locale(`validations.${errors['coreAccn.TMstAccnType.atypId']}`) : ''} />

                                <C1InputField
                                    label={locale("companyDetails.accnName")}
                                    name="coreAccn.accnName"
                                    required
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    value={getValue(inputData?.coreAccn?.accnName)}
                                    error={errors['coreAccn.accnName'] || errors['coreAccn.accnNameDuplicate'] !== undefined ? true : false}
                                    helperText={errors['coreAccn.accnName'] !== undefined
                                        ? locale(`validations.${errors['coreAccn.accnName']}`)
                                        : errors['coreAccn.accnNameDuplicate'] !== undefined
                                            ? locale(`validations.${errors['coreAccn.accnNameDuplicate']}`) : null} />

                                <C1InputField
                                    label={locale("companyDetails.accnCoyRegn")}
                                    name="coreAccn.accnCoyRegn"
                                    required
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    value={getValue(inputData?.coreAccn?.accnCoyRegn)}
                                    inputProps={{ maxLength: 20 }}
                                    error={errors['coreAccn.accnCoyRegn'] !== undefined}
                                    helperText={errors['coreAccn.accnCoyRegn'] !== undefined
                                        ? locale(`validations.${errors['coreAccn.accnCoyRegn']}`) : ''} />
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                </Grid>

                <Grid item lg={3} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <C1CategoryBlock icon={<PhoneInTalkOutlinedIcon />} title={locale("companyDetails.contactDetails")}>
                            <Grid item xs={12} >
                                <C1InputField
                                    label={locale("companyDetails.contactTel")}
                                    name="coreAccn.accnContact.contactTel"
                                    required
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    value={getValue(inputData?.coreAccn?.accnContact?.contactTel) || ""}
                                    inputProps={{
                                        placeholder: locale("common:common.placeHolder.contactTel"),
                                        maxLength: 100
                                    }}
                                    error={errors['coreAccn.accnContact.contactTel'] || errors['coreAccn.accnContact.telInvalid'] !== undefined ? true : false}
                                    helperText={errors['coreAccn.accnContact.contactTel'] !== undefined
                                        ? locale(`validations.${errors['coreAccn.accnContact.contactTel']}`)
                                        : errors['coreAccn.accnContact.telInvalid'] !== undefined
                                            ? locale(`validations.${errors['coreAccn.accnContact.telInvalid']}`) : null} />

                                <C1InputField
                                    label={locale("companyDetails.contactFax")}
                                    name="coreAccn.accnContact.contactFax"
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    inputProps={{ maxLength: 25 }}
                                    value={getValue(inputData?.coreAccn?.accnContact?.contactFax) || ""} />

                                <C1InputField
                                    label={locale("companyDetails.contactEmail")}
                                    name="coreAccn.accnContact.contactEmail"
                                    required
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    inputProps={{ maxLength: 128 }}
                                    value={getValue(inputData?.coreAccn?.accnContact?.contactEmail)}
                                    error={errors['coreAccn.accnContact.contactEmail'] || errors['coreAccn.accnContact.emailInvalid'] !== undefined ? true : false}
                                    helperText={errors['coreAccn.accnContact.contactEmail'] !== undefined
                                        ? locale(`validations.${errors['coreAccn.accnContact.contactEmail']}`)
                                        : errors['coreAccn.accnContact.emailInvalid'] !== undefined
                                            ? locale(`validations.${errors['coreAccn.accnContact.emailInvalid']}`) : null} />
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                </Grid>

                <Grid item lg={6} md={6} xs={12}>
                    <Grid className={classes.gridContainer}>
                        <C1CategoryBlock icon={<RoomOutlinedIcon />} title={locale("companyDetails.address")}>
                            <Grid container alignItems="center" spacing={6}>
                                <Grid item style={{ 'paddingBottom': '0px' }} xs={12} md={12} lg={6}>
                                    <Grid container alignItems="center" spacing={6}>
                                        <Grid item xs={12}>
                                            <C1InputField
                                                label={locale("companyDetails.addrLn1")}
                                                name="coreAccn.accnAddr.addrLn1"
                                                required
                                                disabled={isDisabled}
                                                onChange={handleInputChange}
                                                value={getValue(inputData?.coreAccn?.accnAddr?.addrLn1) || ""}
                                                inputProps={{ maxLength: 70 }}
                                                error={errors['coreAccn.accnAddr.addrLn1'] !== undefined}
                                                helperText={errors['coreAccn.accnAddr.addrLn1'] !== undefined
                                                    ? locale(`validations.${errors['coreAccn.accnAddr.addrLn1']}`) : ''} />
                                        </Grid>
                                    </Grid>
                                </Grid>
                                <Grid item style={{ 'paddingBottom': '0px' }} xs={12} md={12} lg={6}>
                                    <Grid container alignItems="center" spacing={6}>
                                        <Grid item xs={12}>
                                            <C1InputField
                                                label={locale("companyDetails.addrProv")}
                                                name="coreAccn.accnAddr.addrProv"
                                                disabled={isDisabled}
                                                required
                                                onChange={handleInputChange}
                                                value={getValue(inputData?.coreAccn?.accnAddr?.addrProv) || ""}
                                                inputProps={{ maxLength: 15 }}
                                                error={errors['coreAccn.accnAddr.addrProv'] !== undefined}
                                                helperText={errors['coreAccn.accnAddr.addrProv'] !== undefined
                                                    ? locale(`validations.${errors['coreAccn.accnAddr.addrProv']}`) : ''} />
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>
                            <Grid item style={{ 'paddingTop': '0px', 'paddingBottom': '0px' }} xs={12} md={12} lg={12}>
                                <Grid container alignItems="center" spacing={6}>
                                    <Grid item xs={6}>
                                        <C1InputField
                                            label={locale("companyDetails.addrLn2")}
                                            name="coreAccn.accnAddr.addrLn2"
                                            disabled={isDisabled}
                                            required
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.coreAccn?.accnAddr?.addrLn2) || ""}
                                            inputProps={{ maxLength: 70 }}
                                            error={errors['coreAccn.accnAddr.addrLn2'] !== undefined}
                                            helperText={errors['coreAccn.accnAddr.addrLn2'] !== undefined
                                                ? locale(`validations.${errors['coreAccn.accnAddr.addrLn2']}`) : ''} />
                                    </Grid>
                                    <Grid item xs={6}>
                                        <C1InputField
                                            label={locale("companyDetails.addrCity")}
                                            name="coreAccn.accnAddr.addrCity"
                                            disabled={isDisabled}
                                            required
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.coreAccn?.accnAddr?.addrCity) || ""}
                                            error={errors['coreAccn.accnAddr.addrCity'] !== undefined}
                                            helperText={errors['coreAccn.accnAddr.addrCity'] !== undefined
                                                ? locale(`validations.${errors['coreAccn.accnAddr.addrCity']}`) : ''} />
                                    </Grid>
                                </Grid>
                            </Grid>
                            <Grid container alignItems="center" spacing={6}>
                                <Grid item style={{ 'paddingTop': '0px', 'paddingBottom': '0px' }} xs={12} md={12} lg={6}>
                                    <Grid container alignItems="center" spacing={3}>
                                        <Grid item xs={12}>
                                            <C1InputField
                                                label={locale("companyDetails.addrLn3")}
                                                name="coreAccn.accnAddr.addrLn3"
                                                disabled={isDisabled}
                                                onChange={handleInputChange}
                                                inputProps={{ maxLength: 70 }}
                                                value={getValue(inputData?.coreAccn?.accnAddr?.addrLn3) || ""} />
                                        </Grid>
                                    </Grid>
                                </Grid>
                                <Grid item style={{ 'paddingTop': '0px', 'paddingBottom': '0px' }} xs={12} md={12} lg={6}>
                                    <Grid container alignItems="center" spacing={6}>
                                        <Grid item xs={12}>
                                            <C1InputField
                                                label={locale("companyDetails.addrPcode")}
                                                name="coreAccn.accnAddr.addrPcode"
                                                disabled={isDisabled}
                                                required
                                                onChange={handleInputChange}
                                                value={getValue(inputData?.coreAccn?.accnAddr?.addrPcode) || ""}
                                                inputProps={{ maxLength: 17 }}
                                                error={errors['coreAccn.accnAddr.addrPcode'] !== undefined}
                                                helperText={errors['coreAccn.accnAddr.addrPcode'] !== undefined
                                                    ? locale(`validations.${errors['coreAccn.accnAddr.addrPcode']}`) : ''} />
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>
                            <Grid item style={{ 'paddingTop': '0px', 'paddingBottom': '0px' }} xs={12} md={12} lg={12}>
                                <Grid container alignItems="center" spacing={6}>
                                    <Grid item xs={6}>
                                    </Grid>
                                    <Grid item xs={6}>
                                        <C1SelectAutoCompleteField
                                            label={locale("companyDetails.ctyCode")}
                                            name="coreAccn.accnAddr.addrCtry.ctyCode"
                                            required
                                            disabled={isDisabled}
                                            onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                                            value={getValue(inputData?.coreAccn?.accnAddr?.addrCtry?.ctyCode) || ''}
                                            isServer={true}
                                            isShowCode={true}
                                            options={{
                                                url: MST_CTRY_URL,
                                                key: "country",
                                                id: 'ctyCode',
                                                desc: 'ctyDescription',
                                                isCache: true
                                            }}
                                            error={errors['coreAccn.accnAddr.addrCtry.ctyCode'] !== undefined}
                                            helperText={errors['coreAccn.accnAddr.addrCtry.ctyCode'] !== undefined
                                                ? locale(`validations.${errors['coreAccn.accnAddr.addrCtry.ctyCode']}`) : ''} />
                                    </Grid>
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                </Grid>
            </C1TabContainer >
        </React.Fragment >
    );
};

export default AccountDetail;