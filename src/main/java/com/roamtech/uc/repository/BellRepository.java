package com.roamtech.uc.repository;

import com.roamtech.uc.model.Bell;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.Date;


public interface BellRepository extends CrudRepository<Bell, Long> {
    @Query(nativeQuery = true,  value = "select * from `ringback` where starttime <= ?1 and endtime >= ?1 and tenantid = ?2 order by id desc limit 1")
    Bell findByTimeAndTenantid(Date now, Long tenantid);
}
