package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.roamtech.uc.model.EVoucher;

@Repository
public interface EVoucherRepository extends CrudRepository<EVoucher, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<EVoucher> findByType(Integer type);
}
