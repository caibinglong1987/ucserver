package com.roamtech.uc.repository;

import com.roamtech.uc.model.UserEVoucher;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface UserEVoucherRepository extends CrudRepository<UserEVoucher, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<UserEVoucher> findByUserid(Long userId);	
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<UserEVoucher> findByUseridAndUsedDatetimeIsNullAndOrderidIsNull(Long userId);	
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	UserEVoucher findBySn(Long sn);	
	void deleteByOrderid(Long orderid);
	void deleteByOrderidAndEvoucherid(Long orderid,Long evoucherid);
	List<UserEVoucher> findByOrderidAndEvoucherid(Long orderid,Long evoucherid);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<UserEVoucher> findByOrderid(Long orderid);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	List<UserEVoucher> findByUseridAndEvoucherid(Long userid, Long evoucherid);

}
