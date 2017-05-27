package com.roamtech.uc.repository;

import com.roamtech.uc.model.User;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	User findByUserNameAndTenantIdAndPhoneType(String username, Long tenantId, Integer phoneType);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<User> findByUserName(String username);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	User findByPhoneAndTenantIdAndPhoneType(String phone, Long tenantId, Integer phoneType);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	User findByEmail(String email);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<User> findByPhoneLikeAndTenantId(String phoneLike, Long tenantId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	User findByPhoneAndTenantId(String phone, Long tenantId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<User> findByPhone(String phone);
}
