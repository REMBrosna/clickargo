import React, { useEffect, useState } from 'react';
import { AppBar, Toolbar } from "@material-ui/core";
import useHttp from "../../../c1hooks/http";
import { useParams } from "react-router-dom";
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import cliclogo from "../../../MatxLayout/Layout2/cliclogo.png";

const useStyles = makeStyles((theme) => ({
    root: {
        height: '100%',
        width: '100%'
    },
    headerTitle: {
        width: '300px',
        marginBottom: '20px'
    },
    pdfContent: {
        height: '100%',
        width: '95%',
        marginTop: '80px',
        margin: '0px auto'
    },
    iframe: {
        width: '100%',
        height: '100%',
        minHeight: '768px'
    }

}));


export default function PreviewCertificate() {

    const classes = useStyles();
    const { certType, appId } = useParams();
    const [data, setData] = useState([]);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const reqBody = {
        appnId: appId,
        certificateType: certType.toUpperCase()
    };

    useEffect(() => {
        if (urlId === "previewPdf") {
            if (res && res.data && res.data) {
                setData(res.data);
            }
        }
    }, [isLoading, res, error, urlId]);

    useEffect(() => {
        if (reqBody.certificateType.toUpperCase() === "PAYMENT") {
            sendRequest("/api/certificate/print/payments/" + appId, "previewPdf", "POST");
        } else {
            sendRequest("/api/certificate/print/certificate", "previewPdf", "POST", reqBody);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    return (
        <React.Fragment>
            <div className={classes.root}>
                <AppBar position="fixed">
                    <Toolbar>
                        <div className="flex items-center h-full">
                            <img className="h-32" src={cliclogo} alt="" />
                            <span className={clsx("font-medium text-24 mx-4", classes.brandText)}>PortEDI</span>
                        </div>
                    </Toolbar>
                </AppBar>

                <div className={classes.pdfContent}>
                    <h4 className={classes.headerTitle}>{data && data.certName}</h4>
                    {
                        data === null ? <div /> :
                            <iframe title="Preview Certificate" className={classes.iframe} src={`data:application/pdf;base64,${data.certData}`} frameBorder="0" />
                    }
                </div>

            </div>

            <AppBar color="primary" position="static" className={classes.appbar}>
                <Toolbar className={clsx("flex items-center", classes.footer)}>
                    <div className="flex items-center container w-full">
                        <span className="m-auto"></span>
                        <p className="m-0">
                            Developed and Powered by <a href="https://guud.company/" target="_blank" rel="noopener noreferrer">Japanese Grant Aid</a>
                        </p>
                    </div>
                </Toolbar>
            </AppBar>

        </React.Fragment>

    );
}