import Mock from "../mock";

const data = [
  {
    id: 0,
    dbType: "APPROVED",
    title: "Approved Jobs",
    transStatistic: {
      "Pending Payment": 5,
    },
    accnType: "ACC_TYPE_TO",
    img: 'DO_CLAIM.png'
  },
  {
    id: 1,
    dbType: "PAYMENTS",
    title: "Job Payments",
    transStatistic: {
      "Active": 1,
      "Paid": 10
    },
    accnType: "ACC_TYPE_TO",
    img: 'DO_CLAIM.png'
  },
];

Mock.onGet("/api/v1/clickargo/gli/dashboard").reply((config) => {
  return [200, data];
});
