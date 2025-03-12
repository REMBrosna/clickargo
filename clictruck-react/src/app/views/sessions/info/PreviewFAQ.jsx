import React, { useEffect, useState } from 'react';
import { AppBar, Toolbar } from "@material-ui/core";
import { useParams } from "react-router-dom";
import { makeStyles } from "@material-ui/core/styles";
import QuickGuide from './pdf/Quick Start Guide.pdf'
import UserGuide from './pdf/User Quide.pdf';
import PortClearanceGuide from './pdf/Port Clearance Quick Start Guide.pdf'
import ICACrewGuide from './pdf/ICA Crew Service User Guide.pdf'
import clicdo from "app/MatxLayout/Layout2/cliclogo.png";
import clsx from "clsx";

const useStyles = makeStyles((theme) => ({
    root: {
        height: '100%',
        width: '100%'
    },
    headerTitle: {
        width: '500px',
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
    const { fileName } = useParams();
    const [pdf, setPdf] = useState("");
    const [title, setTitle] = useState("");

    useEffect(() => {
        switch (fileName) {
            case 'quickGuide':
                setPdf(QuickGuide);
                setTitle("Quick Start Guide")
                break;

            case 'userGuide':
                setPdf(UserGuide);
                setTitle("User Guide")
                break;

            case 'portClearanceGuide':
                setPdf(PortClearanceGuide);
                setTitle("Port Clearance Quick Start Guide")
                break;

            case 'ICACrewGuide':
                setPdf(ICACrewGuide);
                setTitle("ICA CREW Services User Guide")
                break;

            default:
                break;
        }
    }, [fileName])

    return (
        <React.Fragment>
            <div className={classes.root}>
                <AppBar position="fixed">
                    <Toolbar>
                        <div className="flex items-center h-full">
                            <img className="h-32" src={clicdo} alt="" />
                            <span className={clsx("font-medium text-24 mx-4", classes.brandText)}>ClickDO</span>
                        </div>
                    </Toolbar>
                </AppBar>

                <div className={classes.pdfContent}>
                    <h4 className={classes.headerTitle}>{title}</h4>
                    <iframe title="Preview FAQ" className={classes.iframe} src={pdf} frameBorder="0"></iframe>
                </div>

            </div>
        </React.Fragment>

    );
}