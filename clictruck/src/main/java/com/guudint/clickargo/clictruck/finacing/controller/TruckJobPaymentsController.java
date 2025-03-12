package com.guudint.clickargo.clictruck.finacing.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.finacing.service.impl.TruckPaymentService;
import com.guudint.clickargo.clictruck.planexec.job.dto.MultiRecordRequest;
import com.guudint.clickargo.clictruck.planexec.job.dto.MultiRecordResponse;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/payments")
public class TruckJobPaymentsController {

    @Autowired
    private TruckPaymentService truckPaymentService;

    @GetMapping("/action")
    public ResponseEntity<?> getActions(@RequestParam String accnType, @RequestParam String role,
            @RequestParam String paymentId) {
        List<String> actions = truckPaymentService.getActions(accnType, role, paymentId);
        Map<String, Object> response = new HashMap<>();
        response.put("actions", actions);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> multiRecord(@RequestBody MultiRecordRequest request) {
        MultiRecordResponse response = truckPaymentService.multiRecord(request);
        return ResponseEntity.ok().body(response);
    }
}
