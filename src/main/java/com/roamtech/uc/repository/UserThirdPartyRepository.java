package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.roamtech.uc.model.UserThirdParty;

@Repository
public interface UserThirdPartyRepository extends CrudRepository<UserThirdParty, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<UserThirdParty> findByUserId(Long userId);
	void deleteByUserId(Long userId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	UserThirdParty findByUserIdAndCompanyid(Long userid, Long companyid);	
}
