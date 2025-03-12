import Mock from "../mock";

const data = {
    "iTotalRecords": 2,
    "iTotalDisplayRecords": 2,
    "aaData": [
    {
        "id": 1,
        "paymentId": "CKJT12",
        "billingDate": 1545321600000,
        "amount": 50003145,
        "currency": "IDR",
        "paymentDate": 1545321600000,
        "paidDate": 1545321600000,
        "status": 'NEW',
    },
    {
        "id": 2,
        "paymentId": "CKJT126372",
        "billingDate": 1545321600000,
        "paymentDate": 1545321600000,
        "amount": 50003145,
        "currency": "IDR",
        "paidDate": 1545321600000,
        "status": 'NEW',
    }
]}
// /api\/v1\/clickargo\/clictruck\/administrator\/location\/list\/?.*/
Mock.onGet(/api\/v1\/clickargo\/clictruck\/gli\/dashboard\/jobpayment\/list\/?.*/).reply((config) => {
    return [200, data];
  });