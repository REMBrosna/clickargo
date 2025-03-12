import Mock from "../mock";

//T_CK_MST_AUTH_STATE
export const MOCK_AUTH_STATUS = {
  AUTHORISED: { code: "AUTHORISED", name: "AUTHORISED" },
  SUBMITTED: { code: "SUBMITTED", name: " SUBMITTED" },
  PENDING_AUTHORIZATION: { code: "PENDING_AUTHORIZATION", name: " PENDING_AUTHORIZATION" },
  EXPIRED: { code: "EXPIRED", name: " EXPIRED" },
  DRAFT: { code: "DRAFT", name: " DRAFT" },
  NEW: { code: "NEW", name: " NEW" },
  TERMINATED: { code: "TERMINATED", name: " TERMINATED" },
  REJECTED: { code: "REJECTED", name: " REJECTED" },
}

export const MOCK_AUTH_PARTY_STATUS = {
  ACTIVE: { code: "ACTIVE", name: "ACTIVE" },
  SUSPENDED: { code: "SUSPENDED", name: " SUSPENDED" },
}

//T_CK_SERVICE_AUTH
export const clickDOAuthorisations = {
  list: [
    {
      authId: "CKA2022071695415",//SVAU_ID
      svauService: "CLICDO",
      authStatus: MOCK_AUTH_STATUS.AUTHORISED.code,//SVAU_STATE
      usrUid: "BILLLAY",//SVAU_USR_AUTHORIZER
      authoriser: {
        accnrId: "AOB20220716134210",
        atypId: "ACC_TYPE_CARGO_OWNER",
        accnId: "GDTYRE",//SVAU_ACCN_AUTHORIZER
        name: "Good Tyre Ltd"
      },
      //SVAU_POSITION_AUTHORIZER note: in FE currenty using USR_TITLE
      authPartyUid: "JARRGOH",//SVAU_USR_AUTHORIZED
      authorisedParty: {
        accnrId: "AOB20220725091012",
        atypId: "ACC_TYPE_FREIGHT_FORWARDER",
        accnId: "YUSEN",//SVAU_ACCN_AUTHORIZED
        name: "Yusen Logistics"
      },
      //SVAU_POSITION_AUTHORIZED note: in FE currenty using USR_TITLE
      shippingLine: {
        accnId: "MSC",//SVAU_ACCN_SERVICE <- TBC
        accnName: "Mediterranean Shipping Co",
        isAuthorized: true
      },
      dateStart: "2022-10-18 00:00:00",//SVAU_DT_SERVICE_START
      dateValidity: "2023-12-31 00:00:00",//SVAU_DT_SERVICE_VALID
      //SVAU_REMARKS_AUTHORIZER
      //SVAU_REMARKS_AUTHORIZED
      //SVAU_STATUS 'A' or 'I'
      //SVAU_DT_CREATE
      //SVAU_UID_CREATE
      //SVAU_DT_LUPD
      //SVAU_UID_LUPD
      dateSubmitted: "2022-10-18 00:00:00",//not in T_CK_SERVICE_AUTH
      dateAuthorisation: "2022-11-17 00:00:00",//not in T_CK_SERVICE_AUTH
    },
    {
      authId: "CKA2022071695416",//SVAU_ID
      svauService: "CLICDO",
      authStatus: MOCK_AUTH_STATUS.SUBMITTED.code,//SVAU_STATE
      usrUid: "DESTAY",//SVAU_USR_AUTHORIZER
      authoriser: {
        accnrId: "AOB20220716134210",
        atypId: "ACC_TYPE_CARGO_OWNER",
        accnId: "GDTYRE",//SVAU_ACCN_AUTHORIZER
        name: "Good Tyre Ltd"
      },
      //SVAU_POSITION_AUTHORIZER note: in FE currenty using USR_TITLE
      authPartyUid: "MARVIN",//SVAU_USR_AUTHORIZED
      authorisedParty: {
        accnrId: "AOB20220725091012",
        //        accnId: "TOPCARGO",
        atypId: "ACC_TYPE_FREIGHT_FORWARDER",
        accnId: "PTRINITY",//SVAU_ACCN_AUTHORIZED
        name: "PT. Trinity Omega PerkasaTay"
      },
      //SVAU_POSITION_AUTHORIZED note: in FE currenty using USR_TITLE
      shippingLine: {
        accnId: "MSC",//SVAU_ACCN_SERVICE <- TBC
        accnName: "Mediterranean Shipping Co",
        isAuthorized: true
      },
      dateStart: "2022-10-17 00:00:00",//SVAU_DT_SERVICE_START
      dateValidity: "-",//SVAU_DT_SERVICE_VALID
      //SVAU_REMARKS_AUTHORIZER
      //SVAU_REMARKS_AUTHORIZED
      //SVAU_STATUS 'A' or 'I'
      //SVAU_DT_CREATE
      //SVAU_UID_CREATE
      //SVAU_DT_LUPD
      //SVAU_UID_LUPD
      dateSubmitted: "2022-10-17 00:00:00",//not in T_CK_SERVICE_AUTH
      dateAuthorisation: "-",//not in T_CK_SERVICE_AUTH
    },
    {
      authId: "CKA2022071695417",//SVAU_ID
      svauService: "CLICDO",
      authStatus: MOCK_AUTH_STATUS.PENDING_AUTHORIZATION.code,//SVAU_STATE
      usrUid: "ALLENCHAN",//SVAU_USR_AUTHORIZER
      authoriser: {
        accnrId: "AOB20220716134210",
        atypId: "ACC_TYPE_CARGO_OWNER",
        accnId: "GDTYRE",//SVAU_ACCN_AUTHORIZER
        name: "Good Tyre Ltd"
      },
      //SVAU_POSITION_AUTHORIZER note: in FE currenty using USR_TITLE
      authPartyUid: "ADENNY",//SVAU_USR_AUTHORIZED
      authorisedParty: {
        accnrId: "AOB20220725091012",
        atypId: "ACC_TYPE_FREIGHT_FORWARDER",
        // accnId: "TERAFWD",
        accnId: "PTERA",//SVAU_ACCN_AUTHORIZED
        name: "PT. Tera Forwarders"
      },
      //SVAU_POSITION_AUTHORIZED note: in FE currenty using USR_TITLE
      shippingLine: {
        accnId: "MSC",//SVAU_ACCN_SERVICE <- TBC
        accnName: "Mediterranean Shipping Co",
        isAuthorized: true
      },
      dateStart: "2022-10-16 00:00:00",//SVAU_DT_SERVICE_START
      dateValidity: "-",//SVAU_DT_SERVICE_VALID
      //SVAU_REMARKS_AUTHORIZER
      //SVAU_REMARKS_AUTHORIZED
      //SVAU_STATUS 'A' or 'I'
      //SVAU_DT_CREATE
      //SVAU_UID_CREATE
      //SVAU_DT_LUPD
      //SVAU_UID_LUPD
      dateSubmitted: "2022-10-16 00:00:00",//not in T_CK_SERVICE_AUTH
      dateAuthorisation: "-",//not in T_CK_SERVICE_AUTH
    },
    {
      authId: "CKA2022071695418",//SVAU_ID
      svauService: "CLICDO",
      authStatus: MOCK_AUTH_STATUS.EXPIRED.code,//SVAU_STATE
      usrUid: "BILLLAY",//SVAU_USR_AUTHORIZER
      authoriser: {
        accnrId: "AOB20220716134210",
        atypId: "ACC_TYPE_CARGO_OWNER",
        accnId: "GDTYRE",//SVAU_ACCN_AUTHORIZER
        name: "Good Tyre Ltd"
      },
      //SVAU_POSITION_AUTHORIZER note: in FE currenty using USR_TITLE
      authPartyUid: "NINA",//SVAU_USR_AUTHORIZED
      authorisedParty: {
        accnrId: "AOB20220725091012",
        atypId: "ACC_TYPE_FREIGHT_FORWARDER",
        accnId: "PRIMAINT",//SVAU_ACCN_AUTHORIZED
        name: "PT Prima International Cargo"
      },
      //SVAU_POSITION_AUTHORIZED note: in FE currenty using USR_TITLE
      shippingLine: {
        accnId: "MSC",//SVAU_ACCN_SERVICE <- TBC
        accnName: "Mediterranean Shipping Co",
        isAuthorized: true
      },
      dateStart: "2022-10-15 00:00:00",//SVAU_DT_SERVICE_START
      dateValidity: "2022-09-22 00:00:00",//SVAU_DT_SERVICE_VALID
      //SVAU_REMARKS_AUTHORIZER
      //SVAU_REMARKS_AUTHORIZED
      //SVAU_STATUS 'A' or 'I'
      //SVAU_DT_CREATE
      //SVAU_UID_CREATE
      //SVAU_DT_LUPD
      //SVAU_UID_LUPD
      dateSubmitted: "2021-09-15 00:00:00",//not in T_CK_SERVICE_AUTH
      dateAuthorisation: "2021-09-22 00:00:00",//not in T_CK_SERVICE_AUTH
    },
    {
      authId: "CKA2022071695419",//SVAU_ID
      svauService: "CLICDO",
      authStatus: MOCK_AUTH_STATUS.DRAFT.code,//SVAU_STATE
      usrUid: "ALLENCHAN",//SVAU_USR_AUTHORIZER
      authoriser: {
        accnrId: "AOB20220716134210",
        atypId: "ACC_TYPE_CARGO_OWNER",
        accnId: "GDTYRE",//SVAU_ACCN_AUTHORIZER
        name: "Good Tyre Ltd"
      },
      //SVAU_POSITION_AUTHORIZER note: in FE currenty using USR_TITLE
      authPartyUid: "MILLE",//SVAU_USR_AUTHORIZED
      authorisedParty: {
        accnrId: "AOB20220725091012",
        atypId: "ACC_TYPE_FREIGHT_FORWARDER",
        // accnId: "PAPBOY",
        accnId: "PPBOY",//SVAU_ACCN_AUTHORIZED
        name: "Paperboy Logistic"
      },
      //SVAU_POSITION_AUTHORIZED note: in FE currenty using USR_TITLE
      shippingLine: {
        accnId: "MSC",//SVAU_ACCN_SERVICE <- TBC
        accnName: "Mediterranean Shipping Co",
        isAuthorized: true
      },
      dateStart: "2022-10-18 00:00:00",  //SVAU_DT_SERVICE_START   
      dateValidity: "-",//SVAU_DT_SERVICE_VALID
      //SVAU_REMARKS_AUTHORIZER
      //SVAU_REMARKS_AUTHORIZED
      //SVAU_STATUS 'A' or 'I'
      //SVAU_DT_CREATE
      //SVAU_UID_CREATE
      //SVAU_DT_LUPD
      //SVAU_UID_LUPD
      dateSubmitted: "2022-10-18 02:00:00",//not in T_CK_SERVICE_AUTH
      dateAuthorisation: "-",//not in T_CK_SERVICE_AUTH
    },
  ],
};

