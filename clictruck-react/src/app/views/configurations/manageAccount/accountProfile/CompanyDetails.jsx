import Grid from "@material-ui/core/Grid";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import PhoneInTalkOutlinedIcon from "@material-ui/icons/PhoneInTalkOutlined";
import RoomOutlinedIcon from "@material-ui/icons/RoomOutlined";
import React from "react";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";

import { useStyles } from "app/c1component/C1Styles";
import C1TabContainer from "app/c1component/C1TabContainer";
import {
  AccountTypes,
  MST_ACCN_TYPE_URL,
  MST_CTRY_URL,
} from "app/c1utils/const";
import { getValue, isEditable } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import { InputAdornment } from "@material-ui/core";
import CircularProgress from "@material-ui/core/CircularProgress";

const AccountDetail = ({
  inputData,
  handleInputChange,
  handleUniqueness,
  uniqueTaxCheck,
  viewType,
  errors,
  locale,
}) => {
  const classes = useStyles();
  let isDisabled = isEditable(viewType);

  const { user } = useAuth();

  //only allow GLI to edit/update the company registration/tax no.
  let isTaxNumEditable =
    user?.coreAccn?.TMstAccnType.atypId === AccountTypes.ACC_TYPE_SP.code;

  return (
    <React.Fragment>
      <C1TabContainer>
        <Grid item lg={3} md={6} xs={12}>
          <Grid
            container
            alignItems="center"
            spacing={3}
            className={classes.gridContainer}
          >
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={locale("companyDetails.generalDetails")}
            >
              <Grid item xs={12}>
                <C1InputField
                  label={locale("companyDetails.accnrCoIntial")}
                  name="accnDetails.accnId"
                  disabled
                  required
                  onChange={handleInputChange}
                  value={getValue(inputData?.accnDetails?.accnId)}
                  error={errors["accnDetails.accnId"] !== undefined}
                  helperText={
                    errors["accnDetails.accnId"] !== undefined
                      ? locale(`validations.${errors["accnDetails.accnId"]}`)
                      : ""
                  }
                />

                <C1SelectAutoCompleteField
                  label={locale("companyDetails.atypId")}
                  name="accnDetails.TMstAccnType.atypId"
                  disabled={
                    inputData?.accnDetails?.accnStatus === "N" &&
                    viewType !== "view"
                      ? false
                      : true
                  }
                  required
                  onChange={(e, name, value) =>
                    handleInputChange({ target: { name, value: value?.value } })
                  }
                  value={
                    getValue(inputData?.accnDetails?.TMstAccnType?.atypId) || ""
                  }
                  isServer={true}
                  options={{
                    url: MST_ACCN_TYPE_URL,
                    id: "atypId",
                    desc: "atypDescription",
                    isCache: true,
                  }}
                  error={
                    errors["accnDetails.TMstAccnType.atypId"] !== undefined
                  }
                  helperText={
                    errors["accnDetails.TMstAccnType.atypId"] !== undefined
                      ? locale(
                          `validations.${errors["accnDetails.TMstAccnType.atypId"]}`
                        )
                      : ""
                  }
                />

                <C1InputField
                  label={locale("companyDetails.accnName")}
                  name="accnDetails.accnName"
                  required
                  disabled={isDisabled}
                  onChange={handleInputChange}
                  value={getValue(inputData?.accnDetails?.accnName)}
                  error={
                    errors["accnDetails.accnName"] ||
                    errors["accnDetails.accnNameDuplicate"] !== undefined
                      ? true
                      : false
                  }
                  helperText={
                    errors["accnDetails.accnName"] !== undefined
                      ? locale(`validations.${errors["accnDetails.accnName"]}`)
                      : errors["accnDetails.accnNameDuplicate"] !== undefined
                      ? locale(
                          `validations.${errors["accnDetails.accnNameDuplicate"]}`
                        )
                      : null
                  }
                />

                <C1InputField
                  label={locale("companyDetails.accnCoyRegn")}
                  name="accnDetails.accnCoyRegn"
                  required
                  disabled={!isTaxNumEditable ? true : isDisabled}
                  onChange={handleInputChange}
                  onBlur={handleUniqueness}
                  value={getValue(inputData?.accnDetails?.accnCoyRegn)}
                  inputProps={{ maxLength: 20 }}
                  error={
                    errors["accnDetails.accnCoyRegn"] !== undefined
                      ? true
                      : uniqueTaxCheck?.errMsg ?? false
                  }
                  helperText={
                    errors["accnDetails.accnCoyRegn"] !== undefined
                      ? locale(
                          `validations.${errors["accnDetails.accnCoyRegn"]}`
                        )
                      : uniqueTaxCheck?.errMsg ?? ""
                  }
                  InputProps={
                    uniqueTaxCheck && uniqueTaxCheck?.loading
                      ? {
                          endAdornment: (
                            <InputAdornment position="end">
                              <CircularProgress />
                            </InputAdornment>
                          ),
                        }
                      : null
                  }
                  //   InputProps={
                  //     uniqueTaxCheck && uniqueTaxCheck?.loading ? (
                  //       <InputAdornment position="end">
                  //         <CircularProgress />
                  //       </InputAdornment>
                  //     ) : null
                  //   }
                />
              </Grid>
            </C1CategoryBlock>
          </Grid>
        </Grid>

        <Grid item lg={3} md={6} xs={12}>
          <Grid
            container
            alignItems="center"
            spacing={3}
            className={classes.gridContainer}
          >
            <C1CategoryBlock
              icon={<PhoneInTalkOutlinedIcon />}
              title={locale("companyDetails.contactDetails")}
            >
              <Grid item xs={12}>
                <C1InputField
                  label={locale("companyDetails.contactTel")}
                  name="accnDetails.accnContact.contactTel"
                  required
                  disabled={isDisabled}
                  onChange={handleInputChange}
                  value={
                    getValue(inputData?.accnDetails?.accnContact?.contactTel) ||
                    ""
                  }
                  inputProps={{
                    placeholder:
                      viewType === "view"
                        ? ""
                        : locale("common:common.placeHolder.contactTel"),
                    maxLength: 100,
                  }}
                  error={
                    errors["accnDetails.accnContact.contactTel"] ||
                    errors["accnDetails.accnContact.telInvalid"] ||
                    errors["accnDetails.accnContact.minLength"] !== undefined
                      ? true
                      : false
                  }
                  helperText={
                    errors["accnDetails.accnContact.contactTel"] !== undefined
                      ? locale(
                          `validations.${errors["accnDetails.accnContact.contactTel"]}`
                        )
                      : errors["accnDetails.accnContact.minLength"] !==
                        undefined
                      ? locale(
                          `validations.${errors["accnDetails.accnContact.minLength"]}`
                        )
                      : errors["accnDetails.accnContact.telInvalid"] !==
                        undefined
                      ? locale(
                          `validations.${errors["accnDetails.accnContact.telInvalid"]}`
                        )
                      : null
                  }
                />

                <C1InputField
                  label={locale("companyDetails.contactFax")}
                  name="accnDetails.accnContact.contactFax"
                  disabled={isDisabled}
                  onChange={handleInputChange}
                  inputProps={{
                    placeholder:
                      viewType === "view"
                        ? ""
                        : locale("common:common.placeHolder.contactTel"),
                    maxLength: 25,
                  }}
                  value={
                    getValue(inputData?.accnDetails?.accnContact?.contactFax) ||
                    ""
                  }
                  error={
                    errors["accnDetails.accnContact.contactFaxMin"] ||
                    errors["accnDetails.accnContact.faxInvalid"] !== undefined
                      ? true
                      : false
                  }
                  helperText={
                    errors["accnDetails.accnContact.contactFaxMin"] !==
                    undefined
                      ? locale(
                          `validations.${errors["accnDetails.accnContact.contactFaxMin"]}`
                        )
                      : errors["accnDetails.accnContact.faxInvalid"] !==
                        undefined
                      ? locale(
                          `validations.${errors["accnDetails.accnContact.faxInvalid"]}`
                        )
                      : null
                  }
                />

                <C1InputField
                  label={locale("companyDetails.contactEmail")}
                  name="accnDetails.accnContact.contactEmail"
                  required
                  disabled={isDisabled}
                  onChange={handleInputChange}
                  inputProps={{
                    placeholder:
                      viewType === "view"
                        ? ""
                        : locale("common:common.placeHolder.contactEmail"),
                    maxLength: 128,
                  }}
                  value={getValue(
                    inputData?.accnDetails?.accnContact?.contactEmail
                  )}
                  error={
                    errors["accnDetails.accnContact.contactEmail"] ||
                    errors["accnDetails.accnContact.emailInvalid"] !== undefined
                      ? true
                      : false
                  }
                  helperText={
                    errors["accnDetails.accnContact.contactEmail"] !== undefined
                      ? locale(
                          `validations.${errors["accnDetails.accnContact.contactEmail"]}`
                        )
                      : errors["accnDetails.accnContact.emailInvalid"] !==
                        undefined
                      ? locale(
                          `validations.${errors["accnDetails.accnContact.emailInvalid"]}`
                        )
                      : null
                  }
                />
              </Grid>
            </C1CategoryBlock>
          </Grid>
        </Grid>

        <Grid item lg={6} md={6} xs={12}>
          <Grid className={classes.gridContainer}>
            <C1CategoryBlock
              icon={<RoomOutlinedIcon />}
              title={locale("companyDetails.address")}
            >
              <Grid container alignItems="center" spacing={6}>
                <Grid
                  item
                  style={{ paddingBottom: "0px" }}
                  xs={12}
                  md={12}
                  lg={6}
                >
                  <Grid container alignItems="center" spacing={6}>
                    <Grid item xs={12}>
                      <C1InputField
                        label={locale("companyDetails.addrLn1")}
                        name="accnDetails.accnAddr.addrLn1"
                        required
                        disabled={isDisabled}
                        onChange={handleInputChange}
                        value={
                          getValue(inputData?.accnDetails?.accnAddr?.addrLn1) ||
                          ""
                        }
                        inputProps={{ maxLength: 70 }}
                        error={
                          errors["accnDetails.accnAddr.addrLn1"] !== undefined
                        }
                        helperText={
                          errors["accnDetails.accnAddr.addrLn1"] !== undefined
                            ? locale(
                                `validations.${errors["accnDetails.accnAddr.addrLn1"]}`
                              )
                            : ""
                        }
                      />
                    </Grid>
                  </Grid>
                </Grid>
                <Grid
                  item
                  style={{ paddingBottom: "0px" }}
                  xs={12}
                  md={12}
                  lg={6}
                >
                  <Grid container alignItems="center" spacing={6}>
                    <Grid item xs={12}>
                      <C1InputField
                        label={locale("companyDetails.addrProv")}
                        name="accnDetails.accnAddr.addrProv"
                        disabled={isDisabled}
                        required
                        onChange={handleInputChange}
                        value={
                          getValue(
                            inputData?.accnDetails?.accnAddr?.addrProv
                          ) || ""
                        }
                        inputProps={{ maxLength: 15 }}
                        error={
                          errors["accnDetails.accnAddr.addrProv"] !== undefined
                        }
                        helperText={
                          errors["accnDetails.accnAddr.addrProv"] !== undefined
                            ? locale(
                                `validations.${errors["accnDetails.accnAddr.addrProv"]}`
                              )
                            : ""
                        }
                      />
                    </Grid>
                  </Grid>
                </Grid>
              </Grid>
              <Grid
                item
                style={{ paddingTop: "0px", paddingBottom: "0px" }}
                xs={12}
                md={12}
                lg={12}
              >
                <Grid container alignItems="center" spacing={6}>
                  <Grid item xs={6}>
                    <C1InputField
                      label={locale("companyDetails.addrLn2")}
                      name="accnDetails.accnAddr.addrLn2"
                      disabled={isDisabled}
                      required
                      onChange={handleInputChange}
                      value={
                        getValue(inputData?.accnDetails?.accnAddr?.addrLn2) ||
                        ""
                      }
                      inputProps={{ maxLength: 70 }}
                      error={
                        errors["accnDetails.accnAddr.addrLn2"] !== undefined
                      }
                      helperText={
                        errors["accnDetails.accnAddr.addrLn2"] !== undefined
                          ? locale(
                              `validations.${errors["accnDetails.accnAddr.addrLn2"]}`
                            )
                          : ""
                      }
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <C1InputField
                      label={locale("companyDetails.addrCity")}
                      name="accnDetails.accnAddr.addrCity"
                      disabled={isDisabled}
                      required
                      onChange={handleInputChange}
                      value={
                        getValue(inputData?.accnDetails?.accnAddr?.addrCity) ||
                        ""
                      }
                      error={
                        errors["accnDetails.accnAddr.addrCity"] !== undefined
                      }
                      helperText={
                        errors["accnDetails.accnAddr.addrCity"] !== undefined
                          ? locale(
                              `validations.${errors["accnDetails.accnAddr.addrCity"]}`
                            )
                          : ""
                      }
                    />
                  </Grid>
                </Grid>
              </Grid>
              <Grid container alignItems="center" spacing={6}>
                <Grid
                  item
                  style={{ paddingTop: "0px", paddingBottom: "0px" }}
                  xs={12}
                  md={12}
                  lg={6}
                >
                  <Grid container alignItems="center" spacing={3}>
                    <Grid item xs={12}>
                      <C1InputField
                        label={locale("companyDetails.addrLn3")}
                        name="accnDetails.accnAddr.addrLn3"
                        disabled={isDisabled}
                        onChange={handleInputChange}
                        inputProps={{ maxLength: 70 }}
                        value={
                          getValue(inputData?.accnDetails?.accnAddr?.addrLn3) ||
                          ""
                        }
                      />
                    </Grid>
                  </Grid>
                </Grid>
                <Grid
                  item
                  style={{ paddingTop: "0px", paddingBottom: "0px" }}
                  xs={12}
                  md={12}
                  lg={6}
                >
                  <Grid container alignItems="center" spacing={6}>
                    <Grid item xs={12}>
                      <C1InputField
                        label={locale("companyDetails.addrPcode")}
                        name="accnDetails.accnAddr.addrPcode"
                        disabled={isDisabled}
                        required
                        onChange={handleInputChange}
                        value={
                          getValue(
                            inputData?.accnDetails?.accnAddr?.addrPcode
                          ) || ""
                        }
                        inputProps={{ maxLength: 17 }}
                        error={
                          errors["accnDetails.accnAddr.addrPcode"] !== undefined
                        }
                        helperText={
                          errors["accnDetails.accnAddr.addrPcode"] !== undefined
                            ? locale(
                                `validations.${errors["accnDetails.accnAddr.addrPcode"]}`
                              )
                            : ""
                        }
                      />
                    </Grid>
                  </Grid>
                </Grid>
              </Grid>
              <Grid
                item
                style={{ paddingTop: "0px", paddingBottom: "0px" }}
                xs={12}
                md={12}
                lg={12}
              >
                <Grid container alignItems="center" spacing={6}>
                  <Grid item xs={6}></Grid>
                  <Grid item xs={6}>
                    <C1SelectAutoCompleteField
                      label={locale("companyDetails.ctyCode")}
                      name="accnDetails.accnAddr.addrCtry.ctyCode"
                      required
                      disabled={isDisabled}
                      onChange={(e, name, value) =>
                        handleInputChange({
                          target: { name, value: value?.value },
                        })
                      }
                      value={
                        getValue(
                          inputData?.accnDetails?.accnAddr?.addrCtry?.ctyCode
                        ) || ""
                      }
                      isServer={true}
                      isShowCode={true}
                      options={{
                        url: MST_CTRY_URL,
                        key: "country",
                        id: "ctyCode",
                        desc: "ctyDescription",
                        isCache: true,
                      }}
                      error={
                        errors["accnDetails.accnAddr.addrCtry.ctyCode"] !==
                        undefined
                      }
                      helperText={
                        errors["accnDetails.accnAddr.addrCtry.ctyCode"] !==
                        undefined
                          ? locale(
                              `validations.${errors["accnDetails.accnAddr.addrCtry.ctyCode"]}`
                            )
                          : ""
                      }
                    />
                  </Grid>
                </Grid>
              </Grid>
            </C1CategoryBlock>
          </Grid>
        </Grid>
      </C1TabContainer>
    </React.Fragment>
  );
};

export default AccountDetail;
