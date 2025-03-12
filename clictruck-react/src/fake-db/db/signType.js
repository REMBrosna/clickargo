import Mock from "../mock";

const data = [
  {
    otherLangDesc: null,
    coreMstLocale: null,
    msigId: "PHO",
    msigName: "PHOTO",
    msigNameOth: null,
    msigDesc: "USER PROFILE PICTURE",
    msigDescOth: null,
    msigStatus: "A",
    msigDtCreate: 1543593600000,
    msigUidCreate: "SYS",
    msigDtLupd: 1543593600000,
    msigUidLupd: "SYS",
  },
  {
    otherLangDesc: null,
    coreMstLocale: null,
    msigId: "PRI",
    msigName: "PRIMARY CONTACT",
    msigNameOth: null,
    msigDesc: "PRIMARY",
    msigDescOth: null,
    msigStatus: "A",
    msigDtCreate: 1543593600000,
    msigUidCreate: "SYS",
    msigDtLupd: 1543593600000,
    msigUidLupd: "SYS",
  },
  {
    otherLangDesc: null,
    coreMstLocale: null,
    msigId: "SEC",
    msigName: "SECONDARY CONTACT",
    msigNameOth: null,
    msigDesc: "SECONDARY",
    msigDescOth: null,
    msigStatus: "A",
    msigDtCreate: 1543593600000,
    msigUidCreate: "SYS",
    msigDtLupd: 1543593600000,
    msigUidLupd: "SYS",
  },
];

Mock.onGet("/api/co/master/entity/signType").reply((config) => {
    return [200, data];
  });