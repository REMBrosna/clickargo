import Mock from "../mock";

export const commonApplicationDB = {
  list: [
    {
      appId: "PO2021030812345",
      version: "1",
      submitDate: "20-Nov-20",
      status: "Draft",
      
      port: "PAP",
      voyageNo: "ARV1234",
      shipName: "Maersk Alabama",
     
      masterName: "Tom Hanks",
      portOfArrivalOrDept: "KHKOS",
      dateOfArrv:"13/12/2020",
      portArrivedFrom:"KHSHP",
      nextPort:"SG",
      dateOfDeparture:"12/1/2020",
      
      stowaways: 'Y',
        animalOrPlants: 'N',
        parcelPackage: 'Y',
        armsAmmunitions: 'N',
        illegalDrugs: 'N',
        passengers: 'Y',
        livestocks: 'N',

      signUpload:"",

      reqMessage:"",

      application:"NIL",

      reqMessage:"Please issue a Pilot order for my application. Ship details are furnished",


    },

    {
      appId: "DD123121212",
      version: "2",
      submitDate: "20-Nov-20",
      status: "Approved",
      
      port: "PAS",
      voyageNo: "ARV4323",
      shipName: "Sea Master",
     
      masterName: "Tom Hanks",
      portOfArrivalOrDept: "KHKOS",
      dateOfArrv:"13/12/2020",
      portArrivedFrom:"KHSHP",
      nextPort:"SG",
      dateOfDeparture:"12/1/2020",
      
      stowaways: 'Y',
      animalOrPlants: 'N',
      parcelPackage: 'Y',
      armsAmmunitions: 'N',
      illegalDrugs: 'N',
      passengers: 'Y',
      livestocks: 'N',

      signUpload:"",

      reqMessage:"Please issue a Pilot order for my application. Ship details are furnished",

      application:"Pilot",

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
