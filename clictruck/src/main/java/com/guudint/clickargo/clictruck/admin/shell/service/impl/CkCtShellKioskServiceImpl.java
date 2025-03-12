package com.guudint.clickargo.clictruck.admin.shell.service.impl;

import com.guudint.clickargo.clictruck.admin.shell.dto.CkCtShellKiosk;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellKiosk;
import com.guudint.clickargo.clictruck.admin.shell.service.CkCtShellKioskService;
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

public class CkCtShellKioskServiceImpl extends AbstractClickCargoEntityService<TCkCtShellKiosk, String, CkCtShellKiosk> implements ICkConstant, CkCtShellKioskService {

    private static final Logger LOG = Logger.getLogger(CkCtShellKioskServiceImpl.class);
    private static final String AUDIT = "SHELL_KIOSK";
    private static final String TABLE = "T_CK_CT_SHELL_KIOSK";

    public CkCtShellKioskServiceImpl() {
        super("ckCtShellKioskDao", AUDIT, TCkCtShellKiosk.class.getName(), TABLE);
    }

    @Override
    public void updateStatus(String id, char status) throws ParameterException, EntityNotFoundException, ProcessingException {
        if (id == null) {
            throw new ParameterException("id entity null");
        }

        try {
            TCkCtShellKiosk shellKiosk = super.dao.find(id);
            if (shellKiosk == null) {
                throw new EntityNotFoundException("updateStatus ID::" + id);
            }
            this.updateEntityStatus(shellKiosk, status);
            super.dao.update(shellKiosk);
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
    public CkCtShellKiosk newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    public TCkCtShellKiosk initEnity(TCkCtShellKiosk tCkCtShellKiosk) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellKiosk entityFromDTO(CkCtShellKiosk ckCtShellKiosk) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellKiosk dtoFromEntity(TCkCtShellKiosk entity) throws ParameterException, ProcessingException {
        LOG.info("dtoFromEntity: dtoFromEntity");
        if (entity == null) {
            throw new ParameterException("param entity null");
        }
        CkCtShellKiosk shellKioskDto = new CkCtShellKiosk();
        BeanUtils.copyProperties(entity, shellKioskDto);
        return shellKioskDto;
    }

    @Override
    protected String entityKeyFromDTO(CkCtShellKiosk ckCtShellKiosk) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellKiosk updateEntity(ACTION action, TCkCtShellKiosk entity, Principal principal, Date date) throws ParameterException, ProcessingException {
        Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
        switch (action) {
            case CREATE:
                entity.setSkUidCreate(opUserId.orElse(Constant.DEFAULT_USR));
                entity.setSkDtCreate(date);
                entity.setSkDtLupd(date);
                entity.setSkUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
                entity.setSkStatus('A');
                break;

            case MODIFY:
                entity.setSkDtLupd(date);
                entity.setSkUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
                break;
            default:
                break;
        }
        return entity;
    }

    @Override
    protected TCkCtShellKiosk updateEntityStatus(TCkCtShellKiosk entity, char status) throws ParameterException, ProcessingException {
        LOG.debug("updateEntityStatus");

        try {
            if (null == entity)
                throw new ParameterException("entity param null");

            entity.setSkStatus(status);
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
    protected CkCtShellKiosk preSaveUpdateDTO(TCkCtShellKiosk tCkCtShellKiosk, CkCtShellKiosk ckCtShellKiosk) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected void preSaveValidation(CkCtShellKiosk ckCtShellKiosk, Principal principal) throws ParameterException, ProcessingException {

    }

    @Override
    protected ServiceStatus preUpdateValidation(CkCtShellKiosk ckCtShellKiosk, Principal principal) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected String getWhereClause(CkCtShellKiosk ckCtShellKiosk, boolean b) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected HashMap<String, Object> getParameters(CkCtShellKiosk ckCtShellKiosk) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellKiosk whereDto(EntityFilterRequest entityFilterRequest) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CoreMstLocale getCoreMstLocale(CkCtShellKiosk ckCtShellKiosk) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellKiosk setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtShellKiosk ckCtShellKiosk) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    public CkCtShellKiosk findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");

        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");

            TCkCtShellKiosk entity = dao.find(id);
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
    public CkCtShellKiosk deleteById(String s, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        return null;
    }

    @Override
    public List<CkCtShellKiosk> filterBy(EntityFilterRequest entityFilterRequest) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }



    @Override
    public List<TCkCtShellKiosk> getAllKiosks() throws ProcessingException {
        try {
            return super.dao.getAll();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ProcessingException(e);
        }
    }

    @Override
    public void removeUnfoundKiosks(List<String> unfoundIds) throws ParameterException, ProcessingException {
        if (unfoundIds == null) {
            throw new ParameterException("param ids null");
        }
        try {
            for (String id : unfoundIds) {
                TCkCtShellKiosk shellKiosk = super.dao.find(id);
                if (shellKiosk == null) {
                    throw new EntityNotFoundException("updateStatus ID::" + id);
                }
                super.dao.remove(shellKiosk);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ProcessingException(e);
        }
    }

    @Override
    public void save(TCkCtShellKiosk entity) throws ParameterException, ProcessingException {
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
    public void update(TCkCtShellKiosk entity) throws ParameterException, ProcessingException {
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

    public TCkCtShellKiosk find(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");
        TCkCtShellKiosk tCkCtShellKiosk =  null;
        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");
            tCkCtShellKiosk = dao.find(id);
        } catch (ParameterException | EntityNotFoundException ex) {
            LOG.error("findById", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("findById", ex);
            throw new ProcessingException(ex);
        }
        return tCkCtShellKiosk;
    }
}
