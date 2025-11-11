package com.uade.tpo.query;

import java.util.List;

import org.bson.Document;

import com.uade.tpo.cassandra.CassandraMedicionCRUD;
import com.uade.tpo.mongoDB.MongoDBCRUD;

public class RawQueryExecutor {

    private final CassandraMedicionCRUD cassandraCRUD;
    private final MongoDBCRUD mongoCRUD;

    public RawQueryExecutor(CassandraMedicionCRUD cassandraCRUD, MongoDBCRUD mongoCRUD) {
        this.cassandraCRUD = cassandraCRUD;
        this.mongoCRUD = mongoCRUD;
    }

    // ---- CASSANDRA ----
    public Object executeCassandra(String cql) {
        return cassandraCRUD.rawQuery(cql);
    }

    // ---- MONGO ----
    public List<Document> executeMongo(String rawFind) {
        return mongoCRUD.rawFind(rawFind);
    }

    // ---- AUTO-DETECT ----
    public Object executeAuto(String rawInput) {
        String trimmed = rawInput.trim().toLowerCase();

        if (trimmed.startsWith("select")) {
            System.out.println("🔵 Cassandra auto-detected");
            return executeCassandra(rawInput);
        }

        if (trimmed.startsWith("find(")) {
            System.out.println("🟢 Mongo auto-detected");
            return executeMongo(rawInput);
        }

        throw new IllegalArgumentException("❌ Query inválida: " + rawInput);
    }
}
