import { Grid } from "@material-ui/core";
import C1DateTimeField from "app/c1component/C1DateTimeField";
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import C1TextArea from "app/c1component/C1TextArea";
import React from "react";

const AccountRemarkPopup = ({ popUp, setPopUp, data, locale }) => {

    return (
        <>
            <C1PopUp
                title={locale("listing:remarks.details")}
                openPopUp={popUp}
                setOpenPopUp={setPopUp}
            >
                <Grid container spacing={2}>
                    <Grid container item xs={12} direction='column'>
                        <C1InputField
                            name="tckMstRemarkType.rtDesc"
                            label={locale("listing:remarks.rmkType")}
                            value={data[1]}
                            disabled={true}
                        />
                        <C1TextArea
                            name="arRemark"
                            label={locale("listing:remarks.rmk")}
                            value={data[2]}
                            disabled={true}
                        />
                        <C1DateTimeField
                            name="atDtCreate"
                            label={locale("listing:remarks.rmkDtCreate")}
                            value={data[3]}
                            disabled={true}
                        />
                        <C1InputField
                            name="atUidLupd"
                            label={locale("listing:remarks.rmkUidLupd")}
                            value={data[4]}
                            disabled={true}
                        />
                    </Grid>
                </Grid>
            </C1PopUp>
        </>
    )
}

export default AccountRemarkPopup