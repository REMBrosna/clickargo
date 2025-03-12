import { CircularProgress, Grid, Typography } from "@material-ui/core";
import React from "react";

import C1FileUpload from "app/c1component/C1FileUpload";

import { getValue } from "app/c1utils/utility";
import C1PopUp from "app/c1component/C1PopUp";
import { NearMeOutlined } from "@material-ui/icons";
import C1IconButton from "app/c1component/C1IconButton";

const UploadTemplatePopup = ({
  popupObj,
  handleFileInputChange,
  uploadEventHandler,
  handlePopupEventHandler,
}) => {
  return (
    <C1PopUp
      title={"Upload Template"}
      openPopUp={popupObj?.open}
      setOpenPopUp={handlePopupEventHandler}
      actionsEl={
        <C1IconButton
          tooltip={"Submit"}
          childPosition="right"
          disabled={!popupObj?.fileName}
        >
          {popupObj?.loading ? (
            <CircularProgress color="inherit" size={30} />
          ) : (
            <NearMeOutlined
              color={popupObj?.fileName ? "primary" : "disabled"}
              fontSize="large"
              onClick={(e) => uploadEventHandler(e)}
            />
          )}
        </C1IconButton>
      }
    >
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <C1FileUpload
            inputLabel={""}
            inputProps={{
              placeholder: "No file chosen",
            }}
            value={getValue(popupObj?.fileName)}
            fileChangeHandler={handleFileInputChange}
            label={"Browse"}
            required
            disabled={false}
            errors={popupObj.errors && popupObj.errors.msg ? true : false}
            helperText={
              popupObj.errors && popupObj.errors?.msg
                ? popupObj.errors?.msg
                : null
            }
          />
        </Grid>
        {popupObj?.errors?.list && (
          <Grid
            container
            item
            xs={12}
            spacing={1}
            direction="column"
            justifyContent="flex-start"
            alignItems="flex-start"
          >
            <Typography variant="subtitle1" style={{ color: "red" }}>
              Errors by row:
            </Typography>
            {popupObj?.errors?.list?.map((el, idx) => {
              return (
                <span key={idx} style={{ color: "red" }}>
                  {el}
                </span>
              );
            })}
          </Grid>
        )}
      </Grid>
    </C1PopUp>
  );
};

export default UploadTemplatePopup;
