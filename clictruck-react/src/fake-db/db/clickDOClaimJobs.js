import Mock from "../mock";

export const MOCK_CLAIM_JOBS_STATUS = {
  IN_PROGRESS: { code: "IN_PROGRESS", name: "IN PROGRESS" },
  SUBMITTED: { code: "SUBMITTED", name: "SUBMITTED" },
  COMPLETED: { code: "COMPLETED", name: "COMPLETED" },
  NEW: { code: "NEW", name: "NEW" },
  REJECTED: { code: "REJECTED", name: "REJECTED" },
  CANCELLED: { code: "CANCELLED", name: "CANCELLED" },
  DRAFT: { code: "DRAFT", name: "DRAFT" },
  PAID: { code: "PAID", name: "PAID" },
  PAYMENT_VERIFIED: { code: "PAYMENT_VERIFIED", name: "PAYMENT VERIFIED" },
}

export const clickDOClaimJobs = {
  list: [
    {
      jobId: "CKDOJ2022081051312",//T_CK_JOB_DOI_FF.JOB_ID
      shipmentType: "IMPORT",//T_CK_JOB.JOB_SHIPMENT_TYPE
      authoriserId: {
        id: "PTCARGOOWNERWONG",//T_CK_JOB_DOI_FF_DO.JDO_AUTHORIZER_ACCN
        name: "PT. CARGO OWNER WONG"
      },
      authorizedParty: {
        accnId: "YUSEN",//T_CK_JOB_DOI_FF_DO.JDO_AUTHORIZED_PARTY_ACCN
        accnName: "Yusen Logistics",
      },
      dtSubmitted: "2022-09-21 16:38:10",//T_CK_RECORD_DATE.RCD_DT_SUBMIT
      startDate: "2022-09-22",//T_CK_RECORD_DATE.RCD_DT_START
      expiryDate: "2022-12-12",//T_CK_RECORD_DATE.RCD_DT_EXPIRY
      status: MOCK_CLAIM_JOBS_STATUS.SUBMITTED.name//T_CK_JOB.JOB_STATE
    },
    {
      jobId: "CKDOJ2022081062343",//T_CK_JOB_DOI_FF.JOB_ID
      shipmentType: "IMPORT",//T_CK_JOB.JOB_SHIPMENT_TYPE
      authoriserId: {
        id: "PTCARGOOWNERWONG",//T_CK_JOB_DOI_FF_DO.JDO_AUTHORIZER_ACCN
        name: "PT. CARGO OWNER WONG"
      },
      authorizedParty: {
        accnId: "TOPCARGO",//T_CK_JOB_DOI_FF_DO.JDO_AUTHORIZED_PARTY_ACCN
        accnName: "PT. Trinity Omega PerkasaTay"
      },
      dtSubmitted: "2022-09-21 16:38:10",//T_CK_RECORD_DATE.RCD_DT_SUBMIT
      startDate: "2022-09-22",//T_CK_RECORD_DATE.RCD_DT_START
      expiryDate: "2022-12-12",//T_CK_RECORD_DATE.RCD_DT_EXPIRY
      status: MOCK_CLAIM_JOBS_STATUS.PAID.name//T_CK_JOB.JOB_STATE
    },
    {
      jobId: "CKDOJ2022081073452",//T_CK_JOB_DOI_FF.JOB_ID
      shipmentType: "IMPORT",//T_CK_JOB.JOB_SHIPMENT_TYPE
      authoriserId: {
        id: "PTCARGOOWNERWONG",//T_CK_JOB_DOI_FF_DO.JDO_AUTHORIZER_ACCN
        name: "PT. CARGO OWNER WONG"
      },
      authorizedParty: {
        accnId: "TERAFWD",//T_CK_JOB_DOI_FF_DO.JDO_AUTHORIZED_PARTY_ACCN
        accnName: "PT. Tera Forwarders"
      },
      dtSubmitted: "2022-09-21 16:38:10",//T_CK_RECORD_DATE.RCD_DT_SUBMIT
      startDate: "2022-09-22",//T_CK_RECORD_DATE.RCD_DT_START
      expiryDate: "2022-12-12",//T_CK_RECORD_DATE.RCD_DT_EXPIRY
      status: MOCK_CLAIM_JOBS_STATUS.PAYMENT_VERIFIED.name//T_CK_JOB.JOB_STATE
    },
    {
      jobId: "CKDOJ2022081092343",//T_CK_JOB_DOI_FF.JOB_ID
      shipmentType: "IMPORT",//T_CK_JOB.JOB_SHIPMENT_TYPE
      authoriserId: {
        id: "PTCARGOOWNERWONG",//T_CK_JOB_DOI_FF_DO.JDO_AUTHORIZER_ACCN
        name: "PT. CARGO OWNER WONG"
      },
      authorizedParty: {
        accnId: "PAPBOY",//T_CK_JOB_DOI_FF_DO.JDO_AUTHORIZED_PARTY_ACCN
        accnName: "Paperboy Logistic"
      },
      dtSubmitted: "2022-09-21 16:38:10",//T_CK_RECORD_DATE.RCD_DT_SUBMIT
      startDate: "2022-09-22",//T_CK_RECORD_DATE.RCD_DT_START
      expiryDate: "2022-12-12",//T_CK_RECORD_DATE.RCD_DT_EXPIRY
      status: MOCK_CLAIM_JOBS_STATUS.SUBMITTED.name//T_CK_JOB.JOB_STATE
    },
  ],
};

