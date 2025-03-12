import Mock from "../mock";

export const MOCK_USER_STATUS = {
  ACTIVE: { code: "ACTIVE", name: "ACTIVE" },
  SUSPENDED: { code: "SUSPENDED", name: " SUSPENDED" },//possibly 'I' when saving in T_CORE_USR status
}

export const USER_ACCOUNTS = [
  {
      accnId: "GDTYLT",
      accnName: "Good Tyre Ltd",
      isAuthorized: true
  },
];

//T_CORE_USR
export const clickDOUsers = {
  list: [
    {
      usrUid: "BILLLAY",
      usrAccn: {
        accnId: "GDTYLT",//USR_ACCNID
        accnName: "Good Type Ltd",
        isAuthorized: true
      },
      usrStatus: MOCK_USER_STATUS.ACTIVE.code,
      usrTypeOnline: null,
      usrTypeMbox: null,
      usrName: "Bill Lay",
      usrPassNid: "PN1234",
      usrPosition: "Cargo Manager",//USR_TITLE
      usrDept: null,
      usrDtReg: null,
      usrDtComm: null,
      usrDtDeReg: null,
      usrDtReReg: null,
      usrClass: null,
      addrLn1: "Singapore",
      addrLn2: "Singapore",
      addrLn3: "Singapore",
      addrPcode: "10000",
      addrCity: "Singapore",
      addrProv: "Singapore",
      ctyCode: "SG",
      usrContactNo: "+85509165678",
      usrFax: null,
      usrContactEmail: "query@good.tyre.id",
      usrPwd: null,
      usrPwd1: null,
      usrPwd2: null,
      usrPwd3: null,
      usrPwd4: null,
      usrDtPwdLupd: null,
      usrPwdValidty: null,
      usrPwdForce: null,
      usrLoginInvcnt: null,
      usrDtLoginOk: null,
      usrDtLoginErr: null,
      usrMboxId: null,
      usrMboxMaxCnt: null,
      usrMboxMaxSiz: null,
      usrDurInbx: null,
      usrDurArch: null,
      usrDtCreate: "2022-10-18 04:00:00",
      usrUidCreate: null,
      usrDtModified: "2022-10-18 03:00:00",//USR_DT_LUPD
      usrUidLupd: null,
      TCoreRoles: [//not in T_CORE_USR
        {
          id: "OFFICER",
          roleDesc: "OFFICER"
        }
      ],
      usrOfficeNo: "+85509164321",//not in T_CORE_USR
      enableContactNo: 'Y',//not in T_CORE_USR
      enableContactEmail: 'N',//not in T_CORE_USR
      usrTelegramChatId: "billlay",//not in T_CORE_USR
      enableTelegramChatId: 'Y',//not in T_CORE_USR
    },
    {
      usrUid: "DESTAY",
      usrAccn: {
        accnId: "GDTYLT",//USR_ACCNID
        accnName: "Good Type Ltd",
        isAuthorized: true
      },
      usrStatus: MOCK_USER_STATUS.ACTIVE.code,
      usrTypeOnline: null,
      usrTypeMbox: null,
      usrName: "Desmond Tay",
      usrPassNid: "PN1235",
      usrPosition: "Cargo Manager",//USR_TITLE
      usrDept: null,
      usrDtReg: null,
      usrDtComm: null,
      usrDtDeReg: null,
      usrDtReReg: null,
      usrClass: null,
      addrLn1: "Singapore",
      addrLn2: "Singapore",
      addrLn3: "Singapore",
      addrPcode: "10000",
      addrCity: "Singapore",
      addrProv: "Singapore",
      ctyCode: "SG",
      usrContactNo: "+85509165678",
      usrFax: null,
      usrContactEmail: "query@good.tyre.id",
      usrPwd: null,
      usrPwd1: null,
      usrPwd2: null,
      usrPwd3: null,
      usrPwd4: null,
      usrDtPwdLupd: null,
      usrPwdValidty: null,
      usrPwdForce: null,
      usrLoginInvcnt: null,
      usrDtLoginOk: null,
      usrDtLoginErr: null,
      usrMboxId: null,
      usrMboxMaxCnt: null,
      usrMboxMaxSiz: null,
      usrDurInbx: null,
      usrDurArch: null,
      usrDtCreate: "2022-10-17 03:00:00",
      usrUidCreate: null,
      usrDtModified: "2022-10-17 02:00:00",//USR_DT_LUPD
      usrUidLupd: null,
      TCoreRoles: [
        {
          id: "ADMINISTRATION",
          roleDesc: "ADMINISTRATION"
        }
      ],
      usrOfficeNo: "+85509164321",//not in T_CORE_USR
      enableContactNo: 'Y',//not in T_CORE_USR
      enableContactEmail: 'N',//not in T_CORE_USR
      usrTelegramChatId: "desmondtay",//not in T_CORE_USR
      enableTelegramChatId: 'Y',//not in T_CORE_USR
    },
    {
      usrUid: "ALLENCHAN",
      usrAccn: {
        accnId: "GDTYLT",//USR_ACCNID
        accnName: "Good Type Ltd",
        isAuthorized: true
      },
      usrStatus: MOCK_USER_STATUS.SUSPENDED.code,
      usrTypeOnline: null,
      usrTypeMbox: null,
      usrName: "Allen Chan",
      usrPassNid: "PN1236",
      usrPosition: "Cargo Manager",//USR_TITLE
      usrDept: null,
      usrDtReg: null,
      usrDtComm: null,
      usrDtDeReg: null,
      usrDtReReg: null,
      usrClass: null,
      addrLn1: "Singapore",
      addrLn2: "Singapore",
      addrLn3: "Singapore",
      addrPcode: "10000",
      addrCity: "Singapore",
      addrProv: "Singapore",
      ctyCode: "SG",
      usrContactNo: "+85509165678",
      usrFax: null,
      usrContactEmail: "query@good.tyre.id",
      usrPwd: null,
      usrPwd1: null,
      usrPwd2: null,
      usrPwd3: null,
      usrPwd4: null,
      usrDtPwdLupd: null,
      usrPwdValidty: null,
      usrPwdForce: null,
      usrLoginInvcnt: null,
      usrDtLoginOk: null,
      usrDtLoginErr: null,
      usrMboxId: null,
      usrMboxMaxCnt: null,
      usrMboxMaxSiz: null,
      usrDurInbx: null,
      usrDurArch: null,
      usrDtCreate: "2022-10-16 01:00:00",
      usrUidCreate: null,
      usrDtModified: "2022-10-16 00:00:00",
      usrUidLupd: null,
      TCoreRoles: [//not in T_CORE_USR
        {
          id: "FINANCE",
          roleDesc: "FINANCE"
        }
      ],
      usrOfficeNo: "+85509164321",//not in T_CORE_USR
      enableContactNo: 'Y',//not in T_CORE_USR
      enableContactEmail: 'N',//not in T_CORE_USR
      usrTelegramChatId: "allenchan",//not in T_CORE_USR
      enableTelegramChatId: 'Y',//not in T_CORE_USR
    },
  ],
};

