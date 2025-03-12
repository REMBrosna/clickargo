import { Box, Grid, Link, Paper, Typography } from "@material-ui/core";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import React, { useEffect } from "react";

import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1DateField from "app/c1component/C1DateField";
import C1Information from "app/c1component/C1Information";
import C1InputField from "app/c1component/C1InputField";
import C1OutlinedDiv from "app/c1component/C1OutlinedDiv";
import useHttp from "app/c1hooks/http";
import { titleTab, useStyles } from "app/c1utils/styles";
import { downloadFile } from "app/c1utils/utility";

import { isStringEmpty } from "../../../c1utils/utility";

const StyledTableCell = withStyles((theme) => ({
    head: {
        backgroundColor: '#3C77D0',
        color: theme.palette.common.white,
    },
    body: {
        fontSize: 14,
    },
}))(TableCell);

const useTableStyle = makeStyles({
    table: {
        minWidth: 450,
    },
    column: {
        width: 50,
    },
});

const RegisterSuppDocs = ({
    inputData,
    handleFileChange,
    isSubmitting,
    errors,
    locale
}) => {

    const tableCls = useTableStyle();
    const classes = useStyles();
    const title = titleTab();

    const { sendRequest, res, urlId } = useHttp();

    const viewFile = (fileName, data, attId) => {
        if (isStringEmpty(data) && attId) {
            sendRequest(`/api/co/common/entity/attach/${attId}`, 'view', 'get');
        } else {
            downloadFile(fileName, data);
        }
    }

    useEffect(() => {
        switch (urlId) {
            case "view":
                downloadFile(res?.data?.attName, res?.data?.attData);
            default: break;
        }
    }, [res, urlId]);

    const generalDocList = [
        {
            id: '01',
            docType: "Certificate of Incorporation",
            mandatory: "Yes",
            file: "Test",
            validityDate: ""

        }, {
            id: '02',
            docType: "Authorisation Letter",
            mandatory: "Yes",
            file: "",
            validityDate: ""
        }, {
            id: '03',
            docType: "Staff ID/ Passport List",
            mandatory: "Yes",
            file: "",
            validityDate: ""
        }, {
            id: '04',
            docType: "Others",
            mandatory: "No",
            file: "",
            validityDate: ""
        }
    ]

    const clicDoDocList = [
        {
            id: '01',
            docType: "Authorisation Document Proof",
            mandatory: "Yes",
            file: "Test",
            validityDate: ""

        }, {
            id: '02',
            docType: "License",
            mandatory: "Yes",
            file: "",
            validityDate: ""
        }, {
            id: '03',
            docType: "Others",
            mandatory: "Yes",
            file: "",
            validityDate: ""
        }
    ]

    const clicTruckDocList = [
        {
            id: '01',
            docType: "Trucking Document Proof",
            mandatory: "Yes",
            file: "Test",
            validityDate: ""

        }, {
            id: '02',
            docType: "License",
            mandatory: "Yes",
            file: "",
            validityDate: ""
        }, {
            id: '03',
            docType: "Others",
            mandatory: "Yes",
            file: "",
            validityDate: ""
        }
    ]

    return (
        <React.Fragment>
            <Grid container alignItems="flex-start" spacing={1} className={classes.gridContainer}>
                {/* <Grid className={classes.gridContainer} item xs={12}>
                    <Box className={title.root}>{"General Documents"}</Box>
                </Grid> */}
                <Grid container item xs={12}>
                    <C1OutlinedDiv label="General Documents">
                        <TableContainer component={Paper}>
                            <Table className={tableCls.table} aria-label="simple table">
                                <TableHead>
                                    <TableRow>
                                        <StyledTableCell align="center">{"S/No."}</StyledTableCell>
                                        <StyledTableCell align="center">{locale("suppDocs.docType")}</StyledTableCell>
                                        <StyledTableCell align="center">{"Mandatory"}</StyledTableCell>
                                        <StyledTableCell align="center">{"File"}</StyledTableCell>
                                        <StyledTableCell align="center">{"Validity Date"}</StyledTableCell>
                                        <StyledTableCell align="center"></StyledTableCell>

                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {generalDocList.map((row, index) => (
                                        <TableRow key={index}>
                                            <TableCell align="center">{row.id}</TableCell>
                                            <TableCell align="center">{row.docType}</TableCell>
                                            <TableCell align="center">{row.mandatory ? locale("suppDocs.yes") : locale("suppDocs.not")}</TableCell>
                                            <TableCell align="center" style={{ verticalAlign: 'top' }}>
                                                <C1InputField name={row.file} type="file"
                                                    onChange={(e) => handleFileChange(e, index)}
                                                    disabled={isSubmitting}
                                                    error={errors && errors.regDocs && errors.regDocs[index] ? true : false}
                                                    helperText={errors && errors.regDocs && errors.regDocs[index] ? errors.regDocs[index] : null} />
                                                <Typography variant="subtitle2" color="secondary" gutterBottom>{locale("suppDocs.viewFile")}</Typography>
                                                <Link href="#" onClick={() => viewFile(row.id, row.id, row.id)}>{row.ud}</Link>
                                            </TableCell>
                                            <TableCell align="center" style={{ verticalAlign: 'top' }}>
                                                <C1DateField />
                                            </TableCell>
                                            <TableCell align="center">
                                                <C1DataTableActions
                                                    viewPath={"/pilotOrder/view/" + row.id}
                                                    removeEventHandler={"/pilotOrder/view/" + row.id}>
                                                </C1DataTableActions >
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </C1OutlinedDiv>
                </Grid>

                <Grid container item xs={12}>{/**Space */}</Grid>
                {/* <Grid className={classes.gridContainer} item xs={12}>
                    <Box className={title.root}>{"ClicDO Documents"}</Box>
                </Grid> */}
                <Grid container item xs={12}>
                    <C1OutlinedDiv label="ClicDO Documents">
                        <TableContainer component={Paper}>
                            <Table className={tableCls.table} aria-label="simple table">
                                <TableHead>
                                    <TableRow>
                                        <StyledTableCell align="center">{"S/No."}</StyledTableCell>
                                        <StyledTableCell align="center">{locale("suppDocs.docType")}</StyledTableCell>
                                        <StyledTableCell align="center">{"Mandatory"}</StyledTableCell>
                                        <StyledTableCell align="center">{"File"}</StyledTableCell>
                                        <StyledTableCell align="center">{"Validity Date"}</StyledTableCell>
                                        <StyledTableCell align="center"></StyledTableCell>

                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {clicDoDocList.map((row, index) => (
                                        <TableRow key={index}>
                                            <TableCell align="center">{row.id}</TableCell>
                                            <TableCell align="center">{row.docType}</TableCell>
                                            <TableCell align="center">{row.mandatory ? locale("suppDocs.yes") : locale("suppDocs.not")}</TableCell>
                                            <TableCell align="center" style={{ verticalAlign: 'top' }}>
                                                <C1InputField name={row.file} type="file"
                                                    onChange={(e) => handleFileChange(e, index)}
                                                    disabled={isSubmitting}
                                                    error={errors && errors.regDocs && errors.regDocs[index] ? true : false}
                                                    helperText={errors && errors.regDocs && errors.regDocs[index] ? errors.regDocs[index] : null} />
                                                <Typography variant="subtitle2" color="secondary" gutterBottom>{locale("suppDocs.viewFile")}</Typography>
                                                <Link href="#" onClick={() => viewFile(row.id, row.id, row.id)}>{row.ud}</Link>
                                            </TableCell>
                                            <TableCell align="center" style={{ verticalAlign: 'top' }}>
                                                <C1DateField />
                                            </TableCell>
                                            <TableCell align="center">
                                                <C1DataTableActions
                                                    viewPath={"/pilotOrder/view/" + row.id}
                                                    removeEventHandler={"/pilotOrder/view/" + row.id}>
                                                </C1DataTableActions >
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </C1OutlinedDiv>
                </Grid>

                <Grid container item xs={12}>{/**Space */}</Grid>
                {/* <Grid className={classes.gridContainer} item xs={12}>
                    <Box className={title.root}>{"ClicTruck Documents"}</Box>
                </Grid> */}
                <Grid container item xs={12}>
                    <C1OutlinedDiv label="ClicTruck Documents">
                        <TableContainer component={Paper}>
                            <Table className={tableCls.table} aria-label="simple table">
                                <TableHead>
                                    <TableRow>
                                        <StyledTableCell align="center">{"S/No."}</StyledTableCell>
                                        <StyledTableCell align="center">{locale("suppDocs.docType")}</StyledTableCell>
                                        <StyledTableCell align="center">{"Mandatory"}</StyledTableCell>
                                        <StyledTableCell align="center">{"File"}</StyledTableCell>
                                        <StyledTableCell align="center">{"Validity Date"}</StyledTableCell>
                                        <StyledTableCell align="center"></StyledTableCell>

                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {clicTruckDocList.map((row, index) => (
                                        <TableRow key={index}>
                                            <TableCell align="center">{row.id}</TableCell>
                                            <TableCell align="center">{row.docType}</TableCell>
                                            <TableCell align="center">{row.mandatory ? locale("suppDocs.yes") : locale("suppDocs.not")}</TableCell>
                                            <TableCell align="center" style={{ verticalAlign: 'top' }}>
                                                <C1InputField name={row.file} type="file"
                                                    onChange={(e) => handleFileChange(e, index)}
                                                    disabled={isSubmitting}
                                                    error={errors && errors.regDocs && errors.regDocs[index] ? true : false}
                                                    helperText={errors && errors.regDocs && errors.regDocs[index] ? errors.regDocs[index] : null} />
                                                <Typography variant="subtitle2" color="secondary" gutterBottom>{locale("suppDocs.viewFile")}</Typography>
                                                <Link href="#" onClick={() => viewFile(row.id, row.id, row.id)}>{row.ud}</Link>
                                            </TableCell>
                                            <TableCell align="center" style={{ verticalAlign: 'top' }}>
                                                <C1DateField />
                                            </TableCell>
                                            <TableCell align="center">
                                                <C1DataTableActions
                                                    viewPath={"/pilotOrder/view/" + row.id}
                                                    removeEventHandler={"/pilotOrder/view/" + row.id}>
                                                </C1DataTableActions >
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </C1OutlinedDiv>
                </Grid>

                {/**Information */}
                <Grid container item xs={12}>{/**Space */}</Grid>
                <Grid item lg={12} md={12} xs={12}>
                    <C1Information information="documentDetails" />
                </Grid>
            </Grid>
        </React.Fragment >
    );
};

export default RegisterSuppDocs;