import Mock from "../mock";

export const docUploadDB = {
  list: [
    {
        transNo:"TRAN0001",
        vcrno:"PEDI202012123",
        uploadfor:"Cargo",
        fileType:"pdf",
        status:"Submitted"
    },
    ],
};

Mock.onGet("/api/user/all").reply((config) => {
  return [200, docUploadDB.list];
});

Mock.onGet("/api/user").reply((config) => {
  const id = config.data;
  const response = docUploadDB.list.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/user/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  docUploadDB.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, docUploadDB.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, docUploadDB.list];
});

Mock.onPost("/api/user/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  docUploadDB.list.forEach((element) => {
    if (element.id === user.id) {
        docUploadDB.list[index.i] = user;
      return [200, docUploadDB.list];
    }
    index.i++;
  });
  return [200, docUploadDB.list];
});

Mock.onPost("/api/user/add").reply((config) => {
  let user = JSON.parse(config.data);
  docUploadDB.list.push(user);
  return [200, docUploadDB.list];
});
