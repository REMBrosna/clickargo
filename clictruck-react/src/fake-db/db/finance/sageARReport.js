import Mock from "../../mock";

const data= {
    "iTotalRecords": 2,
    "iTotalDisplayRecords": 2,
    "aaData":[
    {
        "id": 1,
        "batch": 1,
        "reportName": "TAXR_20230706.xls",
        "records": 60,
        "startPeriod": 1664557200000,
        "endPeriod": 1664557200000,
        "status": "ACTIVE"
    },
    {
        "id":2,
        "batch": 2,
        "reportName": "TAXR_20230706part2.xls",
        "records": 40,
        "startPeriod": 1664557200000,
        "endPeriod": 1664557200000,
        "status": "ACTIVE"
    }
]

}

Mock.onGet(/api\/v1\/clickargo\/clictruck\/gli\/finance\/ar-report\/list\/?.*/).reply((config) => {
    return [200, data];
  });