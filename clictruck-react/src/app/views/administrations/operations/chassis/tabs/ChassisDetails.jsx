import { Grid } from "@material-ui/core";
import BorderColorOutlinedIcon from "@material-ui/icons/BorderColorOutlined";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import React from "react";
import { useTranslation } from "react-i18next";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";
import { CK_MST_CHASSIS_TYPE } from "app/c1utils/const";

const ChassisDetails = ({
  inputData,
  handleInputChange,
  handleDateChange,
  handleInputFileChange,
  handleViewFile,
  viewType,
  isDisabled,
  errors,
  fileUploaded,
}) => {
  const { t } = useTranslation(["administration", "common"]);

  return (
    <React.Fragment>
      <Grid item xs={12}>
        <C1TabContainer>
          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t(
                "administration:chassisManagement.chassisDetails.generalDetails"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t(
                      "administration:chassisManagement.chassisDetails.id"
                    )}
                    name="chsId"
                    disabled
                    onChange={handleInputChange}
                    value={inputData?.chsId || ""}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>
          </Grid>

          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t(
                "administration:chassisManagement.chassisDetails.chassisDetails"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1SelectField
                    name="tckCtMstChassisType.chtyId"
                    label={t(
                      "administration:chassisManagement.chassisDetails.chsSize"
                    )}
                    value={inputData?.tckCtMstChassisType?.chtyId}
                    onChange={(e) => handleInputChange(e)}
                    isServer={true}
                    options={{
                      url: CK_MST_CHASSIS_TYPE,
                      key: "chtyId",
                      id: "chtyId",
                      desc: "chtyDesc",
                      isCache: true,
                    }}
                    error={errors["tckCtMstChassisType.chtyId"] !== undefined}
                    helperText={errors["tckCtMstChassisType.chtyId"] || ""}
                    disabled={isDisabled}
                  />
                  <C1InputField
                    label={t(
                      "administration:chassisManagement.chassisDetails.chsNo"
                    )}
                    name="chsNo"
                    onChange={handleInputChange}
                    value={inputData?.chsNo}
                    required
                    error={errors["chsNo"] !== undefined}
                    helperText={errors["chsNo"] || ""}
                    disabled={isDisabled}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>
          </Grid>

          <Grid container item lg={4} md={6} xs={12} direction="column">
            <C1CategoryBlock
              icon={<BorderColorOutlinedIcon />}
              title={t(
                "administration:chassisManagement.chassisDetails.properties"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t(
                      "administration:chassisManagement.chassisDetails.createdBy"
                    )}
                    value={inputData?.chsUidCreate}
                    name="chsUidCreate"
                    required
                    disabled
                    onChange={handleInputChange}
                  />

                  <C1DateField
                    label={t(
                      "administration:chassisManagement.chassisDetails.createdDate"
                    )}
                    name="chsDtCreate"
                    required
                    value={inputData?.chsDtCreate}
                    disabled
                    onChange={handleDateChange}
                    disablePast={true}
                  />

                  <C1InputField
                    label={t(
                      "administration:chassisManagement.chassisDetails.updatedBy"
                    )}
                    value={inputData?.chsUidLupd}
                    name="chsUidLupd"
                    required
                    disabled
                    onChange={handleInputChange}
                  />

                  <C1DateField
                    label={t(
                      "administration:chassisManagement.chassisDetails.updatedDate"
                    )}
                    name="chsDtLupd"
                    required
                    value={inputData?.chsDtLupd}
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

export default ChassisDetails;
