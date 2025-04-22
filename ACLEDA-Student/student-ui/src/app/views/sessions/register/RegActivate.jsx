import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { makeStyles } from "@material-ui/core/styles";
import { CardContent, Card, CardHeader, Typography } from "@material-ui/core";
import clsx from "clsx";
import { useTranslation } from "react-i18next";
import { MatxLoading } from "matx";

import mpwtlogo from "../../../MatxLayout/Layout2/sample_stamp.jpg";

import useHttp from "app/c1hooks/http";
import { isEmpty } from "app/c1utils/utility";




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


const RegActivate = () => {
    const { t } = useTranslation(["register", "common"]);
    let { token } = useParams();

    const [loading, setLoading] = useState(true);
    const classes = useStyles();

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, res, error, sendRequest } = useHttp();
    const [activation, setActivation] = useState({});
    const [activationError, setActivationError] = useState("");


    //for loading the supporting documents
    useEffect(() => {
        sendRequest(`/api/register/activate/${token}`, "activate", "get");
        // eslint-disable-next-line
    }, [token]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            setActivation({ ...activation, ...res.data.data });
        }


        if (error) {
            setActivationError(error.err.msg);
            setLoading(false);
        }


        // eslint-disable-next-line
    }, [isLoading, res, error])


    return (
        loading && !activation ? <MatxLoading /> : <React.Fragment>
            <div className={clsx("relative w-full", classes.root)}>
                <div className="flex justify-between items-center h-full">
                    <div className="flex items-center h-full">
                        <img className="h-32" src={mpwtlogo} alt="" />
                        <span className={clsx("font-medium text-24 mx-4", classes.brandText)}>PortEDI</span>
                    </div>
                </div>
            </div>

            <Card className={classes.card}>
                <CardHeader title={<Typography variant="h5" color="textPrimary" component="p">
                    {isEmpty(activation) && activationError ? t("register.activate.notFound.header") : activation && activation.actStatus === 'A' ? t("register.activate.done.header") : t("register.activate.success.header")}
                </Typography>}>

                </CardHeader>
                <CardContent>
                    <Typography variant="body2" color="textPrimary" component="p">
                        {isEmpty(activation) ? activationError : activation && activation.actStatus === 'A' ? t("register.activate.done.msg") : t("register.activate.success.msg")}
                    </Typography>
                </CardContent>
            </Card>

        </React.Fragment >

    );
};

export default RegActivate;