//T_CK_DOI.DOI_ID -> T_CK_DOI.DOI_BL_NO
export const selectedBLs = {
  list:
    [
      {
        blNo: "BL6624531",
        shipmentType: "IMPORT",
        authoriser: "Good Tyre Pte Ltd Logistics",
        dtSubmit: "2022-08-15 15:30:00",
        tckDoi: {
          doiBlNo: "MSC03051996"
        },
        tckJob: {
          tckMstShipmentType: {
            shtId: "IMPORT"
          },
          tckRecordDate: {
            rcdDtSubmit: "2022-08-17 15:30:00"
          }
        }
      },
      {
        blNo: "MSC0831983",
        shipmentType: "IMPORT",
        authoriser: "Good Tyre Pte Ltd Logistics",
        dtSubmit: "2022-08-16 15:45:00",
        tckDoi: {
          doiBlNo: "MSC09213322"
        },
        tckJob: {
          tckMstShipmentType: {
            shtId: "IMPORT"
          },
          tckRecordDate: {
            rcdDtSubmit: "2022-08-16 15:45:00"
          }
        }
      },
      {
        blNo: "MSC9210021",
        shipmentType: "IMPORT",
        authoriser: "PT. Cargo Owner Wong",
        dtSubmit: "2022-08-15 15:45:00",
        tckDoi: {
          doiBlNo: "MSC17392834"
        },
        tckJob: {
          tckMstShipmentType: {
            shtId: "IMPORT"
          },
          tckRecordDate: {
            rcdDtSubmit: "2022-08-15 15:45:00",
          }
        }
      },
      {
        blNo: "BL9834532",
        shipmentType: "IMPORT",
        authoriser: "PT. Cargo Owner Wong",
        dtSubmit: "2022-08-15 11:25:10",
        tckDoi: {
          doiBlNo: "MSC19391835"
        },
        tckJob: {
          tckMstShipmentType: {
            shtId: "IMPORT"
          },
          tckRecordDate: {
            rcdDtSubmit: "2022-08-15 11:25:10",
          }
        }
      }
    ]
}

export const clickDOClaimJobsTasks = {
  list:
    [
      {
        taskId: "CKT2022083063424",
        consToClaim: 6,
        noOfBl: 1,
        dtCreate: "2022-08-25 15:30:00",
        dtComplete: "2022-08-25 15:30:00",
        tstatus: MOCK_CLAIM_JOBS_STATUS.COMPLETED.code,
        ttype: "IMPORT",
        doList: [
          {
            doNo: "DO1231412",
            blNo: "BL1231438",
            authoriser: "GoodTyre Pte Ltd Logistics",
            noOfCons: "5"
          },
          {
            doNo: "DO1542125",
            blNo: "BL2623235",
            authoriser: "GoodTyre Pte Ltd Logistics",
            noOfCons: "3"
          },
        ]
      },
      {
        taskId: "CKT2022083031623",
        consToClaim: 8,
        noOfBl: 2,
        dtCreate: "2022-08-30 15:30:00",
        dtComplete: null,
        tstatus: MOCK_CLAIM_JOBS_STATUS.NEW.code,
        ttype: "IMPORT",
        doList: [
          {
            taskId: "CKT2022083063424",
            doNo: "DO1231412",
            blNo: "BL1231438",
            authoriser: "GoodTyre Pte Ltd Logistics",
            noOfCons: "5"
          },
          {
            taskId: "CKT2022083063424",
            doNo: "DO1542125",
            blNo: "BL2623235",
            authoriser: "GoodTyre Pte Ltd Logistics",
            noOfCons: "3"
          },
        ]
      },
    ]
}

export const doList = {
  list:
    [
      {
        taskId: "CKT2022083063424",
        doNo: "DO1231412",
        blNo: "BL1231438",
        authoriser: "GoodTyre Pte Ltd Logistics",
        noOfCons: "5"
      },
      {
        taskId: "CKT2022083063424",
        doNo: "DO1542125",
        blNo: "BL2623235",
        authoriser: "GoodTyre Pte Ltd Logistics",
        noOfCons: "3"
      },
      {
        taskId: "CKT2022083031623",
        doNo: "DO1231412",
        blNo: "BL1231438",
        authoriser: "GoodTyre Pte Ltd Logistics",
        noOfCons: "5"
      },
      {
        taskId: "CKT2022083031623",
        doNo: "DO1542125",
        blNo: "BL2623235",
        authoriser: "GoodTyre Pte Ltd Logistics",
        noOfCons: "3"
      }
    ]
}

