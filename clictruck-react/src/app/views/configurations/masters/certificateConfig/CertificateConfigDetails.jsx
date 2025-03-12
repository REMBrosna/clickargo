import Grid from "@material-ui/core/Grid";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import { useStyles } from "app/c1utils/styles";
import { getValue, isEditable } from "app/c1utils/utility";
import React from "react";


const CertificateConfigDetails = (
    {
        inputData,
        viewType,
        isSubmitting,
        locale,
        errors,
        handleInputChange
    }) => {

    const classes = useStyles();
    let isDisabled = isEditable(viewType, isSubmitting);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                onChange={handleInputChange}
                                disabled={viewType === 'edit' || viewType === 'view'}
                                label={locale("masters:certificateConfig.list.table.headers.certsId")}
                                name="pediCertificateService.certsId"
                                value={getValue(inputData?.pediCertificateService?.certsId)} />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={viewType === 'edit' || viewType === 'view'}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certSubReportKey")}
                                name="certSubReportKey"
                                value={inputData.certSubReportKey} />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={viewType === 'edit' || viewType === 'view'}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certJrxmlPath")}
                                name="certJrxmlPath"
                                value={inputData.certJrxmlPath} />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={viewType === 'edit' || viewType === 'view'}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certTemplateType")}
                                name="certTemplateType"
                                value={inputData.certTemplateType} />

                            <C1InputField
                                onChange={handleInputChange}
                                // required
                                disabled={viewType === 'edit' || viewType === 'view'}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certLogoPath")}
                                name="certLogoPath"
                                value={inputData.certLogoPath} />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certReportTitle")}
                                name="certReportTitle"
                                value={inputData.certReportTitle}
                            />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certReportWebviewTitle")}
                                name="certReportWebviewTitle"
                                value={inputData.certReportWebviewTitle}
                            />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certDepartment")}
                                name="certDepartment"
                                value={inputData.certDepartment}
                            />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certInchargeTitle")}
                                name="certInchargeTitle"
                                value={inputData.certInchargeTitle}
                            />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certMinistry")}
                                name="certMinistry"
                                value={inputData.certMinistry}
                            />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certReportTitleKh")}
                                name="certReportTitleKh"
                                value={inputData.certReportTitleKh}
                            />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certReportWebviewTitleKh")}
                                name="certReportWebviewTitleKh"
                                value={inputData.certReportWebviewTitleKh}
                            />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certDepartmentKh")}
                                name="certDepartmentKh"
                                value={inputData.certDepartmentKh}
                            />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certInchargeTitleKh")}
                                name="certInchargeTitleKh"
                                value={inputData.certInchargeTitleKh}
                            />

                            <C1InputField
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                label={locale("masters:certificateConfig.details.tabs.recordDetails.certMinistryKh")}
                                name="certMinistryKh"
                                value={inputData.certMinistryKh}
                            />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default CertificateConfigDetails;