import moment from "moment";
import React from "react";

import C1DateField from "app/c1component/C1DateField";
import C1TimeField from "app/c1component/C1TimeField";
import { VoyageTypes, Roles } from "app/c1utils/const";
import {withStyles} from "@material-ui/core/styles";
import Switch from "@material-ui/core/Switch";
import useAuth from "../hooks/useAuth";

//TODO to add more logic e.g. activate, deactivate, etc.
export function isEditable(viewType, isLoading) {
  let isDisabled = true;
  if (viewType === 'new')
    isDisabled = false;
  else if ((viewType === 'edit' || viewType === 'amend') && !isLoading)
    isDisabled = false;
  else if (viewType === 'view')
    isDisabled = true;

  if (isLoading) {
    isDisabled = true;
  }

  return isDisabled;
}


export const SwitchStyled = withStyles({
  switchBase: {
    color: 'white',
    '&$checked': {
      color: '#1976d2',
    },
    '&$checked + $track': {
      backgroundColor: '#1976d2',
    },
  },
  checked: {},
  track: {},
})(Switch);

export function isShipSide(accountType) {
  let isShipSide = false;
  if (accountType === 'ACC_TYPE_SHIP_LINE' || accountType === 'ACC_TYPE_SHIP_AGENT') {
    isShipSide = true;
  }
  return isShipSide;
}


export function userRolesDesc(userRoles) {
  let desc = "";
  let count = 1;
  if (userRoles !== undefined) {
    userRoles.forEach((doc) => {
      desc += doc.roleDesc;
      if (count < userRoles.length) {
        desc += ",  ";
      }
      count++;
    });
  }
  return desc;
}

export function formatDate(date, isTime, onlyTime) {

  let dateFormat = `DD/MM/YYYY${isTime ? ' HH:mm:ss' : ''}`;

  if (onlyTime) {
    dateFormat = 'HH:mm';
  }

  if (!date) {
    return '';
  } else if (date instanceof Date) {
    // date is Date class
    return moment(date).format(dateFormat);

  } else if (date.length === 13) {
    // 1543593600000
    return moment(new Date(date)).format(dateFormat);

  } else if (date.length === 10 && date.match(/^\d{4}-\d{2}-\d{2}$/)) {
    // 2020-09-11
    return moment(date, 'YYYY-MM-DD').format(dateFormat);

  } else if (date.length === 10 && date.match(/^\d{2}-\d{2}-\d{4}$/)) {
    // 09-11-2020
    return moment(date, 'DD-MM-YYYY').format(dateFormat);

  } else if (date instanceof String && date.indexOf("T") > 0) {
    // "2020-02-20T12:30:45",
    return moment(date).format(dateFormat);

  }
  return moment(date).format(dateFormat);
}


/**
 * Convert Uint8Array To String, use to upload file;
 * @param {*} fileData
 */
export const Uint8ArrayToString = (fileData) => {
  var dataString = "";
  for (var i = 0; i < fileData.length; i++) {
    dataString += String.fromCharCode(fileData[i]);
  }

  return dataString
}


/**
 *
 * @param {*} fileName
 * @param {*} base64String
 */
export const downloadFile = (fileName, base64String) => {
  //console.log(`fileName = ${fileName}`);
  const downloadLink = document.createElement("a");
  if (base64String !== undefined && base64String !== null){
    // Browsers that support HTML5 download attribute
    if (downloadLink.download !== undefined && downloadLink.download !== null) {
      downloadLink.href = `data:application/octet-stream;base64,${base64String}`;
      downloadLink.download = `${fileName}`;
      downloadLink.click();
    }
  }

}

const b64toBlob = (base64str, contentType = '') => {
  // decode base64 string, remove space for IE compatibility
  var binary = atob(base64str.replace(/\s/g, ''));
  var len = binary.length;
  var buffer = new ArrayBuffer(len);
  var view = new Uint8Array(buffer);
  for (var i = 0; i < len; i++) {
    view[i] = binary.charCodeAt(i);
  }
  return new Blob([view], { type: contentType });
}

