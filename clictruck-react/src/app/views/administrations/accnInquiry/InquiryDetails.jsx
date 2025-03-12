import { Grid } from "@material-ui/core";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1TextArea from "app/c1component/C1TextArea";
import { useStyles } from "app/c1utils/styles";
import { getValue } from "app/c1utils/utility";
import React from "react";
import AssignmentOutlinedIcon from "@material-ui/icons/AssignmentOutlined";
import RequestedDocuments from "./RequestedDocuments";
import { RequestState } from "./AccnInquiryList";

const InquiryDetails = ({
  inputData,
  locale,
  dtLoading,
  handleInputChange,
  handleDateChange,
}) => {
  const classes = useStyles();

  return (
    <React.Fragment>
      <C1TabContainer>
        <Grid container item>
          <Grid item lg={4} md={6} xs={12}>
            <Grid
              container
              alignItems="center"
              spacing={3}
              className={classes.gridContainer}
            >
              <Grid item xs={12}>
                <C1InputField
                  label={locale("opadmin:accnInq.reqId")}
                  name="airId"
                  disabled
                  onChange={handleInputChange}
                  value={getValue(inputData?.airId)}
                />

                <C1InputField
                  label={locale("opadmin:accnInq.compAccnId")}
                  name="tcoreAccn.accnId"
                  disabled
                  onChange={handleInputChange}
                  value={getValue(inputData?.tcoreAccn?.accnId)}
                />

                <C1InputField
                  label={locale("opadmin:accnInq.compAccnName")}
                  name="tcoreAccn.accnName"
                  disabled
                  onChange={handleInputChange}
                  value={getValue(inputData?.tcoreAccn?.accnName)}
                />
              </Grid>
            </Grid>
          </Grid>
          <Grid item lg={4} md={6} xs={12}>
            <Grid
              container
              alignItems="center"
              spacing={3}
              className={classes.gridContainer}
            >
              <Grid item xs={12}>
                <C1InputField
                  label={locale("opadmin:accnInq.reqBy")}
                  name="airEmailReq"
                  disabled
                  onChange={handleInputChange}
                  value={getValue(inputData?.airEmailReq)}
                />
                <C1DateField
                  label={locale("opadmin:accnInq.reqDt")}
                  value={inputData?.airDtCreate}
                  onChange={handleInputChange}
                  disabled
                />
                <C1TextArea
                  name="airRemarks"
                  label={locale("opadmin:accnInq.remarks")}
                  multiline
                  textLimit={1024}
                  value={getValue(inputData?.airRemarks)}
                  onChange={handleInputChange}
                  disabled
                />
              </Grid>
            </Grid>
          </Grid>
          <Grid item lg={4} md={6} xs={12}>
            <Grid
              container
              alignItems="center"
              spacing={3}
              className={classes.gridContainer}
            >
              <Grid item xs={12}>
                <C1InputField
                  label={locale("opadmin:accnInq.processedBy")}
                  name="tcoreUsr.usrName"
                  disabled
                  onChange={handleInputChange}
                  value={getValue(inputData?.tcoreUsr?.usrName)}
                />
                <C1DateField
                  label={locale("opadmin:accnInq.processDt")}
                  name="airDtProcessed"
                  disabled
                  onChange={handleDateChange}
                  value={inputData?.airDtProcessed}
                />
              </Grid>
            </Grid>
          </Grid>
        </Grid>
        <Grid item xs={12}>
          <C1CategoryBlock
            icon={<AssignmentOutlinedIcon />}
            title={locale("opadmin:accnInq.docsReqList")}
          ></C1CategoryBlock>
        </Grid>
        <Grid item xs={12}>
          <RequestedDocuments
            dtLoading={dtLoading}
            inputData={inputData}
            disableAdd={inputData?.airReqState === RequestState.COMPLETED.code}
          />
        </Grid>
      </C1TabContainer>
    </React.Fragment>
  );
};

export default InquiryDetails;
