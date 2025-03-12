import { AppBar, Grid, Toolbar, makeStyles } from "@material-ui/core";
import LanguageSelector from "app/MatxLayout/SharedCompoents/LanguageSelector";
import React, { useEffect, useState } from "react";
import cliclogo from "app/MatxLayout/Layout2/Clickargo.svg";
import { clsx } from "clsx";
import C1InputField from "app/c1component/C1InputField";
import { getValue, isEmpty } from "app/c1utils/utility";
import C1Button from "app/c1component/C1Button";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import C1TextArea from "app/c1component/C1TextArea";

const useStyles = makeStyles(({ palette, ...theme }) => ({
  brandText: {
    color: "#000", //palette.primary.contrastText,
  },
  marginTop: {
    marginTop: "100px",
    width: "60%",
    margin: "0px auto",
    marginBottom: "20px",
  },
}));

const AccnInquiry = () => {
  const { isLoading, isFormSubmission, res, error, urlId, sendRequest } =
    useHttp();

  const classes = useStyles();
  const [reqData, setReqData] = useState({
    accnRegTaxNo: null,
    airEmailReq: null,
    airRemarks: null,
  });
  const [validationError, setValidationError] = useState({ msg: null });
  const [warningProps, setWarningProps] = useState({ open: false, msg: "" });

  useEffect(() => {
    if (!isLoading && !error && res) {
      if (urlId === "submitRequest") {
        if (res?.data === "ok") {
          setWarningProps({
            ...warningProps,
            open: true,
            msg: "If the account registration/tax no. exists, request response will be sent to the specified email.",
          });
        }
      }
    }
  }, [urlId, isLoading, isFormSubmission, res, error]);

  const handleWarningPopup = () => {
    setWarningProps({ open: false, msg: "" });
    setReqData({
      ...reqData,
      accnRegTaxNo: null,
      airEmailReq: null,
      airRemarks: null,
    });
  };

  const handleInputChange = (e) => {
    const { name, value } = e?.target;
    setReqData({ ...reqData, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!isEmpty(validate())) {
      return;
    }

    sendRequest(
      `/api/v1/clickargo/clictruck/inquiry/accn`,
      "submitRequest",
      "post",
      { ...reqData }
    );
  };

  const validate = () => {
    let err = {};
    if (!reqData?.accnRegTaxNo) err.accnRegTaxNo = "This field is required.";

    if (!reqData?.airEmailReq) err.airEmailReq = "This field is required";

    if (!reqData?.airRemarks) err.airRemarks = "This field is required";

    if (reqData?.airEmailReq && !isEmail(reqData?.airEmailReq)) {
      err.airEmailReq = "Please enter a valid email.";
    }

    setValidationError({ ...validationError, ...err });
    return err;
  };

  const isEmail = (email) =>
    /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(email);

  return (
    <React.Fragment>
      <AppBar position="fixed" style={{ backgroundColor: "#fff" }}>
        <Toolbar>
          <div className="flex items-center h-full">
            <img className="h-32" src={cliclogo} alt="" />
            <span
              className={clsx("font-medium text-24 mx-4", classes.brandText)}
            >
              Clickargo
            </span>
          </div>
          <LanguageSelector />
        </Toolbar>
      </AppBar>
      <div className={classes.marginTop}>
        <h1 style={{ textAlign: "center" }}>Account Inquiry</h1>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <C1InputField
              label="Company Registration No./Tax No."
              error={!!validationError.accnRegTaxNo}
              helperText={validationError.accnRegTaxNo ?? null}
              name="accnRegTaxNo"
              onChange={handleInputChange}
              required={true}
              value={getValue(reqData?.accnRegTaxNo)}
            />
            <C1TextArea
              name="airRemarks"
              label="Remarks"
              multiline
              required
              textLimit={1024}
              value={getValue(reqData?.airRemarks)}
              onChange={handleInputChange}
              error={!!validationError.airRemarks}
              helperText={validationError.airRemarks ?? null}
            />
            <C1InputField
              label="Requestor Email"
              name="airEmailReq"
              required
              onChange={handleInputChange}
              value={getValue(reqData?.airEmailReq)}
              error={!!validationError.airEmailReq}
              helperText={validationError.airEmailReq ?? null}
            />

            <C1Button
              text="Request Details"
              onClick={handleSubmit}
              color="primary"
              size="large"
            />
          </Grid>
        </Grid>
      </div>
      <C1Warning
        warningTitle="Request Submitted"
        warningMessage={warningProps}
        handleWarningAction={handleWarningPopup}
      />
    </React.Fragment>
  );
};

export default AccnInquiry;
