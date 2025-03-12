import { Grid, Button, Tooltip } from "@material-ui/core";
import C1PopUp from "app/c1component/C1PopUp";
import C1InputField from "app/c1component/C1InputField";
import React, { useState } from "react";
import NearMeOutlined from "@material-ui/icons/NearMeOutlined";
import { dialogStyles } from "app/c1utils/styles";

const CommentsOfRateTablePopUp = ({popUp, setPopUp, action, handleAction, commentHandler, commentValue }) => {

    const [openPopUp, setOpenPopUp] = useState(false);
    // const togglePopUp = () => {setOpenPopUp(openPopUp? false : true)};

    const dialogClasses = dialogStyles();
    const elAction =
        <Tooltip title={"Submit"} className={dialogClasses.dialogButtonSpace} >
            <Button onClick={
                () => handleAction(action)
                } >
                <NearMeOutlined color="primary" fontSize="large" />
            </Button>
        </Tooltip>
    
    const title = action ? action?.charAt(0).toUpperCase() + action?.slice(1).toLowerCase() + " of Rate Table" : "Comment of Rate Table";
    return(
        <>
        <C1PopUp
            title={title}
            openPopUp={popUp}
            setOpenPopUp={setPopUp}
            actionsEl={elAction}
        >
            <Grid container spacing={2}>
                <Grid container item xs={12} direction='column'>
                    <C1InputField
                        label="Comments"
                        name= "comments"
                        onChange={e => commentHandler(e.target.value)}
                        value={commentValue}
                        multiline
                        rows={5}
                    />
                </Grid>
            </Grid>
        </C1PopUp>
        </>
    )
}

export default CommentsOfRateTablePopUp