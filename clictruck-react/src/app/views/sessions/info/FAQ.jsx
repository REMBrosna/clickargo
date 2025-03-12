import React from "react";
import { AppBar, Grid, Toolbar } from "@material-ui/core";
import LanguageSelector from "app/MatxLayout/SharedCompoents/LanguageSelector";
import Footer from "app/MatxLayout/SharedCompoents/Footer";
import useAuth from "app/hooks/useAuth";
import { makeStyles } from '@material-ui/core/styles';
import MuiAccordion from '@material-ui/core/Accordion';
import MuiAccordionSummary from '@material-ui/core/AccordionSummary';
import MuiAccordionDetails from '@material-ui/core/AccordionDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import { withStyles } from '@material-ui/core/styles';
import { faqs } from "./faqdb.js";

const useStyles = makeStyles((theme) => ({
    root: {
        margin: '100px 30px 0px 30px',
        color: 'black'
    },
    rootAccordion: {
        margin: '30px 30px 0px 30px',
        color: 'black',
        minHeight: '72vh',
    },
    authRoot: {
        margin: '30px 30px 0px 30px',
        color: 'black',
    },
    subContent: {
        marginLeft: '30px'
    },
    link: {
        color: '#551A8B'
    },
    sup: {
        fontSize: '9px'
    },
    heading: {
        fontWeight: 'bold'
    }

}));

const Accordion = withStyles({
    root: {
        border: '1px solid rgba(0, 0, 0, .125)',
        boxShadow: 'none',
        '&:not(:last-child)': {
            borderBottom: 0,
        },
        '&:before': {
            display: 'none',
        },
        '&$expanded': {
            margin: 'auto',
        },
    },
    expanded: {},
})(MuiAccordion);

const AccordionSummary = withStyles({
    root: {
        backgroundColor: 'rgba(0, 0, 0, .03)',
        borderBottom: '1px solid rgba(0, 0, 0, .125)',
        marginBottom: -1,
        minHeight: 56,
        '&$expanded': {
            minHeight: 56,
        },
    },
    content: {
        '&$expanded': {
            margin: '12px 0',
        },
    },
    expanded: {},
})(MuiAccordionSummary);

const AccordionDetails = withStyles((theme) => ({
    root: {
        padding: theme.spacing(2),
    },
}))(MuiAccordionDetails);

const FAQ = () => {

    const { isAuthenticated } = useAuth();
    const classes = useStyles();

    return (
        <React.Fragment>
            {isAuthenticated ? null : <AppBar position="fixed">
                <Toolbar>
                    <LanguageSelector />
                </Toolbar>
            </AppBar>}
            <Grid container spacing={1}>
                <Grid container item xs={12}>
                    <Grid item xs={12} className={isAuthenticated ? classes.authRoot : classes.root}>
                        <Typography variant="h4">Frequently Asked Questions</Typography>
                    </Grid>
                    <Grid item xs={12} className={isAuthenticated ? classes.authRoot : classes.rootAccordion}>
                        {/* {
                            faqs.map((item, idx) => {
                                return <Accordion key={idx}>
                                    <AccordionSummary
                                        expandIcon={<ExpandMoreIcon />}
                                        aria-controls="panel1a-content"
                                        id="panel1a-header">
                                        <Typography className={classes.heading}>{item.question}</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        <Typography>
                                            {item.answer.map((itm, i) => {
                                                if (i === 0)
                                                    return itm;
                                                else
                                                    return <span key={i}><br></br>&nbsp;&nbsp;&nbsp;{itm}</span>
                                            })}
                                        </Typography>
                                    </AccordionDetails>
                                </Accordion>
                            })
                        } */}
                    </Grid>
                </Grid>
            </Grid>



            {isAuthenticated ? null : <Footer />}
        </React.Fragment>
    );
};


export default FAQ;