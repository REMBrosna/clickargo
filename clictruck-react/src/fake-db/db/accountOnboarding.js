import Mock from "../mock";

export const accOnBoardingDB = {
  list: [
    {
      accnrId: "AOB20220716134210",
      accnrCoIntial: null,
      accnrCompName: "Good Tyre Ltd.",
      accnrCompReg: "04216.3625-154.221",
      accnrAddressLine1: "163 Poplar Avenue",
      accnrAddressLine2: "Cliffside",
      accnrAddressLine3: "Virginia",
      accnrCity: "SG",
      accnrProv: "Singapore",
      accnrPcode: "56987658",
      accnrTel: "+631234567890",
      accnrFax: "+631234567890",
      accnrEmail: "qa.portedi@gmail.com",
      accnrAplName: "Marvin Abliter",
      accnrAplPassNid: "PHL2022-2",
      accnrAplTitle: "Developer",
      accnrAplEmail: "marvin.abliter@test.com",
      accnrAplTel: "+639276128432",
      accnrAplAddr1: "72 Project Street",
      accnrAplAddr2: "Morpheus Village",
      accnrAplAddr3: "",
      accnrAplCity: "Marilao",
      accnrAplProv: "Bulacan",
      accnrAplPcode: "3019",
      accnrAattDocType: null,
      accnrAattName: null,
      accnrStore: null,
      accnrStatus: "N",
      accnrCreateUid: "SYS",
      accnrCreateDt: 1666350875000,
      accnrLupdUid: "MPWT01",
      accnrLupdDt: 1666365436000,
      TMstAccnType: {
        atypId: "ACC_TYPE_CARGO_OWNER",
        atypDescription: "CARGO OWNER",
        atypDescriptionOth: null,
        atypStatus: "A",
        atypDtCreate: 1586235670000,
        atypUidCreate: "SYS",
        atypDtLupd: 1586235670000,
        atypUidLupd: "SYS"
      },
      TMstCountry: {
        ctyCode: "AE",
        ctyDescription: "UNITED ARAB EMIRATES",
        ctyStatus: "A",
        ctyDescriptionOth: null,
        ctyDtCreate: 1543593600000,
        ctyUidCreate: "SYS",
        ctyDtLupd: 1543593600000,
        ctyUidLupd: "SYS"
      },
      TMstAttType: null,
      TMstCountryApl: {
        ctyCode: "AD",
        ctyDescription: "ANDORRA",
        ctyStatus: "A",
        ctyDescriptionOth: "Ã¥Â®Â‰Ã©Â�Â“Ã¥Â°Â”",
        ctyDtCreate: 1543593600000,
        ctyUidCreate: "SYS",
        ctyDtLupd: 1640864763000,
        ctyUidLupd: "MPWT05"
      },
      accnrSubmitDt: 1659069711500,
      accnrApproveDt: null,
      accnrChannel: "ONLINE"
    },
    {
      accnrId: "AOB20220725091012",
      accnrCoIntial: "PTDHLGF",
      accnrCompName: "PT Dhl Global Forwarding",
      accnrCompReg: "01957.9218-058.000",
      accnrAddressLine1: "163 Poplar Avenue",
      accnrAddressLine2: "Cliffside",
      accnrAddressLine3: "Virginia",
      accnrCity: "SG",
      accnrProv: "Singapore",
      accnrPcode: "56987658",
      accnrTel: "+631234567890",
      accnrFax: "+631234567890",
      accnrEmail: "qa.portedi@gmail.com",
      accnrAplName: "Adenny Andrade",
      accnrAplPassNid: "PHL2022-1",
      accnrAplTitle: "Developer",
      accnrAplEmail: "adenny.andrade@test.com",
      accnrAplTel: "+639276128432",
      accnrAplAddr1: "34B 13th Avenue",
      accnrAplAddr2: "Murphy",
      accnrAplAddr3: "Cubao",
      accnrAplCity: "Quezon City",
      accnrAplProv: "Manila",
      accnrAplPcode: "1108",
      accnrAattDocType: null,
      accnrAattName: null,
      accnrStore: null,
      accnrStatus: "A",
      accnrCreateUid: "SYS",
      accnrCreateDt: 1666350875000,
      accnrLupdUid: "MPWT01",
      accnrLupdDt: 1666365436000,
      TMstAccnType: {
        atypId: "ACC_TYPE_FREIGHT_FORWARDER",
        atypDescription: "FREIGHT FORWARDER",
        atypDescriptionOth: null,
        atypStatus: "A",
        atypDtCreate: 1586235670000,
        atypUidCreate: "SYS",
        atypDtLupd: 1586235670000,
        atypUidLupd: "SYS"
      },
      TMstCountry: {
        ctyCode: "PH",
        ctyDescription: "PHILIPPINES",
        ctyStatus: "A",
        ctyDescriptionOth: null,
        ctyDtCreate: 1543593600000,
        ctyUidCreate: "SYS",
        ctyDtLupd: 1543593600000,
        ctyUidLupd: "SYS"
      },
      TMstAttType: null,
      TMstCountryApl: {
        ctyCode: "AD",
        ctyDescription: "ANDORRA",
        ctyStatus: "A",
        ctyDescriptionOth: "Ã¥Â®Â‰Ã©Â�Â“Ã¥Â°Â”",
        ctyDtCreate: 1543593600000,
        ctyUidCreate: "SYS",
        ctyDtLupd: 1640864763000,
        ctyUidLupd: "MPWT05"
      },
      accnrSubmitDt: 1658734911500,
      accnrApproveDt: 1659426111500,
      accnrChannel: "BACKOFFICE"
    }
  ],
};

