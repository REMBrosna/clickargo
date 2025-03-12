import Mock from "../mock";

export const pilotOrderDB = {
  list: [

    {
      appId: "PO20203124",
      version: "2",
      submitDate: "20-11-2020",
      //status: "Pending Verification",
      status: "Submitted",
      appType: "PO",
      vcrNo: "PEDI202012123",
      port: "PAS",
      voyageNo: "ARV4323",
      shipName: "Sea Master",

      masterName: "Tom Hanks",
      portOfArrivalOrDept: "KHPNH",
      dateOfArrv: "01-01-2020",
      portArrivedFrom: "KHKOS",
      nextPort: "SG",
      dateOfDeparture: "01-01-2020",

      stowaways: 'Y',
        animalOrPlants: 'N',
        parcelPackage: 'Y',
        armsAmmunitions: 'N',
        illegalDrugs: 'N',
        passengers: 'Y',
        livestocks: 'N',

      signUpload: "",

      reqMessage: "Please issue a Pilot order for my application. Ship details are furnished",

      application: "Pilot",
      shipNameP: "Maersk Alabama",
      voyageNoP: "ARV1234",
      portOfArrivalOrDeptP: "KHPNH",
      dateOfArrvP: "01-01-2020",
      portArrivedFromP: "KHPNH",

    },

    {
      appId: "PO20203125",
      version: "1",
      submitDate: "20-11-2020",
      status: "Approved",
      //status: "A",
      appType: "PO",
      vcrNo: "PEDI202012123",

      port: "PAP",
      voyageNo: "ARV1234",
      shipName: "Maersk Alabama",

      masterName: "Tom Hanks",
      portOfArrivalOrDept: "KHSHP",
      dateOfArrv: "01-01-2020",
      portArrivedFrom: "KHOKP",
      nextPort: "SG",
      dateOfDeparture: "12-1-2020",

      stowaways: 'Y',
      animalOrPlants: 'N',
      parcelPackage: 'Y',
      armsAmmunitions: 'N',
      illegalDrugs: 'N',
      passengers: 'Y',
      livestocks: 'N',

      signUpload: "",

      application: "NIL",

      reqMessage: "Please issue a Pilot order for my application. Ship details are furnished",

      shipNameP: "Maersk Alabama",
      voyageNoP: "ARV1234",
      portOfArrivalOrDeptP: "KHPNH",
      dateOfArrvP: "01-01-2020",
      portArrivedFromP: "KHPNH",

    },

  ],
}

Mock.onGet("/api/process/all").reply((config) => {
  return [200, pilotOrderDB.applications];
});
