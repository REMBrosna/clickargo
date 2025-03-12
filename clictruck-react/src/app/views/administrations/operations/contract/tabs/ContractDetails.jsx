import { Checkbox, Grid, MenuItem } from "@material-ui/core";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import SupervisorAccountOutlinedIcon from "@material-ui/icons/SupervisorAccountOutlined";
import LocalAtmOutlinedIcon from "@material-ui/icons/LocalAtmOutlined";
import BorderColorOutlinedIcon from "@material-ui/icons/BorderColorOutlined";
import EventNoteOutlinedIcon from "@material-ui/icons/EventNoteOutlined";
import CreditCardOutlinedIcon from "@material-ui/icons/CreditCardOutlined";
import AccountBalanceOutlinedIcon from "@material-ui/icons/AccountBalanceOutlined";
import React from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";
import {
  CK_ACCOUNT_CO_FF_ACCN_TYPE,
  CK_ACCOUNT_TO_ACCN_TYPE,
  FINANCING_MODELS,
  MST_BANKS_URL,
  MST_CURRENCY_URL,
} from "app/c1utils/const";
import NumFormat from "app/clictruckcomponent/NumFormat";
import C1TextArea from "app/c1component/C1TextArea";
import { getValue } from "app/c1utils/utility";

const mstUnit = [
  { value: "P", desc: "%" },
  { value: "F", desc: "Fixed" },
];

const ContractDetails = ({
  inputData,
  handleInputChange,
  handleDateChange,
  viewType,
  isDisabled,
  errors,
}) => {
  const { t } = useTranslation(["administration"]);
  const payTermTo = [
    {
      desc: "2",
      value: "2",
    },
    {
      desc: "5",
      value: "5",
    },
  ];

  const paymentTermCoff = [
    {
      desc: "30",
      value: "30",
    },
    {
      desc: "45",
      value: "45",
    },
    {
      desc: "60",
      value: "60",
    },
    {
      desc: "90",
      value: "90",
    },
  ];

  const onlyNumber = (e) => {
    const value = e.target?.value;
    if (!/^[0-9,.]*$/.test(value)) {
      return e.preventDefault();
    }
  };

  return (
    <React.Fragment>
      <Grid item xs={12}>
        <C1TabContainer>
          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t(
                "administration:contractManagement.details.generalDetails"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t("administration:contractManagement.details.id")}
                    name="conId"
                    disabled
                    onChange={handleInputChange}
                    value={inputData?.conId || ""}
                  />
                  <C1InputField
                    label={t("administration:contractManagement.details.name")}
                    name="conName"
                    required
                    onChange={handleInputChange}
                    value={getValue(inputData?.conName)}
                    error={errors["conName"] !== undefined}
                    helperText={errors["conName"] || ""}
                    disabled={isDisabled}
                  />
                  <C1TextArea
                    label={t("administration:contractManagement.details.desc")}
                    name="conDescription"
                    multiline
                    textLimit={1024}
                    onChange={handleInputChange}
                    value={inputData?.conDescription || ""}
                    disabled={isDisabled}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>
          </Grid>

          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<SupervisorAccountOutlinedIcon />}
              title={t(
                "administration:contractManagement.details.partiesDetail"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1SelectField
                    name="tcoreAccnByConTo.accnId"
                    label={t(
                      "administration:contractManagement.details.truckOperator"
                    )}
                    value={getValue(inputData?.tcoreAccnByConTo?.accnId)}
                    onChange={(e) => handleInputChange(e)}
                    isServer
                    required
                    options={{
                      url: CK_ACCOUNT_TO_ACCN_TYPE,
                      key: "account",
                      id: "accnId",
                      desc: "accnName",
                      isCache: false,
                    }}
                    error={errors["TcoreAccnByConTo.accnId"] !== undefined}
                    helperText={errors["TcoreAccnByConTo.accnId"] || ""}
                    disabled={isDisabled}
                  />
                  <C1SelectField
                    isServer={true}
                    required
                    disabled={isDisabled}
                    name="tcoreAccnByConCoFf.accnId"
                    label={"CO / FF"}
                    value={getValue(inputData?.tcoreAccnByConCoFf?.accnId)}
                    onChange={handleInputChange}
                    options={{
                      url: CK_ACCOUNT_CO_FF_ACCN_TYPE,
                      key: "account",
                      id: "accnId",
                      desc: "accnName",
                      isCache: false,
                    }}
                    error={errors["TCoreAccnByRtCoFf.accnId"] !== undefined}
                    helperText={errors["TCoreAccnByRtCoFf.accnId"] || ""}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>


          </Grid>

          <Grid container item lg={4} md={6} xs={12} direction="column">
          <C1CategoryBlock
              icon={<EventNoteOutlinedIcon />}
              title={t(
                "administration:contractManagement.details.valilidilityDetails"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1DateField
                    label={t(
                      "administration:contractManagement.details.startDate"
                    )}
                    name="conDtStart"
                    required
                    value={getValue(inputData?.conDtStart)}
                    disabled={isDisabled}
                    onChange={handleDateChange}
                    disablePast={true}
                    error={errors["conDtStart"] !== undefined}
                    helperText={errors["conDtStart"] || ""}
                  />
                  <C1DateField
                    label={t(
                      "administration:contractManagement.details.expiredDate"
                    )}
                    name="conDtEnd"
                    required
                    value={inputData?.conDtEnd}
                    disabled={isDisabled}
                    onChange={handleDateChange}
                    disablePast={true}
                    error={errors["conDtStart"] !== undefined}
                    helperText={errors["conDtStart"] || ""}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>

          </Grid>
        </C1TabContainer>
      </Grid>
    </React.Fragment>
  );
};

export default ContractDetails;
