import React from "react";
import ErrorIcon from '@material-ui/icons/Error';
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

const TabLabel = ({ viewType, tab, errors }) => {

    const classes = useStyles();

    let parsedErrors = errors !== undefined && errors[`invalidTabs.${tab?.id}`] ? JSON.parse(errors[`invalidTabs.${tab?.id}`]) : null;

    let elTabLabel = tab.text;
    if (parsedErrors) {
        if (tab) {
            if (viewType !== 'new' && tab.id) {
                elTabLabel = <div>
                    <div className={classes.tabIcon}>
                        {
                            (parsedErrors && parsedErrors.includes(tab.id)) ?
                                <ErrorIcon fontSize="small" className={classes.error} /> : null
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