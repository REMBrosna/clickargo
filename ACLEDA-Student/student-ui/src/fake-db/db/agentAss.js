import Mock from "../mock";

export const agentAssDB = {
  list: [
    
    {
      "otherLangDesc":null,
      "coreMstLocale":null,
      "id":"IMO123",
      "agentName":"Heng Roth",
      "agentTIN":"R235533566",
      "agentAddr":"Wat Nom",
      "agentCty":"PH",
      "agentPh":"8991919191",
      "agentEmail":"test@gmail.com",
      "portAss":"PHN",
      "assType":"ALL",
      "agentAssDtCreate":"2020/12/01",
      "ctyUidCreate":"TRAD1",
      "ctyDtLupd":"2020/12/01",
      "ctyUidLupd":"SYS"
   },

   {
    "otherLangDesc":null,
    "coreMstLocale":null,
    "id":"IMO345",
    "agentName":"John Cena",
    "agentTIN":"L2435345344",
    "agentAddr":"Wat Phnom",
    "agentCty":"PH",
    "agentPh":"8991919191",
    "agentEmail":"tester@gmail.com",
    "portAss":"SHV",
    "assType":"PARTIAL",
    "agentAssDtCreate":"2020/12/01",
    "ctyUidCreate":"DEV1",
    "ctyDtLupd":"2020/12/01",
    "ctyUidLupd":"SYS"
 },

 {
  "otherLangDesc":null,
  "coreMstLocale":null,
  "id":"IMO988",
  "agentName":"Ben Clarke",
  "agentTIN":"R2345432343",
  "agentAddr":"Vattanac City",
  "agentCty":"PH",
  "agentPh":"8991919191",
  "agentEmail":"testing@gmail.com",
  "portAss":"PHN",
  "assType":"ALL",
  "agentAssDtCreate":"2020/12/01",
  "ctyUidCreate":"TRAD2",
  "ctyDtLupd":"2020/12/01",
  "ctyUidLupd":"SYS"
},

{
  "otherLangDesc":null,
  "coreMstLocale":null,
  "id":"IMO988",
  "agentName":"John Doe",
  "agentTIN":"L2354353456",
  "agentAddr":"City Lights",
  "agentCty":"PH",
  "agentPh":"8991919191",
  "agentEmail":"tester2@gmail.com",
  "portAss":"PHN",
  "assType":"ALL",
  "agentAssDtCreate":"2020/12/01",
  "ctyUidCreate":"TRAD2",
  "ctyDtLupd":"2020/12/01",
  "ctyUidLupd":"SYS"
},
    

    ],
};

Mock.onGet("/api/docRepo/all").reply((config) => {
  return [200, agentAssDB.list];
});

Mock.onGet("/api/country").reply((config) => {
  const id = config.data;
  const response = agentAssDB.list.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/docRepo/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  agentAssDB.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, agentAssDB.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, agentAssDB.list];
});

Mock.onPost("/api/docRepo/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  agentAssDB.list.forEach((element) => {
    if (element.id === user.id) {
      agentAssDB.list[index.i] = user;
      return [200, agentAssDB.list];
    }
    index.i++;
  });
  return [200, agentAssDB.list];
});

Mock.onPost("/api/docRepo/add").reply((config) => {
  let user = JSON.parse(config.data);
  agentAssDB.list.push(user);
  return [200, agentAssDB.list];
});