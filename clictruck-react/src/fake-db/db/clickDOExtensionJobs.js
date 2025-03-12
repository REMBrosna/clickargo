import Mock from "../mock";

export const clickDOExtensionJobs = {
  list: [

  ],
};

Mock.onGet("/api/extensionTasks/all").reply((config) => {
  return [200, clickDOExtensionJobs.users];
});

Mock.onGet("/api/extensionTasks").reply((config) => {
  const id = config.data;
  const response = clickDOExtensionJobs.users.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/extensionTasks/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  clickDOExtensionJobs.users.forEach((element) => {
    if (element.id === user.id) {
      return [200, clickDOExtensionJobs.users.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, clickDOExtensionJobs.users];
});

Mock.onPost("/api/extensionTasks/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  clickDOExtensionJobs.users.forEach((element) => {
    if (element.id === user.id) {
      clickDOExtensionJobs.users[index.i] = user;
      return [200, clickDOExtensionJobs.users];
    }
    index.i++;
  });
  return [200, clickDOExtensionJobs.users];
});

Mock.onPost("/api/extensionTasks/add").reply((config) => {
  let user = JSON.parse(config.data);
  clickDOExtensionJobs.users.push(user);
  return [200, clickDOExtensionJobs.users];
});
