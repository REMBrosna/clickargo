import React, { useEffect, useState } from "react";
import {
    Button,
    Grid,
    Paper,
    Snackbar,
    TableCell,
    TableRow,
    TableHead,
    TableBody,
    TableContainer,
    withStyles,
    Dialog,
    IconButton,
} from "@material-ui/core";
import { AddBoxOutlined as AddBoxIcon } from "@material-ui/icons";
import C1Alert from "app/c1component/C1Alert";
import Table from "@material-ui/core/Table";
import { ConfirmationDialog } from "../../../../../matx";
import DeleteIcon from "@material-ui/icons/DeleteOutlineOutlined";
import ApplicationInsurancePopUp from "./popups/ApplicationInsurancePopUp";
import EditIcon from "@material-ui/icons/EditOutlined";

/*** Administration > Insurance Application component. */
const ApplicationInsurance = ({
      t,
      data,
      error,
      formData,
      openPopUp,
      eventHandler,
      setOpenPopUp,
      openPopDetails,
      handleAddTrucks,
      setOpenPopDetails,
      handlePopUpChange,
      handleDeleteRecord,
      handleAutoCompleteInput
  }) => {
    const [isRefresh, setRefresh] = useState(false);
    const [openWarning, setOpenWarning] = useState(false);
    const [warningMessage, setWarningMessage] = useState("");
    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
        action: null,
        open: false,
    });
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: t("common:msg.deleteSuccess"),
        severity: "success",
    });
    const handleCloseSnackBar = () => {
        setRefresh(false);
        setSnackBarState({ ...snackBarState, open: false });
    };
    const handleWarningAction = (e) => {
        setOpenWarning(false);
        setWarningMessage("");
    };
    let snackBar = null;
    if (snackBarState.open) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;
        snackBar = (
            <Snackbar
                anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
                open={snackBarState.open}
                onClose={handleCloseSnackBar}
                autoHideDuration={3000}
                key={anchorOriginV + anchorOriginH}
            >
                <C1Alert
                    onClose={handleCloseSnackBar}
                    severity={snackBarState.severity}
                >
                    {snackBarState.msg}
                </C1Alert>
            </Snackbar>
        );
    }
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
                onYesClick={(e) => eventHandler(openSubmitConfirm?.action)}
            />
        );
    }
    return (
        <React.Fragment>
            <Grid container justifyContent="flex-end">
                <Grid item>
                    <Button
                        style={{margin: "10px", marginRight: "30px"}}
                        type="button"
                        variant="contained"
                        color="primary"
                        size="large"
                        onClick={() => setOpenPopUp(true)}
                    >
                        <AddBoxIcon /> &nbsp; Add Vehicle
                    </Button>
                </Grid>
            </Grid>
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow style={{ backgroundColor: "#EFF2F5", color : "inherit", paddingRight: "20px"}}>
                            <StyledTableCell style={{ backgroundColor: "#EFF2F5", color : "inherit"}}>Truck No</StyledTableCell>
                            <StyledTableCell style={{ backgroundColor: "#EFF2F5", color : "inherit" }}>Make & Model</StyledTableCell>
                            <StyledTableCell style={{ backgroundColor: "#EFF2F5", color : "inherit" }}>Coverage</StyledTableCell>
                            <StyledTableCell style={{ backgroundColor: "#EFF2F5", color : "inherit" }}>Usage</StyledTableCell>
                            <StyledTableCell style={{ backgroundColor: "#EFF2F5", color : "inherit" }}>Claims</StyledTableCell>
                            <StyledTableCell style={{ backgroundColor: "#EFF2F5", color : "inherit" }}>Suspension</StyledTableCell>
                            <StyledTableCell style={{ backgroundColor: "#EFF2F5", color : "inherit", paddingRight: "50px"}}>Action</StyledTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {data.map((row, index) => (
                            <TableRow key={index}>
                                <TableCell>{row?.licenseNo}</TableCell>
                                <TableCell>{row?.makeAndModel}</TableCell>
                                <TableCell>{row?.coverage}</TableCell>
                                <TableCell>{row?.usage}</TableCell>
                                <TableCell>{row?.claims}</TableCell>
                                <TableCell>{row?.suspension}</TableCell>
                                <TableCell>
                                    <IconButton aria-label="Delete"
                                                type="button"
                                                color="primary"
                                                onClick={(e) => handleDeleteRecord(e, row)}>
                                        <DeleteIcon />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>


            <Dialog maxWidth="xs" open={openWarning}>
                <div className="p-8 text-center w-360 mx-auto">
                    <h4 className="capitalize m-0 mb-2">{"Information"}</h4>
                    <p>{warningMessage}</p>
                    <div className="flex justify-center pt-2 m--2">
                        <Button
                            className="m-2 rounded hover-bg-primary px-6"
                            variant="outlined"
                            color="primary"
                            onClick={(e) => handleWarningAction(e)}
                        >
                            {t("cargoowners:popup.ok")}
                        </Button>
                    </div>
                </div>
            </Dialog>

            <ApplicationInsurancePopUp
                t={t}
                error={error}
                formData={formData}
                openPopUp={openPopUp}
                setOpenPopUp={setOpenPopUp}
                openPopDetails={openPopDetails}
                setOpenPopDetails={setOpenPopDetails}
                handlePopUpChange={handlePopUpChange}
                handleAddTrucks={handleAddTrucks}
                handleAutoCompleteInput={handleAutoCompleteInput}
            />
            {snackBar}
            {confirmDialog}
        </React.Fragment>
    );
};

const StyledTableCell = withStyles((theme) => ({
    head: {
        backgroundColor: '#3C77D0',
        color: theme.palette.common.white,
    },
    body: {
        fontSize: 14,
    },
}))(TableCell);

export default ApplicationInsurance;
