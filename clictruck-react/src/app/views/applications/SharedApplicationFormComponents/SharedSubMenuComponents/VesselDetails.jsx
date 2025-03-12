import React, { useState, useEffect } from "react";
import {
  Grid,
  TextField,
  Button,
  Select,
  Paper,
  Divider,
} from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";


import { titleTab, useStyles } from "app/c1utils/styles";
import SessionCache from 'app/services/sessionCacheService.js';
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import C1InputField from "app/c1component/C1InputField";
import C1GridContainer from "app/c1component/C1TabContainer";
const StyledTableCell = withStyles((theme) => ({
  head: {
    backgroundColor: '#3C77D0',
    color: theme.palette.common.white,
    fontSize: 15,
  },
  body: {
    fontSize: 10,
  },
}))(TableCell);

const StyledTableRow = withStyles((theme) => ({
  root: {
    '&:nth-of-type(odd)': {
      backgroundColor: theme.palette.action.hover,
    },
  },
}))(TableRow);
const useTableStyle = makeStyles({
  table: {
    minWidth: 100,
  },
  column: {
    width: 20,
  },
  column1: {
    width: 10,
  },
});

function valuetext(value) {
  return `${value}`;
}

const marks = [
  {
    value: 1,
    label: '1',
  },
  {
    value: 2,
    label: '2',
  },
  {
    value: 3,
    label: '3',
  },

];



