import React from "react";

import masterRoutes from "./masters/masterRoutes";

const configurationRoutes = [
    ...masterRoutes,

    // FF-CO ACCOUNT
    {
        path: "/manageAccountsFfCo/all/list",
        component: React.lazy(() => import("./manageAccountFfCo/ManageAccountsFfCoList")),
    },
    {
        path: "/manageAccountsFfCo/:viewType/:id/:ffAccnId",
        component: React.lazy(() => import("./manageAccountFfCo/AccountUserProfileFormDetails")),
    },

    // ACCOUNT CONFIGURATION

    {
        path: "/manageAccount/:viewType/:id",
        component: React.lazy(() => import("./manageAccount/accountProfile/AccountProfileFormDetails")),
    },
    {
        path: "/manageAccounts/all/list",
        component: React.lazy(() => import("./manageAccount/ManageAccountsList")),
    },
    {
        path: "/account/profile/",
        component: React.lazy(() => import("./manageAccount/accountProfile/AccountWorkbench"))
    },
    {
        path: "/detail/account/:viewType/:id",
        component: React.lazy(() => import("./manageAccount/accountProfile/AccountProfileFormDetails"))
    },

    // USER CONFIGURATION
    {
        path: "/manageUsers/users/all/list",
        component: React.lazy(() => import("./manageUsers/user/ManageUserListAll")),
    },
    {
        path: "/manageUsers/user/list",
        component: React.lazy(() => import("./manageUsers/user/ManageUserList")),
    },
    {
        path: "/manageUsers/user/:viewType/:id",
        component: React.lazy(() => import("./manageUsers/user/ManageUserFormDetail")),
    },

    {
        path: "/manageAccount/user/:viewType/:id",
        component: React.lazy(() => import("./manageUsers/user/ManageUserFormDetail")),
    },
    {
        path: "/opadmin/user/:viewType/:id",
        component: React.lazy(() => import("./manageUsers/user/ManageUserFormDetailCs")),
    },
    {
        path: "/account/users",
        component: React.lazy(() => import("./manageUsers/user/ManageUserList")),
    },

    // update Password
    {
        path: "/manageUsers/updatePassword",
        component: React.lazy(() => import("./manageUsers/user/UpdatePasswordFormDetails")),
    },

    // CONFIGURATION

    {
        path: "/configuration/crewList/list",
        component: React.lazy(() => import("./crewList/CrewList")),
    },
    {
        path: "/configuration/crewList/:viewType/:id",
        component: React.lazy(() => import("./crewList/CrewFormDetails")),
    },
    {
        path: "/configuration/docRepo/list",
        component: React.lazy(() => import("./docRepo/DocRepoList")),
    },

    {
        path: "/configuration/docRepo/:viewType/:accnID",
        component: React.lazy(() => import("./docRepo/DocRepoFormDetails")),
    },
    {
        path: "/configuration/announcementType/list",
        component: React.lazy(() => import("./announcementType/AnnouncementTypeList")),
    },
    {
        path: "/configuration/announcementType/:viewType/:id?",
        component: React.lazy(() => import("./announcementType/AnnouncementTypeForm")),
    },
    {
        path: "/configuration/announcement/list",
        component: React.lazy(() => import("./announcement/AnnouncementList")),
    },
    {
        path: "/configuration/announcement/:viewType/:id?",
        component: React.lazy(() => import("./announcement/AnnouncementForm")),
    },
    {
        path: "/configuration/notification/preferences",
        component: React.lazy(() => import("../notification/notificationPreference/NotificationPreferenceForm")),
    },
    {
        path: "/association/agentAss/list",
        component: React.lazy(() => import("./association/agentAss/AgentAssList")),
    },

    {
        path: "/association/agentAss/:viewType/:agentTIN",
        component: React.lazy(() => import("./association/agentAss/AgentAssFormDetails")),
    },


    // {
    //     path: "/manageAccount/:viewType/:id",
    //     component: React.lazy(() => import("./manageAccount/accountProfile/AccountProfileFormDetails")),
    // },
    {
        path: "/onboarding/:viewType/:regId",
        component: React.lazy(() => import("./manageAccount/onboarding/AccountOnboardingFormDetail")),
    },
    {
        path: "/onboarding/list",
        component: React.lazy(() => import("./manageAccount/onboarding/AccountOnboardingList")),
    },


    // {
    //     path: "/recoveryApplicationConfig/list",
    //     component: React.lazy(() => import("./recoveryApplications/RecoveryApplicationList")),
    // },
    // {
    //     path: "/recoveryApplicationConfig/:viewType/:recId",
    //     component: React.lazy(() => import("./recoveryApplications/RecoveryApplicationFormDetails")),
    // },

    // {
    //     path: "/shipsBlacklist/list",
    //     component: React.lazy(() => import("./shipsBlacklist/ShipsBlacklistList")),
    // },
    // {
    //     path: "/shipsBlacklist/:viewType/:recId",
    //     component: React.lazy(() => import("./shipsBlacklist/ShipsBlacklistFormDetails")),
    // },

    {
        path: "/applications/documentUpload/doc",
        component: React.lazy(() => import("./applications/uploadApp/UploadFormDetails")),
    },
    {
        path: "/applications/info/contact",
        component: React.lazy(() => import("../sessions/info/Contact")),
    },
    {
        path: "/applications/info/faq",
        component: React.lazy(() => import("../sessions/info/FAQ")),
    },

    // {
    //     path: "/authorisations/authorisation/list",
    //     component: React.lazy(() => import("./authorisations/authorisation/AuthorisationsList")),
    // },
    // {
    //     path: "/authorisations/authorisation/:viewType/:id",
    //     component: React.lazy(() => import("./authorisations/authorisation/AuthorisationsFormDetail")),
    // },
    // {
    //     path: "/deliveryOrder/jobs/claim/list",
    //     component: React.lazy(() => import("../applications/fforwarder/doClaim/DoClaimJobsList"))
    // },
    // {
    //     path: "/deliveryOrder/jobs/claim/:viewType/:jobId",
    //     component: React.lazy(() => import("../applications/fforwarder/doClaim/DoClaimJobsFormDetails")),
    // }
];

export default configurationRoutes;
