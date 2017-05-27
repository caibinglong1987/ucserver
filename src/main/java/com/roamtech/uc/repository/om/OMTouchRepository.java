package com.roamtech.uc.repository.om;

import com.roamtech.uc.model.Touch;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Baron Jobs on 2017/2/27.
 */
public interface OMTouchRepository extends PagingAndSortingRepository<Touch, Long>, JpaSpecificationExecutor<Touch> {
}
