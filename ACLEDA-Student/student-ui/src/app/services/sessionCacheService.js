/* refer to https://medium.com/@atif.ali.ati/user-session-in-react-js-aa749bc4faf6 */

const SessionCache = (function () {
  // Master
  let countryList = null;
  let currencyList = null;
  let portTypeList = null;
  let uomList = null;
  let hsTypeList = null; // Harmonized System
  let accountList = null;
  let accountTypeList = null;
  let userList = null;
  let appCodeList = null;
  let groupList = null;
  let bankCodeList = null;
  let bankBranchList = null;
  let contactTypeList = null;
  let portList = null;

  // CO authorization
  let uriTypeList = null;
  let permissionTypeList = null;
  let uriList = null;

  // Notification
  let notificationChannelList = null;
  let notificationContentList = null;
  let notificationDeviceList = null;
  let notificationTemplateList = null;

  //Announcement
  let announcementTypeList = null;

  //
  let reportCategoryList = null;

  let getCountryList = function () {
    return countryList;
  };

  let setCountryList = function (countryListParam) {
    countryList = countryListParam;
  };

  ///////
  let getCurrencyList = function () {
    return currencyList;
  };

  let setCurrencyList = function (currencyListParam) {
    currencyList = currencyListParam;
  };

  //////////
  let getPortTypeList = function () {
    return portTypeList;
  };

  let setPortTypeList = function (portTypeListParam) {
    portTypeList = portTypeListParam;
  };

  ///
  let getUomList = function () {
    return uomList;
  };
  let setUomList = function (uomListParam) {
    uomList = uomListParam;
  };

  //
  let getHsTypeList = function () {
    return hsTypeList;
  };

  let setHsTypeList = function (hsTypeListParam) {
    hsTypeList = hsTypeListParam;
  };

  //
  let getAccountList = function () {
    return accountList;
  };

  let setAccountList = function (accountListParam) {
    accountList = accountListParam;
  };

  //
  let getAccountTypeList = function () {
    return accountTypeList;
  };

  let setAccountTypeList = function (accountTypeListParam) {
    accountTypeList = accountTypeListParam;
  };

  //
  let getUserList = function () {
    return userList;
  };

  let setUserList = function (userListParam) {
    userList = userListParam;
  };

  //
  let getAppCodeList = function () {
    return appCodeList;
  };

  let setAppCodeList = function (appCodeListParam) {
    appCodeList = appCodeListParam;
  };
  //
  let getGroupList = function () {
    return groupList;
  };

  let setGroupList = function (groupListParam) {
    groupList = groupListParam;
  };

  //
  let getBankCodeList = function () {
    return bankCodeList;
  };

  let setBankCodeList = function (bankCodeListParam) {
    bankCodeList = bankCodeListParam;
  };

  //
  let getBankBranchList = function () {
    return bankBranchList;
  };

  let setBankBranchList = function (bankBranchListParam) {
    bankBranchList = bankBranchListParam;
  };

  //
  let getContactTypeList = function () {
    return contactTypeList;
  };

  let setContactTypeList = function (contactTypeListParam) {
    contactTypeList = contactTypeListParam;
  };

  //
  let getUriTypeList = function () {
    return uriTypeList;
  };

  let setUriTypeList = function (uriTypeListParam) {
    uriTypeList = uriTypeListParam;
  };

  let getPermissionTypeList = function () {
    return permissionTypeList;
  };

  let setPermissionTypeList = function (permissionTypeListParam) {
    permissionTypeList = permissionTypeListParam;
  };

  let getUriList = function () {
    return uriList;
  };

  let setUriList = function (uriListListParam) {
    uriList = uriListListParam;
  };

  // Notification
  let getNotificationChannelList = function () {
    return notificationChannelList;
  };

  let setNotificationChannelList = function (notificationChannelListParam) {
    notificationChannelList = notificationChannelListParam;
  };

  //
  let getNotificationContentList = function () {
    return notificationContentList;
  };

  let setNotificationContentList = function (notificationContentListParam) {
    notificationContentList = notificationContentListParam;
  };

  //
  let getNotificationDeviceList = function () {
    return notificationDeviceList;
  };

  let setNotificationDeviceList = function (notificationDeviceListParam) {
    notificationDeviceList = notificationDeviceListParam;
  };

  //
  let getNotificationTemplateList = function () {
    return notificationTemplateList;
  };

  let setNotificationTemplateList = function (notificationTemplateListParam) {
    notificationTemplateList = notificationTemplateListParam;
  };

  // Announcement
  let getAnnouncementTypeList = function () {
    return announcementTypeList;
  };

  let setAnnouncementTypeList = function (announcementTypeListParam) {
    announcementTypeList = announcementTypeListParam;
  };

  let getReportCategoryList = function () {
    return reportCategoryList;
  };
  let setReportCategoryList = function (reportCategoryListParam) {
    reportCategoryList = reportCategoryListParam;
  };

  let getPortList = function () {
    return portList;
  };
  let setPortList = function (portListParam) {
    portList = portListParam;
  };
  /*
    var _initCountryList = function() {
      axios.get("/api/co/master/entity/country/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=ctyCode&iColumns=1")
      .then(result => {
        countryList = result;
      })
      .catch((error) => {
          console.log(error);
      });
    }

    var _initCurrencyList = function() {
      axios.get("")
      .then(result => {
        currencyList = result;
      })
      .catch((error) => {
          console.log(error);
      });
    }
*/
  return {
    getCountryList: getCountryList,
    setCountryList: setCountryList,
    getCurrencyList: getCurrencyList,
    setCurrencyList: setCurrencyList,
    getPortTypeList: getPortTypeList,
    setPortTypeList: setPortTypeList,
    getPortList: getPortList,
    setPortList: setPortList,
    getUomList: getUomList,
    setUomList: setUomList,

    getHsTypeList: getHsTypeList,
    setHsTypeList: setHsTypeList,

    getAccountList: getAccountList,
    setAccountList: setAccountList,

    getAccountTypeList: getAccountTypeList,
    setAccountTypeList: setAccountTypeList,

    getUserList: getUserList,
    setUserList: setUserList,

    getAppCodeList: getAppCodeList,
    setAppCodeList: setAppCodeList,

    getGroupList: getGroupList,
    setGroupList: setGroupList,

    getBankCodeList: getBankCodeList,
    setBankCodeList: setBankCodeList,

    getBankBranchList: getBankBranchList,
    setBankBranchList: setBankBranchList,

    getContactTypeList: getContactTypeList,
    setContactTypeList: setContactTypeList,

    getUriTypeList: getUriTypeList,
    setUriTypeList: setUriTypeList,

    getPermissionTypeList: getPermissionTypeList,
    setPermissionTypeList: setPermissionTypeList,

    getUriList: getUriList,
    setUriList: setUriList,

    getNotificationChannelList: getNotificationChannelList,
    setNotificationChannelList: setNotificationChannelList,

    getNotificationContentList: getNotificationContentList,
    setNotificationContentList: setNotificationContentList,

    getNotificationDeviceList: getNotificationDeviceList,
    setNotificationDeviceList: setNotificationDeviceList,

    getNotificationTemplateList: getNotificationTemplateList,
    setNotificationTemplateList: setNotificationTemplateList,

    getAnnouncementTypeList: getAnnouncementTypeList,
    setAnnouncementTypeList: setAnnouncementTypeList,

    getReportCategoryList: getReportCategoryList,
    setReportCategoryList: setReportCategoryList,
  };
})();

export default SessionCache;
