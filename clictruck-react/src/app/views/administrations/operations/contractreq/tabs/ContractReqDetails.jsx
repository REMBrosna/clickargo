import { Checkbox, Grid, MenuItem } from "@material-ui/core";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import CreditCardOutlinedIcon from "@material-ui/icons/CreditCardOutlined";
import React from "react";
import { useTranslation } from "react-i18next";
import EventNoteOutlinedIcon from "@material-ui/icons/EventNoteOutlined";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";
import {
  FINANCING_MODELS,
  MST_BANKS_URL,
  MST_CURRENCY_URL,
  Roles,
} from "app/c1utils/const";
import NumFormat from "app/clictruckcomponent/NumFormat";
import { getValue } from "app/c1utils/utility";
import C1TextArea from "app/c1component/C1TextArea";
import SupervisorAccountOutlinedIcon from "@material-ui/icons/SupervisorAccountOutlined";
import LocalAtmOutlinedIcon from "@material-ui/icons/LocalAtmOutlined";
import PersonOutlineOutlinedIcon from "@material-ui/icons/PersonOutlineOutlined";
import AccountBalanceOutlinedIcon from "@material-ui/icons/AccountBalanceOutlined";
import useAuth from "app/hooks/useAuth";

const mstUnit = [
  { value: "P", desc: "%" },
  { value: "F", desc: "Fixed" },
];

const ContractReqDetails = ({
  inputData,
  handleInputChange,
  handleDateChange,
  viewType,
  isDisabled,
  errors,
  coOptionsUrl,
  toOptionsUrl,
  enableBankCharges,
}) => {
  const { t } = useTranslation(["administration"]);
  const { user } = useAuth();
  let isL1 = false;
  if (
    user?.authorities.some((el) => [Roles.SP_L1.code].includes(el.authority))
  ) {
    isL1 = true;
  }

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
                    name="crId"
                    disabled
                    onChange={handleInputChange}
                    value={getValue(inputData?.crId)}
                  />
                  <C1InputField
                    label={t("administration:contractManagement.details.name")}
                    name="crName"
                    required
                    onChange={handleInputChange}
                    value={getValue(inputData?.crName)}
                    error={errors["crName"] !== undefined}
                    helperText={errors["crName"] || ""}
                    disabled={isDisabled}
                  />
                  <C1TextArea
                    label={t("administration:contractManagement.details.desc")}
                    name="crDescription"
                    multiline
                    textLimit={1024}
                    onChange={handleInputChange}
                    value={getValue(inputData?.crDescription)}
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
                    name="tcoreAccnByCrTo.accnId"
                    label={t(
                      "administration:contractManagement.details.truckOperator"
                    )}
                    value={getValue(inputData?.tcoreAccnByCrTo?.accnId)}
                    onChange={(e) => handleInputChange(e)}
                    isServer
                    required
                    options={{
                      url: toOptionsUrl,
                      key: "account",
                      id: "accnId",
                      desc: "accnName",
                      isCache: false,
                    }}
                    error={errors["TCoreAccnByCrTo.accnId"] !== undefined}
                    helperText={errors["TCoreAccnByCrTo.accnId"] || ""}
                    disabled={isDisabled}
                  />
                  <C1SelectField
                    isServer={true}
                    required
                    disabled={isDisabled}
                    name="tcoreAccnByCrCoFf.accnId"
                    label={"CO / FF"}
                    value={getValue(inputData?.tcoreAccnByCrCoFf?.accnId)}
                    onChange={handleInputChange}
                    options={{
                      url: coOptionsUrl,
                      key: "account",
                      id: "accnId",
                      desc: "accnName",
                      isCache: false,
                    }}
                    error={errors["TCoreAccnByCrCoFf.accnId"] !== undefined}
                    helperText={errors["TCoreAccnByCrCoFf.accnId"] || ""}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>

            <C1CategoryBlock
              icon={<PersonOutlineOutlinedIcon />}
              title={t(
                "administration:contractManagement.details.requestorDetails"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t(
                      "administration:contractManagement.details.username"
                    )}
                    name="crUidRequestor"
                    disabled
                    onChange={handleInputChange}
                    value={getValue(inputData?.crUidRequestor)}
                  />

                  <C1TextArea
                    label={t(
                      "administration:contractManagement.details.comments"
                    )}
                    name="crCommentRequestor"
                    rows={10}
                    rowsMax={10}
                    textLimit={512}
                    disabled={isDisabled}
                    required
                    onChange={handleInputChange}
                    value={getValue(inputData?.crCommentRequestor)}
                    error={errors["crCommentRequestor"] !== undefined}
                    helperText={errors["crCommentRequestor"] || ""}
                  />

                  <C1DateField
                    label={t(
                      "administration:contractManagement.details.creationDt"
                    )}
                    name="crDtCreate"
                    value={getValue(inputData?.crDtCreate)}
                    disabled
                    onChange={handleDateChange}
                    disablePast={true}
                  />

                  <C1DateField
                    label={t(
                      "administration:contractManagement.details.submittedDt"
                    )}
                    name="crDtSubmit"
                    value={getValue(inputData?.crDtSubmit)}
                    disabled
                    onChange={handleDateChange}
                    disablePast={true}
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
                    name="crDtStart"
                    required
                    value={getValue(inputData?.crDtStart)}
                    disabled={isDisabled}
                    onChange={handleDateChange}
                    disablePast={true}
                    error={errors["crDtStart"] !== undefined}
                    helperText={errors["crDtStart"] || ""}
                  />
                  <C1DateField
                    label={t(
                      "administration:contractManagement.details.expiredDate"
                    )}
                    name="crDtEnd"
                    required
                    value={getValue(inputData?.crDtEnd)}
                    disabled={isDisabled}
                    onChange={handleDateChange}
                    disablePast={true}
                    error={errors["crDtEnd"] !== undefined}
                    helperText={errors["crDtEnd"] || ""}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>

            <C1CategoryBlock
              icon={<PersonOutlineOutlinedIcon />}
              title={t(
                "administration:contractManagement.details.approverDetails"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t(
                      "administration:contractManagement.details.username"
                    )}
                    name="crUidApprover"
                    disabled
                    onChange={handleInputChange}
                    value={getValue(inputData?.crUidApprover)}
                  />

                  <C1TextArea
                    label={t(
                      "administration:contractManagement.details.comments"
                    )}
                    name="crCommentApprover"
                    rows={10}
                    rowsMax={10}
                    textLimit={512}
                    disabled
                    value={getValue(inputData?.crCommentApprover)}
                    onChange={handleInputChange}
                  />

                  <C1DateField
                    label={t(
                      "administration:contractManagement.details.appRejDt"
                    )}
                    name="crDtApproveReject"
                    value={inputData?.crDtApproveReject}
                    disabled
                    onChange={handleDateChange}
                    disablePast={true}
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

export default ContractReqDetails;
