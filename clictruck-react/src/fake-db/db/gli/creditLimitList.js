import Mock from "../../mock";

const data= {
    "iTotalRecords": 2,
    "iTotalDisplayRecords": 2,
    "aaData":[
    {
        "creditId": "CR0001",
        "accnCode": "ALKNAR",
        "acnnName": "PT.Alkindo Pratama",
        "serviceType": "CLICTRUCK",
        "creditLimit": 50000000,
        "creditCcy": "IDR",
        "newCreditLimit": 80000000,
        "status": "SUBMIT",
        "dateCreated": 1680002620265,
        "dateSubmitted": 1680012620265,
        "createdBy": "John",
        "submittedBy": "John"
    },
    {
        "creditId": "CR0002",
        "accnCode": "ALKNAR",
        "acnnName": "PT.Alkindo Pratama",
        "serviceType": "CLICTRUCK",
        "creditLimit": 50000000,
        "creditCcy": "IDR",
        "newCreditLimit": 90000000,
        "status": "NEW",
        "dateCreated": 1680002620300,
        "dateSubmitted": null,
        "createdBy": "John",
        "submittedBy": "John"
    },
]

}

Mock.onGet(/api\/v1\/clickargo\/clictruck\/creditlimit\/list\/?.*/).reply((config) => {
    return [200, data];
  });