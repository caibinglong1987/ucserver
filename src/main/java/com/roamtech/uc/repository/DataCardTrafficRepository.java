package com.roamtech.uc.repository;

import com.roamtech.uc.model.DataCardTraffic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;


public interface DataCardTrafficRepository extends CrudRepository<DataCardTraffic, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<DataCardTraffic> findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(Long userId,Date startTime,Date endTime);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<DataCardTraffic> findByUserId(Long userId);
	List<DataCardTraffic> findByIccidAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndOrderdetailidNot(String phone, Date endTime, Date startTime, Long orderdetailid);
	//@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<DataCardTraffic> findByIccidAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(String iccid,Date endTime,Date startTime);
	List<DataCardTraffic> findByImsiAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(String imsi,Date endTime,Date startTime);
	List<DataCardTraffic> findByEffectDatetimeLessThanAndFailureDatetimeGreaterThan(Date endTime,Date startTime);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<DataCardTraffic> findByEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndStatusAndPurchaseIdIsNull(Date endTime,Date startTime, Integer status);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<DataCardTraffic> findByFailureDatetimeGreaterThanAndDatacardidIsNull(Date startTime,Pageable pager);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<DataCardTraffic> findByFailureDatetimeGreaterThanAndDatacardidNotNull(Date startTime,Pageable pager);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<DataCardTraffic> findByFailureDatetimeGreaterThan(Date startTime,Pageable pager);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<DataCardTraffic> findByOrderdetailid(Long orderdetailid);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<DataCardTraffic> findByOrderid(Long id);

}
