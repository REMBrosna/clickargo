import { Icon } from "@material-ui/core";
import { Assignment, Build, FileCopy, Schedule } from "@material-ui/icons";
import React from "react";

export const Status = {
  ACK: { code: "ACK", desc: "Acknowledged" },
  APP: { code: "APP", desc: "Approved" },
  NEW: { code: "NEW", desc: "New" },
  DRF: { code: "DRF", desc: "Draft" },
  REJ: { code: "REJ", desc: "Rejected" },
  RET: { code: "RET", desc: "Returned" },
  EXP: { code: "EXP", desc: "Expired" },
  VER: { code: "VER", desc: "Verified" },
  PEN: { code: "PEN", desc: "Pending Payment" },
  PAY: { code: "PAY", desc: "Paid" },
  AMN: { code: "AMN", desc: "Amended" },
  SUB: { code: "SUB", desc: "Submitted" },
  ORT: { code: "ORT", desc: "Officer Returned" },
  PRA: { code: "PRA", desc: "Pre Approved" },
  INV: { code: "INV", desc: "Invoice Generated" },
  ACV: { code: "A", desc: "Active" },
  NCV: { code: "I", desc: "InActive" },
  SUS: { code: "S", desc: "Suspended" },
  SUC: { code: "SUC", desc: "SUCCESS" },
  PAR: { code: "PAR", desc: "PARTIAL" },
};

export const VehicleTypes = {
  '10FT': { code: "10FT", desc: "10 FT" },
  '10FT_EV': { code: "10FT_EV", desc: "10 FT - EV" },
  '14FT': { code: "14FT", desc: "14 FT" },
  '14FT_BOX': { code: "14FT_BOX", desc: "14 FT - BOX" },
  '14FT_BOX_TG': { code: "14FT_BOX_TG", desc: "14 FT - BOX TAILGATE" },
  '14FT_EV': { code: "14FT_EV", desc: "14 FT - EV" },
  '14FT_REEFER': { code: "14FT_REEFER", desc: "14 FT - REEFER" },
  '18FT': { code: "18FT", desc: "18 FT" },
  '18FT_BOX': { code: "18FT_BOX", desc: "18 FT - BOX" },
  '18FT_BOX_TG': { code: "18FT_BOX_TG", desc: "18 FT - BOX TAILGATE" },
  '18FT_EV': { code: "18FT_EV", desc: "18 FT - EV" },
  '18FT_REEFER': { code: "18FT_REEFER", desc: "18 FT - REEFER" },
  '24FT': { code: "24FT", desc: "24 FT" },
  '24FT_BOX': { code: "24FT_BOX", desc: "24 FT - BOX" },
  '24FT_BOX_DG_TG': { code: "24FT_BOX_DG_TG", desc: "24 FT - BOX HAZMAT TAILGATE (DG)" },
  '24FT_BOX_TG': { code: "24FT_BOX_TG", desc: "24 FT - BOX TAILGATE" },
  '24FT_CURTAIN': { code: "24FT_CURTAIN", desc: "24 FT - CURTAIN SIDER" },
  '24FT_CURTAIN_DG': { code: "24FT_CURTAIN_DG", desc: "24 FT - CURTAIN SIDER HAZMAT (DG)" },
  '24FT_EV': { code: "24FT_EV", desc: "24 FT - EV" },
  '24FT_REEFER': { code: "24FT_REEFER", desc: "24 FT - REEFER" },
  '25FT_REEFER': { code: "25FT_REEFER", desc: "25 FT - REEFER" },
  'PRIME_MOVER': { code: "PRIME_MOVER", desc: "PRIME MOVER" },
  'VAN_EV': { code: "VAN_EV", desc: "VAN - EV" },
  'VAN': { code: "VAN", desc: "VAN" },
};

export const ChassisTypes = {
  20: { code: "20FT", desc: "20FT Chassis" },
  40: { code: "40FT", desc: "40FT Chassis" },
};

export const ContractRequestStates = {
  DELETED: { code: "DELETED", desc: "Deleted", altCode: "DEL" },
  NEW_REQ: { code: "NEW_REQ", desc: "New Contract", altCode: "NEW" },
  NEW_SUBMITTED: {
    code: "NEW_SUBMITTED",
    desc: "New Contract Submitted",
    altCode: "SUB",
  },
  NEW_APPROVED: {
    code: "NEW_APPROVED",
    desc: "New Contract Approved",
    altCode: "APP",
  },
  NEW_REJECTED: {
    code: "NEW_REJECTED",
    desc: "New Contract Rejected",
    altCode: "REJ",
  },
  NEW_UPDATE: {
    code: "NEW_UPDATE",
    desc: "New Contract Update",
    altCode: "NEW",
  },
  UPDATE_SUBMITTED: {
    code: "UPDATE_SUBMITTED",
    desc: "Contract Update Submitted",
    altCode: "SUB",
  },
  UPDATE_APPROVED: {
    code: "UPDATE_APPROVED",
    desc: "Contract Update Approved",
    altCode: "APP",
  },
  UPDATE_REJECTED: {
    code: "UPDATE_REJECTED",
    desc: "Contract Update Rejected",
    altCode: "REJ",
  },
  RENEWAL_REQ: {
    code: "RENEWAL_REQ",
    desc: "Contract Renewal",
    altCode: "NEW",
  },
  RENEWAL_SUBMITTED: {
    code: "RENEWAL_SUBMITTED",
    desc: "Contract Renewal Submitted",
    altCode: "SUB",
  },
  RENEWAL_APPROVED: {
    code: "RENEWAL_APPROVED",
    desc: "Contract Renewal Approved",
    altCode: "APP",
  },
  RENEWAL_REJECTED: {
    code: "RENEWAL_REJECTED",
    desc: "Contract Renewal Rejected",
    altCode: "REJ",
  },
  EXPIRED: {
    code: "EXPIRED",
    desc: "Contract Request Expired",
    altCode: "EXP",
  },
  EXPORTED: {
    code: "EXPORTED",
    desc: "Contract Request Exported",
    altCode: "EX",
  },
};

export const AccountsProcessStates = {
  N: { code: "N", altCode: "NEW", desc: "New Account Registration" },
  D: { code: "D", altCode: "DEL", desc: "Deleted" },
  R: { code: "R", altCode: "SUB", desc: "Registration Submitted" },
  X: { code: "X", altCode: "REJ", desc: "Registration Rejected" },
  A: { code: "A", altCode: "APP", desc: "Active" },
  P: { code: "P", altCode: "SUB", desc: "Suspension Submitted" },
  S: { code: "S", altCode: "APP", desc: "Account Suspended" },
  V: { code: "V", altCode: "SUB", desc: "Termination Submitted" },
  T: { code: "T", altCode: "APP", desc: "Termination Approved" },
  Q: { code: "Q", altCode: "SUB", desc: "Resumption Submitted" },

  /* N = NEW (registration)
  D = Deleted
  R = REG_SUBMITTED
  X = REG_REJECTED
  A = APPROVED (for registration)
   
  P = SUS_SUBMITTED
  S = SUS_APPROVED
  A = SUS_REJECTED (goes back to A)
   
  V = TER_SUBMITTED
  T = TER_APPROVED
  A = TER_REJECTED (goes back to A) */
};

