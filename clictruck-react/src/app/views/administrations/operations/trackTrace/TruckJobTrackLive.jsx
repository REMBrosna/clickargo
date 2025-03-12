import React, {
  useContext,
  useEffect,
  useImperativeHandle,
  useState,
} from "react";
import { Grid, Box, Checkbox, TextField, Button, Chip, Avatar } from "@material-ui/core";

import { useStyles } from "app/c1utils/styles";
import { makeStyles } from "@material-ui/core/styles";
import { useTranslation } from "react-i18next";
import ExploreOutlinedIcon from "@material-ui/icons/ExploreOutlined";
import PlaceOutlinedIcon from "@material-ui/icons/PlaceOutlined";
import useHttp from "app/c1hooks/http";
import useAuth from "app/hooks/useAuth";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import TruckTraceFrame from "./TruckTraceFrame";
import Accordion from "@material-ui/core/Accordion";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import AccordionActions from "@material-ui/core/AccordionActions";
import Typography from "@material-ui/core/Typography";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import C1InputField from "app/c1component/C1InputField";
import { deepOrange, green } from '@material-ui/core/colors';

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

const useStylesAvatar = makeStyles((theme) => ({

  square: {
    //color: theme.palette.getContrastText(deepOrange[500]),
    backgroundColor: "#878787",
    width: theme.spacing(3),
    height: theme.spacing(3),
  },
  rounded: {
    color: '#fff',
    backgroundColor: green[500],
  },
}));

