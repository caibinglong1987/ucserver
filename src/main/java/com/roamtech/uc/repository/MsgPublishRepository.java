package com.roamtech.uc.repository;

import com.roamtech.uc.model.MsgPublish;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.List;


public interface MsgPublishRepository extends CrudRepository<MsgPublish, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<MsgPublish> findByType(int type);
}
