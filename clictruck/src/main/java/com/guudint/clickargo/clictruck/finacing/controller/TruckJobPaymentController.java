package com.guudint.clickargo.clictruck.finacing.controller;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.finacing.service.IPaymentService;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService.CkPaymentTypes;
import com.guudint.clickargo.clictruck.finacing.service.impl.CkCtPaymentTxnService;
import com.guudint.clickargo.clictruck.finacing.service.impl.TruckPaymentService.PayoutRequest;
import com.guudint.clickargo.payment.dto.PaymentCallbackResponse;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.master.controller.PathNotFoundException;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/payment")
public class TruckJobPaymentController {

	private static final Logger LOG = Logger.getLogger(TruckJobPaymentController.class);

	@Autowired
	private IPaymentService paymentService;

	@Autowired
	private CkCtPaymentTxnService ctPaymentTxnService;

	/**
	 * Retrieves the DO details + BL + invoice details. This is to load the invoices
	 * associated to the selected jobs for payment.
	 */
	@RequestMapping(value = "/invoice/details", method = RequestMethod.POST)
	public ResponseEntity<Object> viewDoInvoiceDetails(@RequestBody String reqBody) {
		LOG.debug("viewDoInvoiceDetails");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(paymentService.getPaymentDetails(reqBody, false));
		} catch (PathNotFoundException ex) {
			LOG.error("viewDoInvoiceDetails", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (ValidationException ex) {
			LOG.error("createEntity", ex);
			serviceStatus.setData(reqBody);
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		}  catch (Exception ex) {
			LOG.error("viewDoInvoiceDetails", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * End point to execute payment to selected confirmed jobs. Accepts
	 * comma-separated lists of confirmed DO claim jobs.
	 */
	@RequestMapping(value = "/pay/in", method = RequestMethod.POST)
	public ResponseEntity<Object> payInJobs(@RequestBody String reqBody) {
		try {
			LOG.debug("reqBody " + reqBody);

			paymentService.executeInboundPay(reqBody);
			return ResponseEntity.ok().body("successful");
		} catch (ValidationException e) {
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setData(e.getMessage());
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception e) {
			LOG.error("payInJobs", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * End point to execute payment to selected confirmed jobs. Accepts
	 * comma-separated lists of confirmed DO claim jobs.
	 */
	@RequestMapping(value = "/pay/out", method = RequestMethod.POST)
	public ResponseEntity<Object> payOutJobs(@RequestBody String reqBody) {
		try {
			LOG.debug("reqBody " + reqBody);

			paymentService.executeOutboundPay(reqBody);
			return ResponseEntity.ok().body("successful");
		} catch (ValidationException e) {
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setData(e.getMessage());
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception e) {
			LOG.error("payInJobs", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * End point for payment transaction details.
	 */
	@RequestMapping(value = "/txn/{ptxId}", method = RequestMethod.GET)
	public ResponseEntity<Object> getPaymentTxnHistory(@PathVariable String ptxId) {
		try {
			return ResponseEntity.ok(ctPaymentTxnService.findById(ptxId));
		} catch (Exception e) {
			LOG.error("getPaymentTxnHistory", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * End point for payment transaction action such as reject, verify and approve.
	 */
	@RequestMapping(value = "/txn/{ptxId}", method = RequestMethod.PUT)
	public ResponseEntity<Object> payoutAction(@PathVariable String ptxId, @RequestBody PayoutRequest req) {
		try {
			paymentService.payoutAction(ptxId, req);
			return ResponseEntity.ok("");
		} catch (ValidationException e) {
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setData(e.getMessage());
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception e) {
			LOG.error("getPaymentTxnHistory", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/paid", method = RequestMethod.POST)
	public ResponseEntity<Object> paymentCallback(@RequestBody String reqBody) {
		LOG.debug("paymentCallback " + reqBody);
		try {
			PaymentCallbackResponse response = paymentService.executeCallBack(reqBody);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			LOG.error("PaymentCallback ", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.IEntityServiceController#deleteEntityById(java.lang.String,
	 *      java.lang.String)
	 */
	@RequestMapping(value = "/txn/{ckPaymentType}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Object> cancelPayment(@PathVariable String ckPaymentType, @PathVariable String id) {
		LOG.debug("cancelPayment " + ckPaymentType + " - " + id);
		try {

			if (!EnumUtils.isValidEnum(CkPaymentTypes.class, ckPaymentType))
				throw new ParameterException(ckPaymentType + " not a valid payment type");

			paymentService.executeCancelPay(id, CkPaymentTypes.valueOf(ckPaymentType));
			return ResponseEntity.ok("success");
		} catch (ValidationException e) {
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setData(e.getMessage());
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception e) {
			LOG.error("cancelPayment ", e);

			try {
				paymentService.executeRevertCancelPay(id, CkPaymentTypes.valueOf(ckPaymentType));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

}