const useButtonStyles = makeStyles((theme) => ({
  root: {
    '& > *': {
      margin: theme.spacing(1),
    },
  },
}));
const VoyageDetailsSubTab = ({
  handleSubmit,
  data,
  inputData,
  handleInputChange,
  handleValidate,
  viewType,
  isSubmitting,
  props }) => {

  let isDisabled = true;
  if (viewType === 'new')
    isDisabled = false;
  else if (viewType === 'edit')
    isDisabled = false;
  else if (viewType === 'view')
    isDisabled = true;

  if (isSubmitting)
    isDisabled = true;

  const classes = titleTab();
  const tableCls = useTableStyle();
  const fieldClass = useStyles();
  const [tabIndex, setTabIndex] = useState(0);
  const handleTabChange = (e, value) => {
    setTabIndex(value);
  };
  const [voyageInput, setVoyageInputData] = useState(inputData);

  var countryListSession = SessionCache.getCountryList();
  const handleVoyageInputChange = (e) => {
    setVoyageInputData({ ...voyageInput, [e.currentTarget.name]: e.currentTarget.value });
    console.log(e.currentTarget.name, e.currentTarget.value, voyageInput);
  };
  const handleVoyageSelectChange = (e) => {
    setVoyageInputData({ ...voyageInput, [e.target.name]: e.target.value });
    console.log(e.target.name, e.target.value, voyageInput);
  };

  useEffect(() => {
    setVoyageInputData(inputData);

  }, [inputData]);


  return (
    <div>
      <C1GridContainer>
        <Grid container item xs={12} className={classes.root}>
          Ship Content
        </Grid>
      </C1GridContainer>
      <Divider className="mb-6" />
      <C1GridContainer>
        <Grid container item xs={4}>
          <C1InputField label="Imported Goods"
            name="importedGoods"
            type="input"
            required
            disabled={isDisabled}
            onChange={handleVoyageInputChange}
            value={voyageInput.importedGoods}
          />

        </Grid>
        <Grid container item xs={2}>
          <C1InputField label="Quantity of Goods"
            name="qtyGoods"
            type="input"
            required="true"
            disabled={isDisabled}
            onChange={handleVoyageInputChange}
            value={voyageInput.qtyGoods}
          />

        </Grid>
        <Grid container item xs={2}>
          <TextField
            fullWidth
            variant="outlined"
            label="Select UOM"
            name="uom"
            required
            size="medium"
            value={voyageInput.uom}
            disabled={isDisabled}
            onChange={handleVoyageInputChange}
            InputLabelProps={{
              shrink: true,
            }}
            margin="normal"
            select

          >

            <MenuItem value="CM" key="CM" >Cubic Meter</MenuItem>
            <MenuItem value="CNP" key="CNP">Hunderds Pack</MenuItem>
            <MenuItem value="KGM" key="KGM">Kilogram</MenuItem>
            <MenuItem value="LT" key="LT">Litre</MenuItem>
            <MenuItem value="MTR" key="MTR">Meters</MenuItem>
            <MenuItem value="NPR" key="NPR">Number of Pairs</MenuItem>

          </TextField>
        </Grid>

        <Grid container item xs={4}>
          <C1InputField label="No Of Passengers"
            name="noPassengers"
            type="input"
            required="true"
            disabled={isDisabled}
            onChange={handleVoyageInputChange}
            value={voyageInput.noPassengers}
          />

        </Grid>
      </C1GridContainer>
      <C1GridContainer>
        <Grid container item xs={12} className={classes.root}>
          Ship Security Level of last 10 ports
        </Grid>
      </C1GridContainer>
      <Divider className="mb-6" />
      <C1GridContainer>
        <Grid item xs={12} spacing={3}>
          <TableContainer component={Paper}>
            <Table className={tableCls.table} aria-label="simple table">
              <TableHead>
                <TableRow>
                  <StyledTableCell align="center">Port No</StyledTableCell>
                  <StyledTableCell align="center">Level</StyledTableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                <TableRow key={1}>
                  <TableCell align="center" className={tableCls.column1}> port 1</TableCell>
                  <TableCell align="center" className={tableCls.column}>
                    <TextField
                      fullWidth
                      required
                      size="medium"
                      margin="normal"
                      disabled={isDisabled}
                      value={voyageInput.port1 || 1}
                      name="port1"
                      label="Level"
                      variant="outlined"
                      onChange={handleVoyageSelectChange}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      select
                      {...props}
                    >
                      <MenuItem value='' key=''>  </MenuItem>

                      <MenuItem value="1" key="1"> Level 1 </MenuItem>
                      <MenuItem value="2" key="2"> Level 2 </MenuItem>
                      <MenuItem value="3" key="3"> Level 3 </MenuItem>

                    </TextField></TableCell>
                </TableRow>
                <TableRow key={1}>
                  <TableCell align="center" className={tableCls.column1}> port 2</TableCell>
                  <TableCell align="center" className={tableCls.column}>
                    <TextField
                      fullWidth
                      required
                      size="medium"
                      margin="normal"
                      disabled={isDisabled}
                      value={voyageInput.port2 || 1}
                      name="port2"
                      label="Level"
                      variant="outlined"
                      onChange={handleVoyageSelectChange}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      select
                      {...props}
                    >
                      <MenuItem value='' key=''>  </MenuItem>

                      <MenuItem value="1" key="1"> Level 1 </MenuItem>
                      <MenuItem value="2" key="2"> Level 2 </MenuItem>
                      <MenuItem value="3" key="3"> Level 3 </MenuItem>

                    </TextField></TableCell>
                </TableRow>
                <TableRow key={1}>
                  <TableCell align="center" className={tableCls.column1}> port 3</TableCell>
                  <TableCell align="center" className={tableCls.column}>
                    <TextField
                      fullWidth
                      required
                      size="medium"
                      margin="normal"
                      disabled={isDisabled}
                      value={voyageInput.port3 || 1}
                      name="port3"
                      label="Level"
                      variant="outlined"
                      onChange={handleVoyageSelectChange}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      select
                      {...props}
                    >
                      <MenuItem value='' key=''>  </MenuItem>

                      <MenuItem value="1" key="1"> Level 1 </MenuItem>
                      <MenuItem value="2" key="2"> Level 2 </MenuItem>
                      <MenuItem value="3" key="3"> Level 3 </MenuItem>

                    </TextField></TableCell>
                </TableRow>
                <TableRow key={1}>
                  <TableCell align="center" className={tableCls.column1}> port 4</TableCell>
                  <TableCell align="center" className={tableCls.column}>
                    <TextField
                      fullWidth
                      required
                      size="medium"
                      margin="normal"
                      disabled={isDisabled}
                      value={voyageInput.port4 || 2}
                      name="port4"
                      label="Level"
                      variant="outlined"
                      onChange={handleVoyageSelectChange}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      select
                      {...props}
                    >
                      <MenuItem value='' key=''>  </MenuItem>

                      <MenuItem value="1" key="1"> Level 1 </MenuItem>
                      <MenuItem value="2" key="2"> Level 2 </MenuItem>
                      <MenuItem value="3" key="3"> Level 3 </MenuItem>

                    </TextField></TableCell>
                </TableRow>
                <TableRow key={1}>
                  <TableCell align="center" className={tableCls.column1}> port 5</TableCell>
                  <TableCell align="center" className={tableCls.column}>
                    <TextField
                      fullWidth
                      required
                      size="medium"
                      margin="normal"
                      disabled={isDisabled}
                      value={voyageInput.port5 || 2}
                      name="port5"
                      label="Level"
                      variant="outlined"
                      onChange={handleVoyageSelectChange}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      select
                      {...props}
                    >
                      <MenuItem value='' key=''>  </MenuItem>

                      <MenuItem value="1" key="1"> Level 1 </MenuItem>
                      <MenuItem value="2" key="2"> Level 2 </MenuItem>
                      <MenuItem value="3" key="3"> Level 3 </MenuItem>

                    </TextField></TableCell>
                </TableRow>
                <TableRow key={1}>
                  <TableCell align="center" className={tableCls.column1}> port 6</TableCell>
                  <TableCell align="center" className={tableCls.column}>
                    <TextField
                      fullWidth
                      required
                      size="medium"
                      margin="normal"
                      disabled={isDisabled}
                      value={voyageInput.port6 || 2}
                      name="port6"
                      label="Level"
                      variant="outlined"
                      onChange={handleVoyageSelectChange}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      select
                      {...props}
                    >
                      <MenuItem value='' key=''>  </MenuItem>

                      <MenuItem value="1" key="1"> Level 1 </MenuItem>
                      <MenuItem value="2" key="2"> Level 2 </MenuItem>
                      <MenuItem value="3" key="3"> Level 3 </MenuItem>

                    </TextField></TableCell>
                </TableRow>
                <TableRow key={1}>
                  <TableCell align="center" className={tableCls.column1}> port 7</TableCell>
                  <TableCell align="center" className={tableCls.column}>
                    <TextField
                      fullWidth
                      required
                      size="medium"
                      margin="normal"
                      disabled={isDisabled}
                      value={voyageInput.port7 || 3}
                      name="port7"
                      label="Level"
                      variant="outlined"
                      onChange={handleVoyageSelectChange}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      select
                      {...props}
                    >
                      <MenuItem value='' key=''>  </MenuItem>

                      <MenuItem value="1" key="1"> Level 1 </MenuItem>
                      <MenuItem value="2" key="2"> Level 2 </MenuItem>
                      <MenuItem value="3" key="3"> Level 3 </MenuItem>

                    </TextField></TableCell>
                </TableRow>
                <TableRow key={1}>
                  <TableCell align="center" className={tableCls.column1}> port 8</TableCell>
                  <TableCell align="center" className={tableCls.column}>
                    <TextField
                      fullWidth
                      required
                      size="medium"
                      margin="normal"
                      disabled={isDisabled}
                      value={voyageInput.port8 || 3}
                      name="port8"
                      label="Level"
                      variant="outlined"
                      onChange={handleVoyageSelectChange}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      select
                      {...props}
                    >
                      <MenuItem value='' key=''>  </MenuItem>

                      <MenuItem value="1" key="1"> Level 1 </MenuItem>
                      <MenuItem value="2" key="2"> Level 2 </MenuItem>
                      <MenuItem value="3" key="3"> Level 3 </MenuItem>

                    </TextField></TableCell>
                </TableRow>
                <TableRow key={1}>
                  <TableCell align="center" className={tableCls.column1}> port 9</TableCell>
                  <TableCell align="center" className={tableCls.column}>
                    <TextField
                      fullWidth
                      required
                      size="medium"
                      margin="normal"
                      disabled={isDisabled}
                      value={voyageInput.port9 || 3}
                      name="port9"
                      label="Level"
                      variant="outlined"
                      onChange={handleVoyageSelectChange}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      select
                      {...props}
                    >
                      <MenuItem value='' key=''>  </MenuItem>

                      <MenuItem value="1" key="1"> Level 1 </MenuItem>
                      <MenuItem value="2" key="2"> Level 2 </MenuItem>
                      <MenuItem value="3" key="3"> Level 3 </MenuItem>

                    </TextField></TableCell>
                </TableRow>
                <TableRow key={1}>
                  <TableCell align="center" className={tableCls.column1}> port 10</TableCell>
                  <TableCell align="center" className={tableCls.column}>
                    <TextField
                      fullWidth
                      required
                      size="medium"
                      margin="normal"
                      disabled={isDisabled}
                      value={voyageInput.port10 || 3}
                      name="port10"
                      label="Level"
                      variant="outlined"
                      onChange={handleVoyageSelectChange}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      select
                      {...props}
                    >
                      <MenuItem value='' key=''>  </MenuItem>

                      <MenuItem value="1" key="1"> Level 1 </MenuItem>
                      <MenuItem value="2" key="2"> Level 2 </MenuItem>
                      <MenuItem value="3" key="3"> Level 3 </MenuItem>

                    </TextField></TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </TableContainer>
        </Grid>
      </C1GridContainer>
      <br />

    </div>

  );
};

export default VoyageDetailsSubTab;