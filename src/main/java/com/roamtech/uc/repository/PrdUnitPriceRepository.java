package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.roamtech.uc.model.PrdUnitPrice;


public interface PrdUnitPriceRepository extends CrudRepository<PrdUnitPrice, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<PrdUnitPrice> findByProductid(Long productId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	@Query(" from PrdUnitPrice pup where pup.productid=:prd_id and (failure_datetime is null or failure_datetime>:curr_datetime)")
	public List<PrdUnitPrice> findByfailureDatetime(@Param("prd_id")Long productId, @Param("curr_datetime")String currDatetime);	
}
