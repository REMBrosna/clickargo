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

const CkFilterIcon = (props) => {

    const { t } = useTranslation(["buttons"]);
    const classes = iconStyles();

    return (
        <div className={classes.iconButton}>
                <SvgIcon {...props} fontSize="small">
                <path d="M10 18h4v-2h-4v2zM3 6v2h18V6H3zm3 7h12v-2H6v2z"></path>
            </SvgIcon>
            <label className={classes.iconText}>{t("buttons:filter")}</label>
        </div>
    );
};

export default CkFilterIcon;
