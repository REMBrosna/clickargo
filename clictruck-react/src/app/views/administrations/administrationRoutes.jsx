import React from "react";

import bankDetailsRoute from "./operations/bankDetails/bankDetailsRoute";
import chassisManagementRoutes from "./operations/chassis/chassisManagementRoutes";
import contractManagementRoutes from "./operations/contract/contractManagementRoutes";
import { contractReqRoutes } from "./operations/contractreq/contractReqRoutes";
import driverManagementRoutes from "./operations/driver/driverManagementRoutes";
import locationManagementRoutes from "./operations/location/locationManagementRoutes";
import orderTerminationRoutes from "./operations/orderTermination/orderTerminationRoutes";
import rateTableManagementRoutes from "./operations/ratetable/rateTableRoutes";
import rentalManagementRoutes from "./operations/rental/rentalManagementRoutes";
import taxRoutes from "./operations/tax/taxRoutes";
import truckManagementRoutes from "./operations/truck/truckManagementRoutes";
import SageRoutes from "./sage/SageRoutes";
import trucksRentalRoutes from "./operations/trucksRental/trucksRentalRoutes";
import trucksLeasingRoutes from "./operations/trucksLeasing/trucksLeasingRoutes";
import shellManagementRoutes from "./operations/shell/shellManagementRoutes";
import shellCardInvRoutes from "./operations/shellCardInvoicing/shellPaymentstRoutes";
import shellPaymentsRoutes from "./operations/shellCardPayment/shellPaymentsRoutes";

const administrationRoutes = [
  {
    path: "/administrations/audit/list",
    component: React.lazy(() => {
      return import("./auditLog/AuditList");
    }),
  },
  {
    path: "/administrations/audit/:viewType/:id?",
    component: React.lazy(() => import("./auditLog/AuditFormDetail")),
  },
  {
    path: "/administrations/exception/list",
    component: React.lazy(() => {
      return import("./exception/ExceptionList");
    }),
  },
  {
    path: "/administrations/exception/:viewType/:id?",
    component: React.lazy(() => import("./exception/ExceptionForm")),
  },
  {
    path: "/administrations/document/list",
    component: React.lazy(() => {
      return import("./document/DocumentList");
    }),
  },
  {
    path: "/reports/list",
    component: React.lazy(() => {
      return import("./report/ReportList");
    }),
  },
  {
    path: "/reports/generate/:id",
    component: React.lazy(() => import("./report/ReportGenerate")),
  },
  {
    path: "/administrations/sysparam/list",
    component: React.lazy(() => {
      return import("./sysparam/SysParamList");
    }),
  },
  {
    path: "/administrations/sysparam/:viewType/:sysKey?",
    component: React.lazy(() => import("./sysparam/SysParamForm")),
  },
  {
    path: "/administration/ARreport",
    component: React.lazy(() => {
      return import("./financeGli/SageARReportDetails");
    }),
  },
  {
    path: "/administrations/live-monitoring",
    component: React.lazy(() =>
      import("./operations/trackTrace/TruckJobTrackLive")
    ),
  },
  {
    path: "/administrations/historical-monitoring",
    component: React.lazy(() =>
      import("./operations/trackTrace/TruckJobTrackHistorical")
    ),
  },
  {
    path: "/administrations/trackTrackEnterExit/list",
    component: React.lazy(() => {
      return import("./operations/trackTraceEnterExit/TrackTraceEnterExitList");
    }),
  },
  {
    path: "/administrations/insuranceapp",
    component: React.lazy(() =>
        import("./operations/truckInsurance/ApplicationInsuranceFormDetails")
    ),
  },
  ...trucksLeasingRoutes,
  ...trucksRentalRoutes,
  ...truckManagementRoutes,
  ...driverManagementRoutes,
  ...locationManagementRoutes,
  ...rentalManagementRoutes,
  ...contractManagementRoutes,
  ...contractReqRoutes,
  ...rateTableManagementRoutes,
  ...taxRoutes,
  ...shellCardInvRoutes,
  ...shellPaymentsRoutes,
  ...shellManagementRoutes,
  ...SageRoutes,
  ...bankDetailsRoute,
  ...orderTerminationRoutes,
  ...chassisManagementRoutes,
  {
    path: "/opadmin/staticva",
    component: React.lazy(() =>
      import("../administrations/financeGli/StaticVADetails")
    ),
  },
  {
    path: "/opadmin/trackingdevices",
    component: React.lazy(() =>
      import("../administrations/operations/trackTrace/TrackingDevices")
    ),
  },
  {
    path: "/opadmin/accounts/co/list",
    component: React.lazy(() =>
      import("../administrations/operations/manualsuspension/DashboardPanel")
    ),
  },
  {
    path: "/opadmin/users",
    component: React.lazy(() =>
      import("../configurations/manageUsers/user/ManageUserListAll")
    ),
  },
  {
    path: "/opadmin/outboundpayments",
    // component: React.lazy(() => import("../administrations/operations/payments/outbound/outboundPaymentList")),
    component: React.lazy(() =>
      import("../administrations/operations/payments/outbound/DashboardPanel")
    ),
  },
  {
    path: "/opadmin/inboundpayments",
    component: React.lazy(() =>
      import(
        "../administrations/operations/payments/inbound/InboundPaymentList"
      )
    ),
  },
  {
    path: "/opadmin/contracts",
    component: React.lazy(() =>
      import("../administrations/operations/contract/ContractManagement")
    ),
  },
  {
    path: "/opadmin/creditlimit",
    component: React.lazy(() =>
      import("../administrations/operations/creditLimit/CreditLimitList")
    ),
  },
  {
    path: "/opadmin/inquiry/accn/list",
    component: React.lazy(() => import("./accnInquiry/AccnInquiryList")),
  },
  {
    path: "/opadmin/inquiry/accn/view/:id",
    component: React.lazy(() => import("./accnInquiry/AccnInquiryFormDetails")),
  },

  {
    path: "/opadmin/co2x/list",
    component: React.lazy(() => import("./operations/co2x/CO2XList")),
  },
  {
    path: "/opadmin/co2x/:viewType/:id",
    component: React.lazy(() => import("./operations/co2x/CO2XFormDetails")),
  },
  {
    path: "/administrations/department/list",
    component: React.lazy(() =>
      import("./operations/department/DepartmentsList")
    ),
  },
  {
    path: "/administrations/department/:viewType/:id",
    component: React.lazy(() =>
      import("./operations/department/DepartmentFormDetails")
    ),
  },
];

export default administrationRoutes;