export const previewPDF = (fileName, base64String) => {
  // get file extension
  const fileExt = fileName?.split(".").pop();

  let blob;
  if (["jpg", "jpeg", "png"].includes(fileExt)) {
    blob = b64toBlob(base64String, "image/png");
  } else if (fileExt === "pdf") {
    blob = b64toBlob(base64String, "application/pdf");
  } else {
    return downloadFile(fileName, base64String);
  }

  window.open(URL.createObjectURL(blob), "_blank");
}

export const isEmpty = (obj) => {
  for (var prop in obj) {
    if (obj.hasOwnProperty(prop)) {
      return false;
    }
  }

  return JSON.stringify(obj) === JSON.stringify({});
}

export const isStringEmpty = (str) => {
  return str === null || str === '' || str === ' ';
}

export const generateId = () => {
  return Math.floor(Math.random() * Date.now());
}

export const isArrayNotEmpty = (value) => {
  return value && Array.isArray(value) && value.length > 0;
}


export function getValue(str) {
  if (str === undefined || str === null)
    return "";
  else
    return str;

}

//To make the tabs scrollable if it's long
export function tabScroll(index) {
  return {
    id: `scrollable-auto-tab-${index}`,
    'aria-controls': `scrollable-auto-tabpanel-${index}`,
  };
}

export function getAllowBackButton(controls, allowBack) {
  if (!allowBack){
    return controls.map(item => {
      if(item?.ctrlAction !== "" && item?.ctrlAction.toLowerCase() === "exit"){
        item.ctrlAction = "";
        return item;
      } else{
        return item;
      }
    });
  } else {
    return controls;
  }
}

export function listActionsByExemption(controls,exemption,user) {
  let map = new Set(user.authorities.map((el) => el.authority));
  if (exemption){
    if((map.has(Roles.APPROVER_OFFICER.code) && map.has(Roles.VERIFIER_OFFICER.code))|| map.has(Roles.VERIFIER_OFFICER.code)){
      return controls.map(item => {
        if(["verify"].includes(item?.ctrlAction?.toLowerCase())){
          item.ctrlAction = "";
          return item;
        } else{
          return item;
        }
      });
    } else {
      return controls;
    }
  } else if((map.has(Roles.APPROVER_OFFICER.code) && map.has(Roles.VERIFIER_OFFICER.code))) {
    return controls.map(item => {
      if(["acknowledge"].includes(item?.ctrlAction?.toLowerCase())){
        item.ctrlAction = "";
        return item;
      } else{
        return item;
      }
    });
  } else if(map.has(Roles.APPROVER_OFFICER.code)){
    return controls.map(item => {
      if(["acknowledge","return","reject"].includes(item?.ctrlAction?.toLowerCase())){
        item.ctrlAction = "";
        return item;
      } else{
        return item;
      }
    });
  } else {
    return controls;
  }
}

export function getExtension(filename) {
  let pos = filename.lastIndexOf(".");

  if (pos < 1)  // if file name is empty or ...
    return ""; //  `.` not found (-1) or comes first (0)

  return filename.slice(pos + 1).toLowerCase(); // extract extension ignoring `.`
}
const actions = ["customs", "fal", "dsa",'maritime','outbound', "dsd", "frp", "qfd", "poc", "save", "submit", "acknowledge", "approve", "ftApprove", "verify", "return", "reject", "duplicate", "activate", "deactivate",
  "print", "reVerify", "preview", "appPreview", "uploadTemplate", "exit", "refresh", "validate"]

