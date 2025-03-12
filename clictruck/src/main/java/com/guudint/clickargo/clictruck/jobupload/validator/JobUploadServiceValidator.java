package com.guudint.clickargo.clictruck.jobupload.validator;

import com.beust.jcommander.ParameterException;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.jobupload.dto.JobRecord;
import com.guudint.clickargo.clictruck.jobupload.model.JobRecordTempate;
import com.guudint.clickargo.clictruck.jobupload.service.JobUploadService;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.util.ExcelPOIUtil;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.ccm.dto.CoreAccn;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.guudint.clickargo.clictruck.dto.JobLoading.isValidLoading;
import static com.guudint.clickargo.clictruck.dto.JobSubType.isValidJobSubType;

@Component
public class JobUploadServiceValidator {
    private static final Logger log = Logger.getLogger(JobUploadServiceValidator.class);
    private static final String INIT_SHIPMENT_REF = "init_shipment_ref";
    private static final String INIT_FROM_LOCATION = "init_from_location";
    private static final String TRIP_ERROR_MSG = "Value '%s' should match the corresponding column in row %d due to having the same %s!";
    private static final String REQUIRED = "Column %s is required!";
    private static final String GREATER_VALUE = "Column %s value must be greater than 0!";
    private static final String NOT_EXIST = "Column %s value '%s' does not exist!";
    private static final String CONTRACT_TO = "Column %s value '%s' TO is not %s!";
    private static final String CONTRACT_FF = "Column %s value '%s' CO/FF is not %s!";
    private static final String INVALID_DATE_TIME = "Column %s value is invalid date time format(dd/MM/yyyy HH:mm:ss eg. 30/12/2025 12:00:00)!";
    private static final String INVALID_DATE = "Column %s value is invalid date time format(dd/MM/yyyy eg. 30/12/2025)!";
    private static final String TRIP_LOC_ERROR_MSG = "Value '%s' should not match the corresponding column in row %d due to having the same %s!";

    @Autowired
    private JobUploadService jobUploadService;
    public void validationJobSubType(String jobSubType){
        if (!isValidJobSubType(jobSubType)){
            throw new ParameterException(String.format("Invalid job subType provided: %s.", jobSubType));
        }
    }
    public void validationJobLoading(String jobLoading){
        if (!isValidLoading(jobLoading)){
            throw new ParameterException(String.format("Invalid job loading provided: %s.", jobLoading));
        }
    }

