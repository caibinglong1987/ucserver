package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.model.Touch;

public interface TouchRepository extends CrudRepository<Touch, Long>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Touch> findByUserId(Long userId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	Touch findByDevid(String devId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Touch> findByDevtype(Integer devType);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Touch> findByDevtype(Integer devType, Pageable pager);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	Touch findByPhone(String username);
}

