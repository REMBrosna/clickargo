import { Grid, Paper } from "@material-ui/core";
import { FormHelperText, OutlinedInput } from "@material-ui/core";
import FormControl from '@material-ui/core/FormControl';
import IconButton from '@material-ui/core/IconButton';
import InputAdornment from '@material-ui/core/InputAdornment';
import InputLabel from '@material-ui/core/InputLabel';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import React, { useState } from "react";
import { useTranslation } from "react-i18next";

import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import useAuth from "app/hooks/useAuth";
import axios from "axios.js";
import { MatxLoading } from "matx";
import {useHistory} from "react-router-dom";

const UpdatePasswordFormDetails = () => {
    const { t } = useTranslation(["user"]);

    const { user } = useAuth();
    console.log("user", user)
    const defaultInputData = {
        username: user?.username,
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
    };
    const [inputData, setInputData] = useState(defaultInputData);
    const [snackBarOptions, setSnackBarOptions] = useState({
        success: false,
        successMsg: null,
        error: false,
        errorMsg: null,
    });
    const [currentShowPassword, setCurrentShowPassword] = useState(false);
    const [newShowPassword, setNewShowPassword] = useState(false);
    const [confirmShowPassword, setConfirmShowPassword] = useState(false);
    const [validationError, setValidationError] = useState({});
    const history = useHistory();
    const [loading, setLoading] = useState(false);

    const handleSubmit = async () => {
        try {
            const validation = handleValidate();
            if(Object.keys(validation).length === 0) {
                setLoading(true);
                const res = await axios.put(
                    "/change-password",
                    inputData
                );
                if (res.data) {
                    setInputData(defaultInputData);
                    setLoading(false);
                    setSnackBarOptions({
                        success: true,
                        successMsg: res.data.message,
                    });
                }
            }
        } catch (error) {
            setLoading(false);
        }
    };

    const handleClickShowPasswordCurrent = () => {
        setCurrentShowPassword(!currentShowPassword);
    };

    const handleClickShowPasswordNew = () => {
        setNewShowPassword(!newShowPassword);
    };

    const handleClickShowPasswordConfirm = () => {
        setConfirmShowPassword(!confirmShowPassword);
    };

    const handleValidate = () => {
        const errors = {};
        const { newPassword, confirmPassword, currentPassword } = inputData;

        for (const key in { newPassword, confirmPassword }) {
            if (inputData[key] === "") {
                errors[key] = t("user.changePassword.errors.required");
            } else if (inputData[key].length < 8) {
                errors[key] = t("user.changePassword.errors.length");
            }
        }

        if (newPassword && newPassword.length >= 8 && newPassword !== confirmPassword) {
            errors.confirmPassword = t("user.changePassword.errors.notMatch");
        }

        setValidationError(errors)
        return errors;
    };

    const handleInputChange = ({ currentTarget: { name, value } }) => {
        setInputData({ ...inputData, [name]: value });
    };


    const handleMouseDownPassword = (event) => {
        event.preventDefault();
    };
    const formButtons = <C1FormButtons options={{
        submit: { eventHandler: handleSubmit},
        back: { show: true, eventHandler: () => history.push("/student/applicationStudent/list")}
    }} />;


    const renderValidationError = (field) => {
        if (validationError[field]) {
            return <FormHelperText error id="accountId-error">{validationError[field]}</FormHelperText>
        } else {
            return <FormHelperText>{t("user.changePassword.fields.passwordFormat")}</FormHelperText>
        }
    }


    return (
        <>
            {loading && <MatxLoading />}
            <C1FormDetailsPanel
                noValidate
                breadcrumbs={[{ name: t("user.changePassword.title") }]}
                title={t("user.changePassword.title")}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={snackBarOptions}
                isLoading={loading}
            >
                {({ errors }) => (
                    <Paper className="p-3">

                        <Grid container spacing={3} alignItems="center">
                            <Grid item lg={4} md={6} sm={12} >
                                <FormControl variant="outlined" fullWidth>
                                    <InputLabel htmlFor="outlined-adornment-password">
                                        {t("user.changePassword.fields.currentPassword")}</InputLabel>
                                    <OutlinedInput
                                        id="outlined-adornment-password"
                                        type={currentShowPassword ? 'text' : 'password'}
                                        value={inputData?.currentPassword || ""}
                                        name="currentPassword"
                                        onChange={handleInputChange}
                                        endAdornment={
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
                                        label="Password"
                                    />
                                    {renderValidationError("currentPassword")}
                                </FormControl>
                            </Grid>
                            <Grid item lg={4} md={6} sm={12} >
                                <FormControl variant="outlined" fullWidth>
                                    <InputLabel htmlFor="outlined-adornment-password">
                                        {t("user.changePassword.fields.newPassword")}</InputLabel>
                                    <OutlinedInput
                                        id="outlined-adornment-password"
                                        type={newShowPassword ? 'text' : 'password'}
                                        value={inputData?.newPassword || ""}
                                        name="newPassword"
                                        onChange={handleInputChange}
                                        endAdornment={
                                            <InputAdornment position="end">
                                                <IconButton
                                                    aria-label="toggle password visibility"
                                                    onClick={handleClickShowPasswordNew}
                                                    onMouseDown={handleMouseDownPassword}
                                                    edge="end"
                                                >
                                                    {newShowPassword ? <VisibilityOff color="primary" /> : <Visibility color="primary" />}
                                                </IconButton>
                                            </InputAdornment>
                                        }
                                        label="Password"
                                    />
                                    {renderValidationError("newPassword")}
                                </FormControl>
                            </Grid>
                            <Grid item lg={4} md={6} sm={12} >
                                <FormControl variant="outlined" fullWidth>
                                    <InputLabel htmlFor="outlined-adornment-password">
                                        {t("user.changePassword.fields.confirmPassword")}
                                    </InputLabel>
                                    <OutlinedInput
                                        id="outlined-adornment-password"
                                        type={confirmShowPassword ? 'text' : 'password'}
                                        value={inputData?.confirmPassword || ""}
                                        name="confirmPassword"
                                        onChange={handleInputChange}
                                        endAdornment={
                                            <InputAdornment position="end">
                                                <IconButton
                                                    aria-label="toggle password visibility"
                                                    onClick={handleClickShowPasswordConfirm}
                                                    onMouseDown={handleMouseDownPassword}
                                                    edge="end"
                                                >
                                                    {confirmShowPassword ? <VisibilityOff color="primary" /> : <Visibility color="primary" />}
                                                </IconButton>
                                            </InputAdornment>
                                        }
                                        label="Password"
                                    />
                                    {renderValidationError("confirmPassword")}
                                </FormControl>
                            </Grid>
                        </Grid>

                    </Paper>
                )}
            </C1FormDetailsPanel>
        </>
    );
};

export default UpdatePasswordFormDetails;
