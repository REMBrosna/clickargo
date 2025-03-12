import React, { useEffect } from "react";
import {Grid, Paper, Link, Typography} from "@material-ui/core";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import useHttp from "app/c1hooks/http";
import {isStringEmpty } from "app/c1utils/utility";
import C1InputField from "app/c1component/C1InputField";
import { downloadFile } from "app/c1utils/utility";
import useAuth from "../../../../hooks/useAuth";
import { Roles, RegistrationStatus } from "app/c1utils/const";

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
      currentState,
      errors,
      locale
      }) => {

    const tableCls = useTableStyle();
    const { sendRequest, res, urlId } = useHttp();
    const viewFile = (fileName, data, attId) => {
        if (isStringEmpty(data) && attId) {
            sendRequest(`/api/co/common/entity/attach/${attId}`, 'view', 'get');
        } else {
            downloadFile(fileName, data);
        }
    }

    const { user } = useAuth();
    let map = new Set(user.authorities.map((el) => el.authority));
    let isDisabled = true;
    if (map.has(Roles.SYSTEM_ADMIN.code) && currentState === RegistrationStatus.APPROVED.code) {
        isDisabled = false;
    }

    useEffect(() => {
        switch (urlId) {
            case "view":
                downloadFile(res?.data?.attName, res?.data?.attData);
            default: break;
        }
    }, [res, urlId]);

    return (
        <React.Fragment>
            <Grid container item xs={12}>
                <TableContainer component={Paper}>
                    <Table className={tableCls.table} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <StyledTableCell align="center">{locale("suppDocs.no")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("suppDocs.docType")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("suppDocs.required")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("suppDocs.file")}</StyledTableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {inputData.map((row, index) => (
                                <TableRow key={index}>
                                    <TableCell align="center">{row.seq + 1}</TableCell>
                                    <TableCell align="center">{row.desc}</TableCell>
                                    <TableCell align="center">{row.mandatory ? locale("suppDocs.yes") : locale("suppDocs.not")}</TableCell>
                                    <TableCell align="center">
                                        <C1InputField name={row.attType} type="file"
                                                      onChange={(e) => handleFileChange(e, index)}
                                                      disabled={isDisabled}
                                                      error={!!(errors && errors.regDocs && errors.regDocs[index])}
                                                      helperText={errors && errors.regDocs && errors.regDocs[index] ? errors.regDocs[index] : null} />
                                        <Typography variant="subtitle2" color="secondary" gutterBottom>{locale("suppDocs.viewFile")}</Typography> {row.attData || row.attName ? <Link href="#" onClick={() => viewFile(row.attName, row.attData, row.attId)}>{row.attName}</Link> : ""}
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

export default RegisterSuppDocs;