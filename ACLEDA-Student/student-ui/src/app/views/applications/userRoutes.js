import React from "react";
const userRoutes = [

    {
        path: "/student/applicationStudent/list",
        component: React.lazy(() =>
            import("./users/UsersList")
        )
    },
    {
        path: "/student/applicationStudent/:viewType/:id",
        component: React.lazy(() =>
            import("./users/UserFormDetails")
        )
    },
    {
        path: "/student/applicationStudent/updatePassword",
        component: React.lazy(() => import("./users/UserUpdatePasswordFormDetails")),
    },
    {
        path: "/student/applicationStudent/profile",
        component: React.lazy(() => import("./users/UsersDetailsProfile")),
    }
];

export default userRoutes;