//use accounts.js FORWARDER_ACCOUNTS instead
// export const AUTH_PARTY_ACCOUNTS = [
//   {
//       accnId: "AGLP",
//       accnName: "Agape Worldwide Logistics",
//       isAuthorized: true
//   },
//   {
//     accnId: "OIA",
//     accnName: "OIA Global",
//     isAuthorized: true
//   },
// ];

export const SHIPPING_LINE = [
  {
    accnId: "MSC",
    accnName: "Mediterranean Shipping Co",
    isAuthorized: true
  },
];

//SVAU_USR_AUTHORIZED info, similar to T_CORE_USR
export const authParty = {
  list: [
    // {
    //   usrUid: "JARRGOH",
    //   usrAccn: {
    //     accnId: "AGLP",
    //     accnName: "Agape Worldwide Logistics",
    //     isAuthorized: true
    //   },
    //   usrName: "Jarret Goh",
    //   TCoreRoles: {
    //     id: "OFFICER",
    //     roleDesc: "OFFICER"
    //   },
    //   usrDtCreate: "2022-10-18 04:00:00",
    //   usrDtModified: "2022-10-18 03:00:00",
    //   usrStatus: MOCK_AUTH_PARTY_STATUS.ACTIVE.code,
    //   usrPassNid: "PN1234",
    //   usrPosition: "Freight Manager",
    //   usrOfficeNo: "+85509164321",
    //   usrContactNo: "+85509165678",
    //   enableContactNo: 'Y',
    //   usrContactEmail: "query@good.tyre.id",
    //   enableContactEmail: 'N',
    //   usrTelegramChatId: "billlay",
    //   enableTelegramChatId: 'Y',
    //   addrLn1: "Singapore",
    //   addrLn2: "Singapore",
    //   addrLn3: "Singapore",
    //   addrProv: "Singapore",
    //   addrCity: "Singapore",
    //   addrPcode: "10000",
    //   ctyCode: "SG"
    // },
    {
      usrUid: "JARRGOH",
      usrAccn: {
        accnId: "YUSEN",
        accnName: "Yusen Logistics",
        isAuthorized: true
      },
      usrName: "Jarret Goh",
      TCoreRoles: {
        id: "OFFICER",
        roleDesc: "OFFICER"
      },
      usrDtCreate: "2022-10-18 04:00:00",
      usrDtModified: "2022-10-18 03:00:00",
      usrStatus: MOCK_AUTH_PARTY_STATUS.ACTIVE.code,
      usrPassNid: "PN1234",
      usrPosition: "Freight Manager",
      usrOfficeNo: "+85509164321",
      usrContactNo: "+85509165678",
      enableContactNo: 'Y',
      usrContactEmail: "query@good.tyre.id",
      enableContactEmail: 'N',
      usrTelegramChatId: "jarretgoh",
      enableTelegramChatId: 'Y',
      addrLn1: "Singapore",
      addrLn2: "Singapore",
      addrLn3: "Singapore",
      addrProv: "Singapore",
      addrCity: "Singapore",
      addrPcode: "10000",
      ctyCode: "SG"
    },
    {
      usrUid: "MARVIN",
      usrAccn: {
        accnId: "PTRINITY",
        accnName: "PT. Trinity Omega PerkasaTay",
        isAuthorized: true
      },
      usrName: "Jarret Goh",
      TCoreRoles: {
        id: "OFFICER",
        roleDesc: "OFFICER"
      },
      usrDtCreate: "2022-10-18 04:00:00",
      usrDtModified: "2022-10-18 03:00:00",
      usrStatus: MOCK_AUTH_PARTY_STATUS.ACTIVE.code,
      usrPassNid: "PN1234",
      usrPosition: "Freight Manager",
      usrOfficeNo: "+85509164321",
      usrContactNo: "+85509165678",
      enableContactNo: 'Y',
      usrContactEmail: "query@good.tyre.id",
      enableContactEmail: 'N',
      usrTelegramChatId: "jarretgoh",
      enableTelegramChatId: 'Y',
      addrLn1: "Singapore",
      addrLn2: "Singapore",
      addrLn3: "Singapore",
      addrProv: "Singapore",
      addrCity: "Singapore",
      addrPcode: "10000",
      ctyCode: "SG"
    },
    {
      usrUid: "ADENNY",
      usrAccn: {
        accnId: "PTERA",
        accnName: "PT. Tera Forwarders",
        isAuthorized: true
      },
      usrName: "Jarret Goh",
      TCoreRoles: {
        id: "OFFICER",
        roleDesc: "OFFICER"
      },
      usrDtCreate: "2022-10-18 04:00:00",
      usrDtModified: "2022-10-18 03:00:00",
      usrStatus: MOCK_AUTH_PARTY_STATUS.ACTIVE.code,
      usrPassNid: "PN1234",
      usrPosition: "Freight Manager",
      usrOfficeNo: "+85509164321",
      usrContactNo: "+85509165678",
      enableContactNo: 'Y',
      usrContactEmail: "query@good.tyre.id",
      enableContactEmail: 'N',
      usrTelegramChatId: "jarretgoh",
      enableTelegramChatId: 'Y',
      addrLn1: "Singapore",
      addrLn2: "Singapore",
      addrLn3: "Singapore",
      addrProv: "Singapore",
      addrCity: "Singapore",
      addrPcode: "10000",
      ctyCode: "SG"
    },
    {
      usrUid: "NINA",
      usrAccn: {
        accnId: "PRIMAINT",
        accnName: "PT Prima International Cargo",
        isAuthorized: true
      },
      usrName: "Jarret Goh",
      TCoreRoles: {
        id: "OFFICER",
        roleDesc: "OFFICER"
      },
      usrDtCreate: "2022-10-18 04:00:00",
      usrDtModified: "2022-10-18 03:00:00",
      usrStatus: MOCK_AUTH_PARTY_STATUS.ACTIVE.code,
      usrPassNid: "PN1234",
      usrPosition: "Freight Manager",
      usrOfficeNo: "+85509164321",
      usrContactNo: "+85509165678",
      enableContactNo: 'Y',
      usrContactEmail: "query@good.tyre.id",
      enableContactEmail: 'N',
      usrTelegramChatId: "jarretgoh",
      enableTelegramChatId: 'Y',
      addrLn1: "Singapore",
      addrLn2: "Singapore",
      addrLn3: "Singapore",
      addrProv: "Singapore",
      addrCity: "Singapore",
      addrPcode: "10000",
      ctyCode: "SG"
    },
    {
      usrUid: "MILLE",
      usrAccn: {
        accnId: "PAPBOY",
        accnName: "Paperboy Logistic",
        isAuthorized: true
      },
      usrName: "Jarret Goh",
      TCoreRoles: {
        id: "OFFICER",
        roleDesc: "OFFICER"
      },
      usrDtCreate: "2022-10-18 04:00:00",
      usrDtModified: "2022-10-18 03:00:00",
      usrStatus: MOCK_AUTH_PARTY_STATUS.ACTIVE.code,
      usrPassNid: "PN1234",
      usrPosition: "Freight Manager",
      usrOfficeNo: "+85509164321",
      usrContactNo: "+85509165678",
      enableContactNo: 'Y',
      usrContactEmail: "query@good.tyre.id",
      enableContactEmail: 'N',
      usrTelegramChatId: "jarretgoh",
      enableTelegramChatId: 'Y',
      addrLn1: "Singapore",
      addrLn2: "Singapore",
      addrLn3: "Singapore",
      addrProv: "Singapore",
      addrCity: "Singapore",
      addrPcode: "10000",
      ctyCode: "SG"
    },
  ],
};

