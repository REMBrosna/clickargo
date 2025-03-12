import C1PopUp from "app/c1component/C1PopUp";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";

import React, { useEffect, useState } from "react";

import NearMeOutlinedIcon from "@material-ui/icons/NearMeOutlined";
import C1IconButton from "app/c1component/C1IconButton";
import JobAuthLetters from "../form/tabs/JobAuthLetters";

const DeliveredPopup = (props) => {

     const {
         errors,
         viewType,
         translate,
         openPopUp,
         inputData,
         setOpenPopUp,
         handleSubmit,
         handleInputChange,
         handleInputFileChange,
     } = props;


    return (
      <C1PopUp
          maxWidth={"lg"}
          title={translate("job:delivered.title")}
          openPopUp={openPopUp}
          setOpenPopUp={setOpenPopUp}
          actionsEl={
              <C1IconButton tooltip={translate("buttons:submit")} childPosition="right">
                  <NearMeOutlinedIcon
                      color="primary"
                      fontSize="large"
                      onClick={(e) => handleSubmit(e)}
                  />
              </C1IconButton>
          }
      >
        <JobAuthLetters
            viewType={viewType}
            showDocList={false}
            inputData={inputData}
        />
      </C1PopUp>
  );
};

export default withErrorHandler(DeliveredPopup);
