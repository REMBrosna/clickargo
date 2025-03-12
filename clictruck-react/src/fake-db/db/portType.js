import Mock from "../mock";

const data = [
  {
    otherLangDesc: null,
    coreMstLocale: null,
    porttCode: "PORTT_AIR",
    porttDescription: "AIR - MODE OF TRANSPORT",
    porttDescriptionOth: "AIR - MODE OF TRANSPORT",
    porttStatus: "A",
    porttDtCreate: 1543939200000,
    porttUidCreate: "SYS",
    porttDtLupd: 1543939200000,
    porttUidLupd: "SYS",
  },
  {
    otherLangDesc: null,
    coreMstLocale: null,
    porttCode: "PORTT_LAND",
    porttDescription: "LAND - MODE OF TRANSPORT",
    porttDescriptionOth: "LAND - MODE OF TRANSPORT",
    porttStatus: "A",
    porttDtCreate: 1543939200000,
    porttUidCreate: "SYS",
    porttDtLupd: 1543939200000,
    porttUidLupd: "SYS",
  },
  {
    otherLangDesc: null,
    coreMstLocale: null,
    porttCode: "PORTT_SEA",
    porttDescription: "SEA - MODE OF TRANSPORT",
    porttDescriptionOth: "SEA - MODE OF TRANSPORT",
    porttStatus: "A",
    porttDtCreate: 1543939200000,
    porttUidCreate: "SYS",
    porttDtLupd: 1543939200000,
    porttUidLupd: "SYS",
  },
];

Mock.onGet("/api/co/master/entity/portType").reply((config) => {
    return [200, data];
  });