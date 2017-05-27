package com.roamtech.uc.repository;

import com.roamtech.uc.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by Baron Jobs on 2017/2/9.
 */
public interface OMOrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

}
