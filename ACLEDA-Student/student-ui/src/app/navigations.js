
export const navigations = [
    {
        name: "Student Management",
        icon: "list",
        children: [
            {
                name: "Application Student",
                path: "/student/applicationStudent/list",
                iconText: "CC",
            }
        ],
    },
    {
        name: "Administrations",
        icon: "group",
        auth: ["ROLE_ADMIN"], // admin only
        children: [
            {
                name: "Manage Users",
                path: "/user/list",
                iconText: "CC",
                auth: ["ROLE_ADMIN"], // admin only
            },
        ],
    },
];
