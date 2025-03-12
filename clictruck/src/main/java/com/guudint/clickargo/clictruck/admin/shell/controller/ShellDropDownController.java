package com.guudint.clickargo.clictruck.admin.shell.controller;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellInvoice;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellCardTruckServiceImpl;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellInvoiceServiceImpl;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.dto.Response;
import com.guudint.clickargo.master.dao.CoreAccnDao;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/clickargo/shell")
public class ShellDropDownController {

    @Autowired
    private CoreAccnDao coreAccnDao;

    @Autowired
    @Qualifier("ckCtVehDao")
    protected GenericDao<TCkCtVeh, String> ckCtVehDao;

    @Autowired
    @Qualifier("ckCtShellCardDao")
    protected GenericDao<TCkCtShellCard, String> ckCtShellCardDao;

    @Autowired
    private CkCtShellCardTruckServiceImpl ckCtShellCardTruckService;

    @Autowired
    private CkCtShellInvoiceServiceImpl ckCtShellInvoiceService;

    @GetMapping("/accn")
    public ResponseEntity<?> listAll(){
        Response response = new Response();
        try {
            Map<String, Object> parameters = new HashMap<>();
//            parameters.put("cCode", "SG");
            parameters.put("accType", AccountTypes.ACC_TYPE_TO.name());
            String hql = "FROM TCoreAccn o WHERE o.TMstAccnType.atypId = :accType AND o.accnStatus='A'";
            List<TCoreAccn> entity = coreAccnDao.getByQuery(hql, parameters);
            List<Object> arr = new ArrayList<>();
            if(entity.size() == 0){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setError("Get Account Not Found");
                response.getData().addAll(arr);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            for (TCoreAccn item: entity) {
                Map<String, String> map = new HashMap<>();
                map.put("accnId", item.getAccnId());
                map.put("accnName", item.getAccnName());
                arr.add(map);
            }
            response.setStatus(HttpStatus.OK.value());
            response.setSuccess("Success");
            response.getData().addAll(arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/truck/{comId}")
    public ResponseEntity<?> allTrucks(@PathVariable String comId){
        Response response = new Response();
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("comId", comId);
            String hql = "FROM TCkCtVeh o WHERE o.TCoreAccn.accnId = :comId AND o.vhStatus='A'";
            List<String> allAssignedTrucks = this.ckCtShellCardTruckService.getAllAssignedTrucks();
            if (allAssignedTrucks != null && !allAssignedTrucks.isEmpty()){
                parameters.put("vhIds", allAssignedTrucks);
                hql = "FROM TCkCtVeh o WHERE o.vhId NOT IN (:vhIds) AND o.TCoreAccn.accnId = :comId AND o.vhStatus='A'";
            }
            List<TCkCtVeh> entity = ckCtVehDao.getByQuery(hql, parameters);
            List<Object> arr = new ArrayList<>();
            if(entity.size() == 0){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setError("Get Account Not Found");
                response.getData().addAll(arr);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            for (TCkCtVeh item: entity) {
                Map<String, String> map = new HashMap<>();
                map.put("vehId", item.getVhId());
                map.put("vehType", item.getTCkCtMstVehType().getVhtyName());
                map.put("plateNo", item.getVhPlateNo());
                arr.add(map);
            }
            response.setStatus(HttpStatus.OK.value());
            response.setSuccess("Success");
            response.getData().addAll(arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/shellCard")
    public ResponseEntity<?> allShellCards(){
        Response response = new Response();
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("scStatus", 'A');
            parameters.put("currentDate", new Date());
            String hql = "FROM TCkCtShellCard o WHERE o.scStatus = :scStatus AND o.scDtExpiry > :currentDate";

            List<String> allAssignedCards = this.ckCtShellCardTruckService.getAllAssignedCards();
            if (allAssignedCards != null && !allAssignedCards.isEmpty()) {
                parameters.put("cardIds", allAssignedCards);
                hql = "FROM TCkCtShellCard o WHERE o.scId NOT IN (:cardIds) AND o.scStatus = :scStatus AND o.scDtExpiry > :currentDate";
            }
            List<TCkCtShellCard> entity = ckCtShellCardDao.getByQuery(hql, parameters);
            if(entity.size() == 0){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setError("Get Account Not Found");
                response.getData().addAll(new ArrayList<>());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            response.setStatus(HttpStatus.OK.value());
            response.setSuccess("Success");
            response.getData().addAll(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/invoicing/statistic/{accnId}")
    public ResponseEntity<?> invoiceStatistic(@PathVariable String accnId){
        Response response = new Response();
        try {
            List<TCkCtShellInvoice> entity = ckCtShellInvoiceService.getInvoiceStatistic(accnId);
            List<Object> arr = new ArrayList<>();
            if(entity == null){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setError("Not Found");
                response.getData().addAll(new ArrayList<>());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            for (TCkCtShellInvoice item: entity) {
                Map<String, String> map = new HashMap<>();
                map.put("totalAmount", item.getInvAmt().toString());
                map.put("paymentDate", item.getInvPaymentDt().toString());
                arr.add(map);
            }
            response.setStatus(HttpStatus.OK.value());
            response.setSuccess("Success");
            response.getData().addAll(arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(response);
    }
}