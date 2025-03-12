import {
    Checkbox,
    Container,
    FormControlLabel,
    FormGroup,
    Grid,
    IconButton,
    Switch,
    Tooltip,
    Typography,
    Button,
  } from "@material-ui/core";

  import LocationOnIcon from "@material-ui/icons/LocationOn";
 
  import GetAppIcon from "@material-ui/icons/GetApp";
  import React, { useEffect, useState, useContext } from "react";
  import { useTranslation } from "react-i18next";
  import history from "history.js";
  import JobTruckContext from "../form/JobTruckContext";
  import useAuth from "app/hooks/useAuth";
  import C1CategoryBlock from "app/c1component/C1CategoryBlock";
  import C1InputField from "app/c1component/C1InputField";
  import C1SelectField from "app/c1component/C1SelectField";
  import C1TabContainer from "app/c1component/C1TabContainer";
  import C1FileUpload from "app/c1component/C1FileUpload";
  import {
    AccountTypes,
    CK_ACCOUNT_CO_FF_ACCN_TYPE,
    CCM_ACCOUNT_ALL_URL,
  } from "app/c1utils/const";
  import C1DataTableActions from "app/c1component/C1DataTableActions";
  import C1Button from "app/c1component/C1Button";
  import C1PopUp from "app/c1component/C1PopUp";
  import { getValue } from "app/c1utils/utility";
  import useHttp from "app/c1hooks/http";

  const InvoiceReimbursementPopUp = ({
    openPopUp,
    setOpenPopUp,
    title,
  }) => {
    return (
      <C1PopUp
        title={title}
        openPopUp={openPopUp}
        setOpenPopUp={setOpenPopUp}
        maxWidth={"md"}
      >
        <Grid container spacing={3}>
          <Grid item lg={6}>
            <C1CategoryBlock
              icon={<LocationOnIcon />}
              title={"General Details"}
            />
            <C1SelectField
              // isServer={true}
              // required={true}
              name="PickDriver"
              label="Type"
              disabled={true}
              // name="tcoreAccnByJobPartyTo.accnId"
              // label={driverIndex != undefined ? mockDriverList[driverIndex].name : "Driver"}
              // value={driverIndex}
              // onChange={(val)=> console.log(val.target.value)}
              // optionsMenuItemArr={mockDriverList[0]}
              // options={{
              //     url: T_CK_CT_DRV,
              //     key: "drvId",
              //     id: "drvName",
              //     desc: "drvName",
              //     isCache: true,
              // }}
              // error={errors['tcoreAccnByJobPartyTo'] !== undefined}
              // helperText={errors['tcoreAccnByJobPartyTo'] || ''}
            />
            <Grid
              container
              style={{
                paddingTop: 20,
                paddingBottom: 20,
              }}
              alignItems="center"
              justifyContent="space-between"
              direction="row"
            >
              <Typography variant="h6">Receipt:</Typography>
              <GetAppIcon fontSize="large" />
            </Grid>
            <C1InputField
              label={"Remarks"}
              // value={driverPhone}
              // name="Driver Name"
              multiline={true}
              rows={10}
              disabled={true}
              // onChange={handleInputChange}
              // disabled={isDisabled}
              // required={true}
              // error={errors['TCkJob.jobReference'] !== undefined}
              // helperText={errors['TCkJob.jobReference'] || ''}
            />
          </Grid>
          <Grid item lg={6}>
            <C1CategoryBlock
              icon={<LocationOnIcon />}
              title={"Reimbursement Cost"}
            />
            <C1InputField isInteger disabled label="Price(IDR)" />
            <C1InputField isInteger disabled label="Tax(IDR)" />
            <Grid
              container
              style={{ paddingTop: 20 }}
              direction="row"
              justifyContent="space-between"
              xs={12}
            >
              <Typography>Trip Charges(IDR)</Typography>
              <Typography>Rp. 5.605.054</Typography>
            </Grid>
          </Grid>
        </Grid>
      </C1PopUp>
    );
  };

  export default InvoiceReimbursementPopUp