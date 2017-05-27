package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.model.Company;

public interface CompanyRepository extends CrudRepository<Company, Long>  {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Company> findByComptype(Integer type);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	Company findByCode(String code);
}
