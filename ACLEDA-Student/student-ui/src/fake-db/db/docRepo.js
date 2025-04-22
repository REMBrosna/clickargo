import Mock from "../mock";

export const docRepoDB = {
  list: [
    {
        "otherLangDesc":null,
        "coreMstLocale":null,
        "accnID":"MAESRK",
        "vesselID":"SEA STAR",
        "docType":"TRADER_REG_DOC",
        "shippingLine":"Shipping Line 1",
        "docRefNo":"Ref 1",
        "uploadedDate":"2020-12-30T12:30:45",
        "docExpDate":"2021/10/25",
        "uploadedBy":"TRAD1",
        "ctyDtCreate":"2020/11/30",
        "ctyUidCreate":"SYS",
        "ctyDtLupd":"2020/11/30",
        "ctyUidLupd":"SYS"
     },

     {
      "otherLangDesc":null,
      "coreMstLocale":null,
      "accnID":"MAESRK",
      "vesselID":"BLUEMOON",
      "docType":"VESSEL_CERT",
      "shippingLine":"Shipping Line 2",
      "docRefNo":"Ref 2",
      "uploadedDate":"2021-10-01T12:30:45",
      "docExpDate":"2021/10/25",
      "uploadedBy":"DEV1",
      "ctyDtCreate":"2020/11/30",
      "ctyUidCreate":"SYS",
      "ctyDtLupd":"2020/11/30",
      "ctyUidLupd":"SYS"
   },

   {
    "otherLangDesc":null,
    "coreMstLocale":null,
    "accnID":"CAM_SHIPS",
    "vesselID":"VOYAGER 1",
    "docType":"SHIP_REG",
    "shippingLine":"Shipping Line 2",
    "docRefNo":"Ref 3",
    "uploadedDate":"2021-02-21T12:30:45",
    "docExpDate":"2021/10/25",
    "uploadedBy":"TRAD2",
    "ctyDtCreate":"2020/11/30",
    "ctyUidCreate":"SYS",
    "ctyDtLupd":"2020/11/30",
    "ctyUidLupd":"SYS"
 },

 {
  "otherLangDesc":null,
  "coreMstLocale":null,
  "accnID":"CAM_SHIPS",
  "vesselID":"VOYAGER 1",
  "docType":"SHIP_MANIFEST",
  "shippingLine":"Shipping Line 1",
  "docRefNo":"Ref 1",
  "uploadedDate":"2021-02-12T12:30:45",
  "docExpDate":"2021/10/25",
  "uploadedBy":"DEV2",
  "ctyDtCreate":"2020/11/30",
  "ctyUidCreate":"SYS",
  "ctyDtLupd":"2020/11/30",
  "ctyUidLupd":"SYS"
},

    ],
};

Mock.onGet("/api/docRepo/all").reply((config) => {
  return [200, docRepoDB.list];
});

Mock.onGet("/api/country").reply((config) => {
  const id = config.data;
  const response = docRepoDB.list.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/docRepo/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  docRepoDB.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, docRepoDB.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, docRepoDB.list];
});

Mock.onPost("/api/docRepo/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  docRepoDB.list.forEach((element) => {
    if (element.id === user.id) {
        docRepoDB.list[index.i] = user;
      return [200, docRepoDB.list];
    }
    index.i++;
  });
  return [200, docRepoDB.list];
});

Mock.onPost("/api/docRepo/add").reply((config) => {
  let user = JSON.parse(config.data);
  docRepoDB.list.push(user);
  return [200, docRepoDB.list];
});