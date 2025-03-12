import React, {useState, useEffect} from "react";
import { Grid, MenuItem, Tooltip, Button } from "@material-ui/core";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import { DateRangeOutlined, LocalAtmOutlined, NearMeOutlined, DeleteOutline } from '@material-ui/icons'
import C1DateField from "app/c1component/C1DateField";
import C1SelectField from "app/c1component/C1SelectField";
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import useHttp from "app/c1hooks/http";

const vmlApiUrl = "/api/v1/clickargo/clictruck/administrator/vehMlog";

export default function VehicleMaintenance(props) {

    const {popUp, setPopUp} = props;
    let { action, vmlId } = props?.mlogData;
    let isViewMode = false;
    if (action === "view"){isViewMode = true};

    const vehId = props?.truckProps?.props?.inputData?.vhId;

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const vmlDataInit =
    {
        "vmlDtStart": null,
        "vmlDtEnd": null,
        "vmlStatus": null,
        "vmlCost": null,
        "vmlRemarks": null,
        "tckCtVeh": {
            "vhId": vehId
        }
    };
    const [vmlData, setVmlData] = useState(vmlDataInit);

    const elStatusList = 
        React.Children.toArray([
            <MenuItem value={"A"}>Active</MenuItem>,
            <MenuItem value={"I"}>Inactive</MenuItem>
        ]);

    const elAction = 
    <>
        <Tooltip title={"delete"}>
            <Button style={{ float: 'right' }} onClick={deleteHandler}>
                <DeleteOutline fontSize="large" color="primary" />
            </Button>
        </Tooltip>
        <Tooltip title={"submit"}>
            <Button style={{ float: 'right' }} onClick={submitHandler}>
                <NearMeOutlined fontSize="large" color="primary" />
            </Button>
        </Tooltip>
    </>;
    

    function submitHandler(){
        console.log("submit data", vmlData);
        if (action === "edit"){
            console.log("isedit");
            sendRequest(vmlApiUrl+"/"+vmlId, "updateVml","PUT",vmlData);
        } else {
            sendRequest(vmlApiUrl,"submitVml","POST",vmlData);
        }
    }

    function deleteHandler(){
        sendRequest(vmlApiUrl+"/"+vmlId, "deleteVml","DELETE",vmlData);
    }

    function changeHandler(k,v){
        // console.log("vml change",k,v);
        switch (k) {
            case "vmlDtStart":
            case "vmlDtEnd":
                const date = v.getTime();
                // console.log("date value", date);
                setVmlData((p)=>({...p, [k]:date}))
                break;
        
            default:
                setVmlData((p)=>({...p, [k]:v}))
                break;
        }
    }



    useEffect(() => {
      const exec = setTimeout(() => {
        console.log("vml data", vmlData)
      }, 1000);
    
      return () => {
        clearTimeout(exec);
      }
    }, [vmlData])

    useEffect(() => {
      if(vmlId){
        sendRequest(vmlApiUrl+"/"+vmlId, "getVmlData", "GET");
      }
    }, [])
    

    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "getVmlData":
                    setVmlData(res?.data);
                    break;
                case "updateVml":
                case "submitVml":
                case "deleteVml":
                default:
                    setPopUp(false);
                    break;
            }
        }
    }, [isLoading, res, error, urlId])
    
    

    return(
        <>
        <C1PopUp
            title="Vehicle Maintenance"
            openPopUp={popUp}
            setOpenPopUp={setPopUp}
            actionsEl={isViewMode? null : elAction}
        >
            <Grid container spacing={3}>
                <Grid item md={6}>
                    <C1CategoryBlock icon={<DateRangeOutlined />} title="Period">
                        <C1DateField 
                            name="vmlDtStart"
                            label="Start"
                            onChange={changeHandler}
                            value={vmlData?.vmlDtStart}
                            disabled={isViewMode}
                        />
                        <C1DateField
                            name="vmlDtEnd"
                            label="End" 
                            onChange={changeHandler}
                            value={vmlData?.vmlDtEnd}
                            disabled={isViewMode}
                        />
                    </C1CategoryBlock>
                </Grid>    
                <Grid item md={6}>
                    <C1CategoryBlock icon={<LocalAtmOutlined />} title="Status and Charges">
                        <C1SelectField
                            name="vmlStatus"
                            label="Status" 
                            onChange={(e)=>changeHandler(e.target.name, e.target.value)}
                            optionsMenuItemArr={elStatusList}
                            value={vmlData?.vmlStatus}
                            disabled={isViewMode}
                        />
                        <C1InputField
                            name="vmlCost"
                            label="Charge" 
                            onChange={(e)=>changeHandler(e.target.name, e.target.value)}
                            value={vmlData?.vmlCost}
                            disabled={isViewMode}
                        />
                    </C1CategoryBlock>
                </Grid>    
            </Grid>   
        </C1PopUp> 
        </>
    )
};