package com.uade.tpo.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import com.uade.tpo.entity.Medicion;
import java.util.UUID;

public interface MedicionRepository extends CassandraRepository<Medicion, UUID> {
    // Custom queries
}
