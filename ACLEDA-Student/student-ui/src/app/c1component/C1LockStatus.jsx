import React from "react";
import { Badge, Tooltip } from "@material-ui/core";
import LockIcon from '@material-ui/icons/Lock';
import { red } from "@material-ui/core/colors";
import { useTranslation } from "react-i18next";
const C1LockStatus = ({
    children
}) => {

    const { t } = useTranslation(["common"]);

    return <Tooltip title={t("common:tooltip.appLock")}><Badge badgeContent={<LockIcon style={{ color: red['A400'] }} />}>
        {children}
    </Badge></Tooltip>;
}



export default C1LockStatus;