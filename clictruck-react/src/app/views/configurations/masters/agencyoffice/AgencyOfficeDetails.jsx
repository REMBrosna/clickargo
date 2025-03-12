import React from "react";
import {
    Grid,
} from "@material-ui/core";

import { makeStyles } from '@material-ui/core/styles';

import { isEditable } from "app/c1utils/utility";
import C1InputField from "app/c1component/C1InputField"
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";

import { CCM_AGENCY_URL, MST_ADDRESS_TYPE_URL } from "app/c1utils/const";

const AgencyOfficeDetails = ({
    inputData,
    addrType,
    officeAddress,
    handleInputChange,
    handleAddrDetailChange,
    handleAddrTypeChange,
    viewType,
    isSubmitting,
    errors,
    locale }) => {

    const useStyles = makeStyles((theme) => ({
        gridPaddingTop6: {
            paddingTop: "1px !important",
        },
    }));

    const classes = useStyles();
    let isDisabled = isEditable(viewType, isSubmitting);


    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={6} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12}>
                            <C1SelectField
                                name="id.agoAgyCode"
                                required
                                label={locale("masters:agencyOffice.details.tabs.recordDetails.agoAgyCode")}
                                onChange={handleInputChange}
                                value={inputData.id.agoAgyCode && inputData.id.agoAgyCode ? inputData.id.agoAgyCode : ""}
                                disabled={isDisabled}
                                isServer={true}
                                options={{
                                    url: CCM_AGENCY_URL,
                                    id: 'agyCode',
                                    desc: 'agyDesc',
                                    isCache: false
                                }} />

                            <C1InputField
                                label={locale("masters:agencyOffice.details.tabs.recordDetails.agoCode")}
                                name="id.agoCode"
                                required
                                disabled={(viewType === 'edit' || viewType === 'view')}
                                onChange={handleInputChange}
                                value={inputData.id.agoCode}
                                error={errors && errors.agoCode ? true : false}
                                helperText={errors && errors.agoCode ? errors.agoCode : null} />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={6} md={6} xs={12} >
                    <Grid item xs={12}>
                        <C1InputField
                            required
                            disabled={isDisabled}
                            label={locale("masters:agencyOffice.details.tabs.recordDetails.agoDesc")}
                            name="agoDesc"
                            onChange={handleInputChange}
                            value={inputData.agoDesc}
                            error={errors && errors.agoDesc ? true : false}
                            helperText={errors && errors.agoDesc ? errors.agoDesc : null} />

                        <C1InputField
                            disabled={isDisabled}
                            label={locale("masters:agencyOffice.details.tabs.recordDetails.agoDescOth")}
                            name="agoDescOth"
                            onChange={handleInputChange}
                            value={inputData.agoDescOth}
                            error={errors && errors.agoDescOth ? true : false}
                            helperText={errors && errors.agoDescOth ? errors.agoDescOth : null} />
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid item xs={12}>
                        <C1SelectField
                            value={addrType}
                            required
                            name={addrType}
                            label={locale("masters:agencyOffice.details.tabs.recordDetails.addrType")}
                            onChange={handleAddrTypeChange}
                            disabled={isDisabled}
                            isServer={true}
                            options={{
                                url: MST_ADDRESS_TYPE_URL,
                                id: 'adtCode',
                                desc: 'adtDesc',
                                isCache: false
                            }} />

                        <C1InputField
                            disabled={isDisabled}
                            label={locale("masters:contract.list.table.headers.phoneNumber")}
                            name="adrTel"
                            onChange={handleAddrDetailChange}
                            value={officeAddress ? (officeAddress.TCoreAddr ? officeAddress.TCoreAddr.adrTel || '' : '') : ''}
                            error={errors && errors.adrTel ? true : false}
                            helperText={errors && errors.adrTel ? errors.adrTel : null}
                            inputProps={{
                                placeholder: locale("common:common.placeHolder.contactTel")
                            }} />
                        <C1InputField
                            disabled={isDisabled}
                            type="email"
                            label={locale("masters:contract.list.table.headers.email")}
                            name="adrEmail"
                            onChange={handleAddrDetailChange}
                            value={officeAddress ? (officeAddress.TCoreAddr ? officeAddress.TCoreAddr.adrEmail || '' : '') : ''}
                            error={errors && errors.adrEmail ? true : false}
                            helperText={errors && errors.adrEmail ? errors.adrEmail : null} />
                        <C1InputField
                            disabled={isDisabled}
                            label={locale("masters:contract.list.table.headers.fax")}
                            name="adrFax"
                            onChange={handleAddrDetailChange}
                            value={officeAddress ? (officeAddress.TCoreAddr ? officeAddress.TCoreAddr.adrFax || '' : '') : ''}
                            error={errors && errors.adrFax ? true : false}
                            helperText={errors && errors.adrFax ? errors.adrFax : null}
                            inputProps={{
                                placeholder: locale("common:common.placeHolder.contactFax")
                            }} />
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid item xs={12}>
                        <C1InputField
                            label=""
                            style={{
                                "visibility": "hidden"
                            }} />

                        <C1InputField
                            disabled={isDisabled}
                            label={locale("masters:contract.list.table.headers.address1")}
                            name="adrAddr1"
                            onChange={handleAddrDetailChange}
                            value={officeAddress ? (officeAddress.TCoreAddr ? officeAddress.TCoreAddr.adrAddr1 || '' : '') : ''}
                            error={errors && errors.adrAddr1 ? true : false}
                            helperText={errors && errors.adrAddr1 ? errors.adrAddr1 : null} />
                        <C1InputField
                            disabled={isDisabled}
                            label={locale("masters:contract.list.table.headers.address2")}
                            name="adrAddr2"
                            onChange={handleAddrDetailChange}
                            value={officeAddress ? (officeAddress.TCoreAddr ? officeAddress.TCoreAddr.adrAddr2 || '' : '') : ''}
                            error={errors && errors.adrAddr2 ? true : false}
                            helperText={errors && errors.adrAddr2 ? errors.adrAddr2 : null} />
                        <C1InputField
                            disabled={isDisabled}
                            label={locale("masters:contract.list.table.headers.address3")}
                            name="adrAddr3"
                            onChange={handleAddrDetailChange}
                            value={officeAddress ? (officeAddress.TCoreAddr ? officeAddress.TCoreAddr.adrAddr3 || '' : '') : ''}
                            error={errors && errors.adrAddr3 ? true : false}
                            helperText={errors && errors.adrAddr3 ? errors.adrAddr3 : null} />
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12}>
                    <Grid item xs={12}>
                        <C1InputField
                            label=""
                            style={{ visibility: "hidden" }} />

                        <C1InputField
                            disabled={isDisabled}
                            label={locale("masters:contract.list.table.headers.postCode")}
                            name="adrPcode"
                            onChange={handleAddrDetailChange}
                            value={officeAddress ? (officeAddress.TCoreAddr ? officeAddress.TCoreAddr.adrPcode || '' : '') : ''}
                            error={errors && errors.adrPcode ? true : false}
                            helperText={errors && errors.adrPcode ? errors.adrPcode : null} />

                        <C1InputField
                            disabled={isDisabled}
                            label={locale("masters:contract.list.table.headers.cityCode")}
                            name="adrCity"
                            onChange={handleAddrDetailChange}
                            value={officeAddress ? (officeAddress.TCoreAddr ? officeAddress.TCoreAddr.adrCity || '' : '') : ''}
                            error={errors && errors.adrCity ? true : false}
                            helperText={errors && errors.adrCity ? errors.adrCity : null} />
                        <C1InputField
                            disabled={isDisabled}
                            label={locale("masters:province.list.table.headers.province")}
                            name="adrProv"
                            onChange={handleAddrDetailChange}
                            value={officeAddress ? (officeAddress.TCoreAddr ? officeAddress.TCoreAddr.adrProv || '' : '') : ''}
                            error={errors && errors.adrProv ? true : false}
                            helperText={errors && errors.adrProv ? errors.adrProv : null} />
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default AgencyOfficeDetails;