//T_CORE_USR_ROLE
export const userRoles = {
  list: [
    {
      urolUid: 'COPORTSA00',
      urolAppsCode: 'CONE',
      id: "ADMINISTRATION",
      roleDesc: "ADMINISTRATION",//not in T_CORE_USR_ROLE
      urolAdminOpt: 'Y',
      urolStatus: 'A',
      urolDtCreate: null,
      urolUidCreate: null, 
      urolDtLupd: null,
      urolUidLupd: null,
      urolTempRole: 'N'
    },
    {
      urolUid: 'COPORTSA00',
      urolAppsCode: 'CONE',
      id: "FINANCE",
      roleDesc: "FINANCE",//not in T_CORE_USR_ROLE
      urolAdminOpt: 'N',
      urolStatus: 'A',
      urolDtCreate: null,
      urolUidCreate: null, 
      urolDtLupd: null,
      urolUidLupd: null,
      urolTempRole: 'N'
    },
    {
      urolUid: 'COPORTSA00',
      urolAppsCode: 'CONE',
      id: "OFFICER",
      roleDesc: "OFFICER",//not in T_CORE_USR_ROLE
      urolAdminOpt: 'N',
      urolStatus: 'A',
      urolDtCreate: null,
      urolUidCreate: null, 
      urolDtLupd: null,
      urolUidLupd: null,
      urolTempRole: 'N'
    },
  ],
};

// Mock.onGet("/api/users/all/list").reply((config) => {
//   return [200, clickDOUsers.list];
// });

Mock.onPost("/api/users/new").reply((config) => {
  return [200, {}];
});

Mock.onGet(/\/api\/users\/details\/\w+/).reply((config) => {
  const id = config.url.split("/")[4];
  const response = clickDOUsers.list.find((user) => user.usrUid === id);
  return [200, response];
});

// Mock.onGet("/api/users").reply((config) => {
//   const id = config.data;
//   const response = clickDOUsers.list.find((user) => user.id === id);
//   return [200, response];
// });

Mock.onPost("/api/users/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  clickDOUsers.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, clickDOUsers.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, clickDOUsers.list];
});

Mock.onPut("/api/users/update").reply((config) => {
  let user = JSON.parse(config.data);
  clickDOUsers.list.forEach((element, idx) => {
    if (element.usrUid === user?.usrUid) {
        clickDOUsers.list[idx] = user;
      return element;
    }
  });
  return [200, user];
});

Mock.onPost("/api/users/add").reply((config) => {
  let user = JSON.parse(config.data);
  clickDOUsers.list.push(user);
  return [200, clickDOUsers.list];
});

Mock.onGet(/\/api\/user\/accounts/).reply((config) => {
  // const isAuth = config.url.split("/")[4] === 'y' ? true : false;
  const response = USER_ACCOUNTS.filter((accn) => accn.isAuthorized === true);
  return [200, response];
});

Mock.onGet("/api/users/roles/list").reply((config) => {
  return [200, userRoles.list];
});
