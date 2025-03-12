package com.guudint.clickargo.clictruck.planexec.job.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.common.service.ICkListingService;
import com.guudint.clickargo.controller.CustomSerializerProvider;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityFilterResponse;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityOrderBy.ORDERED;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.master.controller.PathNotFoundException;

import io.jsonwebtoken.lang.Collections;

public abstract class AbstractTruckJobListController implements ITruckJobListController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(AbstractTruckJobListController.class);

	protected ObjectMapper objectMapper = new ObjectMapper();

	protected HashMap<String, String> listServices;
	protected HashMap<String, String> listDtos;

	// Attributes
	//////////////
	@Autowired
	protected ApplicationContext applicationContext;

	@PostConstruct
	public void configureObjectMapper() {
		objectMapper.setSerializerProvider(new CustomSerializerProvider());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.IEntityServiceController#getEntitiesBy(java.lang.String,
	 *      java.util.Map)
	 */
	public ResponseEntity<Object> listEntitiesBy(@PathVariable String entity,
			@RequestParam Map<String, String> params) {

		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			Optional<Object> opEntity = this.getEntitiesByProxy(entity, params);
			return ResponseEntity.ok(opEntity.get());
		} catch (PathNotFoundException ex) {
			log.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			log.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Get the entities from the filter service
	 * 
	 * @param entity
	 * @return
	 */
	protected Optional<Object> getEntitiesByProxy(String entity, Map<String, String> params)
			throws ParameterException, PathNotFoundException, ProcessingException {
		log.debug("getEntitiesProxy");

		try {
			if (StringUtils.isEmpty(entity))
				throw new ParameterException("param entity null or empty");
			if (Collections.isEmpty(params))
				throw new ParameterException("param params null or empty");

			if (!listServices.containsKey(entity))
				throw new PathNotFoundException("entity not mapped: " + entity);

			EntityFilterRequest filterRequest = new EntityFilterRequest();
			// start and length parameter extraction
			filterRequest.setDisplayStart(
					params.containsKey("iDisplayStart") ? Integer.valueOf(params.get("iDisplayStart")).intValue() : -1);
			filterRequest.setDisplayLength(
					params.containsKey("iDisplayLength") ? Integer.valueOf(params.get("iDisplayLength")).intValue()
							: -1);
			// where parameters extraction
			ArrayList<EntityWhere> whereList = new ArrayList<>();
			List<String> searches = params.keySet().stream().filter(x -> x.contains("sSearch_"))
					.collect(Collectors.toList());
			for (int nIndex = 1; nIndex <= searches.size(); nIndex++) {
				String searchParam = params.get("sSearch_" + String.valueOf(nIndex));
				String valueParam = params.get("mDataProp_" + String.valueOf(nIndex));
				log.debug("searchParam: " + searchParam + " valueParam: " + valueParam);
				whereList.add(new EntityWhere(valueParam, searchParam));
			}
			filterRequest.setWhereList(whereList);
			// order by parameters extraction
			Optional<String> opSortAttribute = Optional.ofNullable(params.get("mDataProp_0"));
			Optional<String> opSortOrder = Optional.ofNullable(params.get("sSortDir_0"));
			if (opSortAttribute.isPresent() && opSortOrder.isPresent()) {
				EntityOrderBy orderBy = new EntityOrderBy();
				orderBy.setAttribute(opSortAttribute.get());
				orderBy.setOrdered(opSortOrder.get().equalsIgnoreCase("desc") ? ORDERED.DESC : ORDERED.ASC);
				filterRequest.setOrderBy(orderBy);
			}

			if (!filterRequest.isValid())
				throw new ProcessingException("Invalid request: " + filterRequest.toJson());

			Object bean = applicationContext.getBean(listServices.get(entity));
			ICkListingService<?, ?, ?> service = (ICkListingService<?, ?, ?>) bean;

			@SuppressWarnings("unchecked")
			List<Object> entities = (List<Object>) service.filterBy(filterRequest);
			EntityFilterResponse filterResponse = new EntityFilterResponse();
			filterResponse.setiTotalRecords(entities.size());
			filterResponse.setiTotalDisplayRecords(filterRequest.getTotalRecords());
			filterResponse.setAaData((ArrayList<Object>) entities);

			return Optional.of(filterResponse);
		} catch (ParameterException | PathNotFoundException | ProcessingException ex) {
			log.error("getEntitiesProxy", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("getEntitiesProxy", ex);
			throw new ProcessingException(ex);
		}
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public HashMap<String, String> getListServices() {
		return listServices;
	}

	public void setListServices(HashMap<String, String> listServices) {
		this.listServices = listServices;
	}

	public HashMap<String, String> getListDtos() {
		return listDtos;
	}

	public void setListDtos(HashMap<String, String> listDtos) {
		this.listDtos = listDtos;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
