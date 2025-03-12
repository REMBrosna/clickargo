export const authRoles = {
  documents: ['SHIP_LINE_USER'], // Only Super Admin has access
  services: ["FORWARDER_USER", "CARGO_OWNER_USER", "SHIP_LINE_USER", "PORT_USER"],
  servicesJobs: ["CARGO_OWNER_USER"],
  servicesBl: ["FORWARDER_USER"],
  all: ["SHIP_LINE_USER", "FORWARDER_USER", "CARGO_OWNER_USER"],
  admin: ['ADMIN', 'GLI_ADMIN'], // Only SA & Admin has access
  sales: ['GLI_SALES'],
  finance: ['GLI_FINANCE'],
  editor: ['SA', 'ADMIN', 'EDITOR'], // Only SA & Admin & Editor has access
  guest: ['SA', 'ADMIN', 'EDITOR', 'GUEST'] // Everyone has access
}

// Check out app/views/dashboard/DashboardRoutes.js
// Only SA & Admin has dashboard access

// const dashboardRoutes = [
//   {
//     path: "/dashboard/analytics",
//     component: Analytics,
//     auth: authRoles.admin <===============
//   }
// ];


// Check navigaitons.js

// {
//   name: "Dashboard",
//   path: "/dashboard/analytics",
//   icon: "dashboard",
//   auth: authRoles.admin <=================
// }