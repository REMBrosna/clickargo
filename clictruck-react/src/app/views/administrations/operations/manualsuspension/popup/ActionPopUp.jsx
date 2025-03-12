import { Grid } from "@material-ui/core";
import NearMeIcon from '@material-ui/icons/NearMeOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1IconButton from "app/c1component/C1IconButton";
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import C1TextArea from "app/c1component/C1TextArea";
import useHttp from "app/c1hooks/http";

const ActionPopUp = (props) => {

    const { t } = useTranslation(["buttons"]);

    const { openPopUp, togglePopUp, action, accnId, setRefresh, setRefreshPage } = props;
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isOpen, setOpen] = useState(openPopUp);
    const [accnData, setAccnData] = useState();
    const [remark, setRemark] = useState("");

    useEffect(() => {
        // console.log("POPUP", accnId)
        if (openPopUp && accnId) {
            const url = "/api/co/ccm/entity/accn/" + accnId;
            const urlId = "getForPopUp";
            const type = "GET";
            const body = "";
            sendRequest(url, urlId, type, body);
        }
    }, [openPopUp, accnId])

    useEffect(() => {
        if (res && !error) {
            switch (urlId) {
                case "getForPopUp": {
                    setAccnData(res.data);
                    console.log(res.data);
                    break;
                }
                case "submitAction": {
                    setRefresh(true);
                    if (openPopUp) togglePopUp();
                    setRefreshPage(1);
                    // window.location.reload();
                    break;
                }
                default: break;
            }
        }


    }, [isLoading, res, error, urlId]);

    const doActionHandler = (action) => {
        console.log("do", action.toLowerCase());
        if (action && accnData) {

            const urlId = "submitAction";
            const type = "PUT";

            if (action.toLowerCase() === "suspend") {
                const url = "/api/v1/clickargo/clictruck/account/accnSuspend";
                const body = { ...accnData, history: null, remarks: remark, accnStatus: "S" };
                console.log(url, urlId, type, body);
                sendRequest(url, urlId, type, body);
            } else if (action.toLowerCase() === "resume" || action.toLowerCase() === "activate") {
                const url = "/api/v1/clickargo/clictruck/account/accnResumption";
                const body = { ...accnData, history: null, remarks: remark, accnStatus: "A" };
                console.log(url, urlId, type, body);
                sendRequest(url, urlId, type, body);
            }
        }
    }

    const submitButton =
        <C1IconButton tooltip={t("buttons:submit")} childPosition="right">
            <NearMeIcon color="primary" fontSize="large" onClick={() => { doActionHandler(action) }} />
        </C1IconButton>
        ;

    console.log("PopUp", accnId);
    return (
        <React.Fragment>
            <C1PopUp
                title={`${action} Account`}
                openPopUp={openPopUp}
                setOpenPopUp={togglePopUp}
                maxWidth={'md'}
                actionsEl={submitButton}
            >
                <Grid container spacing={3}>
                    <Grid container item md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1CategoryBlock title={"Account Details"} />
                            <C1InputField
                                label={"Account Name"}
                                value={accnData?.accnName}
                                disabled={true}
                            />
                            <C1InputField
                                label={"Account Type"}
                                value={accnData?.TMstAccnType?.atypDescription}
                                disabled={true}
                            />
                        </Grid>
                    </Grid>
                    <Grid container item md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1CategoryBlock title={"Comments"} />
                            <C1TextArea
                                label={"Comments"}
                                defaultValue={remark}
                                onBlur={(e) => { setRemark(e.target.value) }}
                                rows={5}
                            />
                        </Grid>
                    </Grid>
                </Grid>
            </C1PopUp>
        </React.Fragment>
    )
}

export default ActionPopUp;