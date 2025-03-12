import { Avatar } from "@material-ui/core";
import { blue, green, grey, orange, red } from '@material-ui/core/colors';
import Tooltip from '@material-ui/core/Tooltip';
import React from "react";
import { useTranslation } from "react-i18next";

const defBgColor = grey[600];
const appBgColor = green[300];
const procBgColor = orange[300];
const rejBgColor = red[300];
const penBgColor = blue[300];
const payBgColor = blue[600];

const getBackgroundColor = (status) => {
    switch (status) {
        case 'DRF':
            return defBgColor;
        case 'SUB':
            return defBgColor;
        case 'VER':
            return procBgColor;
        case 'ACK':
            return appBgColor;
        case 'APP':
            return appBgColor;
        case 'REJ':
            return rejBgColor;
        case 'PEN': // Pending Payment
            return penBgColor;
        case 'PAY': // Paid
            return payBgColor;
        default: return defBgColor;
    }
}

const getTooltipByStatus = (status, t) => {
    switch (status) {
        case 'DRF':
            return t("common:status.drf");
        case 'SUB':
            return t("common:status.sub");
        case 'VER':
            return t("common:status.ver");
        case 'APP':
            return t("common:status.app");
        case 'REJ':
            return t("common:status.rej");
        case 'RET':
            return t("common:status.ret");
        case 'AMN':
            return t("common:status.amn");
        case 'PEN':
            return t("common:status.pen");
        case 'PAY':
            return t("common:status.pay");
        default: return "";
    }
}

const C1StatusIcon = ({ status, className, children }) => {
    const { t } = useTranslation(['common']);
    return <Tooltip title={getTooltipByStatus(status, t)}><Avatar className={className} style={{ backgroundColor: getBackgroundColor(status) }}> {children} </Avatar></Tooltip>;
}

export default C1StatusIcon;