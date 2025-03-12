import React from "react";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectField from "app/c1component/C1SelectField";
import C1InputField from "app/c1component/C1InputField";
import C1DateTimeField from "app/c1component/C1DateTimeField";
import { Grid } from "@material-ui/core";

const RemarkPopUp = ({popUp, setPopUp}) => {
    return(
        <C1PopUp
            title={"Remarks Details"}
            openPopUp={true}
            setOpenPopUp={setPopUp}
        >
            <Grid container spacing={2}>
                <Grid container item xs={12} direction='column'>
                    <C1SelectField
                        name="a"
                        label="Remark Type"
                        />
                    <C1InputField
                        name="b"
                        label="Remarks"
                        multiline
                        rows={3}
                        />
                    <C1DateTimeField
                        name="c"
                        label="Remarks Date"
                        />
                    <C1InputField
                        name="d"
                        label="Remark By"
                        />
                </Grid>
            </Grid>

        </C1PopUp>
    )
}

export default RemarkPopUp;