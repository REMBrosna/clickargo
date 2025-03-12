import { MenuItem, Typography } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import AccountBalanceOutlinedIcon from "@material-ui/icons/AccountBalanceOutlined";
import BuildOutlinedIcon from "@material-ui/icons/BuildOutlined";
import SelectAllOutlinedIcon from "@material-ui/icons/SelectAllOutlined";
import React, { useEffect, useState } from "react";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import { useStyles } from "app/c1component/C1Styles";
import C1TabContainer from "app/c1component/C1TabContainer";
import { AccountTypes, FINANCING_OPTIONS } from "app/c1utils/const";
import { Uint8ArrayToString, getValue, isEditable } from "app/c1utils/utility";
import C1FileUpload from "app/c1component/C1FileUpload";
import useHttp from "app/c1hooks/http";
import ImageOutlinedIcon from "@material-ui/icons/ImageOutlined";
import MonetizationOnOutlinedIcon from "@material-ui/icons/MonetizationOnOutlined";

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

  const parts = inputData?.bankDetails?.acfgVal?.split(":");
  const bankCode = parts?.[0];
  const bankNo = parts?.[1];
  const bankName = parts?.[2];

  const financeOptions = [
    { value: FINANCING_OPTIONS.NF.code, desc: FINANCING_OPTIONS.NF.desc },
    { value: FINANCING_OPTIONS.BC.code, desc: FINANCING_OPTIONS.BC.desc },
    { value: FINANCING_OPTIONS.OPM.code, desc: FINANCING_OPTIONS.OPM.desc },
  ];

  const [bgImage, setBgImage] = useState({ show: false, data: null });
  const [logoImage, setLogoImage] = useState({ show: false, data: null });
  const [bgImgInvalidErr, setBgImgInvalidErr] = useState(null);
  const [logoImgInvalidErr, setLogoImgInvalidErr] = useState(null);

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
              icon={<BuildOutlinedIcon />}
              title={locale("opadmin:title.sageTitle")}
            >
              <Grid item xs={12}>
                <C1InputField
                  label={locale("companyDetails.sageAccpacId")}
                  name="sageAccpacId"
                  required
                  disabled={
                    isDisabled || null == inputData?.accnDetails?.accnId
                  }
                  onChange={handleInputChange}
                  value={getValue(inputData?.sageAccpacId)}
                  inputProps={{ maxLength: 256 }}
                  error={errors["accnDetails.sageAccpacId"] !== undefined}
                  helperText={
                    errors["accnDetails.sageAccpacId"] !== undefined
                      ? locale(
                          `validations.${errors["accnDetails.sageAccpacId"]}`
                        )
                      : ""
                  }
                />
              </Grid>
            </C1CategoryBlock>
            <C1CategoryBlock
              icon={<MonetizationOnOutlinedIcon />}
              title={locale("opadmin:title.financing")}
            >
              <Grid item xs={12}>
                <C1SelectAutoCompleteField
                  label={locale("companyDetails.fnOpts")}
                  name="financeOptions"
                  disabled={isDisabled}
                  required
                  onChange={(e, name, value) =>
                    handleInputChange({
                      target: { name, value: value?.value },
                    })
                  }
                  value={getValue(inputData?.financeOptions) || ""}
                  isServer={false}
                  optionsMenuItemArr={financeOptions?.map((item, i) => {
                    return { value: item.value, desc: item.desc };
                  })}
                  error={errors["accnDetails.financeOptions"] !== undefined}
                  helperText={
                    errors["accnDetails.financeOptions"] !== undefined
                      ? locale(
                          `validations.${errors["accnDetails.financeOptions"]}`
                        )
                      : ""
                  }
                />
              </Grid>
              {showFinancer && (
                <Grid item xs={12}>
                  <C1SelectAutoCompleteField
                    label={locale("companyDetails.financer")}
                    name="financer"
                    disabled={isDisabled}
                    required
                    onChange={(e, name, value) =>
                      handleInputChange({
                        target: { name, value: value?.value },
                      })
                    }
                    value={getValue(inputData?.financer) || ""}
                    isServer={true}
                    options={{
                      url: "/api/v1/clickargo/clictruck/financing/financers",
                      id: "value",
                      desc: "desc",
                      isCache: false,
                    }}
                    error={errors["accnDetails.financer"] !== undefined}
                    helperText={
                      errors["accnDetails.financer"] !== undefined
                        ? locale(
                            `validations.${errors["accnDetails.financer"]}`
                          )
                        : ""
                    }
                  />
                </Grid>
              )}
            </C1CategoryBlock>
          </Grid>
        </Grid>

        {(isAccnTo || isAccnSp) && (
          <Grid item lg={4} md={6} xs={12} style={{ marginBottom: "20px" }}>
            <Grid className={classes.gridContainer}>
              <C1CategoryBlock
                icon={<AccountBalanceOutlinedIcon />}
                title={locale("opadmin:title.bankTitle")}
              >
                <Grid item xs={12}>
                  <C1InputField
                    label={locale("opadmin:bankList.bankCode")}
                    onChange={handleInputChange}
                    value={bankCode}
                    name="bankCode"
                    disabled={true}
                  />
                  <C1InputField
                    label={locale("opadmin:bankList.bankNo")}
                    onChange={handleInputChange}
                    value={bankNo}
                    name="bankNo"
                    disabled={true}
                  />
                  <C1InputField
                    label={locale("opadmin:bankList.bankName")}
                    onChange={handleInputChange}
                    value={bankName}
                    name="bankName"
                    disabled={true}
                  />
                </Grid>
              </C1CategoryBlock>
              <Grid item xs={12}>
                <Typography variant="subtitle2">
                  {locale("admin:account.info.config")}
                </Typography>
              </Grid>
            </Grid>
          </Grid>
        )}
        {(isAccnCoFf || isAccnSp) && (
          <Grid item lg={4} md={6} xs={12} style={{ marginBottom: "20px" }}>
            <Grid className={classes.gridContainer}>
              <C1CategoryBlock
                icon={<SelectAllOutlinedIcon />}
                title={locale("opadmin:title.staticVaTitle")}
              >
                <Grid item xs={12}>
                  <C1InputField
                    label={locale("listing:staticVAList.staticVaNumber")}
                    onChange={handleInputChange}
                    value={getValue(inputData?.staticVa?.acfgVal)}
                    name="staticVa.acvgVal"
                    disabled={true}
                  />
                </Grid>
              </C1CategoryBlock>
              <Grid item xs={12}>
                <Typography variant="subtitle2">
                  {locale("admin:account.info.config")}
                </Typography>
              </Grid>
            </Grid>
          </Grid>
        )}

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
