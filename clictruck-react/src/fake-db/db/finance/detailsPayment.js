import Mock from "../../mock";

const data = {
  "iTotalRecords": 1,
  "iTotalDisplayRecords": 1,
  "aaData": [
    {
        "no": 1,
        "itemDesc": "Platform fee for Job CKTJ202304101215",
        "qty": 1,
        "currency": "IDR",
        "amount": 30000,
    },
    {
        "no": 2,
        "itemDesc": "Debit Note for Job CKTJ202304101215",
        "qty": 1,
        "currency": "IDR",
        "amount": 512450000,
    }
  ]
}

  Mock.onGet(/api\/v1\/clickargo\/clictruck\/finance\/details\/history\/?.*/).reply((config) => {
    return [200, data];
  });