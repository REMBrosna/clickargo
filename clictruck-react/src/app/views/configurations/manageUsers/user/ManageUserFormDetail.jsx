import { Divider, Grid, Paper, Tab, Tabs } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import { debounce } from "lodash";
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1Button from "app/c1component/C1Button";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import { RecordStatus, Roles } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import {
  getValue,
  hasWhiteSpace,
  isAccountAdmin,
  isCustService,
  isEmpty,
  isSystemAdmin,
} from "app/c1utils/utility";
import { encodeString, isArrayNotEmpty } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import axios from "axios.js";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

import ManageUserDetails from "./ManageUserDetails";
import ManageUsersRoles from "./ManageUserRoles";
import _ from 'lodash';

/** Component for user profile */
const ManageUserFormDetail = () => {
  //useParams hook to acces the dynamic pieaces of the URL
  let { viewType, id } = useParams();

  const { state } = useLocation();
  if (state?.usrUid) {
    id = state.usrUid;
  }

  let history = useHistory();

  const { t } = useTranslation(["admin", "common", "buttons"]);
  const {
    isLoading,
    isFormSubmission,
    res,
    validation,
    error,
    urlId,
    sendRequest,
  } = useHttp();
  const { user } = useAuth();
  const isAccnAdmin = isAccountAdmin([user.authorities]);
  const isSysAdmin = isSystemAdmin([user.authorities]);
  const isCs = isCustService([user.authorities]);

  let isOfficial = user?.authorities?.some(
    (item) =>
      item?.authority === Roles.SP_L1.code ||
      item?.authority === Roles.SP_OP_ADMIN.code
  );

  // const manageUsrUrl = `/api/v1/clickargo/admin/user`;
  const manageUsrUrl = `/api/v1/clickargo/clictruck/manageusr`;

  const tabList = [
    // { text: t("user.details.tabs.profile"), icon: <PersonOutline /> },
    { text: "User Details" }, // icon: <PersonOutline /> },
    { text: t("user.details.tabs.roles") }, // icon: <Group /> },
    // { text: t("user.details.tabs.notifPref"), icon: <Notifications /> },
    // { text: t("common:properties.title"), icon: < Assignment /> },
    { text: t("common:audits.title") }, //, icon: <Schedule /> }
  ];

  //useState with initial value of 0 for tabIndex
  const [tabIndex, setTabIndex] = useState(0);

  const defaultSnackbarValue = {
    success: false,
    successMsg: "",
    error: false,
    errorMsg: "",
    redirectPath: "",
  };
  const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

  const [notHoldRoleList, setNotHoldRoleList] = useState([]);
  const [holdRoleList, setHoldRoleList] = useState([]);

  const [loading, setLoading] = useState(false);
  const [inputData, setInputData] = useState({});
  const [errors, setErrors] = useState({});

  const [notifPrefData, setNotifPrefData] = useState({});
  const [tempEmail, setTempEmail] = useState();
  const [isUserLoggedIn, setIsUserLoggedIn] = useState(false);
  const [confirm, setConfirm] = useState({ id: "", open: false, action: null });

  //flag for popup dialog for reset password
  const [openPopup, setOpenPopup] = useState(false);
  //let isSystemAdmin = location && location.search && location.search.includes('all');
  let isFromProfile = id === "profile";

  //api request for the details here
  useEffect(() => {
    if (viewType !== "new" && viewType !== "newAll") {
      setLoading(true);
      sendRequest(
        `${manageUsrUrl}/${
          isFromProfile ? encodeString(user.id) : encodeString(id)
        }`,
        "getUsr",
        "get",
        {}
      );
    } else {
      //for new
      sendRequest(`${manageUsrUrl}/new`, "newUsr", "get");
    }
  }, [id, viewType]);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      setLoading(isLoading);
      setInputData({ ...inputData, ...res.data });
      // setNotifPrefData({ ...notifPrefData, ...res.data.notifPref });
      // setNotHoldRoleList(n => ([...n, ...res.data.notHoldRoleList || []]));
      //Nina: To remove duplicate keys warning in the browser
      setNotHoldRoleList((n) => {
        if (n?.length == 0) return [...(res?.data?.notHoldRoleList || [])];
        if (
          res?.data?.notHoldRoleList === undefined ||
          res?.data?.notHoldRoleList == null
        )
          return [];
        return [...n];
      });

      //for all users, if location includes any e.g. ?profile, then redirect set to null
      // const toPath = isSysAdmin ? "/manageusers/users/all/list" : isFromProfile ? "/" : "/manageusers/user/list";
      const toPath = isFromProfile ? "/" : "/account/users";

      let accnId = res?.data?.coreUsr?.TCoreAccn?.accnId;
      let isLoggedIn = res?.data?.loggedIn;
      switch (urlId) {
        case "newUsr":
          break;
        case "getUsr":
          setIsUserLoggedIn(isLoggedIn);
          if (isFromProfile) {
            setHoldRoleList([]);
            setNotHoldRoleList([]);
            setNotHoldRoleList((h) => [
              ...h,
              ...(res.data.notHoldRoleList || []),
            ]);
          }
          setHoldRoleList((h) => [...h, ...(res.data.holdRoleList || [])]);
          setInputData({ ...inputData, ...res.data });
          if (viewType === "resetPassword") {
            handleStatusChange("resetPassword");
          }
          break;
        case "saveUsr":
        case "createUsr":
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:genericMsgs.success"),
            redirectPath: toPath,
          });
          break;
        case "getForActive":
          var portalUser = {
            coreUsr: inputData,
            holdRoleList: holdRoleList,
            notifPref: notifPrefData,
          };
          sendRequest(
            `${manageUsrUrl}/activate/${
              isFromProfile ? encodeString(user.id) : encodeString(id)
            }`,
            "activate",
            "PUT",
            portalUser
          );
          break;
        case "activate":
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("admin:user.msg.activatedSuccess"),
            redirectPath: toPath,
          });
          break;
        case "getForDeActive":
          let isUserLoggedIn = res?.data?.loggedIn;
          if (isUserLoggedIn) {
            setConfirm({
              ...confirm,
              id: res?.data?.coreUsr?.usrUid,
              open: true,
              action: "deactivate",
            });
          } else {
            setLoading(true);
            var portalUser = {
              coreUsr: inputData,
              holdRoleList: holdRoleList,
              notifPref: notifPrefData,
            };
            sendRequest(
              `${manageUsrUrl}/deactivate/${
                isFromProfile ? encodeString(user.id) : encodeString(id)
              }`,
              "deactivate",
              "put",
              portalUser
            );
          }
          break;
        case "getForRoleChange":
          let currHoldRoleList = res?.data?.holdRoleList;
          if (
            isLoggedIn &&
            currHoldRoleList &&
            currHoldRoleList.length !== holdRoleList.length
          ) {
            setConfirm({
              ...confirm,
              id: res?.data?.coreUsr?.usrUid,
              open: true,
              action: "saveUsr",
            });
          } else {
            updateUser();
          }
          break;
        case "deactivate":
          //CPEDI-166 Removed the warning message in the Roles tab and replaced with popup confirmation
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("admin:user.msg.deactivatedSuccess"),
            redirectPath: toPath,
          });
          // }
          break;
        case "fetchRoles":
          setNotHoldRoleList(res.data);
          break;
        case "resetPwd":
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("user.msg.resetPasswordSuccess"),
            redirectPath: toPath,
          });
          // setHoldRoleList(h => ([...h, ...res.data.holdRoleList || []]));
          setOpenPopup(false);
          break;
        default:
          break;
      }
    }

    if (error) {
      //goes back to the screen
      setLoading(false);
    }

    if (validation) {
      //not applicable at this point since there's no validation yet done in the backend. May be in the future
      // setErrors({ ...errors, accnrCoIntial: validation.accnrCoIntial });
    }
  }, [urlId, isLoading, isFormSubmission, res, validation, error]);

  const handleRoleChange = (roleList) => {
    setHoldRoleList(roleList);
  };

  const handleTabChange = (e, value) => {
    setTabIndex(value);
  };

  const handleStatusChange = (action) => {
    if (action === "resetPassword") {
      setTempEmail(getValue(inputData?.usrContact?.contactEmail));
      setOpenPopup(true);
    } else {
      if (action === "deactivate") {
        sendRequest(
          `${manageUsrUrl}/${
            isFromProfile ? encodeString(user.id) : encodeString(id)
          }`,
          "getForDeActive",
          "GET",
          {}
        );
      } else {
        var portalUser = {
          coreUsr: inputData,
          holdRoleList: holdRoleList,
          notifPref: notifPrefData,
        };
        sendRequest(
          `${manageUsrUrl}/${action}/${
            isFromProfile ? encodeString(user.id) : encodeString(id)
          }`,
          action,
          "PUT",
          portalUser
        );
      }
    }
  };

  const handleConfirmYesAction = () => {
    setLoading(true);
    var portalUser = {
      coreUsr: inputData,
      holdRoleList: holdRoleList,
      notifPref: notifPrefData,
    };
    if (confirm?.action === "deactivate") {
      sendRequest(
        `/api/v1/clickargo/clictruck/manageusr/deactivate/${
          isFromProfile ? encodeString(user.id) : encodeString(id)
        }`,
        "deactivate",
        "PUT",
        portalUser
      );
    } else {
      updateUser();
    }

    setConfirm({ ...confirm, id: res?.data?.coreUsr?.usrUid, open: false });
  };

  const updateUser = () => {
    setLoading(true);
    var portalUser = {
      coreUsr: inputData.coreUsr,
      holdRoleList: _.uniqBy(holdRoleList, 'id.roleId'),
      notifPref: notifPrefData,
      usetMobileNo: inputData.usetMobileNo,
      usetTelegramChatId: inputData.usetTelegramChatId,
      usetSubDivision: inputData.usetSubDivision,
      usetStreetNoPobox: inputData.usetStreetNoPobox,
    };

    sendRequest(
      `${manageUsrUrl}/save/${
        isFromProfile ? encodeString(user.id) : encodeString(id)
      }`,
      "saveUsr",
      "put",
      portalUser
    );
  };

  /**to be triggered when confirmation dialog is OK'd */
  const handleResetPassword = () => {
    var portalUser = {
      coreUsr: inputData.coreUsr,
      holdRoleList: holdRoleList,
      alternateEmail: tempEmail,
      notifPref: notifPrefData,
    };
    sendRequest(
      `${manageUsrUrl}/resetPassword/${
        isFromProfile ? encodeString(user.id) : encodeString(id)
      }`,
      "resetPwd",
      "put",
      portalUser
    );
  };

  const handleSubmit = async (values) => {
    setLoading(true);
    var portalUser = {
      coreUsr: inputData.coreUsr,
      holdRoleList: holdRoleList,
      notifPref: notifPrefData,
      usetMobileNo: inputData.usetMobileNo,
      usetTelegramChatId: inputData.usetTelegramChatId,
      usetSubDivision: inputData.usetSubDivision,
      usetStreetNoPobox: inputData.usetStreetNoPobox,
    };

    let errors = handleValidate();

    if (isEmpty(errors)) {
      setErrors({});
      switch (viewType) {
        case "newAll":
        case "new":
          sendRequest(
            `${manageUsrUrl}/create`,
            "createUsr",
            "post",
            portalUser
          );
          break;
        case "edit":
          //CPEDI-166 Removed the warning message in the Roles tab and replaced with popup confirmation
          sendRequest(
            `${manageUsrUrl}/${
              isFromProfile ? encodeString(user.id) : encodeString(id)
            }`,
            "getForRoleChange",
            "get",
            {}
          );
          break;
        default:
          break;
      }
    } else {
      setLoading(false);
      setErrors(errors);
    }
  };

  /**Validation is handled here, dont't want to change the portalUser controller for now as it is in clibr. */
  const handleValidate = () => {
    const errors = { profile: {} };
    if (viewType === "new" || viewType === "edit" || viewType === "newAll") {
      if (!inputData?.coreUsr?.usrUid) {
        errors.profile.usrUid = t("common:validationMsgs.required");
      } else if (hasWhiteSpace(inputData?.coreUsr?.usrUid)) {
        errors.profile.usrUid = t("admin:user.field.hasWhitespace");
      } else if (inputData?.coreUsr?.usrUid?.length > 35) {
        errors.profile.usrUid = t("admin:user.field.overLength", { max: 35 });
      } else if (inputData?.coreUsr?.isUserIdExists) {
        errors.profile.usrUid = t("admin:user.field.alreadyExists");
      } else if (inputData?.coreUsr?.isEmailExists) {
        errors.profile.contactEmail = t("admin:user.field.emailNa");
      }

      if (isOfficial && !inputData?.coreUsr?.TCoreAccn.accnId)
        errors.profile.accnId = t("common:validationMsgs.required");
      if (!inputData?.coreUsr?.usrPassNid)
        errors.profile.usrPassNid = t("common:validationMsgs.required");
      if (!inputData?.coreUsr?.usrName)
        errors.profile.usrName = t("common:validationMsgs.required");
      if (!inputData?.coreUsr?.usrTitle)
        errors.profile.usrTitle = t("common:validationMsgs.required");

      if (!inputData?.coreUsr?.usrContact?.contactTel)
        errors.profile.contactTel = t("common:validationMsgs.required");
      else if (inputData?.coreUsr?.usrContact?.contactTel?.length < 8)
        errors.profile.contactTel = t("common:validationMsgs.minLength");
      else if (
        !inputData?.coreUsr?.usrContact?.contactTel.match(
          "^[+]{1}[(]{0,1}[0-9]{1,4}[)]{0,1}[\\d\\.0-9]*$"
        )
      )
        errors.profile.contactTel = t("common:validationMsgs.invalidTelFormat");

      // if (!inputData.usetMobileNo)
      //     errors.profile.usetMobileNo = t("common:validationMsgs.required");
      // else if (!inputData.usetMobileNo.match("\\+\\d{10,15}"))
      //     errors.profile.usetMobileNo = t("common:validationMsgs.invalidTelFormat");

      const emailRegExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      
      if (!inputData?.coreUsr?.usrContact?.contactEmail)
        errors.profile.contactEmail = t("common:validationMsgs.required");
      else if (!(emailRegExp.test(inputData?.coreUsr.usrContact.contactEmail))) {
           errors.profile.contactEmail = t("common:validationMsgs.invalidEmailFormat");
      }

      if (!inputData?.coreUsr?.usrAddr.addrLn1)
        errors.profile.addrLn1 = t("common:validationMsgs.required");
      if (!inputData?.coreUsr?.usrAddr.addrCity)
        errors.profile.addrCity = t("common:validationMsgs.required");
      if (!inputData?.coreUsr?.usrAddr.addrPcode)
        errors.profile.addrPcode = t("common:validationMsgs.required");
      if (!inputData?.coreUsr?.usrAddr?.addrProv)
        errors.profile.addrProv = t("common:validationMsgs.required");
      if (!inputData?.coreUsr?.usrAddr?.addrCtry?.ctyCode)
        errors.profile.ctyCode = t("common:validationMsgs.required");

      // if ((isSysAdmin || isAccnAdmin) && isAccnBorder && usrGroup?.id && !usrGroup?.id?.ugrpGroupid) {
      //     //isDisabled || (isSysAdmin && isGroupNotEmpty ? false : isAccnBorder && isAccnAdmin ? false : isAccnBorder ? false : !isGroupNotEmpty)
      //     errors.profile.ugrpGroupid = t("common:validationMsgs.required");
      // }

      //check for the roles
      // let oldRoles = inputData?.holdRoleList.map(el => el.id.roleId);
      // let newRoles = holdRoleList.map(el => el.id.roleId);
      if (!isArrayNotEmpty(holdRoleList)) {
        errors.holdRoleList = t("admin:user.msg.errorRoles");
      }

      //CPEDI-166 Removed the warning message in the Roles tab and replaced with popup confirmation
      // else if (isUserLoggedIn && (JSON.stringify(oldRoles) != JSON.stringify(newRoles))) {
      //     errors.holdRoleList = t("admin:user.msg.userLoggedInRoleErr", { userId: inputData?.coreUsr?.usrUid });
      // }

      //if there's error for roles
      if (errors.holdRoleList) setTabIndex(1);
      if (!isEmpty(errors.profile)) setTabIndex(0);

      //reset the error to proceed to submit
      if (isEmpty(errors.profile) && !errors.holdRoleList) {
        return {};
      }
    }

    return errors;
  };

  const checkUserId = useCallback(
    debounce((value) => {
      axios
        .get(
          `/api/v1/clickargo/clictruck/manageusr/check-id-availability?usrId=${value}`
        )
        .then(() =>
          setInputData((inputData) => ({
            ...inputData,
            coreUsr: { ...inputData["coreUsr"], isUserIdExists: false },
          }))
        )
        .catch(() =>
          setInputData((inputData) => ({
            ...inputData,
            coreUsr: { ...inputData["coreUsr"], isUserIdExists: true },
          }))
        );
    }, 2000),
    []
  );

  const checkContactEmail = useCallback(
    debounce((id, email) => {
      axios
        .get(
          `/api/v1/clickargo/clictruck/manageusr/check-email-availability?usrId=${id}&email=${email}`
        )
        .then(() =>
          setInputData((inputData) => ({
            ...inputData,
            coreUsr: { ...inputData["coreUsr"], isEmailExists: false },
          }))
        )
        .catch(() =>
          setInputData((inputData) => ({
            ...inputData,
            coreUsr: { ...inputData["coreUsr"], isEmailExists: true },
          }))
        );
    }, 500),
    []
  );

  const handleAutoCompleteInput = (e, name, value) => {
    setInputData({
      ...inputData,
      ...deepUpdateState(inputData, name, value?.value),
    });
  };

  const handleInputChange = async (e) => {
    const elName = e.target.name;
    if (
      elName === "enableContactNo" ||
      elName === "enableContactEmail" ||
      elName === "enableTelegramChatId"
    ) {
      setInputData({ ...inputData, [elName]: e.target.checked ? "Y" : "N" });
    } else {
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value),
      });
    }
    if (elName === "tempEmail") {
      setTempEmail(e.target.value);
    } else if (tabIndex === 2) {
      setNotifPrefData({
        ...notifPrefData,
        [elName]: e.target.checked ? "Y" : "N",
      });
    } else if (elName.includes("accnId")) {
      if (e.target.value !== "") {
        sendRequest(
          `${manageUsrUrl}/fetchRoles/` + e.target.value,
          "fetchRoles",
          "get"
        );
      } else {
        setNotHoldRoleList([]);
      }
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value),
      });
      // setIsAccnBorder(e.target.value.toLowerCase().includes("brd"));
      // } else if (elName.includes('ugrpGroupid')) {
      //     setUsrGroup({ ...usrGroup, ...deepUpdateState(usrGroup, elName, e.target.value) })
    }
    if (elName === "usrUid") {
      setInputData({
        ...inputData,
        coreUsr: { ...inputData["coreUsr"], [elName]: e.target.value },
      });
    } else if (elName === "TCoreAccn.accnId") {
      setInputData({
        ...inputData,
        coreUsr: {
          ...inputData["coreUsr"],
          TCoreAccn: { accnId: e.target.value },
        },
      });
    } else {
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value),
      });

      if (elName === "coreUsr.usrUid") {
        checkUserId(e.target.value);
      }
      if (elName === "coreUsr.usrContact.contactEmail") {
        checkContactEmail(inputData?.coreUsr?.usrUid, e.target.value);
      }
      if (elName === "coreUsr.usrContact.contactFax") {
        setInputData({
          ...inputData,
          ...deepUpdateState(inputData, elName, e.target.value),
        });
      }
      if (elName === "coreUsr.usrUid") {
        if (e.target.value ) {
          let val = e.target.value ;
          val = val.replace(/[^\w]/gi, '');
          val = val.toUpperCase();

          setInputData({
            ...inputData,
            ...deepUpdateState(inputData, elName, val),
          });
        }
        checkUserId(e.target.value);
      } else {
        setInputData({
          ...inputData,
          ...deepUpdateState(inputData, elName, e.target.value),
        });
      }
    }
  };

  const handleInputTelChange = async (e) => {
    const elName = e.target.name;
    if (elName === "coreUsr.usrContact.contactTel") {
      const rgx = /^\+?[0-9]*$/;
      if (e.target.value === "" || rgx.test(e.target.value)) {
        setInputData({
          ...inputData,
          ...deepUpdateState(inputData, elName, e.target.value),
        });
      }
    }
  };

  let elResetPwd =
    viewType !== "new" && viewType !== "newAll" ? (
      <C1LabeledIconButton
        tooltip={t("buttons:resetPw")}
        label={t("buttons:reset")}
        action={() => handleStatusChange("resetPassword")}
      >
        <RotateLeftIcon color="primary" />
      </C1LabeledIconButton>
    ) : null;

  let formButtons = (
    <C1FormButtons
      options={{
        back: {
          show: true,
          eventHandler: () =>
            isSysAdmin
              ? history.goBack()
              : isFromProfile
              ? history.push("/")
              : history.goBack(),
          // eventHandler: () => isFromProfile ? history.push("/") : history.push("/manageUsers/user/list")
        },
        save: {
          show: true,
          eventHandler: () => handleSubmit(),
        },
      }}
    >
      {elResetPwd}
    </C1FormButtons>
  );

  let bcLabel = t("common:pageHeaders.edit", {
    appType: "User Profile",
  });
  if (viewType) {
    switch (viewType) {
      case "view":
        bcLabel = t("common:pageHeaders.view", {
          appType: "User Profile",
        });
        formButtons = (
          <C1FormButtons
            options={{
              back: {
                show: true,
                eventHandler: () =>
                  isSysAdmin
                    ? history.goBack()
                    : isFromProfile
                    ? history.push("/")
                    : history.goBack(),
              },
              activate: {
                show:
                  inputData?.coreUsr?.usrStatus ===
                    RecordStatus.INACTIVE.code && !isFromProfile,
                eventHandler: () => handleStatusChange("activate"),
              },
            }}
          />
        );
        break;
      case "newAll":
      case "new":
        bcLabel = t("common:pageHeaders.new", {
          appType: "User Profile",
        });
        break;

      case "edit":
        bcLabel = t("common:pageHeaders.edit", {
          appType: "User Profile",
        });
        formButtons = (
          <C1FormButtons
            options={{
              back: {
                show: true,
                eventHandler: () =>
                  isSysAdmin
                    ? history.goBack()
                    : isFromProfile
                    ? history.push("/")
                    : history.goBack(),
              },
              activate: {
                show:
                  (inputData?.coreUsr?.usrStatus ===
                    RecordStatus.INACTIVE.code ||
                    inputData?.coreUsr?.usrStatus ===
                      RecordStatus.SUSPENDED.code) &&
                  !isFromProfile,
                eventHandler: () => handleStatusChange("activate"),
              },
              deactivate: {
                show:
                  inputData.coreUsr?.usrStatus === RecordStatus.ACTIVE.code &&
                  !isFromProfile,
                eventHandler: () => handleStatusChange("deactivate"),
              },
              save: {
                show: true,
                eventHandler: () => handleSubmit(),
              },
            }}
          >
            {elResetPwd}
          </C1FormButtons>
        );
        break;
      default:
        break;
    }
  }

  let bc = [];

  if (isFromProfile) {
    bc.push({ name: bcLabel });
  } else {
    bc.push({
      name: isSysAdmin ? t("user.list.titleAll") : t("user.list.title"),
      path: isSysAdmin
        ? "/manageUsers/users/all/list"
        : "/manageUsers/user/list",
    });
    bc.push({ name: bcLabel });
  }
  return loading ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      {confirm && confirm.open && (
        <ConfirmationDialog
          title={
            confirm.action === "deactivate"
              ? t("admin:user.deactivate.title")
              : t("admin:user.roleChange.title")
          }
          open={confirm.open}
          text={
            confirm.action === "deactivate"
              ? t("admin:user.deactivate.content", { userId: confirm.id })
              : t("admin:user.roleChange.content")
          }
          onYesClick={() => handleConfirmYesAction()}
          onConfirmDialogClose={() => setConfirm({ ...confirm, open: false })}
        />
      )}

      <C1FormDetailsPanel
        breadcrumbs={bc}
        titleStatus={
          viewType !== "new" && viewType !== "newAll"
            ? inputData?.coreUsr?.usrStatus
            : RecordStatus.NEW.code
        }
        title={bcLabel}
        formButtons={formButtons}
        initialValues={{ ...inputData }}
        values={{ ...inputData }}
        onSubmit={(values, actions) => handleSubmit(values, actions)}
        onValidate={handleValidate}
        snackBarOptions={snackBarOptions}
        isLoading={loading}
      >
        {(props) => {
          return (
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <Paper>
                  <Tabs
                    className="mt-4"
                    value={tabIndex}
                    onChange={handleTabChange}
                    indicatorColor="primary"
                    textColor="primary"
                  >
                    {tabList.map((item, ind) => {
                      return (
                        <Tab
                          className="capitalize"
                          value={ind}
                          label={item.text}
                          key={ind}
                          icon={item.icon}
                        />
                      );
                    })}
                  </Tabs>
                  <Divider className="mb-6" />

                  {tabIndex === 0 && (
                    <C1TabInfoContainer>
                      <ManageUserDetails
                        inputData={inputData}
                        handleInputChange={handleInputChange}
                        handleInputTelChange={handleInputTelChange}
                        handleAutoComplete={handleAutoCompleteInput}
                        viewType={viewType}
                        isSubmitting={loading}
                        errors={errors}
                        isProfile={isFromProfile}
                        locale={t}
                        isSysAdmin={isSysAdmin}
                        isAccnAdmin={isAccnAdmin}
                        accnId={user.coreAccn.accnId}
                        isCs={isCs}
                      />
                    </C1TabInfoContainer>
                  )}

                  {tabIndex === 1 && (
                    <C1TabInfoContainer>
                      <ManageUsersRoles
                        notHoldRoleList={notHoldRoleList}
                        holdRoleList={_.uniqBy(holdRoleList, 'id.roleId')}
                        handleRoleChange={handleRoleChange}
                        viewType={viewType}
                        isSubmitting={loading}
                        accountID={inputData?.coreUsr?.TCoreAccn?.accnId}
                        locale={t}
                        errors={props.errors}
                      />
                    </C1TabInfoContainer>
                  )}
                  {tabIndex === 2 && (
                    <C1TabInfoContainer>
                      <C1AuditTab filterId={inputData?.coreUsr?.usrUid ? inputData?.coreUsr?.usrUid : 'empty'} />
                    </C1TabInfoContainer>
                  )}
                </Paper>
              </Grid>
            </Grid>
          );
        }}
      </C1FormDetailsPanel>
      <C1PopUp
        title={t("admin:user.resetPwd.title")}
        openPopUp={openPopup}
        setOpenPopUp={setOpenPopup}
      >
        <Grid container spacing={1} alignItems="center">
          <Grid container item spacing={3}>
            <Grid item xs={6}>
              <C1InputField
                label={t("admin:user.details.query.usrId")}
                name="usrUid"
                required
                disabled
                onChange={(e) => {
                  handleInputChange(e);
                }}
                value={inputData?.coreUsr?.usrUid}
              />
            </Grid>
            <Grid item xs={6}>
              <C1InputField
                label={t("admin:user.details.query.email")}
                name="tempEmail"
                required
                disabled={isLoading}
                onChange={(e) => {
                  handleInputChange(e);
                }}
                value={
                  tempEmail
                    ? tempEmail
                    : inputData?.coreUsr?.usrContact?.contactEmail
                }
              />
            </Grid>
          </Grid>
          <Grid
            container
            item
            alignItems="flex-end"
            spacing={2}
            direction="row"
            justify="flex-end"
          >
            <Grid item xs={6}></Grid>
            <Grid
              container
              item
              xs={6}
              spacing={2}
              direction="row"
              justify="flex-end"
            >
              <Grid item xs={3}>
                <Button
                  variant="contained"
                  color="secondary"
                  size="large"
                  fullWidth
                  onClick={(e) => setOpenPopup(false)}
                >
                  {t("admin:user.details.query.btnClose")}
                </Button>
              </Grid>
              <Grid item xs={3}>
                <C1Button
                  variant="contained"
                  color="primary"
                  size="large"
                  withLoading={isLoading}
                  onClick={(e) => handleResetPassword()}
                  text={t("admin:user.details.query.btnReset")}
                />
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </C1PopUp>
    </React.Fragment>
  );
};

export default withErrorHandler(ManageUserFormDetail);
