import React, { useState } from "react";
import { useStyles } from "app/c1utils/styles";
import Grid from "@material-ui/core/Grid";
import { useSelector } from "react-redux";
import { Tooltip, Typography } from "@material-ui/core";
import HelpOutlineIcon from '@material-ui/icons/HelpOutline';
import C1UserGuide from "./C1UserGuide";
import { useTranslation } from "react-i18next";

/**
 * @description CamelOne custom user guide/information container.
 * 
 * @param children - any element can be placed within this container
 * @param title - header label for the container; specify "empty" if title is not applicable
 * @param elAction - right aligned element with action already binded to it (e.g. Button). This just serves as the placeholder of that element. The event should be handled by the parent not here.
 * @param guideId - the guide ID configured from backend in a dot notation, or depending on the key specification
 * @param guideAlign - the alignment of where the guide can be shown - default is left. Option - left, right
 * @param open - default is true. This will toggle to display the guide by default or hide.
 */
const C1TabInfoContainer = ({ children, title, elAction, guideId, guideAlign = "left", open = false }) => {
    const classes = useStyles();
    let userGuide = useSelector(({ userGuide }) => userGuide);
    const [openHelp, setOpenHelp] = useState(open);

    const { t } = useTranslation(["common"]);

    const toggleHelpEvent = (e) => {
        e.preventDefault();

        setOpenHelp(!openHelp);
    }

    let isRightAlign = guideAlign === 'right';

    return (
        <Grid container className={classes.gridContainer} direction="row" justifyContent="flex-start" spacing={guideAlign === 'right' ? 1 : null}>
            {title && <Grid item container direction="row" justifyContent={guideAlign === 'left' ? "flex-start" : "flex-end"} alignItems="center">
                <Grid item xs={6} container direction={isRightAlign ? "row" : null} justifyContent={isRightAlign ? "flex-end" : "flex-start"}>
                    <Typography variant="h5">{title !== 'empty' ? title : ""} {guideId && <Tooltip title={t("common.guide.tooltipQm")}><HelpOutlineIcon style={{marginBottom: "-4px"}} onClick={(e) => toggleHelpEvent(e)} /></Tooltip>}</Typography>
                </Grid>
                {elAction && <Grid container item xs={6} direction="row" justifyContent="flex-end" alignItems="center">
                    <Grid item>{elAction}</Grid>
                </Grid>}
            </Grid>}

            {guideId && openHelp && title && <Grid container item direction="row" justifyContent={guideAlign === 'left' ? "flex-start" : "flex-end"} alignItems="center">
                <Grid item xs={6} >
                    <C1UserGuide locale={t} information={userGuide?.find(e => e.cmguComponentId === guideId)?.cmguGuide} onHide={() => setOpenHelp(false)} />
                </Grid>
            </Grid>}

            {children && <Grid item xs={12}>{children}</Grid>}

        </Grid>
    );
}

export default C1TabInfoContainer;