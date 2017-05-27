package com.roamtech.uc.repository;

import com.roamtech.uc.model.Server;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by roam-caochen on 2016/12/28.
 */
public interface ServerRepository extends CrudRepository<Server, Long> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    List<Server> findByServiceCodeAndGroup(String serviceCode, Integer group);
}
