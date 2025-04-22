import Mock from "../mock";

export const accOnBoardingDB = {
  list: [

    {
      id: "1002021",
      refNo: "REF01",
      accnId: "",
      accType: "AGENT",
      compName: "ABC Company",
      tinNo: "0125010823",
      ownerName: "John",
      companyEmail: "agent@gmail.com",
      nationality: "SG",
      addr1: "163 Poplar Avenue",
      addr2: "Cliffside",
      addr3: "Virginia",
      country: "SG",
      province: "Singapore",
      city: "Singapore",
      postalCode: "56987658",
      fullName: "Stefanie Marsh",
      userLoginId: "MPWT001",
      emailId: "stefaniemarsh@acium.com",
      contactNo: "+60-456786587",
      gender: "Female",
      position: "Admin",
      idPassPortNo: "P899876",
      validity: "10/12/2022",
      dob: "10/12/1980",
      userAddr1: "163 Poplar Avenue",
      userAddr2: "Cliffside",
      userAddr3: "SG",
      userNationality: "SG",
      userProvince: "Singapore",
      userCity: "Singapore",
      userPostalCode: "569809",
      status: "Submitted",

    },

    {
      id: "1012021",
      accnId: "ABCCOMP",
      refNo: "REF02",
      accType: "SHIPPING",
      compName: "ABC Company",
      tinNo: "0125010823",
      ownerName: "John",
      contactNo: "+60-11223444",
      companyEmail: "agent@gmail.com",
      nationality: "SG",
      addr1: "163 Poplar Avenue",
      addr2: "Cliffside",
      addr3: "Virginia",
      country: "SG",
      province: "Singapore",
      city: "Singapore",
      postalCode: "56987658",
      fullName: "Stefanie Marsh",
      userLoginId: "MPWT001",
      emailId: "stefaniemarsh@acium.com",
      gender: "Female",
      position: "Admin",
      idPassPortNo: "P899876",
      validity: "10/12/2022",
      dob: "10/12/1980",
      userAddr1: "163 Poplar Avenue",
      userAddr2: "Cliffside",
      userAddr3: "SG",
      userNationality: "SG",
      userProvince: "Singapore",
      userCity: "Singapore",
      userPostalCode: "569809",
      status: "Approved"

    },

  ],
};

Mock.onGet("/api/docRepo/all").reply((config) => {
  return [200, accOnBoardingDB.list];
});

Mock.onGet("/api/country").reply((config) => {
  const id = config.data;
  const response = accOnBoardingDB.list.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/docRepo/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  accOnBoardingDB.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, accOnBoardingDB.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, accOnBoardingDB.list];
});

Mock.onPost("/api/docRepo/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  accOnBoardingDB.list.forEach((element) => {
    if (element.id === user.id) {
      accOnBoardingDB.list[index.i] = user;
      return [200, accOnBoardingDB.list];
    }
    index.i++;
  });
  return [200, accOnBoardingDB.list];
});

Mock.onPost("/api/docRepo/add").reply((config) => {
  let user = JSON.parse(config.data);
  accOnBoardingDB.list.push(user);
  return [200, accOnBoardingDB.list];
});