export const doPaymentList = {
  list:
    [
      {
        doNo: "DO345214",
        blNo: "BL2132132",
        authoriser: "GoodTyre Pte Ltd Logistics",
        authoriserId: {
          id: "GOODTYREPTELTDLOGISTICS",
          name: "GOODTYRE PTE LTD LOGISTICS"
        },
        dtDocVerified: '',
        dtPayVerified: '',
        invoiceAmt: 150000000
      },
      {
        doNo: "DO512612",
        blNo: "BL2132148",
        authoriser: "GoodTyre Pte Ltd Logistics",
        authoriserId: {
          id: "GOODTYREPTELTDLOGISTICS",
          name: "GOODTYRE PTE LTD LOGISTICS"
        },
        dtDocVerified: '',
        dtPayVerified: '',
        invoiceAmt: 250000000
      },
      {
        doNo: "DO673112",
        blNo: "BL3164289",
        authoriser: "P.T. Cargo Owner Wong",
        authoriserId: {
          id: "PTCARGOOWNERWONG",
          name: "PT. CARGO OWNER WONG"
        },
        dtDocVerified: '',
        dtPayVerified: '',
        invoiceAmt: 150000000
      },
    ]
}

export const doPaymentDtlsList1 = {
  list:
    [
      {
        doNo: "DO345214",
        blNo: "BL2132132",
        authoriser: "GoodTyre Pte Ltd Logistics",
        authoriserId: {
          id: "GOODTYREPTELTDLOGISTICS",
          name: "GOODTYRE PTE LTD LOGISTICS"
        },
        dtVerified: 1660577400000,
        invoiceAmt: 150000000
      },
      {
        doNo: "DO512612",
        blNo: "BL2132148",
        authoriser: "GoodTyre Pte Ltd Logistics",
        authoriserId: {
          id: "GOODTYREPTELTDLOGISTICS",
          name: "GOODTYRE PTE LTD LOGISTICS"
        },
        dtVerified: 1660577400000,
        invoiceAmt: 250000000
      },
    ]
}

export const doPaymentDtlsList2 = {
  list:
    [
      {
        doNo: "DO673112",
        blNo: "BL3164289",
        authoriser: "P.T. Cargo Owner Wong",
        authoriserId: {
          id: "PTCARGOOWNERWONG",
          name: "PT. CARGO OWNER WONG"
        },
        dtVerified: 1660577400000,
        invoiceAmt: 150000000
      },
    ]
}

Mock.onGet("/api/mockgetdoList/all").reply((config) => {
  return [200, doList.list];
});

Mock.onGet(/\/api\/mockgetdo\/\w+/).reply((config) => {
  const id = config.url.split("/")[3];
  const response = clickDOClaimJobsTasks.list.find((dos) => dos.taskId === id);
  return [200, response];
});

Mock.onGet(/\/api\/redemptionTasks\/details\/\w+/).reply((config) => {
  const id = config.url.split("/")[4];
  const response = clickDOClaimJobs.list.find((job) => job.jobId === id);
  return [200, response];
});

Mock.onGet("/api/redemptionTasks/all").reply((config) => {
  return [200, clickDOClaimJobs.users];
});

Mock.onGet("/api/redemptionTasks").reply((config) => {
  const id = config.data;
  const response = clickDOClaimJobs.users.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/redemptionTasks/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  clickDOClaimJobs.users.forEach((element) => {
    if (element.id === user.id) {
      return [200, clickDOClaimJobs.users.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, clickDOClaimJobs.users];
});

Mock.onPost("/api/redemptionTasks/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  clickDOClaimJobs.users.forEach((element) => {
    if (element.id === user.id) {
      clickDOClaimJobs.users[index.i] = user;
      return [200, clickDOClaimJobs.users];
    }
    index.i++;
  });
  return [200, clickDOClaimJobs.users];
});

Mock.onPost("/api/redemptionTasks/add").reply((config) => {
  let user = JSON.parse(config.data);
  clickDOClaimJobs.users.push(user);
  return [200, clickDOClaimJobs.users];
});

Mock.onGet("/api/redemptionTasks/submitted").reply((config) => {
  let count = { i: 0 };
  clickDOClaimJobs.list.forEach((element) => {
    if (element.status === MOCK_CLAIM_JOBS_STATUS.SUBMITTED.name) {
      count.i++;
    }
  });
  return [200, count];
});

Mock.onGet("/api/mockgetBlList/all").reply((config) => {
  return [200, selectedBLs.list];
});

Mock.onPost("/api/mockSelectedBls/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  selectedBLs.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, selectedBLs.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, selectedBLs.list];
});