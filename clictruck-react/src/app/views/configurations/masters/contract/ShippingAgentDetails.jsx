import React from "react";
import Grid from "@material-ui/core/Grid";
import { isEditable } from "app/c1utils/utility";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";

const ShippingAgentDetails = ({ inputData, viewType, isSubmitting, locale }) => {

    let isDisabled = isEditable(viewType, isSubmitting);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3} alignItems="center">
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.companyName")}
                                name="contactTel"
                                value={inputData?.tcoreAccnByAconSagId?.accnName || ""}
                            />

                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.phoneNumber")}
                                name="contactTel"
                                value={inputData?.tcoreAccnByAconSagId?.accnContact?.contactTel || ""}
                                inputProps={{
                                    placeholder: locale("common:common.placeHolder.contactTel")
                                }} />
                            <C1InputField
                                disabled
                                type="email"
                                label={locale("masters:contract.details.tabs.recordDetails.email")}
                                name="contactEmail"
                                value={inputData?.tcoreAccnByAconSagId?.accnContact?.contactEmail || ""} />
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.fax")}
                                name="contactFax"
                                value={inputData?.tcoreAccnByAconSagId?.accnContact?.contactFax || ""}
                                inputProps={{
                                    placeholder: locale("common:common.placeHolder.contactFax")
                                }} />
                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.address1")}
                                name="addrLn1"
                                value={inputData?.tcoreAccnByAconSagId?.accnAddr?.addrLn1 || ""} />
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.address2")}
                                name="addrLn2"
                                value={inputData?.tcoreAccnByAconSagId?.accnAddr?.addrLn2 || ""} />
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.address3")}
                                name="addrLn3"
                                value={inputData?.tcoreAccnByAconSagId?.accnAddr?.addrLn3 || ""} />
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.postCode")}
                                name="addrPcode"
                                value={inputData?.tcoreAccnByAconSagId?.accnAddr?.addrPcode || ""} />
                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled
                                label={locale("masters:contract.details.tabs.recordDetails.cityCode")}
                                name="addrCity"
                                value={inputData?.tcoreAccnByAconSagId?.accnAddr?.addrCity || ""} />
                            <C1InputField
                                disabled={isDisabled}
                                label={locale("masters:contract.details.tabs.recordDetails.proCode")}
                                name="adrProv"
                                value={inputData?.tcoreAccnByAconSagId?.accnAddr?.addrProv || ""} />
                            <C1InputField
                                label=""
                                style={{ visibility: "hidden" }} />
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

export default ShippingAgentDetails;