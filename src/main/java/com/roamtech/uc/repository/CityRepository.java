package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.model.City;


public interface CityRepository extends CrudRepository<City, Long>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<City> findByType(Integer type);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<City> findByPid(Long pid);
}
