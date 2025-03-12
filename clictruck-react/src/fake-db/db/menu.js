import Mock from "../mock";

// const data = [
//   { name: "Services", icon: "grid_view", path: "/" },
//   { name: "Help", icon: "help_outline", path: "/help" },
// ];

const data = [
  {
    name: "Administration",
    icon: "grid_view",
    path: "/",
    children: [
      {
        name: "Manage Truck",
        path: `/administrations/truck-management/list`,
        iconText: "CC",
        // auth: authRoles.services,
      },
      {
        name: "Manage Drive",
        path: "/administrations/driver-management/list",
        iconText: "CC",
        // auth: authRoles.services,
      },
      {
        name: "Manage Location",
        path: "/administrations/location-management/list",
        iconText: "CC",
        // auth: authRoles.services,
      },
      {
        name: "Manage Rental",
        path: "/administrations/rental-management/list",
        iconText: "CC",
        // auth: authRoles.services,
      },
      {
        name: "Manage Contract",
        path: "/administrations/contract-management/list",
        iconText: "CC",
        // auth: authRoles.services,
      },
    ],
  },
  {
    name: "Job",
    icon: "grid_view",
    path: "/",
    children: [
      {
        name: "Job List",
        path: `/applications/services/job/list`,
        iconText: "CC",
        // auth: authRoles.services,
      },
      {
        name: "Job Create New",
        path: "/applications/services/job/job/new/-",
        iconText: "CC",
        // auth: authRoles.services,
      },
      {
        name: "Truck Job List",
        path: `/applications/services/truck-job/list`,
        iconText: "CC",
      },
    ],
  },
  {
    name: "Finance",
    icon: "grid_view",
    path: "/",
    children: [
      {
        name: "Service",
        path: `/applications/finance/verification/dashboard`,
        iconText: "CC",
      },
      {
        name: "History",
        path: `/applications/finance/details/history`,
        iconText: "CC",
      },
    ],
  },
  {
    name: "Gli",
    icon: "grid_view",
    path: "/",
    children: [
      {
        name: "Dashboard",
        path: `/applications/services/job/gli`,
        iconText: "CC",
      },
      {
        name: "Static VA",
        path: `/administration/gli/detail/va`,
        iconText: "CC",
      }
    ],
  },
  { name: "Help", icon: "help_outline", path: "/help" },
];

Mock.onGet("/api/v1/clickargo/auth/menu").reply((config) => {
  return [200, data];
});
