import Mock from "../mock";

const data = [
  {
    otherLangDesc: null,
    coreMstLocale: null,
    ccyCode: "IDR",
    ccyDescription: "INDONESIAN RUPIAH",
    ccyDescriptionOth: null,
    ccyStatus: "A",
    ccyDtCreate: 1670774400000,
    ccyUidCreate: "SYS",
    ccyDtLupd: 1670774400000,
    ccyUidLupd: "SYS",
  },
  {
    otherLangDesc: null,
    coreMstLocale: null,
    ccyCode: "SGD",
    ccyDescription: "SINGAPORE DOLLAR",
    ccyDescriptionOth: null,
    ccyStatus: "A",
    ccyDtCreate: 1543593600000,
    ccyUidCreate: "SYS",
    ccyDtLupd: 1543593600000,
    ccyUidLupd: "SYS",
  },
  {
    otherLangDesc: null,
    coreMstLocale: null,
    ccyCode: "USD",
    ccyDescription: "US DOLLAR",
    ccyDescriptionOth: null,
    ccyStatus: "A",
    ccyDtCreate: 1628730083000,
    ccyUidCreate: "SYS",
    ccyDtLupd: 1628730083000,
    ccyUidLupd: "SYS",
  },
];

Mock.onGet("/api/co/master/entity/currency").reply((config) => {
    return [200, data];
  });

  Mock.onGet(/api\/co\/master\/entity\/currency\/?.*/).reply((config) => {
    return [200, data];
  });