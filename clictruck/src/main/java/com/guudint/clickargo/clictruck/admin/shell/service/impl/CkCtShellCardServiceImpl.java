package com.guudint.clickargo.clictruck.admin.shell.service.impl;

import com.guudint.clickargo.clictruck.admin.shell.dto.CkCtShellCard;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
import com.guudint.clickargo.clictruck.admin.shell.service.CkCtShellCardService;
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

import java.util.*;

public class CkCtShellCardServiceImpl extends AbstractClickCargoEntityService<TCkCtShellCard, String, CkCtShellCard> implements ICkConstant, CkCtShellCardService {

    private static final Logger LOG = Logger.getLogger(CkCtShellCardServiceImpl.class);
    private static final String AUDIT = "SHELL_CARD";
    private static final String TABLE = "T_CK_CT_SHELL_CARD";

    public static char STATUS_ACTIVE = 'A';
    public static char STATUS_EXPIRED = 'E';
    public static char STATUS_INACTIVE = 'I';

    public CkCtShellCardServiceImpl() {
        super("ckCtShellCardDao", AUDIT, TCkCtShellCard.class.getName(), TABLE);
    }

    @Override
    public void updateStatus(String id, char status) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        if (id == null) {
            throw new ParameterException("id entity null");
        }

        try {
            TCkCtShellCard tCkCtShellCard = super.dao.find(id);
            if (tCkCtShellCard == null) {
                throw new EntityNotFoundException("updateStatus ID::" + id);
            }
            this.updateEntityStatus(tCkCtShellCard, status);
            super.dao.update(tCkCtShellCard);
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
    public CkCtShellCard newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellCard initEnity(TCkCtShellCard tCkCtShellCard) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellCard entityFromDTO(CkCtShellCard ckCtShellCard) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellCard dtoFromEntity(TCkCtShellCard tCkCtShellCard) throws ParameterException, ProcessingException {
        LOG.info("dtoFromEntity: TCkCtShellCard");
        if (tCkCtShellCard == null) {
            throw new ParameterException("param entity null");
        }
        CkCtShellCard shellCardDto = new CkCtShellCard();
        BeanUtils.copyProperties(tCkCtShellCard, shellCardDto);
        return shellCardDto;
    }

    @Override
    protected String entityKeyFromDTO(CkCtShellCard ckCtShellCard) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellCard updateEntity(ACTION action, TCkCtShellCard entity, Principal principal, Date date) throws ParameterException, ProcessingException {
        Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
        switch (action) {
            case CREATE:
                entity.setScUidCreate(opUserId.orElse(Constant.DEFAULT_USR));
                entity.setScDtCreate(date);
                entity.setScDtLupd(date);
                entity.setScUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
                entity.setScStatus('A');
                break;

            case MODIFY:
                entity.setScDtLupd(date);
                entity.setScUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
                break;
            default:
                break;
        }
        return entity;
    }

    @Override
    protected TCkCtShellCard updateEntityStatus(TCkCtShellCard entity, char status) throws ParameterException, ProcessingException {
        LOG.debug("updateEntityStatus");

        try {
            if (null == entity)
                throw new ParameterException("entity param null");

            entity.setScStatus(status);
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
    protected CkCtShellCard preSaveUpdateDTO(TCkCtShellCard tCkCtShellCard, CkCtShellCard ckCtShellCard) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected void preSaveValidation(CkCtShellCard ckCtShellCard, Principal principal) throws ParameterException, ProcessingException {

    }

    @Override
    protected ServiceStatus preUpdateValidation(CkCtShellCard ckCtShellCard, Principal principal) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected String getWhereClause(CkCtShellCard ckCtShellCard, boolean b) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected HashMap<String, Object> getParameters(CkCtShellCard ckCtShellCard) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellCard whereDto(EntityFilterRequest entityFilterRequest) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CoreMstLocale getCoreMstLocale(CkCtShellCard ckCtShellCard) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellCard setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtShellCard ckCtShellCard) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    public CkCtShellCard findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");

        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");

            TCkCtShellCard entity = dao.find(id);
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
    public CkCtShellCard deleteById(String s, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        return null;
    }

    @Override
    public List<CkCtShellCard> filterBy(EntityFilterRequest entityFilterRequest) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    public void saveCard(TCkCtShellCard entity) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
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

    public TCkCtShellCard find(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");
        TCkCtShellCard tCkCtShellCard =  null;
        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");
            tCkCtShellCard = dao.find(id);
        } catch (ParameterException | EntityNotFoundException ex) {
            LOG.error("findById", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("findById", ex);
            throw new ProcessingException(ex);
        }
        return tCkCtShellCard;
    }
}
