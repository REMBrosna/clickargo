package com.guudint.clickargo.clictruck.admin.config.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtToPaymentDao;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToPayment;
import com.guudint.clickargo.clictruck.util.ObjectUtil;
import com.guudint.clickargo.clictruck.va.dto.VirtualAccount;
import com.guudint.clickargo.external.dto.CreateFundTransferRequest;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.payment.dao.CkPaymentTxnDao;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreAccnConfig;
import com.vcc.camelone.ccm.dto.CoreAccnConfigId;
import com.vcc.camelone.ccm.model.TCoreAccnConfig;
import com.vcc.camelone.ccm.model.TCoreAccnConfigId;
import com.vcc.camelone.ccm.service.impl.AccnConfigService;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.master.dto.MstAccnType;

public class CoreAccnConfigServiceImpl extends AccnConfigService {

    @Autowired
    private CkPaymentTxnDao ckPaymentTxnDao;
    @Autowired
    private CkCtToPaymentDao ckCtToPaymentDao;
    
    private static final String BANK_DETAIL = "BANK_DETAIL";

    private static Logger LOG = Logger.getLogger(CoreAccnConfigServiceImpl.class);

    public CoreAccnConfigServiceImpl() {
        super();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoreAccnConfig update(CoreAccnConfig coreAccnConfig, Principal principal)
            throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        LOG.info("update");
        if (coreAccnConfig == null) {
            throw new ParameterException("param dto null");
        }
        if (principal == null) {
            throw new ParameterException("param principal null");
        }
        try {
            List<TCkPaymentTxn> tCkPaymentTxns = ckPaymentTxnDao.findByPtxPayee(coreAccnConfig.getId().getAcfgAccnid());
            String[] bankDetail = coreAccnConfig.getAcfgVal().split(":");
            for (TCkPaymentTxn tCkPaymentTxn : tCkPaymentTxns) {
                if (bankDetail.length > 1) {
                    tCkPaymentTxn.setPtxMerchantBank(bankDetail[0]);
                    tCkPaymentTxn.setPtxPayeeBankAccn(bankDetail[1]);
                    ckPaymentTxnDao.update(tCkPaymentTxn);
                }
            }
            List<TCkCtToPayment> tCkCtToPayments = ckCtToPaymentDao
                    .findByAccnTo(coreAccnConfig.getId().getAcfgAccnid());
            for (TCkCtToPayment tCkCtToPayment : tCkCtToPayments) {
                if (StringUtils.isNotBlank(tCkCtToPayment.getTopJson())) {
                    CreateFundTransferRequest fundTransferRequest = ObjectUtil
                            .stringToObject(tCkCtToPayment.toJson(), CreateFundTransferRequest.class);
                    if (fundTransferRequest != null) {
                        fundTransferRequest.setBank(bankDetail[0]);
                        fundTransferRequest.setBeneficiaryAccount(bankDetail[1]);
                        tCkCtToPayment.setTopJson(fundTransferRequest.toJson());
                        ckCtToPaymentDao.update(tCkCtToPayment);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return super.update(coreAccnConfig, principal);
    }

    @Override
    public CoreAccnConfig deleteObj(Object obj, Principal principal)
            throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        CoreAccnConfig coreAccnConfig = (CoreAccnConfig) obj;
        try {
            List<TCkPaymentTxn> tCkPaymentTxns = ckPaymentTxnDao.findByPtxPayee(coreAccnConfig.getId().getAcfgAccnid());
            if (tCkPaymentTxns.isEmpty()) {
                return super.delete(coreAccnConfig, principal);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected HashMap<String, Object> getParameters(CoreAccnConfig coreAccnConfig)
            throws ParameterException, ProcessingException {
        if (coreAccnConfig == null) {
            throw new ParameterException("param dto null");
        }
        HashMap<String, Object> params = super.getParameters(coreAccnConfig);
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
        CoreAccn coreAccn = Optional.ofNullable(coreAccnConfig.getTCoreAccn()).orElse(new CoreAccn());
        
        if (null != coreAccnConfig.getId()) {
			if (coreAccnConfig.getId().getAcfgKey() != null) {
				params.put("acfgKey", coreAccnConfig.getId().getAcfgKey());
			}
			if (coreAccnConfig.getId().getAcfgAccnid() != null) {
				params.put("acfgAccnid", "%" + coreAccnConfig.getId().getAcfgAccnid() + "%");
			}
		}
        
        if (StringUtils.isNotBlank(coreAccnConfig.getTCoreAccn().getAccnName())) {
        	params.put("accnName", "%" + coreAccnConfig.getTCoreAccn().getAccnName() + "%");
		}
        
        if (StringUtils.isNotBlank(coreAccnConfig.getTCoreAccn().getAccnId())) {
        	params.put("accnId", "%" + coreAccnConfig.getTCoreAccn().getAccnId() + "%");
		}
        
        if (null != coreAccn.getTMstAccnType()) {
            if (coreAccn.getTMstAccnType().getAtypDescription() != null) {
            	params.put("atypDescription", "%" + coreAccn.getTMstAccnType().getAtypDescription() + "%");
            }
        }
        
        if (StringUtils.isNotBlank(coreAccnConfig.getAcfgVal())) {
        	params.put("acfgVal", "%" + coreAccnConfig.getAcfgVal() + "%");
		}
        
        
        if (null != coreAccnConfig.getAcfgDtCreate()) {
        	params.put("acfgDtCreate", sdf.format(coreAccnConfig.getAcfgDtCreate()));
		}

		if (null != coreAccnConfig.getAcfgDtLupd()) {
			params.put("acfgDtLupd", sdf.format(coreAccnConfig.getAcfgDtLupd()));
		}
	
        return params;
    }

    @Override
    protected String getWhereClause(CoreAccnConfig coreAccnConfig, boolean wherePrinted)
            throws ParameterException, ProcessingException {
    	String EQUAL = " = :", CONTAIN = " like :";
        StringBuffer searchStatement = new StringBuffer(super.getWhereClause(coreAccnConfig, wherePrinted));
        CoreAccn coreAccn = Optional.ofNullable(coreAccnConfig.getTCoreAccn()).orElse(new CoreAccn());
        wherePrinted = searchStatement.toString().contains("WHERE");
       
        if (null != coreAccnConfig.getId()) {
            if (StringUtils.isNotBlank(coreAccnConfig.getId().getAcfgKey())) {
                searchStatement.append(getOperator(wherePrinted) + "o.id.acfgKey = :acfgKey");
                wherePrinted = true;
            }
            if (StringUtils.isNotBlank(coreAccnConfig.getId().getAcfgAccnid())) {
            	searchStatement.append(getOperator(wherePrinted) + "o.id.acfgAccnid" + CONTAIN + "acfgAccnid");
                wherePrinted = true;
            }
        }
        
        if (StringUtils.isNotBlank(coreAccnConfig.getTCoreAccn().getAccnName())) {
        	searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccn.accnName" + CONTAIN + "accnName");
			wherePrinted = true;
		}
        
        if (StringUtils.isNotBlank(coreAccnConfig.getTCoreAccn().getAccnId())) {
        	searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccn.accnId" + CONTAIN + "accnId");
			wherePrinted = true;
		}
        
        if (coreAccn.getTMstAccnType() != null) {
            if (StringUtils.isNotBlank(coreAccn.getTMstAccnType().getAtypDescription())) {
                searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccn.TMstAccnType.atypDescription" + CONTAIN
                		+ "atypDescription");
                wherePrinted = true;
            }
        }
        
		if (StringUtils.isNotBlank(coreAccnConfig.getAcfgVal())) {
			searchStatement.append(getOperator(wherePrinted) + "o.acfgVal" + CONTAIN + "acfgVal");
			wherePrinted = true;
		}
		
		if (null != coreAccnConfig.getAcfgDtCreate()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.acfgDtCreate" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "acfgDtCreate");
			wherePrinted = true;
		}

		if (null != coreAccnConfig.getAcfgDtLupd()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.acfgDtLupd" + ",'" + DateFormat.MySql.D_M_Y
					+ "')" + EQUAL + "acfgDtLupd");
			wherePrinted = true;
		}
        
        return searchStatement.toString();
    }

    @Override
    protected CoreAccnConfig whereDto(EntityFilterRequest filterRequest)
            throws ParameterException, ProcessingException {
        try {
			if (filterRequest == null) {
				throw new ParameterException("param filterRequest null");
			}
			SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
			VirtualAccount dto = new VirtualAccount();

			CoreAccn coreAccn = new CoreAccn();
			dto.setTCoreAccn(coreAccn);

			MstAccnType mstAccnType = new MstAccnType();
			
			TCoreAccnConfigId tCoreAccnConfigId = new TCoreAccnConfigId();
			dto.setId(new CoreAccnConfigId(tCoreAccnConfigId));
			dto.getId().setAcfgKey(BANK_DETAIL);
	
			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;
				String attribute = "o." + entityWhere.getAttribute();
				
				if (attribute.equalsIgnoreCase("o.TCoreAccn.accnId"))
					coreAccn.setAccnId(opValue.get());
				else if (attribute.equalsIgnoreCase("o.TCoreAccn.accnName"))
					coreAccn.setAccnName(opValue.get());
				else if (attribute.equalsIgnoreCase("o.TcoreAccn.TMstAccnType.atypDescription")) {
					if(null!=opValue.get()) {
						mstAccnType.setAtypDescription(opValue.get());
					}else {
						mstAccnType.setAtypId(AccountTypes.ACC_TYPE_TO.getDesc());
					}
					coreAccn.setTMstAccnType(mstAccnType);
				}
				else if (attribute.equalsIgnoreCase("o.id.acfgAccnid"))
					dto.getId().setAcfgAccnid(opValue.get());
				else if (attribute.equalsIgnoreCase("o.acfgVal"))
					dto.setAcfgVal(opValue.get());
				else if (attribute.equalsIgnoreCase("o.acfgDtCreate"))
					dto.setAcfgDtCreate(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.acfgDtLupd"))
					dto.setAcfgDtLupd(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.acfgStatus"))
					dto.setAcfgStatus((opValue.get() == null) ? null : opValue.get().charAt(0));
				else if (attribute.equalsIgnoreCase("o.history"))
					dto.setHistory(opValue.get());
			}
			return dto;
        } catch (ParameterException ex) {
			LOG.error("whereDto", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("whereDto", ex);
			throw new ProcessingException(ex);
		}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public List<CoreAccnConfig> filterBy(EntityFilterRequest filterRequest)
            throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.debug("filterBy");
        try {
            if (null == filterRequest) {
                throw new ParameterException("param filterRequest null");
            } else {
                CoreAccnConfig dto = this.whereDto(filterRequest);
                if (null == dto) {
                    throw new ProcessingException("whereDto null");
                } else {
                    List<CoreAccnConfig> coreAccnConfigs = new ArrayList<>();
                    filterRequest.setTotalRecords(super.countByAnd(dto));
                    String selectClause = "from TCoreAccnConfig o ";
                    String orderByClause = this.formatOrderByObj(filterRequest.getOrderBy()).toString();
                    List<TCoreAccnConfig> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
                            filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
                    for (TCoreAccnConfig tCoreAccnConfig : entities) {
                        CoreAccnConfig coreAccnConfig = dtoFromEntity(tCoreAccnConfig);
                        coreAccnConfigs.add(coreAccnConfig);
                    }
                    return coreAccnConfigs;
                }
            }
        } catch (ProcessingException | ParameterException var7) {
            LOG.error("filterBy", var7);
            throw var7;
        } catch (Exception var8) {
            LOG.error("filterBy", var8);
            throw new ProcessingException(var8);
        }
    }
    
    /**
	 * 
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	protected EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) throws Exception {

		if (orderBy == null)
			return null;

		if (StringUtils.isEmpty(orderBy.getAttribute()))
			return null;

		String newAttr = formatOrderBy(orderBy.getAttribute());
		if (StringUtils.isEmpty(newAttr))
			return orderBy;

		orderBy.setAttribute(newAttr);
		return orderBy;

	}
	
	/**
	 * 
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	protected String formatOrderBy(String attribute) throws Exception {

		String newAttr = attribute;
		if (StringUtils.contains(newAttr, "tcoreAccn"))
			newAttr = newAttr.replace("tcoreAccn", "TCoreAccn");

		return newAttr;
	}
	
}
