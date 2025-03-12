package com.guudint.clickargo.clictruck.master.service;

import java.util.List;

public interface MasterService<T> {

    List<T> listAll() throws Exception;

    List<T> listByStatus(Character status);
}
