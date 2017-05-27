package com.roamtech.uc.repository;

import com.roamtech.uc.model.DataCardArea;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.List;

public interface DataCardAreaRepository extends CrudRepository<DataCardArea, Long>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<DataCardArea> findByDatacardType(Integer datacardType);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	DataCardArea findByAreaname(String areaname);
}