export const authLettersDB = {
  list: [
    {
      attId: "2022102112341",
      taskId: "CKT2022083031623",
      attType: "Power of Authority",
      authoriser: "GoodTyre Pte Ltd Logistics",
      blNo: "BL6624531",
      doNo: "DO345214",
      createDate: "31/08/2022",
    },
    {
      attId: "2022102112342",
      taskId: "CKT2022083031623",
      attType: "Letter of Assignment",
      authoriser: "GoodTyre Pte Ltd Logistics",
      blNo: "BL6624531",
      doNo: "DO345214",
      createDate: "31/08/2022",
    },
    {
      attId: "2022102112343",
      taskId: "CKT2022083031623",
      attType: "Bill of Lading",
      authoriser: "GoodTyre Pte Ltd Logistics",
      blNo: "BL6624531",
      doNo: "DO345214",
      createDate: "31/08/2022",
    },
    {
      attId: "2022102112344",
      taskId: "CKT2022083031623",
      attType: "Container Loan Application",
      authoriser: "GoodTyre Pte Ltd Logistics",
      blNo: "BL6624531",
      doNo: "DO345214",
      createDate: "31/08/2022",
    },
    {
      attId: "2022102112345",
      taskId: "CKT2022083031623",
      attType: "Power of Authority",
      authoriser: "PT. Cargo Owner Wong",
      blNo: "BL7234122",
      doNo: "DO673112",
      createDate: "31/08/2022",
    },
    {
      attId: "2022102112346",
      taskId: "CKT2022083031623",
      attType: "Letter of Assignment",
      authoriser: "PT. Cargo Owner Wong",
      blNo: "BL7234122",
      doNo: "DO673112",
      createDate: "31/08/2022",
    },
    {
      attId: "2022102112347",
      taskId: "CKT2022083031623",
      attType: "Bill of Lading",
      authoriser: "PT. Cargo Owner Wong",
      blNo: "BL7234122",
      doNo: "DO673112",
      createDate: "31/08/2022",
    },
    {
      attId: "2022102112348",
      taskId: "CKT2022083031623",
      attType: "Container Loan Application",
      authoriser: "PT. Cargo Owner Wong",
      blNo: "BL7234122",
      doNo: "DO673112",
      createDate: "31/08/2022",
    },
  ],
};

