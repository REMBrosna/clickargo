package com.guudint.clickargo.clictruck.admin.shell.service.impl;

import com.guudint.clickargo.clictruck.admin.shell.dto.CkCtShellInvoice;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellInvoice;
import com.guudint.clickargo.clictruck.admin.shell.service.CkCtShellInvoiceService;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.service.impl.CkSeqNoServiceImpl;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class CkCtShellInvoiceServiceImpl extends AbstractClickCargoEntityService<TCkCtShellInvoice, String, CkCtShellInvoice> implements ICkConstant, CkCtShellInvoiceService {

    private static final Logger LOG = Logger.getLogger(CkCtShellInvoiceServiceImpl.class);
    private static final String AUDIT = "SHELL_INVOICE";
    private static final String TABLE = "T_CK_CT_SHELL_INVOICE";
    private static final String PREFIX = "INV";

    public CkCtShellInvoiceServiceImpl() {
        super("ckCtShellInvoiceDao", AUDIT, TCkCtShellInvoice.class.getName(), TABLE);
    }

    @Override
    public void updateStatus(String id, char status) throws ParameterException, EntityNotFoundException, ProcessingException {
        if (id == null) {
            throw new ParameterException("id entity null");
        }

        try {
            TCkCtShellInvoice entity = super.dao.find(id);
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
    public CkCtShellInvoice newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    public TCkCtShellInvoice initEnity(TCkCtShellInvoice entity) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellInvoice entityFromDTO(CkCtShellInvoice dto) throws ParameterException, ProcessingException {
        TCkCtShellInvoice entity = new TCkCtShellInvoice();
        TCoreAccn tCoreAccn = new TCoreAccn();
        if(Objects.nonNull(dto.getTCoreAccn())) {
            tCoreAccn.setAccnId(dto.getTCoreAccn().getAccnId());
        }
        entity.setTCoreAccn(tCoreAccn);
        entity.setInvUidLupd(dto.getInvUidLupd());
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected CkCtShellInvoice dtoFromEntity(TCkCtShellInvoice entity) throws ParameterException, ProcessingException {
        LOG.info("dtoFromEntity: dtoFromEntity");
        if (entity == null) {
            throw new ParameterException("param entity null");}
        CkCtShellInvoice dto = new CkCtShellInvoice();
        TCoreAccn tCoreAccn= new TCoreAccn();
        if (Objects.nonNull(entity.getTCoreAccn())) {
            tCoreAccn.setAccnId(entity.getTCoreAccn().getAccnId());
            tCoreAccn.setAccnName(entity.getTCoreAccn().getAccnName());
            tCoreAccn.setAccnNameOth(entity.getTCoreAccn().getAccnNameOth());
            tCoreAccn.setAccnCoyRegn(entity.getTCoreAccn().getAccnCoyRegn());
            tCoreAccn.setAccnNationality(entity.getTCoreAccn().getAccnNationality());
        }
        dto.setTCoreAccn(tCoreAccn);
        dto.setInvId(entity.getInvId());
        dto.setInvNo(entity.getInvNo());
        dto.setInvDt(entity.getInvDt());
        dto.setInvAmt(entity.getInvAmt());
        dto.setInvPaymentDt(entity.getInvPaymentDt());
        dto.setInvPaymentAmt(entity.getInvPaymentAmt());
        dto.setInvBalanceAmt(entity.getInvBalanceAmt());
        dto.setInvStatus(entity.getInvStatus());
        dto.setInvUidCreate(entity.getInvUidCreate());
        dto.setInvDtCreate(entity.getInvDtCreate());
        dto.setInvDtLupd(entity.getInvDtLupd());
        dto.setInvUidLupd(entity.getInvUidLupd());
        dto.setInvStatus(entity.getInvStatus());
        return dto;
    }

    @Override
    protected String entityKeyFromDTO(CkCtShellInvoice dto) throws ParameterException, ProcessingException {
        LOG.debug("entityKeyFromDTO");
        if (null == dto)
            throw new ParameterException("dto param null");

        return dto.getInvId();
    }

    @Override
    protected TCkCtShellInvoice updateEntity(ACTION action, TCkCtShellInvoice entity, Principal principal, Date date) throws ParameterException, ProcessingException {
        Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
        switch (action) {
            case CREATE:
                entity.setInvId(CkUtil.generateId(PREFIX));
                entity.setInvUidCreate(opUserId.orElse(Constant.DEFAULT_USR));
                entity.setInvDtCreate(date);
                entity.setInvDtLupd(date);
                entity.setInvUidLupd("");
                entity.setInvStatus('U');
                break;

            case MODIFY:
                entity.setInvDtLupd(date);
                entity.setInvUidLupd(entity.getInvUidLupd());
                break;
            default:
                break;
        }
        return entity;
    }

    @Override
    protected TCkCtShellInvoice updateEntityStatus(TCkCtShellInvoice entity, char status) throws ParameterException, ProcessingException {
        LOG.debug("updateEntityStatus");

        try {
            if (null == entity)
                throw new ParameterException("entity param null");

            entity.setInvStatus(status);
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
    protected CkCtShellInvoice preSaveUpdateDTO(TCkCtShellInvoice entity, CkCtShellInvoice dto) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected void preSaveValidation(CkCtShellInvoice dto, Principal principal) throws ParameterException, ProcessingException {

    }

    @Override
    protected ServiceStatus preUpdateValidation(CkCtShellInvoice dto, Principal principal) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected String getWhereClause(CkCtShellInvoice dto, boolean wherePrinted) throws ParameterException, ProcessingException {
        try {
            if (null == dto)
                throw new ParameterException("param dto null");
            String EQUAL = " = :", CONTAIN = " like :";
            StringBuilder searchStatement = new StringBuilder();

            if (!StringUtils.isEmpty(dto.getInvId())) {
                searchStatement.append(getOperator(wherePrinted)).append("o.invId LIKE :invId");
                wherePrinted = true;
            }

            if (dto.getInvStatus() != null) {
                searchStatement.append(getOperator(wherePrinted)).append("o.invStatus").append(CONTAIN).append("invStatus");
                wherePrinted = true;
            }

            if (dto.getTCoreAccn() != null && dto.getTCoreAccn().getAccnId() != null) {
                searchStatement.append(getOperator(wherePrinted)).append("o.TCoreAccn.accnId").append(EQUAL).append("accnId");
                wherePrinted = true;
            }

            return searchStatement.toString();
        } catch (ParameterException ex) {
            LOG.error("getWhereClause", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("getWhereClause", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    protected HashMap<String, Object> getParameters(CkCtShellInvoice dto) throws ParameterException, ProcessingException {
        try {
            if (null == dto)
                throw new ParameterException("param dto null");

            Principal principal = principalUtilService.getPrincipal();
            if (principal == null)
                throw new ProcessingException("principal null");

            HashMap<String, Object> parameters = new HashMap<String, Object>();

            if (!StringUtils.isEmpty(dto.getInvId())){
                parameters.put("invId", "%" + dto.getInvId() + "%");
            }
            if (dto.getInvStatus() != null) {
                parameters.put("invStatus", dto.getInvStatus());
            }
            if (dto.getTCoreAccn() != null && dto.getTCoreAccn().getAccnId() != null) {
                parameters.put("accnId", dto.getTCoreAccn().getAccnId());
            }

            return parameters;
        } catch (ParameterException ex) {
            LOG.error("getParameters", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("getParameters", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    protected CkCtShellInvoice whereDto(EntityFilterRequest entityFilterRequest) throws ParameterException, ProcessingException {
        LOG.info("whereDto");
        if (entityFilterRequest == null) {
            throw new ParameterException("param filterRequest null");
        }

        try {
            CkCtShellInvoice dto = new CkCtShellInvoice();
            for (EntityWhere entityWhere : entityFilterRequest.getWhereList()) {
                Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
                if (!opValue.isPresent())
                    continue;

                if ("invId".equalsIgnoreCase(entityWhere.getAttribute())) {
                    dto.setInvId(opValue.get());
                }

                if ("invStatus".equalsIgnoreCase(entityWhere.getAttribute())) {
                    dto.setInvStatus(opValue.get().charAt(0));
                }

                if ("TCoreAccn.accnId".equalsIgnoreCase(entityWhere.getAttribute())) {
                    TCoreAccn  tCoreAccn = new TCoreAccn();
                    tCoreAccn.setAccnId(opValue.get());
                    dto.setTCoreAccn(tCoreAccn);
                }
            }
            return dto;
        } catch (Exception ex) {
            LOG.error("whereDto", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    protected CoreMstLocale getCoreMstLocale(CkCtShellInvoice dto) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellInvoice setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtShellInvoice dto) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    public CkCtShellInvoice findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");

        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");

            TCkCtShellInvoice entity = dao.find(id);
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
    public CkCtShellInvoice deleteById(String s, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        return null;
    }

    @Override
    public List<CkCtShellInvoice> filterBy(EntityFilterRequest request) throws ParameterException, EntityNotFoundException, ProcessingException {
        try {
            if (null == request)
                throw new ParameterException("param request null");

            CkCtShellInvoice dto = this.whereDto(request);
            if (null == dto)
                throw new ProcessingException("dto from filter is null");

            request.setTotalRecords(super.countByAnd(dto));
            String selectClause = "FROM TCkCtShellInvoice o";

            String orderByClause = request.getOrderBy().toString();
            List<TCkCtShellInvoice> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
                    request.getDisplayLength(), request.getDisplayStart());

            return entities.stream().map((x) -> {
                try {
                    return this.dtoFromEntity(x);
                } catch (ParameterException | ProcessingException var3) {
                    LOG.error("filterBy", var3);
                }
                return null;
            }).collect(Collectors.toList());

        } catch (ParameterException | ProcessingException ex) {
            LOG.error("filterBy", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("filterBy", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    public void save(TCkCtShellInvoice entity) throws ParameterException, ProcessingException {
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
    public void update(TCkCtShellInvoice entity) throws ParameterException, ProcessingException {
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

    @Override
    public List<TCkCtShellInvoice> getAllInvoice() throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    public TCkCtShellInvoice find(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");
        TCkCtShellInvoice TCkCtShellInvoice =  null;
        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");
            TCkCtShellInvoice = dao.find(id);
        } catch (ParameterException | EntityNotFoundException ex) {
            LOG.error("findById", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("findById", ex);
            throw new ProcessingException(ex);
        }
        return TCkCtShellInvoice;
    }

    public List<TCkCtShellInvoice> getInvoiceStatistic(String accnId) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("getInvoiceStatistic");
        try {
            LocalDate today = LocalDate.now();
            LocalDate firstDayOfYear = LocalDate.of(today.getYear(), 1, 1);
            LocalDate firstDayOfCurrentMonth = today.withDayOfMonth(1);
            LocalDate lastDayOfPreviousMonth = firstDayOfCurrentMonth.minusDays(1);

            Date startDate = Date.from(firstDayOfYear.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(lastDayOfPreviousMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("accnId", accnId);
            parameters.put("startDate", startDate);
            parameters.put("endDate", endDate);
            parameters.put("invStatus", 'A');

            String hql = "FROM TCkCtShellInvoice o WHERE o.TCoreAccn.accnId = :accnId AND o.invPaymentDt BETWEEN :startDate AND :endDate AND o.invStatus = :invStatus";
            List<TCkCtShellInvoice> invoices = dao.getByQuery(hql, parameters);
            if (Objects.nonNull(invoices) && !invoices.isEmpty()){
                return invoices;
            }
        } catch (ParameterException | EntityNotFoundException ex) {
            LOG.error("getInvoiceStatistic", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("getInvoiceStatistic", ex);
            throw new ProcessingException(ex);
        }
        return null;
    }
}
