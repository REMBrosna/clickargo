package com.guudint.clickargo.clictruck.planexec.trip.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtToInvoiceDao;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstReimbursementType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstReimbursementType;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.constant.CkCtTripReimbursementConstant;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPaymentDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripReimbursementDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripReimbursement;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPayment;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripReimbursement;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.master.model.TMstCurrency;
import com.vcc.camelone.util.PrincipalUtilService;

public class CkCtTripReimbursementService
        extends AbstractClickCargoEntityService<TCkCtTripReimbursement, String, CkCtTripReimbursement> {

    @Autowired
    private CkCtTripReimbursementDao ckCtTripReimbursementDao;
    @Autowired
    private CkCtPaymentDao ckCtPaymentDao;
    @Autowired
    private CkCtToInvoiceDao ckCtToInvoiceDao;

    @Autowired
    @Qualifier("ckCtTripDao")
    private GenericDao<TCkCtTrip, String> ckCtTripDao;

    @Autowired
    @Qualifier("ckJobTruckDao")
    private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@Autowired
	protected PrincipalUtilService principalUtilService;

    private static Logger LOG = Logger.getLogger(CkCtTripReimbursementService.class);

    public CkCtTripReimbursementService() {
        super(CkCtTripReimbursementConstant.Table.NAME_DAO, CkCtTripReimbursementConstant.Prefix.AUDIT_TAG,
                CkCtTripReimbursementConstant.Table.NAME_ENTITY, CkCtTripReimbursementConstant.Table.NAME);
    }

    @Override
    public CkCtTripReimbursement newObj(Principal principal)
            throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("newObj");
        CkCtTripReimbursement ckCtTripReimbursement = new CkCtTripReimbursement();
        ckCtTripReimbursement.setTCkCtMstReimbursementType(new CkCtMstReimbursementType());
        ckCtTripReimbursement.setTCkCtTrip(new CkCtTrip());
        return ckCtTripReimbursement;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtTripReimbursement deleteById(String id, Principal principal)
            throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        LOG.info("deleteById");
        if (StringUtils.isBlank(id)) {
            throw new ParameterException("param id null or empty");
        }
        if (principal == null) {
            throw new ParameterException("param principal null or empty");
        }
        try {
            TCkCtTripReimbursement tCkCtTripReimbursement = dao.find(id);
            if (tCkCtTripReimbursement == null) {
                throw new EntityNotFoundException("id::" + id);
            }
            updateEntityStatus(tCkCtTripReimbursement, RecordStatus.INACTIVE.getCode());
            updateEntity(ACTION.MODIFY, tCkCtTripReimbursement, principal, new Date());
            CkCtTripReimbursement ckCtTripReimbursement = dtoFromEntity(tCkCtTripReimbursement);
            this.delete(ckCtTripReimbursement, principal);
            return ckCtTripReimbursement;
        } catch (Exception e) {
            LOG.error(e);
            throw new ProcessingException(e);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public List<CkCtTripReimbursement> filterBy(EntityFilterRequest filterRequest)
            throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("filterBy");
        if (filterRequest == null) {
            throw new ParameterException("param filterRequest null");
        }

        Principal principal = principalUtilService.getPrincipal();
        
        if (principal == null) {
            throw new ParameterException("param principal null or empty");
        }
        CkCtTripReimbursement ckCtTripReimbursement = whereDto(filterRequest);
        filterRequest.setTotalRecords(countByAnd(ckCtTripReimbursement));
        String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
        List<TCkCtTripReimbursement> tCkCtTripReimbursements = findEntitiesByAnd(ckCtTripReimbursement,
                "from TCkCtTripReimbursement o", orderByClause, filterRequest.getDisplayLength(),
                filterRequest.getDisplayStart());
        List<CkCtTripReimbursement> ckCtTripReimbursements = new ArrayList<>();
        for (TCkCtTripReimbursement tCkCtTripReimbursement : tCkCtTripReimbursements) {
            CkCtTripReimbursement dto = dtoFromEntity(tCkCtTripReimbursement);
            
            if (principal.getCoreAccn().getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF_CO.name())) {
            	// clear amount to 0 for ACC_TYPE_FF_CO
            	dto = this.clearAmountTo0(dto);
            }
            ckCtTripReimbursements.add(dto);
        }
        return ckCtTripReimbursements;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtTripReimbursement findById(String id)
            throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");
        if (StringUtils.isBlank(id)) {
            throw new ParameterException("param id null or empty");
        }
        try {

            Principal principal = principalUtilService.getPrincipal();
            
            TCkCtTripReimbursement tCkCtTripReimbursement = dao.find(id);
            if (tCkCtTripReimbursement == null) {
                throw new EntityNotFoundException("id::" + id);
            }
            initEnity(tCkCtTripReimbursement);
            CkCtTripReimbursement reim = dtoFromEntity(tCkCtTripReimbursement);

			// clear amount to 0 for FF-CO
			if(principal != null && principal.getCoreAccn() != null ) {
				if (principal.getCoreAccn().getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF_CO.name())) {
					
					reim = this.clearAmountTo0(reim);
				}
			}
            return reim;
        } catch (Exception e) {
            LOG.error(e);
            throw new ProcessingException(e);
        }
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected void initBusinessValidator() {

    }

    @Override
    protected CkCtTripReimbursement dtoFromEntity(TCkCtTripReimbursement tCkCtTripReimbursement)
            throws ParameterException, ProcessingException {
        LOG.info("dtoFromEntity");
        if (tCkCtTripReimbursement == null) {
            throw new ParameterException("param entity null");
        }
        CkCtTripReimbursement ckCtTripReimbursement = new CkCtTripReimbursement(tCkCtTripReimbursement);
        if (tCkCtTripReimbursement.getTCkCtMstReimbursementType() != null) {
            ckCtTripReimbursement.setTCkCtMstReimbursementType(
                    new CkCtMstReimbursementType(tCkCtTripReimbursement.getTCkCtMstReimbursementType()));
        }
        if (tCkCtTripReimbursement.getTCkCtTrip() != null) {
            ckCtTripReimbursement.setTCkCtTrip(new CkCtTrip(tCkCtTripReimbursement.getTCkCtTrip()));
        }
        if (StringUtils.isNotBlank(tCkCtTripReimbursement.getTrReceiptLoc())) {
            try {
                String base64 = FileUtil.toBase64(tCkCtTripReimbursement.getTrReceiptLoc());
                ckCtTripReimbursement.setBase64File(base64);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
        return ckCtTripReimbursement;
    }

    @Override
    protected TCkCtTripReimbursement entityFromDTO(CkCtTripReimbursement ckCtTripReimbursement)
            throws ParameterException, ProcessingException {
        LOG.info("entityFromDTO");
        if (ckCtTripReimbursement == null) {
            throw new ParameterException("param entity null");
        }
        if (isReimbursementMoreThanCharge(ckCtTripReimbursement)) {
            throw new ProcessingException("Reimbursement cannot be more than trip charge");
        }
        TCkCtTripReimbursement tCkCtTripReimbursement = new TCkCtTripReimbursement(ckCtTripReimbursement);
        if (ckCtTripReimbursement.getTCkCtMstReimbursementType() != null) {
            tCkCtTripReimbursement.setTCkCtMstReimbursementType(
                    ckCtTripReimbursement.getTCkCtMstReimbursementType().toEntity(new TCkCtMstReimbursementType()));
        }
        if (ckCtTripReimbursement.getTCkCtTrip() != null) {
            tCkCtTripReimbursement.setTCkCtTrip(ckCtTripReimbursement.getTCkCtTrip().toEntity(new TCkCtTrip()));
        }
        saveOrUpdateReceipt(ckCtTripReimbursement);
        tCkCtTripReimbursement.setTrReceiptName(ckCtTripReimbursement.getTrReceiptName());
        tCkCtTripReimbursement.setTrReceiptLoc(ckCtTripReimbursement.getTrReceiptLoc());
        return tCkCtTripReimbursement;
    }

    @Override
    protected String entityKeyFromDTO(CkCtTripReimbursement ckCtTripReimbursement)
            throws ParameterException, ProcessingException {
        if (ckCtTripReimbursement == null) {
            throw new ParameterException("param dto null");
        }
        return ckCtTripReimbursement.getTrId();
    }

    @Override
    protected CoreMstLocale getCoreMstLocale(CkCtTripReimbursement ckCtTripReimbursement)
            throws ParameterException, EntityNotFoundException, ProcessingException {
        if (ckCtTripReimbursement == null) {
            throw new ParameterException("dto param null");
        }
        if (ckCtTripReimbursement.getCoreMstLocale() == null) {
            throw new ProcessingException("coreMstLocale null");
        }
        return ckCtTripReimbursement.getCoreMstLocale();
    }

    @Override
    protected HashMap<String, Object> getParameters(CkCtTripReimbursement ckCtTripReimbursement)
            throws ParameterException, ProcessingException {
        LOG.info("getParameters");
        if (ckCtTripReimbursement == null) {
            throw new ParameterException("param dto null");
        }
        HashMap<String, Object> parameters = new HashMap<>();
        if (ckCtTripReimbursement.getTCkCtTrip() != null) {
            if (StringUtils.isNotBlank(ckCtTripReimbursement.getTCkCtTrip().getTrId())) {
                parameters.put(CkCtTripReimbursementConstant.ColumnParam.TR_TRIP,
                        ckCtTripReimbursement.getTCkCtTrip().getTrId());
            }
            if (ckCtTripReimbursement.getTCkCtTrip().getTCkJobTruck() != null) {
                if (StringUtils.isNotBlank(ckCtTripReimbursement.getTCkCtTrip().getTCkJobTruck().getJobId())) {
                    parameters.put(CkCtTripReimbursementConstant.ColumnParam.TR_JOB_ID,
                    		ckCtTripReimbursement.getTCkCtTrip().getTCkJobTruck().getJobId());
                }
            }
        }
        if (StringUtils.isNotBlank(ckCtTripReimbursement.getTrId())) {
            parameters.put(CkCtTripReimbursementConstant.ColumnParam.TR_ID, ckCtTripReimbursement.getTrId());
        }
        if (ckCtTripReimbursement.getTCkCtMstReimbursementType() != null) {
            if (StringUtils.isNotBlank(ckCtTripReimbursement.getTCkCtMstReimbursementType().getRbtypId())) {
                parameters.put(CkCtTripReimbursementConstant.ColumnParam.TR_TYPE,
                        ckCtTripReimbursement.getTCkCtMstReimbursementType().getRbtypId());
            }
        }
        if (StringUtils.isNotBlank(ckCtTripReimbursement.getTrRemarks())) {
            parameters.put(CkCtTripReimbursementConstant.ColumnParam.TR_REMARKS,
                    "%" + ckCtTripReimbursement.getTrRemarks() + "%");
        }
        if (ckCtTripReimbursement.getTrPrice() != null) {
            parameters.put(CkCtTripReimbursementConstant.ColumnParam.TR_PRICE, ckCtTripReimbursement.getTrPrice());
        }
        if (ckCtTripReimbursement.getTrTax() != null) {
            parameters.put(CkCtTripReimbursementConstant.ColumnParam.TR_PRICE, ckCtTripReimbursement.getTrTax());
        }
        parameters.put(CkCtTripReimbursementConstant.ColumnParam.TR_STATUS,
                (ckCtTripReimbursement.getTrStatus() == null) ? RecordStatus.ACTIVE.getCode()
                        : ckCtTripReimbursement.getTrStatus());
        return parameters;
    }

    @Override
    protected String getWhereClause(CkCtTripReimbursement ckCtTripReimbursement, boolean wherePrinted)
            throws ParameterException, ProcessingException {
        LOG.info("getWhereClause");
        String EQUAL = " = :", CONTAIN = " like :";
        if (ckCtTripReimbursement == null) {
            throw new ParameterException("param dto null");
        }
        StringBuffer condition = new StringBuffer();
        if (ckCtTripReimbursement.getTCkCtTrip() != null) {
            if (StringUtils.isNotBlank(ckCtTripReimbursement.getTCkCtTrip().getTrId())) {
                condition.append(getOperator(wherePrinted) + CkCtTripReimbursementConstant.Column.TR_TRIP + EQUAL
                        + CkCtTripReimbursementConstant.ColumnParam.TR_TRIP);
                wherePrinted = true;
            }
            if (ckCtTripReimbursement.getTCkCtTrip().getTCkJobTruck() != null) {
                if (StringUtils.isNotBlank(ckCtTripReimbursement.getTCkCtTrip().getTCkJobTruck().getJobId())) {
                    condition.append(getOperator(wherePrinted) + CkCtTripReimbursementConstant.Column.TR_TRIP_JOBID + EQUAL
                            + CkCtTripReimbursementConstant.ColumnParam.TR_JOB_ID);
                    wherePrinted = true;
                }
            }
        }
        if (StringUtils.isNotBlank(ckCtTripReimbursement.getTrId())) {
            condition.append(getOperator(wherePrinted) + CkCtTripReimbursementConstant.Column.TR_ID + EQUAL
                    + CkCtTripReimbursementConstant.ColumnParam.TR_ID);
            wherePrinted = true;
        }
        if (ckCtTripReimbursement.getTCkCtMstReimbursementType() != null) {
            if (StringUtils.isNotBlank(ckCtTripReimbursement.getTCkCtMstReimbursementType().getRbtypId())) {
                condition.append(getOperator(wherePrinted) + CkCtTripReimbursementConstant.Column.TR_TYPE + EQUAL
                        + CkCtTripReimbursementConstant.ColumnParam.TR_TYPE);
                wherePrinted = true;
            }
        }
        if (StringUtils.isNotBlank(ckCtTripReimbursement.getTrRemarks())) {
            condition.append(getOperator(wherePrinted) + EQUAL + CkCtTripReimbursementConstant.Column.TR_REMARKS
                    + CONTAIN + CkCtTripReimbursementConstant.ColumnParam.TR_REMARKS);
            wherePrinted = true;
        }
        if (ckCtTripReimbursement.getTrPrice() != null) {
            condition.append(getOperator(wherePrinted) + EQUAL + CkCtTripReimbursementConstant.Column.TR_PRICE + EQUAL
                    + CkCtTripReimbursementConstant.ColumnParam.TR_PRICE);
            wherePrinted = true;
        }
        if (ckCtTripReimbursement.getTrTax() != null) {
            condition.append(getOperator(wherePrinted) + EQUAL + CkCtTripReimbursementConstant.Column.TR_TAX + EQUAL
                    + CkCtTripReimbursementConstant.ColumnParam.TR_TAX);
            wherePrinted = true;
        }
        condition.append(getOperator(wherePrinted) + CkCtTripReimbursementConstant.Column.TR_STATUS + EQUAL
                + CkCtTripReimbursementConstant.ColumnParam.TR_STATUS);
        return condition.toString();
    }

    @Override
    protected TCkCtTripReimbursement initEnity(TCkCtTripReimbursement tCkCtTripReimbursement)
            throws ParameterException, ProcessingException {
        LOG.info("initEnity");
        if (tCkCtTripReimbursement != null) {
            Hibernate.initialize(tCkCtTripReimbursement.getTCkCtMstReimbursementType());
            Hibernate.initialize(tCkCtTripReimbursement.getTCkCtTrip());
        }
        return tCkCtTripReimbursement;
    }

    @Override
    protected CkCtTripReimbursement preSaveUpdateDTO(TCkCtTripReimbursement tCkCtTripReimbursement,
            CkCtTripReimbursement ckCtTripReimbursement) throws ParameterException, ProcessingException {
        LOG.info("preSaveUpdateDTO");
        if (tCkCtTripReimbursement == null) {
            throw new ParameterException("param entity null");
        }
        if (ckCtTripReimbursement == null) {
            throw new ParameterException("param dto null");
        }
        if (isReimbursementMoreThanCharge(ckCtTripReimbursement)) {
            throw new ProcessingException("Reimbursement cannot be more than trip charge");
        }
        saveOrUpdateReceipt(ckCtTripReimbursement);
        ckCtTripReimbursement.setTrDtCreate(tCkCtTripReimbursement.getTrDtCreate());
        ckCtTripReimbursement.setTrUidCreate(tCkCtTripReimbursement.getTrUidCreate());
        return ckCtTripReimbursement;
    }

    @Override
    protected void preSaveValidation(CkCtTripReimbursement arg0, Principal principal)
            throws ParameterException, ProcessingException {

    }

    @Override
    protected ServiceStatus preUpdateValidation(CkCtTripReimbursement arg0, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtTripReimbursement setCoreMstLocale(CoreMstLocale arg0, CkCtTripReimbursement arg1)
            throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtTripReimbursement updateEntity(ACTION action, TCkCtTripReimbursement tCkCtTripReimbursement,
            Principal principal, Date date) throws ParameterException, ProcessingException {
        LOG.info("updateEntity");
        if (tCkCtTripReimbursement == null)
            throw new ParameterException("param entity null");
        if (principal == null)
            throw new ParameterException("param principal null");
        if (date == null)
            throw new ParameterException("param date null");
        Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
        switch (action) {
            case CREATE:
                tCkCtTripReimbursement
                        .setTrId(CkUtil.generateId(CkCtTripReimbursementConstant.Prefix.PREFIX_CK_CT_TRP_REM));
                tCkCtTripReimbursement.setTrStatus(RecordStatus.ACTIVE.getCode());
                tCkCtTripReimbursement.setTrUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
                tCkCtTripReimbursement.setTrDtCreate(date);
                tCkCtTripReimbursement.setTrDtLupd(date);
                tCkCtTripReimbursement.setTrUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
                break;

            case MODIFY:
                tCkCtTripReimbursement.setTrDtLupd(date);
                tCkCtTripReimbursement.setTrUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
                break;

            default:
                break;
        }
        return tCkCtTripReimbursement;
    }

    @Override
    protected TCkCtTripReimbursement updateEntityStatus(TCkCtTripReimbursement tCkCtTripReimbursement, char status)
            throws ParameterException, ProcessingException {
        LOG.info("updateEntityStatus");
        if (tCkCtTripReimbursement == null) {
            throw new ParameterException("entity param null");
        }
        tCkCtTripReimbursement.setTrStatus(status);
        return tCkCtTripReimbursement;
    }

    @Override
    protected CkCtTripReimbursement whereDto(EntityFilterRequest filterRequest)
            throws ParameterException, ProcessingException {
        LOG.info("whereDto");
        if (filterRequest == null) {
            throw new ParameterException("param filterRequest null");
        }
        CkCtTripReimbursement ckCtTripReimbursement = new CkCtTripReimbursement();
        CkCtTrip ckCtTrip = new CkCtTrip();
        CkJobTruck jobTruck = new CkJobTruck();
        
        ckCtTrip.setTCkJobTruck(jobTruck);
        ckCtTripReimbursement.setTCkCtTrip(ckCtTrip);
        CkCtMstReimbursementType ckCtMstReimbursementType = new CkCtMstReimbursementType();
        ckCtTripReimbursement.setTCkCtMstReimbursementType(ckCtMstReimbursementType);
        for (EntityWhere entityWhere : filterRequest.getWhereList()) {
            if (entityWhere == null) {
                continue;
            }
            String attribute = "o." + entityWhere.getAttribute();
            if (CkCtTripReimbursementConstant.Column.TR_TRIP.equalsIgnoreCase(attribute)) {
                ckCtTrip.setTrId(entityWhere.getValue());
            } else if (CkCtTripReimbursementConstant.Column.TR_TRIP_JOBID.equalsIgnoreCase(attribute)) {
                ckCtTrip.getTCkJobTruck().setJobId(entityWhere.getValue());
            } else if (CkCtTripReimbursementConstant.Column.TR_TYPE.equalsIgnoreCase(attribute)) {
                ckCtMstReimbursementType.setRbtypId(entityWhere.getValue());
            } else if (CkCtTripReimbursementConstant.Column.TR_ID.equals(attribute)) {
                ckCtTripReimbursement.setTrId(entityWhere.getValue());
            } else if (CkCtTripReimbursementConstant.Column.TR_REMARKS.equalsIgnoreCase(attribute)) {
                ckCtTripReimbursement.setTrRemarks(entityWhere.getValue());
            } else if (CkCtTripReimbursementConstant.Column.TR_PRICE.equalsIgnoreCase(attribute)) {
                ckCtTripReimbursement.setTrPrice(NumberUtil.toBigDecimal(entityWhere.getValue()));
            } else if (CkCtTripReimbursementConstant.Column.TR_TAX.equalsIgnoreCase(attribute)) {
                ckCtTripReimbursement.setTrTax(NumberUtil.toBigDecimal(entityWhere.getValue()));
            } else if (CkCtTripReimbursementConstant.Column.TR_STATUS.equalsIgnoreCase(attribute)) {
                ckCtTripReimbursement
                        .setTrStatus((entityWhere.getValue() == null) ? null : entityWhere.getValue().charAt(0));
            }
        }
        return ckCtTripReimbursement;
    }

    private EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) {
        if (orderBy == null) {
            return null;
        }
        if (StringUtils.isEmpty(orderBy.getAttribute())) {
            return null;
        }
        String newAttr = orderBy.getAttribute();
        newAttr = newAttr.replaceAll("tckCtMstReimbursementType", "TCkCtMstReimbursementType");
        orderBy.setAttribute(newAttr);
        return orderBy;
    }

    private void saveOrUpdateReceipt(CkCtTripReimbursement ckCtTripReimbursement) {
        if (StringUtils.isNotBlank(ckCtTripReimbursement.getBase64File())
                && StringUtils.isNotBlank(ckCtTripReimbursement.getTrReceiptName())) {
            try {
                byte[] data = Base64.getDecoder().decode(ckCtTripReimbursement.getBase64File());
                String basePath;
                basePath = getSysParam(CtConstant.KEY_ATTCH_BASE_LOCATION);
                if (StringUtils.isBlank(basePath)) {
                    throw new ProcessingException("basePath is not configured");
                }
                String filePath = FileUtil.saveAttachment(basePath.concat(ckCtTripReimbursement.getTrReceiptName()),
                        data);
                ckCtTripReimbursement.setTrReceiptLoc(filePath);
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

    private boolean isReimbursementMoreThanCharge(CkCtTripReimbursement ckCtTripReimbursement) {
        try {
            List<TCkCtTripReimbursement> tCkCtTripReimbursements = ckCtTripReimbursementDao
                    .findByTripIdAndStatus(ckCtTripReimbursement.getTCkCtTrip().getTrId(),
                            RecordStatus.ACTIVE.getCode());
            double totalNegativeReimbursement = 0.0;
            for (TCkCtTripReimbursement tCkCtTripReimbursement : tCkCtTripReimbursements) {
                double reimbursement = Optional.ofNullable(tCkCtTripReimbursement.getTrTotal())
                        .orElse(BigDecimal.ZERO).doubleValue();
                if (reimbursement < 0) {
                    totalNegativeReimbursement += reimbursement;
                }
            }
            TCkCtTrip tCkCtTrip = ckCtTripDao.find(ckCtTripReimbursement.getTCkCtTrip().getTrId());
            Hibernate.initialize(tCkCtTrip.getTCkCtTripCharge());
            double tripCharge = Optional.ofNullable(tCkCtTrip.getTCkCtTripCharge().getTcPrice()).orElse(BigDecimal.ZERO)
                    .doubleValue();
            return Math.abs(totalNegativeReimbursement) > tripCharge;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    private CkCtTripReimbursement clearAmountTo0(CkCtTripReimbursement ckCtTripReimbursement) {
    
    	ckCtTripReimbursement.setTrPrice(BigDecimal.ZERO);
    	ckCtTripReimbursement.setTrTax(BigDecimal.ZERO);
    	ckCtTripReimbursement.setTrTotal(BigDecimal.ZERO);
    	
    	return ckCtTripReimbursement;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public Map<String, Object> calculateTotalCharges(String tripId) throws ProcessingException {
        Map<String, Object> map = new HashMap<>();
        
        try {

    		Principal principal = principalUtilService.getPrincipal();
    		if (principal == null) {
    			throw new ProcessingException("principal is null");
    		}
			if(principal.getCoreAccn() != null ) {
				if (principal.getCoreAccn().getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF_CO.name())) {

		            map.put("totalReimbursement", BigDecimal.ZERO);
		            map.put("totalJobCharge", BigDecimal.ZERO);
		            return map;
				}
			}
    		
            List<TCkCtTripReimbursement> tCkCtTripReimbursements = ckCtTripReimbursementDao
                    .findByTripIdAndStatus(tripId, RecordStatus.ACTIVE.getCode());

            double totalReimbursement = 0.0, totalJobCharge = 0.0;
            // double totalNegativeReimbursement = 0.0;

            // Total Job Charge AA [START]
            TCkCtTrip ckcTrip = ckCtTripDao.find(tripId);
            TCkJobTruck ckJobTruck = null;
            if (null != ckcTrip) {
                ckJobTruck = ckJobTruckDao.find(ckcTrip.getTCkJobTruck().getJobId());
            }
            if (null != ckJobTruck) {
                totalJobCharge = null != ckJobTruck.getJobTotalCharge() ? ckJobTruck.getJobTotalCharge().doubleValue()
                        : 0.0;
            }
            // Total Job Charge AA [END]

            if (!tCkCtTripReimbursements.isEmpty()) {
                for (TCkCtTripReimbursement tCkCtTripReimbursement : tCkCtTripReimbursements) {
                    double reimbursement = Optional.ofNullable(tCkCtTripReimbursement.getTrTotal())
                            .orElse(BigDecimal.ZERO).doubleValue();
                    totalReimbursement += reimbursement;
                }
                TCkCtTripReimbursement tCkCtTripReimbursement = tCkCtTripReimbursements.get(0);
                Hibernate.initialize(tCkCtTripReimbursement.getTCkCtTrip());
                Hibernate.initialize(tCkCtTripReimbursement.getTCkCtTrip().getTCkCtTripCharge());
                Hibernate.initialize(tCkCtTripReimbursement.getTCkCtTrip().getTCkJobTruck());
                totalJobCharge = totalReimbursement
                        + tCkCtTripReimbursement.getTCkCtTrip().getTCkCtTripCharge().getTcPrice().doubleValue();
                Optional<TCkCtPayment> optTCkCtPayment = ckCtPaymentDao
                        .findByJobId(tCkCtTripReimbursement.getTCkCtTrip().getTCkJobTruck().getJobId());
                TCkCtPayment tCkCtPayment = null;
                if (optTCkCtPayment.isPresent()) {
                    tCkCtPayment = optTCkCtPayment.get();
                } else {
                    tCkCtPayment = new TCkCtPayment(CkUtil.generateId("CKCTPAY"));
                    List<TCkCtToInvoice> tCkCtToInvoices = ckCtToInvoiceDao.findByTripId(tripId);
                    if (!tCkCtToInvoices.isEmpty()) {
                        tCkCtPayment.setCtpRef(tCkCtToInvoices.get(0).getInvNo());
                    }
                    tCkCtPayment.setCtpJob(tCkCtTripReimbursement.getTCkCtTrip().getTCkJobTruck().getJobId());
                    tCkCtPayment.setCtpItem(tCkCtPayment.getCtpJob() + "-" + tCkCtPayment.getCtpRef());
                    tCkCtPayment.setCtpQty(new Short("1"));
                    TMstCurrency tMstCurrency = new TMstCurrency();
                    tMstCurrency.setCcyCode("IDR");
                    tCkCtPayment.setTMstCurrency(tMstCurrency);
                }
                tCkCtPayment.setCtpAmount(NumberUtil.toBigDecimal(totalReimbursement));
                ckCtPaymentDao.saveOrUpdate(tCkCtPayment);
            }
            map.put("totalReimbursement", totalReimbursement);
            map.put("totalJobCharge", totalJobCharge);
        } catch (ProcessingException e) {
            throw new ProcessingException(e.getMessage());
        } catch (Exception e) {
            LOG.error(e);
        }
        return map;
    }
}
