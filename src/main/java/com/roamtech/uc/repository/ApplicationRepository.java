package com.roamtech.uc.repository;

import com.roamtech.uc.model.Application;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;

/**
 * Created by roam-caochen on 2016/12/12.
 */
public interface ApplicationRepository extends CrudRepository<Application, Long> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    @Query("from Application a where (a.packageName = ?1 or a.bundleId = ?1) and a.secret = ?2")
    Application getApplication(String appId, String appKey);
 }
