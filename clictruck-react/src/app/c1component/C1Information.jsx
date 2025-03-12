import Grid from "@material-ui/core/Grid";
import { makeStyles } from "@material-ui/core/styles";
import React from "react";

import C1OutlinedDiv from "./C1OutlinedDiv";

const useStyles = makeStyles(({ palette, ...theme }) => ({

    card: {
        width: "1200!important",
        borderRadius: 12,
        margin: "5rem",
    },

    root: {
        backgroundColor: '#3C77D0',
        borderColor: palette.divider,
        display: "table",
        height: "var(--topbar-height)",
        borderBottom: "1px solid transparent",
        paddingTop: "1rem",
        paddingBottom: "1rem",
        zIndex: 98,
        paddingLeft: "1.75rem",
        [theme.breakpoints.down("sm")]: {
            paddingLeft: "1rem",
        },
    },

    brandText: {
        color: palette.primary.contrastText,
    },

}));


const C1Information = ({ information }) => {

    return (
        <React.Fragment>
            <C1OutlinedDiv>
                {information}
            </C1OutlinedDiv>
        </React.Fragment >

    );
};

export default C1Information;