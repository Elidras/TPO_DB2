package com.uade.tpo.repository;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;

import com.uade.tpo.entity.Medicion;

public interface MedicionRepository extends CassandraRepository<Medicion, UUID> {
    // Custom queries
}
