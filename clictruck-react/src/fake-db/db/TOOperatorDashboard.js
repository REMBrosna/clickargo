import Mock from "../mock";

const data = [
  {
    id: 0,
    dbType: "TRUCKING_JOBS",
    title: "Trucking Jobs",
    transStatistic: {
      "New Jobs": 8,
      "Ongoing Jobs": 10,
    },
    accnType: "ACC_TYPE_CO",
  },
  {
    id: 1,
    dbType: "BILLED_JOBS",
    title: "Job Billing",
    transStatistic: {
      "Submitted Job": 8,
      "Ready Jobs": 32,
    },
    accnType: "ACC_TYPE_CO",
  },
  // {
  //     "id": 2,
  //     "dbType": "MY_DO",
  //     "title": "My DO",
  //     "transStatistic": {
  //         "ACTIVE": 1
  //     },
  //     "accnType": "ACC_TYPE_CO"
  // },
  // {
  //     "id": 3,
  //     "dbType": "DO_EXT",
  //     "title": "DO Extension",
  //     "transStatistic": {
  //         "ACTIVE": 0
  //     },
  //     "accnType": "ACC_TYPE_CO"
  // }
];

Mock.onGet("/api/v1/clickargo/clicdo/truck-job-dashboard").reply((config) => {
  return [200, data];
});
