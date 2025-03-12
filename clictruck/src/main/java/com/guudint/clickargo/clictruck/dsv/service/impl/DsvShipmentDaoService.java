package com.guudint.clickargo.clictruck.dsv.service.impl;

import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;
import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class DsvShipmentDaoService {

	@Autowired
	private CkCtShipmentDao shipmentDao;

	////////////////////////////////////////////////

	public void updateStatusMsgPath(String shId, String statusMsgPath) throws Exception {

		TCkCtShipment shipment = shipmentDao.find(shId);

		shipment.setShStatusmessagePath(statusMsgPath);
		shipment.setShStatusmessageSize((int) (new File(statusMsgPath).length()));
		shipment.setShDtStatusmessageCreate(new Date());

		shipment.setShDtLupd(new Date());

		shipmentDao.saveOrUpdate(shipment);
	}

	public void updatePushStatusMsg2SFTP(String shId) throws Exception {

		TCkCtShipment shipment = shipmentDao.find(shId);
		shipment.setShDtStatusmessagePush2sftp(new Date());

		shipmentDao.saveOrUpdate(shipment);
	}

	public void updateStatusMsgRemark(String shId, String remark) throws Exception {

		TCkCtShipment shipment = shipmentDao.find(shId);
		shipment.setShStatusmessageRemark(remark);

		shipmentDao.saveOrUpdate(shipment);
	}

	////////////////////////////////////////////////

	public void updateEpodFilePath(String shId, String ePodPath) throws Exception {

		TCkCtShipment shipment = shipmentDao.find(shId);

		shipment.setShPodPath(ePodPath);
		shipment.setShDtPodCreate(new Date());

		shipment.setShDtLupd(new Date());

		shipmentDao.saveOrUpdate(shipment);
	}

	public void updateSendEpodEmailTime(String shId) throws Exception {

		TCkCtShipment shipment = shipmentDao.find(shId);

		shipment.setShDtPodEmailNotify(new Date());

		shipmentDao.saveOrUpdate(shipment);
	}

	public void updateEpodRemark(String shId, String remark) throws Exception {

		TCkCtShipment shipment = shipmentDao.find(shId);
		shipment.setShPodRemark(remark);

		shipmentDao.saveOrUpdate(shipment);
	}

	////////////////////////////////////////////////

	public void updatePhotoPath(String shId, String photoPath) throws Exception {

		TCkCtShipment shipment = shipmentDao.find(shId);

		shipment.setShPhotoPath(photoPath);
		shipment.setShDtPhotoCreate(new Date());

		shipment.setShDtLupd(new Date());

		shipmentDao.saveOrUpdate(shipment);
	}

	public void updateSendPhotoEmailTime(String shId) throws Exception {

		TCkCtShipment shipment = shipmentDao.find(shId);

		shipment.setShDtPhotoEmailNotify(new Date());

		shipmentDao.saveOrUpdate(shipment);
	}

	public void updatePhotoRemark(String shId, String remark) throws Exception {

		TCkCtShipment shipment = shipmentDao.find(shId);
		shipment.setShPhotoRemark(remark);

		shipmentDao.saveOrUpdate(shipment);
	}

}
