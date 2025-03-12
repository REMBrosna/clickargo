import Grid from "@material-ui/core/Grid";
import React from "react";

import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1TabContainer from "app/c1component/C1TabContainer";
import { CCM_ACCOUNT_BY_TYPEID_URL, Status } from "app/c1utils/const";
import { isEditable } from "app/c1utils/utility";

const ContractDetails = ({ inputData, handleAutoCompleteSelect, handleDateChange, viewType, isSubmitting, locale, errors, isShippingAgent, isAdmin }) => {

    let isDisabled = isEditable(viewType, isSubmitting);


    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3} alignItems="center">
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            {/* CPEDI-156 */}
                            <C1SelectAutoCompleteField
                                value={inputData?.tcoreAccnByAconShlId?.accnId || ''}
                                required
                                name="accnId"
                                label={locale("masters:contract.details.tabs.recordDetails.companyName")}
                                onChange={handleAutoCompleteSelect}
                                disabled={isDisabled || inputData?.aconStatus === Status.SUB.code || inputData?.aconStatus === Status.APP.code}
                                isServer={true}
                                options={{
                                    url: CCM_ACCOUNT_BY_TYPEID_URL + "ACC_TYPE_SHIP_LINE",
                                    id: 'accnId',
                                    desc: 'accnName',
                                    isCache: false
                                }}
                                error={!(errors?.validation?.['tcoreAccnByAconShlId.accnId'] === undefined)}
                                helperText={errors?.validation?.['tcoreAccnByAconShlId.accnId'] || ''}
                            />

                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.phoneNumber")}
                                name="contactTel"
                                value={inputData?.tcoreAccnByAconShlId?.accnContact?.contactTel || ""}
                                inputProps={{
                                    placeholder: locale("common:common.placeHolder.contactTel")
                                }} />
                            <C1InputField
                                disabled
                                type="email"
                                label={locale("masters:contract.details.tabs.recordDetails.email")}
                                name="contactEmail"
                                value={inputData?.tcoreAccnByAconShlId?.accnContact?.contactEmail || ""} />
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.fax")}
                                name="contactFax"
                                value={inputData?.tcoreAccnByAconShlId?.accnContact?.contactFax || ""}
                                inputProps={{
                                    placeholder: locale("common:common.placeHolder.contactFax")
                                }} />
                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1DateField
                                required
                                disabled={isDisabled || (inputData?.aconStatus === Status.SUB.code && !isAdmin)}
                                type="date"
                                label={locale("masters:contract.details.tabs.recordDetails.accnExpiryDate")}
                                name="accnExpiryDate"
                                onChange={handleDateChange}
                                value={inputData?.accnExpiryDate}
                                error={errors?.["inputData.accnExpiryDate"] ?? false}
                                helperText={errors?.["inputData.accnExpiryDate"] ?? null}
                            />

                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.address1")}
                                name="addrLn1"
                                value={inputData?.tcoreAccnByAconShlId?.accnAddr?.addrLn1 || ""} />
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.address2")}
                                name="addrLn2"
                                value={inputData?.tcoreAccnByAconShlId?.accnAddr?.addrLn2 || ""} />
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.address3")}
                                name="addrLn3"
                                value={inputData?.tcoreAccnByAconShlId?.accnAddr?.addrLn3 || ""} />
                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.postCode")}
                                name="addrPcode"
                                value={inputData?.tcoreAccnByAconShlId?.accnAddr?.addrPcode || ""} />
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.cityCode")}
                                name="addrCity"
                                value={inputData?.tcoreAccnByAconShlId?.accnAddr?.addrCity || ""} />
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.proCode")}
                                name="adrProv"
                                value={inputData?.tcoreAccnByAconShlId?.accnAddr?.addrProv || ""} />
                            <C1InputField
                                label=""
                                style={{ visibility: "hidden" }} />
                        </Grid>
                    </Grid>

                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default ContractDetails;