    public void validateDeFaultExcel(Cell cell, CtConstant.JobRecordFieldEnum field, LinkedHashMap<String, String> error, Row rowNum,
                                     JobRecordTempate.JobRecordTempateItem jobRecordTempateItem, CoreAccn currentUserLogin, List<JobRecord> jobRecordList,
                                     LinkedHashMap<String, String> initValue, Row header, int visibleIndex) throws Exception {

        String rowNumAndField = rowNum.getRowNum() + 1 + ":" + visibleIndex + ":"+ jobRecordTempateItem.getLabel();

        switch (field) {
            case CONTRACT_ID:
                String contractName = ExcelPOIUtil.getCellValueAsString(cell);
                if (StringUtils.isBlank(contractName)) {
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                } else {
                    TCkCtContract existingContract = jobUploadService.getExistingContract(contractName);
                    if (Objects.isNull(existingContract)) {
                        error.put(rowNumAndField, String.format(NOT_EXIST, jobRecordTempateItem.getLabel(), contractName));
                    } else if (AccountTypes.ACC_TYPE_TO.name().equalsIgnoreCase(currentUserLogin.getTMstAccnType().getAtypId())) {
                        if (!currentUserLogin.getAccnId().equalsIgnoreCase(existingContract.getTCoreAccnByConTo().getAccnId())) {
                            error.put(rowNumAndField, String.format(CONTRACT_TO, jobRecordTempateItem.getLabel(), contractName, currentUserLogin.getAccnId()));
                        }
                    } else if (!currentUserLogin.getAccnId().equalsIgnoreCase(existingContract.getTCoreAccnByConCoFf().getAccnId())) {
                        error.put(rowNumAndField, String.format(CONTRACT_FF, jobRecordTempateItem.getLabel(), contractName, currentUserLogin.getAccnId()));
                    } else {
                        if (jobRecordList.size() > 0) {
                            List<JobRecord> existingTrips = jobRecordList.stream()
                                    .filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
                                    .collect(Collectors.toList());

                            if (!existingTrips.isEmpty()) {
                                JobRecord firstTrip = existingTrips.get(0);
                                if (!jobUploadService.isMatchingContract(firstTrip, contractName)){
                                    error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, contractName, firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
                                }
                            }
                        }
                    }
                }
                break;
            case SHIPMENT_REF:
                String invNo = ExcelPOIUtil.getCellValueAsString(cell);
                if (StringUtils.isBlank(invNo)){
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }
                break;
            case LOADING:
                String loading = ExcelPOIUtil.getCellValueAsString(cell);
                if (StringUtils.isBlank(loading)){
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }else {
                    if (jobRecordList.size() > 0) {
                        List<JobRecord> existingTrips = jobRecordList.stream()
                                .filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
                                .collect(Collectors.toList());

                        if (!existingTrips.isEmpty()) {
                            JobRecord firstTrip = existingTrips.get(0);
                            String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
                            if (!jobUploadService.isMatchingLoading(firstTrip, loading)){
                                error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, loading, firstTrip.getRowId() + 1, invRef));
                            }
                        }
                    }
                }
                break;
            case BOOKING_DATE:
            case PLAN_DATE:
                if (cell == null) {
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                } else {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        Date dateValue = cell.getDateCellValue();
                        if (dateValue == null) {
                            error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                        }
                    } else if (StringUtils.isBlank(cell.toString())) {
                        error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                    } else {
                        if (jobRecordList.size() > 0) {
                            List<JobRecord> existingTrips = jobRecordList.stream()
                                    .filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
                                    .collect(Collectors.toList());

                            if (!existingTrips.isEmpty()) {
                                JobRecord firstTrip = existingTrips.get(0);
                                String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
                                if (!jobUploadService.isMatchingJobDate(firstTrip, cell.toString())){
                                    error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, cell.toString(), firstTrip.getRowId() + 1, invRef));
                                }
                            }
                        }
                    }
                }
                break;
            case CARGO_TRUCK_TYPE:
                String truckType = ExcelPOIUtil.getCellValueAsString(cell);
                if (StringUtils.isBlank(truckType)) {
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }else {
                    TCkCtMstVehType ckCtMstVehType = jobUploadService.getVehType(truckType);
                    if(ckCtMstVehType == null){
                        error.put(rowNumAndField, String.format(NOT_EXIST, jobRecordTempateItem.getLabel(), truckType));
                    }
                    if (jobRecordList.size() > 0) {
                        List<JobRecord> existingTrips = jobRecordList.stream()
                                .filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
                                .collect(Collectors.toList());

                        if (!existingTrips.isEmpty()) {
                            JobRecord firstTrip = existingTrips.get(0);
                            String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
                            if (!jobUploadService.isMatchingTruckType(firstTrip, truckType)){
                                error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, truckType, firstTrip.getRowId() + 1,invRef));
                            }
                        }
                    }
                }
                break;
            case FROM_LOCATION_DETAILS:
                String fromLoc = ExcelPOIUtil.getCellValueAsString(cell);
                if (StringUtils.isBlank(fromLoc)) {
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }else {
                    if (jobRecordList.size() > 0) {
                        List<JobRecord> existingTrips = jobRecordList.stream()
                                .filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF)))).trim()))
                                .collect(Collectors.toList());

                        if (!existingTrips.isEmpty()) {
                            JobRecord firstTrip = existingTrips.get(0);
                            if (!jobUploadService.isMatchingStartLocation(firstTrip, fromLoc)){
                                error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, fromLoc, firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
                            }
                        }
                    }
                }
                break;
            case FROM_LOCATION_DATE_TIME:
                if (cell == null ||
                        (cell.getCellType() == CellType.NUMERIC && cell.getDateCellValue() == null)) {
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }else if (StringUtils.isBlank(cell.toString())) {
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }
                if (jobRecordList.size() > 0) {
                    List<JobRecord> existingTrips = jobRecordList.stream()
                            .filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
                            .collect(Collectors.toList());

                    if (!existingTrips.isEmpty()) {
                        JobRecord firstTrip = existingTrips.get(0);
                        if (jobUploadService.isMatchingJobFromDateTime(firstTrip, cell.toString())){
                            error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, cell.toString(), firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
                        }
                    }
                }
                break;

            case ADDITIONAL_INFO:
                jobUploadService.processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "DELIVERY_NOTE");
                break;

            case TO_LOCATION_REMARKS:
                jobUploadService.processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "TO_LOCATION_REMARKS");
                break;

            case REMARK:
                jobUploadService.processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "REMARK");
                break;

            case CONTACT_NUMBER:
                jobUploadService.processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "CONTACT_NUMBER");
                break;

            case PHONE_NUMBER_NOTIFICATION:
                String toMobileNotif = ExcelPOIUtil.getCellValueAsString(cell);
                if (jobRecordList.size() > 0) {
                    List<JobRecord> existingTrips = jobRecordList.stream()
                            .filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF)))).trim()))
                            .collect(Collectors.toList());

                    if (!existingTrips.isEmpty()) {
                        JobRecord firstTrip = existingTrips.get(0);
                        if (!jobUploadService.isMatchingEndLocationMobilePhone(firstTrip, toMobileNotif)){
                            error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, toMobileNotif, firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
                        }
                    }
                }
                break;
            case TO_LOCATION_MOBILE_NUMBER:
                String toMobile = ExcelPOIUtil.getCellValueAsString(cell);
                if (StringUtils.isBlank(toMobile)){
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }else {
                    jobUploadService.processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "TO_LOCATION_MOBILE_NUMBER");
                }
                break;
            case TO_LOCATION_DETAILS:
                String toLoc = ExcelPOIUtil.getCellValueAsString(cell);
                if (StringUtils.isBlank(toLoc)){
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }else {
                    if (jobRecordList.size() > 0) {
                        List<JobRecord> existingTrips = jobRecordList.stream()
                                .filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF)))).trim()))
                                .collect(Collectors.toList());

                        if (!existingTrips.isEmpty()) {
                            JobRecord firstTrip = existingTrips.get(0);
                            if (jobUploadService.isMatchingEndLocation(firstTrip, String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_FROM_LOCATION)))))){
                                error.put(rowNumAndField, String.format(TRIP_LOC_ERROR_MSG, toLoc, firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
                            }
                        }
                    }
                }
                break;
            case DATE_OF_DELIVERY:
                if (cell == null ||
                        (cell.getCellType() == CellType.NUMERIC && cell.getDateCellValue() == null)) {
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }else if (StringUtils.isBlank(cell.toString())) {
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }else{
                    jobUploadService.processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "DATE_OF_DELIVERY");
                }
                break;
            case TO_LOCATION_DATE_TIME:
                if (cell == null ||
                        (cell.getCellType() == CellType.NUMERIC && cell.getDateCellValue() == null)) {
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }else if (StringUtils.isBlank(cell.toString())) {
                    error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
                }else{
                    jobUploadService.processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "TO_LOCATION_DATE_TIME");
                }
                break;
            default:
                break;
        }
    }
}
