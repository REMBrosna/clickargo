import React from "react";

const bankDetailsRoute = [
    {
        path: "/opadmin/bankaccounts",
        component: React.lazy(() => import("./BankAccountList")),
    },
]

export default bankDetailsRoute;