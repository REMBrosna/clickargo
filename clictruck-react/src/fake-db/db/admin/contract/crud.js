import Mock from "../../../mock";

const data = {
  "otherLangDesc": "",
  "coreMstLocale": null,
  "conId": "88373878",
  "conName": "PRATAMA",
  "conDescription": "desc",
  "conDtStart": 1683890017641,
  "conDtEnd": 1683890017643,
  "conStatus": "A",
  "conDtCreate": 1683824400000,
  "conUidCreate": "",
  "conDtLupd": 1683890017643,
  "conUidLupd": "",
  "tckCtContractChargeByConChargeCoFf": {
    "otherLangDesc": "",
    "coreMstLocale": null,
    "concId": "PPBOY",
    "concPltfeeAmt": "400",
    "concPltfeeType": "",
    "concAddtaxAmt": "500",
    "concAddtaxType": "",
    "concWhtaxAmt": "600",
    "concWhtaxType": "",
    "concStatus": "",
    "concDtCreate": 1683890017644,
    "concUidCreate": "",
    "concDtLupd": 1683890017644,
    "concUidLupd": ""
  },
  "tckCtContractChargeByConChargeTo": {
    "otherLangDesc": "",
    "coreMstLocale": null,
    "concId": "PTRINITY",
    "concPltfeeAmt": "100",
    "concPltfeeType": "",
    "concAddtaxAmt": "",
    "concAddtaxType": "",
    "concWhtaxAmt": "300",
    "concWhtaxType": "",
    "concStatus": "",
    "concDtCreate": 1683890017644,
    "concUidCreate": "",
    "concDtLupd": 1683890017644,
    "concUidLupd": ""
  },
  "tcoreAccnByConTo": {
    "otherLangDesc": "",
    "coreMstLocale": null,
    "accnId": "",
    "accnStatus": "\u0000",
    "accnName": "",
    "accnNameOth": "",
    "accnCoyRegn": "",
    "accnPassNid": "",
    "accnNationality": "",
    "accnVatFlag": "",
    "accnVatNo": "",
    "accnAddr": null,
    "accnContact": null,
    "accnDtAgree": 1683890017653,
    "accnDtReg": 1683890017653,
    "accnDtDereg": 1683890017653,
    "accnDtRereg": 1683890017653,
    "accnDtSusp": 1683890017653,
    "accnDtReins": 1683890017653,
    "accnDtCreate": 1683890017653,
    "accnUidCreate": "",
    "accnDtLupd": 1683890017653,
    "accnUidLupd": "",
    "accnBusinessAct": "",
    "accnOwnerNationality": "",
    "accnOwnerName": "",
    "cityCode": "",
    "coreLocale": null,
    "TMstAccnType": null
  },
  "tcoreAccnByConCoFf": {
    "otherLangDesc": "",
    "coreMstLocale": null,
    "accnId": "",
    "accnStatus": "\u0000",
    "accnName": "",
    "accnNameOth": "",
    "accnCoyRegn": "",
    "accnPassNid": "",
    "accnNationality": "",
    "accnVatFlag": "",
    "accnVatNo": "",
    "accnAddr": null,
    "accnContact": null,
    "accnDtAgree": 1683890017653,
    "accnDtReg": 1683890017653,
    "accnDtDereg": 1683890017653,
    "accnDtRereg": 1683890017653,
    "accnDtSusp": 1683890017653,
    "accnDtReins": 1683890017654,
    "accnDtCreate": 1683890017654,
    "accnUidCreate": "",
    "accnDtLupd": 1683890017654,
    "accnUidLupd": "",
    "accnBusinessAct": "",
    "accnOwnerNationality": "",
    "accnOwnerName": "",
    "cityCode": "",
    "coreLocale": null,
    "TMstAccnType": null
  },
  "tmstCurrency": {
    "otherLangDesc": "",
    "coreMstLocale": null,
    "ccyCode": "IDR",
    "ccyDescription": "",
    "ccyDescriptionOth": "",
    "ccyStatus": "\u0000",
    "ccyDtCreate": 1683890017655,
    "ccyUidCreate": "",
    "ccyDtLupd": 1683890017655,
    "ccyUidLupd": ""
  },
  "tOAddCheck": true,
  "tOWithHoldCheck": true,
  "fFAddCheck": true,
  "fFWithHoldCheck": true
}

  Mock.onPut(/api\/v1\/clickargo\/clictruck\/administrator\/contract\/.*/).reply((config) => {
    return [200, data];
  });

  Mock.onDelete(/api\/v1\/clickargo\/clictruck\/administrator\/contract\/.*/).reply((config) => {
    return [200, data];
  });

  Mock.onGet(/api\/v1\/clickargo\/clictruck\/administrator\/contract\/.*/).reply((config) => {
    return [200, data];
  });

  Mock.onPost("/api/v1/clickargo/clictruck/administrator/contract").reply((config) => {
    return [200, data];
  });