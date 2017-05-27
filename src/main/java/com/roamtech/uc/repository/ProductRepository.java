package com.roamtech.uc.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.roamtech.uc.model.Product;


public interface ProductRepository extends CrudRepository<Product, Long>   {
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Product> findByStoreid(Long storeId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Product> findByBrandid(Long brandId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Product> findByCategoryid(Long categoryId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Product> findByStoreidAndCategoryid(Long storeId, Long categoryId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Product> findByStoreidAndBrandid(Long storeId, Long brandId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Product> findByCategoryidAndBrandid(Long categoryId, Long brandId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	List<Product> findByStoreidAndBrandidAndCategoryid(Long storeId, Long brandId, Long categoryId);
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") }) 
	@Query(" from Product prd where prd.name like '%:prd_name%' or prd.subname like '%:prd_name%'")
	List<Product> findByName(@Param("prd_name")String name);
}
