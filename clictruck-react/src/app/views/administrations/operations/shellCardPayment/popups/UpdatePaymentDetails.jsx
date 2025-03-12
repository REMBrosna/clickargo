import React, {useState, useEffect} from "react";
import { Grid, Tooltip, Button } from "@material-ui/core";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import {DateRangeOutlined} from '@material-ui/icons'
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import moment from "moment";
import LocalAtmOutlinedIcon from '@material-ui/icons/LocalAtmOutlined';
import ActionButton from "../../../../../atomics/atoms/ActionButton";
import C1DateField from "../../../../../c1component/C1DateField";
import {deepUpdateState} from "../../../../../c1utils/stateUtils";
import {MatxLoading} from "../../../../../../matx";

const UpdatePaymentDetails = (props) => {

    const {
        popUp,
        errors,
        setOpen,
        loading,
        setPopUp,
        translate,
        setErrors,
        inputData,
        setConfirm,
        setSuccess,
        setInputData,
        setOpenActionConfirm,
    } = props;

    const handleInputChange = (e) => {
        const { value, name } = e.target;
        setInputData(pre => ({...pre, [name]: value}))
    }

    const actionsHandler = (action, value ) => {
        if(Object.keys(validation()).length > 0){
            setErrors(validation())
        } else {
            setConfirm(value);
            setOpenActionConfirm({action})
            setOpen(true)
            setSuccess(false)
        }
    };

    const validation = () => {
        const error = {}
        if (inputData?.invPaymentDt === "" || inputData?.invPaymentDt === null){
            error["invPaymentDt"] =  translate("common:validationMsgs.required");
        }
        if (inputData?.invUidLupd === "" || inputData?.invUidLupd === null){
            error["invUidLupd"] =  translate("common:validationMsgs.required");
        }
        return error;
    }

    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
    };

    const elAction = (
        <>
            <Tooltip title={translate("administration:shellCardInv.button.updatePay")}>
                <ActionButton
                    variant="contained"
                    style={{ float: 'right', margin: "9px"}}
                    icon={<LocalAtmOutlinedIcon />}
                    handleAction={() => actionsHandler("UPDATE", inputData)}
                >
                    {translate("administration:shellCardInv.button.updatePay")}
                </ActionButton>
            </Tooltip>
        </>
    );

    return(
        <C1PopUp
            title={translate("administration:shell.form.title")}
            openPopUp={popUp}
            setOpenPopUp={setPopUp}
            actionsEl={elAction}
        >
            {loading && <MatxLoading />}
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <C1CategoryBlock
                        icon={<DateRangeOutlined />}
                        title={translate("administration:shellCardInv.popup.section.inv.title")}
                    >
                        <Grid container alignItems="center" spacing={1} style={{paddingTop: "25px"}}>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invNo"}
                                    value={inputData?.invNo}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.invNo")}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invDt"}
                                    value={moment(inputData?.invDt).format('YYYY-MM-DD')}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.invDate")}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invAmt"}
                                    value={`SGD ${inputData?.invAmt?.toLocaleString('en-US')}`}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.amount")}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invPaymentAmt"}
                                    value={`SGD ${inputData?.invPaymentAmt?.toLocaleString('en-US')}`}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.payAmount")}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invBalanceAmt"}
                                    value={`SGD ${inputData?.invBalanceAmt?.toLocaleString('en-US')}`}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.balAmount")}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1DateField
                                    required
                                    label={translate("administration:shellCardInv.popup.section.inv.payDate")}
                                    name={"invPaymentDt"}
                                    value={inputData?.invPaymentDt}
                                    type="date"
                                    onChange={handleDateChange}
                                    error={errors && errors["invPaymentDt"] || undefined}
                                    helperText={errors && errors["invPaymentDt"] || ''}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    // disabled
                                    name={"invUidLupd"}
                                    value={inputData?.invUidLupd}
                                    error={errors && errors["invUidLupd"] || undefined}
                                    helperText={errors && errors["invUidLupd"] || ''}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.payInfo")}
                                />
                            </Grid>
                        </Grid >
                    </C1CategoryBlock>
                </Grid>
            </Grid>
        </C1PopUp>
    )
};

export default UpdatePaymentDetails;