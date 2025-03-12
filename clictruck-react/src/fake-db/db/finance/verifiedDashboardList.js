import Mock from "../../mock";

const data = [
  {
    id: 0,
    dbType: "VERIVIED",
    title: "Verified Jobs",
    transStatistic: {
      "Pending Approval": 10,
    },
    accnType: "ACC_TYPE_FINANCE",
    img: 'DO_CLAIM.png'
  },
  {
    id: 1,
    dbType: "APPROVED",
    title: "Approved Jobs",
    transStatistic: {
      "Pending Payment": 8,
    },
    accnType: "ACC_TYPE_FINANCE",
    img: 'DO_CLAIM.png'
  },
  {
    id: 2,
    dbType: "PAYMENTS",
    title: "Job Payments",
    transStatistic: {
      "Active": 1,
      "Paid": 10
    },
    accnType: "ACC_TYPE_FINANCE",
    img: 'DO_CLAIM.png'
  },
];

Mock.onGet("/api/v1/clickargo/finanace/verifications/dashboard").reply((config) => {
  return [200, data];
});
