import { Grid, IconButton, InputLabel, Select } from "@material-ui/core";
import BorderColorOutlinedIcon from "@material-ui/icons/BorderColorOutlined";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import React from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1TextArea from "app/c1component/C1TextArea";
import ArrowBackIosIcon from "@material-ui/icons/ArrowBackIos";
import ArrowForwardIosIcon from "@material-ui/icons/ArrowForwardIos";
import { colorCodes } from "./deptconst";

export default function DepartmentDetails({
  inputData,
  handleInputChange,
  handleChangeMultiple,
  handleDateChange,
  viewType,
  isDisabled,
  errors,
  selectedAccnUsers,
  selectedAccnVehs,
  selectedUsers,
  selectedVehs,
  handleSelectedToAvail,
  handleAvailToSelected,
}) {
  const { t } = useTranslation(["administration", "common"]);

  return (
    <React.Fragment>
      <Grid item xs={12}>
        <C1TabContainer>
          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t("administration:department.form.catGeneralDetails")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t("administration:department.form.accn")}
                    name="tcoreAccn.accnName"
                    disabled
                    onChange={handleInputChange}
                    value={inputData?.tcoreAccn?.accnName || ""}
                  />

                  <C1InputField
                    label={t("administration:department.form.name")}
                    name="deptName"
                    disabled={isDisabled}
                    required
                    onChange={handleInputChange}
                    value={inputData?.deptName || ""}
                    error={errors["deptName"] !== undefined}
                    helperText={errors["deptName"] || ""}
                  />
                  <C1TextArea
                    name="deptDesc"
                    label={t("administration:department.form.desc")}
                    multiline
                    textLimit={1024}
                    value={inputData?.deptDesc || ""}
                    onChange={handleInputChange}
                    disabled={isDisabled}
                  />
                  <C1SelectAutoCompleteField
                    name="deptColor"
                    label={t("administration:department.form.colorCode")}
                    optionsMenuItemArr={colorCodes.map((item, i) => {
                      return {
                        value: item.code,
                        desc: item.value,
                      };
                    })}
                    required
                    value={inputData?.deptColor || ""}
                    disabled={isDisabled}
                    onChange={(e, name, value) =>
                      handleInputChange({
                        target: { name, value: value?.value },
                      })
                    }
                    error={errors["deptColor"] !== undefined}
                    helperText={errors["deptColor"] || ""}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>

            <Grid item style={{ height: "39px" }}></Grid>
          </Grid>

          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t("administration:department.form.catUsers")}
            >
              <Grid
                container
                alignItems="center"
                spacing={3}
                direction="row"
                justifyContent="center"
                style={{ paddingTop: 10 }}
              >
                <Grid item xs={5}>
                  <InputLabel shrink htmlFor="select-multiple-native">
                    {t("administration:department.form.available")}
                  </InputLabel>
                  <Select
                    size="medium"
                    margin="dense"
                    value={selectedAccnUsers}
                    name="accnUsers"
                    multiple
                    native
                    disableunderline="true"
                    displayEmpty
                    variant="outlined"
                    fullWidth
                    onChange={(e) => {
                      handleChangeMultiple(e);
                    }}
                    inputProps={{
                      id: "select-multiple-native",
                      style: { height: 250 },
                    }}
                    disabled={isDisabled}
                  >
                    {inputData?.accnUsers &&
                      inputData?.accnUsers?.map((d, ind) => (
                        <option value={d.usrUid} key={ind}>
                          {d.usrName}
                        </option>
                      ))}
                  </Select>
                </Grid>
                <Grid
                  container
                  item
                  xs={2}
                  justifyContent="center"
                  alignItems="center"
                  direction="column"
                >
                  <Grid item>
                    <IconButton
                      onClick={(e) => handleAvailToSelected(e, "users")}
                      disabled={isDisabled}
                    >
                      <ArrowForwardIosIcon />
                    </IconButton>
                  </Grid>
                  <Grid item>
                    <IconButton
                      onClick={(e) => handleSelectedToAvail(e, "users")}
                      disabled={isDisabled}
                    >
                      <ArrowBackIosIcon />
                    </IconButton>
                  </Grid>
                </Grid>
                <Grid item xs={5}>
                  <InputLabel shrink htmlFor="select-multiple-native">
                    {t("administration:department.form.selected")}
                  </InputLabel>
                  <Select
                    size="medium"
                    margin="dense"
                    value={selectedUsers}
                    name="deptUsers"
                    multiple
                    native
                    disableunderline="true"
                    displayEmpty
                    variant="outlined"
                    fullWidth
                    onChange={(e) => {
                      handleChangeMultiple(e);
                    }}
                    inputProps={{
                      id: "select-multiple-native",
                      style: { height: 250 },
                    }}
                    disabled={isDisabled}
                  >
                    {inputData?.deptUsers &&
                      inputData?.deptUsers?.map((d, ind) => (
                        <option value={d.usrUid} key={ind}>
                          {d.usrName}
                        </option>
                      ))}
                  </Select>
                </Grid>
              </Grid>
            </C1CategoryBlock>
            <Grid item style={{ height: "39px" }}></Grid>
            {inputData?.accnVehs && (
              <C1CategoryBlock
                icon={<DescriptionIcon />}
                title={t("administration:department.form.catVeh")}
              >
                <Grid
                  container
                  alignItems="center"
                  spacing={3}
                  direction="row"
                  justifyContent="center"
                  style={{ paddingTop: 10 }}
                >
                  <Grid item xs={5}>
                    <InputLabel shrink htmlFor="select-multiple-native">
                      {t("administration:department.form.available")}
                    </InputLabel>
                    <Select
                      size="medium"
                      margin="dense"
                      value={selectedAccnVehs}
                      name="accnVehs"
                      multiple
                      native
                      disableunderline="true"
                      displayEmpty
                      variant="outlined"
                      fullWidth
                      onChange={(e) => {
                        handleChangeMultiple(e);
                      }}
                      inputProps={{
                        id: "select-multiple-native",
                        style: { height: 250 },
                      }}
                      disabled={isDisabled}
                    >
                      {inputData?.accnVehs?.map((d, ind) => (
                        <option value={d.vhId} key={ind}>
                          {d.vhPlateNo}
                        </option>
                      ))}
                    </Select>
                  </Grid>
                  <Grid
                    container
                    item
                    xs={2}
                    justifyContent="center"
                    alignItems="center"
                    direction="column"
                  >
                    <Grid item>
                      <IconButton
                        onClick={(e) => handleAvailToSelected(e, "vehs")}
                        disabled={isDisabled}
                      >
                        <ArrowForwardIosIcon />
                      </IconButton>
                    </Grid>
                    <Grid item>
                      <IconButton
                        onClick={(e) => handleSelectedToAvail(e, "vehs")}
                        disabled={isDisabled}
                      >
                        <ArrowBackIosIcon />
                      </IconButton>
                    </Grid>
                  </Grid>
                  <Grid item xs={5}>
                    <InputLabel shrink htmlFor="select-multiple-native">
                      {t("administration:department.form.selected")}
                    </InputLabel>
                    <Select
                      size="medium"
                      margin="dense"
                      value={selectedVehs}
                      name="deptVehs"
                      multiple
                      native
                      disableunderline="true"
                      displayEmpty
                      variant="outlined"
                      fullWidth
                      disabled={isDisabled}
                      onChange={(e) => {
                        handleChangeMultiple(e);
                      }}
                      inputProps={{
                        id: "select-multiple-native",
                        style: { height: 250 },
                      }}
                    >
                      {inputData?.deptVehs &&
                        inputData?.deptVehs?.map((d, ind) => (
                          <option value={d.vhId} key={ind}>
                            {d.vhPlateNo}
                          </option>
                        ))}
                    </Select>
                  </Grid>
                </Grid>
              </C1CategoryBlock>
            )}
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
                    value={inputData?.deptUidCreate}
                    name="deptUidCreate"
                    required
                    disabled
                    onChange={handleInputChange}
                  />

                  <C1DateField
                    label={t(
                      "administration:driverManagement.driverDetails.createdDate"
                    )}
                    name="deptDtCreate"
                    required
                    value={inputData?.deptDtCreate || ""}
                    disabled
                    onChange={handleDateChange}
                    disablePast={true}
                  />

                  <C1InputField
                    label={t(
                      "administration:driverManagement.driverDetails.updatedBy"
                    )}
                    value={inputData?.deptUidLupd}
                    name="deptUidLupd"
                    required
                    disabled
                    onChange={handleInputChange}
                  />

                  <C1DateField
                    label={t(
                      "administration:driverManagement.driverDetails.updatedDate"
                    )}
                    name="deptDtLupd"
                    required
                    value={inputData?.deptDtLupd || ""}
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
}
