package com.guudint.clickargo.clicdo.do_.job.service;

import java.util.Map;

import com.vcc.camelone.common.COAbstractEntity;

/**
 * Interface for different attachments. New document attachments should
 * implement this one.
 */
public interface IJobAttachService<D extends COAbstractEntity<D>> {

	Map<String, Object> getParams(D dto) throws Exception;
}
