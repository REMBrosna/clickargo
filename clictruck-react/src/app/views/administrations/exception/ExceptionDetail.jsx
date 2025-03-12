import React from "react";
import Grid from "@material-ui/core/Grid";

import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1DateField from "../../../c1component/C1DateField";

const ExceptionDetail = (
    {
        data,
        locale,
        isLoading,
        viewType,
    }) => {

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3} alignItems="center">
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled
                                label={locale("exception.details.tabs.recordDetails.expToken")}
                                name="expToken"
                                value={data?.expToken || ""}
                            />
                            <C1InputField
                                disabled
                                label={locale("exception.details.tabs.recordDetails.expCode")}
                                name="expCode"
                                value={data?.expCode || ""}/>
                            <C1InputField
                                disabled
                                label={locale("exception.details.tabs.recordDetails.expUid")}
                                name="expUid"
                                value={data?.expUid || ""}/>
                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled
                                label={locale("exception.details.tabs.recordDetails.expLevel")}
                                name="expLevel"
                                value={data?.expLevel || ""}
                            />
                            <C1InputField
                                disabled
                                label={locale("exception.details.tabs.recordDetails.expSvrAddr")}
                                name="expSvrAddr"
                                value={data?.expSvrAddr || ""}/>
                            <C1InputField
                                disabled
                                label={locale("exception.details.tabs.recordDetails.expApp")}
                                name="expApp"
                                value={data?.audtRemoteIp || ""}/>

                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>

                            <C1DateField
                                disabled
                                // label={locale("exception.details.tabs.recordDetails.expTimestamp")}
                                label={locale("exception.details.tabs.recordDetails.expCreatedDate")}
                                name="expTimestamp"
                                value={data?.expTimestamp || ""}/>

                            <C1InputField
                                disabled
                                label={locale("exception.details.tabs.recordDetails.expAddinfo")}
                                name="expAddinfo"
                                value={data?.expAddinfo || ""}/>
                            <C1InputField
                                label=""
                                style={{
                                    "visibility": "hidden"
                                }}/>
                        </Grid>
                    </Grid>
                    <Grid item lg={12} md={12} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                disabled
                                label={locale("exception.details.tabs.recordDetails.expDetails")}
                                name="expDetails"
                                value={data?.expDetails || ""}
                                rowsMax={7}
                                multiline={true}
                            />
                        </Grid>
                    </Grid>
                </Grid>

            </C1TabContainer>
        </React.Fragment>
    );
};

export default ExceptionDetail;