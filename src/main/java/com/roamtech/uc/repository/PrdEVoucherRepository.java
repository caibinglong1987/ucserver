package com.roamtech.uc.repository;

import com.roamtech.uc.model.PrdEVoucher;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface PrdEVoucherRepository extends CrudRepository<PrdEVoucher, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<PrdEVoucher> findByEvoucherid(Long evoucherId);
}
