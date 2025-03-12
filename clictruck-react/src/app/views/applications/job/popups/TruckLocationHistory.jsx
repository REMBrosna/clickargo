import React, {useEffect, useState} from "react";
import {Grid} from "@material-ui/core";
import C1TabContainer from "../../../../c1component/C1TabContainer";
import C1CategoryBlock from "../../../../c1component/C1CategoryBlock";
import EditLocationOutlinedIcon from "@material-ui/icons/EditLocationOutlined";

import useHttp from "../../../../c1hooks/http";
import Paper from "@material-ui/core/Paper";
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import TablePagination from "@material-ui/core/TablePagination";
import CircularProgress from "@material-ui/core/CircularProgress";
import {formatDate} from "../../../../c1utils/utility";
import StatusTrain from "../../../../clictruckcomponent/StatusTrain";
import AccountTreeOutlinedIcon from '@material-ui/icons/AccountTreeOutlined';

const TruckLocationHistory = (props) => {

    const {
        t,
        imei,
        end,
        stat,
        trips,
        jobId,
        tripIds,
    } = props;

    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const [page, setPage] = useState(0);
    const [data, setData] = useState([]);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setLoading(true)
        const startTime = stat / 1000;
        const endTime = end / 1000;
        sendRequest(`/api/v1/clickargo/clictruck/vehicle/location/history/${imei || "01"}/${startTime}/${endTime}`, "GET_TRUCK_HISTORY", "GET", {});
    }, []);

    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "GET_TRUCK_HISTORY": {
                    let data = res.data
                    setData(data);
                    setLoading(false);
                    break;
                }
                default: break;
            }
        }

    }, [urlId, isLoading, res]);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    return (
        <Grid item xs={12}>
            <C1TabContainer style={{display: "flex", justifyContent: "center"}}>
                {loading && <CircularProgress style={{position: "absolute",top: "260px"}} disableShrink />}
                <Grid item lg={5} md={5} sm={12} xs={12}>
                    <C1CategoryBlock
                        icon={<AccountTreeOutlinedIcon />}
                        title={t("job:tracking.jobStatusTrain")}
                    >
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12}>
                                <Paper>
                                    <StatusTrain
                                        jobId={jobId}
                                        trips={trips}
                                        tripIds={tripIds}
                                    />
                                </Paper>
                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>
                <Grid item lg={7} md={7} sm={12} xs={12}>
                    <C1CategoryBlock
                        icon={<EditLocationOutlinedIcon />}
                        title={t("job:tripDetails.truckLocationHistoryList")}
                    >
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12}>
                                <Paper>
                                    <TableContainer>
                                        <Table>
                                            <TableHead>
                                                <TableRow>
                                                    {false&&<TableCell width={200}>S/No</TableCell>}
                                                    <TableCell width={300}>Time</TableCell>
                                                    <TableCell>Location Name</TableCell>
                                                </TableRow>
                                            </TableHead>
                                            <TableBody>
                                                {data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row, index) => (
                                                    <TableRow key={index}>
                                                        {false &&<TableCell width={200}>{row?.name}</TableCell>}
                                                        <TableCell width={300}>{formatDate(row?.dateTime, true)}</TableCell>
                                                        <TableCell>{row?.loc}</TableCell>
                                                    </TableRow>
                                                ))}
                                            </TableBody>
                                        </Table>
                                    </TableContainer>
                                    <TablePagination
                                        rowsPerPageOptions={[5, 10, 25]}
                                        component="div"
                                        count={data.length}
                                        rowsPerPage={rowsPerPage}
                                        page={page}
                                        onPageChange={handleChangePage}
                                        onRowsPerPageChange={handleChangeRowsPerPage}
                                    />
                                </Paper>
                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>
            </C1TabContainer>
        </Grid>
    )
}

export default TruckLocationHistory;