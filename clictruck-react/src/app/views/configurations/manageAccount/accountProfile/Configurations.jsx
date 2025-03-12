import React, { useEffect, useState } from "react";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import { useStyles } from "app/c1component/C1Styles";
import C1TabContainer from "app/c1component/C1TabContainer";
import { AccountTypes, FINANCING_OPTIONS } from "app/c1utils/const";
import { Uint8ArrayToString, getValue, isEditable } from "app/c1utils/utility";
import C1FileUpload from "app/c1component/C1FileUpload";
import useHttp from "app/c1hooks/http";
import ImageOutlinedIcon from "@material-ui/icons/ImageOutlined";
import MonetizationOnOutlinedIcon from "@material-ui/icons/MonetizationOnOutlined";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { Grid, Checkbox, MenuItem, Typography  } from "@material-ui/core";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { makeStyles } from '@material-ui/core/styles';

const useValueAddStyles = makeStyles({
  checkbox: {
      paddingLeft: '20px',
      paddingTop: '10px',
  }
});


const Configurations = ({
  inputData,
  setInputData,
  handleInputChange,
  viewType,
  errors,
  locale,
  enableFinancing,
  showFinancer,
}) => {
  const classes = useStyles();
  const vasClasses = useValueAddStyles();
  let isDisabled = isEditable(viewType) || !enableFinancing;

  const { isLoading, res, error, urlId, sendRequest } = useHttp();

  const isAccnCoFf =
    inputData?.accnDetails?.TMstAccnType.atypId ===
      AccountTypes.ACC_TYPE_CO.code ||
    inputData?.accnDetails?.TMstAccnType.atypId ===
      AccountTypes.ACC_TYPE_FF.code;
  const isAccnTo =
    inputData?.accnDetails?.TMstAccnType.atypId ===
    AccountTypes.ACC_TYPE_TO.code;
  const isAccnSp =
    inputData?.accnDetails?.TMstAccnType.atypId ===
    AccountTypes.ACC_TYPE_SP.code;


  const [bgImage, setBgImage] = useState({ show: false, data: null });
  const [logoImage, setLogoImage] = useState({ show: false, data: null });
  const [bgImgInvalidErr, setBgImgInvalidErr] = useState(null);
  const [logoImgInvalidErr, setLogoImgInvalidErr] = useState(null);

  const handleInputDeepChange = (e) => {
  
    const name = e.target.name;
    const checked = e.target.checked;
    console.log("name:", name, "checked", checked);
    
    let tmp = { ...inputData, ckAccn:(inputData?.ckAccn||{}) };
    tmp = { ...tmp, ...deepUpdateState(tmp, name, checked) }

    console.log("tmp:", tmp);

    setInputData(tmp);
  };

  const handleUploadImageFileChange = (e, name) => {
    let file = e.target.files[0];
    if (["image/png", "image/jpg", "image/jpeg"].includes(file?.type)) {
      if (name === "background") setBgImgInvalidErr(null);
      else setLogoImgInvalidErr(null);
      const fileReader = new FileReader();
      fileReader.readAsArrayBuffer(e.target.files[0]);
      fileReader.onload = (e) => {
        const uint8Array = new Uint8Array(e.target.result);
        if (uint8Array.byteLength === 0) {
          return;
        }
        let imgStr = Uint8ArrayToString(uint8Array);
        let base64Sign = btoa(imgStr);
        if (name === "background")
          setInputData({
            ...inputData,
            bgImageWl: {
              ...inputData?.bgImageWl,
              data: base64Sign,
              filename: file.name,
            },
          });
        else
          setInputData({
            ...inputData,
            companyLogo: {
              ...inputData?.companyLogo,
              aattStore: base64Sign,
              aattName: file.name,
            },
          });
      };
    } else {
      if (name === "background")
        setBgImgInvalidErr(locale("opadmin:images.errorNonImage"));
      else setLogoImgInvalidErr("Only images allowed");
    }
  };

  const handleToggleImageShow = (e, name) => {
    if (name === "background") {
      if (!bgImage?.data) {
        sendRequest(
          `/api/v1/clickargo/manageaccn/bglogin/${inputData?.accnDetails?.accnId}`,
          "getBgImage"
        );
      } else {
        setBgImage({ ...bgImage, show: !bgImage.show });
      }

      //setShowBgImage(!showBgImage);
    } else {
      if (!logoImage?.data) {
        sendRequest(
          `/api/v1/clickargo/manageaccn/logo/${inputData?.accnDetails?.accnId}`,
          "getLogoImage"
        );
      } else {
        setLogoImage({ ...logoImage, show: !logoImage.show });
      }
    }
  };

  useEffect(() => {
    if (!isLoading && !error && res) {
      if (urlId === "getBgImage") {
        setBgImage({
          ...bgImage,
          show: res?.data ? true : false,
          data: res?.data,
        });
        //set the inputData to res?.data in case the user wants to save withouth re-uploading anything
        setInputData({
          ...inputData,
          bgImageWl: { ...inputData?.bgImageWl, data: res?.data },
        });
      } else {
        setLogoImage({
          ...logoImage,
          show: res?.data ? true : false,
          data: res?.data,
        });
      }
    }
  }, [urlId, isLoading, res, error]);

  return (
    <React.Fragment>
      <C1TabContainer>
        <Grid item lg={4} md={6} xs={12} style={{ marginBottom: "50px" }}>
          <Grid className={classes.gridContainer}>

            <C1CategoryBlock
              icon={<MonetizationOnOutlinedIcon />}
              title={"Value Added Services"}
            >
              <Grid item xs={12}>
              <FormControlLabel
                  control={
                    <Checkbox
                      checked={inputData?.ckAccn?.caccnWhatsapp || false}
                      onChange={(e) => {
                        handleInputDeepChange(e);
                      }}
                      name={"ckAccn.caccnWhatsapp"}
                    />
                  }
                  className={vasClasses.checkbox}
                  label={"Whatsapp Notification"}
                  disabled={isDisabled}
                />
              </Grid>
              <Grid item xs={12}>
              {false && <FormControlLabel
                  control={
                    <Checkbox
                      checked={inputData?.ckAccn?.caccnSms || false}
                      onChange={(e) => {
                        handleInputDeepChange(e);
                      }}
                      name={"ckAccn.caccnSms"}
                    />
                  }
                  className={vasClasses.checkbox}
                  label={"SMS Notification"}
                  disabled={isDisabled}
                /> }
              </Grid>
              <Grid item xs={12}>
              <FormControlLabel
                  control={
                    <Checkbox
                    checked={inputData?.ckAccn?.caccnCo2x || false}
                      onChange={(e) => {
                        handleInputDeepChange(e);
                      }}
                      name={"ckAccn.caccnCo2x"}
                    />
                  }
                  className={vasClasses.checkbox}
                  label={"CO2X Monitoring"}
                  disabled={isDisabled}
                />
              </Grid>
              <Grid item xs={12}>
              {false && <FormControlLabel
                  control={
                    <Checkbox
                    checked={inputData?.ckAccn?.caccnRoutePlanning || false}
                      onChange={(e) => {
                        handleInputDeepChange(e);
                      }}
                      name={"ckAccn.caccnRoutePlanning"}
                    />
                  }
                  className={vasClasses.checkbox}
                  label={"Route Planning"}
                  disabled={isDisabled}
                />}
              </Grid>

            </C1CategoryBlock>
          </Grid>
        </Grid>


        {/* For uploading background image and logo */}
        {(isAccnTo || isAccnSp) && (
          <Grid item lg={4} md={6} xs={12} style={{ marginBottom: "20px" }}>
            <Grid className={classes.gridContainer}>
              <C1CategoryBlock
                icon={<ImageOutlinedIcon />}
                title={locale("opadmin:title.images")}
              >
                <Grid item xs={12}>
                  <C1FileUpload
                    value={getValue(inputData?.bgImageWl?.filename)}
                    inputProps={{
                      placeholder: locale("listing:attachments.nofilechosen"),
                    }}
                    fileChangeHandler={(e) =>
                      handleUploadImageFileChange(e, "background")
                    }
                    disabled={isDisabled}
                    label={locale("listing:attachments.browse").toUpperCase()}
                    inputLabel={locale("opadmin:images.loginBackgroundImage")}
                    errors={bgImgInvalidErr}
                    helperText={
                      bgImgInvalidErr || locale("opadmin:images.bgImageText")
                    }
                  />
                  <Typography variant="subtitle2" color="primary">
                    <a
                      href="#"
                      onClick={(e) => handleToggleImageShow(e, "background")}
                    >
                      {bgImage?.show
                        ? locale("opadmin:images.toggleHideImage")
                        : locale("opadmin:images.toggeleShowImage")}
                    </a>
                  </Typography>
                </Grid>
                {bgImage?.show && (
                  <Grid item xs={12}>
                    {bgImage?.data ? (
                      <div style={{ height: "100%", width: "100%" }}>
                        <img
                          src={`data:image/jpeg;base64,${bgImage?.data}`}
                          style={{
                            height: "100%",
                            width: "100%",
                            objectFit: "contain",
                          }}
                        />
                      </div>
                    ) : (
                      locale("opadmin:images.noImageAvailable")
                    )}
                  </Grid>
                )}
                <Grid item xs={12}>
                  <C1FileUpload
                    value={inputData?.companyLogo?.aattName}
                    inputProps={{
                      placeholder: locale("listing:attachments.nofilechosen"),
                    }}
                    fileChangeHandler={(e) =>
                      handleUploadImageFileChange(e, "logo")
                    }
                    disabled={isDisabled}
                    label={locale("listing:attachments.browse").toUpperCase()}
                    inputLabel={locale("opadmin:images.companyLogo")}
                    errors={logoImgInvalidErr}
                    helperText={logoImgInvalidErr}
                  />
                  <Typography variant="subtitle2" color="primary">
                    <a
                      href="#"
                      onClick={(e) => handleToggleImageShow(e, "logo")}
                    >
                      {logoImage?.show
                        ? locale("opadmin:images.toggleHideImage")
                        : locale("opadmin:images.toggeleShowImage")}
                    </a>
                  </Typography>
                </Grid>
                {logoImage?.show && (
                  <Grid item xs={12}>
                    {logoImage?.data ? (
                      <div style={{ height: "100%", width: "100%" }}>
                        <img
                          src={`data:image/jpeg;base64,${logoImage?.data}`}
                          style={{
                            height: "100%",
                            width: "100%",
                            objectFit: "contain",
                          }}
                        />
                      </div>
                    ) : (
                      locale("opadmin:images.noImageAvailable")
                    )}
                  </Grid>
                )}
              </C1CategoryBlock>
            </Grid>
          </Grid>
        )}
      </C1TabContainer>
    </React.Fragment>
  );
};

export default Configurations;
