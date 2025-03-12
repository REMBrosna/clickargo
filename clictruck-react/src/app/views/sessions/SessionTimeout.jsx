import React from "react";
import {
    Card,
    Grid,
    Button,
    Divider,
    Box,
} from "@material-ui/core";

import { makeStyles } from "@material-ui/core/styles";
import { Link } from "react-router-dom";
import clsx from "clsx";
import clickargoLogo from '../sessions/login/clickargo-logo.png';
import { useHistory } from "react-router-dom/cjs/react-router-dom";



const useStyles = makeStyles(({ palette, ...theme }) => ({
    cardHolder: {
        background: "#3C77D0",
    },
    card: {
        maxWidth: 1000,
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

const SessionTimeout = () => {

    const classes = useStyles();

    const history = useHistory();

    const handleOnClick = () => {
        let customLoginPath = window.localStorage.getItem("loginPath");
        if (customLoginPath)
            window.location.href = customLoginPath
        else
            history.push("/session/signin");
    }

    return (
        <Box
            className={clsx(
                "flex justify-center items-center  min-h-full-screen",
                classes.cardHolder
            )}
        >
            <Card className={classes.card}>
                <Grid container>
                    <Grid item lg={5} md={5} sm={5} xs={12}>
                        <Box className="p-8 flex justify-center items-center h-full">
                            <img
                                className="w-200"
                                src={clickargoLogo}
                                alt=""
                            />
                        </Box>
                    </Grid>
                    <Grid item lg={7} md={7} sm={7} xs={12}>
                        <Box className="p-8 h-full bg-light-gray relative">
                            <Box className="m-2" fontWeight='fontWeightMedium'>Session Timeout</Box>
                            <Divider />
                            <br />
                            <Box className="m-2">
                                {/* You have been logged out due to expired or multiple sessions. Please click
                                <Link to="/session/signin" >
                                    <span className="text-primary" > HERE </span>
                                </Link>to log in. */}
                                {/* JIRA CLICDO-210 [ClicDO] Session expired page has two buttons that lead to the same page */}
                                You have been logged out due to expired or multiple sessions. Please click Home button  to log in.
                                <br />
                                <br />
                                <Button
                                    variant="contained"
                                    color="primary"
                                    type="submit" onClick={handleOnClick}>
                                    Home
                                </Button>
                            </Box>
                            <br />
                            <br />
                            <br />
                            <br />
                        </Box>
                    </Grid>
                </Grid>
            </Card>
        </Box >
    );
};

export default SessionTimeout;
