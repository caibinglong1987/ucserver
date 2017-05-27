package com.roamtech.uc.repository;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.model.Payment;

import javax.persistence.QueryHint;
import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long>  {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    List<Payment> findByEnabledAndTerminalTypeOrderBySortAsc(Boolean enabled, Integer terminalType);
}
