import useAuth from "../../../../hooks/useAuth";
import useHttp from "../../../../c1hooks/http";
import React, { useEffect, useState } from "react";
import {Grid, Select} from "@material-ui/core";
import { useStyles } from "../../../../c1utils/styles";
import {useTranslation} from "react-i18next";
import C1CategoryBlock from "../../../../c1component/C1CategoryBlock";
import PlaceOutlinedIcon from "@material-ui/icons/PlaceOutlined";
import TruckTraceFrame from "../trackTrace/TruckTraceFrame";
import {LocalShippingOutlined} from "@material-ui/icons";
import {getColorFromCode} from "../../../../c1utils/const";
const TruckTrackingDetails = () => {
    const classes = useStyles();
    const { t } = useTranslation(["administration"]);
    const { sendRequest, res, urlId, isLoading, error } = useHttp();
    const [jsonBody, setJsonBody] = useState({});
    const [vhGpsImeiList, setVhGpsImeiList] = useState([]);
    const [selectVechileSize, setSelectVechileSize] = useState(5);
    const [vhIdList, setVhIdList] = useState([]);
    const [truckDetailArr, setTruckDetailArr] = useState([]);
    const { user } = useAuth();

    useEffect(() => {
        // fetch vehicle by account id
        sendRequest(
            `/api/v1/clickargo/clictruck/administrator/trackingTrucks/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=vhPlateNo&mDataProp_1=TcoreAccn.accnId&sSearch_1=${user?.coreAccn?.accnId}&iColumns=2`,
            "getCompanyTruck", "get"
        );
    }, []);

    const handleChangeMultiple = (event) => {
        const { options } = event.target;
        const value = [];
        for (let i = 0, l = options.length; i < l; i += 1) {
            if (options[i].selected) {
                value.push(options[i].value);
            }
        }

        setVhIdList(value);
        setVhGpsImeiListByTruckIdList(value);
    };

    const setVhGpsImeiListByTruckIdList = (truckIdList) => {
        let selectedTruckList = truckDetailArr.filter((truck) =>
            truckIdList.includes(truck.vhId)
        );
        let iMeiArray = selectedTruckList.reduce((pre, cur) => {
            pre.push(cur.vhGpsImei);
            return pre;
        }, []);
        setVhGpsImeiList(iMeiArray);
    };

    useEffect(() => {
        if (!isLoading && res && !error) {
            if (urlId === "getCompanyTruck") {
                const trucks = res?.data?.aaData;
                setTruckDetailArr(trucks);
                setSelectVechileSize(computeSelectVehicleSize(trucks.length));
            }
        }
    }, [urlId, isLoading, error, res]);

    useEffect(() => {
        const colors = vhIdList.map(id => {
            const truck = truckDetailArr.find(truck => truck.vhId === id);
            return truck ? getColorFromCode(truck?.colorCode) : null;
        }).filter(color => color !== null);

        const json = {
            fromTime: null, //Math.trunc(new Date().getTime() / 1000),
            endTime: "0",
            units: vhGpsImeiList,
            colors: colors.length > 0 ? colors : [],
            coordinates: "",
            latest: 1,
            radius: ""
        };
        setJsonBody(json);
    }, [vhGpsImeiList, vhGpsImeiList.length, truckDetailArr, vhIdList]);

    const computeSelectVehicleSize = (size) => {
        if (size > 20) {
            return 20;
        } else if (size < 5) {
            return 5;
        } else {
            return size;
        }
    };

    return (
        <React.Fragment>
            <Grid container spacing={3} className={classes.gridContainer + " FullHeight"}>
                <Grid item lg={2} xs={12}>
                    <C1CategoryBlock
                        icon={<LocalShippingOutlined />} title={t("administration:truckTracking.listing.title")}
                    >
                        <Select
                            style={{ marginTop: 10 }}
                            size="medium"
                            margin="normal"
                            value={vhIdList || []}
                            name="plateNo"
                            multiple
                            native
                            disableunderline="true"
                            displayEmpty
                            variant="outlined"
                            onChange={handleChangeMultiple}
                            inputProps={{
                                size: selectVechileSize < 20 ? selectVechileSize : 20
                            }}
                            fullWidth
                        >
                            {truckDetailArr.map((truck, idx) => {
                                return (
                                    <option value={truck?.vhId} key={idx}>
                                        {truck?.vhPlateNo}
                                    </option>
                                );
                            })}
                        </Select>
                    </C1CategoryBlock>
                </Grid>
                <Grid item lg={10} xs={12}>
                    <C1CategoryBlock
                        icon={<PlaceOutlinedIcon />}
                        title={t("administration:truckTracking.listing.location")} />
                    <div style={{ marginBottom: 10 }}></div>
                    <TruckTraceFrame jsonBody={jsonBody}></TruckTraceFrame>
                </Grid>
            </Grid>
        </React.Fragment>
    );
};

export default TruckTrackingDetails;
