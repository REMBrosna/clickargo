import Mock from "../../mock";

const data= {
    "iTotalRecords": 2,
    "iTotalDisplayRecords": 2,
    "aaData":[
    {
        "id": 1688525628314,
        "prefixLeft": 10,
        "prefixMid": 7,
        "prefixYear": 2023,
        "sequenceStart": 899000000,
        "sequenceEnd": 899020000,
        "consumedNum": 62,
        "status": "ACTIVE",
        "dateCreated": 1664557200000,
        "paymentState": "BILL",
    },
    { 
        "id": 1688525807020,
        "prefixLeft": 10,
        "prefixMid": 8,
        "prefixYear": 2023,
        "sequenceStart": 899500000,
        "sequenceEnd": 899520000,
        "consumedNum": 62,
        "status": "ACTIVE",
        "dateCreated": 1664557200000,
        "paymentState": "BILL",
    }
]

}

Mock.onGet(/api\/v1\/clickargo\/clictruck\/gli\/finance\/tax-management\/tax-sequence\/list\/?.*/).reply((config) => {
    return [200, data];
  });