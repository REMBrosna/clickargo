import { SvgIcon } from "@material-ui/core";
import { makeStyles } from '@material-ui/core/styles';
import React from "react";
import { useTranslation } from "react-i18next";

const iconStyles = makeStyles(theme => ({
    iconButton: {
        display: "flex",
        flexDirection: "column",
        justifyContent: 'center',
        alignItems: 'center',
        width: '20px',
        height: '20px',
    },
    iconText: {
        fontSize: '0.65rem',
        fontFamily: "Poppins",
        // textTransform: "uppercase",
        fontWeight: 600
    }
}));

const CkColumnIcon = (props) => {

    const { t } = useTranslation(["buttons"]);
    const classes = iconStyles();

    return (
        <React.Fragment>
            <div className={classes.iconButton}>
                <SvgIcon {...props} fontSize="small">
                    <path d="M4 5v13h17V5H4zm10 2v9h-3V7h3zM6 7h3v9H6V7zm13 9h-3V7h3v9z"></path>
                </SvgIcon>
                <label className={classes.iconText}>{t("buttons:cols")}</label>
            </div>
        </React.Fragment>
    );
};

export default CkColumnIcon;