const TruckJobTrackLive = ({}) => {
  const classes = useStyles();
  const classesAccord = useStylesAccord();
  const classesAvatar = useStylesAvatar();
  const { t } = useTranslation(["administration"]);

  const { sendRequest, res, urlId, isLoading, error } = useHttp();

  const [jsonBody, setJsonBody] = useState({});
  const [vhGpsImeiList, setVhGpsImeiList] = useState([]);
  const [vhColorsList, setVhColorsList] = useState([]);
  //const [selectVechileSize, setSelectVechileSize] = useState(5);
  const [totalSelected, setTotalSelected] = useState(0);
  const [truckAllArr, setTruckAllArr] = useState([]); // All truck in arrays,

  // [{deptId, selected:false, dept, vehArr:[{isSelectedVh:false, vh:},{}]}, {}]
  const [truckFilterDeptArr, setTruckFilterDeptArr] = useState([]); // All truck split by department in arrays

  const [selectedAllTruck, setSelectedAllTruck] = useState(false); // Is selected all trucks
  const [filterVeh, setFilterVeh] = useState("");
  const [isVisibleLeft, setIsVisibleLeft] = useState(true);

  const { user } = useAuth();

  const noDeptment = { deptId: "NO_DEPT2198043", deptName: "General" };

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
      "get"
    );
  }, []);
  /*-
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
    let selectedTruckList = truckAllArr.filter((truck) =>
      truckIdList.includes(truck.vhId)
    );
    let iMeiArray = selectedTruckList.reduce((pre, cur) => {
      pre.push(cur.vhGpsImei);
      return pre;
    }, []);

    console.log("iMeiArray:", iMeiArray);
    setVhGpsImeiList(iMeiArray);
  };
*/
  useEffect(() => {
    if (!isLoading && res && !error) {
      if (urlId === "getCompanyTruck") {
        initTruckAllArr(res?.data?.aaData);
        initTruckFilterDeptArr(res?.data?.aaData);
        //setSelectVechileSize(computeSelectVehicleSize(res?.data?.aaData?.length));
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
      // console.log("result", result);
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

  const onClickDepartmentCheckbox = (e) => {
    e.stopPropagation();

    const deptId = e.target.name;
    const checked = e.target.checked;
    console.log("deptId", deptId, "checked", checked);

    let deptObj = truckFilterDeptArr?.filter((item) => item.deptId === deptId);

    deptObj = truckFilterDeptArr?.find((item) => item.deptId === deptId);
    deptObj.selected = checked;

    // update subElement
    for (const vehItem of deptObj?.vehArr) {
      vehItem.selected = checked;
    }

    console.log("deptObj", deptObj, "truckFilterDeptArr", truckFilterDeptArr);
    setTruckFilterDeptArrWrap([...truckFilterDeptArr]);
  };

  const onClickVehCheckbox = (e) => {
    const vhId = e.target.name;
    const checked = e.target.checked;
    console.log("vhId", vhId, "checked", checked);

    for (const deptObj of truckFilterDeptArr) {
      for (const vehItem of deptObj?.vehArr) {
        if (vhId === vehItem.veh.vhId) {
          vehItem.selected = checked;
          updateDeptCheckbox(deptObj, checked); //
          break;
        }
      }
    }
    /*-
    truckFilterDeptArr.forEach((deptObj) => {
      deptObj.vehArr.forEach((vehItem) => {
        if(vhId === vehItem.veh.vhId) {
          vehItem.selected = checked;
        }
      })
    });
*/
    setTruckFilterDeptArrWrap([...truckFilterDeptArr]);
  };

  const updateDeptCheckbox = (deptObj, checked) => {
    let sameVal = true;
    for (const vehItem of deptObj?.vehArr) {
      if (vehItem.selected != checked) {
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

  const onClickAllVehCheckbox = (e) => {
    const checked = e.target.checked;
    console.log("checked", checked);

    updateAllVehCheckbox(checked);

    setSelectedAllTruck(checked);
    setTruckFilterDeptArrWrap([...truckFilterDeptArr]);
  };

  const updateAllVehCheckbox = (checked) => {
    console.log("checked", checked);

    for (const deptObj of truckFilterDeptArr) {
      deptObj.selected = checked;
      for (const vehItem of deptObj?.vehArr) {
        vehItem.selected = checked;
      }
    }
  };

  const handleResetAction = () => {
    let checked = false;
    updateAllVehCheckbox(checked);

    setSelectedAllTruck(checked);
    setFilterVeh("");
    //not expanded
    for (const deptObj of truckFilterDeptArr) {
      deptObj.expanded = false;
    }
    setTruckFilterDeptArrWrap([...truckFilterDeptArr]);
    setVhGpsImeiList([]);
  };

  const setTruckFilterDeptArrWrap = (arr) => {
    console.log("truckFilterDeptArr", arr);
    computeVehInDept(arr);
    setTruckFilterDeptArr(arr);
  }

  const computeVehInDept = (arr) => {
    for (const deptObj of arr) {

      if(deptObj?.vehArr) {
        deptObj.total = deptObj?.vehArr.filter((vehItem)=>( (!vehItem.hidden))).length;
      }
      // console.log("deptObj.total", deptObj.total, deptObj?.vehArr);
      //if(deptObj?.vehArr) {
      //  deptObj.total = deptObj?.vehArr.length;
      //}
    }
  }

  const handleApplyAction = () => {
    let iMeiArray = [];
    let colourArray = [];
    for (const deptObj of truckFilterDeptArr) {
      for (const vehItem of deptObj?.vehArr) {
        if (
          vehItem.selected &&
          !vehItem.hidden &&
          vehItem?.veh &&
          vehItem?.veh?.vhGpsImei
        ) {
          iMeiArray.push(vehItem?.veh?.vhGpsImei);
          //console.log("deptObj.deptColor", deptObj.dept?.deptColor, deptObj);
          colourArray.push(deptObj.dept?.deptColor || 0); // default is 0, black color
        }
      }
    }
    setVhGpsImeiList(iMeiArray);
    setVhColorsList(colourArray);
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

  useEffect(() => {
    computeTotalSelected();
    computeAllSelected();
  }, [truckFilterDeptArr]);

  const inputChangeFilterVeh = (e) => {
    let val = e.target.value;
    setFilterVeh(val);
  }

  const inputChangeFilterVehImpl = (val) => {

    if (val && val.length > 0) {
      for (const deptObj of truckFilterDeptArr) {
        let isHiddenDept = true;
        for (const vehItem of deptObj?.vehArr) {
          // console.log("vehItem.vhPlateNo", vehItem.veh.vhPlateNo.toUpperCase().includes(val.toUpperCase())  );
          if (
            vehItem?.veh?.vhPlateNo &&
            vehItem.veh.vhPlateNo.toUpperCase().includes(val.toUpperCase())
          ) {
            isHiddenDept = false;
            vehItem.hidden = false;
          } else {
            vehItem.hidden = true;
          }
        }
        deptObj.hidden = isHiddenDept;
      }
    } else {
      // clear
      for (const deptObj of truckFilterDeptArr) {
        deptObj.hidden = false;
        for (const vehItem of deptObj?.vehArr) {
          vehItem.hidden = false;
        }
      }
    }
    setTruckFilterDeptArrWrap([...truckFilterDeptArr]);
  };

  useEffect(() => {
    inputChangeFilterVehImpl(filterVeh);
  }, [filterVeh]);

  const handleClickAccordionSummary = (deptId) => {
    let deptObj = truckFilterDeptArr.find(dept => (deptId === dept.deptId))
    
    console.log("deptId", deptId, deptObj);

    if( deptObj) {
      deptObj.expanded = !deptObj.expanded;
      setTruckFilterDeptArrWrap([...truckFilterDeptArr]);
    }
  }

  useEffect(() => {
    // const iframe = document.querySelector("iframe");

    let json = {
      fromTime: Math.trunc(new Date().getTime() / 1000 - 60 * 24),
      //fromTime: Math.trunc(new Date().getTime() / 1000 ),
      //fromTime: 0,
      //endTime: Math.trunc((new Date().getTime() / 1000) + 3600),
      endTime: 0,
      units: vhGpsImeiList,
      coordinates: "",
      latest: 1,
      radius: "",
      colors: vhColorsList,
      reset: (vhGpsImeiList.length == 0)   // reset is true, if vhGpsImeiList is empty
    };
    //iframe.contentWindow.postMessage(json, "*");
    setJsonBody(json);
  }, [vhGpsImeiList, vhGpsImeiList?.length, vhColorsList, vhColorsList?.length] );

  return (
    <React.Fragment>
      <C1ListPanel
        routeSegments={[{ name: t("administration:liveTracking.title") }]}
        guideId="clicdo.doi.co.jobs.list"
        isFullHeight={true}
      >
        <br />
        <Grid
          container
          spacing={3}
          className={classes.gridContainer + " FullHeight"}
        >
            <Grid item lg={2} md={3} xs={12}>
              <Box pl={2} pt={1} pb={1} style={{ backgroundColor: "#f0f8ff" }}>
                <div
                  style={{ display: "flex", justifyContent: "space-between" }}
                >
                  <span>
                    <b>Filter</b>
                  </span>
                  <span onClick={() => {}}></span>
                </div>
              </Box>

              <Box pl={2} pt={1} pr={2} mt={1} mb={1} style={{ backgroundColor: "#f0f8ff" }}>
                <TextField
                  label={"Search Truck License"}
                  size="small"
                  fullWidth
                  variant="outlined"
                  style={{ backgroundColor: "white" }}
                  onChange={(e) => inputChangeFilterVeh(e)}
                  value={filterVeh}
                  disabled={false}
                />
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={selectedAllTruck}
                      onChange={(e) => onClickAllVehCheckbox(e)}
                      style={{ padding: "2px" }}
                    />
                  }
                  label={"All Trucks License Number"}
                />
              </Box>

              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <span> Truck License Number</span>
                <span>{totalSelected} selected</span>
              </div>

              {truckFilterDeptArr?.map((item) => {
                return item.hidden ? (
                  <></>
                ) : (
                  <Accordion expanded={item.expanded} key={item.deptId}
                    onChange={ () => handleClickAccordionSummary(item.deptId)}>
                      
                    <AccordionSummary
                      expandIcon={<ExpandMoreIcon />}
                      aria-controls="panel1c-content"
                      id={`panel1c-header-${item.deptId}`}
                      className={classesAccord.summary}
                    >
                      <div className={classesAccord.column} style={{display:"flex"}}>
                        <FormControlLabel
                          control={
                            <Checkbox
                              checked={item.selected}
                              onChange={(e) => {
                                onClickDepartmentCheckbox(e);
                              }}
                              name={item.deptId}
                              style={{ padding: "2px" }}
                            />
                          }
                          label={item.dept?.deptName || item.deptId}
                        />
                        <Typography style={{paddingTop:3, backgroundColor:"#8e8e90", paddingLeft:10, paddingRight:10}}> {item.total} </Typography>
                      </div>
                    </AccordionSummary>
                    <AccordionDetails className={classesAccord.details} id={item.deptId} >
                      {item.vehArr.map((vehItem) => {
                        //<div>{veh?.vhPlateNo}</div>
                        return vehItem.hidden ? (
                          <></>
                        ) : (
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                            key={vehItem.veh.vhId}
                          >
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
                    onClick={(e) => handleApplyAction(e)}
                  >
                    Apply
                  </Button>
                </Grid>
              </Grid>
            </Grid>
          
          <Grid item lg={10} md={9} xs={12}>
            <C1CategoryBlock
              icon={<PlaceOutlinedIcon />}
              title={t("administration:liveTracking.location")}
            />
            <div style={{ marginBottom: 10 }}></div>
            <TruckTraceFrame jsonBody={jsonBody}></TruckTraceFrame>
          </Grid>
        </Grid>
      </C1ListPanel>
    </React.Fragment>
  );
};

export default TruckJobTrackLive;
