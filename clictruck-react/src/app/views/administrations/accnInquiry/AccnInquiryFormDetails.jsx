import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import useHttp from "app/c1hooks/http";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { MatxLoading } from "matx";
import React, { useEffect, useState } from "react";
import { useParams, useHistory } from "react-router-dom";
import { RequestState } from "./AccnInquiryList";
import C1FormButtons from "app/c1component/C1FormButtons";
import SendOutlinedIcon from "@material-ui/icons/SendOutlined";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import InquiryDetails from "./InquiryDetails";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { useTranslation } from "react-i18next";
import { Divider, Grid, Paper, Tab, Tabs } from "@material-ui/core";
import C1AuditTab from "app/c1component/C1AuditTab";

const AccnInquiryFormDetails = () => {
  let { id } = useParams();
  let history = useHistory();
  const { t } = useTranslation(["common", "opadmin"]);
  const tabList = [
    { text: t("opadmin:accnInq.tabDetails") },
    { text: t("common:audits.title") },
  ];

  const [loading, setLoading] = useState(false);
  const [dtLoading, setDtLoading] = useState(false);
  const [inputData, setInputData] = useState({});
  const [tabIndex, setTabIndex] = useState(0);

  const {
    isLoading,
    isFormSubmission,
    res,
    validation,
    error,
    urlId,
    sendRequest,
  } = useHttp();

  const defaultSnackbarValue = {
    success: false,
    successMsg: "",
    error: false,
    errorMsg: "",
    redirectPath: "",
  };
  const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

  useEffect(() => {
    setLoading(true);
    setDtLoading(true);
    sendRequest(
      `/api/v1/clickargo/clictruck/inquiry/accn/${id}`,
      "getAccnInqReq"
    );
  }, []);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      setLoading(isLoading);
      if (urlId === "getAccnInqReq") {
        setInputData({ ...inputData, ...res?.data });
        setTimeout(() => setDtLoading(false), 2000);
      } else if (urlId === "updateSave" || urlId === "updateSend") {
        setInputData({ ...inputData, ...res?.data });
        setSnackBarOptions({
          ...snackBarOptions,
          success: true,
          successMsg:
            urlId === "updateSave"
              ? t("opadmin:accnInq.reqSavedSuccess")
              : t("opadmin:accnInq.reqResSuccess"),
        });
      }
    }
  }, [urlId, isLoading, isFormSubmission, res, validation, error]);

  const handleTabChange = (e, value) => {
    setTabIndex(value);
  };

  const handleSave = (e) => {
    e.preventDefault();
    //This just basically update the sttus to IN-PROGRESS since most of the fields are readonly
    //and the document upload is sent to BE already.
    setLoading(true);
    sendRequest(
      `/api/v1/clickargo/clictruck/inquiry/accn/${id}`,
      "updateSave",
      "put",
      { ...inputData, action: "SAVE" }
    );
  };

  const handleSendDocuments = (e) => {
    e.preventDefault();
    setLoading(true);
    sendRequest(
      `/api/v1/clickargo/clictruck/inquiry/accn/${id}`,
      "updateSend",
      "put",
      { ...inputData, action: "SEND" }
    );
  };

  const handleDateChange = (name, e) => {
    setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
  };

  const handleInputChange = (e) => {
    const elName = e.target.name;

    setInputData({
      ...inputData,
      ...deepUpdateState(inputData, elName, e.target.value),
    });
  };

  let formButtons = (
    <C1FormButtons
      options={{
        back: {
          show: true,
          eventHandler: () => history.goBack(),
        },
        save: {
          show: [
            RequestState.INPROGRESS.code,
            RequestState.PENDING.code,
          ].includes(inputData?.airReqState)
            ? true
            : false,
          eventHandler: (e) => handleSave(e),
        },
      }}
    >
      {inputData?.airReqState !== RequestState.COMPLETED.code && (
        <C1LabeledIconButton
          tooltip={t("opadmin:accnInq.btnSend")}
          label={t("opadmin:accnInq.btnSend")}
          action={(e) => handleSendDocuments(e)}
        >
          <SendOutlinedIcon color="primary" />
        </C1LabeledIconButton>
      )}
    </C1FormButtons>
  );

  return loading ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      <C1FormDetailsPanel
        breadcrumbs={[
          {
            name: t("opadmin:accnInq.bcList"),
            path: "/opadmin/inquiry/accn/list",
          },
        ]}
        titleStatus={RequestState[inputData?.airReqState]?.desc}
        title={t("opadmin:accnInq.title")}
        formButtons={formButtons}
        initialValues={{ ...inputData }}
        values={{ ...inputData }}
        onSubmit={(values, actions) => handleSendDocuments(values, actions)}
        snackBarOptions={snackBarOptions}
        isLoading={loading}
      >
        {(props) => (
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <Paper>
                <Tabs
                  className="mt-4"
                  value={tabIndex}
                  onChange={handleTabChange}
                  indicatorColor="primary"
                  textColor="primary"
                >
                  {tabList.map((item, ind) => {
                    return (
                      <Tab
                        className="capitalize"
                        value={ind}
                        label={item.text}
                        key={ind}
                        icon={item.icon}
                      />
                    );
                  })}
                </Tabs>
                <Divider className="mb-6" />
                {tabIndex === 0 && (
                  <C1TabInfoContainer>
                    <InquiryDetails
                      dtLoading={dtLoading}
                      locale={t}
                      inputData={inputData}
                      handleInputChange={handleInputChange}
                      handleDateChange={handleDateChange}
                    />
                  </C1TabInfoContainer>
                )}
                {tabIndex === 1 && (
                  <C1TabInfoContainer>
                    <C1AuditTab filterId={inputData?.airId} />
                  </C1TabInfoContainer>
                )}
              </Paper>
            </Grid>
          </Grid>
        )}
      </C1FormDetailsPanel>
    </React.Fragment>
  );
};

export default withErrorHandler(AccnInquiryFormDetails);
