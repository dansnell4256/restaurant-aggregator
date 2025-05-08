package com.catalogue.repository;

import com.catalogue.model.EntityDiagnostic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for diagnosing database connectivity
 */
@Repository
public interface RepositoryDiagnostic extends JpaRepository<EntityDiagnostic, Long> {
}
