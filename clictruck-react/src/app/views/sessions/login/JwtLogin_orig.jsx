import { AppBar, Button, Card, Checkbox, FormControlLabel, Grid, Toolbar } from "@material-ui/core";
import IconButton from '@material-ui/core/IconButton';
import Snackbar from '@material-ui/core/Snackbar';
import { makeStyles } from "@material-ui/core/styles";
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import CloseIcon from '@material-ui/icons/Close';
import clsx from "clsx";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { TextValidator, ValidatorForm } from "react-material-ui-form-validator";

import useHttp from "app/c1hooks/http";
import useAuth from 'app/hooks/useAuth';
import Footer from "app/MatxLayout/SharedCompoents/Footer";
import LanguageSelector from "app/MatxLayout/SharedCompoents/LanguageSelector";
import history from "history.js";

import Announcement from "../notification/Announcement";
import clickargoLogo from './clickargo-logo.png';
import notificationImage from './NotificationBell.PNG';
import PortEDI_BackImage from './PortEDI_BackImage.jpg';
import C1Button from "app/c1component/C1Button";
import { InputAdornment } from "@material-ui/core";
import { Visibility, VisibilityOff } from "@material-ui/icons";

const useStyles = makeStyles(({ palette, ...theme }) => ({
    cardHolder: {
        backgroundImage: `url(${PortEDI_BackImage})`,
        backgroundSize: 'cover',
        overflow: 'hidden',
    },
    card: {
        maxWidth: 550,
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
    root: {
        flexGrow: 1,
    },
    menuButton: {
        marginRight: theme.spacing(2),
    },
    title: {
        flexGrow: 1,
    },
}));

let LoginStatus = {};

const LoginStatusArray = [
    "AUTHORIZED_REG",
    "AUTHORIZED_IGNORE",
    "UNAUTHENTICATED",
    "UNAUTHORIZED",
    "AUTHORIZED_LOGIN",
    "AUTHORIZED_LOGIN_CHNG_PW_RQD",
    "ACCOUNT_SUSPENDED",
    "ACCOUNT_INACTIVE",
    "USER_SESSION_EXISTING",
    "PASSWORD_RESET_SUCCESS",
    "SESSION_EXPIRED_OR_INACTIVE",
];

LoginStatusArray.map((status) => LoginStatus = { ...LoginStatus, [status]: status });

const JwtLogin = (props) => {
    const { t } = useTranslation(['session']);

    const { state } = props?.location;
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const [data, setData] = useState([]);

    const [open, setOpen] = React.useState(false);
    const [loading, setLoading] = useState(false);
    const [userInfo, setUserInfo] = useState({ email: "", password: "", isRememberMe: false });
    const [message, setMessage] = useState('');
    const [togglePassword, setTogglePassword] = useState(false);
    const { login } = useAuth();
    //const success = useSelector(state => state.success);
    const classes = useStyles();


    const [openModal, setOpenModal] = useState(false);

    useEffect(() => {
        setLoading(false);
        // sendRequest("/api/pedi/announcement/public/CPEDI", "getAnnouncement", "get", {});
        // eslint-disable-next-line
    }, []);

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId === 'getAnnouncement') {
                setData(res.data);
                if (res.data?.length > 0) {
                    setOpen(true);
                }
            }
        } else if (error) {
            setLoading(false);
        }
        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    const handleChange = ({ target: { name, value } }) => {
        let temp = { ...userInfo };
        if (name === 'email') {
            // username to uppercase
            temp[name] = value.toUpperCase();
        } else {
            temp[name] = value;
        }
        setUserInfo(temp);
    };

    //NOTE: Use below for backend api
    const handleFormSubmit = async (event) => {
        setLoading(true);
        try {
            let data = await login(userInfo.email, userInfo.password, userInfo.isRememberMe);
            switch (data.loginStatus) {
                case LoginStatus.AUTHORIZED_LOGIN: {
                    if (state && state.redirectUrl)
                        history.push(state.redirectUrl);
                    else
                        history.push("/");

                    break;
                }
                case LoginStatus.AUTHORIZED_LOGIN_CHNG_PW_RQD:
                    history.push("/session/force-change-password", userInfo.email);
                    break;
                default:
                    setMessage(data.err);
                    setLoading(false);
                    break;
            }
        } catch (e) {
            console.log(e);
            setMessage(e.message);
            setLoading(false);
        }
    };

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setOpen(false);
    };

    const openNotificationModal = () => {
        setOpenModal(true);
        setOpen(false);
    }
    const handleAnnouncementClose = (cal) => {
        setOpenModal(false);
        setOpen(true);
    }

    const handleTogglePasswordHide = () => {
        setTogglePassword(!togglePassword);
    }

    return (
        <React.Fragment>
            <AppBar position="fixed">
                <Toolbar>
                    <LanguageSelector />
                </Toolbar>
            </AppBar>
            <div className={clsx("flex justify-end items-center  min-h-full-screen", classes.cardHolder)}>
                <Card className={classes.card}>
                    <Grid container>
                        <Grid item lg={5} md={5} sm={5} xs={12}>
                            <div className="p-8 flex justify-center items-center h-full">
                                <img className="w-200" src={clickargoLogo} alt="" />
                            </div>
                        </Grid>
                        <Grid item lg={7} md={7} sm={7} xs={12}>
                            <div className="p-8 h-full bg-light-gray relative">
                                <ValidatorForm onSubmit={handleFormSubmit}>
                                    <TextValidator
                                        className="mb-6 w-full"
                                        variant="outlined"
                                        size="medium"
                                        label={t("login.label.userId")}
                                        onChange={handleChange}
                                        type="text"
                                        name="email"
                                        value={userInfo.email}
                                        validators={["required"]}
                                        errorMessages={[t("login.msgs.error.usrIdRequired")]} />
                                    <TextValidator
                                        className="mb-3 w-full"
                                        label={t("login.label.pwd")}
                                        variant="outlined"
                                        size="medium"
                                        onChange={handleChange}
                                        name="password"
                                        type={togglePassword ? "text" : "password"}
                                        value={userInfo.password}
                                        validators={["required"]}
                                        errorMessages={[t("login.msgs.error.pwdRequired")]}
                                        InputProps={{
                                            endAdornment: <InputAdornment position="end">
                                                <IconButton
                                                    aria-label="Show Password"
                                                    onClick={handleTogglePasswordHide}>
                                                    {togglePassword ? <Visibility /> : <VisibilityOff />}
                                                </IconButton>
                                            </InputAdornment>
                                        }} />
                                    <FormControlLabel
                                        className="mb-3 min-w-288"
                                        name="agreement"
                                        onChange={handleChange}
                                        control={
                                            <Checkbox size="small" onChange={({ target: { checked } }) =>
                                                handleChange({
                                                    target: { name: "isRememberMe", value: checked },
                                                })
                                            } checked={userInfo.isRememberMe || false} />
                                        } label={t("login.label.rememberMe")} />

                                    {message && <p className="text-error">{message}</p>}

                                    <div className="flex flex-wrap items-center mb-4">
                                        <div className="relative">
                                            <C1Button text={t("login.btn.signIn")}
                                                color="primary"
                                                type="submit"
                                                disabled={loading}
                                                withLoading={loading}
                                                tipTitle="Login" />
                                        </div>
                                        <span className="mr-2 ml-5"> {t("login.label.or")} </span>
                                        <div className="relative">
                                            <C1Button text={t("login.btn.signUp")}
                                                tipTitle="Account Registration"
                                                className="capitalize"
                                                onClick={() => history.push("/session/signup")} />
                                        </div>
                                    </div>
                                    <Button className="text-primary"
                                        onClick={() => history.push("/session/forgot-password")}> {t("login.label.forgotPwd")}  </Button>
                                </ValidatorForm>
                            </div>
                        </Grid>
                    </Grid>
                </Card>


                <div>

                    <Announcement onOpen={{ open: openModal }} onClose={handleAnnouncementClose} data={data} />
                    <Snackbar
                        anchorOrigin={{
                            vertical: 'bottom',
                            horizontal: 'left',
                        }}
                        open={open}
                        onClose={handleClose}
                        message={
                            "Message"
                        }>
                        <div style={notificationStyle}>
                            <div style={{ float: "right" }}>
                                <React.Fragment>
                                    <IconButton size="small" aria-label="close" color="inherit" onClick={handleClose}>
                                        <CloseIcon fontSize="small" />
                                    </IconButton>
                                </React.Fragment>
                            </div>
                            <div style={{ float: "left" }}>
                                <img src={notificationImage} alt="notification" style={{ width: "70px" }} />
                            </div>
                            <div style={{ padding: "5px 0px 0px 5px" }}>
                                <h6>{data[0]?.canuSubject.length > 50 ? data[0]?.canuSubject.substring(0, 50) + "..." : data[0]?.canuSubject}</h6>
                                <span>{data[0]?.canuDescription.length > 23 ? data[0]?.canuDescription.substring(0, 30) + "..." : data[0]?.canuDescription}</span>
                                <div style={{ float: "right", paddingBottom: "3px", cursor: "pointer", paddingTop: "0px" }} onClick={openNotificationModal}>
                                    <ChevronRightIcon style={{ boxSizing: "unset" }} />
                                </div>
                            </div>
                        </div>
                    </Snackbar>
                </div>

            </div>
            <Footer />
        </React.Fragment>
    );
};

const notificationStyle = {
    backgroundColor: 'white',
    marginLeft: "2px",
    width: "350px",
    height: "90px",
    padding: "10px",
    borderRadius: '5px',
    // cursor: "pointer"
}
export default JwtLogin;
