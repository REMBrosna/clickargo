import Mock from "../mock";

const data = [
  {
    otherLangDesc: null,
    coreMstLocale: null,
    bankId: "DBS",
    bankStatus: "A",
    bankDescription: "DBS Bank",
    bankDescriptionOth: null,
    bankAddr1: "Marina Bay Financial Centre",
    bankAddr2: "Tower 3 ",
    bankAddr3: "Marina Bay",
    bankPcode: "018987",
    bankCity: "Singapore",
    bankProv: "",
    bankCtycode: null,
    bankTel: "6566888888",
    bankFax: "",
    bankEmail: "support@dbs.com.sg",
    bankDtCreate: 1543593600000,
    bankUidCreate: "SYS",
    bankDtLupd: 1543593600000,
    bankUidLupd: "SYS",
    bankIsOnline: "N",
    bankLogoFileData: null,
    logo: null,
    logoByteStr: null,
  },
];

Mock.onGet("/api/co/master/entity/bank").reply((config) => {
    return [200, data];
  });