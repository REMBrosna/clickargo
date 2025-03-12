import React from "react";
import {
  Card,
  TextField,
  MenuItem,
  IconButton,
  Icon
} from "@material-ui/core";
import StatCard3 from "./shared/StatCard3";


const Analytics2 = () => {
  return (
    <div className="analytics m-sm-30">
      <div className="flex justify-between items-center items-center mb-6">
        <h3 className="m-0">Overview</h3>
        <TextField defaultValue="1" variant="outlined" size="small" select>
          <MenuItem value="1">This Month</MenuItem>
          <MenuItem value="2">Last Month</MenuItem>
          <MenuItem value="3">Six Month</MenuItem>
          <MenuItem value="4">Last Year</MenuItem>
        </TextField>
      </div>

      <StatCard3 />

      <Card className="mt-5 mb-6" elevation={3}>
        <div className=" px-4 py-3 mb-6 flex justify-between items-center bg-light-gray">
          <span className="font-medium text-muted">STATISTICS</span>
          <IconButton size="small">
            <Icon>more_horiz</Icon>
          </IconButton>
        </div>

      </Card>


    </div>
  );
};

export default Analytics2;
