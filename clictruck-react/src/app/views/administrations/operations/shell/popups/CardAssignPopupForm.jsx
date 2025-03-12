import React, {useState, useEffect} from "react";
import { Grid, Tooltip, Button } from "@material-ui/core";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import {DateRangeOutlined, NearMeOutlined} from '@material-ui/icons'
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import HowToRegIcon from '@material-ui/icons/HowToReg';
import C1AccnDropDownList from "../../../../../c1component/dropdownList/C1AccnDropDownList";
import C1TrucksDropDownList from "../../../../../c1component/dropdownList/C1TrucksDropDownList";
import C1ShellCardDropDownList from "../../../../../c1component/dropdownList/C1ShellCardDropDownList";

const CardAssignPopupForm = (props) => {

    const {
        popUp,
        errors,
        setOpen,
        success,
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
        if (inputData?.tcoreAccn?.accnId === "" || inputData?.tcoreAccn?.accnId === null){
            error["tcoreAccn?.accnId"] =  translate("common:validationMsgs.required");
        }
        if (inputData?.tcoreAccn?.accnName === "" || inputData?.tcoreAccn?.accnName === null){
            error["tcoreAccn?.accnName"] =  translate("common:validationMsgs.required");
        }
        if (inputData?.tckCtShellCard?.scId === "" || inputData?.tckCtShellCard?.scId === null){
            error["tckCtShellCard?.scId"] =  translate("common:validationMsgs.required");
        }
        if (inputData?.tckCtVeh?.vhId === "" || inputData?.tckCtVeh?.vhId === null){
            error["tckCtVeh?.vhId"] =  translate("common:validationMsgs.required");
        }
        return error;
    }

    const handleAutoChange = (e, ojb) => {
        const { value ,name } = e.target;
        const [obName, fName] = name.split(".");
        setInputData(pre => ({...pre, [obName]: { ...pre?.[obName], [fName]: value}}))
    };

    const handleAccnAutoChange = (e, ojb) => {
        const { value, name } = e.target;
        const [obName, fName] = name.split(".");
        setInputData(pre => ({...pre, [obName]: { ...pre?.[obName], [fName]: value, accnName: ojb?.accnName}}))
    };

    const elAction = (
        <>
            <Tooltip title={translate("administration:shell.form.button.assign")}>
                <Button style={{ float: 'right' }} >
                    <HowToRegIcon fontSize="large" color="primary" onClick={() => actionsHandler("ASSIGN", inputData)}/>
                </Button>
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
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <C1CategoryBlock
                        icon={<DateRangeOutlined />}
                        title={translate("administration:shell.form.section")}
                    >
                        <Grid container alignItems="center" spacing={1} style={{paddingTop: "25px"}}>
                            <Grid item sm={6} xs={12}>
                                <C1AccnDropDownList
                                    additionalProps={{required: true}}
                                    value={inputData?.tcoreAccn?.accnId}
                                    onChange={handleAccnAutoChange}
                                    className="min-w-280"
                                    error={errors && errors["tcoreAccn?.accnId"] || undefined}
                                    helperText={errors && errors["tcoreAccn?.accnId"] || ''}
                                    label={translate("administration:shell.form.control.accId")}
                                />
                            </Grid>
                            <Grid item sm={6} xs={12}>
                                <C1TrucksDropDownList
                                    accnId={inputData?.tcoreAccn?.accnId}
                                    value={inputData?.tckCtVeh?.vhId}
                                    onChange={handleAutoChange}
                                    className="min-w-280"
                                    additionalProps={{required: true}}
                                    error={errors && errors["tckCtVeh?.vhId"] || undefined}
                                    helperText={errors && errors["tckCtVeh?.vhId"] || ''}
                                    label={translate("administration:shell.form.control.truckPlateNo")}
                                />
                            </Grid>
                            <Grid item sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"tcoreAccn.accnName"}
                                    value={inputData?.tcoreAccn?.accnName}
                                    onChange={handleInputChange}
                                    error={errors && errors["tcoreAccn?.accnName"] || undefined}
                                    helperText={errors && errors["tcoreAccn?.accnName"] || ''}
                                    label={translate("administration:shell.form.control.name")}
                                />
                            </Grid>
                            <Grid item sm={6} xs={12}>
                                <C1ShellCardDropDownList
                                    additionalProps={{required: true}}
                                    value={inputData?.tckCtShellCard?.scId}
                                    onChange={handleAutoChange}
                                    className="min-w-280"
                                    error={errors && errors["tckCtShellCard?.scId"] || undefined}
                                    helperText={errors && errors["tckCtShellCard?.scId"] || ''}
                                    label={translate("administration:shell.form.control.cardNo")}
                                />
                            </Grid>
                        </Grid >
                    </C1CategoryBlock>
                </Grid>
            </Grid>
        </C1PopUp>
    )
};

export default CardAssignPopupForm;