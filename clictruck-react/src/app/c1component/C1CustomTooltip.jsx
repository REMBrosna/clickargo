import MuiTooltip from "@material-ui/core/Tooltip";
import React from "react";
import { useTranslation } from "react-i18next";

const C1CustomTooltip = ({ children, ...props }) => {

    const { t } = useTranslation(["buttons"]);

    let toolTip = "";

    if (props?.title === "Filter Table")
        toolTip = t("buttons:filterTable");
    if (props?.title === "Download CSV")
        toolTip = t("buttons:downloadCsv");
    if (props?.title === "Print")
        toolTip = t("buttons:print");
    if (props?.title === "View Columns")
        toolTip = t("buttons:viewColumns");
    if (props?.title === "Download Data")
        toolTip = t("buttons:downloadData")
    return (
        <MuiTooltip
            {...props}
            title={toolTip}
        >
            {children}
        </MuiTooltip>
    );
};

export default C1CustomTooltip;