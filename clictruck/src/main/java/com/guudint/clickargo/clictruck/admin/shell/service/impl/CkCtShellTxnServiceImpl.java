package com.guudint.clickargo.clictruck.admin.shell.service.impl;

import com.guudint.clickargo.clictruck.admin.shell.dto.CkCtShellTxn;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellKiosk;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellTxn;
import com.guudint.clickargo.clictruck.admin.shell.service.CkCtShellTxnService;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoFm;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class CkCtShellTxnServiceImpl extends AbstractClickCargoEntityService<TCkCtShellTxn, String, CkCtShellTxn> implements ICkConstant, CkCtShellTxnService {

    private static final Logger LOG = Logger.getLogger(CkCtShellTxnServiceImpl.class);
    private static final String AUDIT = "SHELL_TXN";
    private static final String TABLE = "T_CK_CT_SHELL_TXN";

    public CkCtShellTxnServiceImpl() {
        super("ckCtShellTxnDao", AUDIT, TCkCtShellTxn.class.getName(), TABLE);
    }

    @Override
    public void updateStatus(String id, char status) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        if (id == null) {
            throw new ParameterException("id entity null");
        }

        try {
            TCkCtShellTxn TCkCtShellTxn = super.dao.find(id);
            if (TCkCtShellTxn == null) {
                throw new EntityNotFoundException("updateStatus ID::" + id);
            }
            this.updateEntityStatus(TCkCtShellTxn, status);
            super.dao.update(TCkCtShellTxn);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ProcessingException(e);
        }
    }

    @Override
    protected void initBusinessValidator() {

    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public CkCtShellTxn newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellTxn initEnity(TCkCtShellTxn TCkCtShellTxn) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellTxn entityFromDTO(CkCtShellTxn ckCtShellTxn) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellTxn dtoFromEntity(TCkCtShellTxn entity) throws ParameterException, ProcessingException {
        LOG.info("dtoFromEntity: TCkCtShellTxn");
        if (entity == null) {
            throw new ParameterException("param entity null");
        }
        CkCtShellTxn dto = new CkCtShellTxn();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    protected String entityKeyFromDTO(CkCtShellTxn ckCtShellTxn) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellTxn updateEntity(ACTION action, TCkCtShellTxn entity, Principal principal, Date date) throws ParameterException, ProcessingException {
        Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
        switch (action) {
            case CREATE:
                entity.setSbUidCreate(opUserId.orElse(Constant.DEFAULT_USR));
                entity.setSbDtCreate(date);
                entity.setSbDtLupd(date);
                entity.setSbUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
                entity.setSbStatus('A');
                break;

            case MODIFY:
                entity.setSbDtLupd(date);
                entity.setSbUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
                break;
            default:
                break;
        }
        return entity;
    }

    @Override
    protected TCkCtShellTxn updateEntityStatus(TCkCtShellTxn entity, char status) throws ParameterException, ProcessingException {
        LOG.debug("updateEntityStatus");

        try {
            if (null == entity)
                throw new ParameterException("entity param null");

            entity.setSbStatus(status);
            return entity;
        } catch (ParameterException ex) {
            LOG.error("updateEntityStatus", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("updateEntityStatus", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    protected CkCtShellTxn preSaveUpdateDTO(TCkCtShellTxn TCkCtShellTxn, CkCtShellTxn ckCtShellTxn) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected void preSaveValidation(CkCtShellTxn ckCtShellTxn, Principal principal) throws ParameterException, ProcessingException {

    }

    @Override
    protected ServiceStatus preUpdateValidation(CkCtShellTxn ckCtShellTxn, Principal principal) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected String getWhereClause(CkCtShellTxn ckCtShellTxn, boolean b) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected HashMap<String, Object> getParameters(CkCtShellTxn ckCtShellTxn) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellTxn whereDto(EntityFilterRequest entityFilterRequest) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CoreMstLocale getCoreMstLocale(CkCtShellTxn ckCtShellTxn) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellTxn setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtShellTxn ckCtShellTxn) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    public CkCtShellTxn findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");

        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");

            TCkCtShellTxn entity = dao.find(id);
            if (null == entity)
                throw new EntityNotFoundException("id: " + id);

            return this.dtoFromEntity(entity);
        } catch (ParameterException | EntityNotFoundException ex) {
            LOG.error("findById", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("findById", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    public CkCtShellTxn deleteById(String s, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        return null;
    }

    @Override
    public List<CkCtShellTxn> filterBy(EntityFilterRequest entityFilterRequest) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    public void save(TCkCtShellTxn entity) throws ParameterException, ProcessingException {
        if (entity == null) {
            throw new ParameterException("param entity null");
        }
        try {
            super.dao.add(entity);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ProcessingException(e);
        }
    }

    @Override
    public void update(TCkCtShellTxn entity) throws ParameterException, ProcessingException {
        if (entity == null) {
            throw new ParameterException("param entity null");
        }
        try {
            super.dao.update(entity);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ProcessingException(e);
        }
    }

    public TCkCtShellTxn findByTxn(String txnId) throws ParameterException {
        LOG.info("findByTxn");

        if (txnId == null) {
            throw new ParameterException("param txnId null");
        }

        TCkCtShellTxn tCkCtShellTxn = null;
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("stTxnId", txnId);
            String hql = "FROM TCkCtShellTxn o WHERE o.stTxnId = :stTxnId";
            List<TCkCtShellTxn> entity = dao.getByQuery(hql, parameters);
            if (entity != null && entity.size() > 0) {
                if (Objects.nonNull(entity.get(0).getStId())){
                    tCkCtShellTxn = dao.find(entity.get(0).getStId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tCkCtShellTxn;
    }

    @Override
    public List<TCkCtShellTxn> getAllShellTxn() throws ParameterException, ProcessingException {
        LOG.info("getAllShellTxn");
        try {
            Map<String, Object> parameters = new HashMap<>();

            LocalDate today = LocalDate.now();
            LocalDate firstDayOfCurrentMonth = today.withDayOfMonth(1);
            LocalDate lastDayOfPreviousMonth = firstDayOfCurrentMonth.minusDays(1);
            LocalDate firstDayOfPreviousMonth = lastDayOfPreviousMonth.withDayOfMonth(1);

            Date startDate = Date.from(firstDayOfPreviousMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(lastDayOfPreviousMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            parameters.put("startDate", startDate);
            parameters.put("endDate", endDate);
            parameters.put("sbStatus", 'A');
            String hql = "FROM TCkCtShellTxn o WHERE o.stTxnDate BETWEEN :startDate AND :endDate AND o.sbStatus = :sbStatus";
            List<TCkCtShellTxn> entity = dao.getByQuery(hql, parameters);
            if (entity != null && entity.size() > 0) {
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("getAllShellTxn", e);
        }
        return null;
    }
}
