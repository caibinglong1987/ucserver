package com.roamtech.uc.repository;

import com.roamtech.uc.model.UserAssociate;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by roam-caochen on 2017/3/2.
 */
@Repository
public interface UserAssociateRepository extends CrudRepository<UserAssociate, Long> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    UserAssociate findByUserId(Long userId);

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    List<UserAssociate> findByParentUserId(Long parentUserId);
}
