package com.roamtech.uc.repository.om;

import com.roamtech.uc.model.om.OrderDetailJoin;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Baron Jobs on 2017/2/10.
 */
public interface OMOrderDetailRepository extends PagingAndSortingRepository<OrderDetailJoin, Long>, JpaSpecificationExecutor<OrderDetailJoin> {
}
