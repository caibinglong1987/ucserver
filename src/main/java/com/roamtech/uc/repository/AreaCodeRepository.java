package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.model.AreaCode;
import com.roamtech.uc.model.City;


public interface AreaCodeRepository extends CrudRepository<AreaCode, Long>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<AreaCode> findByGroupid(Long pid);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	AreaCode findByNationalcode(String isocode);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	AreaCode findByAreaname(String name);
}
