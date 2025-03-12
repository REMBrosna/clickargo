import Mock from "../../../mock";

const data = {
    "iTotalRecords": 3,
    "iTotalDisplayRecords": 3,
    "aaData": [
      {
        "companyId":"ALKNAR",
        "companyName":"PT. Alkanar Indonesia"
      },
      {
        "companyId":"BERAM",
        "companyName":"PT. Berkah Agung Mulia"
      },
      {
        "companyId":"SUKSES",
        "companyName":"PT. Sukses Selalu"
      },
    ]
  }

  Mock.onGet(/api\/v1\/clickargo\/credit\/company\/list\/?.*/).reply((config) => {
    return [200, data];
  });