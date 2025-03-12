/**
 * 
 */
package com.guudint.clickargo.clictruck.planexec.job.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.vcc.camelone.common.dao.GenericDao;

/**
 * @author adenny
 *
 */
public interface CkJobTruckDao extends GenericDao<TCkJobTruck, String> {

    List<TCkJobTruck> findByIds(List<String> jobIds) throws Exception;

    List<TCkJobTruck> findByIdPrefixs(String jobId) throws Exception;
    
    List<TCkJobTruck> findByDrvMobileId(String drvMobileId) throws Exception;
    
    List<TCkJobTruck> findByDrvMobileId(String drvMobileId, List<String> states) throws Exception;
    
    List<TCkJobTruck> findOngoingJob() throws Exception;
    
    List<TCkJobTruck> findOngoingJobByDrvId(String drvId) throws Exception;

    List<TCkJobTruck> findByParentId(String parentId) throws Exception;
    
    List<TCkJobTruck> findByDrvIdAndJobStatus(String drvId, String ...status) throws Exception;
    
    List<TCkJobTruck> findByFinancerAndDocVerifyDate(List<String> jobFinanceOptList, String docVerifyDate) throws Exception;
    
    void updateUtilizeDate(List<String> jobTruckIdList) throws Exception;
    
    List<TCkJobTruck> findByStatusAndEpodIsNull(String jobStatus, int limit) throws Exception; 
    
    public List<TCkJobTruck> findByStatusAndEpodIsNull(String jobStatus, String dsvAS) throws Exception;

    List<TCkJobTruck> findByCoFf(String deptId, String accnId) throws Exception;
}