export function getActionButton(initialButtons, controls, eventHandler, fal) {
  controls.map(ctr => {
    const ctrAction = ctr?.ctrlAction;
    const filter = actions.filter(a => a.toLowerCase() === ctrAction?.toLowerCase())

    if (filter.length > 0) {
      let act = filter[0];
      if (act.toLowerCase() === "submit") {
        act = "submitOnClick";
      }
      else if (act.toLowerCase() === "exit") {
        act = "back";
      }
      if (ctr.ctrlButtonRef && ctr.ctrlButtonRef !== 'test-button-ref') {
        Object.assign(initialButtons, {
          [act]: {
            show: true,
            what: ctr.ctrlButtonRef,
            eventHandler: () => eventHandler(ctrAction)
          },
        });
      } else {

        if (act.toLowerCase() === 'fal') {
          Object.assign(initialButtons, {
            [act]: {
              show: true,
              eventHandlerFal1: () => fal("general"),
              eventHandlerFal2: () => fal("cargo"),
              eventHandlerFal3: () => fal("shipStore"),
              eventHandlerFal4: () => fal("crewEffect"),
              eventHandlerFal5: () => fal("crewList"),
              eventHandlerFal6: () => fal("passengerList"),
              eventHandlerFal7: () => fal("dangerousGoods"),
              eventHandlerMaritime: () => fal("maritimeOfHealth"),
              eventHandlerOutbound: () => fal("healthOutbound"),
            },
          });
        } else {
          Object.assign(initialButtons, {
            [act]: {
              show: true,
              eventHandler: () => eventHandler(ctrAction)
            },
          });
        }
      }
    }
  })

  return initialButtons;
}

/**Created this function to convert a string date into a date object for C1DateField. */
const createDateFromString = (strDate) => {
  if (strDate) {
    var dParts = strDate.split("/");
    return new Date(dParts[2], dParts[1] - 1, dParts[0]);
  }
  return '';
}

export const customFilterDateDisplay = (filterList, onChange, index, column) => {
  return <C1DateField
    label={column.label}
    name={column.name}
    onChange={(name, date) => {
      filterList[index] = [];
      filterList[index].push(formatDate(date, false));//displayd as DD/MM/YYYY
      onChange(filterList[index], index, column);
    }}
    value={createDateFromString(filterList[index][0]) || null} />
}

const createTimeFromString = (strTime) => {
  if (strTime) {
    var tParts = strTime.split(":");
    return new Date(1, 1, 1, tParts[0], tParts[1]);
  }
  return '';

}

export const customFilterTimeDisplay = (filterList, onChange, index, column) => {
  return <C1TimeField
    ampm={false}
    label={column.label}
    name={column.name}
    onChange={(name, date) => {
      filterList[index] = [];
      filterList[index].push(formatDate(date, false, true));//displayd as HH:mm
      onChange(filterList[index], index, column);
    }}
    value={createTimeFromString(filterList[index][0]) || null} />
}

export const hasWhiteSpace = (s) => {
  return (/\s/).test(s);
}

export const isDecimal = (number) => {
  return number % 1 !== 0;
}

export const isSystemAdmin = ([authorities]) => {
  const systemAdmin = [Roles.SYSTEM_ADMIN.code]
  let ret = false;
  authorities.forEach((item, indx) => {
    if (systemAdmin.includes(item.authority))
      ret = true;
  });

  return ret;
}

export const isAccountAdmin = ([authorities]) => {
  const arrAdminRoles = [Roles.SHIP_LINE_ADMIN.code, Roles.SHIP_AGENT_ADMIN.code, Roles.ADMIN_OFFICER.code]
  let ret = false;
  authorities.forEach((item, indx) => {
    if (arrAdminRoles.includes(item.authority))
      ret = true;
  });

  return ret;
}

export const isGDAccnAdmin = ([authorities]) => {
  const arrAdminRoles = [Roles.GD_OFFICER_ADMIN.code]
  let ret = false;
  authorities.forEach((item, indx) => {
    if (arrAdminRoles.includes(item.authority))
      ret = true;
  });

  return ret;
}

export const encodeString = (string) => {
  return Buffer.from(string).toString("base64");
}

export const decodeString = (string) => {
  return Buffer.from(string, 'base64').toString("utf-8");
  //return Buffer.of(string).toString('utf-8');
}
