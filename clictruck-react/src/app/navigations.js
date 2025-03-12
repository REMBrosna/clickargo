import { authRoles } from "./auth/authRoles";

export const navigations = [

    {
        name: "Onboarding",
        path: `/onboarding/list`,
        icon: "group",
        auth: authRoles.admin,
    },
    // {
    //     //To be shown later
    //     name: "Subscribed Services",
    //     path: "/applications/services",
    //     icon: "work",
    //     auth: authRoles.services
    // },
    {
        name: "Services",
        path: "/applications/jobs/list",
        icon: "work",
        auth: authRoles.servicesJobs
    },
    {
        name: "Services",
        path: "/applications/services/bl",
        icon: "work",
        auth: authRoles.servicesBl
    },
    {
        name: "Documents",
        path: "/applications/documents/do",
        icon: "work",
        auth: authRoles.documents
    },
    {
        name: "Payments",
        path: "/payments",
        icon: "payment",
        auth: authRoles.all
    },
    {
        name: "Administrations",
        icon: "group",
        auth: authRoles.services,
        children: [
            {
                name: "Account Profile",
                path: `/manageAccount/edit/my`,
                iconText: "CC",
                auth: authRoles.services
            },
            {
                name: "Manage Users",
                path: "/manageUsers/user/list",
                iconText: "CC",
                auth: authRoles.services
            },
            {
                name: "Manage Account",
                path: `/manageAccount/edit`,
                iconText: "CC",
                auth: authRoles.services
            },
            {
                name: "Authorisations",
                path: `/authorisations/authorisation/list`,
                iconText: "CC",
                auth: authRoles.services,
            },
        ],
    },
    {
        name: "Help",
        icon: "help",
        path: "/help",
        iconText: "CC",
        auth: authRoles.all
    },
];
