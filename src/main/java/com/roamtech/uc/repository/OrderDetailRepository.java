package com.roamtech.uc.repository;

import com.roamtech.uc.model.OrderDetail;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

public interface OrderDetailRepository extends CrudRepository<OrderDetail, Long>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<OrderDetail> findByOrderid(Long orderId);
	void deleteByOrderid(Long orderId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<OrderDetail> findByUserId(Long userId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<OrderDetail> findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndPhoneIsNull(Long userId, Date endTime,Date startTime);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<OrderDetail> findByUserIdAndStatusAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndPhoneIsNull(Long userId, Integer status, Date endTime,Date startTime);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<OrderDetail> findByUserIdAndStatusAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(Long userId, Integer status, Date endTime,Date startTime);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<OrderDetail> findByUserIdAndOrderidNotAndStatusAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndPhoneIsNull(Long userId, Long orderId, Integer status, Date endTime,Date startTime);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<OrderDetail> findByUserIdAndFailureDatetimeGreaterThanAndPhoneIsNull(Long userId, Date today);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<OrderDetail> findByUserIdAndStatusAndFailureDatetimeGreaterThanAndPhoneIsNull(Long userId, Integer status, Date today);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<OrderDetail> findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndCallDurationNotNull(
			Long userId, Date today, Date today2);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<OrderDetail> findByUserIdAndStatusAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndCallDurationNotNull(
			Long userId, Integer status, Date today, Date today2);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	OrderDetail findByUserIdAndStatusAndCallDurationAndOrderid(Long userId, Integer status, Integer callDuration, Long orderid);

}
