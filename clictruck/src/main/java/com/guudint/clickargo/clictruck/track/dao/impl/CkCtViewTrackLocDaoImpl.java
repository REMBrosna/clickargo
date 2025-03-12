package com.guudint.clickargo.clictruck.track.dao.impl;

import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.track.dao.CkCtViewTrackLocDao;
import com.guudint.clickargo.clictruck.track.model.VCkCtTrackLoc;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Service("ckCtViewTrackLocDao")
public class CkCtViewTrackLocDaoImpl extends GenericDaoImpl<VCkCtTrackLoc, String>
		implements CkCtViewTrackLocDao {

}
