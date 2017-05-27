package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.model.AdminRole;


public interface AdminRoleRepository extends CrudRepository<AdminRole, Long>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<AdminRole> findByUserid(Long userid);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<AdminRole> findByRoleId(Long roleId);
	void deleteByUserid(Long userId);
}
