package com.roamtech.uc.opensips.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.opensips.model.MissedCalls;

public interface MissedCallsRepository extends CrudRepository<MissedCalls, String> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<MissedCalls> findByCaller(String caller);
}
