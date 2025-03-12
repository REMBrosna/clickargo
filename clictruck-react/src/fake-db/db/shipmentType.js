import Mock from "../mock";

const data = [
  {
    otherLangDesc: null,
    coreMstLocale: null,
    shtId: "EXPORT",
    shtName: "EXPORT",
    shtDesc: "EXPORT",
    shtDescOth: "EXPORT",
    shtStatus: "A",
    shtDtCreate: 1601481600000,
    shtUidCreate: "SYS",
    shtDtLupd: 1601481600000,
    shtUidLupd: "SYS",
  },
  {
    otherLangDesc: null,
    coreMstLocale: null,
    shtId: "IMPORT",
    shtName: "IMPORT",
    shtDesc: "IMPORT",
    shtDescOth: "IMPORT",
    shtStatus: "A",
    shtDtCreate: 1601481600000,
    shtUidCreate: "SYS",
    shtDtLupd: 1601481600000,
    shtUidLupd: "SYS",
  },
  {
    otherLangDesc: null,
    coreMstLocale: null,
    shtId: "DOMESTIC",
    shtName: "DOMESTIC",
    shtDesc: "DOMESTIC",
    shtDescOth: "DOMESTIC",
    shtStatus: "A",
    shtDtCreate: 1601481600000,
    shtUidCreate: "SYS",
    shtDtLupd: 1601481600000,
    shtUidLupd: "SYS",
  },
];

Mock.onGet("/api/v1/clickargo/master/ckMstShipmentType").reply((config) => {
  return [200, data];
});
