package com.guudint.clickargo.clictruck.admin.shell.service.impl;

import com.guudint.clickargo.clictruck.admin.shell.dto.CkCtShellBatchWindow;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellBatchWindow;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellKiosk;
import com.guudint.clickargo.clictruck.admin.shell.service.CkCtShellBatchWindowService;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.ICkConstant;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CkCtShellBatchWindowServiceImpl extends AbstractClickCargoEntityService<TCkCtShellBatchWindow, String, CkCtShellBatchWindow> implements ICkConstant, CkCtShellBatchWindowService {

    private static final Logger LOG = Logger.getLogger(CkCtShellBatchWindowServiceImpl.class);
    private static final String AUDIT = "SHELL_BATCH_WINDOW";
    private static final String TABLE = "T_CK_CT_SHELL_BATCH_WINDOW";


    public CkCtShellBatchWindowServiceImpl() {
        super("ckCtShellBatchWindowDao", AUDIT, TCkCtShellBatchWindow.class.getName(), TABLE);
    }

    @Override
    public void updateStatus(String id, char status) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        if (id == null) {
            throw new ParameterException("id entity null");
        }

        try {
            TCkCtShellBatchWindow entity = super.dao.find(id);
            if (entity == null) {
                throw new EntityNotFoundException("updateStatus ID::" + id);
            }
            this.updateEntityStatus(entity, status);
            super.dao.update(entity);
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
    public CkCtShellBatchWindow newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellBatchWindow initEnity(TCkCtShellBatchWindow entity) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellBatchWindow entityFromDTO(CkCtShellBatchWindow dto) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellBatchWindow dtoFromEntity(TCkCtShellBatchWindow entity) throws ParameterException, ProcessingException {
        LOG.info("dtoFromEntity: TCkCtShellBatchWindow");
        if (entity == null) {
            throw new ParameterException("param entity null");
        }
        CkCtShellBatchWindow dto = new CkCtShellBatchWindow();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    protected String entityKeyFromDTO(CkCtShellBatchWindow dto) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellBatchWindow updateEntity(ACTION action, TCkCtShellBatchWindow entity, Principal principal, Date date) throws ParameterException, ProcessingException {
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
    protected TCkCtShellBatchWindow updateEntityStatus(TCkCtShellBatchWindow entity, char status) throws ParameterException, ProcessingException {
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
    protected CkCtShellBatchWindow preSaveUpdateDTO(TCkCtShellBatchWindow entity, CkCtShellBatchWindow dto) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected void preSaveValidation(CkCtShellBatchWindow dto, Principal principal) throws ParameterException, ProcessingException {

    }

    @Override
    protected ServiceStatus preUpdateValidation(CkCtShellBatchWindow dto, Principal principal) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected String getWhereClause(CkCtShellBatchWindow dto, boolean b) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected HashMap<String, Object> getParameters(CkCtShellBatchWindow dto) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellBatchWindow whereDto(EntityFilterRequest entityFilterRequest) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CoreMstLocale getCoreMstLocale(CkCtShellBatchWindow dto) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellBatchWindow setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtShellBatchWindow dto) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    public CkCtShellBatchWindow findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");

        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");

            TCkCtShellBatchWindow entity = dao.find(id);
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
    public CkCtShellBatchWindow deleteById(String s, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        return null;
    }

    @Override
    public List<CkCtShellBatchWindow> filterBy(EntityFilterRequest entityFilterRequest) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    public void update(TCkCtShellBatchWindow entity) throws ParameterException, ProcessingException {
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

    public TCkCtShellBatchWindow find(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");
        TCkCtShellBatchWindow entity =  null;
        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");
            entity = dao.find(id);
        } catch (ParameterException | EntityNotFoundException ex) {
            LOG.error("findById", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("findById", ex);
            throw new ProcessingException(ex);
        }
        return entity;
    }
}