export const attType = [
  {
    mattId: "Certificate_of_Incorporation",
    mattName: "Certificate of Incorporation",
  },
  {
    mattId: "CONTRACT",
    mattName: "CONTRACT (M)",
  },
  {
    mattId: "TAX",
    mattName: "TAX (O)",
  },
];

//TBC if T_CORE_ATTACH
export const authSuppDocs = {
  list: [
    // {
    //   attId: 'AD2022071695415',
    //   tmstAttType: {
    //     mattId: "COI",
    //     mattName: "Certificate of Incorporation",
    //   },
    //   aattName: "Good_Tyre_Incorporation.pdf",
    //   mandatory: "Yes",
    //   file: "Test",
    //   expiryDate: "08/23/2023",
    //   attData: "",    
    // }
  ]
};

//TBC if T_CORE_ATTACH
export const authPartySuppDocs = {
  list: [
    // {
    //   attId: 'AD2022071637181',
    //   tmstAttType: {
    //     mattId: "COI",
    //     mattName: "Certificate of Incorporation",
    //   },
    //   aattName: "Agape_Incorporation.pdf",
    //   mandatory: "Yes",
    //   file: "Test",
    //   expiryDate: "08/30/2023",
    //   attData: "",    
    // }
  ]
};

const generateID = (prefix) => {
  var date = new Date();
  var y = date.toLocaleDateString("default", { year: "numeric" });
  var m = date.toLocaleDateString("default", { month: "2-digit" });
  var d = date.toLocaleDateString("default", { day: "2-digit" });
  return prefix + y + m + d + Math.random(1, 5);
}

