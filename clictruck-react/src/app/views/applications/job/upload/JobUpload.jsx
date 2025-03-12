import React, {
  useEffect,
  useRef,
  useState,
  useImperativeHandle,
  forwardRef,
} from "react";
import { Grid, Button } from "@material-ui/core";

import { useTranslation } from "react-i18next";
import useHttp from "app/c1hooks/http";

import { Uint8ArrayToString } from "app/c1utils/utility";

import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import AddBoxIcon from "@material-ui/icons/AddBox";
import ErrorTable from "./UploadErrorTable";

let initCoreAttach = { attName: "Select Excel File" };

export const formatUploadRst = (uploadResult) => {

  let rst = (
    <Grid
      container
      alignItems="flex-start"
      spacing={3}
      style={{ textAlign: "center" }}
    >
      {uploadResult && uploadResult?.upFileName && (
        <Grid item md={6} xs={6} container justifyContent="flex-end">
          File Name
        </Grid>
      )}
      {uploadResult && uploadResult?.upFileName && (
        <Grid item md={6} xs={6} container justifyContent="flex-start">
          {uploadResult?.upFileName}
        </Grid>
      )}

      {uploadResult && uploadResult?.upTotalLines != undefined && (
        <Grid item md={6} xs={6} container justifyContent="flex-end">
          Total lines:
        </Grid>
      )}
      {uploadResult && uploadResult?.upTotalLines != undefined && (
        <Grid item md={6} xs={6} container justifyContent="flex-start">
          {uploadResult?.upTotalLines}
        </Grid>
      )}

      {uploadResult && uploadResult?.upSuccessJobIds && (
        <Grid item md={6} xs={6} container justifyContent="flex-end">
          Success job:
        </Grid>
      )}
      {uploadResult && uploadResult?.upSuccessJobIds && (
        <Grid
          item
          md={6}
          xs={6}
          container
          justifyContent="flex-start"
          style={{ display: "block", textAlign: "left" }}
        >
          <React.Fragment>
            {Object.keys(JSON.parse(uploadResult.upSuccessJobIds)).map(
              (rowId) => (
                  <div key={rowId} style={{color: "green"}}>
                    Row: {parseInt(rowId)}; &nbsp;&nbsp; job id:{" "}
                    {JSON.parse(uploadResult.upSuccessJobIds)[rowId]}
                    <br/>
                  </div>
              )
            )}
          </React.Fragment>
        </Grid>
      )}

      {uploadResult && uploadResult?.upFailLines && (
        <Grid item xs={12} container justifyContent="flex-start"
          style={{ display: "block", textAlign: "left" }}
        >
          <ErrorTable jsonError={JSON.parse(uploadResult.upFailLines)}/>
        </Grid>
      )}

      {uploadResult && uploadResult?.upRemark && (
        <Grid item md={6} xs={6} container justifyContent="flex-end">
          Remark:
        </Grid>
      )}
      {uploadResult && uploadResult?.upRemark && (
        <Grid item md={6} xs={6} container justifyContent="flex-start" style={{ color: "red" }}>
          {uploadResult?.upRemark}
        </Grid>
      )}
    </Grid>
  );
  return rst;
};
// @Deprecated
/*
export const formatUploadRstStr = (uploadResult) => {
  let rst = ""; //uploadResult
  if (uploadResult && uploadResult?.upFileName) {
    rst = rst + "File name: " + uploadResult?.upFileName + "<br/>";
  }
  if (uploadResult && uploadResult?.upTotalLines) {
    rst = rst + "Total lines: " + uploadResult?.upTotalLines + "<br/>";
  }
  if (uploadResult && uploadResult?.upSuccessJobIds) {
    let upSuccessJobIds = JSON.parse(uploadResult?.upSuccessJobIds);
    if (upSuccessJobIds && Object.keys(upSuccessJobIds).length > 0) {
      rst = rst + "Success job: <br/>";
      for (const rowId in upSuccessJobIds) {
        if (upSuccessJobIds.hasOwnProperty(rowId)) {
          rst =
            rst +
            "Row: " +
            rowId +
            "  job id: " +
            upSuccessJobIds[rowId] +
            " <br/>";
        }
      }
    }
  }
  if (uploadResult && uploadResult?.upFailLines) {
    let upFailLines = JSON.parse(uploadResult?.upFailLines);
    if (upFailLines && Object.keys(upFailLines).length > 0) {
      rst = rst + "Fail job: <br/>";
      for (const rowId in upFailLines) {
        if (upFailLines.hasOwnProperty(rowId)) {
          rst =
            rst +
            "Row: " +
            rowId +
            "  Reason: " +
            upFailLines[rowId] +
            " <br/>";
        }
      }
    }
  }
  if (uploadResult && uploadResult?.upRemark) {
    rst = rst + "Remark: <br/>";
    rst = rst + uploadResult?.upRemark + " <br/>";
  }
  return rst;
};
*/

