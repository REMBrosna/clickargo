import React from "react";
import Grid from "@material-ui/core/Grid";

import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1DateField from "../../../c1component/C1DateField";

const AuditDetail = ({
                         data,
                         viewType,
                         locale
                     }) => {
    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3} alignItems="center">
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled
                                label={locale("administration:audit.details.tabs.recordDetails.audtId")}
                                name="audtId"
                                value={data?.audtId || ""}
                            />
                            <C1InputField
                                disabled
                                label={locale("administration:audit.details.tabs.recordDetails.audtEvent")}
                                name="audtEvent"
                                value={data?.audtEvent || ""}/>
                            <C1DateField
                                disabled
                                // label={locale("administration:audit.details.tabs.recordDetails.audtTimestamp")}
                                label={locale("administration:audit.details.tabs.recordDetails.audtCreatedDate")}
                                name="audtTimestamp"
                                value={data?.audtTimestamp || ""}/>
                            <C1InputField
                                label=""
                                style={{
                                    "visibility": "hidden"
                                }}/>
                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled
                                label={locale("administration:audit.details.tabs.recordDetails.audtAccnid")}
                                name="audtAccnid"
                                value={data?.audtAccnid || ""}
                            />
                            <C1InputField
                                disabled
                                label={locale("administration:audit.details.tabs.recordDetails.audtUid")}
                                name="audtUid"
                                value={data?.audtUid || ""}/>
                            <C1InputField
                                disabled
                                label={locale("administration:audit.details.tabs.recordDetails.audtRemoteIp")}
                                name="audtRemoteIp"
                                value={data?.audtRemoteIp || ""}/>

                            <C1InputField
                                disabled
                                label={locale("administration:audit.details.tabs.recordDetails.audtUname")}
                                name="audtTimestamp"
                                value={data?.audtUname || ""}/>
                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled
                                label={locale("administration:audit.details.tabs.recordDetails.audtParam1")}
                                name="audtParam1"
                                value={data?.audtParam1 || ""}
                            />
                            <C1InputField
                                disabled
                                label={locale("administration:audit.details.tabs.recordDetails.audtParam2")}
                                name="audtParam2"
                                value={data?.audtParam2 || ""}/>
                            <C1InputField
                                disabled
                                label={locale("administration:audit.details.tabs.recordDetails.audtParam3")}
                                name="audtParam3"
                                value={data?.audtParam3 || ""}/>

                            <C1InputField
                                disabled
                                label={locale("administration:audit.details.tabs.recordDetails.audtRemarks")}
                                name="audtRemarks"
                                value={data?.audtRemarks || ""}/>

                        </Grid>
                    </Grid>
                </Grid>

            </C1TabContainer>
        </React.Fragment>
    );
};

export default AuditDetail;