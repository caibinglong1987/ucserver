package com.roamtech.uc.repository;

import com.roamtech.uc.model.BlackList;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by Baron Jobs on 2017/3/3.
 */
public interface BlackListRepository extends CrudRepository<BlackList, Long>{
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    List<BlackList> findByIdGreaterThanAndUserId(Long id, Long userId);
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    BlackList findByPhoneAndUserId(String phone, Long userId);
}
