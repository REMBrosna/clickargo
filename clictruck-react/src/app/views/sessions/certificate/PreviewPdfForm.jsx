import React from 'react';
import {makeStyles} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";

const useStyles = makeStyles(() => ({
    root: {
        height:'100%' ,
        width: '100%'
    },
    headerTitle: {
        width:'100%',
        marginBottom:'20px'
    },
    iframe: {
        width:'100%',
        height:'100%',
        minHeight: '768px'
    }

}));

const PreviewPdfForm = ({
    data
}) => {
    const classes = useStyles();
    const { t } = useTranslation(["certificate"]);
    return (
        <React.Fragment>
            <h4 className={classes.headerTitle}>{data && data.certName}</h4>
            {data.certData === null || data.certData === undefined || data.certData === ''? <div><h1 style={{textAlign: 'center'}}>{t("certificate:certificate.message.certificateNotFound")}</h1></div>: <iframe className={classes.iframe} src={`data:application/pdf;base64,${data.certData}`} frameBorder="0"/> }
        </React.Fragment>
    );
}

export default PreviewPdfForm;