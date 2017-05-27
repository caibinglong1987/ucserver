package com.roamtech.uc.repository.om;

import com.roamtech.uc.model.DataCardTraffic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by Baron Jobs on 2017/3/3.
 */
public interface OMDataCardTrafficRepository extends JpaRepository<DataCardTraffic, Long>, JpaSpecificationExecutor<DataCardTraffic> {
}
