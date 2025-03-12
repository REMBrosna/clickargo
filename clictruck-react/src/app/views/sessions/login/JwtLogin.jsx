import { Button, Checkbox, CssBaseline, FormControlLabel, Grid } from "@material-ui/core";
import IconButton from '@material-ui/core/IconButton';
import { makeStyles } from "@material-ui/core/styles";

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
import ClickargoLoginBack from "./ClickargoLoginBack.png";
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Avatar from '@material-ui/core/Avatar';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import { Link } from "react-router-dom";
import Box from '@material-ui/core/Box';
import CircularProgress from "@material-ui/core/CircularProgress";

const useStyles = makeStyles((theme) => ({
    root: {
        height: '100vh',
    },
    image: {
        backgroundImage: `url(${ClickargoLoginBack})`,
        backgroundRepeat: 'no-repeat',
        backgroundColor:
            theme.palette.type === 'light' ? theme.palette.grey[50] : theme.palette.grey[900],
        backgroundSize: 'cover',
    },
    paper: {
        margin: theme.spacing(10, 4),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    avatar: {
        margin: theme.spacing(1),
        backgroundColor: theme.palette.secondary.main,
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(1),
        color: '#FFFFFF',
    },
    submit: {
        margin: theme.spacing(3, 0, 2),
        backgroundColor: '#13B1ED',
        padding: "14px 14px"
    },
    login: {
        backgroundColor: '#0772BA',
    },
    links: {
        color: '#FFFFFF',
    }

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
            // Check session path from BackToLoginPage
            if (props?.location?.pathname === "/session/signin/back-to-login") {
                history.push("/");
            } else {
                // Handle LoginStatus cases
                setLoading(false);
                switch (data.loginStatus) {
                    case LoginStatus.AUTHORIZED_LOGIN: {
                        if (state?.redirectUrl) {
                            history.push(state.redirectUrl);
                        } else {
                            history.push("/");
                        }
                        break;
                    }
                    case LoginStatus.AUTHORIZED_LOGIN_CHNG_PW_RQD: {
                        history.push("/session/force-change-password", userInfo.email);
                        break;
                    }
                    default: {
                        setMessage(data.err);
                        break;
                    }
                }
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
            <Grid container component="main" className={classes.root}>
                <CssBaseline />
                <Grid item xs={12} sm={8} md={9} className={classes.image} />
                <Grid item xs={12} sm={4} md={3} component={Paper} elevation={6} className={classes.login}>
                    <LanguageSelector showText={true} />
                    <Grid className={classes.paper}>
                        <Avatar className={classes.avatar}>
                            <LockOutlinedIcon />
                        </Avatar>
                        <Typography component="h1" variant="h5" style={{ color: '#fff' }}>
                            Sign in
                        </Typography>
                        <ValidatorForm className={classes.form} onSubmit={handleFormSubmit}>
                            {t("login.label.userId")}
                            <TextValidator
                                className="mb-6 w-full"
                                variant="outlined"
                                size="medium"
                                onChange={handleChange}
                                type="text"
                                name="email"
                                value={userInfo.email}
                                validators={["required"]}
                                errorMessages={[t("login.msgs.error.usrIdRequired")]}
                                style={{ backgroundColor: '#fff' }} />
                            {t("login.label.pwd")}
                            <TextValidator
                                className="mb-6 w-full"
                                variant="outlined"
                                size="medium"
                                onChange={handleChange}
                                name="password"
                                type={togglePassword ? "text" : "password"}
                                value={userInfo.password}
                                validators={["required"]}
                                errorMessages={[t("login.msgs.error.pwdRequired")]}
                                InputProps={{
                                    endAdornment: <InputAdornment position="end" required>
                                        <IconButton
                                            aria-label="Show Password"
                                            onClick={handleTogglePasswordHide}>
                                            {togglePassword ? <Visibility /> : <VisibilityOff />}
                                        </IconButton>
                                    </InputAdornment>
                                }}
                                style={{ backgroundColor: '#fff' }} />
                            {message && <p className="text-error">{message}</p>}
                            <Grid container>
                                <Grid item xs>
                                    <FormControlLabel
                                        //className="mb-2 min-w-288"
                                        name="isRememberMe"
                                        onChange={handleChange}
                                        control={
                                            <Checkbox size="small" onChange={({ target: { checked } }) =>
                                                handleChange({
                                                    target: { name: "isRememberMe", value: checked },
                                                })
                                            } checked={userInfo.isRememberMe || false} />
                                        } label={t("login.label.rememberMe")} />

                                </Grid>
                            </Grid>



                            <Grid className="relative">
                                <Button
                                    type="submit"
                                    fullWidth
                                    variant="contained"
                                    color="primary"
                                    disabled={loading}
                                    className={classes.submit}>
                                    {t("login.btn.signIn")}
                                </Button>
                                {loading && (<CircularProgress size={24} style={{
                                    position: "absolute",
                                    top: "50%",
                                    left: "50%",
                                    marginTop: -12,
                                    marginLeft: -12
                                }} />)}
                            </Grid>
                            <Grid container justifyContent="flex-end">
                                <Grid item>
                                    <Grid item xs style={{ paddingTop: '10px' }}>
                                        <Link to="/session/forgot-password" variant="body2" className={classes.links}>
                                            {t("login.label.forgotPwd")}
                                        </Link>
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Box mt={5}>
                                {/* <Copyright /> */}
                            </Box>
                        </ValidatorForm>
                    </Grid>
                </Grid>
            </Grid>
        </React.Fragment >
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
