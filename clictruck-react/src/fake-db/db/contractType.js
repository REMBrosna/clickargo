import Mock from "../mock";

const data = [
  {
    otherLangDesc: null,
    coreMstLocale: null,
    ctypId: "CNT_EMAIL",
    ctypDescription: "Email Contact",
    ctypDescriptionOth: null,
    ctypStatus: "A",
    ctypDtCreate: 1543593600000,
    ctypUidCreate: "SYS",
    ctypDtLupd: 1543593600000,
    ctypUidLupd: "SYS",
  },
];

Mock.onGet("/api/co/master/entity/contactType").reply((config) => {
    return [200, data];
  });