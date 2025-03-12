import {Grid, MenuItem} from "@material-ui/core";
import BorderColorOutlinedIcon from "@material-ui/icons/BorderColorOutlined";
import CalendarTodayIcon from "@material-ui/icons/CalendarTodayOutlined";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import PersonIcon from "@material-ui/icons/PersonOutlineOutlined";
import PhoneInTalkOutlinedIcon from "@material-ui/icons/PhoneInTalkOutlined";
import React, { useContext } from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import NoteAddOutlinedIcon from "@material-ui/icons/NoteAddOutlined";
import {
  AccountTypes,
  CCM_ACCOUNT_ALL_URL,
  CK_ACCOUNT_TO_ACCN_TYPE,
  CK_MST_SHIPMENT_TYPE,
  JobStates,
  TRUCK_OPERATORS_CONTRACT_VALIDITY,
  CK_CT_FFCO_ACCN,
} from "app/c1utils/const";
import { getValue, isArrayNotEmpty, toTitleCase } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";

import JobTruckContext from "../JobTruckContext";
import JobProperties from "./category/JobProperties";
import JobAdditionalFields from "./category/JobAdditionalFields";

const JobNewDetails = ({ shipmentType,defaultValueJobType,defaultValueLoading, sagawa,  errors }) => {
  const { t } = useTranslation(["job"]);
  const {
    inputData,
    addtionalFieldsInputData,
    jobState,
    handleInputChange,
    handleDateChange,
    isDisabled,
    isWithSession,
    additionalFields,
    viewType,
    handleAddtlFieldsInputChange,
  } = useContext(JobTruckContext);
  const { user } = useAuth();
  const accnType = user?.coreAccn?.TMstAccnType?.atypId;

  const getCoFfAcountTypeLabel = () => {
    let accnTypeLabel = "";

    if (
      !inputData?.tckJob?.tckMstJobState?.jbstId ||
      JobStates.NEW === inputData?.tckJob?.tckMstJobState?.jbstId
    ) {
      accnTypeLabel = user?.coreAccn?.TMstAccnType?.atypDescription;
    } else if (inputData?.tckJob?.tcoreAccnByJobFfAccn) {
      accnTypeLabel = AccountTypes.ACC_TYPE_FF.desc;
    } else {
      accnTypeLabel = AccountTypes.ACC_TYPE_CO.desc;
    }

    return toTitleCase(accnTypeLabel);
  };

  const isDisplayFfCoSelect = () => {
    if (
      JobStates.NEW === inputData?.tckJob?.tckMstJobState?.jbstId ||
      accnType === AccountTypes.ACC_TYPE_FF.code
    ) {
      return true;
    }
    if (inputData?.tckJob?.TCoreAccnByJobSlAccn) {
      return true;
    }
    return false;
  };
  const dropdownOptions = [
    { value: "FTL", desc: "FTL" },
    { value: "LTL", desc: "LTL" },
  ];

  const dropdownOptionsJobType = [
    { value: "LOCAL", desc: "Local" },
    { value: "IMPORT", desc: "Import" },
    { value: "EXPORT", desc: "Export" },
  ];

  return (
    <React.Fragment>
      <Grid item xs={12}>
        <C1TabContainer>
          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t("job:jobDetails.generalDetails")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t("job:jobDetails.jobId")}
                    name="jobId"
                    disabled
                    onChange={handleInputChange}
                    value={inputData?.jobId || "-"}
                  />
                  <C1SelectField
                    name="tckJob.tckMstShipmentType.shtId"
                    label={t("job:jobDetails.shipmentType")}
                    value={
                      shipmentType ||
                      getValue(inputData?.tckJob?.tckMstShipmentType?.shtId)
                    }
                    onChange={(e) => handleInputChange(e)}
                    disabled={true}
                    isServer={true}
                    options={{
                      url: CK_MST_SHIPMENT_TYPE,
                      key: "shtId",
                      id: "shtId",
                      desc: "shtDesc",
                      isCache: true,
                    }}
                  />
                  <C1InputField
                    label={t("job:jobDetails.shipmentRef")}
                    value={getValue(inputData?.jobShipmentRef)}
                    name="jobShipmentRef"
                    onChange={handleInputChange}
                    disabled={isDisabled}
                    required={true}
                    error={errors["jobShipmentRef"] !== undefined}
                    helperText={errors["jobShipmentRef"] || ""}
                    inputProps={{ maxLength: 255 }}
                  />
                  <C1SelectField
                      name="jobSubType"
                      label={t("job:jobDetails.jobSubType")}
                      value={
                        inputData?.tckJob?.jobSubType
                      }
                      onChange={(e) => handleInputChange(e)}
                      disabled={isDisabled}
                      isServer={false}
                      required={true}
                      optionsMenuItemArr={dropdownOptionsJobType?.map((item, ind) => (
                          <MenuItem value={item.value} key={ind}>
                            {item.desc}
                          </MenuItem>
                      ))}
                      error={errors["jobSubType"] !== undefined}
                      helperText={errors["jobSubType"] || ""}
                  />
                  <C1InputField
                    label={t("job:jobDetails.costumerRef")}
                    name="jobCustomerRef"
                    value={getValue(inputData?.jobCustomerRef)}
                    onChange={handleInputChange}
                    disabled={isDisabled}
                    error={errors["jobCustomerRef"] !== undefined}
                    helperText={errors["jobCustomerRef"] || ""}
                  />
                  <C1SelectField
                      name="jobLoading"
                      label={t("job:jobDetails.loading")}
                      value={
                        inputData?.tckJob?.jobLoading
                      }
                      onChange={(e) => handleInputChange(e)}
                      disabled={isDisabled}
                      isServer={false}
                      required={true}
                      optionsMenuItemArr={dropdownOptions.map((item, ind) => (
                          <MenuItem value={item.value} key={ind}>
                            {item.desc}
                          </MenuItem>
                      ))}
                      error={errors["jobLoading"] !== undefined}
                      helperText={errors["jobLoading"] || ""}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>
            <Grid item style={{ height: "39px" }}></Grid>
            <C1CategoryBlock
              icon={<CalendarTodayIcon />}
              title={t("job:jobDetails.jobDates")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1DateField
                    label={t("job:jobDetails.bookingDate")}
                    name="jobDtBooking"
                    value={
                      ![JobStates.NEW.code, JobStates.DRF.code].includes(
                        jobState
                      )
                        ? getValue(inputData?.jobDtBooking)
                        : null
                    }
                    disabled={true}
                    onChange={handleDateChange}
                    disablePast={true}
                  />
                  <C1DateField
                    label={t("job:jobDetails.planDate")}
                    name="jobDtPlan"
                    required={true}
                    value={getValue(inputData?.jobDtPlan)}
                    disabled={isDisabled}
                    onChange={handleDateChange}
                    disablePast={true}
                    error={errors["jobDtPlan"] !== undefined}
                    helperText={errors["jobDtPlan"] || ""}
                  />
                  <C1DateField
                    label={t("job:jobDetails.deliveredDate")}
                    name="jobDtDelivery"
                    value={getValue(inputData?.jobDtDelivery)}
                    disabled
                    onChange={handleDateChange}
                    disablePast={true}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>
          </Grid>

          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<PersonIcon />}
              title={t("job:jobDetails.partyDetails")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  {/* Trucking Operator* -------------------------------- selection ----------------*/}
                  <C1SelectField
                    isServer={true}
                    required={true}
                    name="tcoreAccnByJobPartyTo.accnId"
                    label={t("job:jobDetails.TO")}
                    value={getValue(inputData?.tcoreAccnByJobPartyTo?.accnId)}
                    onChange={handleInputChange}
                    disabled={isDisabled}
                    options={
                      !isWithSession
                        ? {
                            url: CCM_ACCOUNT_ALL_URL,
                            key: "account",
                            id: "accnId",
                            desc: "accnName",
                            isCache: false,
                          }
                        : user?.coreAccn?.TMstAccnType?.atypId ===
                            "ACC_TYPE_TO" ||
                          user?.coreAccn?.TMstAccnType?.atypId ===
                            "ACC_TYPE_FF_CO"
                        ? {
                            url: CK_ACCOUNT_TO_ACCN_TYPE,
                            key: "accnId",
                            id: "accnId",
                            desc: "accnName",
                          }
                        : user?.coreAccn?.TMstAccnType?.atypId === "ACC_TYPE_SP"
                        ? {
                            url: CCM_ACCOUNT_ALL_URL,
                            key: "account",
                            id: "accnId",
                            desc: "accnName",
                            isCache: false,
                          }
                        : viewType === "view"
                        ? {
                            url: CCM_ACCOUNT_ALL_URL,
                            key: "account",
                            id: "accnId",
                            desc: "accnName",
                            isCache: false,
                          }
                        : {
                            // url: TRUCK_OPERATORS_BY_CONTRACT_WITH_CO_ACCNID + user.coreAccn.accnId,
                            url: TRUCK_OPERATORS_CONTRACT_VALIDITY,
                            key: "accnId",
                            id: "accnId",
                            desc: "accnName",
                            isCache: false,
                          }
                    }
                    error={errors["tcoreAccnByJobPartyTo.accnId"] !== undefined}
                    helperText={errors["tcoreAccnByJobPartyTo.accnId"] || ""}
                  />
                  {/* Freight Forwarder / Cargo Owner Selection */}
                  {isDisplayFfCoSelect() && (
                    <C1SelectField
                      isServer={true}
                      disabled={isDisabled}
                      name="tckJob.tcoreAccnByJobSlAccn.accnId"
                      label={t("job:jobDetails.CO")}
                      value={getValue(
                        inputData?.tckJob?.tcoreAccnByJobSlAccn?.accnId
                      )}
                      onChange={handleInputChange}
                      options={{
                        url: CK_CT_FFCO_ACCN,
                        key: "account",
                        id: "accnId",
                        desc: "accnName",
                        isCache: false,
                      }}
                    />
                  )}
                  <C1SelectField
                    isServer={true}
                    required
                    disabled={true}
                    name="tcoreAccnByJobPartyCoFf.accnId"
                    //label={t("job:jobDetails.COFF")}
                    label={getCoFfAcountTypeLabel()}
                    value={getValue(inputData?.tcoreAccnByJobPartyCoFf?.accnId)}
                    onChange={handleInputChange}
                    options={{
                      url: CCM_ACCOUNT_ALL_URL,
                      key: "account",
                      id: "accnId",
                      desc: "accnName",
                      isCache: false,
                    }}
                    error={errors["tcoreAccnByJobPartyCoFf"] !== undefined}
                    helperText={errors["tcoreAccnByJobPartyCoFf"] || ""}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>
            <Grid item style={{ height: "39px" }}></Grid>
            <C1CategoryBlock
              icon={<PhoneInTalkOutlinedIcon />}
              title={t("job:jobDetails.contactDetailsCOFF")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t("job:jobDetails.contactPerson")}
                    name="tckCtContactDetailByJobContactCoFf.cdName"
                    value={getValue(
                      inputData?.tckCtContactDetailByJobContactCoFf?.cdName
                    )}
                    onChange={handleInputChange}
                    disabled={true}
                  />
                  <C1InputField
                    label={t("job:jobDetails.phoneNumber")}
                    placeholder={t("job:tripDetails.mobileFormat")}
                    name="tckCtContactDetailByJobContactCoFf.cdPhone"
                    value={getValue(
                      inputData?.tckCtContactDetailByJobContactCoFf?.cdPhone
                    )}
                    onChange={handleInputChange}
                    disabled={true}
                  />
                  <C1InputField
                    label={t("job:jobDetails.email")}
                    name="tckCtContactDetailByJobContactCoFf.cdEmail"
                    value={getValue(
                      inputData?.tckCtContactDetailByJobContactCoFf?.cdEmail
                    )}
                    onChange={handleInputChange}
                    disabled={true}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>
            <Grid item style={{ height: "39px" }}></Grid>
            <C1CategoryBlock
              icon={<PhoneInTalkOutlinedIcon />}
              title={t("job:jobDetails.contactDetailsTO")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  {/*change tckCtContactDetailByJobContactTo after user input to account*/}
                  <C1InputField
                    label={t("job:jobDetails.contactPerson")}
                    name="tckCtContactDetailByJobContactTo.cdName"
                    value={getValue(
                      inputData?.tckCtContactDetailByJobContactTo?.cdName
                    )}
                    onChange={handleInputChange}
                    disabled={true}
                  />
                  <C1InputField
                    label={t("job:jobDetails.phoneNumber")}
                    placeholder={t("job:tripDetails.mobileFormat")}
                    name="tckCtContactDetailByJobContactTo.cdPhone"
                    value={getValue(
                      inputData?.tckCtContactDetailByJobContactTo?.cdPhone
                    )}
                    onChange={handleInputChange}
                    disabled={true}
                  />
                  <C1InputField
                    label={t("job:jobDetails.email")}
                    name="tckCtContactDetailByJobContactTo.cdEmail"
                    value={getValue(
                      inputData?.tckCtContactDetailByJobContactTo?.cdEmail
                    )}
                    onChange={handleInputChange}
                    disabled={true}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>
          </Grid>

          <Grid item lg={4} md={6} xs={12}>
            {inputData?.showAdditionalFields &&
              (isArrayNotEmpty(additionalFields) ||
                isArrayNotEmpty(addtionalFieldsInputData)) && (
                <C1CategoryBlock
                  icon={<NoteAddOutlinedIcon />}
                  title={t("job:additionalFields.header")}
                >
                  <JobAdditionalFields
                    inputData={inputData}
                    addtionalFieldsInputData={addtionalFieldsInputData}
                    fields={additionalFields}
                    errors={errors}
                    handleDateChange={handleDateChange}
                    handleAddtlFieldsInputChange={handleAddtlFieldsInputChange}
                    disabled={isDisabled}
                  />
                </C1CategoryBlock>
              )}
            <C1CategoryBlock
              icon={<BorderColorOutlinedIcon />}
              title={t("job:jobDetails.properties")}
            >
              <JobProperties
                inputData={inputData}
                errors={errors}
                handleDateChange={handleDateChange}
                handleInputChange={handleInputChange}
              />
            </C1CategoryBlock>
          </Grid>
        </C1TabContainer>
      </Grid>
    </React.Fragment>
  );
};

export default JobNewDetails;
