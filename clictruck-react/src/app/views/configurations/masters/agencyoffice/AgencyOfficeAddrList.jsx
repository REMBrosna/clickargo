import React, { useState, useEffect } from "react";
import {
    Grid,
    TextField,
} from "@material-ui/core";

import C1DataTableClient from "app/c1component/C1DataTableClient";
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import VisibilityIcon from '@material-ui/icons/Visibility';
import EditIcon from '@material-ui/icons/Edit';

import AddressDetails from "./AddressDetails";
import axios from 'axios.js';
import { useTranslation } from "react-i18next";

const AgencyOfficeAddrList = ({
    inputData,
    handleInputChange,
    handleAgencySelectChange,
    handleAgencyOfficeIdChange,
    isSubmitting,
    agoaCode,
    errors, }) => {

    const [data, setData] = useState([]);
    const [viewType, setViewType] = useState();
    const { t } = useTranslation(['masters']);
    let isDisabled = true;
    const setViewTypeWrap = (viewTypeParam) => {
        setViewType(viewTypeParam);

        if (viewType === 'new')
            isDisabled = false;
        else if (viewType === 'edit')
            isDisabled = false;
        else if (viewType === 'view')
            isDisabled = true;

        if (isSubmitting)
            isDisabled = true;
    };


    const [open, setOpen] = React.useState(false);
    const [addr, setAddr] = React.useState({});

    const popupDialog = (viewTypeParam) => {
        setViewTypeWrap(viewTypeParam);
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleSaveAddr = () => {
        setOpen(false);
    };


    const columns = [
        {
            name: "id.agoaAddrType", // field name in the row object
            label: t("agencyOffice.details.tabs.recordDetails.addrType"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "TCoreAddr.adrCtycode",
            label: t("province.list.table.headers.country"),
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "TCoreAddr.adrProv",
            label: t("province.list.table.headers.province"),
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "TCoreAddr.adrCity",
            label: t("province.list.table.headers.city"),
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "TCoreAddr.adrPcode",
            label: t("contract.list.table.headers.postCode"),
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "TCoreAddr.adrEmail",
            label: t("contract.list.table.headers.email"),
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "action",
            label: " ",
            options: {
                filter: false,
                display: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return < div className="flex items-center" >
                        <div className="flex-grow"></div>
                        <Button onClick={() => { popupDialog('edit') }}>
                            <EditIcon viewBox="0 0 24 24" color="primary"></EditIcon></Button>
                        <Button onClick={() => { popupDialog('view') }}>
                            <VisibilityIcon viewBox="0 0 24 24"></VisibilityIcon></Button>
                    </div>

                },
            },
        },
    ];


    useEffect(() => {
        let url = `/api/co/ccm/entity/agencyOfficeAddr/list?sEcho=3&`
            + `iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=agoaDtCreate&mDataProp_1=id.agoaCode&sSearch_1=${agoaCode}&iColumns=2`;
        axios.get(url)
            .then(result => {
                //console.log( `result = ${JSON.stringify(result)}`);
                setData(result.data.aaData);
            })
            .catch((error) => {
                console.log(error);
            });
    }, [agoaCode, agoaAgyCode]);

    useEffect(() => {
        if ( agoaCode && agoaAgyCode && agoaAddrType) {
            let url = `/api/co/ccm/entity/agencyOfficeAddr/list?sEcho=3&`
                + `iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=agoaDtCreate&mDataProp_1=id.agoaCode&sSearch_1=${agoaCode}&iColumns=2`;
            axios.get(url)
                .then(result => {
                    //console.log( `result = ${JSON.stringify(result)}`);
                    setData(result.data.aaData);
                })
                .catch((error) => {
                    console.log(error);
                });
        }
    }, [agoaCode, agoaAgyCode, agoaAddrType]);


    return (
        <>
            <C1DataTableClient data={data}
                columns={columns}
                title={t("agencyOffice.list.table.headers.agoAddrList")}
                showAdd={{
                    popupDialog: popupDialog
                }}>

            </C1DataTableClient>


            <Dialog open={open} onClose={handleClose} aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">{t("agencyOffice.dialog.officeAddress")}</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                    </DialogContentText>
                    <AddressDetails
                        data={data} inputData={addr}
                        handleInputChange={handleInputChange}
                        handleAgencySelectChange={handleAgencySelectChange}
                        handleAgencyOfficeIdChange={handleAgencyOfficeIdChange}
                        viewType={viewType} />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={handleSaveAddr} color="primary">
                        Save
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
};

export default AgencyOfficeAddrList;