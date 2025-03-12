import React from "react";

const masterRoutes = [
    //country
    {
        path: "/master/country/list",
        component: React.lazy(() => import("./country/CountryList")),
    },

    {
        path: "/master/country/:viewType/:ctryCode",
        component: React.lazy(() => import("./country/CountryFormDetails")),
    },

    //currency
    {
        path: "/master/currency/list",
        component: React.lazy(() => import("./currency/CurrencyList")),
    },
    {
        path: "/master/currency/:viewType/:ccyCode?",
        component: React.lazy(() => import("./currency/CurrencyFormDetails")),
    },

    //contract
    {
        path: "/master/contract/list",
        component: React.lazy(() => import("./contract/ContractList")),
    },
    {
        path: "/master/contract/:viewType/:radio?",
        component: React.lazy(() => import("./contract/ContractFormDetails")),
    },
    //harmonized
    {
        path: "/master/hsCode/list",
        component: React.lazy(() => import("./hsCode/HsCodeList")),
    },
    {
        path: "/master/hsCode/:viewType/:hsCode?",
        component: React.lazy(() => import("./hsCode/HsCodeFormDetails")),
    },

    //for port
    {
        path: "/master/port/list",
        component: React.lazy(() => import("./port/PortList")),
    },
    {
        path: "/master/port/:viewType/:portCode?",
        component: React.lazy(() => import("./port/PortFormDetails")),
    },

    //for UOM
    {
        path: "/master/uom/list",
        component: React.lazy(() => import("./uom/UOMCodesList")),
    },
    {
        path: "/master/uom/:viewType/:uomCode",
        component: React.lazy(() => import("./uom/UOMCodeFormDetails")),
    },

    //for Ministry
    {
        path: "/master/ministry/list",
        component: React.lazy(() => import("./ministry/MinistryList")),
    },
    {
        path: "/master/ministry/:viewType/:id?",
        component: React.lazy(() => import("./ministry/MinistryFormDetails")),
    },

    //for Agency
    {
        path: "/master/agency/list",
        component: React.lazy(() => import("./agency/AgencyList")),
    },
    {
        path: "/master/agency/:viewType/:id?",
        component: React.lazy(() => import("./agency/AgencyFormDetails")),
    },

    //for Agency Office
    {
        path: "/master/agencyoffice/list",
        component: React.lazy(() => import("./agencyoffice/AgencyOfficeList")),
    },
    {
        path: "/master/agencyoffice/:viewType/:id?",
        component: React.lazy(() => import("./agencyoffice/AgencyOfficeFormDetails")),
    },


    {
        path: "/master/attachType/list",
        component: React.lazy(() => {
            return import("./attachType/AttachTypeList");
        }),
    },
    {
        path: "/master/attachType/:viewType/:id?",
        component: React.lazy(() => {
            return import("./attachType/AttachTypeFormDetails");
        }),
    },
    {
        path: "/master/docAssociate/list",
        component: React.lazy(() => {
            return import("./docAssociate/DocAssociateList");
        }),
    },
    {
        path: "/master/docAssociate/:viewType/:id?",
        component: React.lazy(() => {
            return import("./docAssociate/DocAssociateFormDetails");
        }),
    },

    //Thruster Type Master Data
    {
        path: "/master/thrusterType/list",
        component: React.lazy(() => import("./thrusterType/ThrusterTypeList")),
    },
    {
        path: "/master/thrusterType/:viewType/:ttCode?",
        component: React.lazy(() => import("./thrusterType/ThrusterTypeFormDetails")),
    },

    // Payment Type Master Data
    {
        path: "/master/paymentType/list",
        component: React.lazy(() => import("./paymentType/PaymentTypeList")),
    },
    {
        path: "/master/paymentType/:viewType/:ptCode?",
        component: React.lazy(() => import("./paymentType/PaymentTypeFormDetails")),
    },

    // Province
    {
        path: "/master/province/list",
        component: React.lazy(() => import("./province/ProvinceList")),
    },
    {
        path: "/master/province/:viewType/:provinceId?",
        component: React.lazy(() => import("./province/ProvinceFormDetails")),
    },

    // Article Category
    {
        path: "/master/articleCategory/list",
        component: React.lazy(() => import("./articleCategory/ArticleCategoryList")),
    },
    {
        path: "/master/articleCategory/:viewType/:arcCode?",
        component: React.lazy(() => import("./articleCategory/ArticleCategoryFormDetails")),
    },


    // Port Terminal
    {
        path: "/master/portTerminal/list",
        component: React.lazy(() => import("./portTerminal/PortTerminalList")),
    },
    {
        path: "/master/portTerminal/:viewType/:portTeminalId?",
        component: React.lazy(() => import("./portTerminal/PortTerminalFormDetails")),
    },


    // Notification Template
    {
        path: "/notification/templates/list",
        component: React.lazy(() => import("../../notification/notificationTemplate/NotificationTemplateList")),
    },
    {
        path: "/notification/templates/:viewType/:ntplId?",
        component: React.lazy(() => import("../../notification/notificationTemplate/NotificationTemplateFormDetails")),
    },

    // Certificate Config
    {
        path: "/master/certificateConfig/list",
        component: React.lazy(() => import("./certificateConfig/CertificateConfigList")),
    },
    {
        path: "/master/certificateConfig/:viewType/:id",
        component: React.lazy(() => import("./certificateConfig/CertificateConfigFormDetails")),
    },

    // Certificate Svc
    {
        path: "/master/certificateSvc/list",
        component: React.lazy(() => import("./certificateSvc/CertificateSvcList")),
    },
    {
        path: "/master/certificateSvc/:viewType/:id?",
        component: React.lazy(() => import("./certificateSvc/CertificateSvcFormDetails")),
    },
    {
        path: "/administrations/alert",
        component: React.lazy(() => import("../notification/NotificationFormDetails")),
    },
    {
        path: "/master/notification/list",
        component: React.lazy(() => import("../notification/NotificationFormDetails")),
    },
    {
        path: "/master/notificationType/list",
        component: React.lazy(() => import("./notificationType/NotificationTypeList")),
    },
    {
        path: "/master/notificationType/:viewType/:id?",
        component: React.lazy(() => import("./notificationType/NotificationTypeFormDetails")),
    },
];

export default masterRoutes;