export const AccnProcessTypes = {
  ACCN_SUSPENSION: { code: "ACCN_SUSPENSION", desc: "Account Suspension" },
  ACCN_TERMINATION: { code: "ACCN_TERMINATION", desc: "Account Termination" },
  ACCN_REGISTRATION: {
    code: "ACCN_REGISTRATION",
    desc: "Account Registration",
  },
  ACCN_RESUMPTION: { code: "ACCN_RESUMPTION", desc: "Account Resumption" },
};

export const AccountRemarksType = [
  { id: "REG_APPROVE", desc: "REGISTRATION APPROVED" },
  { id: "REG_REJECT", desc: "REGISTRATION REJECTED" },
  { id: "REG_REQ", desc: "REGISTRATION REQUEST" },
  { id: "SUS_APPROVE", desc: "SUSPENSION APPROVED" },
  { id: "SUS_REJECT", desc: "SUSPENSION REJECTED" },
  { id: "SUS_REQ", desc: "SUSPENSION REQUEST" },
  { id: "TERM_APPROVE", desc: "TERMINATION APPROVED" },
  { id: "TERM_REJECT", desc: "TERMINATION REJECTED" },
  { id: "TERM_REQ", desc: "TERMINATION REQUEST" },
  { id: "RESUMPT_APPROVE", desc: "RESUMPTION APPROVED" },
  { id: "RESUMPT_REJECT", desc: "RESUMPTION REJECT" },
  { id: "RESUMPT_REQ", desc: "RESUMPTION REQUEST" },
];

export const DocumentVerifyStatus = {
  NOV: { code: "NOV", name: "Not Verified", desc: "Not Verified" },
  PEN: { code: "PEN", name: "Pending Return", desc: "Pending Return" },
  VER: { code: "VER", name: "Verified", desc: "Verified" },
};

export const PaymentState = {
  NEW: { code: "NEW", desc: "New", alt: "New" },
  PAID: { code: "PAID", desc: "Paid", alt: "Paid" },
  PAYING: { code: "PAYING", desc: "Paying", alt: "Paying" },
  CAN: { code: "CAN", desc: "Cancelled", alt: "Cancelled" },
  VER: { code: "VER", desc: "Verified", alt: "Verified" },
  APP: { code: "APP", desc: "Approved", alt: "Approved" },
};

export const TrackDeviceState = {
  NEW: { code: "NEW", desc: "New" },
  ACTIVATE: { code: "ACTIVATE", desc: "Activated" },
  DEACTIVATE: { code: "DEACTIVATE", desc: "Deactivated" },
};

export const TruckJobTypes = {
  TRUCKING_IN: { code: "TRKI", desc: "Trucking In" },
  TRUCKING_OUT: { code: "TRKO", desc: "Trucking Out" },
};

export const ServiceTypes = {
  CLICDEC: { code: "CLICDEC", desc: "ClicDeclare" },
  CLICDEPO: { code: "CLICDEPO", desc: "ClicDepot" },
  CLICDO: { code: "CLICDO", desc: "ClicDO" },
  CLICGP: { code: "CLICGP", desc: "ClicGatePass" },
  CLICTDS: { code: "CLICTDS", desc: "ClicTDS" },
  CLICTRUCK: { code: "CLICTRUCK", desc: "ClicTruck" },
};

export const JobStates = {
  ACP: { code: "ACP", desc: "Accepted" },
  APP: { code: "APP", desc: "Approved" },
  ASG: { code: "ASG", desc: "Assigned" },
  BILLED: { code: "BILLED", desc: "Billed" },
  CAN: { code: "CAN", desc: "Cancelled" },
  COM: { code: "COM", desc: "Completed" },
  CLM: { code: "CLM", desc: "Claimed" },
  CON: { code: "CON", desc: "Confirmed" },
  DEL: { code: "DEL", desc: "Deleted" },
  DLV: { code: "DLV", desc: "Delivered" },
  ONGOING: { code: "ONGOING", desc: "On-going" },
  DRF: { code: "DRF", desc: "Draft" },
  PROG: { code: "PROG", desc: "In Progress" },
  NEW: { code: "NEW", desc: "New" },
  PAID: { code: "PAID", desc: "Paid" },
  PMV: { code: "PMV", desc: "Payment Verified" },
  REJ: { code: "REJ", desc: "Rejected" },
  SUB: { code: "SUB", desc: "Submitted" },
  PYG: { code: "PYG", desc: "Paying" },
  PEN: { code: "PEN", desc: "Pending Payment" },
  STRTD: { code: "STRTD", desc: "Started" },
  VER: { code: "VER", desc: "Verified" },
  FAIL: { code: "FAIL", desc: "Failed" },
  VER_BILL: { code: "VER_BILL", desc: "Billing Verified" },
  APP_BILL: { code: "APP_BILL", desc: "Billing Approved" },
  REJ_BILL: { code: "REJ_BILL", desc: "Billing Rejected" },
  ACK_BILL: { code: "ACK_BILL", desc: "Billing Approved" },
  SUC: { code: "SUC", desc: "Success" },
  PAR: { code: "PAR", desc: "Partial" },
  PAUSED: { code: "PAUSED", desc: "Paused" },
};

export const CreditLimitUpdateStates = {
  NEW: { code: "NEW", desc: "New" },
  APP: { code: "APP", desc: "Approved" },
  SUB: { code: "SUB", desc: "Submitted" },
  DEL: { code: "DEL", desc: "Deleted" },
  REJ: { code: "REJ", desc: "Rejected" },
};

export const Actions = {
  STOP: { text: "Deliver", result: "Delivered" },
  START: { text: "Start", result: "Started" },
  CANCEL: { text: "Cancel", result: "Cancelled" },
  SUBMIT: { text: "Submit", result: "Submitted" },
  ACCEPT: { text: "Accept", result: "Accepted" },
  REJECT: { text: "Reject", result: "Rejected" },
  REJECT_BILL: { text: "Reject Billing", result: "Billing Rejected" },
  VERIFY_BILL: { text: "Verify Billing", result: "Billing Verified" },
  ACKNOWLEDGE_BILL: { text: "Approve Billing", result: "Billing Approved" },
  APPROVE_BILL: { text: "Approve Billing", result: "Billing Approved" },
  ASSIGN: { text: "Assign Driver/Truck", result: "Driver/Truck Assigned" },
  BILLJOB: { text: "Bill Job", result: "Job Billed" },
  WITHDRAW: { text: "Withdraw", result: "Withdrawn" },
  DELETE: { text: "Delete", result: "Deleted" },
  APPROVE: { text: "Approve", result: "Approved" },
  VERIFY: { text: "Verify", result: "Verified" },
  SUSPEND: { text: "Suspend", result: "Submitted for Suspension" },
  TERMINATE: { text: "Terminate", result: "Submitted for Termination" },
  RESUMPTION: { text: "Unsuspend", result: "Submitted for Resumption" },
};

export const StaticVAStates = {
  A: { code: "A", desc: "Active" },
  // I: { code: "I", desc: "InActive" },
};

