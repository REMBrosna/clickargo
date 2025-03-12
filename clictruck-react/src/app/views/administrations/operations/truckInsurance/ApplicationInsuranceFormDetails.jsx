import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import {Button, Checkbox, Dialog, Grid} from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import { LocalShippingOutlined } from "@material-ui/icons";
import { isEmpty } from "lodash";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1CategoryBlock from "../../../../c1component/C1CategoryBlock";
import C1InputField from "../../../../c1component/C1InputField";
import ApplicationInsurance from "./ApplicationInsurance";
import useHttp from "app/c1hooks/http";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "../../../../hooks/useAuth";
import { ConfirmationDialog, MatxLoading } from "matx";

const useStyles = makeStyles((theme) => ({
  tabText: {
    marginLeft: "16px",
    marginTop: "-1px",
  },
  tabIcon: {
    position: "absolute",
    marginLeft: "-5px",
  },
  valid: {
    color: "#F0DB72",
  },
  error: {
    color: "red",
  },
}));

const ApplicationInsuranceFormDetails = () => {
  const { t } = useTranslation([
    "buttons",
    "listing",
    "administration",
    "common",
    "cargoowners",
  ]);
  const classes = useStyles();
  const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();
  const history = useHistory();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [openPopUp, setOpenPopUp] = useState(false);
  const [formData, setFormData] = useState({});
  const [trucks, setTrucks] = useState([]);
  const [openWarning, setOpenWarning] = useState(false);
  const [warningMessage, setWarningMessage] = useState("");
  const [isChecked, setIsChecked] = useState(false);
  const [openDeleteConfirm, setOpenDeleteConfirm] = useState({ action: null, open: false });
  const defaultSnackbarValue = {
    success: false,
    successMsg: "",
    error: false,
    errorMsg: "",
    redirectPath: "",
  };
  const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
  const [inputData, setInputData] = useState({
    contactNo: "",
    email: "",
    trucks: [],
  });
  const [openPopDetails, setOpenPopDetails] = useState({
    licenseNo: "",
    makeAndModel: "",
    coverage: "",
    usage: "",
    claims: "",
    suspension: "",
  });
  const [validationErrors, setValidationErrors] = useState({});
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [submitting, setSubmitting] = useState(false);
  useEffect(() => {
    sendRequest("/api/v2.0/clicTruck/insurance/sg/assue/form", "getData", "get", null);
  }, [sendRequest]);
  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      setLoading(isLoading);
      switch (urlId) {
        case "submit": {
          let msg = t("cargoowners:msg.submitSuccess");
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: msg,
            redirectPath: "/applications/services/job/to/truck"
          });
          setLoading(false);
          setSubmitting(false);
          break;
        }
        case "deleteData": {
          setLoading(false);
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.deleteSuccess"),
          });
          break;
        }
        case "getData": {
          setFormData(res?.data)
          break;
        }
        default:
          break;
      }
    } else if (error) {
      setLoading(false);
    }
    if (validation) {
      setValidationErrors({ ...validation });
      setLoading(false);
      setSubmitting(false);
    }
  }, [urlId, isLoading, res, validation, error]);

  const handleAddTrucks = () => {
    const errorList = handleAddTrucksValidate();
    if (!isEmpty(errorList)) {
      setValidationErrors(errorList);
    } else {
      const existingTruckIndex = trucks.findIndex(truck => truck.licenseNo === openPopDetails.licenseNo);
      if (existingTruckIndex !== -1) {
        const updatedTrucks = [...trucks];
        updatedTrucks[existingTruckIndex] = openPopDetails;
        setTrucks(updatedTrucks);
      } else {
        const newTruck = { ...openPopDetails };
        const newData = [...trucks, newTruck];
        setTrucks(newData);
      }

      setInputData({
        ...inputData,
        companyName: user.name,
        trucks: [...trucks, openPopDetails], // Update with the new truck
      });
      setOpenPopDetails({
        licenseNo: "",
        makeAndModel: "",
        coverage: "",
        usage: "",
        claims: "",
        suspension: "",
      });
      setOpenPopUp(false); // Close the pop-up
    }
  };


  const handleValidate = () => {
    const errors = {};
    if (!inputData.contactNo) {
      errors.contactNo = t("common:validationMsgs.required");
    }
    if (!inputData.email) {
      errors.email = t("common:validationMsgs.required");
    }
    if (inputData.trucks.length === 0) {
      errors.trucks = t("common:validationMsgs.required");
    }
    if (!isChecked){
      errors.checkbox =  t("common:validationMsgs.required");
    }
    return errors;
  };

  const handleAddTrucksValidate = () => {
    let errors = {};
    if (!openPopDetails.licenseNo) {
      errors.licenseNo = t("common:validationMsgs.required");
    }
    if (!openPopDetails.makeAndModel) {
      errors.makeAndModel = t("common:validationMsgs.required");
    }
    if (!openPopDetails.coverage) {
      errors.coverage = t("common:validationMsgs.required");
    }
    if (!openPopDetails.usage) {
      errors.usage = t("common:validationMsgs.required");
    }
    if (!openPopDetails.claims) {
      errors.claims = t("common:validationMsgs.required");
    }
    if (!openPopDetails.suspension) {
      errors.suspension = t("common:validationMsgs.required");
    }
    return errors;
  };
  const handleDeleteOnClick = (e, row) => {
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "DELETE",
      open: true,
      msg: t("common:msg.deleteConfirm"),
      onConfirm: () => {
        const newData = trucks.filter((item) => item !== row);
        setInputData({ ...inputData, trucks: newData });
        setTrucks(newData);
      }
    });
  };
  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  const handleInputChange = (e) => {
    const elName = e.target.name;
    const value = e.target.value;
    setInputData({
      ...inputData,
      [elName]: value,
    });
  };

  const handlePopUpChange = (e) => {
    const elName = e.target.name;
    const value = e.target.value;
    setOpenPopDetails({
      ...openPopDetails,
      [elName]: value,
    });
  };


  const handleSubmitOnClick = (e) => {
    e.preventDefault();
    const errorList = handleValidate();
      setValidationErrors({});
    if (!isEmpty(errorList)) {
      setValidationErrors(errorList);
    } else {
      setSubmitting(true);
      setLoading(true);
      sendRequest(
          "/api/v2.0/clicTruck/insurance/sg/assue",
          "submit",
          "post",
          inputData
      );
    }
  };

  const eventHandler = (action) => {
     if (action.toLowerCase() === "delete") {
       handleDeleteOnClick();
    } else {
      setOpenSubmitConfirm({ action: action, open: true });
    }
  };
  const handleAutoCompleteInput = (e, name, value) => {
    setOpenPopDetails({
      ...openPopDetails,
      [name]: value?.value,
    });
  };
  const handleCheckBoxChange = (event) => {
    setIsChecked(event.target.checked);
  };

  let bcLabel = t("administration:truckInsurance.title");
  let formButtons = (
      <C1FormButtons
          options={{
            submitOnClick: { show: true, eventHandler: handleSubmitOnClick },
            back: { show: true, eventHandler: () => history.goBack() },
          }}
      />
  );


  return (
      <React.Fragment>
        {loading ? (
            <MatxLoading />
        ) : (
            <React.Fragment>
              <C1FormDetailsPanel
                  breadcrumbs={[{ name: t("administration:truckInsurance.title") }]}
                  title={bcLabel}
                  formButtons={formButtons}
                  snackBarOptions={snackBarOptions}
                  formInitialValues={{ ...inputData }}
                  formValues={{ ...inputData }}
                  onValidate={handleValidate}
                  isLoading={loading}
              >
                {() => (
                    <Grid
                        container
                        spacing={3}
                        className={classes.gridContainer}
                        style={{margin: "10px"}}
                    >
                      <Grid item lg={2} xs={12}>
                        <C1CategoryBlock
                            icon={<DescriptionIcon/>}
                            title={t("administration:truckInsurance.details")}
                        />
                        <C1InputField
                            label={t("administration:truckInsurance.accountId")}
                            name="accnId"
                            required
                            value={user?.coreAccn?.accnId}
                            onChange={handleInputChange}
                            disabled={true}
                        />
                        <C1InputField
                            label={t("administration:truckInsurance.accountName")}
                            name="companyName"
                            required
                            value={user?.coreAccn.accnName}
                            onChange={handleInputChange}
                            disabled={true}
                        />
                        <C1InputField
                            label={t("administration:truckInsurance.mobileNumber")}
                            name="contactNo"
                            required={true}
                            type="number"
                            value={inputData?.contactNo}
                            error={validationErrors && validationErrors.contactNo ? true : false}
                            helperText={validationErrors && validationErrors.contactNo ? validationErrors.contactNo : null}
                            onChange={handleInputChange}
                            disabled={false}
                        />
                        <C1InputField
                            label={t("administration:truckInsurance.email")}
                            name="email"
                            required={true}
                            value={inputData?.email}
                            error={validationErrors && validationErrors.email ? true : false}
                            helperText={validationErrors && validationErrors.email ? validationErrors.email : null}
                            onChange={handleInputChange}
                            disabled={false}
                        />
                      </Grid>
                      <Grid item lg={10} xs={12}>
                        <C1CategoryBlock
                            icon={<LocalShippingOutlined/>}
                            title={t("administration:truckInsurance.trucks")}
                        />
                        <ApplicationInsurance
                            setOpenSubmitConfirm={setOpenSubmitConfirm}
                            setOpenPopDetails={setOpenPopDetails}
                            handlePopUpChange={handlePopUpChange}
                            handleAddTrucks={handleAddTrucks}
                            openPopDetails={openPopDetails}
                            eventHandler={eventHandler}
                            error={validationErrors}
                            handleAutoCompleteInput={handleAutoCompleteInput}
                            handleDeleteRecord={handleDeleteOnClick}
                            setInputData={setInputData}
                            setOpenPopUp={setOpenPopUp}
                            openPopUp={openPopUp}
                            inputData={inputData}
                            formData={formData}
                            data={trucks}
                            t={t}
                        />
                      </Grid>
                      <Grid container justifyContent="center">
                        <Grid item xs={12}>
                          <table>
                            <caption style={{display: "flex", alignItems: "center", marginTop:"0px"}}>
                              <p style={{marginInlineStart: "-10px", margin: "10px 50px 10px 0px"}}>
                                <Checkbox
                                    defaultChecked={false}
                                    name=""
                                    checked={isChecked}
                                    color="primary"
                                    inputProps={{'aria-label': 'secondary checkbox'}}
                                    onChange={handleCheckBoxChange}
                                    error={validationErrors && validationErrors.checkbox ? true : false}
                                    helperText={validationErrors && validationErrors.checkbox ? validationErrors.checkbox : null}
                                />
                                I/we hereby understand that I/we need to disclose our personal data in order for Assue
                                (Singapore) Pte Ltd to generate a quotation for me/us. I/we have read the Privacy Policy and
                                I/we have consent to the collection and use of my/our personal data in accordance with its
                                terms and conditions. I/we agree and understand that the accuracy of the quotation premium. I/we
                                agree and understand that accuracy of quotation premium is solely reliant iron information
                                provided by me/us only
                              </p>
                            </caption>
                          </table>
                        </Grid>
                      </Grid>
                    </Grid>
                )}
              </C1FormDetailsPanel>
              <ConfirmationDialog
                  open={openSubmitConfirm?.open}
                  onConfirmDialogClose={() => setOpenSubmitConfirm({...openSubmitConfirm, action: null, open: false})}
                  text={t("cargoowners:msg.confirmation", {action: openSubmitConfirm?.action})}
                  title={t("cargoowners:popup.confirmation")}
                  onYesClick={(e) => {
                    setOpenSubmitConfirm({...openSubmitConfirm, open: false});
                    if (openSubmitConfirm.onConfirm) {
                      openSubmitConfirm.onConfirm();
                    }
                  }}
              />
              <Dialog maxWidth="xs" open={openWarning}>
                <div className="p-8 text-center w-360 mx-auto">
                  <h4 className="capitalize m-0 mb-2">{"Warning"}</h4>
                  <p>{warningMessage}</p>
                  <div className="flex justify-center pt-2 m--2">
                    <Button
                        className="m-2 rounded hover-bg-primary px-6"
                        variant="outlined"
                        color="primary"
                        onClick={(e) => handleWarningAction(e)}
                    >
                      {t("cargoowners:popup.ok")}
                    </Button>
                  </div>
                </div>
              </Dialog>
            </React.Fragment>
        )}
      </React.Fragment>
  );
};

export default withErrorHandler(ApplicationInsuranceFormDetails);
