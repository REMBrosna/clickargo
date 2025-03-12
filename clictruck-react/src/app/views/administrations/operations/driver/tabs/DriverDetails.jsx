import { Button, FormControl, Grid, IconButton, InputAdornment, InputLabel, OutlinedInput } from "@material-ui/core";
import { GetAppOutlined, Visibility, VisibilityOff } from "@material-ui/icons";
import BorderColorOutlinedIcon from '@material-ui/icons/BorderColorOutlined';
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
// import EditOutlinedIcon from '@material-ui/icons/EditOutlined';
import LockOutlined from '@material-ui/icons/LockOutlined';
import React, { useState } from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1TabContainer from "app/c1component/C1TabContainer";

const DriverDetails = ({
    inputData,
    handleInputChange,
    handleDateChange,
    handleInputFileChange,
    handleViewFile,
    viewType,
    isDisabled,
    errors,
    fileUploaded,
    handleEditPwChange,
    editable
}) => {

    const { t } = useTranslation(["administration", "common"]);
    const [currentShowPassword, setCurrentShowPassword] = useState(false);

    const handleClickShowPasswordCurrent = () => {
        setCurrentShowPassword(!currentShowPassword);
    };

    const handleMouseDownPassword = (event) => {
        event.preventDefault();
    };

    return (
        <React.Fragment>
            <Grid item xs={12}>
                <C1TabContainer>

                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:driverManagement.driverDetails.generalDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:driverManagement.driverDetails.id")}
                                        name="drvId"
                                        disabled
                                        onChange={handleInputChange}
                                        value={inputData?.drvId || ''} />
                                    <C1InputField
                                        label={t("administration:driverManagement.driverDetails.name")}
                                        name="drvName"
                                        required
                                        onChange={handleInputChange}
                                        value={inputData?.drvName || ''}
                                        error={errors['drvName'] !== undefined}
                                        helperText={errors['drvName'] || ''}
                                        disabled={isDisabled}
                                    />
                                </Grid>
                                {/*<Grid item xs={inputData?.drvLicensePhotoName ? 10 : 12}>*/}
                                {/*    <C1FileUpload*/}
                                {/*        inputProps={{ placeholder: t("administration:driverManagement.driverDetails.noFileChosen") }}*/}
                                {/*        name="fhotoFileButton"*/}
                                {/*        label={t("administration:driverManagement.driverDetails.browse")}*/}
                                {/*        fileChangeHandler={handleInputFileChange}*/}
                                {/*        inputLabel={t("administration:driverManagement.driverDetails.photo")}*/}
                                {/*        value={inputData?.drvLicensePhotoName}*/}
                                {/*        errors={errors['fhotoFileButton'] !== undefined}*/}
                                {/*        helperText={errors['fhotoFileButton'] || ''}*/}
                                {/*        required*/}
                                {/*        disabled={isDisabled}*/}
                                {/*    />*/}
                                {/*</Grid>*/}
                                {/*{inputData?.drvLicensePhotoName &&*/}
                                {/*    <Grid item xs={1}>*/}
                                {/*        <C1LabeledIconButton tooltip={t("buttons:download")} label={t("buttons:download")}*/}
                                {/*                             action={() => handleViewFile(inputData?.drvLicensePhotoName, inputData?.base64File)}>*/}
                                {/*            <GetAppOutlined />*/}
                                {/*        </C1LabeledIconButton>*/}
                                {/*    </Grid>}*/}
                            </Grid>
                        </C1CategoryBlock>

                        {/*<Grid item style={{ height: '39px' }}></Grid>*/}
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:driverManagement.driverDetails.licenseDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:driverManagement.driverDetails.licenseNo")}
                                        name="drvLicenseNo"
                                        required
                                        onChange={handleInputChange}
                                        value={inputData?.drvLicenseNo}
                                        error={errors['drvLicenseNo'] !== undefined}
                                        helperText={errors['drvLicenseNo'] || ''}
                                        disabled={isDisabled}
                                    />
                                    <Grid item xs={inputData?.drvLicensePhotoName ? 10 : 12}>
                                        <C1FileUpload
                                            inputProps={{ placeholder: t("administration:driverManagement.driverDetails.noFileChosen") }}
                                            name="fhotoFileButton"
                                            label={t("administration:driverManagement.driverDetails.browse")}
                                            fileChangeHandler={handleInputFileChange}
                                            inputLabel={t("administration:driverManagement.driverDetails.photo")}
                                            value={inputData?.drvLicensePhotoName}
                                            errors={errors['fhotoFileButton'] !== undefined}
                                            helperText={errors['fhotoFileButton'] || ''}
                                            disabled={isDisabled}
                                        />
                                    </Grid>
                                    {inputData?.drvLicensePhotoName &&
                                        <Grid item xs={1}>
                                            <C1LabeledIconButton tooltip={t("buttons:download")} label={t("buttons:download")}
                                                                 action={() => handleViewFile(inputData?.drvLicensePhotoName, inputData?.base64File)}>
                                                <GetAppOutlined />
                                            </C1LabeledIconButton>
                                        </Grid>}
                                    {/*<C1DateField*/}
                                    {/*    label={t("administration:driverManagement.driverDetails.expiredDate")}*/}
                                    {/*    name="drvLicenseExpiry"*/}
                                    {/*    value={inputData?.drvLicenseExpiry}*/}
                                    {/*    onChange={handleDateChange}*/}
                                    {/*    disabled={isDisabled}*/}
                                    {/*    error={errors['drvLicenseExpiry'] !== undefined}*/}
                                    {/*    helperText={errors['drvLicenseExpiry'] || ''}*/}
                                    {/*/>*/}

                                    <Grid container item spacing={4} alignItems="flex-start" alignContent="center">

                                    </Grid>

                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:driverManagement.driverDetails.accountDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:driverManagement.driverDetails.userId")}
                                        name="drvMobileId"
                                        onChange={handleInputChange}
                                        value={ inputData?.drvMobileId}
                                        required
                                        autoComplete="new-password"
                                        error={errors['drvMobileId'] !== undefined}
                                        helperText={errors['drvMobileId'] || ''}
                                        disabled={viewType !== 'new'}
                                    />
                                    {false && <Grid container item spacing={4} alignItems="flex-start" alignContent="center">
                                        <Grid item xs={!editable && viewType === 'edit' ? 10 : 12}>
                                            <FormControl variant="outlined" fullWidth style={{ marginTop: '15px' }}>

                                                <InputLabel htmlFor="password-driver" variant="outlined" shrink={true} required={true}> 
                                                    {t("administration:driverManagement.driverDetails.password")}
                                                </InputLabel>
                                                
                                                <OutlinedInput
                                                    id="password-driver"
                                                    label={t("administration:driverManagement.driverDetails.password")}
                                                    name="drvMobilePassword"
                                                    onChange={handleInputChange}
                                                    value={ inputData?.drvMobilePassword}
                                                    type={editable && viewType === 'edit' ? (currentShowPassword ? 'text' : 'password') : 'password'}
                                                    required
                                                    notched={true}
                                                    autoComplete="new-password"
                                                    error={errors['drvMobilePassword'] !== undefined}
                                                    helperText={errors['drvMobilePassword'] || ''}
                                                    disabled={!inputData?.drvEditPassword}
                                                    endAdornment={editable && viewType === 'edit' &&
                                                        <InputAdornment position="end">
                                                            <IconButton
                                                                aria-label="toggle password visibility"
                                                                onClick={handleClickShowPasswordCurrent}
                                                                onMouseDown={handleMouseDownPassword}
                                                                edge="end"
                                                            >
                                                                {currentShowPassword ? <VisibilityOff color="primary" /> : <Visibility color="primary" />}
                                                            </IconButton>
                                                        </InputAdornment>
                                                    }
                                                />
                                            </FormControl>
                                        </Grid>
                                        {!editable && viewType === 'edit' &&
                                            <Grid item xs={1} style={{ marginTop: '20px' }}>
                                                <C1LabeledIconButton tooltip={t("buttons:resetPw")} label={t("buttons:reset")}
                                                    action={() => handleEditPwChange(editable)}>
                                                    <LockOutlined />
                                                </C1LabeledIconButton>
                                            </Grid>}
                                    </Grid>}
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>

                        <Grid item style={{ height: '80px' }}></Grid>
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:driverManagement.driverDetails.contactDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        inputProps={{
                                            placeholder: t("common:common.placeHolder.contactEmail"),
                                            maxLength: 100
                                        }}
                                        label={t("administration:driverManagement.driverDetails.email")}
                                        name="drvEmail"
                                        required
                                        onChange={handleInputChange}
                                        value={inputData?.drvEmail || ''}
                                        error={errors['drvEmail'] !== undefined}
                                        helperText={errors['drvEmail'] || ''}
                                        disabled={isDisabled}
                                    />
                                    <C1InputField
                                        inputProps={{
                                            placeholder: t("common:common.placeHolder.contactTel"),
                                            maxLength: 100
                                        }}
                                        label={t("administration:driverManagement.driverDetails.phone")}
                                        name="drvPhone"
                                        required
                                        onChange={handleInputChange}
                                        value={inputData?.drvPhone || ''}
                                        error={errors['drvPhone'] !== undefined}
                                        helperText={errors['drvPhone'] || ''}
                                        disabled={isDisabled}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid container item lg={4} md={6} xs={12} direction="column" >
                        <C1CategoryBlock icon={<BorderColorOutlinedIcon />} title={t("administration:driverManagement.driverDetails.properties")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:driverManagement.driverDetails.createdBy")}
                                        value={inputData?.drvUidCreate}
                                        name="drvUidCreate"
                                        disabled
                                        onChange={handleInputChange}
                                    />

                                    <C1DateField
                                        label={t("administration:driverManagement.driverDetails.createdDate")}
                                        name="drvDtCreate"
                                        value={inputData?.drvDtCreate}
                                        disabled
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    />

                                    <C1InputField
                                        label={t("administration:driverManagement.driverDetails.updatedBy")}
                                        value={inputData?.drvUidLupd}
                                        name="drvUidLupd"
                                        disabled
                                        onChange={handleInputChange}
                                    />

                                    <C1DateField
                                        label={t("administration:driverManagement.driverDetails.updatedDate")}
                                        name="drvDtLupd"
                                        value={inputData?.drvDtLupd}
                                        disabled
                                        onChange={handleDateChange}
                                    />

                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                </C1TabContainer>

            </Grid >
        </React.Fragment >
    );
};

export default DriverDetails;