export const MultiSelectJobOptions = {
  COFF: {
    NEW: [
      { title: "Submit Job", action: "SUBMIT" },
      { title: "Cancel Job", action: "DELETE" },
    ],
    SUB: [{ title: "Withdraw Job", action: "WITHDRAW" }],
    BILLED: [
      { title: "Verify Billing", action: "VERIFY_BILL" },
      { title: "Reject Billing", action: "REJECT_BILL" },
    ],
    VER_BILL: [
      { title: "Approve Billing", action: "APPROVE_BILL" },
      { title: "Reject Billing", action: "REJECT_BILL" },
    ],
  },
  TO: {
    SUB: [
      { title: "Accept Job", action: "ACCEPT" },
      { title: "Reject Job", action: "REJECT" },
    ],
    ACP: [{ title: "Assign Job", action: "ASSIGN" }],
    ASG: [{ title: "Start Job", action: "START" }],
    ONGOING: [{ title: "Finish Job", action: "STOP" }],
    DLV: [{ title: "Bill JOB", action: "BILLJOB" }],
  },
  CO_FINANCE: {
    BILLED: [
      { title: "Verify Billing", action: "VERIFY_BILL" },
      { title: "Reject Billing", action: "REJECT_BILL" },
    ],
    VER_BILL: [
      { title: "Approve Billing", action: "APPROVE_BILL" },
      { title: "Reject Billing", action: "REJECT_BILL" },
    ],
  },
  GLI: {
    VER: [
      { title: "Verify", action: "Verified" },
      { title: "Approve", action: "Approve" },
    ],
  },
};

export const DoStates = {
  ASG: { code: "ASG", desc: "Assigned" },
  CLM: { code: "CLM", desc: "Claimed" },
  EXP: { code: "EXP", desc: "Expired" },
  PAID: { code: "PAID", desc: "Paid" },
  RDY: { code: "RDY", desc: "Ready" },
  UNRDY: { code: "UNRDY", desc: "Unready" },
};

// Added Driver States
export const DriverStates = {
  ASSIGNED: { code: "ASSIGNED", desc: "Assigned" },
  MAINTENANCE: { code: "MAINTENANCE", desc: "Maintenance" },
  UNASSIGNED: { code: "UNASSIGNED", desc: "Unassigned" },
};

export const BlStates = {
  ASG: { id: "ASSIGNED", desc: "Assigned" },
  CAN: { id: "CANCELLED", desc: "Cancelled" },
  CLM: { id: "CLAIMED", desc: "Claimed" },
  CON: { id: "CONFIRMED", desc: "Confirmed" },
  INV: { id: "INVALID", desc: "Invalid" },
  NEW: { id: "NEW", desc: "New" },
  REJ: { id: "REJECTED", desc: "Rejected" },
  SUB: { id: "SUBMITTED", desc: "Submitted" },
};

export const DocumentTypes = {
  ACR: { code: "ACR", name: "ACCOUNT ACRA", desc: "Account ACRA" },
  BL: { code: "BL", name: "BILL OF LADING", desc: "Bill of Lading" },
  CGA: {
    code: "CGA",
    name: "CONTAINER GUARANTEE",
    desc: "Container Guarantee",
  },
  CLO: { code: "CLO", name: "COMPANY LOGO", desc: "Company Logo" },
  CUS: {
    code: "CUS",
    name: "COMPANY USER SIGNATURES",
    desc: "Company User Signatures",
  },
  ETI: { code: "ETI", name: "E-TAX INVOICE", desc: "e-Tax Invoice" },
  LAS: {
    code: "LAS",
    name: "LIST OF AUTHORISED SIGN",
    desc: "List of Authorised Sign",
  },
  LOA: {
    code: "LOA",
    name: "LETTER OF ASSIGNMENT",
    desc: "Letter of Assignment",
  },
  LOI: {
    code: "LOI",
    name: "LETTER OF INDEMNITY",
    desc: "Letter of Indemnity",
  },
  LOU: {
    code: "LOU",
    name: "LETTER OF UNDERTAKING",
    desc: "Letter of Undertaking",
  },
  PFI: { code: "PFI", name: "PROFORMA INVOICE", desc: "Proforma Invoice" },
  PHO: {
    code: "PHO",
    name: "USER PROFILE PICTURE",
    desc: "User Profile Picture",
  },
  POA: { code: "POA", name: "POWER OF AUTHORITY", desc: "Power of Authority" },
  SIG: { code: "SIG", name: "USER SIGNATURE", desc: "User Signature" },
  SK: { code: "SK", name: "SURAT KUASA", desc: "Surat Kuasa" },
  STP: { code: "STP", name: "COMPANY STAMP", desc: "Company Stamp" },
};

export const CommitteeAccount = {
  BRDRCUST: {
    code: "BRDRCUST",
    type: "ACC_TYPE_CUSTOMS",
    name: "PPAP Border Customs",
  },
  BRDRIMMN: {
    code: "BRDRIMMN",
    type: "ACC_TYPE_IMMIGRATION",
    name: "PPAP Border Immigration",
  },
  BRDRQRNT: {
    code: "BRDRQRNT",
    type: "ACC_TYPE_QUARANTINE",
    name: "PPAP Border Quarantine",
  },
  PASCUST: { code: "PASCUST", type: "ACC_TYPE_CUSTOMS", name: "PAS Customs" },
  PASIMMN: {
    code: "PASIMMN",
    type: "ACC_TYPE_IMMIGRATION",
    name: "PAS Immigration",
  },
  PASQRNT: {
    code: "PASQRNT",
    type: "ACC_TYPE_QUARANTINE",
    name: "PAS Quarantine",
  },
  PASPORT: {
    code: "PASPORT",
    type: "ACC_TYPE_PORT",
    name: "PAS Port Authority",
  },
  GDCE: {
    code: "GDCE",
    type: "ACC_TYPE_CUSTOMS",
    name: "General Department of Customs and Excise",
  },
  GDI: {
    code: "GDI",
    type: "ACC_TYPE_IMMIGRATION",
    name: "General Department of Immigration",
  },
  CDCD: {
    code: "CDCD",
    type: "ACC_TYPE_QUARANTINE",
    name: "Communicable Diseases Control Department",
  },
  MMD: { code: "MMD", type: "ACC_TYPE_MPWT", name: "MMD" },
  PPAPCUST: {
    code: "PPAPCUST",
    type: "ACC_TYPE_CUSTOMS",
    name: "PPAP Customs",
  },
  PPAPIMMN: {
    code: "PPAPIMMN",
    type: "ACC_TYPE_IMMIGRATION",
    name: "PPAP Immigration",
  },
  PPAPQRNT: {
    code: "PPAPQRNT",
    type: "ACC_TYPE_QUARANTINE",
    name: "PPAP Quarantine",
  },
  PPAPPORT: {
    code: "PPAPPORT",
    type: "ACC_TYPE_PORT",
    name: "PPAP Port Authority",
  },
};

export const VoyageTypes = {
  IN: { code: "INWARD", desc: "Inward" },
  OUT: { code: "OUTWARD", desc: "Outward" },
};

export const FeeTypes = {
  DO: { code: "PER DO", desc: "Per DO" },
  BL: { code: "PER BL", desc: "Per BL" },
};

export const PaymentTypes = {
  CLICPAY: { code: "CLICPAY", desc: "ClicPay" },
  CREDIT: { code: "CREDIT", desc: "Credit" },
};

