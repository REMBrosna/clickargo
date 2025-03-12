import Mock from "../../mock";

const data = {
  "iTotalRecords": 1,
  "iTotalDisplayRecords": 1,
  "aaData": [
    {
        "jobId": "8373987",
        "jobType": "Import",
        "invoiceFrom": "PT, Derian Sejartra",
        "noOfTrips": "1",
        "changes": "Rp 300.000",
        "reimbursement": "Rp 500.000",
        "billingDate": 1664557200000,
        "status": "VERIFIED",
        "verifiedDate": 1664557200000,
        "verifiedBy": "DERIAN",
    }
  ]
}

  Mock.onGet(/api\/v1\/clickargo\/clictruck\/finance\/verification\/list\/?.*/).reply((config) => {
    return [200, data];
  });