import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import { AppBar, Grid, Toolbar, Typography } from "@material-ui/core";
import LanguageSelector from "app/MatxLayout/SharedCompoents/LanguageSelector";
import Footer from "app/MatxLayout/SharedCompoents/Footer";
import useAuth from "app/hooks/useAuth";


const useStyles = makeStyles((theme) => ({
    root: {
        margin: '100px 30px 0px 30px',
        color: 'black',
    },
    rootSub: {
        margin: '30px 30px 0px 30px',
        color: 'black'
    },
    authRoot: {
        margin: '30px 30px 0px 30px',
        color: 'black',
    },
    subContent: {
        marginLeft: '30px'
    },
    header: {
        marginBottom: '30px'
    },
    subHeader: {
        marginBottom: '20px'
    },
    bottom: {
        marginTop: '50px'
    },
    link: {
        color: '#551A8B'
    },
    sup: {
        fontSize: '9px'
    },
    height: {
        height: "500vh"
    },

}));

export default function Contact() {
    const classes = useStyles();
    const { isAuthenticated } = useAuth();

    return (
        <div className="h-full-screen flex-column flex-grow">
            {isAuthenticated ? null : <AppBar position="fixed">
                <Toolbar>
                    <LanguageSelector />
                </Toolbar>
            </AppBar>}
            <div className={classes.height}>
                <Grid container spacing={5} >
                    <Grid container item xs={12}>
                        <Grid item xs={12} className={isAuthenticated ? classes.authRoot : classes.root}>
                            <Typography variant="h4">Contact Us</Typography>
                        </Grid>
                        {/* <Grid container item xs={3} spacing={3} className={isAuthenticated ? classes.authRoot : classes.rootSub}>
                        <Grid item xs={12}>
                            <Typography variant="h6">MPWT (Ministry of Public Works and Transport)</Typography>
                            <Typography variant="subtitle1">Helpdesk Officer</Typography>
                            <Typography variant="subtitle2">&nbsp; &nbsp; &nbsp; &nbsp; H/P: +855 11 292 333</Typography>

                        </Grid>
                        <Grid item xs={12} style={{ borderBottom: "0.5px solid #bdc0c5", }}></Grid>
                        <Grid item xs={12}>
                            <Typography variant="h6">PPAP (Phnom Penh Autonomous Port)</Typography>
                            <Typography variant="subtitle1">Support Officer</Typography>
                            <Typography variant="subtitle2">&nbsp; &nbsp; &nbsp; &nbsp; H/P: +855 10 444 641</Typography>
                            <Typography variant="subtitle2">&nbsp; &nbsp; &nbsp; &nbsp; H/P: +855 10 444 382</Typography>

                        </Grid>
                        <Grid item xs={12} style={{ borderBottom: "0.5px solid #bdc0c5", }}></Grid>
                        <Grid item xs={12}>
                            <Typography variant="h6">PAS (Sihanoukville Autonomous Port)</Typography>
                            <Typography variant="subtitle1">Support Officer</Typography>
                            <Typography variant="subtitle2">&nbsp; &nbsp; &nbsp; &nbsp; H/P: +855 12 456 545</Typography>
                            <Typography variant="subtitle2">&nbsp; &nbsp; &nbsp; &nbsp; H/P: +855 81 666 652</Typography>
                            <Typography variant="subtitle2">&nbsp; &nbsp; &nbsp; &nbsp; H/P: +855 15 838 483</Typography>

                        </Grid>
                    </Grid> */}
                    </Grid>
                </Grid>
            </div>
            {isAuthenticated ? <div></div> : <Footer position="fixed" />}
        </div>

    );
}
