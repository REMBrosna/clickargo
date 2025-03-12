import { Box, Chip, Tooltip, Typography, makeStyles } from "@material-ui/core";
import React from "react";

const ChipStatus = (props) => {

    const {text, color, istooltip, ...other} = props;

    const useStyles = makeStyles((theme) => ({
        wrapper: {
            backgroundColor: color,
            borderRadius: 5,
            color: '#fff',
            fontSize: '0.688rem',
            fontWeight: 500,
            textAlign: 'center',
            whiteSpace: 'nowrap !important',
            width: "100px"
        },
    }));

    const classNames = useStyles();

    return istooltip  ? <Tooltip title={text || ""}>
            <Chip label={text} className={classNames.wrapper} {...other} />
        </Tooltip> :  <Chip label={text} className={classNames.wrapper} {...other} />
        
    
}

export default ChipStatus;