export const ShipmentTypes = {
  EXPORT: { code: "EXPORT", desc: "EXPORT" },
  IMPORT: { code: "IMPORT", desc: "IMPORT" },
  DOMESTIC: { code: "DOMESTIC", desc: "DOMESTIC" },
};

export const BillingTypes = {
  IMMEDIATE: { code: "IMMDT", desc: "Immediate" },
  WEEKLY: { code: "WEEK", desc: "Weekly" },
  MONTHLY: { code: "MONTH", desc: "Monthly" },
};

export const ApplicationType = {
  VP: { code: "VP", desc: "Vessel Profile" },
  SR: { code: "SR", desc: "Ship Registration" },
  VC: { code: "VC", desc: "Vessel Call" },
  EP: { code: "EP", desc: "Entry Permit" },
  PAS: { code: "PAS", desc: "Ship Pre-Arrival Security Information Notice" },
  PO: { code: "PO", desc: "Pilot Order" },
  DOS: { code: "DOS", desc: "Declaration of Security" },
  SSCC: { code: "SSCC", desc: "Ship Sanitation Control Certificate" },
  SSCEC: {
    code: "SSCEC",
    desc: "Ship Sanitation Control Excemption Certificate",
  },
  PAN: { code: "PAN", desc: "Pre-Arrival Notice" },
  AD: { code: "AD", desc: "Arrival Declaration" },
  DD: { code: "DD", desc: "Departure Declaration" },
  ADSUB: { code: "ADSUB", desc: "Arrival Declaration" },
  DDSUB: { code: "DDSUB", desc: "Departure Declaration" },
  PAY: { code: "PAY", desc: "Payment" },
};

export const mainPort = {
  KHKOS: { code: "KHKOS", desc: "KHKOS- Sihanouville Port" },
  KHPNH: { code: "KHPNH", desc: "KHPNH- Phnom Penh Autonomous Port" },
};

export const ApplicationTypePath = {
  SR: { code: "SR", path: "/vessel/shipRegistration/view" },
  VC: { code: "VC", path: "/applications/vesselCall/view" },
  EP: { code: "EP", path: "/applications/entryPermit/view" },
  PAS: { code: "PAS", path: "/applications/cargoSecurityInfo/view" },
  PO: { code: "PO", path: "/applications/pilotOrder/view" },
  DOS: { code: "DOS", path: "/applications/dos/view" },
  PAN: { code: "PAN", path: "/applications/preArrivalNotice/view" },
  AD: { code: "AD", path: "/applications/arrivalDeclaration/view" },
  DD: { code: "DD", path: "/applications/departureDeclaration/view" },
  SSCC: { code: "SSCC", path: "/applications/sscec/sscc/view" },
  SSCEC: { code: "SSCEC", path: "/applications/sscec/sscc/view" },
  ADSUB: { code: "ADSUB", path: "/applications/arrivalDeclaration/view" },
  DDSUB: { code: "DDSUB", path: "/applications/departureDeclaration/view" },
};

export const RegistrationStatus = {
  EXPIRED: { code: "X", desc: "Expired" },
  PENDING_APPROVAL: { code: "P", desc: "Pending Approval" },
  PENDING_ACCCN_ACTIVATION: { code: "C", desc: "Pending Account Activation" },
  APPROVED: { code: "V", desc: "Approved" },
  REJECTED: { code: "R", desc: "Rejected" },
};

export const RecordStatus = {
  NEW: { code: "N", desc: "New" },
  ACTIVE: { code: "A", desc: "Active" },
  INACTIVE: { code: "I", desc: "InActive" },
  SUSPENDED: { code: "S", desc: "Suspended" },
  DEACTIVE: { code: "D", desc: "Deactivated" },
  VERIFIED: { code: "V", desc: "Verified" },
  SUBMITTED: { code: "S", desc: "Submitted" },
};

export const AccountStatus = {
  SUS_APPROVED: { code: "S", desc: "Suspension Approved" },
  TER_APPROVED: { code: "T", desc: "Termination Approved" },
  REG_SUBMITTED: { code: "R", desc: "Registration Submitted" },
  SUS_SUBMITTED: { code: "P", desc: "Suspension Submitted" },
  TER_SUBMITTED: { code: "V", desc: "Termination Submitted" },
  RESUMPT_SUBMITTED: { code: "Q", desc: "Resumption Submitted" },
};

export const TaxReportStatus = {
  ACTIVE: { code: "A", desc: "Active" },
  DOWNLOADED: { code: "D", desc: "Downloaded" },
};

export const TaxSequenceStatus = {
  ACTIVE: { code: "A", desc: "Active" },
  DELETED: { code: "D", desc: "Deleted" },
  EXPIRED: { code: "E", desc: "Expired" },
  INACTIVE: { code: "I", desc: "InActive" },
};

export const TaxInvoiceStatus = {
  COMPLETED: { code: "C", desc: "Completed" },
  EXPORTED: { code: "E", desc: "Exported" },
  NEW: { code: "N", desc: "New" },
};

export const AccountTypes = {
  ACC_TYPE_SL: { code: "ACC_TYPE_SL", desc: "SHIPPING LINE" },
  ACC_TYPE_FF: { code: "ACC_TYPE_FF", desc: "FREIGHT FORWARDER" },
  ACC_TYPE_FF_CO: { code: "ACC_TYPE_FF_CO", desc: "CARGO OWNER" },
  ACC_TYPE_CO: { code: "ACC_TYPE_CO", desc: "CARGO OWNER" },
  ACC_TYPE_CK: { code: "CLICKARGO", desc: "CLICKARGO" },
  ACC_TYPE_TO: { code: "ACC_TYPE_TO", desc: "TRUCK OPERATOR" },
  ACC_TYPE_SP: { code: "ACC_TYPE_SP", desc: "SERVICE PROVIDER" },
  ACC_TYPE_TO_WJ: {
    code: "ACC_TYPE_TO_WJ",
    desc: "TRUCK OPERATOR WITHOUT JOB",
  },
};

export const AccountTypesColumns = {
  ACC_TYPE_SHIP_LINE: { code: "ACC_TYPE_SHIP_LINE", desc: "SL" },
  ACC_TYPE_SHIP_AGENT: { code: "ACC_TYPE_SHIP_AGENT", desc: "SA" },
};

export const Applications = {
  CONE: { code: "CONE", desc: "CamelOne Portal" },
  CPEDI: { code: "CPEDI", desc: "Cambodia PortEDI" },
  MSW: { code: "MSW", desc: "Marinetime Single Window" },
};

export const ShipType = {
  CONTAINER: { code: "CONTAINER", desc: "Container" },
  CARGO: { code: "GENERAL_CARGO", desc: "General Cargo" },
  PASSENGER: { code: "PASSENGER_CRUISE", desc: "Passenger / Cruise" },
  TANKER: { code: "TANKER", desc: "Tanker" },
};

