package com.roamtech.uc.repository;

import com.roamtech.uc.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;
import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long>   {
//	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
//	@Query(nativeQuery = true, value = "SELECT * FROM `prd_order` a where a.userid = :userid and a.id < :orderid order by a.createtime desc limit :size") 
//	List<Order> findByUseridAndIdLessThanOrderByCreatetimeDesc(@Param("userid")Long userId,@Param("orderid")Long orderId,@Param("size")Integer limit);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<Order> findByUseridAndOrderStatusNotAndOrderStatusNotOrderByCreatetimeDesc(Long userId,Integer initStatus,Integer closeStatus1,Pageable pager);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Order> findByUseridAndOrderStatusOrderByCreatetimeDesc(Long userId, Integer orderStatus,Pageable pager);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Order> findByUseridAndPayStatusOrderByCreatetimeDesc(Long userId, Integer payStatus,Pageable pager);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Order> findByPayStatusAndShippingStatusAndShippingIdNotNull(Integer payStatus,Integer shippingStatus,Pageable pager);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Order> findByPayStatusAndShippingStatusAndObtainvoucher(Integer payStatus,Integer shippingStatus,Boolean obtainvoucher,Pageable pager);
}
