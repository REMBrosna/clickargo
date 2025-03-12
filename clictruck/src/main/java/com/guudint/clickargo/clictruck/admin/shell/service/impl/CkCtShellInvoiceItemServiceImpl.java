package com.guudint.clickargo.clictruck.admin.shell.service.impl;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.shell.dto.CkCtShellInvoice;
import com.guudint.clickargo.clictruck.admin.shell.dto.CkCtShellInvoiceItem;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellInvoiceItem;
import com.guudint.clickargo.clictruck.admin.shell.service.CkCtShellInvoiceItemService;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.common.service.impl.CkCtVehServiceImpl;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class CkCtShellInvoiceItemServiceImpl extends AbstractClickCargoEntityService<TCkCtShellInvoiceItem, String, CkCtShellInvoiceItem> implements ICkConstant, CkCtShellInvoiceItemService {

    private static final Logger LOG = Logger.getLogger(CkCtShellInvoiceItemServiceImpl.class);
    private static final String AUDIT = "SHELL_INVOICE_ITEM";
    private static final String TABLE = "T_CK_CT_SHELL_INVOICE_ITEM";
    private static final String PREFIX = "INV-I";

    public CkCtShellInvoiceItemServiceImpl() {
        super("ckCtShellInvoiceItemDao", AUDIT, TCkCtShellInvoiceItem.class.getName(), TABLE);
    }

    @Autowired
    @Qualifier("ckCtVehDao")
    protected GenericDao<TCkCtVeh, String> ckCtVehDao;

    @Override
    public void updateStatus(String id, char status) throws ParameterException, EntityNotFoundException, ProcessingException {
        if (id == null) {
            throw new ParameterException("id entity null");
        }

        try {
            TCkCtShellInvoiceItem entity = super.dao.find(id);
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
    public CkCtShellInvoiceItem newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    public TCkCtShellInvoiceItem initEnity(TCkCtShellInvoiceItem entity) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellInvoiceItem entityFromDTO(CkCtShellInvoiceItem dto) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellInvoiceItem dtoFromEntity(TCkCtShellInvoiceItem entity) throws ParameterException, ProcessingException {
        LOG.info("dtoFromEntity: dtoFromEntity");
        if (entity == null) {
            throw new ParameterException("param entity null");
        }
        CkCtShellInvoiceItem dto = new CkCtShellInvoiceItem();
        TCkCtVeh tCkCtVeh = new TCkCtVeh();
        if (Objects.nonNull(entity.getTCkCtVeh())){
            try {
                TCkCtVeh veh = ckCtVehDao.find(entity.getTCkCtVeh().getVhId());
                tCkCtVeh.setVhId(veh.getVhId());
                tCkCtVeh.setVhPlateNo(veh.getVhPlateNo());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        entity.setTCkCtVeh(tCkCtVeh);
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    protected String entityKeyFromDTO(CkCtShellInvoiceItem dto) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected TCkCtShellInvoiceItem updateEntity(ACTION action, TCkCtShellInvoiceItem entity, Principal principal, Date date) throws ParameterException, ProcessingException {
        Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
        switch (action) {
            case CREATE:
                entity.setItmId(CkUtil.generateId(PREFIX));
                entity.setItmUidCreate(opUserId.orElse(Constant.DEFAULT_USR));
                entity.setItmDtCreate(date);
                entity.setItmDtLupd(date);
                entity.setItmUidLupd("");
                entity.setItmStatus('U');
                break;

            case MODIFY:
                entity.setItmDtLupd(date);
                entity.setItmUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
                break;
            default:
                break;
        }
        return entity;
    }

    @Override
    protected TCkCtShellInvoiceItem updateEntityStatus(TCkCtShellInvoiceItem entity, char status) throws ParameterException, ProcessingException {
        LOG.debug("updateEntityStatus");

        try {
            if (null == entity)
                throw new ParameterException("entity param null");

            entity.setItmStatus(status);
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
    protected CkCtShellInvoiceItem preSaveUpdateDTO(TCkCtShellInvoiceItem entity, CkCtShellInvoiceItem dto) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected void preSaveValidation(CkCtShellInvoiceItem dto, Principal principal) throws ParameterException, ProcessingException {

    }

    @Override
    protected ServiceStatus preUpdateValidation(CkCtShellInvoiceItem dto, Principal principal) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected String getWhereClause(CkCtShellInvoiceItem dto, boolean wherePrinted) throws ParameterException, ProcessingException {
        try {
            if (null == dto)
                throw new ParameterException("param dto null");
            String EQUAL = " = :", CONTAIN = " like :";
            StringBuilder searchStatement = new StringBuilder();

            if (!StringUtils.isEmpty(dto.getItmId())) {
                searchStatement.append(getOperator(wherePrinted)).append("o.itmId LIKE :itmId");
                wherePrinted = true;
            }

            if (dto.getItmStatus() != null) {
                searchStatement.append(getOperator(wherePrinted)).append("o.itmStatus").append(CONTAIN).append("itmStatus");
                wherePrinted = true;
            }

            searchStatement.append(getOperator(wherePrinted)).append("o.TCkCtShellInvoice.invId").append(EQUAL).append("invId");

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
    protected HashMap<String, Object> getParameters(CkCtShellInvoiceItem dto) throws ParameterException, ProcessingException {
        try {
            if (null == dto)
                throw new ParameterException("param dto null");

            Principal principal = principalUtilService.getPrincipal();
            if (principal == null)
                throw new ProcessingException("principal null");

            CoreAccn accn = principal.getCoreAccn();

            HashMap<String, Object> parameters = new HashMap<String, Object>();

            if (!StringUtils.isEmpty(dto.getItmId())){
                parameters.put("itmId", "%" + dto.getItmId() + "%");
            }
            if (dto.getItmStatus() != null) {
                parameters.put("itmStatus", dto.getItmStatus());
            }
            parameters.put("invId", dto.getTCkCtShellInvoice().getInvId());

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
    protected CkCtShellInvoiceItem whereDto(EntityFilterRequest entityFilterRequest) throws ParameterException, ProcessingException {
        LOG.info("whereDto");
        if (entityFilterRequest == null) {
            throw new ParameterException("param filterRequest null");
        }

        try {
            CkCtShellInvoiceItem dto = new CkCtShellInvoiceItem();
            for (EntityWhere entityWhere : entityFilterRequest.getWhereList()) {
                Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
                if (!opValue.isPresent())
                    continue;

                if ("itmId".equalsIgnoreCase(entityWhere.getAttribute())) {
                    dto.setItmId(opValue.get());
                }

                if ("itmStatus".equalsIgnoreCase(entityWhere.getAttribute())) {
                    dto.setItmStatus(opValue.get().charAt(0));
                }

                if ("invId".equalsIgnoreCase(entityWhere.getAttribute())) {
                    CkCtShellInvoice ckCtShellInvoice = new CkCtShellInvoice();
                    ckCtShellInvoice.setInvId(opValue.get());
                    dto.setTCkCtShellInvoice(ckCtShellInvoice);
                }
            }
            return dto;
        } catch (Exception ex) {
            LOG.error("whereDto", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    protected CoreMstLocale getCoreMstLocale(CkCtShellInvoiceItem dto) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtShellInvoiceItem setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtShellInvoiceItem dto) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    public CkCtShellInvoiceItem findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");

        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");

            TCkCtShellInvoiceItem entity = dao.find(id);
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
    public CkCtShellInvoiceItem deleteById(String s, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        return null;
    }

    @Override
    public List<CkCtShellInvoiceItem> filterBy(EntityFilterRequest request) throws ParameterException, EntityNotFoundException, ProcessingException {
        try {
            if (null == request)
                throw new ParameterException("param request null");

            CkCtShellInvoiceItem dto = this.whereDto(request);
            if (null == dto)
                throw new ProcessingException("dto from filter is null");

            request.setTotalRecords(super.countByAnd(dto));
            String selectClause = "FROM TCkCtShellInvoiceItem o";

            String orderByClause = request.getOrderBy().toString();
            List<TCkCtShellInvoiceItem> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
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
    public void save(TCkCtShellInvoiceItem entity) throws ParameterException, ProcessingException {
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
    public void update(TCkCtShellInvoiceItem entity) throws ParameterException, ProcessingException {
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

    public TCkCtShellInvoiceItem find(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("findById");
        TCkCtShellInvoiceItem TCkCtShellInvoiceItem =  null;
        try {
            if (StringUtils.isEmpty(id))
                throw new ParameterException("param id null or empty");
            TCkCtShellInvoiceItem = dao.find(id);
        } catch (ParameterException | EntityNotFoundException ex) {
            LOG.error("findById", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("findById", ex);
            throw new ProcessingException(ex);
        }
        return TCkCtShellInvoiceItem;
    }

    public BigDecimal getTotalAmountByInvId(String invId) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.info("getTotalAmountByInvId");
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("status", 'A');
            parameters.put("invId", invId);
            String hql = "SELECT SUM(o.itmTotal) FROM TCkCtShellInvoiceItem o WHERE o.TCkCtShellInvoice.invId = :invId AND o.itmStatus = :status";
            List<TCkCtShellInvoiceItem> result = dao.getByQuery(hql, parameters);

            if (Objects.nonNull(result) && !result.isEmpty() && result.get(0) != null) {
                return new BigDecimal(String.valueOf(result.get(0)));
            }
        } catch (ParameterException | EntityNotFoundException ex) {
            LOG.error("getTotalAmountByInvId", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("getTotalAmountByInvId", ex);
            throw new ProcessingException("Error while processing shell transactions", ex.getMessage());
        }
        return BigDecimal.ZERO;
    }
}