export const Roles = {
  SYS_SUPER_ADMIN: {
    code: "SYS_SUPER_ADMIN",
    desc: "Camel Portal Adminstrator",
  },
  OP_OFFICER: { code: "OP_ADMIN", desc: "OPERATIONS ADMIN OFFICER" },
  OFFICER: { code: "OFFICER", desc: "OFFICER" },
  FF_FINANCE: { code: "FF_FINANCE", desc: "FF FINANCE" },
  FINANCE: { code: "FINANCE", desc: "SHIPPING LINE FINANCE" },
  OPERATIONS: { code: "OPERATIONS", desc: "SHIPPING LINE DOC VERIFIER" },
  ADMIN: { code: "ADMIN", desc: "ACCOUNT ADMINISTRATOR" },
  FF_ADMIN: { code: "FF_ADMIN", desc: "Freight Forward ADMINISTRATOR" },
  FF_CO_ADMIN: { code: "FF_CO_ADMIN", desc: "FF-CO ADMINISTRATOR" },
  TO_OPERATION: { code: "TO_OPERATION", desc: "TRUCK OPERATOR OPERATION" },
  FINANCE_VERIFIER: { code: "SP_FIN_ADMIN", desc: "FINANCE ADMIN" },
  FINANCE_APPROVER: { code: "SP_FIN_HD", desc: "FINANCE HEAD" },
  SP_OP_ADMIN: {
    code: "SP_OP_ADMIN",
    desc: "SERVICE PROVIDER OPERATIONS ADMIN",
  },
  SP_L1: { code: "SP_L1", desc: "LEVEL 1 SUPPORT" },
  SP_COM: { code: "SP_COM", desc: "COMMERCIAL" },
  SP_BZ_HD: { code: "SP_BZ_HD", desc: "BUSINESS HEAD" },
  OP_ADMIN_WJ: { code: "OP_ADMIN_WJ", desc: "OPERATOR ADMIN WITHOUT JOB" },
};

export const TripType = {
  S: { code: "S", desc: "Single Trip" },
  M: { code: "M", desc: "Multi-Drop" },
  C: { code: "C", desc: "Child Multi-Drop" },
};

export const AdminOfficerRoles = [Roles.ADMIN.codes];
export const OfficerRoles = [Roles.OFFICER.code];
export const FinanceRoles = [Roles.FINANCE.code];
// export const OperationsRoles = [Roles.OPERATIONS.code];
// export const ToOperationRoles = [Roles.TO_OPERATION.code];

export const DashboardStatus = {
  SUBMITTED: "Submitted",
  PENDING_VERIFICATION: "Pending Verification",
  VERIFIED: "Pending Approval",
  APPROVED: "Pending Payment",
  ACTIVE: "Active",
  PAID: "Paid",
};

export const CreditTransactionTypes = {
  JOB_APPROVE: { code: "JOB_APPROVE", desc: "JOB APPROVE" },
  JOB_CANCEL: { code: "JOB_CANCEL", desc: "JOB CANCEL" },
  JOB_SUBMIT: { code: "JOB_SUBMIT", desc: "JOB SUBMIT" },
  JOB_PAYMENT: { code: "JOB_PAYMENT", desc: "JOB PAYMENT" },
  JOB_PAYMENT_APPROVE: {
    code: "JOB_PAYMENT_APPROVE",
    desc: "JOB PAYMENT APPROVE",
  },
  JOB_REJECT: { code: "JOB_REJECT", desc: "JOB REJECT" },
  JOB_SUBMIT_REIMBURSEMENT: {
    code: "JOB_SUBMIT_REIMBURSEMENT",
    desc: "JOB SUBMIT REIMBURSEMENT",
  },
  JOB_OPM_ACCEPT: { code: "JOB_OPM_ACCEPT", desc: "JOB ACCEPTED" },
};

export const MST_CTRY_URL =
  "/api/co/master/entity/country/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=ctyCode&iColumns=1";
export const MST_ACCN_TYPE_URL = "/api/co/master/entity/accnType";
export const MST_CURRENCY_URL =
  "/api/co/master/entity/currency/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=ccyCode&iColumns=1";
export const MST_CURRENCY = "/api/co/master/entity/currency";
export const MST_BANKS_URL = "/api/co/master/entity/bank";
export const MST_BANKS_BRANCH_URL = "/api/co/master/entity/bankbr";
export const MST_CONTACT_TYPE_URL = "/api/co/master/entity/contactType";
export const MST_UOM_URL = "/api/co/master/entity/uom";
export const MST_PORT_TYPE_URL = "/api/co/master/entity/portType";
export const MST_PORT = "/api/co/master/entity/port";
export const MST_PORT_KH =
  MST_PORT +
  "/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=portCode&iColumns=1&mDataProp_1=TMstCountry.ctyCode&sSearch_1=KH&mDataProp_2=portStatus&sSearch_2=A";
export const MST_PORT_TERMINAL_BY_PORT =
  "/api/co/pedi/entity/pediMstPortTerminal/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&iColumns=1&mDataProp_0=portTeminalId&mDataProp_2=portTeminalStatus&sSearch_2=A&mDataProp_1=mstPort.portCode&sSearch_1=";
export const MST_PORT_KHP =
  "/api/co/master/entity/port/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=portCode&iColumns=1&mDataProp_1=portCode&sSearch_1=KHP";
export const MST_PORT_MAIN =
  "/api/co/master/entity/port/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=portCode&iColumns=1&mDataProp_1=portDescription&sSearch_1=Autonomous";
export const MST_HS_CODE_TYPE_URL = "/api/co/master/entity/hsCodeType";
export const MST_ADDRESS_TYPE_URL = "/api/co/master/entity/addrType";
export const MST_HSCODE_TYPE_URL = "/api/co/master/entity/hsCodeType";
export const MST_HSCODE_URL = "/api/co/master/entity/hsCode";
export const MST_PROVINCE_URL = "/api/co/pedi/mst/entity/pediMstProvince";
export const MST_DUTY_PAID_AT_URL = "/api/co/pedi/mst/entity/pediMstDutyPaidAt";
export const MST_SHIP_TYPE_URL = "/api/co/pedi/mst/entity/pediMstShipType";
export const MST_THRUSTER_TYPE_URL =
  "/api/co/pedi/mst/entity/pediMstThrusterType";
export const MST_PAYMENT_TYPE_TABLE_URL =
  "/api/co/pedi/mst/entity/pediMstPaymentType";
export const MST_RECOVERY_APPLICATION_TABLE_URL = "/api/recoveryApplications";
export const MST_CREW_RANK = "/api/co/pedi/mst/entity/pediMstCrewRank";
export const MST_NUMBER_ENGINE_URL =
  "/api/co/pedi/mst/entity/pediMstNumberEngine";
export const MST_NUMBER_ENGINE =
  MST_NUMBER_ENGINE_URL +
  "/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=neSeq&iColumns=2&mDataProp_1=neStatus&sSearch_1=A";
export const PEDI_MST_ARTICLE_CAT_URL =
  "/api/co/pedi/mst/entity/pediMstArticleCat";
export const PEDI_MST_ARTICLE_CAT =
  PEDI_MST_ARTICLE_CAT_URL +
  "/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=arcCode&mDataProp_1=arcStatus&sSearch_1=A&mDataProp_2=arcCode&iColumns=1";

export const MST_PORT_TERMINAL_URL =
  "/api/co/pedi/mst/entity/pediMstPortTerminal";
export const MST_DOS_ACTIVITY_URL =
  "/api/co/pedi/mst/entity/pediMstDosActivity";
