import { Grid } from "@material-ui/core";
import BorderColorOutlinedIcon from "@material-ui/icons/BorderColorOutlined";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import React from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";

const CO2XDetails = ({
  inputData,
  handleInputChange,
  handleDateChange,
  viewType,
  isDisabled,
  errors,
}) => {
  const { t } = useTranslation(["administration", "common"]);

  let isFilter = viewType === "new" ? "Y" : "N";
  return (
    <React.Fragment>
      <Grid item xs={12}>
        <C1TabContainer>
          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t("administration:co2x.form.catGeneralDetails")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1SelectAutoCompleteField
                    name="tcoreAccn.accnId"
                    label={t("administration:co2x.form.accnId")}
                    options={{
                      url: `/api/v1/clickargo/clictruck/selectOptions/co2xAccns?isFilter=${isFilter}`,
                      id: "accnId",
                      desc: "accnName",
                    }}
                    required
                    isServer={true}
                    value={inputData?.tcoreAccn?.accnId || ""}
                    disabled={viewType === "new" ? false : true}
                    onChange={(e, name, value) =>
                      handleInputChange({
                        target: { name, value: value?.value },
                      })
                    }
                    error={errors["tcoreAccn.accnId"] !== undefined}
                    helperText={errors["tcoreAccn.accnId"] || ""}
                  />

                  <C1InputField
                    label={t("administration:co2x.form.companyId")}
                    name="co2xCoyId"
                    disabled={isDisabled}
                    required
                    onChange={handleInputChange}
                    value={inputData?.co2xCoyId || ""}
                    error={errors["co2xCoyId"] !== undefined}
                    helperText={errors["co2xCoyId"] || ""}
                  />
                  <C1DateField
                    label={t("administration:co2x.form.expiryDate")}
                    value={inputData?.co2xDtExpiry}
                    onChange={handleDateChange}
                    disabled={isDisabled}
                    required
                    name="co2xDtExpiry"
                    error={errors["co2xDtExpiry"] !== undefined}
                    helperText={errors["co2xDtExpiry"] || ""}
                    disablePast
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>

            <Grid item style={{ height: "39px" }}></Grid>
          </Grid>

          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t("administration:co2x.form.catCredentials")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t("administration:co2x.form.userId")}
                    name="co2xUid"
                    required
                    onChange={handleInputChange}
                    value={inputData?.co2xUid}
                    error={errors["co2xUid"] !== undefined}
                    helperText={errors["co2xUid"] || ""}
                    disabled={isDisabled}
                  />
                  <C1InputField
                    label={t("administration:co2x.form.userPwd")}
                    name="co2xPwd"
                    required
                    onChange={handleInputChange}
                    value={inputData?.co2xPwd}
                    error={errors["co2xPwd"] !== undefined}
                    helperText={errors["co2xPwd"] || ""}
                    disabled={isDisabled}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>

            <Grid item style={{ height: "39px" }}></Grid>
          </Grid>

          <Grid container item lg={4} md={6} xs={12} direction="column">
            <C1CategoryBlock
              icon={<BorderColorOutlinedIcon />}
              title={t(
                "administration:driverManagement.driverDetails.properties"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t(
                      "administration:driverManagement.driverDetails.createdBy"
                    )}
                    value={inputData?.co2xUidCreate}
                    name="co2xUidCreate"
                    required
                    disabled
                    onChange={handleInputChange}
                  />

                  <C1DateField
                    label={t(
                      "administration:driverManagement.driverDetails.createdDate"
                    )}
                    name="co2xDtCreate"
                    required
                    value={inputData?.co2xDtCreate || ""}
                    disabled
                    onChange={handleDateChange}
                    disablePast={true}
                  />

                  <C1InputField
                    label={t(
                      "administration:driverManagement.driverDetails.updatedBy"
                    )}
                    value={inputData?.co2xUidLupd}
                    name="co2xUidLupd"
                    required
                    disabled
                    onChange={handleInputChange}
                  />

                  <C1DateField
                    label={t(
                      "administration:driverManagement.driverDetails.updatedDate"
                    )}
                    name="co2xDtLupd"
                    required
                    value={inputData?.co2xDtLupd || ""}
                    disabled
                    onChange={handleDateChange}
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

export default CO2XDetails;
