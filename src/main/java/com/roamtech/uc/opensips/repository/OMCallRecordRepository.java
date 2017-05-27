package com.roamtech.uc.opensips.repository;

import com.roamtech.uc.opensips.model.om.OMAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by Baron Jobs on 2017/2/10.
 */
public interface OMCallRecordRepository extends JpaRepository<OMAcc, Long>, JpaSpecificationExecutor<OMAcc> {

}
