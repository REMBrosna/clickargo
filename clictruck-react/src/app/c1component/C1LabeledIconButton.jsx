import { Box, IconButton, makeStyles, Tooltip, Typography } from "@material-ui/core";
import React from "react";

/**
 * @param tooltip - tooltip
 * @param label - label of the icon
 * @param action - onClick event
 * @param color - primary, secondary
 * @param children - icon
 */
const C1LabeledIconButton = ({ tooltip, label, action, disabled, color = 'primary', children, ...other }) => {

    const styles = {
        wrapper: {
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            flexDirection: 'column',
            paddingX: 1,
        },
        label: {
            fontSize: '0.65rem',
            fontFamily: "Poppins",
            //textTransform: "uppercase"
        }
    }

    const useStyles = makeStyles((theme) => ({
        icon: {
            color: !color || color === 'primary' ? '#1976d2' : disabled ? "#DDDDDD" : ''
        }
    }));

    const classNames = useStyles();

    return (
        <Tooltip title={tooltip}>
            <Box component={`div`} sx={styles.wrapper}>
                <IconButton onClick={action ? action : null} size="small" disabled={disabled ? disabled : false}
                    color={color} className={classNames.icon}>
                    {children}
                </IconButton>
                {/* <label style={styles.label}>{label}</label> */}
                {/* <Typography color={color} style={styles.label} {...other}>{label}</Typography> */}
                <Typography variant="subtitle2" display="block" align="center" className={classNames.icon} color={color} style={styles.label} {...other}>{label ? label : null}</Typography>
            </Box>
        </Tooltip>
    )
};

export default C1LabeledIconButton;
