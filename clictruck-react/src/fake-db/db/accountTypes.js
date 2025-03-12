import Mock from "../mock";

export const accountTypeList = [
    {
        otherLangDesc: null,
        coreMstLocale: null,
        atypId: "ACC_TYPE_ADMIN",
        atypDescription: "ADMIN",
        atypDescriptionOth: null,
        atypStatus: "A",
        atypDtCreate: 1543593600000,
        atypUidCreate: "SYS",
        atypDtLupd: 1543593600000,
        atypUidLupd: "SYS"
    },
    {
        otherLangDesc: null,
        coreMstLocale: null,
        atypId: "ACC_TYPE_CARGO_OWNER",
        atypDescription: "CARGO OWNER",
        atypDescriptionOth: null,
        atypStatus: "A",
        atypDtCreate: 1586235670000,
        atypUidCreate: "SYS",
        atypDtLupd: 1586235670000,
        atypUidLupd: "SYS"
    },
    {
        otherLangDesc: null,
        coreMstLocale: null,
        atypId: "ACC_TYPE_FREIGHT_FORWARDER",
        atypDescription: "FREIGHT FORWARDER",
        atypDescriptionOth: null,
        atypStatus: "A",
        atypDtCreate: 1586235670000,
        atypUidCreate: "SYS",
        atypDtLupd: 1586235670000,
        atypUidLupd: "SYS"
    },
    {
        otherLangDesc: null,
        coreMstLocale: null,
        atypId: "ACC_TYPE_PORT",
        atypDescription: "PORT USER",
        atypDescriptionOth: null,
        atypStatus: "A",
        atypDtCreate: 1543593600000,
        atypUidCreate: "SYS",
        atypDtLupd: 1543593600000,
        atypUidLupd: "SYS"
    },
    {
        otherLangDesc: null,
        coreMstLocale: null,
        atypId: "ACC_TYPE_SHIP_LINE",
        atypDescription: "SHIPPING LINE",
        atypDescriptionOth: null,
        atypStatus: "A",
        atypDtCreate: 1586235670000,
        atypUidCreate: "SYS",
        atypDtLupd: 1586235670000,
        atypUidLupd: "SYS"
    },
]

Mock.onGet("/api/accountTypes/all").reply((config) => {
    return [200, accountTypeList];
});