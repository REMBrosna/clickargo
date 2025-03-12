import Mock from "../mock";

const data = {
    "iTotalRecords": 2,
    "iTotalDisplayRecords": 2,
    "aaData": [
    {
       "id": 1,
        "jobId": "CKJT12",
        "jobType": "IMPORT",
        "invoiceFromAccn": {
            "accnName": "PT BERKAH"
        },
        "noOfTrips": 1,
        "charges": 50003145,
        "reimbursement": 5000314,
        "billingDate": 1545321600000,
        "paymentDueDate": 1545321600000,
        "status": 'APPROVED',
        "approvedDate": 1545321600000,
        "approvedBy": "JOHN",
        "paymentState": "NEW",
    },
    {
        "id": 2,
        "jobId": "CKJST12",
        "jobType": "IMPORT",
        "invoiceFromAccn": {
            "accnName": "PT BERKAH"
        },
        "noOfTrips": 1,
        "charges": 50003145,
        "reimbursement": 5000314,
        "billingDate": 1545321600000,
        "paymentDueDate": 1545321600000,
        "status": 'APPROVED',
        "approvedDate": 1545321600000,
        "approvedBy": "JOHN",
        "paymentState": "NEW",
    }
]}
// /api\/v1\/clickargo\/clictruck\/administrator\/location\/list\/?.*/
Mock.onGet(/api\/v1\/clickargo\/clictruck\/gli\/dashboard\/approved\/list\/?.*/).reply((config) => {
    return [200, data];
  });


  Mock.onPost("/api/testing").reply((config) => {
    
    return [200, "OKEE"];
  });