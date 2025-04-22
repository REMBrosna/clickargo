import Mock from "../mock";

export const crewDB = {
  list: [
    {
      id: "CL20201213",
      name: "Albert",
      passportNo: "P183456",
      rank: "CAPTAIN",
      createdBy: "ADMIN01",
      createdDate: "2021-02-21T12:30:45",
      familyName: "Dsouza",
      givenName: "Albert",
      gender: "Male",
      nationality: "AT",
      dob: "01-04-1990",
      placeOfBirth: "Atlanta",
      identityDocType: "Passport",
      issuingStateOfIdnetity: "FR",
      expiryDateOfIdentity: "12-03-2025",
      vaccinationNo: "Vacc1",
      typeOfVaccination: "Cholera",
      expDate: "12-09-2022",
    },
    {
      id: "ID234",
      name: "Roth",
      passportNo: "P183456",
      rank: "OFFICER",
      createdBy: "ADMIN01",
      createdDate: "2020-12-13T12:30:45",
      familyName: "Dsouza",
      givenName: "Roth",
      gender: "Male",
      nationality: "AG",
      dob: "12-08-1998",
      placeOfBirth: "Atlanta",
      identityDocType: "Passport",
      issuingStateOfIdnetity: "FR",
      expiryDateOfIdentity: "12-02-2023",
      vaccinationNo: "Vacc2",
      typeOfVaccination: "Yellow Fever",
      expDate: "12-08-2024",
    },
    {
      id: "ID566",
      name: "Heng",
      passportNo: "P183456",
      rank: "OFFICER",
      createdBy: "ADMIN01",
      createdDate: "2021-04-23T12:30:45",
      familyName: "Dsouza",
      givenName: "Heng",
      gender: "Male",
      nationality: "AG",
      dob: "14-01-1993",
      placeOfBirth: "Atlanta",
      identityDocType: "Passport",
      issuingStateOfIdnetity: "FR",
      expiryDateOfIdentity: "21-08-2024",
      vaccinationNo: "Vacc2",
      typeOfVaccination: "Yellow Fever",
      expDate: "23-08-2023",
    },
    {
      id: "ID988",
      name: "Ung",
      passportNo: "P183456",
      rank: "SHIP-MASTER",
      createdBy: "ADMIN01",
      createdDate: "2021-02-21T12:30:45",
      familyName: "Dsouza",
      givenName: "Visal",
      gender: "Male",
      nationality: "AG",
      dob: "01-08-1994",
      placeOfBirth: "Atlanta",
      identityDocType: "Passport",
      issuingStateOfIdnetity: "FR",
      expiryDateOfIdentity: "08-07-2022",
      vaccinationNo: "Vacc2",
      typeOfVaccination: "Yellow Fever",
      expDate: "09-04-2023",
    },
  ],
};

Mock.onGet("/api/user/all").reply((config) => {
  return [200, crewDB.list];
});

Mock.onGet("/api/user").reply((config) => {
  const id = config.data;
  const response = crewDB.list.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/user/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  crewDB.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, crewDB.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, crewDB.list];
});

Mock.onPost("/api/user/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  crewDB.list.forEach((element) => {
    if (element.id === user.id) {
      crewDB.list[index.i] = user;
      return [200, crewDB.list];
    }
    index.i++;
  });
  return [200, crewDB.list];
});

Mock.onPost("/api/user/add").reply((config) => {
  let user = JSON.parse(config.data);
  crewDB.list.push(user);
  return [200, crewDB.list];
});
