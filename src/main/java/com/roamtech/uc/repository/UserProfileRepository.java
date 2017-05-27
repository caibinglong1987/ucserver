package com.roamtech.uc.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.roamtech.uc.model.UserProfile;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	UserProfile findByUserId(Long userId);	
}
