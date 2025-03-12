import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import {Button, Checkbox, Grid, Select} from "@material-ui/core";
import { useStyles } from "app/c1utils/styles";
import ExploreOutlinedIcon from '@material-ui/icons/ExploreOutlined';
import PlaceOutlinedIcon from '@material-ui/icons/PlaceOutlined';
import C1DateTimeField from "app/c1component/C1DateTimeField";
import useHttp from "app/c1hooks/http";
import useAuth from "app/hooks/useAuth";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import TruckTraceFrame from "./TruckTraceFrame"
import Accordion from "@material-ui/core/Accordion";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import {makeStyles} from "@material-ui/core/styles";

const useStylesAccord = makeStyles((theme) => ({
    root: {
        width: "100%",
    },
    summary: {
        fontSize: theme.typography.pxToRem(15),
        fontWeight: theme.typography.fontWeightRegular,
        backgroundColor: "#f0f7ff",
    },
    heading: {
        fontSize: theme.typography.pxToRem(15),
    },
    secondaryHeading: {
        fontSize: theme.typography.pxToRem(15),
        color: theme.palette.text.secondary,
    },
    icon: {
        verticalAlign: "bottom",
        height: 20,
        width: 20,
    },
    details: {
        alignItems: "center",
        display: "block",
    },
    column: {
        // flexBasis: "33.33%",
    },
    helper: {
        borderLeft: `2px solid ${theme.palette.divider}`,
        padding: theme.spacing(1, 2),
    },
    link: {
        color: theme.palette.primary.main,
        textDecoration: "none",
        "&:hover": {
            textDecoration: "underline",
        },
    },
}));

