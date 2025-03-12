import Mock from "../../mock";

const data = {
  "otherLangDesc": null,
  "coreMstLocale": null,
  "crsId": "1686658836819",
  "crsAmt": 500000.0,
  "crsReserve": 0.0,
  "crsUtilized": 0.0,
  "crsBalance": 500000.0,
  "crsStatus": "A",
  "crsDtCreate": 1686658837000,
  "crsUidCreate": "ALKNAR_A001",
  "crsDtLupd": 1686658837000,
  "crsUidLupd": "ALKNAR_A001",
  "tcoreAccn": {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "accnId": "ALKNAR",
      "accnStatus": "A",
      "accnName": "Alkindo Naratama",
      "accnNameOth": "Alkindo Naratama",
      "accnCoyRegn": "21",
      "accnPassNid": null,
      "accnNationality": "Indonesia",
      "accnVatFlag": null,
      "accnVatNo": null,
      "accnAddr": null,
      "accnContact": null,
      "accnDtAgree": null,
      "accnDtReg": 1664557200000,
      "accnDtDereg": null,
      "accnDtRereg": null,
      "accnDtSusp": null,
      "accnDtReins": null,
      "accnDtCreate": 1664557200000,
      "accnUidCreate": "SYS",
      "accnDtLupd": 1664557200000,
      "accnUidLupd": "SYS",
      "accnBusinessAct": null,
      "accnOwnerNationality": null,
      "accnOwnerName": null,
      "cityCode": "23",
      "coreLocale": null,
      "TMstAccnType": null
  },
  "tmstCurrency": {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "ccyCode": "IDR",
      "ccyDescription": "INDONESIAN RUPIAH",
      "ccyDescriptionOth": null,
      "ccyStatus": "A",
      "ccyDtCreate": 1670778000000,
      "ccyUidCreate": "SYS",
      "ccyDtLupd": 1670778000000,
      "ccyUidLupd": "SYS"
  },
  "tckMstServiceType": {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "svctId": "CLICTRUCK",
      "svctName": "CLICTRUCK",
      "svctDesc": "CLICTRUCK",
      "svctDescOth": "CLICTRUCK",
      "svctStatus": "A",
      "svctDtCreate": 1601485200000,
      "svctUidCreate": "SYS",
      "svctDtLupd": 1601485200000,
      "svctUidLupd": "SYS"
  }
};

Mock.onPost("/api/v1/clickargo/credit/getCreditBalance").reply((config) => {
  return [200, data];
});

const credit = {
  "otherLangDesc": null,
  "coreMstLocale": null,
  "crId": "1686658795924",
  "crAmt": 500000.0,
  "crDtStart": 1601485200000,
  "crDtEnd": 1601485200000,
  "crUsrVerify": "ALKNAR_A001",
  "crDtVerify": 1686658812000,
  "crUsrApprove": "ALKNAR_A001",
  "crDtApprove": 1686658837000,
  "crRemarks": "Sample Remark Approve.",
  "crStatus": "A",
  "crDtCreate": 1686658796000,
  "crUidCreate": "ALKNAR_A001",
  "crDtLupd": 1686658837000,
  "crUidLupd": "ALKNAR_A001",
  "tcoreAccn": {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "accnId": "ALKNAR",
      "accnStatus": "A",
      "accnName": "Alkindo Naratama",
      "accnNameOth": "Alkindo Naratama",
      "accnCoyRegn": "21",
      "accnPassNid": null,
      "accnNationality": "Indonesia",
      "accnVatFlag": null,
      "accnVatNo": null,
      "accnAddr": null,
      "accnContact": null,
      "accnDtAgree": null,
      "accnDtReg": 1664557200000,
      "accnDtDereg": null,
      "accnDtRereg": null,
      "accnDtSusp": null,
      "accnDtReins": null,
      "accnDtCreate": 1664557200000,
      "accnUidCreate": "SYS",
      "accnDtLupd": 1664557200000,
      "accnUidLupd": "SYS",
      "accnBusinessAct": null,
      "accnOwnerNationality": null,
      "accnOwnerName": null,
      "cityCode": "23",
      "coreLocale": null,
      "TMstAccnType": null
  },
  "tckMstCreditState": null,
  "tmstCurrency": {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "ccyCode": "IDR",
      "ccyDescription": "INDONESIAN RUPIAH",
      "ccyDescriptionOth": null,
      "ccyStatus": "A",
      "ccyDtCreate": 1670778000000,
      "ccyUidCreate": "SYS",
      "ccyDtLupd": 1670778000000,
      "ccyUidLupd": "SYS"
  },
  "tckMstServiceType": {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "svctId": "CLICTRUCK",
      "svctName": "CLICTRUCK",
      "svctDesc": "CLICTRUCK",
      "svctDescOth": "CLICTRUCK",
      "svctStatus": "A",
      "svctDtCreate": 1601485200000,
      "svctUidCreate": "SYS",
      "svctDtLupd": 1601485200000,
      "svctUidLupd": "SYS"
  }
};

Mock.onPost("/api/v1/clickargo/credit/getCredit").reply((config) => {
    return [200, credit];
  });


