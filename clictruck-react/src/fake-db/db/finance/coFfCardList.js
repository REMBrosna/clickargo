import Mock from "../../mock";

const data = [
  {
    id: 0,
    dbType: "APPROVALS",
    title: "Invoice Approvals",
    transStatistic: {
      "Pending Invoices": 10,
    },
    accnType: "ACC_TYPE_FINANCE",
    img: 'JOB_BILLING.png'
  },
  {
    id: 1,
    dbType: "DN_PAYMENTS",
    title: "Debit Note Payments",
    transStatistic: {
      "Unpaid DN": 8,
    },
    accnType: "ACC_TYPE_FINANCE",
    img: 'JOB_BILLING.png'
  },
  {
    id: 2,
    dbType: "PF_PAYMENTS",
    title: "Platform Fee Payments",
    transStatistic: {
      "Unpaid Invoices": 1,
    },
    accnType: "ACC_TYPE_FINANCE",
    img: 'JOB_BILLING.png'
  },
];

Mock.onGet("/api/v1/clickargo/coff/finance/dashboard/card").reply((config) => {
  return [200, data];
});
