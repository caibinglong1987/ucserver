package com.roamtech.uc.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.roamtech.uc.model.DataCard;
import com.roamtech.uc.model.Touch;

public interface DataCardRepository extends CrudRepository<DataCard, Long>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<DataCard> findByUserId(Long userId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	DataCard findByIccid(String iccid);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<DataCard> findByImsi(String iccid);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	//@Query(" from DataCard where (createtime>:start_datetime and createtime<:end_datetime)")
	Long countByCreatetimeGreaterThan(Date startDatetime);//,@Param("end_datetime")String endDatetime);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	@Query("from DataCard a where a.id > ?1 and a.iccid != null and a.imsi != null and a.userIdentifier != null")
	List<DataCard> findByIdGreaterThanAndUserIdentifierNotNull(long id, Pageable pageable);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	@Query("from DataCard a where a.id > ?1 and a.iccid != null and a.imsi != null")
	List<DataCard> findByIdGreaterThan(long id, Pageable pageable);
	List<DataCard> findByDistributor(Long distributor);
}