export const MST_AREAS_INSPECTED_URL =
  "/api/co/pedi/mst/entity/pediMstAreasInspected";
export const MST_APP_TYPE_URL = "/api/co/pedi/mst/entity/pediMstAppType";
export const MST_ACTIONS_URL = "/api/co/pedi/mst/entity/pediMstActions";
export const MST_SHIP_BARGE_URL = "/api/co/pedi/mst/entity/pediMstShipBarge";
export const MST_PEDI_NOTIFICATION_PREF_URL =
  "/api/co/pedi/mst/entity/pediSysNotifyPref";
export const MST_HAZARD_CLASS_URL =
  "/api/co/pedi/mst/entity/pediMstHazardClass";
export const PEDI_NOTIF_PREF_URL =
  "/api/pedi/notifications/preferences/template";
export const PEDI_NOTIF_PREF_APPTYPES_URL =
  "/api/pedi/notifications/preferences/appTypes";
export const PEDI_NOTIF_PREF_ACTIONS_BY_APPTYPES_URL = `/api/pedi/notifications/preferences/actions`;

export const CCM_ACCOUNT_ALL_URL = "/api/co/ccm/entity/accn";
export const CCM_ACCOUNT_ALL_SL_SA = "/api/pedi/manageaccn/slsa";

export const CCM_ACTIVE_ACCOUNT_ALL_URL =
  "/api/co/ccm/entity/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=accnId&iColumns=2&mDataProp_1=accnStatus&sSearch_1=A";
export const CCM_ACCOUNT_BY_TYPEID_URL =
  "/api/co/ccm/entity/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=accnName&iColumns=2&mDataProp_1=TMstAccnType.atypId&sSearch_1=";

export const ACCOUNT_BY_TYPEID_URL =
  "/api/v1/clickargo/manageaccn/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=accnDtCreate&mDataProp_1=history&sSearch_1=default&iColumns=3&mDataProp_2=TMstAccnType.atypId&sSearch_2=";

//&sSearch_1=${acctTypeId}
export const MST_ACCN_ATT_TYPE_OPTIONAL = `/api/v1/clickargo/master/ckMstAccnAttType/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=id.atId&iColumns=2&mDataProp_1=atStatus&sSearch_1=A&mDataProp_2=atMandatory&sSearch_2=N`;
export const MST_ACCN_ATT_TYPE_MANDATORY = `/api/v1/clickargo/master/ckMstAccnAttType/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=id.atId&iColumns=2&mDataProp_1=atStatus&sSearch_1=A&mDataProp_2=atMandatory&sSearch_2=Y`;

export const CCM_USER_ALL_URL = "/api/co/ccm/entity/usr";
export const CCM_USER_BY_ACCNID_URL =
  "api/co/ccm/entity/usr/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=usrUid&iColumns=2&mDataProp_1=TCoreAccn.accnId&sSearch_1=";
//&sSearch_1=${accnId}`
export const CCM_USER_BY_USERID_URL =
  "/api/co/ccm/entity/usr/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=usrDtCreate&iColumns=2&mDataProp_1=usrUid&sSearch_1=";
//&sSearch_1=${usrUid}
export const CCM_USER_APP_CODE_URL =
  "api/co/ccm/entity/usrApp/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=id.uappUid&iColumns=2&mDataProp_1=id.uappUid&sSearch_1=";
export const CCM_GROUP_BY_ACCNID_URL =
  "/api/co/ccm/entity/group/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=id.grpAccnid&iColumns=2&mDataProp_1=id.grpAccnid&sSearch_1=";
//&sSearch_1=${grpAccnid}
export const CCM_APPS_CODE_URL = "/api/co/ccm/entity/apps";
export const CCM_MINISTRY_URL =
  "/api/co/ccm/entity/ministry/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=minCode&iColumns=1";
export const CCM_AGENCY_URL =
  "/api/co/ccm/entity/agency/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=agyCode&iColumns=1";
export const ATTACH_TYPE =
  "/api/co/master/entity/attType/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=mattName&iColumns=1";
export const GET_ATT_TYPE_BY_ID = "/api/co/master/entity/attType/";

export const CAC_ROLE_BY_APP_CODE_URL =
  "api/co/cac/entity/role/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=id.roleId&iColumns=2&mDataProp_1=id.roleAppscode&sSearch_1=";
export const CAC_URITYPE_URL = "/api/co/cac/entity/uriType";
export const CAC_PORTAL_URI_URL = "/api/co/cac/portaluri/";
export const CAC_PERMISSION_TYPE_URL = "/api/co/cac/entity/permissionType";
export const CAC_PERMISSIONS_BY_APPCODE_URL =
  "api/co/cac/entity/permission/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=permId&iColumns=2&mDataProp_1=appsCode&sSearch_1=";

export const CAN_NOTIFICATION_CHANNEL_TYPE_URL =
  "/api/co/can/entity/notificationChannel";
export const CAN_NOTIFICATION_CONTENT_TYPE_URL =
  "/api/co/can/entity/notificationContent";
export const CAN_NOTIFICATION_DEVICE_URL =
  "/api/co/can/entity/notificationDevice";
export const CAN_NOTIFICATION_TEMPLATE_URL =
  "/api/co/can/entity/notificationTemplate";

export const ANNOUNCEMENT_TYPE_URL = "/api/co/anncmt/entity/anncmtType";

export const COMMON_SYSPARAM_URL = "/api/co/common/entity/sysparam";
export const COMMON_ATTACH_LIST_BY_REFID_URL =
  "/api/co/common/entity/attach/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=attSeq&mDataProp_1=attReferenceid&iColumns=2&sSearch_1=";

export const COMMON_ATTACH_LIST_BY_ACCNID_URL =
  "/api/v1/clickargo/attachments/accnAtt/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&iColumns=3&mDataProp_0=atDtCreate&mDataProp_1=atStatus&sSearch_1=A&mDataProp_2=TCoreAccn.accnId&sSearch_2=";

export const auditTab = [{ text: "common:recordDetails", icon: <FileCopy /> }];

export const commonTabs = [
  { text: "common:recordDetails", icon: <FileCopy /> },
  { text: "common:properties.title", icon: <Assignment /> },
  { text: "common:audits.title", icon: <Schedule /> },
];

export const registerTabs = [
  { text: "common:register.tabs.details.title", icon: <FileCopy /> },
  { text: "common:register.tabs.admin.title", icon: <Assignment /> },
  { text: "Services", icon: <Build /> },
  { text: "common:register.tabs.suppDocs.title", icon: <Assignment /> },
];

//@deprecated
export const tabList = [
  { text: "Record Details", icon: <FileCopy /> },
  { text: "Properties", icon: <Assignment /> },
  { text: "Audits", icon: <Icon>schedule</Icon> },
];

export const APPROVED_SR_SHIPPINGLINE_URL = "/api/app/sr/sl";
export const APPROVED_SR_VESSELBYSL_URL = "/api/app/sr/vessel/";

export const PEDI_MST_VOYAGE_TYPE_URL = "/api/pedi/mst/voyageTypes";
export const PEDI_MST_SHIP_TYPE_URL =
  "/api/co/pedi/mst/entity/pediMstShipType/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=shipTypeId&iColumns=1&mDataProp_1=shipTypeStatus&sSearch_1=A";
