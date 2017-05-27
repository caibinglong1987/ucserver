package com.roamtech.uc.repository;

import com.roamtech.uc.model.UserAddress;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface UserAddressRepository extends CrudRepository<UserAddress, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<UserAddress> findByUserId(Long userId);
	
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<UserAddress> findByUserIdAndStatusNot(Long userId, int status);
	void deleteByUserId(Long userId);

}
