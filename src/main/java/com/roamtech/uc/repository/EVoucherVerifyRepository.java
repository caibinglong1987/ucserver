package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.roamtech.uc.model.EVoucherVerify;

@Repository
public interface EVoucherVerifyRepository extends CrudRepository<EVoucherVerify, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<EVoucherVerify> findByUserid(Long userId);	
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<EVoucherVerify> findByActionUserid(Long actionUserid);		
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<EVoucherVerify> findBySn(Long sn);	
}
