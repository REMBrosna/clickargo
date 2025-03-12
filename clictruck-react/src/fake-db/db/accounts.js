import Mock from "../mock";

export const FORWARDER_ACCOUNTS = [
    {
        account: "YUSEN",
        accnId: "YUSEN",
        accnName: "Yusen Logistics",
        isAuthorized: true
    },
    {
        account: "PTRINITY",
        accnId: "PTRINITY",
        accnName: "PT. Trinity Omega PerkasaTay",
        isAuthorized: true
    },
    {
        account: "PTERA",
        accnId: "PTERA",
        accnName: "PT. Tera Forwarders",
        isAuthorized: true
    },
    {
        account: "PPBOY",
        accnId: "PPBOY",
        accnName: "Paperboy Logistics",
        isAuthorized: true
    },
    {
        account: "UNAUTH01",
        accnId: "UNAUTH01",
        accnName: "UnAuthorized Freight Logistics",
        isAuthorized: false
    },
    {
        account: "UNAUTH02",
        accnId: "UNAUTH02",
        accnName: "UnAuthorized AB Freigh Logistics",
        isAuthorized: false
    },
    {
        account: "PRIMAINT",
        accnId: "PRIMAINT",
        accnName: "PT Prima International Cargo",
        isAuthorized: true
    },
];


Mock.onGet(/\/api\/accounts\/authorised\/\w+/).reply((config) => {
    const isAuth = config.url.split("/")[4] === 'y' ? true : false;
    const response = FORWARDER_ACCOUNTS.filter((job) => job.isAuthorized === isAuth);
    return [200, response];
});

Mock.onGet(/\/api\/accounts\/authorised/).reply((config) => {
    const response = FORWARDER_ACCOUNTS.filter((accn) => accn.isAuthorized === true);
    return [200, response];
});

Mock.onGet(/clictruck\/api\/co\/ccm\/entity\/accn\/?.*/).reply((config) => {
    return [200, FORWARDER_ACCOUNTS];
  });