import Grid from "@material-ui/core/Grid";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";

import { useStyles } from "app/c1component/C1Styles";
import C1TabContainer from "app/c1component/C1TabContainer";
import useHttp from "app/c1hooks/http";
import {
  CCM_ACTIVE_ACCOUNT_ALL_URL,
  CCM_GROUP_BY_ACCNID_URL,
  MST_CTRY_URL,
} from "app/c1utils/const";
import { getValue, isEditable } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";

const ManageUserDetails = ({
  inputData,
  usrGroup,
  handleInputChange,
  handleInputTelChange,
  handleAutoComplete,
  viewType,
  errors,
  isSubmitting,
  locale,
  isProfile,
  isSysAdmin,
  isAccnAdmin,
  isGDAdmin,
  isCs,
  accnId,
  isAccnBorder,
}) => {
  const classes = useStyles();

  const { user } = useAuth();
  const history = useHistory();

  let usrIdExist =
    inputData?.coreUsr?.isUserIdExists === true
      ? locale("admin:user.field.alreadyExists")
      : null;
  let usrEmailExist =
    inputData?.coreUsr?.usrUid && inputData?.coreUsr?.isEmailExists === true
      ? locale("admin:user.field.emailNa")
      : null;
  let isDisabled = isEditable(viewType, isSubmitting);
  if (viewType === "newAll") {
    isDisabled = false;
  }

  let [isGroupNotEmpty, setIsGroupNotEmpty] = useState(false);
  const { isLoading, res, error, urlId, sendRequest } = useHttp();

  const [accnArr, setAccnArr] = React.useState([]);
  const handleIsGroupNotEmpty = (e) => {
    handleInputChange(e);

    if (
      inputData?.coreUsr?.TCoreAccn?.accnId &&
      inputData?.coreUsr?.TCoreAccn?.accnId?.toLowerCase().includes("brd")
    ) {
      sendRequest(
        CCM_GROUP_BY_ACCNID_URL +
          getValue(inputData?.coreUsr?.TCoreAccn?.accnId),
        "getGroup",
        "get",
        null
      );
    } else {
      setIsGroupNotEmpty(false);
    }
  };

  useEffect(() => {
    sendRequest(CCM_ACTIVE_ACCOUNT_ALL_URL, "getAccount", "get");
  }, []);

  useEffect(() => {
    if (!isLoading && res) {
      switch (urlId) {
        case "getAccount": {
          setAccnArr(res?.data);
          break;
        }
        case "getGroup": {
          let objArr = res.data && res.data.aaData ? res.data.aaData : res.data;
          if (objArr.length > 0) {
            setIsGroupNotEmpty(true);
          } else {
            setIsGroupNotEmpty(false);
          }
          break;
        }
        default:
          break;
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [res, isLoading, error, urlId, isAccnBorder]);

  //note: for BE development check TCOREUSR for values like coreUsr.usrDept, etc
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
            <Grid item xs={12}>
              <C1InputField
                label={locale("user.details.profile.usrUid")}
                name="coreUsr.usrUid"
                disabled={
                  isProfile ? true : viewType === "edit" ? true : isDisabled
                }
                required
                onChange={handleInputChange}
                value={getValue(inputData?.coreUsr?.usrUid)}
                error={errors?.profile?.usrUid || usrIdExist ? true : false}
                helperText={
                  errors?.profile?.usrUid
                    ? errors.profile.usrUid
                    : usrIdExist || null
                }
                inputProps={{ maxLength: 35 }}
              />
              {viewType === "new" &&
              history?.location?.state?.from === "manageAllUser" ? (
                <C1SelectAutoCompleteField
                  required
                  name="coreUsr.TCoreAccn.accnId"
                  label={locale("user.details.profile.accnId")}
                  value={getValue(inputData?.coreUsr?.TCoreAccn?.accnId)}
                  onChange={(e, name, value) =>
                    handleInputChange({
                      target: { name, value: value?.value },
                    })
                  }
                  isServer={true}
                  options={{
                    url: CCM_ACTIVE_ACCOUNT_ALL_URL,
                    key: "accn",
                    id: "accnId",
                    desc: "accnName",
                    isCache: false,
                  }}
                  error={errors?.profile?.accnId ? true : false}
                  helperText={
                    errors?.profile?.accnId ? errors.profile.accnId : null
                  }
                />
              ) : (
                <C1InputField
                  label={locale("user.details.profile.accnId")}
                  name="coreUsr.TCoreAccn.accnId"
                  disabled={true}
                  onChange={handleInputChange}
                  value={getValue(inputData?.coreUsr?.TCoreAccn?.accnName)}
                />
              )}
              <C1InputField
                label={locale("user.details.profile.usrPassNid")}
                name="coreUsr.usrPassNid"
                disabled={isDisabled}
                required
                onChange={handleInputChange}
                value={getValue(inputData?.coreUsr?.usrPassNid)}
                error={errors?.profile?.usrPassNid ? true : false}
                helperText={
                  errors?.profile?.usrPassNid ? errors.profile.usrPassNid : null
                }
                inputProps={{ maxLength: 20 }}
              />
              <C1InputField
                label={locale("user.details.profile.usrName")}
                name="coreUsr.usrName"
                disabled={isDisabled}
                required
                onChange={handleInputChange}
                value={getValue(inputData?.coreUsr?.usrName)}
                error={errors?.profile?.usrName ? true : false}
                helperText={
                  errors?.profile?.usrName ? errors.profile.usrName : null
                }
                inputProps={{ maxLength: 35 }}
              />
            </Grid>
          </Grid>
        </Grid>
        <Grid item lg={3} md={6} xs={12}>
          <Grid
            container
            alignItems="center"
            spacing={3}
            className={classes.gridContainer}
          >
            <Grid item xs={12}>
              <C1InputField
                label={locale("user.details.profile.usrTitle")}
                name="coreUsr.usrTitle"
                disabled={isDisabled}
                required
                onChange={handleInputChange}
                value={getValue(inputData?.coreUsr?.usrTitle)}
                error={errors?.profile?.usrTitle ? true : false}
                helperText={
                  errors?.profile?.usrTitle ? errors.profile.usrTitle : null
                }
                inputProps={{ maxLength: 35 }}
              />
              <C1InputField
                label={locale("user.details.profile.usrDept")}
                name="coreUsr.usrDept"
                disabled={isDisabled}
                onChange={handleInputChange}
                value={getValue(inputData?.coreUsr?.usrDept)}
                error={errors?.profile?.usrDept ? true : false}
                helperText={
                  errors?.profile?.usrDept ? errors.profile.usrDept : null
                }
                inputProps={{ maxLength: 35 }}
              />

              <Grid container item spacing={1} alignItems="center">
                <Grid item xs={12}>
                  <C1InputField
                    label={locale("user.details.profile.contactTel")}
                    name="coreUsr.usrContact.contactTel"
                    disabled={isDisabled}
                    required
                    onChange={handleInputTelChange}
                    value={getValue(inputData?.coreUsr?.usrContact?.contactTel)}
                    error={errors?.profile?.contactTel ? true : false}
                    helperText={
                      errors?.profile?.contactTel
                        ? errors.profile.contactTel
                        : null
                    }
                    inputProps={{
                      placeholder:
                        viewType === "view"
                          ? ""
                          : locale("common:common.placeHolder.contactTel"),
                      maxLength: 15,
                    }}
                  />
                </Grid>
                {/* <Grid item xs={2} >
                                <FormGroup>
                                    <FormControlLabel
                                        control={<Switch checked={inputData?.enableContactNo === undefined
                                            || inputData?.enableContactNo === 'Y'
                                        }
                                            disabled={true}
                                            name="enableContactNo"
                                            onChange={handleInputChange}
                                        />}
                                    />
                                </FormGroup>
                            </Grid> */}
              </Grid>
              <Grid container item spacing={1} alignItems="center">
                <Grid item xs={12}>
                  <C1InputField
                    label={locale("user.details.profile.contactEmail")}
                    name="coreUsr.usrContact.contactEmail"
                    disabled={isDisabled}
                    required
                    onChange={handleInputChange}
                    value={getValue(
                      inputData?.coreUsr?.usrContact?.contactEmail
                    )}
                    error={
                      errors?.profile?.contactEmail || usrEmailExist
                        ? true
                        : false
                    }
                    helperText={
                      errors?.profile?.contactEmail
                        ? errors.profile.contactEmail
                        : usrEmailExist || null
                    }
                    inputProps={{
                      placeholder:
                        viewType === "view"
                          ? ""
                          : locale("common:common.placeHolder.contactEmail"),
                      maxLength: 128,
                    }}
                  />
                </Grid>
                {/* <Grid item xs={2} >
                                <FormGroup>
                                    <FormControlLabel
                                        control={<Switch checked={inputData?.enableContactEmail === undefined
                                            || inputData?.enableContactEmail === 'Y'
                                        }
                                            disabled={true}
                                            name="enableContactEmail"
                                            onChange={handleInputChange}
                                        />}
                                    />
                                </FormGroup>
                            </Grid> */}
              </Grid>

              {/* <Grid container item spacing={1} alignItems="center">
                            <Grid item xs={9} >
                                <C1InputField
                                    label={"Telegram Id"}
                                    name="usrTelegramChatId"
                                    disabled={isDisabled}
                                    onChange={handleInputChange}
                                    value={getValue(inputData?.usrTelegramChatId) || ''}
                                    error={errors?.profile?.usetTelegramChatId ? true : false}
                                    helperText={errors?.profile?.usetTelegramChatId ? errors.profile.usetTelegramChatId : null}
                                    inputProps={{
                                        maxLength: 50
                                    }} />
                            </Grid>
                            <Grid item xs={3} >
                                <FormGroup>
                                    <FormControlLabel
                                        control={<Switch checked={inputData?.enableTelegramChatId === undefined
                                            || inputData?.enableTelegramChatId === 'Y'
                                        }
                                            disabled={isDisabled}
                                            name="enableTelegramChatId"
                                            onChange={handleInputChange}
                                        />}
                                    // label={locale("register:permissions.shipClearanceProcess")} 
                                    />
                                </FormGroup>
                            </Grid>
                        </Grid> */}
            </Grid>
          </Grid>
        </Grid>
        <Grid item lg={3} md={6} xs={12}>
          <Grid
            container
            alignItems="center"
            spacing={3}
            className={classes.gridContainer}
          >
            <Grid item xs={12}>
              <C1InputField
                  label={locale("user.details.profile.usrFax")}
                  name="coreUsr.usrContact.contactFax"
                  disabled={isDisabled}
                  required={false}
                  onChange={handleInputChange}
                  value={getValue(inputData?.coreUsr?.usrContact?.contactFax)}
                  error={errors?.profile?.coreUsr?.usrContact?.contactFax ? true : false}
                  helperText={
                    errors?.coreUsr?.usrContact?.contactFax ? errors?.coreUsr?.usrContact?.contactFax : null
                  }
                  inputProps={{ maxLength: 35 }}
              />
              <C1InputField
                label={locale("user.details.profile.addrLn1")}
                name="coreUsr.usrAddr.addrLn1"
                disabled={isDisabled}
                required
                onChange={handleInputChange}
                value={getValue(inputData?.coreUsr?.usrAddr?.addrLn1)}
                error={errors?.profile?.addrLn1 ? true : false}
                helperText={
                  errors?.profile?.addrLn1 ? errors.profile.addrLn1 : null
                }
                inputProps={{ maxLength: 35 }}
              />
              <C1InputField
                label={locale("user.details.profile.addrLn2")}
                name="coreUsr.usrAddr.addrLn2"
                disabled={isDisabled}
                onChange={handleInputChange}
                value={getValue(inputData?.coreUsr?.usrAddr?.addrLn2)}
                error={errors?.profile?.addrLn2 ? true : false}
                helperText={
                  errors?.profile?.addrLn2 ? errors.profile.addrLn2 : null
                }
                inputProps={{ maxLength: 35 }}
              />
              <C1InputField
                label={locale("user.details.profile.addrLn3")}
                name="coreUsr.usrAddr.addrLn3"
                disabled={isDisabled}
                onChange={handleInputChange}
                value={getValue(inputData?.coreUsr?.usrAddr?.addrLn3)}
                error={errors?.profile?.addrLn3 ? true : false}
                helperText={
                  errors?.profile?.addrLn3 ? errors.profile.addrLn3 : null
                }
                inputProps={{ maxLength: 35 }}
              />
            </Grid>
          </Grid>
        </Grid>
        <Grid item lg={3} md={6} xs={12}>
          <Grid
            container
            alignItems="center"
            spacing={3}
            className={classes.gridContainer}
          >
            <Grid item xs={12}>
              <C1InputField
                label={locale("user.details.profile.addrPcode")}
                name="coreUsr.usrAddr.addrPcode"
                disabled={isDisabled}
                required
                onChange={handleInputChange}
                value={getValue(inputData?.coreUsr?.usrAddr?.addrPcode)}
                error={errors?.profile?.addrPcode ? true : false}
                helperText={
                  errors?.profile?.addrPcode ? errors.profile.addrPcode : null
                }
                inputProps={{ maxLength: 10 }}
              />

              <C1InputField
                label={locale("user.details.profile.addrProv")}
                name="coreUsr.usrAddr.addrProv"
                disabled={isDisabled}
                required
                onChange={handleInputChange}
                value={getValue(inputData?.coreUsr?.usrAddr?.addrProv)}
                error={errors?.profile?.addrProv ? true : false}
                helperText={
                  errors?.profile?.addrProv ? errors.profile.addrProv : null
                }
                inputProps={{ maxLength: 15 }}
              />
              <C1InputField
                  label={locale("user.details.profile.addrCity")}
                  name="coreUsr.usrAddr.addrCity"
                  disabled={isDisabled}
                  required
                  onChange={handleInputChange}
                  value={getValue(inputData?.coreUsr?.usrAddr?.addrCity)}
                  error={errors?.profile?.addrCity ? true : false}
                  helperText={
                    errors?.profile?.addrCity ? errors.profile.addrCity : null
                  }
                  inputProps={{ maxLength: 15 }}
              />
              <C1SelectAutoCompleteField
                label={locale("user.details.profile.ctyCode")}
                name="coreUsr.usrAddr.addrCtry.ctyCode"
                required
                disabled={isDisabled}
                onChange={handleAutoComplete}
                value={getValue(inputData?.coreUsr?.usrAddr?.addrCtry?.ctyCode)}
                isServer={true}
                isShowCode={true}
                options={{
                  url: MST_CTRY_URL,
                  key: "country",
                  id: "ctyCode",
                  desc: "ctyDescription",
                  isCache: true,
                }}
                error={errors?.profile?.ctyCode ? true : false}
                helperText={
                  errors?.profile?.ctyCode ? errors.profile.ctyCode : null
                }
              />
            </Grid>
          </Grid>
        </Grid>
      </C1TabContainer>
    </React.Fragment>
  );
};
export default ManageUserDetails;
