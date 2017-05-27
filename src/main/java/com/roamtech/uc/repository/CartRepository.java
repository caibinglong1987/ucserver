package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;


import com.roamtech.uc.model.Cart;


public interface CartRepository extends CrudRepository<Cart, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Cart> findByUserid(Long userId);
	void deleteByUserid(Long userId);
//	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Cart> findBySessionid(String sessionId);	
	void deleteBySessionid(String sessionId);
}
