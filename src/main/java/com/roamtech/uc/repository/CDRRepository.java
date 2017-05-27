package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.model.CallDetailRecord;



public interface CDRRepository extends CrudRepository<CallDetailRecord, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<CallDetailRecord> findByUserId(Long userId);
	@Query("select sum(cdr.duration) from CallDetailRecord cdr where cdr.userId = ?1 and cdr.direction = 1 and cdr.myroambox != 1")
	Long sumCallDurationByUserId(Long userId);
}
