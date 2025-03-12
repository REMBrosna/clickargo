import Mock from "../mock";

const data = [
  {
    otherLangDesc: null,
    coreMstLocale: null,
    adtCode: "KHMER",
    adtDesc: "KHMER ADDRESS",
    adtDescOth: "",
    adtStatus: "A",
    adtDtCreate: 1618369357000,
    adtUidCreate: "SYS",
    adtDtLupd: null,
    adtUidLupd: null,
  },
  {
    otherLangDesc: null,
    coreMstLocale: null,
    adtCode: "PRIMARY",
    adtDesc: "ENGLISH ADDRESS",
    adtDescOth: "",
    adtStatus: "A",
    adtDtCreate: 1618369357000,
    adtUidCreate: "SYS",
    adtDtLupd: null,
    adtUidLupd: null,
  },
];

Mock.onGet("/api/co/master/entity/addrType").reply((config) => {
    return [200, data];
  });