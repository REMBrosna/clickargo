import {Grid, MenuItem, TextField} from "@material-ui/core";
import {
  CalendarTodayOutlined,
  GetAppOutlined,
  InfoOutlined,
} from "@material-ui/icons";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import React from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1TextArea from "app/c1component/C1TextArea";
import { CK_MST_CHASSIS_TYPE, CK_MST_VEH_TYPE } from "app/c1utils/const";
import VehExt from "./VehExt";
import {makeStyles} from "@material-ui/core/styles";

const TruckDetails = (props) => {
  const {
    inputData,
    handleInputChange,
    handleInputFileChange,
    handleViewFile,
    isDisabled,
    errors,
    chassisNoList,
  } = props;
  const { t } = useTranslation(["administration", "buttons"]);
  const useStyles = makeStyles({
    multilineOverride: {
      "& .MuiOutlinedInput-multiline": {
        padding: "10px 15px 0px 10px",
      },
    },
  });
  const classes = useStyles();
  const mstClass = [
    { value: "1", desc: "1" },
    { value: "2", desc: "2" },
    { value: "3", desc: "3" },
  ];
  const mstMaintenance = [
    { value: "Y", desc: "Yes" },
    { value: "N", desc: "No" },
  ];

  const onlyNumber = (e) => {
    if (e.charCode < 48) {
      return e.preventDefault();
    }
  };
// Compute valueChassisNo based on conditions
  let valueChassisNo = null;
  if (inputData?.tckCtMstChassisType?.chtyId && inputData?.vhChassisNo?.includes("OTHERS")) {
    if (inputData?.vhChassisNo !== "OTHERS") {
      const values = JSON.parse(inputData?.vhChassisNo);
      valueChassisNo = values?.vhChassisNo;
    } else {
      valueChassisNo = inputData?.vhChassisNo;
    }
  }else{
    valueChassisNo = inputData?.vhChassisNo;
  }
  return (
    <React.Fragment>
      <Grid item xs={12}>
        <C1TabContainer>
          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t(
                "administration:truckManagement.truckDetails.generalDetails"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1InputField
                    label={t("administration:truckManagement.truckDetails.id")}
                    name="vhId"
                    disabled
                    onChange={handleInputChange}
                    value={inputData?.vhId || ""}
                  />
                  <C1SelectAutoCompleteField
                    name="tckCtMstVehType.vhtyId"
                    label={t(
                      "administration:truckManagement.truckDetails.type"
                    )}
                    value={inputData?.tckCtMstVehType?.vhtyId}
                    onChange={(e, name, value) =>
                      handleInputChange({
                        target: { name, value: value?.value },
                      })
                    }
                    required
                    isServer={true}
                    options={{
                      url: CK_MST_VEH_TYPE,
                      key: "vhtyId",
                      id: "vhtyId",
                      desc: "vhtyDesc",
                      isCache: true,
                    }}
                    error={errors["TCkCtMstVehType.vhtyId"] !== undefined}
                    helperText={errors["TCkCtMstVehType.vhtyId"] || ""}
                    disabled={isDisabled}
                  />
                  <C1InputField
                    label={t(
                      "administration:truckManagement.truckDetails.platNo"
                    )}
                    name="vhPlateNo"
                    onChange={handleInputChange}
                    required
                    value={inputData?.vhPlateNo || ""}
                    error={errors["vhPlateNo"] !== undefined}
                    helperText={errors["vhPlateNo"] || ""}
                    disabled={isDisabled}
                  />
                  <Grid
                    container
                    item
                    spacing={4}
                    alignItems="flex-start"
                    alignContent="center"
                  >
                    <Grid item xs={inputData?.vhPhotoName ? 10 : 12}>
                      <C1FileUpload
                        inputProps={{
                          placeholder: t(
                            "administration:truckManagement.truckDetails.noFileChosen"
                          ),
                        }}
                        name="fhotoFileButton"
                        label={t("buttons:browse")}
                        fileChangeHandler={handleInputFileChange}
                        inputLabel={t(
                          "administration:truckManagement.truckDetails.photo"
                        )}
                        value={inputData?.vhPhotoName}
                        disabled={isDisabled}
                        errors={errors["fhotoFileButton"] !== undefined}
                        helperText={errors["fhotoFileButton"] || ""}
                      />
                    </Grid>
                    {inputData?.vhPhotoName && (
                      <Grid item xs={1} style={{ marginTop: "20px" }}>
                        <C1LabeledIconButton
                          tooltip={t("buttons:download")}
                          label={t("buttons:download")}
                          action={() =>
                            handleViewFile(
                              inputData?.vhPhotoName,
                              inputData?.base64File
                            )
                          }
                        >
                          <GetAppOutlined />
                        </C1LabeledIconButton>
                      </Grid>
                    )}
                  </Grid>
                </Grid>
              </Grid>
            </C1CategoryBlock>

            <Grid item style={{ height: "39px" }}></Grid>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t(
                "administration:truckManagement.truckDetails.chasisDetails"
              )}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1SelectField
                    name="tckCtMstChassisType.chtyId"
                    label={t(
                      "administration:truckManagement.truckDetails.size"
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
                  <C1SelectField
                    name="vhChassisNo"
                    label={t(
                      "administration:truckManagement.truckDetails.number"
                    )}
                    value={inputData?.tckCtMstChassisType?.chtyId ? valueChassisNo : ""}
                    onChange={(e) => handleInputChange(e)}
                    isServer={true}
                    optionsMenuItemArr={Object.values(chassisNoList).map(
                      (row) => {
                        if (row.chsNo)
                          return (
                            <MenuItem value={row.chsNo} key={row.chsId}>
                              {row.chsNo}
                            </MenuItem>
                          );
                      }
                    )}
                    error={errors["vhChassisNo"] !== undefined}
                    helperText={errors["vhChassisNo"] || ""}
                    disabled={
                      isDisabled || !inputData?.tckCtMstChassisType?.chtyId
                    }
                  />
                  {inputData?.vhChassisNo?.includes("OTHERS") &&
                    inputData?.tckCtMstChassisType?.chtyId && (
                      <C1InputField
                        label={t(
                          "administration:truckManagement.truckDetails.numberOth"
                        )}
                        name="vhChassisNoOth"
                        onChange={handleInputChange}
                        required
                        value={
                          inputData?.tckCtMstChassisType?.chtyId
                            ? inputData?.vhChassisNoOth
                            : ""
                        }
                        error={errors["vhChassisNoOth"] !== undefined}
                        helperText={errors["vhChassisNoOth"] || ""}
                        disabled={isDisabled}
                      />
                    )}
                </Grid>
              </Grid>
            </C1CategoryBlock>
          </Grid>

          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<CalendarTodayOutlined />}
              title={t("administration:truckManagement.truckDetails.dwv")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <Grid container spacing={1}>
                    <Grid item xs={4}>
                      <C1InputField
                        label="Dimention L"
                        name="vhLength"
                        required
                        onChange={handleInputChange}
                        value={inputData?.vhLength}
                        type="number"
                        error={errors["vhLength"] !== undefined}
                        helperText={errors["vhLength"] || ""}
                        disabled={isDisabled}
                        inputProps={{
                          onKeyPress: onlyNumber,
                        }}
                      />
                    </Grid>
                    <Grid item xs={4}>
                      <C1InputField
                        label="Dimention W"
                        name="vhWidth"
                        required
                        onChange={handleInputChange}
                        value={inputData?.vhWidth}
                        type="number"
                        error={errors["vhWidth"] !== undefined}
                        helperText={errors["vhWidth"] || ""}
                        disabled={isDisabled}
                        inputProps={{
                          onKeyPress: onlyNumber,
                        }}
                      />
                    </Grid>
                    <Grid item xs={4}>
                      <C1InputField
                        label="Dimention H"
                        name="vhHeight"
                        required
                        onChange={handleInputChange}
                        value={inputData?.vhHeight}
                        type="number"
                        error={errors["vhHeight"] !== undefined}
                        helperText={errors["vhHeight"] || ""}
                        disabled={isDisabled}
                        inputProps={{
                          onKeyPress: onlyNumber,
                        }}
                      />
                    </Grid>
                  </Grid>
                  <C1InputField
                    label={t(
                      "administration:truckManagement.truckDetails.maxWeight"
                    )}
                    name="vhWeight"
                    onChange={handleInputChange}
                    required
                    value={inputData?.vhWeight}
                    error={errors["vhWeight"] !== undefined}
                    helperText={errors["vhWeight"] || ""}
                    disabled={isDisabled}
                    inputProps={{
                      onKeyPress: onlyNumber,
                    }}
                  />
                  <C1InputField
                    label={t(
                      "administration:truckManagement.truckDetails.volume"
                    )}
                    name="vhVolume"
                    required
                    onChange={handleInputChange}
                    value={inputData?.vhVolume}
                    error={errors["vhVolume"] !== undefined}
                    helperText={errors["vhVolume"] || ""}
                    disabled={isDisabled}
                    inputProps={{
                      onKeyPress: onlyNumber,
                    }}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>

            <Grid item style={{ height: "39px" }}></Grid>
          </Grid>

          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock icon={<InfoOutlined />} title={t("administration:truckManagement.truckDetails.otherDetails")}>
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <div className={classes.multilineOverride}>
                    <C1TextArea
                        label={t("administration:truckManagement.truckDetails.remark")}
                        name="vhRemarks"
                        multiline
                        textLimit={512}
                        onChange={handleInputChange}
                        disabled={isDisabled}
                        value={inputData?.vhRemarks || ""}
                        sx={{
                          '& .MuiOutlinedInput-multiline': {
                            padding: '0px 0px',
                          }
                        }}
                    />
                  </div>
                    <C1InputField
                        label={t(
                            "administration:truckManagement.truckDetails.department"
                        )}
                        name="department"
                        onChange={handleInputChange}
                        value={inputData?.department}
                        disabled
                    />
                </Grid>
              </Grid>
            </C1CategoryBlock>
            {/* <C1CategoryBlock icon={<NearMeOutlined />} title={t("administration:truckManagement.truckDetails.truckMonitor")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:truckManagement.truckDetails.imei")}
                                        name="vhGpsImei"
                                        onChange={handleInputChange}
                                        disabled={isDisabled}
                                        value={inputData?.vhGpsImei || ''} />
                                    <C1InputField
                                        label={t("administration:truckManagement.truckDetails.odo")}
                                        name="vhOdo"
                                        onChange={handleInputChange}
                                        disabled={isDisabled}
                                        value={inputData?.vhOdo || ''} />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock> */}

            {/*<VehExt props={props} />*/}
          </Grid>
        </C1TabContainer>
      </Grid>
    </React.Fragment>
  );
};

export default TruckDetails;
