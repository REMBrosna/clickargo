import Mock from "../../mock";

const data= {
    "iTotalRecords": 2,
    "iTotalDisplayRecords": 2,
    "aaData":[
    {
        "audtEvent": "Tes event",
        "audtTimestamp": 1680002620265,
        "audtRemarks": "Test remarks",
        "audtUid": "Test Id",
        "audtUname": "Test Uname",
    },
    {
        "audtEvent": "Tes event",
        "audtTimestamp": 1680002620300,
        "audtRemarks": "Test remarks",
        "audtUid": "Test Id",
        "audtUname": "Test Uname",
    },
]

}


Mock.onGet(/api\/v1\/clickargo\/clictruck\/creditlimit\/audit\/list\/?.*/).reply((config) => {
    return [200, data];
  });