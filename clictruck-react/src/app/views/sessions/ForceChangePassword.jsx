import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Card, Grid, Button, CircularProgress, InputAdornment } from "@material-ui/core";
import { TextValidator, ValidatorForm } from "react-material-ui-form-validator";
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import axios from "axios.js";
import { useLocation } from "react-router-dom";
import PortEDI_BackImage from 'app/views/sessions/login/PortEDI_BackImage.jpg';
import clickargoLogo from 'app/views/sessions/login/clickargo-logo.png';
import { useTranslation } from "react-i18next";
import { Visibility, VisibilityOff } from "@material-ui/icons";
import IconButton from '@material-ui/core/IconButton';

const useStyles = makeStyles(({ palette, ...theme }) => ({
    cardHolder: {
        backgroundImage: `url(${PortEDI_BackImage})`,
        background: "#1A2038",
        backgroundSize: 'cover',
        overflow: 'hidden',
    },
    card: {
        maxWidth: 800,
        borderRadius: 12,
        margin: "1rem",
    },
    buttonProgress: {
        position: "absolute",
        top: "50%",
        left: "50%",
        marginTop: -12,
        marginLeft: -12,
    },
}));

const ForceChangePassword = () => {

    const { t } = useTranslation(['session']);
    const classes = useStyles();
    const [success, setSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");
    const [togglePassword, setTogglePassword] = useState({ currentPassword: false, newPassword: false, confirmPassword: false })

    const location = useLocation();
    const search = location.search;
    const key = new URLSearchParams(search).get('key');

    const [inputData, setInputData] = useState({
        userId: location.state,
        forceChangePwd: true,
        newPassword: "",
        confirmPassword: ""
    });

    useEffect(() => {
        ValidatorForm.addValidationRule('isPasswordMatch', (value) => value === inputData.newPassword);
        return () => {
            ValidatorForm.removeValidationRule('isPasswordMatch');
        }
    })

    const handleChange = ({ target: { name, value } }) => {
        setInputData({
            ...inputData,
            [name]: value,
        });
    };

    const handleTogglePassword = (fieldName, value) => {
        setTogglePassword({ ...togglePassword, [fieldName]: value })
    }

    const handleFormSubmit = async (event) => {
        setLoading(true);
        try {
            const { data: rspnData } = await axios.put("/api/v1/clickargo/manageusr/password/change", { ...inputData, token: key });
            const { status, data, err } = rspnData;

            if (status === "SUCCESS") {
                setMessage(data);
                setLoading(false);
                setSuccess(true)
            } else {
                setMessage(err.msg);
                setLoading(false);
                setSuccess(false);
            }
        } catch (error) {
            setMessage(error?.err?.msg);
            setLoading(false);
        }
    };

    return (
        <div
            className={clsx(
                "flex justify-center items-center  min-h-full-screen",
                classes.cardHolder
            )}
        >
            <Card className={classes.card}>
                <Grid container>
                    <Grid item lg={5} md={5} sm={5} xs={12}>
                        <div className="p-8 flex justify-center items-center h-full">
                            <img className="w-200" src={clickargoLogo} alt="" />
                        </div>
                    </Grid>
                    <Grid item lg={7} md={7} sm={7} xs={12}>
                        <div className="p-8 h-full bg-light-gray relative">
                            {!success && (<h5 className="text-error">{t("forcedPwdChange.label.header")}</h5>)}
                            {success && (<h5 className="text-error">{t("forcedPwdChange.msgs.pwdChngSuccess")}</h5>)}
                            {!success && (
                                <ValidatorForm onSubmit={handleFormSubmit} noValidate>

                                    <TextValidator
                                        required
                                        disabled
                                        className="mb-6 w-full"
                                        variant="outlined"
                                        label={t("forcedPwdChange.label.userId")}
                                        onChange={handleChange}
                                        type="text"
                                        name="userId"
                                        size="small"
                                        value={inputData.userId || ""}
                                        validators={["required"]}
                                        errorMessages={[
                                            t("forcedPwdChange.msgs.usrIdRequired")
                                        ]} />
                                    <TextValidator
                                        required
                                        disabled={loading}
                                        className="mb-6 w-full"
                                        variant="outlined"
                                        label={t("forcedPwdChange.label.currentPwd")}
                                        onChange={handleChange}
                                        name="currentPassword"
                                        size="small"
                                        type={togglePassword.currentPassword ? "text" : "password"}
                                        value={inputData.currentPassword || ""}
                                        validators={["required"]}
                                        errorMessages={[
                                            t("forcedPwdChange.msgs.oldPwdRequired")
                                        ]}
                                        InputProps={{
                                            endAdornment: <InputAdornment position="end" required>
                                                <IconButton
                                                    aria-label="Show Password"
                                                    onClick={() => handleTogglePassword("currentPassword", !togglePassword.currentPassword)}>
                                                    {togglePassword.currentPassword ? <Visibility /> : <VisibilityOff />}
                                                </IconButton>
                                            </InputAdornment>
                                        }} />
                                    <TextValidator
                                        required
                                        disabled={loading}
                                        className="mb-6 w-full"
                                        variant="outlined"
                                        label={t("forcedPwdChange.label.newPwd")}
                                        onChange={handleChange}
                                        type={togglePassword.newPassword ? "text" : "password"}
                                        name="newPassword"
                                        size="small"
                                        value={inputData.newPassword || ""}
                                        validators={["required"]}
                                        errorMessages={[
                                            t("forcedPwdChange.msgs.newPwdRequired")
                                        ]}
                                        helperText={t("forcedPwdChange.msgs.passwordFormat")}
                                        InputProps={{
                                            endAdornment: <InputAdornment position="end" required>
                                                <IconButton
                                                    aria-label="Show Password"
                                                    onClick={() => handleTogglePassword("newPassword", !togglePassword.newPassword)}>
                                                    {togglePassword.newPassword ? <Visibility /> : <VisibilityOff />}
                                                </IconButton>
                                            </InputAdornment>
                                        }} />

                                    <TextValidator
                                        required
                                        disabled={loading}
                                        className="mb-6 w-full"
                                        variant="outlined"
                                        label={t("forcedPwdChange.label.confirmPwd")}
                                        onChange={handleChange}
                                        type={togglePassword.confirmPassword ? "text" : "password"}
                                        name="confirmPassword"
                                        size="small"
                                        value={inputData.confirmPassword || ""}
                                        validators={["required", "isPasswordMatch"]}
                                        errorMessages={[
                                            t("forcedPwdChange.msgs.confPwdRequired"),
                                            t("forcedPwdChange.msgs.pwdNoMatch")
                                        ]}
                                        helperText={t("forcedPwdChange.msgs.passwordFormat")}
                                        InputProps={{
                                            endAdornment: <InputAdornment position="end" required>
                                                <IconButton
                                                    aria-label="Show Password"
                                                    onClick={() => handleTogglePassword("confirmPassword", !togglePassword.confirmPassword)}>
                                                    {togglePassword.confirmPassword ? <Visibility /> : <VisibilityOff />}
                                                </IconButton>
                                            </InputAdornment>
                                        }} />

                                    {message && <p className="text-error">{message}</p>}

                                    <div className="flex items-center">
                                        <Button
                                            type="submit"
                                            variant="contained"
                                            color="primary"
                                            disabled={loading}>
                                            {t("forcedPwdChange.btn.changePwd")}
                                        </Button>
                                        {loading && (<CircularProgress size={24} className={classes.buttonProgress}/>)}
                                        <span className="ml-4 mr-2">or</span>
                                        <Link to="/session/signin/back-to-login">
                                            <Button
                                                style={{boxShadow: "rgba(0, 0, 0, 0.15) 1.95px 1.95px 2.6px"}}
                                                className="capitalize"
                                            >
                                                {t("login.label.backToLoginPage")}
                                            </Button>
                                        </Link>

                                    </div>
                                </ValidatorForm>
                            )}

                            {success && (
                                <>
                                    <p>{message}</p>

                                    <div className="flex items-center">
                                        <Link to="/session/signin">
                                            <Button
                                                variant="contained"
                                                color="primary"
                                                className="capitalize">
                                                {t("forcedPwdChange.btn.backToSignIn")}
                                            </Button>
                                        </Link>
                                    </div>
                                </>
                            )}
                        </div>
                    </Grid>
                </Grid>
            </Card>
        </div>
    );
};

export default ForceChangePassword;
