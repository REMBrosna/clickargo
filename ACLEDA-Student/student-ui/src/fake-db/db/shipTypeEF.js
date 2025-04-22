import Mock from "../mock";

export const shipTypeEFDB = {
  list: [
    {
        "otherLangDesc":null,
        "coreMstLocale":null,
        "shipType":"CARGO",
        "desc":"NA",  
        "shipTypeOthLang":"NA",
        "fieldName":"NA",
        "fieldNameOth":"NA",
        "fieldInputType":"NA",
        "mandatory":'Y',
        "shipTypeDtCreate":"2020/11/30",
        "shiptypeUidCreate":"TRAD1",
        "ctyDtLupd":"2020/11/30",
        "ctyUidLupd":"SYS"
     },

     {
      "otherLangDesc":null,
      "coreMstLocale":null,
      "shipType":"CONTAINER",
      "desc":"NA",  
      "shipTypeOthLang":"NA",
      "fieldName":"NA",
      "fieldNameOth":"NA",
      "fieldInputType":"NA",
      "mandatory":'Y',
      "shipTypeDtCreate":"2020/11/30",
      "shiptypeUidCreate":"DEV1",
      "ctyDtLupd":"2020/11/30",
      "ctyUidLupd":"SYS"
   },

   {
    "otherLangDesc":null,
    "coreMstLocale":null,
    "shipType":"PASSENGER",
    "desc":"NA",  
    "shipTypeOthLang":"NA",
    "fieldName":"NA",
    "fieldNameOth":"NA",
    "fieldInputType":"NA",
    "mandatory":'Y',
    "shipTypeDtCreate":"2020/11/30",
    "shiptypeUidCreate":"TRAD2",
    "ctyDtLupd":"2020/11/30",
    "ctyUidLupd":"SYS"
 },

 {
  "otherLangDesc":null,
  "coreMstLocale":null,
  "shipType":"TANKER",
  "desc":"NA",  
  "shipTypeOthLang":"NA",
  "fieldName":"NA",
  "fieldNameOth":"NA",
  "fieldInputType":"NA",
  "mandatory":'Y',
  "shipTypeDtCreate":"2020/11/30",
  "shiptypeUidCreate":"DEV2",
  "ctyDtLupd":"2020/11/30",
  "ctyUidLupd":"SYS"
},

    ],
};

Mock.onGet("/api/docRepo/all").reply((config) => {
  return [200, shipTypeEFDB.list];
});

Mock.onGet("/api/country").reply((config) => {
  const id = config.data;
  const response = shipTypeEFDB.list.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/docRepo/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  shipTypeEFDB.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, shipTypeEFDB.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, shipTypeEFDB.list];
});

Mock.onPost("/api/docRepo/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  shipTypeEFDB.list.forEach((element) => {
    if (element.id === user.id) {
      shipTypeEFDB.list[index.i] = user;
      return [200, shipTypeEFDB.list];
    }
    index.i++;
  });
  return [200, shipTypeEFDB.list];
});

Mock.onPost("/api/docRepo/add").reply((config) => {
  let user = JSON.parse(config.data);
  shipTypeEFDB.list.push(user);
  return [200, shipTypeEFDB.list];
});