const TruckJobTrackHistorical = ({ }) => {
    const classesAccord = useStylesAccord();
    const classes = useStyles();
    const { t } = useTranslation(["administration"]);
    const now = new Date();
    const [inputData, setInputData] = useState({ fromTime: new Date(now.getFullYear(), now.getMonth(), now.getDate()), endTime: now });
    const [truckDetailArr, setTruckDetailArr] = useState([]);
    const [selectedTruck, setSelectedTruck] = useState({});
    const [jsonBody, setJsonBody] = useState({});
    const [truckFilterDeptArr, setTruckFilterDeptArr] = useState([]);
    const { user } = useAuth();
    const { sendRequest, res, urlId, isLoading, error } = useHttp();
    const [totalSelected, setTotalSelected] = useState(0);
    const [selectedAllTruck, setSelectedAllTruck] = useState(false);
    const [truckAllArr, setTruckAllArr] = useState([]);
    const noDeptment = { deptId: "NO_DEPT2198043", deptName: "General" };
    const handleDateTimeChange = (name, e) => {
        setInputData({ ...inputData, [name]: e });
    }
/*
    const getVhGpsImei = (vhId) => {

        let selectedTruckList = truckDetailArr.filter(truck => (truck.vhId === vhId));
        if (selectedTruckList && selectedTruckList.length > 0) {
            let imei = selectedTruckList[0].vhGpsImei;
            return imei;
            // delay 0.5 second
            //setTimeout(() => setInputData({ ...inputData, "vhGpsImei": imei }), 500);
        }
    }
*/
    useEffect(() => {
        if (inputData?.fromTime && inputData?.endTime && selectedTruck?.vhId) {
            //const iframe = document.querySelector("iframe");
            console.log("inputData", inputData)
            console.log("deptColor", selectedTruck?.tckCtDept?.deptColor)
                let json = {
                fromTime: (inputData?.fromTime / 1000),
                endTime: (inputData?.endTime / 1000),
                units: [selectedTruck?.vhGpsImei],
                coordinates: "",
                latest: 1,
                radius: "",
                alertIfNoImei:true,
                colors:[selectedTruck?.tckCtDept?.deptColor]
            };
            //iframe.contentWindow.postMessage(json, "*");
            console.log("json", json)
            setJsonBody(json);
        }

    }, [inputData?.fromTime, inputData?.endTime, selectedTruck?.vhId]);

    // fetch all trucks by account id;
    useEffect(() => {
        const params = new URLSearchParams({
            sEcho: 3,
            iDisplayStart: 0,
            iDisplayLength: 1000,
            iSortCol_0: 0,
            sSortDir_0: 'asc',
            iSortingCols: 1,
            mDataProp_0: 'vhPlateNo',
            mDataProp_1: 'TcoreAccn.accnId',
            sSearch_1: user?.coreAccn?.accnId || '',
            mDataProp_2: 'department',
            sSearch_2: 'Y',
            mDataProp_3: 'vhStatus',
            sSearch_3: 'A',
            iColumns: 2
        });
        sendRequest(
            `/api/v1/clickargo/clictruck/administrator/vehicle/list?${params.toString()}`,
            "getCompanyTruck",
            'get', null
        )
    }, []);

    useEffect(() => {
        if (!isLoading && res && !error) {
            if (urlId === "getCompanyTruck") {
                setTruckDetailArr(res?.data?.aaData);
            }
        }
    }, [urlId, isLoading, error, res]);
    useEffect(() => {
        computeTotalSelected();
        computeAllSelected();
    }, [truckFilterDeptArr]);

    useEffect(() => {
        if (!isLoading && res && !error) {
            if (urlId === "getCompanyTruck") {
                initTruckAllArr(res?.data?.aaData);
                initTruckFilterDeptArr(res?.data?.aaData);
            }
        }
    }, [urlId, isLoading, error, res]);

    const initTruckAllArr = (data) => {
        setTruckAllArr(data);
    };
    const initTruckFilterDeptArr = (data) => {
        let noDeptVeh = [];

        let groupedByDepartment = data.reduce((result, veh) => {
            const tckCtDept = veh.tckCtDept;
            const selectedVeh = { selected: false, hidden: false, veh };
            // no department
            if (!tckCtDept || !tckCtDept?.deptId) {
                noDeptVeh.push(selectedVeh);
                return result;
            }
            let deptObj = result.filter((item) => item.deptId === tckCtDept?.deptId);
            if (!deptObj || deptObj?.length == 0) {
                result.push({
                    deptId: tckCtDept?.deptId,
                    selected: false,
                    hidden: false,
                    expanded: false,
                    total: 0,
                    dept: tckCtDept,
                    vehArr: [selectedVeh],
                });
            } else {
                deptObj[0].vehArr.push(selectedVeh);
            }

            return result;
        }, []);

        //groupedByDepartment.set("000", noDeptVeh);
        groupedByDepartment.push({
            deptId: noDeptment.deptId,
            selected: false,
            hidden: false,
            expanded: false,
            total:0,
            dept: noDeptment,
            vehArr: noDeptVeh,
        });

        setTruckFilterDeptArrWrap(groupedByDepartment);
    };
    const computeTotalSelected = () => {
        let total = 0;
        for (const deptObj of truckFilterDeptArr) {
            for (const vehItem of deptObj?.vehArr) {
                if (vehItem.selected && !vehItem.hidden) {
                    total++;
                }
            }
        }
        setTotalSelected(total);
    };
    const computeAllSelected = () => {
        let isAllSelected = true;
        for (const deptObj of truckFilterDeptArr) {
            for (const vehItem of deptObj?.vehArr) {
                if (!vehItem.selected) {
                    isAllSelected = false;
                }
            }
        }
        setSelectedAllTruck(isAllSelected);
    };
    const handleClickAccordionSummary = (deptId) => {
        let deptObj = truckFilterDeptArr.find(dept => (deptId === dept.deptId))
        if( deptObj) {
            deptObj.expanded = !deptObj.expanded;
            setTruckFilterDeptArrWrap([...truckFilterDeptArr]);
        }
    }
    const setTruckFilterDeptArrWrap = (arr) => {
        computeVehInDept(arr);
        setTruckFilterDeptArr(arr);
    }
    const computeVehInDept = (arr) => {
        for (const deptObj of arr) {
            if(deptObj?.vehArr) {
                deptObj.total = deptObj?.vehArr.filter((vehItem)=>( (!vehItem.hidden))).length;
            }
        }
    }
    const onClickDepartmentCheckbox = (selectedDeptId) => {
        const updatedTruckFilterDeptArr = truckFilterDeptArr.map((item) => {
            if (item.deptId === selectedDeptId) {
                // Check the clicked department
                return { ...item, selected: true };
            }
            // Uncheck all other departments
            return { ...item, selected: false };
        });

        // Update the state with the modified array
        setTruckFilterDeptArr(updatedTruckFilterDeptArr);
    };
    const updateDeptCheckbox = (deptObj, checked) => {
        let sameVal = true;
        for (const vehItem of deptObj?.vehArr) {
            if (vehItem.selected !== checked) {
                sameVal = false;
            }
        }
        // all same;
        if (sameVal) {
            deptObj.selected = checked;
        } else {
            deptObj.selected = false;
        }
    };

    const onClickVehCheckbox = (e) => {
        const vhId = e.target.name;
        const checked = e.target.checked;
        let selectedTruck = null;
        // Update vehicle selection
        truckFilterDeptArr.forEach((deptObj) => {
            deptObj.vehArr.forEach((vehItem) => {
                if (vhId === vehItem.veh.vhId) {
                    vehItem.selected = checked;
                    updateDeptCheckbox(deptObj);  // Update department based on vehicle state
                }else {
                    vehItem.selected = false;
                    updateDeptCheckbox(deptObj);
                }
            });
            selectedTruck = truckDetailArr.find(truck => (truck.vhId === vhId));
            setSelectedTruck(selectedTruck);
        });
    };
    const handleInputChangeTruck = (e) => {

        // vhId CKCTVEH001879
        // e.target.value is vhId
        let vhId = e.target.value;
        let selectedTruck = truckDetailArr.find(truck => (truck.vhId === vhId));
        setSelectedTruck(selectedTruck);
    };
    const handleApplyAction = (e) => {
        const selectedVehicles = truckFilterDeptArr.reduce((acc, deptObj) => {
            const selectedTrucksInDept = deptObj.vehArr.filter(vehItem => vehItem.selected);
            return [...acc, ...selectedTrucksInDept];
        }, []);
        if (selectedVehicles.length === 1) {
            setSelectedTruck(selectedVehicles[0].veh);
        }
        setSelectedTruck(selectedVehicles.map(item => item.veh));
        console.log("selectedTruck:", selectedTruck);
    };

    const handleResetAction = () => {
        const updatedTruckFilterDeptArr = truckFilterDeptArr.map((deptObj) => ({
            ...deptObj,
            selected: false,
            expanded: false,
            vehArr: deptObj.vehArr.map((vehItem) => ({
                ...vehItem,
                selected: false,
                hidden: false
            }))
        }));
        setTruckFilterDeptArr(updatedTruckFilterDeptArr);
        setJsonBody({});
    };

    return (
        <React.Fragment>
            <C1ListPanel
                routeSegments={[
                    { name: t("administration:liveTracking.history") }
                ]}
                guideId="clicdo.doi.co.jobs.list"
                isFullHeight={true}>
                <br />
                <Grid container spacing={3} className={classes.gridContainer + " FullHeight"}>
                    <Grid item lg={2} xs={12} >

                        <C1CategoryBlock
                            icon={<ExploreOutlinedIcon/>}
                            title={t("administration:liveTracking.details")}>

                            <C1DateTimeField
                                label={t("administration:liveTracking.start")}
                                name="fromTime"
                                disabled={false}
                                onChange={handleDateTimeChange}
                                required
                                value={inputData?.fromTime}
                            />
                            <C1DateTimeField
                                label={t("administration:liveTracking.end")}
                                name="endTime"
                                disabled={false}
                                onChange={handleDateTimeChange}
                                required
                                value={inputData?.endTime}
                            />
                            <div style={{display: "flex", justifyContent: "space-between"}}>
                                <span> Truck License Number</span>
                            </div>
                            {truckFilterDeptArr?.map((item) => {
                                return item.hidden ? (
                                    <></>
                                ) : (
                                    <Accordion expanded={item.expanded} key={item.deptId} onChange={() => handleClickAccordionSummary(item.deptId)}>
                                        <AccordionSummary
                                            expandIcon={<ExpandMoreIcon />}
                                            aria-controls="panel1c-content"
                                            id={`panel1c-header-${item.deptId}`}
                                            className={classesAccord.summary}>
                                            <div className={classesAccord.column} style={{ display: "flex" }}>
                                                <FormControlLabel
                                                    control={
                                                        <C1CategoryBlock
                                                            checked={item.selected}
                                                            onChange={() => onClickDepartmentCheckbox(item.deptId)} // no need for event object
                                                            name={item.deptId}
                                                            style={{ padding: "2px" }}
                                                        />
                                                    }
                                                    label={item.dept?.deptName || item.deptId}
                                                />
                                            </div>
                                        </AccordionSummary>
                                        <AccordionDetails className={classesAccord.details} id={item.deptId}>
                                            {item.vehArr.map((vehItem) => {
                                                return vehItem.hidden ? (
                                                    <></>
                                                ) : (
                                                    <div style={{ display: "flex", justifyContent: "space-between" }} key={vehItem.veh.vhId}>
                                                            <span>
                                                                <FormControlLabel
                                                                    control={
                                                                        <Checkbox
                                                                            checked={vehItem?.selected}
                                                                            onChange={(e) => onClickVehCheckbox(e)}
                                                                            name={vehItem?.veh.vhId}
                                                                            style={{ padding: "2px" }}
                                                                        />
                                                                    }
                                                                    label={vehItem?.veh.vhPlateNo}
                                                                />
                                                            </span>
                                                        <span>&nbsp;</span>
                                                    </div>
                                                );
                                            })}
                                        </AccordionDetails>
                                    </Accordion>
                                );
                            })}
                            <Grid container spacing={3}>
                                <Grid item lg={6}>
                                    <Button
                                        variant="outlined"
                                        fullWidth
                                        color="primary"
                                        onClick={(e) => handleResetAction(e)}
                                    >
                                        Reset
                                    </Button>
                                </Grid>
                                <Grid item lg={6}>
                                    <Button
                                        variant="outlined"
                                        fullWidth
                                        color="primary"
                                        value={selectedTruck?.vhId || ""}
                                        onClick={(e) => handleApplyAction(e)}
                                    >
                                        Apply
                                    </Button>
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item lg={10} xs={12}>
                        <C1CategoryBlock
                            icon={<PlaceOutlinedIcon/>}
                            title={t("administration:liveTracking.location")} />
                        <div style={{ marginBottom: 10 }}></div>
                        <TruckTraceFrame
                            jsonBody={jsonBody}>
                        </TruckTraceFrame>
                    </Grid>
                </Grid>
            </C1ListPanel>
        </React.Fragment>
    );
};

export default TruckJobTrackHistorical;