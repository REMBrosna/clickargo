import React from "react";
import {AppBar, MenuItem, Toolbar} from "@material-ui/core";
import LanguageSelector from "../../../MatxLayout/SharedCompoents/LanguageSelector";
import Footer from "../../../MatxLayout/SharedCompoents/Footer";
import {Grid} from "@material-ui/core";
import C1InputField from "app/c1component/C1InputField";
import {makeStyles} from "@material-ui/core/styles";
import C1Button from "../../../c1component/C1Button";
import PreviewPdfForm from "./PreviewPdfForm";
import C1SelectField from "../../../c1component/C1SelectField";
import {useTranslation} from "react-i18next";

const useStylesCustom = makeStyles(() => ({
    marginTop: {
        marginTop:'100px',
        width: '60%',
        margin: '0px auto',
        marginBottom:'20px',
    },
    root: {
        width: '60%',
        margin: '0px auto',
        marginBottom:'20px',
        marginTop:'20px',
    },
    pdfContent: {
        height:'100%',
        width: '100%',
        marginTop: '30px',
        margin:'0px auto'
    }
}));


const SearchCriteriaForm = ({
         errors,
         onSearchHandle,
         certificateNoChangeHandle,
         imoChangeHandle,
         selectChange,
         certificateData,
         vesselNameChangeHandle,
         isInquiry
     }) => {
    const classes = useStylesCustom();
    const { t } = useTranslation(["certificate"]);

    return (
        <React.Fragment>
            <AppBar position="fixed">
                <Toolbar>
                    <LanguageSelector />
                </Toolbar>
            </AppBar>
            <div className={classes.marginTop}>
                <h1 style={{textAlign: 'center'}}>{t("certificate:certificate.title")}</h1>
                <Grid container spacing={2}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={t("certificate:certificate.fields.certificateNo")}
                            error={!!errors.certificateNo}
                            helperText={errors.certificateNo ?? null}
                            name="certificateNo"
                            onChange={certificateNoChangeHandle}
                            required={true}
                        />
                        <C1InputField
                            label={t("certificate:certificate.fields.vesselName")}
                            name="vesselName"
                            onChange={vesselNameChangeHandle}
                            required={false}
                        />
                        <C1InputField
                            label={t("certificate:certificate.fields.imoNo")}
                            name="imoNo"
                            onChange={imoChangeHandle}
                            required={false}
                        />
                        <C1SelectField
                            required
                            name="certificateType"
                            label={t("certificate:certificate.fields.certificateType")}
                            onChange={selectChange}
                            error={!!errors.certificateType}
                            helperText={errors.certificateType ?? null}
                        >
                            <MenuItem value="EP" key="EP"> Entry Permit </MenuItem>
                            <MenuItem value="DOS" key="DOS"> Declaration of Security </MenuItem>
                            <MenuItem value="PAS" key="PAS"> Ship Pre-Arrival Security Information Notice </MenuItem>
                            <MenuItem value="QUD" key="QUD"> Quarantine for Departure </MenuItem>
                            {/* <MenuItem value="QUA" key="QUA"> Quarantine for Arrival </MenuItem> */}
                            <MenuItem value="FRQ" key="FRQ"> Free Pratique </MenuItem>
                            <MenuItem value="POC" key="POC"> Port Clearance </MenuItem>
                            <MenuItem value="SSCC" key="SSCC"> Ship Sanitation Control Certificate </MenuItem>
                            <MenuItem value="SSCEC" key="SSCEC"> Ship Sanitation Control Exemption Certificate </MenuItem>
                        </C1SelectField>
                        <C1Button
                            text={t("certificate:certificate.fields.search")}
                            onClick={onSearchHandle}
                        />
                    </Grid>

                </Grid>
                <div className={classes.pdfContent}>
                    {
                        isInquiry ? "" : certificateData === null || certificateData === undefined || certificateData === '' ? <div><h1 style={{textAlign: 'center'}}>{t("certificate:certificate.message.certificateNotFound")}</h1></div>: <PreviewPdfForm data = {certificateData}/>
                    }
                </div>
            </div>

            <Footer />
        </React.Fragment>
    );
};


export default SearchCriteriaForm;