export const newAuthDb = {
  authId: generateID('CKA'),
  authoriserAccountId: "",
  authoriserName: "",
  authorisedPartyAccountId: null,
  authorisedPartyName: null,
  dateSubmitted: null,
  dateAuthorisation: null,
  dateValidity: null,
  authStatus: MOCK_AUTH_STATUS.NEW.code
};

Mock.onGet("/api/authorisations/all/list").reply((config) => {
  return [200, clickDOAuthorisations.list];
});


Mock.onPost("/api/authorisations/new").reply((config) => {
  return [200, newAuthDb];
});

Mock.onGet(/\/api\/authorisations\/details\/\w+/).reply((config) => {
  const id = config.url.split("/")[4];
  const response = clickDOAuthorisations.list.find((auth) => auth.authId === id);
  return [200, response];
});

// Mock.onGet("/api/authorisations").reply((config) => {
//   const id = config.data;
//   const response = clickDOAuthorisations.list.find((authorisation) => authorisation.id === id);
//   return [200, response];
// });

Mock.onDelete("/api/authorisations/delete").reply((config) => {
  let authorisation = JSON.parse(config.data);
  let index = { i: 0 };
  clickDOAuthorisations.list.forEach((element) => {
    if (element.authId === authorisation.authId) {
      return [200, clickDOAuthorisations.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, clickDOAuthorisations.list];
});

Mock.onPut("/api/authorisations/update").reply((config) => {
  let authorisation = JSON.parse(config.data);
  let index = { i: 0 };
  clickDOAuthorisations.list.forEach((element) => {
    if (element.authId === authorisation.authId) {
      clickDOAuthorisations.list[index.i] = authorisation;
      return [200, clickDOAuthorisations.list];
    }
    index.i++;
  });
  return [200, clickDOAuthorisations.list];
});

Mock.onPost("/api/authorisations/add").reply((config) => {
  let authorisation = JSON.parse(config.data);
  clickDOAuthorisations.list.push(authorisation);
  return [200, clickDOAuthorisations.list];
});

Mock.onGet(/\/api\/authorisations\/shippingline/).reply((config) => {
  const response = SHIPPING_LINE.filter((sl) => sl.isAuthorized === true);
  return [200, response];
});

Mock.onGet(/\/api\/authorisations\/authParty\/\w+/).reply((config) => {
  const id = config.url.split("/")[4];
  const response = authParty.list.find((auth) => auth.usrUid === id);
  return [200, response];
});

Mock.onGet(/\/api\/authorisations\/attType/).reply((config) => {
  return [200, attType];
});

Mock.onPost("/api/authorisations/authSuppDocs/add").reply((config) => {
  let authorisation = JSON.parse(config.data);
  authSuppDocs.list.push(authorisation);
  return [200, authSuppDocs.list];
});

Mock.onPost("/api/authorisations/authPartySuppDocs/add").reply((config) => {
  let authorisation = JSON.parse(config.data);
  authPartySuppDocs.list.push(authorisation);
  return [200, authPartySuppDocs.list];
});
