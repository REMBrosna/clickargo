import Grid from "@material-ui/core/Grid";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import { isEditable } from "app/c1utils/utility";
import React from "react";


const CertificateSvcDetails = (
    {
        inputData,
        viewType,
        isSubmitting,
        locale,
        errors,
        handleInputChange
    }) => {

    let isDisabled = isEditable(viewType, isSubmitting);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3} alignItems="center">
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                onChange={handleInputChange}
                                required
                                disabled={viewType === 'edit' || viewType === 'view'}
                                label={locale("masters:certificateSvc.list.table.headers.certsId")}
                                name="certsId"
                                value={inputData.certsId}
                            />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateSvc.list.table.headers.certsVal")}
                                name="certsVal"
                                value={inputData.certsVal}
                            />

                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                onChange={handleInputChange}
                                required
                                disabled={isDisabled}
                                label={locale("masters:certificateSvc.list.table.headers.certsModuleService")}
                                name="certsModuleService"
                                value={inputData.certsModuleService}
                            />
                            <C1InputField
                                label=""
                                style={{
                                    "visibility": "hidden"
                                }} />

                        </Grid>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                onChange={handleInputChange}
                                required
                                disabled={isDisabled}
                                label={locale("masters:certificateSvc.list.table.headers.certsDecs")}
                                name="certsDecs"
                                value={inputData.certsDecs}
                            />
                            <C1InputField
                                label=""
                                style={{
                                    "visibility": "hidden"
                                }} />
                        </Grid>
                    </Grid>

                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default CertificateSvcDetails;