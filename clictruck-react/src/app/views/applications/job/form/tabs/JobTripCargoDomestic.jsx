import {
  ButtonGroup,
  Grid
} from "@material-ui/core";
import {
  DeleteOutline,
  EditOutlined,
  VisibilityOutlined,
} from "@material-ui/icons";
import LocalShippingOutlinedIcon from "@material-ui/icons/LocalShippingOutlined";
import _, { isEmpty } from "lodash";
import React, {
  useContext,
  useState,
} from "react";
import { useTranslation } from "react-i18next";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import { dialogStyles, tabScroll } from "app/c1utils/styles";
import {
  isStringEmpty,
} from "app/c1utils/utility";
import C1PopUp from "app/clictruckcomponent/JobPopUp";
import { ConfirmationDialog } from "matx";
import JobTruckContext from "../JobTruckContext";
import CargoPopup from "../../popups/CargoPopup";
import {Alert, AlertTitle} from "@material-ui/lab";
import {deepUpdateState} from "../../../../../c1utils/stateUtils";
const JobTripCargoDomestic = ({
    cargoTypes
}) => {
  const { t } = useTranslation(["job"]);
  const [cargoData, setCargoData] = useState({});
  const { inputData, tcrData, setTcrData, viewType, isDisabled, ableToModifiesCargo, acceptedStatus} = useContext(JobTruckContext);
  const dialogClasses = dialogStyles();
  const [openCargoPopUp, setOpenCargoPopUp] = useState(false);
  const [errors, setErrors] = useState({});
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [snackBarState, setSnackBarState] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: "",
    severity: "success",
  });
  const [deleteId, setDeleteId] = useState("");
  const [action, setAction] = useState(null);
  const [formAction, setFormAction] = useState("");

  const handleInputCargo = (e) => {
    const elName = e.target.name;

    if (elName === "tckCtMstCargoType.crtypId") {
      const tckCtMstCargoType = cargoTypes.find(
        (item) => item.crtypId === e.target.value
      );
      setCargoData({ ...cargoData, tckCtMstCargoType });
    } else {
      setCargoData({ ...cargoData, [e.target.name]: e.target.value });
    }
  };
  const handleAutoComplete = (e, name, value, reason) => {
    setCargoData({ ...cargoData, ...deepUpdateState(cargoData, name, value?.value) });
  };

  const handleSetData = (e) => {
    setFormAction("");
    let validationErrors = []; // previously doValidate()
    const isEmptyObject = (obj) => Object.keys(obj).length === 0 && obj.constructor === Object;

    if (!isEmpty(validationErrors)) {
      setErrors(validationErrors);
      return;
    }
    if (isEmptyObject(cargoData)) {
      return (
          <Alert severity="warning">
            <AlertTitle>Warning</AlertTitle>
            This is a test alert
          </Alert>
      )
    }

    setOpenCargoPopUp(false);
    if (!tcrData.tripCargoMmList) {
      tcrData.tripCargoMmList = [];
    }
    if (cargoData.cgId) {
      let idx = tcrData?.tripCargoMmList?.findIndex(
        (item) => item.cgId === cargoData.cgId
      );
      if (idx >= 0) {
        tcrData.tripCargoMmList[idx] = cargoData;
      }
    } else {
      let cgId = Math.ceil(Math.random() * 10000);
      cargoData.cgId = cgId;
      tcrData.tripCargoMmList.push(cargoData);
    }
  };

  const doValidate = () => {
    //cargoData
    let validationErrors = {};
    if (isStringEmpty(cargoData?.tckCtMstCargoType?.crtypId)) {
      validationErrors["tckCtMstCargoType.crtypId"] = `${t(
        "job:tripDetails.type"
      )} is required.`;
    }
    if (isStringEmpty(cargoData?.cgCargoQty)) {
      validationErrors["cgCargoQty"] = `${t(
        "job:tripDetails.quantity"
      )} is required.`;
    }
    if (isStringEmpty(cargoData?.cgCargoMarksNo)) {
      validationErrors["cgCargoMarksNo"] = `${t(
        "job:tripDetails.marksNo"
      )} is required.`;
    }
    if (isStringEmpty(cargoData?.cgCargoWeight)) {
      validationErrors["cgCargoWeight"] = `${t(
        "job:tripDetails.weight"
      )} is required.`;
    }
    if (isStringEmpty(cargoData?.cgCargoVolume)) {
      validationErrors["cgCargoVolume"] = `${t(
        "job:tripDetails.volume"
      )} is required.`;
    }
    return validationErrors;
  };

  const setOpenCargoPopUpWrap = (action) => {
    //console.log("isOpen", isOpen);
    setFormAction(action);
    setAction("create");
    setOpenCargoPopUp(true);
    setCargoData({});
    setErrors({});
  };

  const handleEditViewCargo = (e, cgId, action, isDisable) => {
    setFormAction(action)
    setAction(action);
    setOpenCargoPopUp(true);
    let cargo = tcrData?.tripCargoMmList?.filter((e) => e.cgId === cgId)[0];
    setCargoData(cargo);
    setErrors({});
  };

  const handleDeleteConfirm = (e, cgId) => {
    setOpenCargoPopUp(false);
    const newValues = tcrData.tripCargoMmList.filter(val => val?.cgId !== cgId);
    setTcrData(prevData => ({ ...prevData, tripCargoMmList: newValues }));
    setOpenSubmitConfirm(prev => ({ ...prev, action: "", open: false }));
    setDeleteId("");
  }
  const handleOnClose = (e) => {
    setOpenCargoPopUp(false)
  }

  const columnCargo = [
    {
      name: "cgId",
      label: "S/No",
      options: {
        display: false,
      },
    },
    {
      name: "tckCtMstCargoType.crtypDesc",
      label: t("job:tripDetails.cargoType"),
    },
    {
      name: "cgCargoQty",
      label: t("job:tripDetails.quantity"),
    },
    {
      name: "cgCargoMarksNo",
      label: t("job:tripDetails.marksNo"),
    },
    {
      name: "marksNo",
      label: t("job:tripDetails.action"),
      options: {
        filter: false,
        sort: false,
        display: true,
        viewColumns: false,
        setCellHeaderProps: () => {
          return { style: { textAlign: "center" } };
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const id = tableMeta.rowData[0];
          return (
            <C1DataTableActions>
              <Grid container alignItems="flex-start" justifyContent="center">
                {isDisabled ? (
                    <>
                    <span style={{ minWidth: "48px" }}>
                      <C1LabeledIconButton
                        tooltip={t("buttons:view")}
                        label={t("buttons:view")}
                        action={(e) => handleEditViewCargo(e, id, "view", true)}
                      >
                        <VisibilityOutlined />
                      </C1LabeledIconButton>
                    </span>
                    {ableToModifiesCargo && (
                        <>
                        <span style={{ minWidth: "48px" }}>
                            <C1LabeledIconButton
                                tooltip={t("buttons:edit")}
                                label={t("buttons:edit")}
                                action={(e) =>
                                    handleEditViewCargo(e, id, "update", false)
                                }
                            >
                              <EditOutlined />
                            </C1LabeledIconButton>
                        </span>
                        <span style={{ minWidth: "48px" }}>
                            <C1LabeledIconButton
                                tooltip={t("buttons:delete")}
                                label={t("buttons:delete")}
                                action={(e) => handlePopupDeleteConfirm(e, id)}
                            >
                                <DeleteOutline />
                            </C1LabeledIconButton>
                        </span>
                        </>
                    )}
                  </>
                ) : (
                  <>
                    <span style={{ minWidth: "48px" }}>
                      <C1LabeledIconButton
                        tooltip={t("buttons:edit")}
                        label={t("buttons:edit")}
                        action={(e) =>
                          handleEditViewCargo(e, id, "update", false)
                        }
                      >
                        <EditOutlined />
                      </C1LabeledIconButton>
                    </span>
                    <span style={{ minWidth: "48px" }}>
                      <C1LabeledIconButton
                        tooltip={t("buttons:delete")}
                        label={t("buttons:delete")}
                        action={(e) => handlePopupDeleteConfirm(e, id)}
                      >
                        <DeleteOutline />
                      </C1LabeledIconButton>
                    </span>
                  </>
                )}
              </Grid>
            </C1DataTableActions>
          );
        },
      },
    },
  ];

  let confirmDialog = "";
  if (openSubmitConfirm.open) {
    confirmDialog = (
      <ConfirmationDialog
        open={openSubmitConfirm?.open}
        onConfirmDialogClose={() =>
          setOpenSubmitConfirm({
            ...openSubmitConfirm,
            action: null,
            open: false,
          })
        }
        text={openSubmitConfirm?.msg}
        title={t("common:popup.confirmation")}
        onYesClick={(e) => handleDeleteConfirm(e, deleteId)}
      />
    );
  }

  const handlePopupDeleteConfirm = (e, id) => {
    setDeleteId(id);
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "DELETE",
      open: true,
      msg: t("common:msg.deleteConfirm"),
    });
  };

  let popupTitle = t("job:tripDetails.viewCargo");
  switch (action) {
    case "create":
      popupTitle = t("job:tripDetails.addCargo");
      break;
    case "update":
      popupTitle = t("job:tripDetails.editCargo");
      break;
    default:
      break;
  }

  const notEditable = ["view"].includes(formAction);

  return (
    <React.Fragment>
      {confirmDialog}
      <C1CategoryBlock
        icon={<LocalShippingOutlinedIcon />}
        title={t("job:tripDetails.cargoDetails")}
      >
        <Grid container alignItems="center" spacing={3}>
          <Grid item xs={12}>
            <C1DataTable
              dbName={{ list: tcrData?.tripCargoMmList || [] }}
              isServer={false}
              columns={columnCargo}
              defaultOrder="attDtCreate"
              defaultOrderDirection="desc"
              viewTextFilter={
                <React.Fragment key={0}>
                  <ButtonGroup
                    style={{ marginRight: 0, height: 30 }}
                    color="primary"
                    key="viewTextFilter"
                  >
                    <C1FormButtons
                      options={{
                        uploadTemplate: {
                          show: viewType !== "view",
                          eventHandler: () => console.log("test"),
                        },
                        // add: {
                        //     show: viewType !== "view" ? true : false,
                        //     eventHandler: setOpenCargoPopUpWrap
                        // }
                      }}
                    />
                  </ButtonGroup>
                </React.Fragment>
              }
              showAdd={viewType !== "view" || (ableToModifiesCargo && !notEditable) ? {
                      type: "popUp",
                      popUpHandler: () => setOpenCargoPopUpWrap("add"),
                    }
                  : null
              }
              isShowDownload={true}
              isShowPrint={false}
              isShowViewColumns={false}
              isShowFilter={false}
              guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
            />
          </Grid>
        </Grid>
      </C1CategoryBlock>
        <C1PopUp
            title={popupTitle}
            openPopUp={openCargoPopUp}
            setOpenPopUp={setOpenCargoPopUp}
            // setSubmitButton={!isDisabled || (ableToModifiesCargo && !notEditable && acceptedStatus) ? handleSetData : undefined}
            maxWidth="lg"
            maxHeight="500px"
            overflowY="auto"
            flex-wrap="none"
            customStyles={{
              backgroundColor: "rgba(244,244,244,0.11)",
              borderRadius: "12px",
              boxShadow: "0 4px 10px rgba(0, 0, 0, 0.2)",
              bottom: "100px",
            }}
            disableCloseButton={true}
        >
        <CargoPopup
            errors={errors}
            cargoData={cargoData}
            cargoTypes={cargoTypes}
            isDisabled={isDisabled}
            notEditable={notEditable}
            handleSave={handleSetData}
            handleOnClose={handleOnClose}
            setCargoData={setCargoData}
            handleInputCargo={handleInputCargo}
            handleAutoComplete={handleAutoComplete}
            ableToModifiesCargo={ableToModifiesCargo}
        />
      </C1PopUp>
    </React.Fragment>
  );
};

export default JobTripCargoDomestic;
