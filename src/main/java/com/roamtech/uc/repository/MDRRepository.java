package com.roamtech.uc.repository;

import com.roamtech.uc.model.MsgDetailRecord;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.List;


public interface MDRRepository extends CrudRepository<MsgDetailRecord, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<MsgDetailRecord> findByUserId(Long userId);

}
