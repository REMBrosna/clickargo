import React, { useState, useEffect } from "react";
import {
    Grid,
    TextField,
    Button,
    Select,
    Paper,
    Snackbar,
    Tabs,
    Tab,
    Divider,
    Tooltip
} from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";

import AddBoxIcon from '@material-ui/icons/AddBox';

import { titleTab, useStyles } from "app/c1utils/styles";
import C1InputField from "../../../../c1component/C1InputField";
import SessionCache from 'app/services/sessionCacheService.js';

import C1SelectField from "app/c1component/C1SelectField";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import DeleteIcon from '@material-ui/icons/Delete';
import CloseIcon from '@material-ui/icons/Close';
import IconButton from '@material-ui/core/IconButton';
import PageviewIcon from '@material-ui/icons/Pageview';
import { useTranslation } from "react-i18next";

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

const useButtonStyles = makeStyles((theme) => ({
    root: {
        '& > *': {
            margin: theme.spacing(1),
        },
    },
}));



function createData(seq, code, docType, docRefNo, isMandatory, docFile, subRow = []) {
    return { seq, code, docType, docRefNo, isMandatory, docFile, subRow };
}

const docTypes = [
    { value: 'COR', desc: "Certificate of Registry" },
    { value: 'ISSC', desc: "International Ship Security Certificate" },
    { value: 'OTH', desc: "Others" }

];



// let rowsView = [
//     createData('1', "Certificate of Registry", "Mandatory", "Registry.pdf", <Tooltip title="Download">
//         <IconButton aria-label="Preview" type="button" onClick={handlePreviewClick} >
//             <PageviewIcon />
//         </IconButton>
//     </Tooltip>),
//     createData('2', "International Ship Security Certificate", "Mandatory", "ISSC.pdf", <Tooltip title="Download">
//         <IconButton aria-label="Preview" type="button" onClick={handlePreviewClick} >
//             <PageviewIcon />
//         </IconButton>
//     </Tooltip>),

// ];