const JobUpload = forwardRef((props, ref) => {
  const [loading, setLoading] = useState(false);
  const { t } = useTranslation(["job"]);

  const { sendRequest, res, error, validation, urlId } = useHttp();
  const [isRefresh, setRefresh] = useState(false);
  const [coreAttach, setCoreAttach] = useState(initCoreAttach);
  const [uploadRst, setUploadRst] = useState({});
  //const [isDisabled, setIsDisabled] = useState(true);

  useImperativeHandle(ref, () => ({
    uploadRst: uploadRst,
  }));

  const uploadExcelFile = (e) => {
    e.preventDefault();
    var file = e.target.files[0];
    if (!file) return;

    const fileReader = new FileReader();
    fileReader.readAsArrayBuffer(file);
    fileReader.onload = (e) => {
      const uint8Array = new Uint8Array(e.target.result);
      var imgStr = Uint8ArrayToString(uint8Array);
      var base64Sign = btoa(imgStr);
      let tmpCoreAttach = {
        ...coreAttach,
        attName: file.name,
        attData: base64Sign,
      };
      setCoreAttach(tmpCoreAttach);
    };
    setUploadRst({});
  };

  const handleBtnAddFile = () => {
    setRefresh(false);
    sendRequest(
      `/api/v1/clickargo/clictruck/jobUpload`,
      "uploadFile",
      "POST",
      coreAttach
    );
    setLoading(true);
  };


  useEffect(() => {
    if (!error && res && !validation) {
      console.log("response", res?.data);
      if (urlId === "uploadFile") {
        console.log("response", res?.data);
        setUploadRst(res?.data);
        setCoreAttach(initCoreAttach);
        setLoading(false);
      }

    }
    if (error) {
      //goes back to the screen
      //setDlOpen(false);
    }
    if (validation) {
      //setValidationErrors({ ...validation });
      //setDlOpen(false);
    }
    // eslint-disable-next-line
  }, [urlId, error, res]);

  return (
    <div>
      <Grid container alignItems="flex-start" spacing={3}>
        <Grid item md={12} xs={12} style={{ textAlign: "center" }}>
          <label htmlFor="upload-multiple-file">
            <Button
              type="button"
              color="primary"
              component="span"
              variant="contained"
              size="large"
            >
              <CloudUploadIcon viewBox="1 -1 30 30"></CloudUploadIcon>
              {coreAttach?.attName}
            </Button>
          </label>
          <input
            className="hidden"
            onChange={(e) => uploadExcelFile(e)}
            id="upload-multiple-file"
            type="file"
            single="true"
            // accept={uploadFileType}
          />
          <Button
            type="button"
            ml={3}
            style={{ marginLeft: "16px" }}
            disabled={!coreAttach?.attData || loading }
            color="primary"
            variant="contained"
            size="large"
            onClick={handleBtnAddFile}
          >
            <AddBoxIcon viewBox="1 -1 30 30"></AddBoxIcon>Upload
          </Button>
          <br />
          {formatUploadRst(uploadRst)}
        </Grid>
        <Grid item md={6} xs={12}></Grid>
      </Grid>
    </div>
  );
});

export default JobUpload;
