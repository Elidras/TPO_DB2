package com.uade.tpo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
  exclude = {
    org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration.class,
    // 👉 esta faltaba:
    org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration.class
  }
)
public class SensorMngrMain {
  public static void main(String[] args) {
    SpringApplication.run(SensorMngrMain.class, args);
  }
}
