package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.model.Homepage;

public interface HomepageRepository extends CrudRepository<Homepage, Long> {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<Homepage> findByTypeAndLocationAndDisabledNotOrderBySortAsc(Integer type, String location, Boolean disabled);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<Homepage> findByTypeAndDisabledNotOrderBySortAsc(Integer type, Boolean disabled);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Homepage> findByTypeAndLocationOrderBySortAsc(Integer type, String location);
}
