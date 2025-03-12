import React from "react";
import Grid from "@material-ui/core/Grid";

import FormControlLabel from '@material-ui/core/FormControlLabel';
import Switch from '@material-ui/core/Switch';
import FormLabel from '@material-ui/core/FormLabel';
import FormControl from '@material-ui/core/FormControl';
import FormGroup from '@material-ui/core/FormGroup';

import C1TabContainer from "app/c1component/C1TabContainer";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";

import { MST_HSCODE_TYPE_URL, MST_UOM_URL } from "app/c1utils/const";
import { useStyles } from "app/c1utils/styles";
import { isEditable } from "app/c1utils/utility";

const HsCodeDetails = ({
    inputData,
    handleInputChange,
    handleInputChangeSwitch,
    viewType,
    isSubmitting,
    errors,
    locale }) => {

    const classes = useStyles();
    let isDisabled = isEditable(viewType, isSubmitting);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField
                                label={locale("masters:hsCode.details.tabs.recordDetails.hsCode")}
                                name="hsCode"
                                required
                                disabled={(viewType === 'edit' || viewType === 'view')}
                                onChange={handleInputChange}
                                value={inputData.hsCode}
                                error={errors && errors.hsCode ? true : false}
                                helperText={errors && errors.hsCode ? errors.hsCode : null}
                                inputProps={{
                                    maxLength: 10
                                }} />

                            <C1InputField
                                required
                                label={locale("masters:hsCode.details.tabs.recordDetails.hsDescription")}
                                name="hsDescription"
                                disabled={isDisabled}
                                onChange={handleInputChange}
                                value={inputData.hsDescription}
                                error={errors && errors.hsDescription ? true : false}
                                helperText={errors && errors.hsDescription ? errors.hsDescription : null}
                                inputProps={{
                                    maxLength: 512
                                }} />

                            <FormControl component="fieldset" fullWidth >
                                <FormLabel component="legend">{locale("masters:hsCode.details.tabs.recordDetails.controlled.label")}</FormLabel>
                                <FormGroup row >
                                    <FormControlLabel style={{ width: "50%" }}
                                        control={
                                            <Switch
                                                checked={"Y" === inputData.hsImpControlled}
                                                name="hsImpControlled"
                                                color="primary"
                                                onChange={handleInputChangeSwitch}
                                                disabled={isDisabled}
                                            />
                                        }
                                        label={locale("masters:hsCode.details.tabs.recordDetails.controlled.hsImpControlled")}
                                    />
                                    <FormControlLabel
                                        control={
                                            <Switch
                                                checked={"Y" === inputData.hsExpControlled}
                                                name="hsExpControlled"
                                                color="primary"
                                                onChange={handleInputChangeSwitch}
                                                disabled={isDisabled}
                                            />
                                        }
                                        label={locale("masters:hsCode.details.tabs.recordDetails.controlled.hsExpControlled")}
                                    />
                                </FormGroup>
                            </FormControl>

                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1SelectField
                                name="TMstHsType.hstypeCode"
                                label={locale("masters:hsCode.details.tabs.recordDetails.hstypeCode")}
                                onChange={handleInputChange}
                                value={inputData.TMstHsType ? inputData.TMstHsType.hstypeCode : ""}
                                disabled={isDisabled}
                                isServer={true}
                                options={{
                                    url: MST_HSCODE_TYPE_URL,
                                    key: "hsCodeType",
                                    id: 'hstypeCode',
                                    desc: 'hstypeDescription',
                                    isCache: false
                                }} />

                            <C1InputField
                                label={locale("masters:hsCode.details.tabs.recordDetails.hsDutyType")}
                                name="hsDutyType"
                                disabled={isDisabled}
                                onChange={handleInputChange}
                                value={inputData.hsDutyType}
                                error={errors && errors.hsDutyType ? true : false}
                                helperText={errors && errors.hsDutyType ? errors.hsDutyType : null}
                                inputProps={{
                                    maxLength: 3
                                }} />

                            <C1InputField
                                type="number"
                                label={locale("masters:hsCode.details.tabs.recordDetails.hsExciseDutyRate")}
                                name="hsExciseDutyRate"
                                disabled={isDisabled}
                                onChange={handleInputChange}
                                value={inputData.hsExciseDutyRate}
                                error={errors && errors.hsExciseDutyRate ? true : false}
                                helperText={errors && errors.hsExciseDutyRate ? errors.hsExciseDutyRate : null}
                            />

                            <C1InputField
                                type="number"
                                label={locale("masters:hsCode.details.tabs.recordDetails.hsCustomsDutyRate")}
                                name="hsCustomsDutyRate"
                                disabled={isDisabled}
                                onChange={handleInputChange}
                                value={inputData.hsCustomsDutyRate}
                                error={errors && errors.hsCustomsDutyRate ? true : false}
                                helperText={errors && errors.hsCustomsDutyRate ? errors.hsCustomsDutyRate : null}
                            />
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                        <Grid item xs={12} >
                            <C1SelectField
                                name="TMstUomByHsUom.uomCode"
                                label={locale("masters:hsCode.details.tabs.recordDetails.uomCode")}
                                onChange={handleInputChange}
                                value={inputData.TMstUomByHsUom ? inputData.TMstUomByHsUom.uomCode : ""}
                                disabled={isDisabled}
                                isServer={true}
                                isShowCode={true}
                                options={{
                                    url: MST_UOM_URL,
                                    key: "uom",
                                    id: 'uomCode',
                                    desc: 'uomDescription',
                                    isCache: false
                                }} />

                            <C1SelectField
                                name="TMstUomByHsExciseDutyUom.uomCode"
                                label={locale("masters:hsCode.details.tabs.recordDetails.TMstUomByHsExciseDutyUom")}
                                onChange={handleInputChange}
                                value={inputData.TMstUomByHsExciseDutyUom ? inputData.TMstUomByHsExciseDutyUom.uomCode : ""}
                                disabled={isDisabled}
                                isServer={true}
                                isShowCode={true}
                                options={{
                                    url: MST_UOM_URL,
                                    key: "uom",
                                    id: 'uomCode',
                                    desc: 'uomDescription',
                                    isCache: false
                                }} />

                            <C1SelectField
                                name="TMstUomByHsCustomsDutyUom.uomCode"
                                label={locale("masters:hsCode.details.tabs.recordDetails.TMstUomByHsCustomsDutyUom")}
                                onChange={handleInputChange}
                                value={(inputData.TMstUomByHsCustomsDutyUom) ? inputData.TMstUomByHsCustomsDutyUom.uomCode : ""}
                                disabled={isDisabled}
                                isServer={true}
                                isShowCode={true}
                                options={{
                                    url: MST_UOM_URL,
                                    key: "uom",
                                    id: 'uomCode',
                                    desc: 'uomDescription',
                                    isCache: false
                                }} />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default HsCodeDetails;