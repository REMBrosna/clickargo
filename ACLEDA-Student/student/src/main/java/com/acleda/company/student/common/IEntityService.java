package com.acleda.company.student.common;

import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.common.payload.EntityFilterRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IEntityService <E, K, D>{

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    D find(D var1) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    D findById(String var1) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    List<D> findAll() throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    List<D> findByAnd(D var1, int var2, int var3, String var4, String var5) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    int countByAnd(D var1) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    D add(D var1, TAppUser var2) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    Object addObj(Object var1, TAppUser var2) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    D update(D var1, TAppUser var2) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    D update(D var1, TAppUser var2, boolean var3) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    Object updateObj(Object var1, TAppUser var2) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    D updateStatus(D var1, TAppUser var2, char var3) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    Object updateObjStatus(Object var1, TAppUser var2, char var3) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    D delete(D var1, TAppUser var2) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    D deleteById(String var1, TAppUser var2) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    List<D> filterBy(EntityFilterRequest var1) throws Exception;

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    boolean isRecordExists(D var1) throws Exception;
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    D deleteObj(Object var1, TAppUser var2) throws Exception;

}