export const PEDI_MST_CARGO_TYPE_URL =
  "/api/co/pedi/mst/entity/pediMstCargoType/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=cargoTypeId&iColumns=1&mDataProp_1=cargoTypeStatus&sSearch_1=A";
export const PEDI_MST_DUTY_PAID_AT_URL = "/api/pedi/mst/dutyPaidAtList";
export const VESSEL_DROP_DOWN = "/api/app/sr/vessel/list/";
export const PEDI_MST_SHIP_TYPE = "/api/co/pedi/mst/entity/pediMstShipType";
export const PEDI_MST_GENDER = "/api/pedi/mst/gender";
export const PEDI_SEC_LEVEL = "/api/pedi/mst/secLevel/";
export const PEDI_MST_CREW_RANK = "/api/pedi/mst/crewRankList";
export const PEDI_MST_THRUSTER_TYPE = "/api/pedi/mst/thrusterType";
export const PEDI_MST_IDENTITY_DOC_TYPE = "/api/pedi/mst/identityDocTypes";
export const MST_PORT_BY_COUNTRY = "/api/pedi/mst/portsByCountry/";
export const MST_CUSS_OFF_TYPE = "/api/pedi/mst/cusOffice";
export const MST_AREAS_INSPECTED = "/api/pedi/mst/areasInspected";
export const MST_APP_FEE_CONFIG = "/api/co/pedi/entity/appFeeConfig";
export const PEDI_MST_BORDER_GATE = "/api/pedi/mst/borderGate";
export const PEDI_MST_EFF_TYPE = "/api/pedi/mst/crewEff";

export const MANIFEST_BILL_NATURE = "/api/pedi/mst/manifest/nature";
export const MANIFEST_BILL_TYPE = "/api/pedi/mst/manifest/type";
export const MANIFEST_MANIFEST_TYPE = "/api/pedi/mst/manifestType";
export const MANIFEST_MANIFEST_TRANSPORT_MODE = "/api/pedi/mst/transportMode";
export const MANIFEST_PACKAGE_TYPE = "/api/pedi/mst/manifest/pkgType";
export const MANIFEST_CONTAINER_TYPE = "/api/pedi/mst/manifest/containerType";
export const MANIFEST_CONTAINER_SIZE = "/api/pedi/mst/manifest/containerSize";

export const MST_DOC_CATEGORY = "/api/co/pedi/mst/entity/pediMstDocCategory";
export const PEDI_ACCN_APPTYPE_ASSOC = "/api/app/report/misc/accn/assoc/";

export const MASTER_CONTAINER_CAT = "/api/pedi/mst/contCat/:contType";
export const MASTER_CONTAINER_CAT_DETAILS =
  "/api/pedi/mst/contCat/:contType/:contcatCode";
export const MASTER_CONTAINER_TYPE = "/api/pedi/mst/containerType";

export const nilOrManifestOptions = [
  { value: "NIL", desc: "NIL" },
  { value: "MANIFEST", desc: "As per Manifest" },
];

export const PEDI_MANAGE_ACCN_lIST_URL = "/api/pedi/manageaccn/list";

export const MAX_FILE_SIZE = 10; //10M
export const ALLOWED_FILE_EXTS = [
  "pdf",
  "csv",
  "xlsx",
  "xls",
  "doc",
  "docx",
  "jpeg",
  "jpg",
  "png",
  "ppt",
];
export const isCompress = false; // don't compress,

export const CK_MST_SHIPMENT_TYPE =
  "/api/v1/clickargo/master/ckMstShipmentType";
export const CK_SVC_AUTH_PARTIES =
  "/api/v1/clickargo/clicSvc/ckSvcAuth/authParties";
export const CK_ACCOUNT_SHIPPING_LINE_ACCN_TYPE =
  "/api/co/ccm/entity/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=accnName&iColumns=2&mDataProp_1=TMstAccnType.atypId&sSearch_1=ACC_TYPE_SL";
export const CK_ACCOUNT_FF_ACCN_TYPE =
  "/api/co/ccm/entity/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=accnName&iColumns=2&mDataProp_1=TMstAccnType.atypId&sSearch_1=ACC_TYPE_FF";
export const CK_ACCOUNT_CO_ACCN_TYPE =
  "/api/co/ccm/entity/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=accnName&iColumns=2&mDataProp_1=TMstAccnType.atypId&sSearch_1=ACC_TYPE_CO";
export const CK_ACCOUNT_TO_ACCN_TYPE =
  "/api/co/ccm/entity/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=accnName&iColumns=2&mDataProp_1=TMstAccnType.atypId&sSearch_1=ACC_TYPE_TO";
export const CK_ACCOUNT_CO_FF_ACCN_TYPE =
  "/api/co/ccm/entity/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=accnName&iColumns=2&mDataProp_1=TMstAccnType.atypId&sSearch_1=ACC_TYPE_CO,ACC_TYPE_FF";
export const CK_ACTIVE_COFF_ACCN =
  CK_ACCOUNT_CO_FF_ACCN_TYPE + "&mDataProp_2=accnStatus&sSearch_2=A";
export const CK_ACTIVE_ACCOUNT_TO_ACCN =
  CK_ACCOUNT_TO_ACCN_TYPE + "&mDataProp_2=accnStatus&sSearch_2=A";

//for listing opm
export const CK_ACCN_OPM =
  "/api/v1/clickargo/clictruck/administrator/ckAccountExtOpm/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=accnName&iColumns=2&mDataProp_1=accnStatus&sSearch_1=A";
export const CK_ACTIVE_COFF_ACCN_OPM =
  CK_ACCN_OPM +
  "&mDataProp_2=TMstAccnType.atypId&sSearch_2=ACC_TYPE_CO,ACC_TYPE_FF";

export const CK_ACTIVE_TO_ACCN_OPM =
  CK_ACCN_OPM + "&mDataProp_2=TMstAccnType.atypId&sSearch_2=ACC_TYPE_TO";

export const CK_MST_PORT_ID =
  "/api/co/master/entity/port/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=portCode&iColumns=1&mDataProp_1=TMstCountry.ctyCode&sSearch_1=ID&mDataProp_2=portStatus&sSearch_2=A";

export const CK_CT_LOCATION =
  "/api/v1/clickargo/clictruck/administrator/location/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=locId&iColumns=1&mDataProp_1=locStatus&sSearch_1=A";

export const CK_MST_VEH_TYPE = "/api/v1/clickargo/clictruck/master/veh-type";
export const CK_MST_CHASSIS_TYPE =
  "/api/v1/clickargo/clictruck/master/chassis-type";
export const CK_CT_MST_CHASSIS =
  "/api/v1/clickargo/clictruck/administrator/chassis/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&iColumns=1&mDataProp_0=chsId&mDataProp_1=chsStatus&sSearch_1=A";
export const CK_MST_LOCATION_TYPE =
  "/api/v1/clickargo/clictruck/master/location-type";
export const T_CK_CT_VEH =
  "/api/v1/clickargo/clictruck/administrator/vehicle/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=vhPlateNo";
export const T_CK_CT_VEH_DROPDOWN =
    "/api/v1/clickargo/clictruck/administrator/vehicle/list?sEcho=3&iDisplayStart=0&iDisplayLength=200&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=vhPlateNo";
