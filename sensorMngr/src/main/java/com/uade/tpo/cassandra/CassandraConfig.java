package com.uade.tpo.cassandra;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.oss.driver.api.core.CqlSession;

@Configuration
public class CassandraConfig {

    @Value("${spring.cassandra.contact-points:localhost}")
    private String contactPoint;

    @Value("${spring.cassandra.port:9042}")
    private int port;

    @Value("${spring.cassandra.local-datacenter:datacenter1}")
    private String datacenter;

    @Value("${spring.cassandra.keyspace-name:mediciones}")
    private String keyspace;

    @Bean
    public CqlSession cqlSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoint, port))
                .withLocalDatacenter(datacenter)
                .withKeyspace(keyspace)
                .build();
    }


    @Bean
    public CassandraMedicionCRUD cassandraMedicionCRUD(CqlSession session) {
        return CassandraMedicionCRUD.getInstance(session);
    }
}
