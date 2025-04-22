import Mock from "../mock";

export const commonApplicationDB = {
  list: [
    {
      appId: "SSCEC20201220123",
      version: "1",
      submitDate: "20-Nov-20",
      status: "S",
      
      appType:"SSCEC",
      port: "KHPNH",
      voyageNo: "ARV1234",
      shipName: "Maersk Alabama",
      expDate:"02-Dec-2021",
     
      imoNo:"IM0123",
      callSign:"c1Sign",
      cty:"SG",
      shippingLineTIN:"SL448",
      shippingLineName:"Moris Port",
      shippingLineAddress:"Line 1",
      portApplied:"Mories",

      docType:"AD Cert",
      docRefNo:"Ref123",

      securityLevel:"Level 1",

      ctyUidCreate:"TRAD1",
      ctyDtLupd:"2020/12/01",
      ctyDtCreate:"2020/12/01",
      ctyUidLupd:"SYS",
    },
    

    {
      appId: "SSCC20201213435",
      version: "2",
      submitDate: "20-Nov-20",
      status: "A",
      
      appType:"SSCC",
      port: "KHPNH",
      voyageNo: "ARV4323",
      shipName: "Sea Master",
      expDate:"02-Jan-2022",
     
      imoNo:"IM0123",
      callSign:"c1Sign",
      cty:"SG",
      shippingLineTIN:"SL448",
      shippingLineName:"Moris Port",
      shippingLineAddress:"Line 1",
      portApplied:"Mories",

      docType:"AA Cert",
      docRefNo:"Ref123",

      securityLevel:"Level 2",

      ctyUidCreate:"TRAD1",
      ctyDtLupd:"2020/12/01",
      ctyDtCreate:"2020/12/01",
      ctyUidLupd:"SYS",
      
    },

    
 
  ],
};

Mock.onGet("/api/process/all").reply((config) => {
  return [200, commonApplicationDB.applications];
});

Mock.onGet("/api/process").reply((config) => {
  const id = config.data;
  const response = commonApplicationDB.applications.find((app) => app.appId === id);
  return [200, response];
});

Mock.onPost("/api/process/delete").reply((config) => {
  let app = JSON.parse(config.data);
  let index = { i: 0 };
  commonApplicationDB.applications.forEach((element) => {
    if (element.id === app.appId) {
      return [200, commonApplicationDB.applications.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, commonApplicationDB.applications];
});

Mock.onPost("/api/process/update").reply((config) => {
  let app = JSON.parse(config.data);
  let index = { i: 0 };
  commonApplicationDB.applications.forEach((element) => {
    if (element.id === app.appId) {
      commonApplicationDB.applications[index.i] = app;
      return [200, commonApplicationDB.applications];
    }
    index.i++;
  });
  return [200, commonApplicationDB.applications];
});

Mock.onPost("/api/process/add").reply((config) => {
  let app = JSON.parse(config.data);
  commonApplicationDB.applications.push(app);
  return [200, commonApplicationDB.applications];
});
