package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.model.ShippingArea;

public interface ShippingAreaRepository extends CrudRepository<ShippingArea, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<ShippingArea> findByShippingId(Integer shippingId);
}
