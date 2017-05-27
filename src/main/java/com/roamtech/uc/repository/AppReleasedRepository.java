package com.roamtech.uc.repository;

import com.roamtech.uc.model.AppReleased;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.List;


public interface AppReleasedRepository extends CrudRepository<AppReleased, String>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<AppReleased> findByTypeAndVersionGreaterThanAndStatusOrderByVersionDesc(Integer type,Integer version,Integer status);
	AppReleased findByTypeAndVersion(Integer type,Integer version);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	AppReleased findByTypeAndVersionName(Integer type, String versionName);
}
