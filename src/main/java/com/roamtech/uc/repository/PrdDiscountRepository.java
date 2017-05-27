package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.roamtech.uc.model.PrdDiscount;


public interface PrdDiscountRepository extends CrudRepository<PrdDiscount, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<PrdDiscount> findByProductid(Long productId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	@Query(" from PrdDiscount pup where pup.productid=:prd_id and (failure_datetime is null or failure_datetime>:curr_datetime)")
	public List<PrdDiscount> findByfailureDatetime(@Param("prd_id")Long productId, @Param("curr_datetime")String currDatetime);	
}
