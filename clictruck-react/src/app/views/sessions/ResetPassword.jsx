import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Card, Grid, Button, CircularProgress, InputAdornment } from "@material-ui/core";
import { TextValidator, ValidatorForm } from "react-material-ui-form-validator";
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import axios from "axios.js";
import { useLocation } from "react-router-dom";
import clickargoLogo from './login/clickargo-logo.png';
import { Visibility, VisibilityOff } from "@material-ui/icons";
import IconButton from '@material-ui/core/IconButton';

const useStyles = makeStyles(({ palette, ...theme }) => ({
    cardHolder: {
        background: "#1976d2",
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

const ResetPassword = () => {
    const [inputData, setInputData] = useState({
        newPassword: "",
        confirmPassword: ""
    });
    const classes = useStyles();
    const [success, setSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    const [togglePassword, setTogglePassword] = useState(false);
    const [toggleConfirmedPwd, setToggleConfirmedPwd] = useState(false);

    const search = useLocation().search;
    const key = new URLSearchParams(search).get('key');


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

    const handleFormSubmit = async (event) => {
        setLoading(true);
        try {
            const { data } = await axios.put("/api/v1/clickargo/manageusr/password/change", { ...inputData, token: key });
            if (data) {
                setMessage(data.message);
                setLoading(false);
                setSuccess(true)
            }
        } catch (error) {
            setMessage(error?.err?.msg);
            setLoading(false);
        }
    };

    const handleTogglePasswordHide = () => {
        setTogglePassword(!togglePassword);
    }

    const handleToggleConfirmPwdHide = () => {
        setToggleConfirmedPwd(!toggleConfirmedPwd);
    }

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
                            <img
                                className="w-full"
                                src={clickargoLogo}
                                alt=""
                            />
                        </div>
                    </Grid>
                    <Grid item lg={7} md={7} sm={7} xs={12}>
                        <div className="p-8 h-full bg-light-gray relative">
                            {!success && (
                                <ValidatorForm onSubmit={handleFormSubmit} noValidate>
                                    <TextValidator
                                        required
                                        disabled={loading}
                                        className="mb-6 w-full"
                                        variant="outlined"
                                        label="New Password"
                                        onChange={handleChange}
                                        type={togglePassword ? "text" : "password"}
                                        name="newPassword"
                                        size="small"
                                        value={inputData.newPassword || ""}
                                        validators={["required"]}
                                        errorMessages={[
                                            "This field is required"
                                        ]}
                                        InputProps={{
                                            endAdornment: <InputAdornment position="end" required style={{ backgroundColor: "#e7f4fd" }}>
                                                <IconButton
                                                    aria-label="Show Password"
                                                    onClick={handleTogglePasswordHide}>
                                                    {togglePassword ? <Visibility /> : <VisibilityOff />}
                                                </IconButton>
                                            </InputAdornment>
                                        }}
                                    />

                                    <TextValidator
                                        required
                                        disabled={loading}
                                        className="mb-6 w-full"
                                        variant="outlined"
                                        label="Confirm Password"
                                        onChange={handleChange}
                                        type={toggleConfirmedPwd ? "text" : "password"}
                                        name="confirmPassword"
                                        size="small"
                                        value={inputData.confirmPassword || ""}
                                        validators={["required", "isPasswordMatch"]}
                                        errorMessages={[
                                            "This field is required",
                                            "Passwords do not match"
                                        ]}
                                        InputProps={{
                                            endAdornment: <InputAdornment position="end" required>
                                                <IconButton
                                                    aria-label="Show Password"
                                                    onClick={handleToggleConfirmPwdHide}>
                                                    {toggleConfirmedPwd ? <Visibility /> : <VisibilityOff />}
                                                </IconButton>
                                            </InputAdornment>
                                        }}
                                    />

                                    {message && <p className="text-error">{message}</p>}

                                    <div className="flex items-center">
                                        <Button
                                            type="submit"
                                            variant="contained"
                                            color="primary"
                                            disabled={loading}
                                        >
                                            Change Password
                                        </Button>
                                        {loading && (<CircularProgress size={24} className={classes.buttonProgress} />)}
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
                                                className="capitalize"
                                            >
                                                Back to Sign in
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

export default ResetPassword;
