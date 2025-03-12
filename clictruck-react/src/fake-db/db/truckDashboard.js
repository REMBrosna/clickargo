import Mock from "../mock";

const data = [
  {
    id: 0,
    dbType: "BL",
    title: "Trucking Jobs",
    transStatistic: {
      "Active Jobs": 10,
      "Ongoing Jobs": 30,
    },
    accnType: "ACC_TYPE_CO",
  },
  {
    id: 1,
    dbType: "DO_CLAIM",
    title: "Approve Jobs",
    transStatistic: {
      "Submitted Jobs": 8,
      "Ongoing Jobs": 30,
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

Mock.onGet("/api/v1/clickargo/clicdo/dashboard").reply((config) => {
  return [200, data];
});
