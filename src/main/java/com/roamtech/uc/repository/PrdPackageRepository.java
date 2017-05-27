package com.roamtech.uc.repository;


import com.roamtech.uc.model.PrdPackage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PrdPackageRepository extends CrudRepository<PrdPackage, Long>  {
    List<PrdPackage> findByPackageid(Long packageid);
}
