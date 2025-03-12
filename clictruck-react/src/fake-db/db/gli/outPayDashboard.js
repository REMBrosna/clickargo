import Mock from "../../mock";

const data = [
  {
    id: 0,
    dbType: "APPROVED_JOBS",
    title: "Approved Jobs",
    transStatistic: {
      "Pending Payment": "-",
    },
    accnType: "SP_OP_ADMIN",
    img: null
  },
  {
    id: 1,
    dbType: "JOB_PAYMENTS",
    title: "Job Payments",
    transStatistic: {
      "Active": "-",
      "Paid": "-"
    },
    accnType: "SP_OP_ADMIN",
    img: null
  },
];

Mock.onGet("/api/v1/clickargo/clictruck/dashboard/outboundPayment/fake").reply((config) => {
  return [200, data];
});
