import React from "react";
import { Grid, MenuItem } from "@material-ui/core";
import C1InputField from "app/c1component/C1InputField";
import { getValue } from "app/c1utils/utility";

import C1DateField from "app/c1component/C1DateField";
import { useTranslation } from "react-i18next";
import C1SelectField from "app/c1component/C1SelectField";

const JobProperties = ({
  inputData,
  errors,
  handleInputChange,
  handleDateChange,
}) => {
  const { t } = useTranslation(["common"]);

  const mobileJobOptions = [
    { value: "Y", desc: "Yes" },
    { value: "N", desc: "No" },
  ];

  const financeOptions = [
    { value: "N", desc: "Non-Finance" },
    { value: "F", desc: "Finance" },
    { value: "E", desc: "Finance Extension" },
  ];

  return (
    <Grid container alignItems="center" spacing={3}>
      <Grid item xs={12}>
        <C1InputField
          label={t("common:common.label.createdBy")}
          value={getValue(inputData?.jobUidCreate)}
          disabled={true}
          onChange={handleInputChange}
          error={errors["jobUidCreate"] !== undefined}
          helperText={errors["jobUidCreate"] || ""}
        />
        <C1DateField
          label={t("common:common.label.createdDt")}
          name="jobDtCreate"
          value={getValue(inputData?.jobDtCreate)}
          disabled={true}
          onChange={handleDateChange}
          disablePast={true}
          minDate={inputData?.jobDtCreate ? inputData?.jobDtCreate : new Date()}
        />
        <C1InputField
          label={t("common:common.label.updatedBy")}
          value={getValue(inputData?.jobUidLupd)}
          name="jobUidLupd"
          disabled={true}
          onChange={handleInputChange}
          error={errors["jobUidLupd"] !== undefined}
          helperText={errors["jobUidLupd"] || ""}
        />
        <C1DateField
          label={t("common:common.label.updatedDt")}
          name="jobDtLupd"
          value={getValue(inputData?.jobDtLupd)}
          disabled={true}
          onChange={handleDateChange}
          disablePast={true}
        />

        <Grid container spacing={2} direction="row" alignItems="center">
          <Grid item xs={6}>
            <C1SelectField
              label={t("common:common.label.mobileEnabled")}
              name="jobMobileEnabled"
              value={inputData?.jobMobileEnabled || ""}
              disabled={true}
              optionsMenuItemArr={mobileJobOptions.map((item, ind) => (
                <MenuItem value={item.value} key={ind}>
                  {item.desc}
                </MenuItem>
              ))}
            />
          </Grid>
          {/** Added finance options in Job Properties */}
          <Grid item xs={6}>
            <C1SelectField
              label={t("common:common.label.financeOpt")}
              name="jobIsFinanced"
              value={inputData?.jobIsFinanced || ""}
              disabled={true}
              optionsMenuItemArr={financeOptions.map((item, ind) => (
                <MenuItem value={item.value} key={ind}>
                  {item.desc}
                </MenuItem>
              ))}
            />
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
};

export default JobProperties;
