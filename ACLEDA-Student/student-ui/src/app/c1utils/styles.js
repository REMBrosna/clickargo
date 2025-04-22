import { green } from '@material-ui/core/colors';
import { makeStyles } from '@material-ui/core/styles';

//To make the tabs scrollable if it's long
export function tabScroll(index) {
    return {
        id: `scrollable-auto-tab-${index}`,
        'aria-controls': `scrollable-auto-tabpanel-${index}`,
    };
}


export const useStyles = makeStyles((theme) => ({
    gridContainer: {
        paddingLeft: 10,
        paddingRight: 10,

    },
    gridContainerFormRow: {
        marginBottom: 20
    },
    root: {
        display: 'flex',
        alignItems: 'center',
    },
    wrapper: {
        margin: theme.spacing(1),
        position: 'relative',
    },
    buttonSuccess: {
        backgroundColor: green[500],
        '&:hover': {
            backgroundColor: green[700],
        },
    },
    fabProgress: {
        color: green[500],
        position: 'absolute',
        top: -6,
        left: -6,
        zIndex: 1,
    },
    buttonProgress: {
        color: green[500],
        position: 'absolute',
        top: '50%',
        left: '50%',
        marginTop: -12,
        marginLeft: -12,
    },
    dataTablePaper: {
        overflow: "auto"
    }
}));

export const dialogStyles = makeStyles(theme => ({
    dialogWrapper: {
        padding: theme.spacing(2),
        position: 'absolute',
        top: theme.spacing(5)
    },
    dialogTitle: {
        paddingRight: '0px'
    },
    dialogPopUp: {
        width: 400
    },
    dialogButtonSpace: {
        float: "right"
    },
    // CPEDI-49
    paperFullWidth: {
        overflowY: 'visible'
    },
    dialogContentRoot: {
        overflowY: 'visible'
    }
}));


export const titleTab = makeStyles(theme => ({
    root: {
        boxShadow: "none",
        backgroundColor: "white",
        color: '#3C77D0',
        borderBottom: "0.5px solid #bdc0c5",
        lineHeight: "1.9",
        marginTop: "5px",
        fontFamily: [
            "Roboto", "Helvetica", "Arial", "sans-serif", "Khmer OS Siemreap"
        ].join(','),
        fontSize: '0.875rem',
        fontWeight: 500,
        letterSpacing: '0.02857em',
        whiteSpace: 'normal',
        marginBottom: 0
    }
}));



export const buttonStyles = makeStyles(theme => ({
    root: {
        '& > *': {
            margin: theme.spacing(1),
        },
    },
    buttonSpace: {
        float: "right"
    },
}));

export const loadingStyles = makeStyles(theme => ({
    loading: {
        position: "relative",
        left: 0,
        right: 0,
        top: "calc(50%-20px)",
        margin: "auto",
        height: "40px",
        width: "40px",
        zIndex: 1,
        "& img": {
            position: "absolute",
            height: "25px",
            width: "auto",
            top: 0,
            bottom: 0,
            left: 0,
            right: 0,
            margin: "auto",
            zIndex: 100
        }
    }
}));
