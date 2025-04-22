import React from "react";
import MuiAlert from '@material-ui/lab/Alert';
const C1Alert = (props) => {
    return <MuiAlert elevation={6} variant="filled" {...props}
         style={
             {
                 borderRadius: 5,
                 width:450,
                 height:80,
                 justifyContent: 'center',
                 textAlign: 'center',
                 flex:1,
                 textAlignVertical: 'center',
                 backgroundColor: "#66c66d",
                 padding: "20px",
                 color:"#110202",
                 fontSize: "18px",
                 boxShadow: '0px 60px 40px -10px #3B3B98',
                 duration: 400, delay: 0,
             }
         }
    />;
}

export default C1Alert;