package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.roamtech.uc.model.Store;


public interface StoreRepository extends CrudRepository<Store, Long>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	Store findByUserid(Long userId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	@Query(" from Store sto where sto.name like '%:sto_name%' or sto.intro like '%:sto_name%'")
	List<Store> findByName(@Param("sto_name")String name);
}
