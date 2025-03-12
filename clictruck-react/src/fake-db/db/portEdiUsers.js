import Mock from "../mock";

export const userDB = {
  list: [
    {
      id: "STFMAR",
      index: 0,
      guid: "c01da2d1-07f8-4acc-a1e3-72dda7310af8",
      isActive: false,
      balance: 2838.08,
      age: 30,
      name: "Stefanie Marsh",
      gender: "female",
      company: "ACIUM",
      email: "stefaniemarsh@acium.com",
      phone: "+1 (857) 535-2066",
      address: "163 Poplar Avenue, Cliffside, Virginia, 4592",
      bd: "2015-02-08T04:28:44 -06:00",
      imgUrl: "/assets/images/face-1.png",
      userRole: "SHIPPING_LINE_ADMIN",
      nricNo: "P12BH345",
      designation: "Portal Admin",
      department: "IT",
      addr1: "29 Tai Seng Ave #05-01",
      addr2: "Natural Cool Lifestyle Hub",
      city: "Singapore",
      postalCode: "540086",
      country: "SG",
      fax: "543456787",
      telephone: "5679876789",
      province: "SG"
    },
    {
      id: "ELENABT",
      index: 1,
      guid: "3f04aa40-62da-466d-ac14-2b8a5da3d1ce",
      isActive: true,
      balance: 3043.81,
      age: 39,
      name: "Elena Bennett",
      gender: "female",
      company: "FIBRODYNE",
      email: "elenabennett@fibrodyne.com",
      phone: "+1 (994) 570-2070",
      address: "526 Grace Court, Cherokee, Oregon, 7017",
      bd: "2017-11-15T09:04:57 -06:00",
      imgUrl: "/assets/images/face-2.png",
      userRole: "SHIPPING_AGENT",
      nricNo: "P12BH345",
      designation: "Portal Admin",
      department: "IT",
      addr1: "29 Tai Seng Ave #05-01",
      addr2: "Natural Cool Lifestyle Hub",
      city: "Singapore",
      postalCode: "540086",
      country: "SG",
      fax: "543456787",
      telephone: "5679876789",
      province: "SG"
    },
    {
      id: "JONICAB",
      index: 2,
      guid: "e7d9d61e-b657-4fcf-b069-2eb9bfdc44fa",
      isActive: true,
      balance: 1796.92,
      age: 23,
      name: "Joni Cabrera",
      gender: "female",
      company: "POWERNET",
      email: "jonicabrera@powernet.com",
      phone: "+1 (848) 410-2368",
      address: "554 Barlow Drive, Alamo, Michigan, 3686",
      bd: "2017-10-15T12:55:51 -06:00",
      imgUrl: "/assets/images/face-3.png",
      userRole: "SHIPPING_AGENT",
      nricNo: "P12BH345",
      designation: "Portal Admin",
      department: "IT",
      addr1: "29 Tai Seng Ave #05-01",
      addr2: "Natural Cool Lifestyle Hub",
      city: "Singapore",
      postalCode: "540086",
      country: "SG",
      fax: "543456787",
      telephone: "5679876789",
      province: "SG"
    },
    {
      id: "GALGSHAW",
      index: 3,
      guid: "47673d82-ab31-48a1-8a16-2c6701573c67",
      isActive: false,
      balance: 2850.27,
      age: 37,
      name: "Gallagher Shaw",
      gender: "male",
      company: "ZILLAR",
      email: "gallaghershaw@zillar.com",
      phone: "+1 (896) 422-3786",
      address: "111 Argyle Road, Graball, Idaho, 7272",
      bd: "2017-11-19T03:38:30 -06:00",
      imgUrl: "/assets/images/face-4.png",
      userRole: "AGENT_ASSISTANT",
      nricNo: "P12BH345",
      designation: "Portal Admin",
      department: "IT",
      addr1: "29 Tai Seng Ave #05-01",
      addr2: "Natural Cool Lifestyle Hub",
      city: "Singapore",
      postalCode: "540086",
      country: "SG",
      fax: "543456787",
      telephone: "5679876789",
      province: "SG"
    },
    
  ],
};

Mock.onGet("/api/user/all").reply((config) => {
  return [200, userDB.list];
});

Mock.onGet("/api/user").reply((config) => {
  const id = config.data;
  const response = userDB.list.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/user/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  userDB.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, userDB.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, userDB.list];
});

Mock.onPost("/api/user/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  userDB.list.forEach((element) => {
    if (element.id === user.id) {
      userDB.list[index.i] = user;
      return [200, userDB.list];
    }
    index.i++;
  });
  return [200, userDB.list];
});

Mock.onPost("/api/user/add").reply((config) => {
  let user = JSON.parse(config.data);
  userDB.list.push(user);
  return [200, userDB.list];
});
