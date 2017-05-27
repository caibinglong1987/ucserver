package com.roamtech.uc.repository;

import com.roamtech.uc.model.Refund;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.List;

public interface RefundRepository extends CrudRepository<Refund, Long>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Refund> findByUserid(Long userId);

    List<Refund> findByUseridAndOrderid(Long userid, Long orderId);

	List<Refund> findByUseridAndOrderDetailId(Long userid, Long orderDetailId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<Refund> findByRealAndStatus(Boolean real,Integer status,Pageable pager);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<Refund> findByStatus(Integer status,Pageable pager);

}
