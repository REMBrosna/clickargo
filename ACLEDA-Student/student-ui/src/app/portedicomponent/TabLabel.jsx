import React from "react";
import ErrorIcon from '@material-ui/icons/Error';
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({

    tabText: {
        marginLeft: '16px',
        marginTop: '-1px',
        textTransform: 'capitalize'
    },
    tabIcon: {
        position: 'absolute',
        marginLeft: '-5px',
    },
    valid: {
        color: 'rgb(9 182 109)'
    },
    error: {
        color: 'red'
    }
}));

const TabLabel = ({ viewType, invalidTabs, tab, errors }) => {

    const classes = useStyles();

    let elTabLabel = tab.text;
    if (invalidTabs) {
        if (tab) {
            if (viewType !== 'new' && tab.name) {
                elTabLabel = <div>
                    <div className={classes.tabIcon}>
                        {
                            invalidTabs.find(e => e === tab.name) || (errors && errors[tab.name] === 'invalid') ?
                                <ErrorIcon fontSize="small" className={classes.error} /> :
                                <CheckCircleIcon fontSize="small" className={classes.valid} />
                        }
                    </div>
                    <div className={classes.tabText}>{tab.text}</div>
                </div>;
            }
        }
    }

    return elTabLabel;
}

export default TabLabel;