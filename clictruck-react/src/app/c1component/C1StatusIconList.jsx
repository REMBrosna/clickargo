
import React from "react";
import C1StatusIcon from "./C1StatusIcon"
import { makeStyles } from '@material-ui/core/styles';

const iconStyles = makeStyles((theme) => ({
    root: {
        display: 'flex',
        '& > *': {
            margin: theme.spacing(0),
        },
    },
    small: {
        width: theme.spacing(3),
        height: theme.spacing(3),
    },
}));


const C1StatusIconList = ({ statusLabelList }) => {

    const classes = iconStyles();

    return <div className={classes.root}>
        {statusLabelList && statusLabelList.map((statusLabel, ind) => (
            <C1StatusIcon key={ind} status={statusLabel.status} className={classes.small}> {statusLabel.label}</C1StatusIcon>
        ))}
    </div>;
}

export default C1StatusIconList;