export const T_CK_CT_DRV =
  "/api/v1/clickargo/clictruck/administrator/driver/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=drvName";
// export const T_CK_CT_DRV = "/api/v1/clickargo/clictruck/admin/driver/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=accnName&iColumns=2&mDataProp_1=&sSearch_1=";

export const TRUCK_OPERATORS_BY_RATE_TABLE_URL =
  "/api/v1/clickargo/clictruck/selectOptions/truckOperators";

export const TRUCK_OPERATORS_BY_CONTRACT_WITH_CO_ACCNID =
  "/api/v1/clickargo/clictruck/administrator/contract/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=conId&mDataProp_1=TCoreAccnByConCoFf.accnId&sSearch_1=A&mDataProp_2=TCoreAccnByConCoFf.accnId&sSearch_2=";

// endpoint for list truck operator based on valid contract using principal coff
export const TRUCK_OPERATORS_CONTRACT_VALIDITY =
  "/api/v1/clickargo/clictruck/administrator/truckoperators";

export const CK_MST_CONTAINER_TYPE =
  "/api/v1/clickargo/clictruck/master/container-type";
export const CK_MST_GOODS_TYPE = "/api/v1/clickargo/clictruck/master/good-type";
export const CK_MST_CONTAINER_LOAD =
  "/api/v1/clickargo/clictruck/master/container-load";
export const CK_MST_CARGO_TYPES =
  "/api/v1/clickargo/clictruck/master/good-type";
export const TRIP_ATTACH_TYPE =
  "/api/v1/clickargo/clictruck/master/tripAttType";
export const CK_MST_REIMBURSE_TYPE =
  "api/v1/clickargo/clictruck/master/reimburse-type/active";

export const CK_CT_DISBURSE =
  "/api/v1/clickargo/clictruck/truck/reimburse/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=trDtCreate&iColumns=2";

export const CK_CT_TERMINATION_JOB_LIST =
  "/api/v1/clickargo/clictruck/job/truck/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=jobDtCreate&mDataProp_1=jobOutPaymentState&sSearch_1=NEW,PENDING&iColumns=4&mDataProp_2=TCkJob.TCkMstJobState.jbstId&sSearch_2=ACP,APP,ASG,BILLED,CON,CLM,COM,DLV,ONGOING,PENDING,PYG,PMV,PROG,REJ,STRTD,SUB,VER,VER_BILL,APP_BILL,ACK_BILL,REJ_BILL&mDataProp_3=TCoreAccnByJobPartyCoFf.accnId&sSearch_3=";
export const CK_CT_TERMINATION_JOB_TERM_LIST =
  "/api/v1/clickargo/clictruck/job/jobTerm/list?sEcho=3&iDisplayStart=0&iDisplayLength=100&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=jtDtCreate&mDataProp_1=TCkCtJobTermReq.jtrId&iColumns=2&sSearch_1=";
// &mDataProp_1=TckCtTrip.trId&sSearch_1=CTTR10980846196089570

export const CK_CT_FFCO_ACCN =
  "/api/v1/clickargo/clictruck/selectOptions/getFfCoFilteredByFf";

export const DashboardTypes = {
  TRACKING: { code: "TRACKING", desc: "Tracking" },
  TRUCK_JOBS: { code: "TRUCK_JOBS", desc: "Trucking Jobs" },
  BILLED_JOBS: { code: "BILLED_JOBS", desc: "Billed Jobs" },
  JOB_BILLING: { code: "JOB_BILLING", desc: "Job Billing" },
  VERIFIED_JOBS: { code: "VERIFIED_JOBS", desc: "Verified Jobs" },
  APPROVED_JOBS: { code: "APPROVED_JOBS", desc: "Approved Jobs" },
  PENDING_OUT_PAYMENTS: {
    code: "PENDING_OUT_PAYMENTS",
    desc: "Pending Payments",
  },
  JOB_PAYMENTS: { code: "JOB_PAYMENTS", desc: "Job Payments" },
  SEQUENCE: { code: "SEQUENCE", desc: "Tax Sequence" },
  REPORTS: { code: "REPORTS", desc: "Tax Report" },
  INVOICES: { code: "INVOICES", desc: "Tax Invoices" },
  SUSPENSION: { code: "SUSPENSION", desc: "Account Suspension" },
  RESUMPTION: { code: "RESUMPTION", desc: "Account Resumption" },
  DOCUMENT_VERIFICATIONS: {
    code: "DOCUMENT_VERIFICATIONS",
    desc: "Document Verifications",
  },
  DRIVER_AVAILABILITY: {
    code: "DRIVER_AVAILABILITY",
    desc: "Driver Availability",
  },
  TRUCK_RENTAL: {
    code: "TRUCK_RENTAL",
    desc: "Rentals",
  },
  LEASE_APPLICATION: {
    code: "LEASE_APPLICATION",
    desc: "Lease Applications",
  },
  TRUCK_TRACKING: { code: "TRUCK_TRACKING", desc: "Truck Tracking" },
};

export const FINANCING_OPTIONS = {
  OC: { code: "OC", desc: "Cargo Owner OPM" },
  OT: { code: "OT", desc: "Trucking Operator OPM" },
  OPM: { code: "OPM", desc: "Other People Money" },
  NF: { code: "NF", desc: "Non-Financing" },
  BC: { code: "BC", desc: "Balance Sheet Financing" },
};

export const FINANCING_MODELS = [
  { code: "OPM_OC", desc: "Cargo Owner OPM" },
  { code: "OPM_OT", desc: "Trucking Operator OPM" },
  { code: "BSF", desc: "Balance Sheet Financing" },
];

export const STATIC_RENTAL_DAS = [
  {
    id: 1,
    dbType: "TRUCK_RENTAL",
    title: "Rentals",
    transStatistic: {
      PROVIDERS: 0,
    },
    accnType: "ACC_TYPE_TO",
    image: null,
  },
  {
    id: 2,
    dbType: "LEASE_APPLICATION",
    title: "Lease Application",
    transStatistic: {},
    accnType: "ACC_TYPE_TO",
    image: null,
  },
];
export const getColorFromCode = (code) => {
  switch (code) {
    case "0":
      //BLACK
      return "0";
    case "1":
      //RED
      return "1";
    case "2":
      //GREEN
      return "2";
    case "3":
      //BLUE
      return "3";
    case "4":
      //YELLOW
      return "4";
    case "5":
      //ORANGE
      return "5";
    case "6":
      //PURPLE
      return "6";
    case "7":
      //WHITE
      return "7";
    case "8":
      //CYAN
      return "8";
    case "9":
      //GREY
      return "9";
    default:
      return null;
  }
};

export const notificationType = [
  { text: "common:recordDetails", icon: <FileCopy /> },
  { text: "common:audits.title", icon: <Schedule /> },
];

export const jobSubType = {
  LOCAL : { code: "LOCAL", desc: "Local" },
  IMPORT : { code: "IMPORT", desc: "Import" },
  EXPORT : { code: "EXPORT", desc: "Export" },
}
export const jobLoading = {
  FTL : { code: "FTL", desc: "FTL" },
  LTL : { code: "LTL", desc: "LTL" }
}
