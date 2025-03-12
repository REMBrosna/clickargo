import Mock from "../mock";

const data = [
  {
    otherLangDesc: null,
    coreMstLocale: null,
    hstypeCode: "001",
    hstypeDescription: "GENERAL GOODS",
    hstypeDescriptionOth: "GENERAL GOODS",
    hstypeStatus: "A",
    hstypeDtCreate: 1545321600000,
    hstypeUidCreate: "SYS",
    hstypeDtLupd: 1545321600000,
    hstypeUidLupd: "SYS",
  },
  {
    otherLangDesc: null,
    coreMstLocale: null,
    hstypeCode: "002",
    hstypeDescription: "VEHICLE",
    hstypeDescriptionOth: "GENERAL GOODS",
    hstypeStatus: "A",
    hstypeDtCreate: 1545321600000,
    hstypeUidCreate: "SYS",
    hstypeDtLupd: 1545321600000,
    hstypeUidLupd: "SYS",
  },
];

Mock.onGet("/api/co/master/entity/hsCodeType").reply((config) => {
    return [200, data];
  });