import React, { useEffect, useState } from "react";
import { Grid, MenuItem } from "@material-ui/core";
import { BorderColorOutlined } from "@material-ui/icons";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import { customFilterDateDisplay } from "app/c1utils/utility";
import moment from "moment";
import VehicleMaintenance from "../popups/VehMaintenance";
import { RecordStatus } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { expiryType, monitorBy, notifyMethod } from "./vehextconst";
import { deepUpdateState } from "app/c1utils/stateUtils";

const VextMlogUrl = `/api/v1/clickargo/clictruck/administrator/vehMlog`;

export default function VehExt(props) {
  const { inputData, errors, setVextInputData, isDisabled, viewType } =
    props?.props;

  const [buttonADD, setButtonADD] = useState(false);
  const [popUp, setPopUp] = useState(false);

  //state for maintenance category
  const [vextMaintenance, setVextMaintenance] = useState({
    ...inputData?.maintenance,

    id: {
      ...inputData?.maintenance?.id,
      vextParam: "VEHICLE_MAINTENANCE",
    },
  });
  const [maintenanceNotifyBy, setMaintenanceNotifyBy] = useState({
    disable: inputData?.maintenance?.vextValue
      ? false
      : isDisabled || viewType === "new",
    type: inputData?.maintenance?.vextNotifyEmail
      ? "vextNotifyEmail"
      : inputData?.maintenance?.vextNotifyWhatsapp
      ? "vextNotifyWhatsapp"
      : null,
  });
  const [maintenanceMonitorBy, setMaintenanceMonitoryBy] = useState({
    disable: inputData?.maintenance?.vextValue
      ? false
      : isDisabled || viewType === "new",
  });

  //state for expiry category
  const [vextExpiry, setVextExpiry] = useState({ ...inputData?.expiry });
  const [expNotifyBy, setExpNotifyBy] = useState({
    disable: inputData?.expiry?.vextValue
      ? false
      : isDisabled || viewType === "new",
    type: inputData?.expiry?.vextNotifyEmail
      ? "vextNotifyEmail"
      : inputData?.expiry?.vextNotifyWhatsapp
      ? "vextNotifyWhatsapp"
      : null,
  });
  const [expMonitorBy, setExpMonitorBy] = useState({
    disable: inputData?.expiry?.vextValue
      ? false
      : isDisabled || viewType === "new",
  });

  const mlogDataInit = {
    action: "edit",
    vmlId: null,
  };

  const [mlogData, setMlogData] = useState(mlogDataInit);
  const [mlogTableRefresh, setMlogTableRefresh] = useState(false);

  function mlogPopUpHandler(action, id) {
    action = action ?? (id ? "view" : "new");

    setMlogData((p) => ({ ...p, action: action, vmlId: id }));
    if (popUp) {
      setMlogTableRefresh(true);
    }
    setPopUp((p) => (p ? false : true));
  }

  const mLogColumns = [
    {
      name: "vmlDtStart",
      label: "Start",
      options: {
        customBodyRender: (value, tableMeta, updateValue) => {
          return moment(value).format("DD/MM/YYYY");
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "vmlDtEnd",
      label: "End",
      options: {
        customBodyRender: (value, tableMeta, updateValue) => {
          return moment(value).format("DD/MM/YYYY");
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "vmlCost",
      label: "Cost",
    },
    {
      name: "vmlStatus",
      label: "Status",
      options: {
        filter: true,
        display: true,
        filterType: "dropdown",
        filterOptions: {
          names: [RecordStatus.ACTIVE.code, RecordStatus.INACTIVE.code],
          renderValue: (v) => {
            switch (v) {
              case RecordStatus.ACTIVE.code:
                return RecordStatus.ACTIVE.desc;
              case RecordStatus.INACTIVE.code:
                return RecordStatus.INACTIVE.desc;
              default:
                break;
            }
          },
        },
        customFilterListOptions: {
          render: (v) => {
            switch (v) {
              case RecordStatus.ACTIVE.code:
                return RecordStatus.ACTIVE.desc;
              case RecordStatus.INACTIVE.code:
                return RecordStatus.INACTIVE.desc;
              default:
                break;
            }
          },
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          return getStatusDesc(value);
        },
      },
    },
    {
      name: "vmlId",
      label: "Action",
      options: {
        filter: false,
        viewColumns: false,
        customBodyRender: (value, tableMeta, updateValue) => {
          return (
            <C1DataTableActions
              viewDocumentEventhandler={() => {
                mlogPopUpHandler("view", value);
              }}
              editEventHandler={() => {
                mlogPopUpHandler("edit", value);
              }}
            />
          );
        },
      },
    },
  ];

  const handleMaintenanceDueDate = (name, e) => {
    console.log("name", name);
    let dueDate = moment(e).format("YYYY-MM-DD");
    setVextMaintenance({
      ...vextMaintenance,
      vextValue: e,
    });
    //enable the associated fields
    setMaintenanceMonitoryBy({ ...maintenanceMonitorBy, disable: false });
    setMaintenanceNotifyBy({ ...maintenanceNotifyBy, disable: false });
  };

  const handleExpiryDueDate = (name, e) => {
    let dueDate = moment(e).format("YYYY-MM-DD");
    setVextExpiry({
      ...vextExpiry,
      vextValue: e,
    });
    //enable the associated fields
    setExpMonitorBy({ ...expMonitorBy, disable: false });
    setExpNotifyBy({ ...expNotifyBy, disable: false });
  };

  function handleInputChange(e, fieldName) {
    const { value, name } = e?.target;

    console.log("fieldName", fieldName);
    switch (fieldName) {
      case "maintenanceNotifyBy":
        setVextMaintenance({
          ...vextMaintenance,
          vextNotify: "Y",
          vextNotifyEmail:
            value === "vextNotifyEmail"
              ? vextMaintenance?.vextNotifyEmail
              : null,
          vextNotifyWhatsapp:
            value === "vextNotifyWhatsapp"
              ? vextMaintenance?.vextNotifyWhatsapp
              : null,
        });

        setMaintenanceNotifyBy({ ...maintenanceNotifyBy, type: value });
        break;
      case "maintenanceMonitorBy":
        setVextMaintenance({
          ...vextMaintenance,
          vextMonitorMthd: value,
        });
        setMaintenanceMonitoryBy({ ...maintenanceMonitorBy, type: value });
        break;
      case "expiryType":
        setVextExpiry({
          ...vextExpiry,
          id: { ...vextExpiry?.id, vextParam: value },
        });
        break;
      case "expiryNotifyBy":
        setVextExpiry({
          ...vextExpiry,
          vextNotify: "Y",
          vextNotifyEmail:
            value === "vextNotifyEmail" ? vextExpiry?.vextNotifyEmail : null,
          vextNotifyWhatsapp:
            value === "vextNotifyWhatsapp"
              ? vextExpiry?.vextNotifyWhatsapp
              : null,
        });

        setExpNotifyBy({ ...expNotifyBy, type: value });
        break;
      case "expiryMonitorBy":
        setVextExpiry({
          ...vextExpiry,
          vextMonitorMthd: value,
        });
        setExpMonitorBy({ ...expMonitorBy, type: value });
        break;
      case `EXP_${expNotifyBy?.type}`:
        setVextExpiry({
          ...vextExpiry,
          ...deepUpdateState(vextExpiry, expNotifyBy?.type, value),
        });
        break;
      case `EXP_${expMonitorBy?.type}`:
        setVextExpiry({
          ...vextExpiry,
          ...deepUpdateState(vextExpiry, "vextMonitorValue", value),
        });
        break;
      default:
        setVextMaintenance({
          ...vextMaintenance,
          ...deepUpdateState(vextMaintenance, name, value),
        });
        break;
    }
  }

  useEffect(() => {
    const exec = setTimeout(() => {
      setVextInputData((p) => ({
        ...p,
        expiry: vextExpiry?.id?.vextParam === null ? null : vextExpiry,
        maintenance: vextMaintenance,
      }));
    }, 700);

    return () => clearTimeout(exec);
  }, [vextExpiry, vextMaintenance]);

  useEffect(() => {
    return () => setMlogTableRefresh(false);
  }, [mlogTableRefresh]);

  return (
    <>
      <C1CategoryBlock icon={<BorderColorOutlined />} title={"Maintenance"}>
        <Grid container alignItems="center" spacing={3}>
          <Grid item xs={12}>
            <C1DateField
              name="maintenanceDueDate"
              label="Due Date"
              onChange={handleMaintenanceDueDate}
              value={vextMaintenance?.vextValue || ""}
              disabled={isDisabled}
            />
            <Grid container alignItems="center" spacing={1}>
              <Grid item xs={5}>
                <C1SelectField
                  name="maintenanceNotifyBy"
                  label="Notify By"
                  required={true}
                  optionsMenuItemArr={notifyMethod?.map((item, idx) => {
                    return (
                      <MenuItem value={item?.id} key={idx}>
                        {item?.desc}
                      </MenuItem>
                    );
                  })}
                  value={maintenanceNotifyBy?.type}
                  onChange={(e) => handleInputChange(e, "maintenanceNotifyBy")}
                  disabled={isDisabled || maintenanceNotifyBy?.disable}
                  helperText={(errors && errors["MNT_notifyBy"]) || ""}
                  error={errors && errors["MNT_notifyBy"]}
                />
              </Grid>
              <Grid item xs={7}>
                <C1InputField
                  name={maintenanceNotifyBy?.type}
                  label=""
                  disabled={isDisabled || maintenanceNotifyBy?.disable}
                  onChange={handleInputChange}
                  value={vextMaintenance[maintenanceNotifyBy?.type]}
                  helperText={
                    errors &&
                    (errors["MNT_" + maintenanceNotifyBy?.type] ||
                      errors["MNT_notifyBy"])
                  }
                  error={
                    errors &&
                    (errors["MNT_" + maintenanceNotifyBy?.type] ||
                      errors["MNT_notifyBy"])
                  }
                />
              </Grid>
            </Grid>
            <Grid container alignItems="center" spacing={1}>
              <Grid item xs={5}>
                <C1SelectField
                  name="maintenanceMonitorBy"
                  label="Monitor By"
                  required={true}
                  optionsMenuItemArr={monitorBy?.map((item, idx) => {
                    return (
                      <MenuItem value={item?.id} key={idx}>
                        {item?.desc}
                      </MenuItem>
                    );
                  })}
                  onChange={(e) => handleInputChange(e, "maintenanceMonitorBy")}
                  value={vextMaintenance?.vextMonitorMthd || ""}
                  disabled={isDisabled || maintenanceMonitorBy?.disable}
                  helperText={(errors && errors["MNT_vextMonitorMthd"]) || ""}
                  error={errors && errors["MNT_vextMonitorMthd"]}
                />
              </Grid>
              <Grid item xs={7}>
                <C1InputField
                  name="vextMonitorValue"
                  label=""
                  type="number"
                  disabled={isDisabled || maintenanceMonitorBy?.disable}
                  onChange={handleInputChange}
                  value={vextMaintenance?.vextMonitorValue || ""}
                  helperText={(errors && errors["MNT_vextMonitorValue"]) || ""}
                  error={errors && errors["MNT_vextMonitorValue"]}
                />
              </Grid>
            </Grid>
          </Grid>
          <Grid item xs={12} style={{ marginBottom: "20px" }}>
            {inputData.vhId && (
              <C1DataTable
                url={VextMlogUrl}
                filterBy={[
                  { attribute: "tckCtVeh.vhId", value: inputData.vhId },
                ]}
                columns={mLogColumns}
                defaultOrder="vmlDtLupd"
                defaultOrderDirection="desc"
                isRefresh={mlogTableRefresh}
                isShowDownload={false}
                isShowPrint={false}
                isShowViewColumns={!isDisabled}
                isShowFilter={!isDisabled}
                showAdd={
                  isDisabled
                    ? null
                    : buttonADD
                    ? { type: "popUp", popUpHandler: (e) => mlogPopUpHandler() }
                    : null
                }
                isShowPagination={false}
              />
            )}
          </Grid>
        </Grid>
      </C1CategoryBlock>

      {/* ------------------------- EXPIRY ------------------------- */}
      <C1CategoryBlock icon={<BorderColorOutlined />} title={"Expiry Dates"}>
        <Grid container alignItems="center" spacing={3}>
          <Grid item xs={12}>
            <C1SelectField
              name="expiryType"
              label="Type"
              onChange={(e) => handleInputChange(e, "expiryType")}
              value={vextExpiry?.id?.vextParam || ""}
              disabled={isDisabled}
              optionsMenuItemArr={expiryType?.map((item, idx) => {
                return (
                  <MenuItem value={item?.id} key={idx}>
                    {item?.desc}
                  </MenuItem>
                );
              })}
            />
            <C1DateField
              name="expiryDate"
              label="Expiry Date"
              onChange={handleExpiryDueDate}
              value={vextExpiry?.vextValue || ""}
              disabled={isDisabled}
            />
            <Grid container alignItems="center" spacing={1}>
              <Grid item xs={5}>
                <C1SelectField
                  name="expiryNotifyBy"
                  label="Notify By"
                  optionsMenuItemArr={notifyMethod?.map((item, idx) => {
                    return (
                      <MenuItem value={item?.id} key={idx}>
                        {item?.desc}
                      </MenuItem>
                    );
                  })}
                  required
                  value={expNotifyBy?.type}
                  onChange={(e) => handleInputChange(e, "expiryNotifyBy")}
                  disabled={isDisabled || expNotifyBy?.disable}
                  helperText={(errors && errors["EXP_notifyBy"]) || ""}
                  error={errors && errors["EXP_notifyBy"]}
                />
              </Grid>
              <Grid item xs={7}>
                <C1InputField
                  name={expNotifyBy?.type}
                  label=""
                  disabled={isDisabled || expNotifyBy?.disable}
                  onChange={(e) =>
                    handleInputChange(e, `EXP_${expNotifyBy?.type}`)
                  }
                  value={vextExpiry[expNotifyBy?.type]}
                  helperText={
                    errors &&
                    (errors["MNT_" + expNotifyBy?.type] ||
                      errors["EXP_notifyBy"])
                  }
                  error={
                    errors &&
                    (errors["MNT_" + expNotifyBy?.type] ||
                      errors["EXP_notifyBy"])
                  }
                />
              </Grid>
            </Grid>
            <Grid container alignItems="center" spacing={1}>
              <Grid item xs={5}>
                <C1SelectField
                  name="expiryMonitorBy"
                  label="Monitor By"
                  required
                  optionsMenuItemArr={monitorBy?.map((item, idx) => {
                    return (
                      <MenuItem value={item?.id} key={idx}>
                        {item?.desc}
                      </MenuItem>
                    );
                  })}
                  onChange={(e) => handleInputChange(e, "expiryMonitorBy")}
                  value={vextExpiry?.vextMonitorMthd || ""}
                  disabled={isDisabled || expMonitorBy?.disable}
                  helperText={(errors && errors["EXP_vextMonitorMthd"]) || ""}
                  error={errors && errors["EXP_vextMonitorMthd"]}
                />
              </Grid>
              <Grid item xs={7}>
                <C1InputField
                  name="vextMonitorValue"
                  type="number"
                  label=""
                  disabled={isDisabled || expMonitorBy?.disable}
                  onChange={(e) =>
                    handleInputChange(e, `EXP_${expMonitorBy?.type}`)
                  }
                  value={vextExpiry?.vextMonitorValue || ""}
                  helperText={(errors && errors["EXP_vextMonitorValue"]) || ""}
                  error={errors && errors["EXP_vextMonitorValue"]}
                />
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </C1CategoryBlock>

      {popUp && (
        <VehicleMaintenance
          popUp={popUp}
          setPopUp={(e) => mlogPopUpHandler()}
          truckProps={props}
          mlogData={mlogData}
        />
      )}
    </>
  );
}
