import { Button, Grid, makeStyles} from "@material-ui/core";
import DeleteIcon from "@material-ui/icons/DeleteOutlineOutlined";
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import NearMeIcon from '@material-ui/icons/NearMeOutlined';
import PublishIcon from '@material-ui/icons/Publish';
import Add from '@material-ui/icons/AddOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ChipStatus from "app/atomics/atoms/ChipStatus";
import DataTable from "app/atomics/organisms/DataTable";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1IconButton from "app/c1component/C1IconButton";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import useHttp from "app/c1hooks/http";
import { TaxSequenceStatus } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import { MatxLoading } from "matx";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
    amountCell: {
        justifyContent: 'center',
        textAlign: 'right',
        display: 'flex',
        flex: 1
    }
}));

const TaxSequence = () => {
    // const { user } = useAuth();
    const { t } = useTranslation(["buttons", "payments", "common", "listing"]);
    // const classes = iconStyles();
    // const bdClasses = useStyles();
    // const history = useHistory();

    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();
    const [loading, setLoading] = useState(false);
    const [showHistory, setShowHistory] = useState(false);
    const [filterHistory, setFilterHistory] = useState([{ attribute: "history", value: "default" }]);
    const [isRefresh, setRefresh] = useState(false);
    const [showNewSequence, setShowNewSequence] = useState(false)
    const [validationErrors, setValidationErrors] = useState({})

    const defaultSeqState =
    {
        "otherLangDesc": null,
        "coreMstLocale": null,
        "stId": null,
        "stPrefix": "",
        "stRangeBegin": "",
        "stRangeEnd": "",
        "stRangeCurrent": 0,
        "stRangeFormat": "%08d",
        "stDtCreate": null,
        "stUidCreate": null,
        "stDtLupd": null,
        "stUidLupd": null
    }
    const [tempSeqState, setTempSeqState] = useState(defaultSeqState)
    // {
    //     prefix: null,
    //     sequenceStart: null,
    //     sequenceEnd: null
    // })

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "submitSequence": {
                    // window.location.reload(); //refresh whole page
                    setShowNewSequence(false); // close popup
                    setRefresh(true); // refresh table

                    break;
                }
                case "deleteSequence": {
                    setRefresh(true);
                    break;
                }
                default: break;
            }


        }
        if (validation) {
            console.log(validation)
            setValidationErrors({ ...validation })
        }
    }, [urlId, res, isLoading, error, validation]);

    useEffect(() => {
        if (isRefresh) setRefresh(false);
    }, [isRefresh])

    useEffect(() => {
        console.log("History", showHistory);
        if (showHistory) {
            setFilterHistory([{ attribute: "history", value: "history" }]);
        } else {
            setFilterHistory([{ attribute: "history", value: "default" }])

        }
    }, [showHistory]);

    const submitNewSequence = () => {

        const url = `/api/v1/clickargo/clictruck/sagetax`;
        const senId = "submitSequence";
        const method = "POST";
        const body = tempSeqState;

        sendRequest(url, senId, method, body);
    }

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    const columns = [
        {
            name: "stId",
            label: "Id",
        },
        // // prefix num req
        // {
        //     name: "prefixLeft",
        //     options: {
        //         display: "excluded"
        //     }
        // },
        // {
        //     name: "prefixMid",
        //     options: {
        //         display: "excluded"
        //     }
        // },
        // // show prefix 
        // {
        //     name: "prefixYear",
        //     label: "Prefix",
        //     options:{
        //         sort: true,
        //         filter: true,
        //         filterType: 'custom',
        //         customBodyRender: (value, tableMeta, updateValue) => {
        //             const leftPrefix = formatPrefixHandle(tableMeta.rowData[1])
        //             const midPrefix = formatPrefixHandle(tableMeta.rowData[2])

        //             const prefixDate = tableMeta.rowData[3]?.toString().slice(2)

        //             return `${leftPrefix}.${midPrefix}.${prefixDate}`
        //         }
        //     }
        // },
        {
            name: "stPrefix",
            label: t("listing:taxSequence.prefix"),
        },
        {
            name: "stRangeBegin",
            label: t("listing:taxSequence.sequenceA"),
            options: {
                filter: true,
                sort: true,
                filterType: 'custom',
                filterOptions: {
                    display: (filterList, onChange, index, column) => {
                        return <C1InputField
                            label={column.label}
                            name={column.name}
                            isServer
                            type="number"
                            onChange={event => {
                                filterList[index][0] = event.target.value;
                                onChange(filterList[index], index, column);
                            }}
                            value={filterList[index][0] || ""} />
                    }
                },
            }
        },
        {
            name: "stRangeEnd",
            label: t("listing:taxSequence.sequenceB"),
            options: {
                filter: true,
                sort: true,
                filterType: 'custom',
                filterOptions: {
                    display: (filterList, onChange, index, column) => {
                        return <C1InputField
                            label={column.label}
                            name={column.name}
                            isServer
                            type="number"
                            onChange={event => {
                                filterList[index][0] = event.target.value;
                                onChange(filterList[index], index, column);
                            }}
                            value={filterList[index][0] || ""} />
                    }
                },
            }
        },
        {
            name: "stRangeCurrent",
            label: t("listing:taxSequence.used"),
            options: {
                filter: true,
                sort: true,
                filterType: 'custom',
                filterOptions: {
                    display: (filterList, onChange, index, column) => {
                        return <C1InputField
                            label={column.label}
                            name={column.name}
                            isServer
                            type="number"
                            onChange={event => {
                                filterList[index][0] = event.target.value;
                                onChange(filterList[index], index, column);
                            }}
                            value={filterList[index][0] || ""} />
                    }
                },
            }
        },
        {
            name: "stStatus",
            label: t("listing:taxSequence.status"),
            options: {
                sort: true,
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: [TaxSequenceStatus.ACTIVE.code, TaxSequenceStatus.INACTIVE.code, TaxSequenceStatus.DELETED.code, TaxSequenceStatus.EXPIRED.code],
                    renderValue: v => {
                        switch (v) {
                            case TaxSequenceStatus.ACTIVE.code: return TaxSequenceStatus.ACTIVE.desc;
                            case TaxSequenceStatus.INACTIVE.code: return TaxSequenceStatus.INACTIVE.desc;
                            case TaxSequenceStatus.DELETED.code: return TaxSequenceStatus.DELETED.desc;
                            case TaxSequenceStatus.EXPIRED.code: return TaxSequenceStatus.EXPIRED.desc;
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case TaxSequenceStatus.ACTIVE.code: return TaxSequenceStatus.ACTIVE.desc;
                            case TaxSequenceStatus.INACTIVE.code: return TaxSequenceStatus.INACTIVE.desc;
                            case TaxSequenceStatus.DELETED.code: return TaxSequenceStatus.DELETED.desc;
                            case TaxSequenceStatus.EXPIRED.code: return TaxSequenceStatus.EXPIRED.desc;
                            default: break;
                        }
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    let status = "";

                    switch (value) {
                        case "C": {
                            // status = "COMPLETED";
                            status = <ChipStatus text="Completed" color="#0095A9" />
                            break;
                        }
                        case "D": {
                            // status = "DELETED";
                            status = <ChipStatus text="Deleted" color="#b4a0a3" />
                            break;
                        }
                        case "E": {
                            // status = "COMPLETED";
                            status = <ChipStatus text="Expired" color="#0095A9" />
                            break;
                        }
                        default: return getStatusDesc(value);
                    }
                    return status;

                }
            }
        },
        {
            name: "stDtCreate",
            label: t("listing:taxSequence.dtCreate"),
            options: {
                filter: true,
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        // {
        //     name: "paymentState",
        //     label: "Payment Status",
        //     options:{
        //         sort: true,
        //         filter: true,
        //     }
        // },
        {
            name: "action",
            label: t("listing:common.action"),
            options: {
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue) => {

                    const seqId = tableMeta.rowData[0];
                    const status = tableMeta.rowData[5];
                    if (status === "I")
                        return <Grid container direction="row" justifyContent='center' alignItems="center">
                            <Grid item sm={6} xs={6}>
                                <C1LabeledIconButton tooltip={t("buttons:delete")}
                                    label={t("buttons:delete")}
                                    action={(e) => handleCancelConfirm(e, seqId)}>
                                    <DeleteIcon />
                                </C1LabeledIconButton>
                            </Grid>
                        </Grid>
                    else return null
                }
            }
        }
    ]

    const formatPrefixHandle = (value) => {
        if (value < 10) {
            return `00${value}`
        } else if (value >= 10 && value < 100) {
            return `0${value}`
        } else {
            return `${value}`
        }
    }

    const onlyNumber = (e) => {
        if (e.charCode < 48) {
            return e.preventDefault();
        }
    }

    const handleCancelConfirm = (e, id) => {

        const url = `/api/v1/clickargo/clictruck/sagetax/${id}`;
        const sendId = "deleteSequence";
        const method = "DELETE";
        sendRequest(url, sendId, method);
    }

    const handleShowNewSequence = () => {
        setValidationErrors({})
        setShowNewSequence(true)
    }

    const toggleHistory = (filter) => {
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    // function formatTaxPrefix(number) {
    //     // Convert the number to a string and split it into groups of three digits
    //     const numberString = String(number);
    //     const groups = numberString.match(/\d{1,3}/g);

    //     // Join the groups with a dot and add a dash before the last two digits
    //     const formattedNumber = groups.join('.') + '-' + numberString.slice(-2);

    //     return formattedNumber;
    //   }

    function formatTaxPrefix(input) {
        // Use regular expressions to insert the desired separators
        const formattedString = input.replace(/(\d{3})(\d{3})(\d{2})$/, "$1.$2-$3.");
        return formattedString;
    }

    const handleSequenceChange = (e) => {
        let { name, value } = e.target;
        if (name === "stPrefix") {
            const regex = /^\d{8}$/;
            const isEightDigitNumber = regex.test(value);
            if (isEightDigitNumber) {

                value = formatTaxPrefix(value);
            }
        }

        setTempSeqState({
            ...tempSeqState,
            [name]: value
        })

        // if (tempSeqState?.stRangeEnd > tempSeqState?.stRangeBegin){
        //     const stRangeCurrent = tempSeqState?.stRangeEnd - tempSeqState?.stRangeBegin;
        //     setTempSeqState({
        //         ...tempSeqState,
        //         stRangeCurrent : stRangeCurrent
        //     })
        // }
    }

    let elNewSequence = <Grid container spacing={1}>
        <Grid item>
            <Button
                variant="contained"
                style={{
                    backgroundColor: "#14b1ed", color: "#fff",
                    padding: '10px 20px 10px 20px', fontWeight: "bold"
                }} onClick={() => setShowNewSequence(true)} startIcon={<PublishIcon />}>New Sequence
            </Button>
        </Grid>
    </Grid>

    let elAction = <C1IconButton tooltip={"Submit"} childPosition="right">
        <NearMeIcon color="primary" fontSize="large" onClick={submitNewSequence}></NearMeIcon>
    </C1IconButton>


    return loading ? <MatxLoading /> : (
        <React.Fragment>

            <C1PopUp
                title={t("listing:taxSequence.newSeq")}
                openPopUp={showNewSequence}
                setOpenPopUp={setShowNewSequence}
                maxWidth={'sm'}
                actionsEl={elAction}
            >
                <React.Fragment>
                    <C1CategoryBlock
                        icon={<DescriptionIcon />}
                        title={"Sequence Details"}
                    >
                        <C1InputField
                            label={t("listing:taxSequence.prefix")}
                            name="stPrefix"
                            // required
                            onChange={handleSequenceChange}
                            value={tempSeqState?.stPrefix}
                            error={validationErrors?.stPrefix !== undefined}
                            helperText={validationErrors?.stPrefix ? validationErrors?.stPrefix : ""}
                        />
                        <C1InputField
                            label={t("listing:taxSequence.start")}
                            name="stRangeBegin"
                            // required
                            type="number"
                            inputProps={{
                                onKeyPress: onlyNumber
                            }}
                            onChange={handleSequenceChange}
                            value={tempSeqState?.stRangeBegin}
                            error={validationErrors?.stRangeBegin !== undefined}
                            helperText={validationErrors?.stRangeBegin ? validationErrors?.stRangeBegin : ""}
                        />
                        <C1InputField
                            label={t("listing:taxSequence.end")}
                            name="stRangeEnd"
                            // required
                            type="number"
                            inputProps={{
                                onKeyPress: onlyNumber
                            }}
                            onChange={handleSequenceChange}
                            value={tempSeqState?.stRangeEnd}
                            error={validationErrors?.stRangeEnd !== undefined}
                            helperText={validationErrors?.stRangeEnd ? validationErrors?.stRangeEnd : ""}
                        />
                    </C1CategoryBlock>
                </React.Fragment>
            </C1PopUp>

            <DataTable
                columns={columns}
                isServer={true}
                defaultOrder="stDtCreate"
                defaultOrderDirection={"asc"}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={true}
                // url={"api/v1/clickargo/clictruck/gli/finance/tax-management/tax-sequence"}
                url={`/api/v1/clickargo/clictruck/sagetax`}
                isRefresh={isRefresh}
                isShowToolbar={true}
                isShowPagination={true}
                isShowFilterChip
                showActiveHistoryButton={toggleHistory}
                filterBy={filterHistory}
                showAddButton={[
                    {
                        label: t("listing:taxSequence.newSeq").toUpperCase(),
                        icon: <Add />,
                        action: () => handleShowNewSequence()
                    }
                ]}
            />

        </React.Fragment>
    )

}

export default TaxSequence;
