import React, { useState } from 'react';
import { Grid, Button, Icon, Typography } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { Uint8ArrayToString } from "app/c1utils/utility";


const CompanyStampDetails = ({ inputData, handleImageChange, isSubmitting, errors }) => {

    const { t } = useTranslation(["register"]);
    const [fileSrc, setFileSrc] = useState();
    var imgRef = React.createRef();

    const onFileChangeHandler = (e) => {
        e.preventDefault();
        console.log("onFileChangeHandler", e);
        var file = e.target.files[0];
        if (!file) {
            // didn't select file
            return;
        }
        setFileSrc(URL.createObjectURL(e.target.files[0]));

        const fileReader = new FileReader();
        fileReader.readAsArrayBuffer(e.target.files[0]);
        fileReader.onload = e => {
            const uint8Array = new Uint8Array(e.target.result);
            var imgStr = Uint8ArrayToString(uint8Array);
            // console.log("imgStr 2 ", imgStr.length, imgStr,);
            var base64Sign = btoa(imgStr);
            setFileSrc('data:image/png;base64,' + base64Sign);
            handleImageChange(file.name, uint8Array.length, base64Sign);
        };
    };

    return (
        <Grid container spacing={3} alignItems="flex-start">
            <Grid item lg={6} md={6} xs={12} >
                <Grid container alignItems="flex-start">
                    <Grid item xs={12}>
                        <div>
                            <label htmlFor="upload-multiple-file">
                                <Button
                                    className="capitalize"
                                    color="primary"
                                    component="span"
                                    variant="contained"
                                    disabled={isSubmitting}
                                >
                                    <div className="flex items-center">
                                        <Icon className="pr-8">cloud_upload</Icon>
                                        <span>{t("companyStamp.btnUpload")}</span>
                                    </div>
                                </Button>
                            </label>
                            <input
                                className="hidden"
                                onChange={onFileChangeHandler}
                                id="upload-multiple-file"
                                disabled={isSubmitting}
                                type="file"
                                name="STP"
                                single="true" />
                        </div>
                        <div>
                            <p> {t("companyStamp.stampPhotoSpec1")} <br />
                                * {t("companyStamp.stampPhotoSpec2")} <br />
                                * {t("companyStamp.stampPhotoSpec3")} </p>
                        </div>
                    </Grid>
                </Grid>
            </Grid>
            <Grid item lg={6} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3}>
                    <Grid item xs={12} >
                        <div>{t("companyStamp.preview")}</div>
                        {errors ? errors.map((e, index) => <Typography key={index} variant="subtitle2" gutterBottom color="error">{e}</Typography>) : <img ref={imgRef} alt={inputData.accnLogo.attData} src={fileSrc} width="300px"></img>}

                    </Grid>

                </Grid>
            </Grid>
        </Grid>
    );
}

export default CompanyStampDetails;