package com.roamtech.uc.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.model.TouchVoice;


public interface TouchVoiceRepository extends CrudRepository<TouchVoice, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<TouchVoice> findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndOrderdetailidNot(Long userId,Date endTime,Date startTime,Long orderdetailid);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<TouchVoice> findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(Long userId,Date endTime,Date startTime);
//	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<TouchVoice> findByPhoneAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndOrderdetailidNot(String phone,Date endTime,Date startTime,Long orderdetailid);
//	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<TouchVoice> findByPhoneAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(String phone,Date endTime,Date startTime);
//	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<TouchVoice> findByEffectDatetimeLessThanAndFailureDatetimeGreaterThan(Date endTime,Date startTime);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<TouchVoice> findByTouchdevidAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(Long devid,Date endTime,Date startTime);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<TouchVoice> findByUserIdOrderByFailureDatetimeDesc(Long userId,Pageable pager);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	TouchVoice findByOrderdetailid(Long orderdetailid);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<TouchVoice> findByUserIdAndFailureDatetimeGreaterThan(Long userId, Date today);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<TouchVoice> findByOrderid(Long id);
}
