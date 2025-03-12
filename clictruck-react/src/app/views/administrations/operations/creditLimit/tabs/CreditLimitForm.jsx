import React from "react";
import {  Grid,  InputAdornment } from "@material-ui/core";

import useAuth from "app/hooks/useAuth";
import {  Roles} from "app/c1utils/const";

import C1TabContainer from 'app/c1component/C1TabContainer'
import C1CategoryBlock from "app/c1component/C1CategoryBlock";

import LocalAtmIcon from '@material-ui/icons/LocalAtm';
import SyncAltIcon from '@material-ui/icons/SyncAlt';
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1InputField from "app/c1component/C1InputField";
import C1DateField from "app/c1component/C1DateField";
import NumFormat from "app/clictruckcomponent/NumFormat";
import CreditLimitContext from "../CreditLimitUpdateContext";

const CreditLimitForm = ({viewType}) => {
  const { user } = useAuth();

  const { companyList, inputData, setInputData, serviceList, handleDetailCreditLimit, servicePicked, creditData, handleUpdateCreditLimit, validationErrors } = React.useContext(CreditLimitContext)

  const [disabledField, setDisabledField] = React.useState(true)
  const isFnHDSupport = user?.authorities?.some((item)=> item?.authority === Roles.FINANCE_APPROVER.code)

  React.useEffect(()=>{
    if(inputData?.tcoreAccn?.accnId && inputData?.tckMstServiceType?.svctId){
      if(viewType === "view"){
        setDisabledField(true)
      }else{
        setDisabledField(false)
      } 
    }
  },[inputData, viewType])

 console.log("creditData", creditData);

  return (
    <React.Fragment>
        <Grid item xs={12}>
          <C1TabContainer>
            <Grid item lg={6} md={6} xs={12}>
              <C1CategoryBlock icon={<LocalAtmIcon />} title={"Credit Limit Details"}>
                <C1SelectAutoCompleteField
                  disabled={viewType === "new"? false : true}
                  required
                  label="Company Name"
                  name="tcoreAccn.accnId"
                  value={inputData?.tcoreAccn?.accnId}
                  onChange={(e,name,value)=> handleDetailCreditLimit(e, name, value)}
                  optionsMenuItemArr={companyList?.map((item, i) => {                                      
                    return {
                        value: item?.accnId,
                        desc: item?.accnName,
                    }
                            
                })}
                />
                <C1SelectAutoCompleteField
                  disabled={inputData?.tcoreAccn?.accnId && viewType == "new" ? false : true}
                  required
                  label="Service"
                  name="tckMstServiceType.svctId"
                  value={inputData?.tckMstServiceType?.svctId}
                  onChange={(e,name,value)=> handleDetailCreditLimit(e, name, value)}
                  optionsMenuItemArr={serviceList?.map((item, i)=>{
                    return {
                      value: item,
                      desc: item
                    }
                  })}
                />
                <C1InputField
                  label={"Credit Limit"}
                  disabled={true}
                  value={creditData?.crAmt?.toLocaleString("id-ID")}
                />
                <C1InputField
                  disabled
                  label="Currency"
                  value={creditData?.tmstCurrency?.ccyCode}
                />
                <C1DateField
                  disabled
                  label="Start Date"
                  value={creditData?.crDtStart}
                />
                <C1DateField
                  disabled
                  label="End Date"
                  value={creditData?.crDtEnd}
                />
                <C1InputField
                  multiline={true}
                  rows={5}
                  disabled
                  label="Comments"
                  value={creditData?.crRemarks}
                />
                <C1InputField
                  disabled
                  label="Approved By"
                  value={creditData?.tcoreUsrApprove?.usrName}
                />
                <C1DateField
                  disabled
                  label="Approved Date"
                  value={creditData?.crDtApprove}
                />
              </C1CategoryBlock>
            </Grid>
            <Grid item lg={6} md={6} xs={12}>
              <C1CategoryBlock icon={<SyncAltIcon />} title={"Credit Limit Update Details"}>
                <C1InputField
                  disabled={disabledField}
                  label={"New Limit"}
                  name={"cruAmt"}
                  value={inputData?.cruAmt}
                  required
                  onChange={(e)=>handleUpdateCreditLimit({target:{name:e.target.name, value: parseInt(e.target.value)}})}
                  InputProps={{
                      style: { textAlign: 'right' },
                      inputComponent: NumFormat,
                      startAdornment:
                          <InputAdornment position="start" style={{ paddingRight: "8px" }}>
                              
                          </InputAdornment>
                  }}
                  error={validationErrors["cruAmt"] !== undefined}
                  helperText={validationErrors['cruAmt'] || ''}
                />
                <C1DateField
                  disabled={disabledField}
                  required
                  disablePast
                  name={"cruDtStart"}
                  onChange={(e, date)=> setInputData({...inputData, [e]: date?.getTime()})}
                  value={inputData?.cruDtStart}
                  label="Start Date"
                  error={validationErrors["cruDtStart"] !== undefined}
                  helperText={validationErrors['cruDtStart'] || ''}
                />
                <C1DateField
                  disabled={disabledField}
                  required
                  disablePast
                  minDate={inputData?.cruDtStart ? inputData?.cruDtStart : 0}
                  onChange={(e, date)=> setInputData({...inputData, [e]: date?.getTime()})}
                  name={"cruDtEnd"}
                  value={inputData?.cruDtEnd}
                  label="End Date"
                  error={validationErrors["cruDtEnd"] !== undefined}
                  helperText={validationErrors['cruDtEnd'] || ''}
                />
                <C1InputField
                  disabled={disabledField}
                  multiline={true}
                  rows={5}
                  required
                  name={"cruRemarks"}
                  onChange={(e)=>handleUpdateCreditLimit({target:{name:e.target.name, value: e.target.value}})}
                  value={inputData?.cruRemarks}
                  label="Comments"
                  error={validationErrors["cruRemarks"] !== undefined}
                  helperText={validationErrors['cruRemarks'] || ''}
                />
                <C1InputField
                  disabled={disabledField}
                  required
                  name={"cruRequester"}
                  label="Requester"
                  onChange={(e)=>handleUpdateCreditLimit({target:{name:e.target.name, value: e.target.value}})}
                  value={inputData?.cruRequester}
                  error={validationErrors["cruRequester"] !== undefined}
                  helperText={validationErrors['cruRequester'] || ''}
                />
                <C1DateField
                  disabled
                  label="Submitted Date"
                  name={"cruDtSubmitted"}
                  value={inputData?.cruDtSubmitted}
                />
                <C1InputField
                  multiline={true}
                  rows={5}
                  required
                  disabled={!isFnHDSupport || inputData?.tckMstCreditRequestState?.stId === "APP" || inputData?.tckMstCreditRequestState?.stId === "REJ"}
                  label="Approver Comments"
                  name={"cruApproverRemarks"}
                  onChange={(e)=>handleUpdateCreditLimit({target:{name:e.target.name, value: e.target.value}})}
                  value={inputData?.cruApproverRemarks}
                  error={validationErrors["cruApproverRemarks"] !== undefined}
                  helperText={validationErrors['cruApproverRemarks'] || ''}
                />
                <C1DateField
                  // required
                  disabled
                  label="Approve/Reject Date"
                  name={inputData?.cruDtApprove ? "cruDtApprove": "cruDtReject"}
                  value={inputData?.cruDtApprove ? inputData?.cruDtApprove : inputData?.cruDtReject}
                />
              </C1CategoryBlock>
            </Grid>
          </C1TabContainer>
        </Grid>
    </React.Fragment>
  )
}

export default CreditLimitForm