export const suppDocsDB = {
  list: [
    {
      attId: "AD202207169415",
      attType: "Authoriser Proof",
      attName: "Good_Tyre_AuthoriserProof.pdf",
      mandatory: "Yes",
      expiryDate: "31/08/2023",
    },
    {
      attId: "AD202207169416",
      attType: "License",
      attName: "Good_Tyre_License.pdf",
      mandatory: "Yes",
      expiryDate: "17/10/2023",
    },
    {
      attId: "AD202207169417",
      attType: "Authoriser Proof",
      attName: "Good_Tyre_Others.pdf",
      mandatory: "Yes",
      expiryDate: "12/09/2025",
    }
  ],
};

export const suppDocsQuotationDB = {
  list: [
    {
      attId: "AD202207169415",
      attType: "Quotation",
      attName: "Good_Tyre_Quotation.pdf",
      mandatory: "Yes",
      expiryDate: "30/08/2023",
    }
  ],
};

export const genSuppDocsDB = {
  list: [
    {
      attId: 'AD2022071695415',
      tmstAttType: {
        mattName: "Certificate of Incorporation",
      },
      aattName: "Good_Tyre_Incorporation.pdf",
      mandatory: "Yes",
      file: "Test",
      expiryDate: "08/23/2023"
    }
  ],
};

export const servicesDB = {
  list: [
    {
      serviceId: "AS202208169415",
      serviceType: "CLICDO",
      startDate: "31/08/2022",
      expiryDate: "31/08/2023",
      status: "PEN",
    },
    {
      serviceId: "AS202208169415",
      serviceType: "CLICGATEPASS",
      startDate: "31/08/2023",
      expiryDate: "31/08/2023",
      status: "PEN",
    },
  ]
};

export const clicDeclareDB = {
  list: [
    {
      serviceType: "ClicDeclare",
      transactionId: "CL2022071695415",
      type: "INITIALIZE",
      date: "31/08/2022",
      reference: "CLCI1245341",
      adjustment: "+50000",
      line: "5000",
      status: "CPT"
    }
  ]
};

export const clicGatePassDB = {
  list: [
    {
      serviceType: "ClicGatePass",
      transactionId: "CL2022071695415",
      type: "INITIALIZE",
      date: "31/08/2023",
      reference: "CLCI1245341",
      adjustment: "+50000",
      line: "50000",
      status: "CPT"
    },
    {
      serviceType: "ClicGatePass",
      transactionId: "CL2022082916123",
      type: "ADJUSTMENT",
      date: "31/08/2023",
      reference: "CLCA12386733124",
      adjustment: "+10000",
      line: "58000",
      status: "PEN"
    },
  ]
};

Mock.onGet(/\/api\/mockaccnRegister\/details\/\w+/).reply((config) => {
  const id = config.url.split("/")[4];
  const response = accOnBoardingDB.list.find((accnr) => accnr.accnrId === id);
  return [200, response];
});

// Mock.onGet("/api/docRepo/all").reply((config) => {
//   return [200, accOnBoardingDB.list];
// });

// Mock.onGet("/api/country").reply((config) => {
//   const id = config.data;
//   const response = accOnBoardingDB.list.find((user) => user.id === id);
//   return [200, response];
// });

// Mock.onPost("/api/docRepo/delete").reply((config) => {
//   let user = JSON.parse(config.data);
//   let index = { i: 0 };
//   accOnBoardingDB.list.forEach((element) => {
//     if (element.id === user.id) {
//       return [200, accOnBoardingDB.list.splice(index.i, 1)];
//     }
//     index.i++;
//   });
//   return [200, accOnBoardingDB.list];
// });

// Mock.onPost("/api/docRepo/update").reply((config) => {
//   let user = JSON.parse(config.data);
//   let index = { i: 0 };
//   accOnBoardingDB.list.forEach((element) => {
//     if (element.id === user.id) {
//       accOnBoardingDB.list[index.i] = user;
//       return [200, accOnBoardingDB.list];
//     }
//     index.i++;
//   });
//   return [200, accOnBoardingDB.list];
// });

// Mock.onPost("/api/docRepo/add").reply((config) => {
//   let user = JSON.parse(config.data);
//   accOnBoardingDB.list.push(user);
//   return [200, accOnBoardingDB.list];
// });