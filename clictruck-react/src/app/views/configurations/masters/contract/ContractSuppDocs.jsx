import React from "react";
import {
    Grid, Paper, IconButton, Tooltip
} from "@material-ui/core";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import DeleteIcon from '@material-ui/icons/Delete';
import Visibility from '@material-ui/icons/Visibility';

import C1InputField from "app/c1component/C1InputField";
import { Status } from "app/c1utils/const";

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

const ContractSuppDocs = ({
    viewType,
    contractDocs,
    inputData,
    handleFileChange,
    viewFile,
    handleDeleteFile,
    isSubmitting,
    errors,
    locale
}) => {

    const tableCls = useTableStyle();

    return (
        <React.Fragment>
            <Grid container item xs={12}>
                <TableContainer component={Paper}>
                    <Table className={tableCls.table} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <StyledTableCell align="center">{locale("contract.suppDocs.no")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("contract.suppDocs.docType")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("contract.suppDocs.required")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("contract.suppDocs.file")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("contract.suppDocs.action")}</StyledTableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {contractDocs.map((row, index) => (
                                <TableRow key={index}>
                                    <TableCell align="center">{row.seq + 1}</TableCell>
                                    <TableCell align="center">{row.desc}</TableCell>
                                    <TableCell align="center">{row.mandatory ? locale("docAssociate.list.table.headers.yes") : locale("docAssociate.list.table.headers.no")}</TableCell>
                                    {viewType !== "view" ? (
                                        <TableCell align="center">
                                            <C1InputField name={row.attType} type="file"
                                                inputProps={{ id: 'uploadFile', "accept": "application/pdf" }}
                                                onChange={(e) => handleFileChange(e, index)}
                                                disabled={isSubmitting || row?.attName}
                                                error={errors?.validation?.[`supDocs.[${index}]`] ? true : false}
                                                helperText={errors?.validation?.[`supDocs.[${index}]`] ? errors.validation[`supDocs.[${index}]`] : null} />
                                        </TableCell>
                                    ) : (
                                        <TableCell align="center">{row.attName}</TableCell>
                                    )}
                                    <TableCell align="center">
                                        <Tooltip title="View">
                                            <IconButton aria-label="Preview" type="button" disabled={!row.attName}
                                                color="primary" onClick={(e) => viewFile(row.attName, row.attData)}>
                                                <Visibility />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Delete">
                                            <IconButton aria-label="Delete" type="button" disabled={!row.attName || viewType === "view"}
                                                color="primary" onClick={(e) => handleDeleteFile(e, row.attId)}>
                                                <DeleteIcon />
                                            </IconButton>
                                        </Tooltip>

                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Grid>
        </React.Fragment>
    );
};

export default ContractSuppDocs;