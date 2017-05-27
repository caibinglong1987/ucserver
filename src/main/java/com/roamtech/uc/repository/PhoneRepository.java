package com.roamtech.uc.repository;

import com.roamtech.uc.model.Phone;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.List;

public interface PhoneRepository extends CrudRepository<Phone, Long>   {
//	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<Phone> findByUserId(Long userId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	Phone findByPhoneAndTenantIdAndPhoneType(String phone, Long tenantId, Integer phoneType);
	void deleteByUserId(Long userId);

}