const SupportingDocsSubTab = ({
    handleSubmit,
    data,
    inputData,
    handleValidate,
    viewType,
    isSubmitting,
    props }) => {


    let isDisabled = true;
    if (viewType === 'new')
        isDisabled = false;
    else if (viewType === 'edit')
        isDisabled = false;
    else if (viewType === 'view')
        isDisabled = true;

    if (isSubmitting)
        isDisabled = true;

    const { t } = useTranslation(["buttons", "common"]);
    const [actualRows, setActualRows] = useState([
        createData('1', "COR", "Certificate of Registry", isDisabled ? "Ref-1" : "", "Mandatory", isDisabled ? "Cor.pdf" : "", []),
        createData('2', "ISSC", "International Ship Security Certificate", isDisabled ? "Ref-2" : "", "Mandatory", isDisabled ? "Cor.pdf" : "", []),
        createData('3', "OTH", "Others", isDisabled ? "Ref-3" : "", "Optional", isDisabled ? "Cor.pdf" : "", [])
    ]);


    const [uploadFile, setUploadFile] = useState({ attUcrNo: '', attUid: '', attSeq: '', attType: '', attDesc: '', attReferenceid: '', attName: '', attDesc: '', attData: '' });

    const tableCls = useTableStyle();

    const gridClass = useStyles();

    const [supportingDocs, setSupportingDocsIputData] = useState(inputData);


    useEffect(() => {
        console.log("actual rows", actualRows);

    }, [actualRows]);

    const handleDeleteRow = (e) => {
        // rows = rows.splice(rows.lastIndex, rows.length - 1);
        // setActualRows(rows);
    }

    function Uint8ArrayToString(fileData) {
        var dataString = "";
        for (var i = 0; i < fileData.length; i++) {
            dataString += String.fromCharCode(fileData[i]);
        }

        return dataString;
    }

    const onFileChangeHandler = (e) => {
        e.preventDefault();
        var file = e.target.files[0];

        if (!file) {
            // click <Cancel> button, didn't select file,
            return;
        }


        const fileReader = new FileReader();
        fileReader.readAsArrayBuffer(e.target.files[0]);
        fileReader.onload = e => {
            //console.log("e.target.result", e.target.result);

            const uint8Array = new Uint8Array(e.target.result);

            var imgStr = Uint8ArrayToString(uint8Array);
            // console.log("imgStr 2 ", imgStr.length, imgStr,);
            var base64Sign = btoa(imgStr);

            //just default to this file regardless of what file has been uploaded
            uploadFile.attUcrNo = inputData.appId;
            uploadFile.attName = file.name;
            uploadFile.attData = base64Sign;
        };
    };

    const handleBtnAddFile = (e) => {
        e.preventDefault();
        setActualRows(rows => {
            return rows.map(r => {
                let newRow = r;
                console.log(uploadFile.attType);
                if (r.code === uploadFile.attType) {
                    //if subRow does not have any record yet, insert in the parent level, or docFile is still empty
                    if (r.subRow.length === 0 && r.docFile === '') {
                        //.docFile, docRefNo
                        newRow.docFile = uploadFile.attName;
                        newRow.docRefNo = uploadFile.attReferenceid;
                    } else {
                        newRow.subRow.push({ id: Math.random(), code: uploadFile.attType, file: uploadFile.attName, refNo: uploadFile.attReferenceid });
                    }

                }
                return newRow;
            });
        });

        setUploadFile({ ...uploadFile, attUcrNo: '', attUid: '', attSeq: '', attType: '', attDesc: '', attReferenceid: '', attName: '', attDesc: '', attData: '' });
    }

    const handleInputChange = (e) => {

        let attDesc = '';
        if (e.target.value === 'attType') {
            attDesc = e.target.options[e.target.selectedIndex].text;
            setUploadFile({ ...uploadFile, attDesc: attDesc });
        } else {
            setUploadFile({ ...uploadFile, [e.target.name]: e.target.value });
        }
    };

    const handleDelete = (e, _code, _id) => {
        //only delete from the subrow, if there's only one record just set the filename, ref no to ''
        e.preventDefault();

        setActualRows(rows => {
            return rows.map(r => {
                if (r.code === _code) {
                    //if subRow does not have any record yet, insert in the parent level, or docFile is still empty
                    if (r.subRow.length === 0) {
                        r.docFile = '';
                        r.docRefNo = '';
                    } else {
                        let newSubRow = r.subRow.filter(sr => sr.id !== _id);
                        if (newSubRow.length <= 0 && r.subRow.length === 1) {
                            r.subRow = [];
                        } else {
                            r.subRow = [];
                            r.subRow.push(newSubRow);
                        }
                    }
                }
                return r;
            });
        });
    }


    return (
        <React.Fragment>
            <Grid container spacing={3} alignItems="center" className={gridClass.gridContainer}>
                <Grid container item xs={3}>
                    <C1SelectField
                        label={t("common:common.label.docType")}
                        name="attType"
                        required
                        onChange={handleInputChange}
                        value={uploadFile.attType}
                        disabled={isDisabled}
                        options={
                            docTypes.map((item, ind) => (
                                <MenuItem value={item.value} key={ind}>{item.desc}</MenuItem>
                            ))} />

                </Grid>
                <Grid container item xs={3}>
                    <C1InputField
                        label={t("common:common.label.docRefNo")}
                        name="attReferenceid"
                        disabled={isDisabled}
                        onChange={handleInputChange}
                        value={uploadFile.attReferenceid}
                    />

                </Grid>
                <Grid container item xs={3}>
                    <C1InputField
                        required
                        disabled={isDisabled}
                        label={t("common:common.label.uploadFile")}
                        name="attData"
                        onChange={onFileChangeHandler}
                        type="file" />

                </Grid>
                <Grid container item xs={3}>
                    <Grid item direction="column">
                        <Tooltip title={t("buttons:add")} aria-label="add">
                            <Button type="submit" disabled={isDisabled} color="primary" variant="contained" size="large" disabled={isDisabled} onClick={handleBtnAddFile}><AddBoxIcon viewBox="1 -1 30 30"></AddBoxIcon>{t("buttons:add")} </Button>
                        </Tooltip>
                    </Grid>
                </Grid>
            </Grid>

            <br></br>

            <Grid container item xs={12} className={gridClass.gridContainer}>
                <TableContainer component={Paper}>
                    <Table className={tableCls.table} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <StyledTableCell align="center">{t("common:common.dataTable.number")}</StyledTableCell>
                                <StyledTableCell align="center">{t("common:common.dataTable.docType")}</StyledTableCell>
                                <StyledTableCell align="center">{t("common:common.dataTable.required")}</StyledTableCell>
                                <StyledTableCell align="center">{t("common:common.dataTable.docRefNo")}</StyledTableCell>
                                <StyledTableCell align="center">{t("common:common.dataTable.fileName")}</StyledTableCell>
                                <StyledTableCell align="center">{t("common:common.dataTable.action")}</StyledTableCell>

                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {actualRows.map((row) => {
                                let element =
                                    <TableRow key={row.name}>
                                        <TableCell align="center">{row.seq}</TableCell>
                                        <TableCell align="center">{row.docType}</TableCell>
                                        <TableCell align="center">{row.isMandatory}</TableCell>
                                        <TableCell align="center">{row.docRefNo}</TableCell>
                                        <TableCell align="center">{row.docFile}</TableCell>
                                        <TableCell align="center">{row.docFile !== '' ?
                                            <div>
                                                <IconButton aria-label="Preview" type="button" color="primary" onClick={() => window.open(`null`, '_blank', 'titlebar=no')}>
                                                    <PageviewIcon />
                                                </IconButton>
                                                {!isDisabled && <IconButton aria-label="Delete" type="button" color="primary" disabled={isDisabled} onClick={(event) => handleDelete(event, row.code, row.seq)}>
                                                    <DeleteIcon />
                                                </IconButton>}
                                            </div> : ''}
                                        </TableCell>

                                    </TableRow>;

                                let subElement = [];
                                //code: uploadFile.attType, file: uploadFile.attName, refNo: uploadFile.attReferenceid
                                if (row.subRow && row.subRow.length >= 1) {
                                    row.subRow.map((sr, i) => {
                                        subElement.push(
                                            (<TableRow key={sr.id}>
                                                <TableCell colSpan={3}></TableCell>
                                                <TableCell align="center">{sr.refNo}</TableCell>
                                                <TableCell align="center">{sr.file}</TableCell>
                                                <TableCell align="center">{sr.file !== '' ?
                                                    <div>
                                                        <IconButton aria-label="Preview" type="button" color="primary" onClick={() => window.open(`null`, '_blank', 'titlebar=no')}>
                                                            <PageviewIcon />
                                                        </IconButton>
                                                        <IconButton aria-label="Delete" type="button" color="primary" disabled={isDisabled} onClick={(event) => handleDelete(event, row.code, sr.id)}>
                                                            <DeleteIcon />
                                                        </IconButton></div> : ''}
                                                </TableCell>
                                            </TableRow>));
                                    });
                                }


                                return <React.Fragment>
                                    {element}
                                    {subElement.map(i => i)}
                                </React.Fragment>;


                            })}

                        </TableBody>
                    </Table>
                </TableContainer>


            </Grid>
            <br />
        </React.Fragment >
    );
};

export default SupportingDocsSubTab;