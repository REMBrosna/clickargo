import { Chip, Grid } from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import Switch from "@material-ui/core/Switch";
import { format } from "date-fns";
import moment from "moment";
import React from "react";

import C1DateField from "app/c1component/C1DateField";
import C1DateTimeField from "app/c1component/C1DateTimeField";
import C1InputField from "app/c1component/C1InputField";
import C1TimeField from "app/c1component/C1TimeField";
import { Roles, VoyageTypes } from "app/c1utils/const";
import CryptoJS from "crypto-js"

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

export function getVoyageType(status) {
    switch (status) {
        case VoyageTypes.IN.code:
        case VoyageTypes.IN.desc:
            return VoyageTypes.IN.code;
        case VoyageTypes.OUT.code:
        case VoyageTypes.OUT.desc:
            return VoyageTypes.OUT.code;
        default: return "";
    }
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

/**This method is to specifically bypass the isDisabled value passed in the disabled props, if the viewType is amend.
 * As not all fields will be editable if the application is returned and subject for amendment.
 */
export function isFieldDisabled(viewType, isDisabled) {
    return viewType === 'amend' ? true : isDisabled;
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

export function userRolesDescChip(userRoles) {
    var entries = [];
    if (userRoles !== undefined) {
        for (var i = 0; i < userRoles.length; i++) {
            entries.push(
                <span key={i} style={{ padding: '1px' }}>
                    <Chip label={userRoles[i].roleDesc} variant="outlined" size="small" />
                </span>

            );
        }
    }

    return (<div>{entries}</div>);
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
    if (base64String !== undefined && base64String !== null) {
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
    const fileExt = fileName.split(".").pop();

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
    return str === undefined || str === null || str?.trim()?.length == 0;
}

export const generateId = () => {
    return Math.floor(Math.random() * Date.now());
}

export const generateID = (prefix) => {
    var date = new Date();
    var y = date.toLocaleDateString("default", { year: "numeric" });
    var m = date.toLocaleDateString("default", { month: "2-digit" });
    var d = date.toLocaleDateString("default", { day: "2-digit" });
    return prefix + y + m + d + Math.floor(Math.random() * 90000);
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
    if (!allowBack) {
        return controls.map(item => {
            if (item?.ctrlAction !== "" && item?.ctrlAction.toLowerCase() === "exit") {
                item.ctrlAction = "";
                return item;
            } else {
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
const actions = ["customs", "fal", "dsa", 'maritime', 'outbound', "dsd", "frp", "qfd", "poc", "save", "submit", "acknowledge", "approve", "ftApprove", "verify", "return", "reject", "duplicate", "activate", "deactivate",
    "print", "reVerify", "preview", "appPreview", "uploadTemplate", "exit", "refresh", "validate"]

// To be removed - please use formActionUtils.js
export function getActionButton(initialButtons, controls, eventHandler) {
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
                Object.assign(initialButtons, {
                    [act]: {
                        show: true,
                        eventHandler: () => eventHandler(ctrAction)
                    },
                });

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

const createTimeFromString = (strTime) => {
    if (strTime) {
        var tParts = strTime.split(":");
        return new Date(1, 1, 1, tParts[0], tParts[1]);
    }
    return '';

}

const createDateTimeFromString = (strDateTime) => {
    if (strDateTime) {
        var dParts = strDateTime.split("/");
        var yearTimeSplit = dParts[2].split(" ");
        var tParts = yearTimeSplit[1].split(":");
        return new Date(yearTimeSplit[0], dParts[1] - 1, dParts[0], tParts[0], tParts[1]);
    }
    return '';
}

export const customNumFieldDisplay = (filterList, onChange, index, column) => {
    return <C1InputField type="number" label={column.label}
        name={column.name} onChange={(e) => {
            filterList[index] = [];
            filterList[index].push(e.target.value);
            onChange(filterList[index], index, column);
        }}
        value={getValue(filterList[index][0])}
    />
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

export const customFilterDateTimeDisplay = (filterList, onChange, index, column) => {
    return <C1DateTimeField label={column.label}
        name={column.name} autoOk={false} ampm={false}
        format="dd/MM/yyyy HH:mm"
        variant="standard"
        onChange={(name, date) => {
            filterList[index] = [];
            filterList[index].push(format(date, "dd/MM/yyy HH:mm"));//displayd as DD/MM/YYYY
            onChange(filterList[index], index, column);
        }}
        value={createDateTimeFromString(filterList[index][0]) || null} />
}

export const hasWhiteSpace = (s) => {
    return (/\s/).test(s);
}

export const isDecimal = (number) => {
    return number % 1 !== 0;
}

export const isSystemAdmin = ([authorities]) => {
    const systemAdmin = [Roles.SYS_SUPER_ADMIN.code]
    let ret = false;
    authorities.forEach((item, indx) => {
        if (systemAdmin.includes(item.authority))
            ret = true;
    });

    return ret;
}

export const isCustService = ([authorities]) => {
    const arrCsRoles = [Roles.SP_OP_ADMIN.code]
    let ret = false;
    authorities.forEach((item, indx) => {
        if (arrCsRoles.includes(item.authority))
            ret = true;
    });

    return ret;
}
export const isAccountAdmin = ([authorities]) => {
    const arrAdminRoles = [Roles.ADMIN.code]
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

export const isFinanceApprover = ([authorities]) => {
    const checkRoles = [Roles.FINANCE_APPROVER.code];
    let ret = false;
    authorities.forEach((item, index) => {
        if (checkRoles.includes(item.authority))
            ret = true;
    });
    return ret;
}

export const isSpL1 = ([authorities]) => {
    // SP_L1: { code: "SP_L1", desc: "LEVEL 1 SUPPORT" },
    const checkRoles = [Roles.SP_L1.code];
    let ret = false;
    authorities.forEach((item, index) => {
        if (checkRoles.includes(item.authority))
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

function getValueHelper(obj, arrSelector) {
    if (arrSelector.length > 1) {
        let field = arrSelector.shift();
        let subObj = {};

        try {
            subObj = getValueHelper(obj[field], arrSelector);
        } catch {
            subObj = getValueHelper(obj, arrSelector);
        }

        return subObj;
    } else {
        return obj[arrSelector];
    }
}

//For deep update of states
export function getDeepValue(obj, selector) {
    if (selector.indexOf(".") !== -1) {
        let sel = selector.split(".");
        let newState = getValueHelper(obj, sel);
        // if (autoAssign) return Object.assign(state, newState);

        return newState;
    }

    return obj;
}

export function currencyFormat(num) {
    return num.toFixed(2).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')
}

export function idrCurrency(num) {
    return new Intl.NumberFormat("id-ID", {
        maximumFractionDigits: 0,
        style: "currency",
        currency: "IDR"
    }).format(num);
}

export function formatCurrency(number, currencyCode) {
    if (currencyCode === 'IDR') {
        if (isDecimal(number)) {
            return number.toLocaleString("id-ID", { maximumFractionDigits: 2, style: "currency", currency: currencyCode });
        } else {
            return number.toLocaleString("id-ID", { maximumFractionDigits: 0, style: "currency", currency: currencyCode });
        }
    } else if (currencyCode === 'USD') {
        return number.toLocaleString("en-US", { maximumFractionDigits: 2, style: "currency", currency: currencyCode });
    }
}

export function encryptText(text, accnId, userId) {

    let key = accnId + userId;
    key = key.padEnd(32, "0");
    //console.log("key",text, key);

    text = CryptoJS.enc.Utf8.parse(text);
    key = CryptoJS.enc.Utf8.parse(key);

    var encrypted = CryptoJS.AES.encrypt(text, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7,
    });

    let encryptedStr = encrypted.ciphertext.toString(CryptoJS.enc.Hex);

    //console.log("encrypted", text, accnId, userId, encryptedStr);

    return encryptedStr;
}

export function toTitleCase (str) {
    const titleCase = (str || '')
        .replaceAll('_', ' ')
        .toLowerCase()
        .split(' ')
        .map(word => {
            return word.charAt(0).toUpperCase() + word.slice(1);
        })
        .join(' ');

    return titleCase;
}