import { Grid } from "@material-ui/core";
import C1DateTimeField from "app/c1component/C1DateTimeField";
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectField from "app/c1component/C1SelectField";
import C1TextArea from "app/c1component/C1TextArea";
import React from "react";
import MenuItem from "@material-ui/core/MenuItem";

const CommentsDetailsPopUp = ({popUp, setPopUp, data}) => {
    
    // const [openPopUp, setOpenPopUp] = useState(false);
    // const togglePopUp = () => {setOpenPopUp(openPopUp? false : true)};

    // useEffect( () => {setOpenPopUp(popUp)}, [popUp]);
    const CommentType = [
        {value: "A", desc: "Approval"},
        {value: "V", desc: "Verification"},
        {value: "R", desc: "Rejection"}
    ]


    return(
        <>
        <C1PopUp
            title="Comment Details"
            openPopUp={popUp}
            setOpenPopUp={setPopUp}
        >
            <Grid container spacing={2}>
                <Grid container item xs={12} direction='column'>
                    <C1SelectField 
                        name = "rtrType"
                        label = "Comment Type"
                        value = {data[0]}
                        disabled = {true}
                        optionsMenuItemArr={ CommentType?.map((item, i) => {
                            return <MenuItem value={item.value} key={item.value}>{item.desc}</MenuItem>
                        })}
                    />
                    <C1TextArea 
                        name = "rtrComment"
                        label = "Comments"
                        value = {data[1]}
                        disabled = {true}
                    />
                    <C1DateTimeField 
                        name = "rtrDtCreate"
                        label = "Comment Date"
                        value = {data[2]}
                        disabled = {true}
                    />
                    <C1InputField 
                        name = "rtrUidCreate"
                        label = "Commented By"
                        value = {data[3]}
                        disabled = {true}
                    />
                </Grid>
            </Grid>
        </C1PopUp>
        </>
    )
}

export default CommentsDetailsPopUp