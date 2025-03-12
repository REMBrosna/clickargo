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

const CkDownloadIcon = (props) => {

    const { t } = useTranslation(["buttons"]);
    const classes = iconStyles();

    return (
        <div className={classes.iconButton}>
            <SvgIcon {...props} fontSize="small">
                <path d="M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM19 18H6c-2.21 0-4-1.79-4-4 0-2.05 1.53-3.76 3.56-3.97l1.07-.11.5-.95C8.08 7.14 9.94 6 12 6c2.62 0 4.88 1.86 5.39 4.43l.3 1.5 1.53.11c1.56.1 2.78 1.41 2.78 2.96 0 1.65-1.35 3-3 3zm-5.55-8h-2.9v3H8l4 4 4-4h-2.55z"></path>
            </SvgIcon>
            <label className={classes.iconText}>{t("buttons:csv")}</label>
        </div>
    );
};

export default CkDownloadIcon;
