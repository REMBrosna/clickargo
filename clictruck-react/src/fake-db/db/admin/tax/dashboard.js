import Mock from "../../../mock";

const data = [
  {
    id: 0,
    dbType: "SEQUENCE",
    title: "Tax Sequence",
    transStatistic: {
      "Used": 5,
      "Total": 100
    },
    accnType: "ACC_TYPE_GLI",
    img: 'DO_CLAIM.png'
  },
  {
    id: 1,
    dbType: "REPORTS",
    title: "Tax Report",
    transStatistic: {
      "Ready Report": 1
    },
    accnType: "ACC_TYPE_GLI",
    img: 'DO_CLAIM.png'
  },
  {
    id: 2,
    dbType: "INVOICES",
    title: "Tax Invoices",
    transStatistic: {
      "Without PDF": 1
    },
    accnType: "ACC_TYPE_GLI",
    img: 'DO_CLAIM.png'
  },
];

Mock.onGet("/api/v1/clickargo/tax/dashboard").reply((config) => {
  return [200, data];
});
