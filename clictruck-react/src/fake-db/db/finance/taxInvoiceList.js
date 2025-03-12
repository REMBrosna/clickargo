import Mock from "../../mock";

const data= {
    "iTotalRecords": 2,
    "iTotalDisplayRecords": 2,
    "aaData":[
    {
        "id": "1",
        "service": "CLICTRUCK",
        "invoiceNo": "CT-PF-283738378",
        "invoiceIssueDate": 1664557200000,
        "taxNo": "0109377833877",
        "customer": "PT. DERIAN PRATAMA",
        "status": "EXPORTED",
    },
    {
        "id": "2",
        "service": "CLICTRUCK",
        "invoiceNo": "CT-PF-097383738",
        "invoiceIssueDate": 1664557200000,
        "taxNo": "010876383638",
        "customer": "PT. DERIAN PRATAMA",
        "status": "EXPORTED",
    },
]

}

Mock.onGet(/api\/v1\/clickargo\/clictruck\/gli\/finance\/tax-management\/tax-invoices\/list\/?.*/).reply((config) => {
    return [200, data];
  });