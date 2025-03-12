import React, {useState, useEffect} from "react";
import { Grid, MenuItem, Tooltip, Button } from "@material-ui/core";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import {DateRangeOutlined, DeleteOutline, NearMeOutlined} from '@material-ui/icons'
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import useHttp from "app/c1hooks/http";

const LeaseApplicationPopupForm = (props) => {

    const {
        popUp,
        errors,
        success,
        setOpen,
        setPopUp,
        inputData,
        translate,
        setErrors,
        setConfirm,
        setLoading,
        setSuccess,
        setInputData,
        setOpenActionConfirm,
    } = props;

    const handleInputChange = (e) => {
        const { value, name } = e.target;
        setInputData(pre => ({...pre, [name]: value}))
    }

    const actionsHandler = (action, trucksName, ) => {
        setConfirm({trucksName});
        setOpenActionConfirm({action})
        setOpen(true)
        setSuccess(false)
    };

    const elAction = (
        <>
            <Tooltip title={"submit"}>
                <Button style={{ float: 'right' }} >
                    <NearMeOutlined fontSize="large" color="primary" onClick={() => actionsHandler("SUBMIT", inputData?.truck)}/>
                </Button>
            </Tooltip>
        </>
    );

    return(
        <C1PopUp
            title={translate("administration:trucksRental.form.popupTitle")}
            openPopUp={popUp}
            setOpenPopUp={setPopUp}
            actionsEl={elAction}
        >
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <C1CategoryBlock
                        icon={<DateRangeOutlined />}
                        title={translate("administration:trucksRental.form.popupCategory")}
                    >
                        <Grid container alignItems="center" spacing={1}>
                            <Grid item sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name="company"
                                    label={translate("administration:trucksRental.form.popupField.company")}
                                    value={inputData?.company}
                                    onChange={handleInputChange}
                                    error={errors && errors["company"] || undefined}
                                    helperText={errors && errors["company"] || ''}
                                />
                            </Grid>
                            <Grid item sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name="accn"
                                    label={translate("administration:trucksRental.form.popupField.account")}
                                    value={inputData?.accn}
                                    onChange={handleInputChange}
                                    error={errors && errors["accn"] || undefined}
                                    helperText={errors && errors["accn"] || ''}
                                />
                            </Grid>
                            <Grid item sm={6} xs={12}>
                                <C1InputField
                                    required
                                    name="name"
                                    label={translate("administration:trucksRental.form.popupField.name")}
                                    value={inputData?.name}
                                    onChange={handleInputChange}
                                    error={errors && errors["name"] || undefined}
                                    helperText={errors && errors["name"] || ''}
                                />
                            </Grid>
                            <Grid item sm={6} xs={12}>
                                <C1InputField
                                    required
                                    name="contact"
                                    label={translate("administration:trucksRental.form.popupField.contact")}
                                    value={inputData?.contact}
                                    onChange={handleInputChange}
                                    error={errors && errors["contact"] || undefined}
                                    helperText={errors && errors["contact"] || ''}
                                />
                            </Grid>
                            <Grid item sm={6} xs={12}>
                                <C1InputField
                                    required
                                    name="email"
                                    label={translate("administration:trucksRental.form.popupField.email")}
                                    value={inputData?.email}
                                    onChange={handleInputChange}
                                    error={errors && errors["email"] || undefined}
                                    helperText={errors && errors["email"] || ''}
                                />
                            </Grid>
                        </Grid >
                    </C1CategoryBlock>
                </Grid>
            </Grid>
        </C1PopUp>
    )
};

export default LeaseApplicationPopupForm;