package com.guudint.clickargo.clictruck.admin.service;

import java.util.List;

import com.guudint.clickargo.clictruck.admin.dto.Parties;

public interface ClicTruckAccnService {
    
    List<Parties> getParties(Parties parties) throws Exception;
    
}
