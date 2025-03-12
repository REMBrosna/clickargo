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
        height: '20px'
    },
    iconText: {
        fontSize: '0.65rem',
        fontFamily: "Poppins",
        // textTransform: "uppercase",
        fontWeight: 600
    }
}));

const CkPrintIcon = (props) => {

    const { t } = useTranslation(["buttons"]);
    const classes = iconStyles();

    return (
        <div className={classes.iconButton}>
            <SvgIcon {...props} fontSize="small">
                <path d="M19 8h-1V3H6v5H5c-1.66 0-3 1.34-3 3v6h4v4h12v-4h4v-6c0-1.66-1.34-3-3-3zM8 5h8v3H8V5zm8 12v2H8v-4h8v2zm2-2v-2H6v2H4v-4c0-.55.45-1 1-1h14c.55 0 1 .45 1 1v4h-2z"></path>
            </SvgIcon>
            <label className={classes.iconText}>{t("buttons:print")}</label>
        </div>
    );
};

export default CkPrintIcon;
