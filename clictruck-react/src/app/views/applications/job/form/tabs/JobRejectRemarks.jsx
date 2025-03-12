import React from "react";
import C1DataTable from 'app/c1component/C1DataTable';
import { useTranslation } from "react-i18next";
import { customFilterDateTimeDisplay, formatDate } from "app/c1utils/utility";
import C1PopUp from "app/c1component/C1PopUp";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1InputField from "app/c1component/C1InputField";
import { Grid } from "@material-ui/core";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import { VisibilityOutlined } from "@material-ui/icons";
import C1TextArea from "app/c1component/C1TextArea";


const JobRejectRemarks = ({ parentJobId }) => {
    const { t } = useTranslation(["common"]);
    let detailDefaultData = {
        type: "",
        date: "",
        by: "",
        remarks: ""
    }

    const RemarksType = {
        A: { code: "A", desc: "Billing Approval" },
        V: { code: "V", desc: "Billing Verification" },
        R: { code: "R", desc: "Billing Rejection" },
        J: { code: "J", desc: "Job Rejection" }
    }

    const [showDetails, setShowDetails] = React.useState(false)
    const [popUpDetailsData, setPopUpDetailsData] = React.useState(detailDefaultData)
    const columns = [
        {
            name: "jobrId", // field name in the row object
            label: "ID", // column title that will be shown in table
            options: {
                display: false,
                filter: false,
                viewColumns: false,
            }
        },
        {
            name: "jobrRemarkType", // field name in the row object
            label: "Remarks Type", // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                filterOptions: {
                    names: Object.keys(RemarksType),
                    // renderValue: v => v
                    renderValue: v => {
                        return RemarksType[v].desc;
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        return RemarksType[v].desc;
                    }
                },
                filterType: 'dropdown',
                customBodyRender: (status, tableMeta, updateValue) => {
                    return status && RemarksType[status].desc
                }
            }
        },
        {
            name: "jobrUidCreated", // field name in the row object
            label: "Remarks By", // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },

        },
        {
            name: "jobrDtRemarks",
            label: "Remarks Date",
            options: {
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
                filter: true,
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateTimeDisplay
                },
            },
        },
        {
            name: "jobrReason",
            label: "Reason",
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    let str = value?.slice(0, 150)
                    return value?.length >= 150 ? str?.concat("...") : value
                }
            }
        },
        {
            name: "",
            label: t("listing:common.action"),
            options: {
                filter: false,
                sort: false,
                viewColumns: false,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const type = tableMeta.rowData[1]
                    const date = formatDate(tableMeta.rowData[3], true)
                    const by = tableMeta.rowData[2]
                    const remarks = tableMeta.rowData[4]
                    return (
                        <Grid container
                            justifyContent="center"
                            alignItems="center"   >
                            <C1LabeledIconButton
                                tooltip={t("buttons:view")}
                                label={t("buttons:view")}
                                action={() => handleShowDetails(type, date, by, remarks)}
                            >
                                <VisibilityOutlined />
                            </C1LabeledIconButton>
                        </Grid>
                    )
                }
            }
        },

    ];

    const handleShowDetails = (type, date, by, remarks) => {
        if (showDetails === false) {
            setPopUpDetailsData({ type, date, by, remarks })
            setTimeout(() => setShowDetails(true), 500)
        } else {
            setShowDetails(false)
            setPopUpDetailsData(detailDefaultData)
        }
    }

    return (
        <React.Fragment>
            <C1DataTable url="/api/v1/clickargo/clictruck/list/misc/job/remarks"
                columns={columns}
                isShowToolbar={false}
                defaultOrder="jobrDtRemarks"
                isRowsSelectable={false}
                filterBy={[{ attribute: "TCkJob.jobId", value: parentJobId }]}
                defaultOrderDirection="desc" isShowFilterChip={false} />

            <C1PopUp
                title={`Remarks Details`}
                openPopUp={showDetails}
                setOpenPopUp={handleShowDetails}
                maxWidth={"md"} >
                <C1TabContainer>
                    <Grid item lg={6} md={6} xs={12}>
                        <C1InputField disabled value={RemarksType[popUpDetailsData?.type]?.desc} label="Type" />
                        <C1InputField disabled value={popUpDetailsData?.date} label="Date" />
                        <C1InputField disabled value={popUpDetailsData?.by} label="By" />
                    </Grid>
                    <Grid item lg={6} md={6} xs={12}>
                        <C1TextArea
                            label="Remarks"
                            disabled={true}
                            value={popUpDetailsData?.remarks}
                            multiline
                            rowsMax={10} />
                    </Grid>
                </C1TabContainer>
            </C1PopUp>
        </React.Fragment>
    );
};


export default JobRejectRemarks;