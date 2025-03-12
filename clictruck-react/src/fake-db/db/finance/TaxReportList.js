import Mock from "../../mock";

const data= {
    "iTotalRecords": 2,
    "iTotalDisplayRecords": 2,
    "aaData":[
    {
        "reportId": "CKTRJ1688525628314",
        "reportName": "TAXR_20230706.xls",
        "noRecords": 135,
        "dateCreated": 1664557200000,
        "dateUpdated": 1664557200000,
        "status": "ACTIVE",
    },
    { 
        "reportId": "CKTRJ1688527035978",
        "reportName": "TAXR_20230706part2.xls",
        "noRecords": 200,
        "dateCreated": 1664557200000,
        "dateUpdated": 1664557200000,
        "status": "ACTIVE",
    }
]

}

Mock.onGet(/api\/v1\/clickargo\/clictruck\/gli\/finance\/tax-management\/tax-report\/list\/?.*/).reply((config) => {
    return [200, data];
  });