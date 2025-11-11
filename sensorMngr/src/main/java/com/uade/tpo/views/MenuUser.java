package com.uade.tpo.views;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.cassandra.CassandraMedicionCRUD;
import com.uade.tpo.entity.User;
import com.uade.tpo.mongoDB.MongoDBCRUD;
import com.uade.tpo.query.RawQueryExecutor;

public class MenuUser {

    private final User usuario;
    private final Scanner scanner = new Scanner(System.in);
    private final MongoDBCRUD mongoCRUD;           // (Mongo) se mantiene
    private final CassandraMedicionCRUD cCRUD;     // (Cassandra) se mantiene

    // --- Helpers HTTP/JSON para llamar a los endpoints Redis existentes ---
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String BASE = "http://localhost:8080";  // si cambias el puerto, cámbialo aquí

    public MenuUser(User usuario, MongoDBCRUD mongoCRUD, CassandraMedicionCRUD cCRUD) {
        this.usuario = usuario;
        this.mongoCRUD = mongoCRUD;
        this.cCRUD = cCRUD;
    }

    public void mostrarMenu() {
        boolean salir = false;

        while (!salir) {
            SpaceAdder.addSpace(9);
            System.out.println("\n===== MENÚ USUARIO =====");
            System.out.println("1.  Cambiar datos de mi cuenta (Mongo)");
            System.out.println("2.  Solicitar mediciones de sensores (Cassandra)");
            System.out.println("3.  Consultar estado de deuda (Redis / sessions usage)");
            System.out.println("3.  Consultar casilla de mensajes (Redis / inbox)"); 
            System.out.println("5.  Hacer una búsqueda personalizada (Cassandra/Mongo)");
            System.out.println("6.  Cerrar sesión");
            System.out.println("7.  Ver ofertas (catálogo en Redis)");
            System.out.println("8.  Solicitar proceso (crear orden y encolar)");
            System.out.println("9.  Ver pedidos COMPLETADOS (historial)");
            System.out.println("10. Ver INBOX (resultados recibidos)");
            System.out.println("11. Iniciar sesión de consulta privada (mide duración y registra uso)");
            System.out.print("\nSeleccione una opción: ");
            SpaceAdder.addSpace(3);

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> cambiarDatosCuenta();          // Mongo
                case "2" -> solicitarMediciones();          // Cassandra
                case "3" -> consultarDeuda();               // Redis usage
                case "4" -> customQuery();                  // ya estaba (Cassandra/Mongo)
                case "5" -> consultarCasillaMensajes();     // Redis inbox
                case "6" -> { System.out.println("Cerrando sesión..."); salir = true; }
                case "7" -> verOfertas();                   // GET /ofertas
                case "8" -> solicitarProceso();             // POST /orders
                case "9" -> verPedidosCompletados();        // GET /users/{uid}/history
                case "10" -> verInbox();                    // GET /users/{uid}/inbox
                case "11" -> sesionConsultaPrivada();       // /sessions/start + stop
                default -> System.out.println("Opción inválida, intente nuevamente.");
            }
        }
    }

    private void cambiarDatosCuenta() {
        System.out.println(">> Cambiando datos de la cuenta de " + usuario.getNombre());
        mongoCRUD.modificarAtributoUsuario(usuario);
    }

    private void solicitarMediciones() {
        System.out.println(">> Solicitando mediciones de sensores (Cassandra)...");
        // Aquí dejas tu flujo Cassandra tal como lo usabas en el proyecto.
    }

    private void consultarDeuda() {
        try {
            String yyyyMM = DateTimeFormatter.ofPattern("yyyyMM")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now());

            String url = BASE + "/sessions/usage/" + usuario.getId() + "?yyyyMM=" + yyyyMM;
            String json = httpGet(url);

            // Respuesta esperada: { "userId": "...", "yyyyMM": "...", "usage_ms": 1234 }
            Map<String, Object> obj = mapper.readValue(json, new TypeReference<>() {});
            long ms = getAsLong(obj.get("usage_ms"));
            System.out.printf("➡ Deuda (base tiempo) %s: %,d ms (≈ %.2f min)\n",
                    yyyyMM, ms, ms / 1000.0 / 60.0);
        } catch (Exception e) {
            System.out.println("⚠ No se pudo consultar la deuda: " + e.getMessage());
        }
    }

    private void customQuery() {
        RawQueryExecutor query = new RawQueryExecutor(cCRUD, mongoCRUD);
        System.out.println("Usar find({}) (MongoDB - Sensores) o SELECT (Cassandra - Mediciones)");
        System.out.print("Escribe tu query: ");
        String input = scanner.nextLine();

        Object result = query.executeAuto(input);
        SpaceAdder.addSpace(3);
        System.out.println("✅ Resultado:");
        System.out.println(result);
    }

    private void consultarCasillaMensajes() {
        // Mantengo este método, pero ahora implementado con Redis (inbox)
        verInbox();
    }

    private void verOfertas() {
        try {
            String json = httpGet(BASE + "/ofertas");
            // Array de hashes: [{id,name,description,cost}, ...]
            List<Map<String, Object>> ofertas = mapper.readValue(json, new TypeReference<>() {});
            if (ofertas.isEmpty()) {
                System.out.println("No hay ofertas cargadas.");
                return;
            }
            System.out.println("\n== Ofertas disponibles ==");
            for (Map<String, Object> it : ofertas) {
                String id   = str(it.get("id"));
                String name = str(it.get("name"));
                String desc = str(it.get("description"));
                String cost = str(it.get("cost"));
                System.out.printf(" • %s | %s | %s | $%s\n", id, name, desc, cost);
            }
        } catch (Exception e) {
            System.out.println("⚠ No se pudo leer el catálogo: " + e.getMessage());
        }
    }

    /** 8) Solicitar proceso -> POST /orders con {userId, processId, params, notes} */
    private void solicitarProceso() {
        try {
            System.out.print("processId (ej: top-hot-3 | top-cold-3 | country-temp): ");
            String pid = scanner.nextLine().trim();

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("userId", usuario.getId());
            payload.put("processId", pid);

            // params dependiendo del proceso
            Map<String, Object> params = new LinkedHashMap<>();
            if ("country-temp".equalsIgnoreCase(pid)) {
                System.out.print("País para la consulta: ");
                String country = scanner.nextLine().trim();
                params.put("country", country);
            }
            payload.put("params", params);

            System.out.print("Notas (opcional): ");
            String notes = scanner.nextLine();
            if (notes != null && !notes.isBlank()) {
                payload.put("notes", notes);
            }

            String resp = httpPostJson(BASE + "/orders", payload);
            Map<String, Object> obj = mapper.readValue(resp, new TypeReference<>() {});
            System.out.println("✅ Orden creada: " + str(obj.get("orderId")));

        } catch (Exception e) {
            System.out.println("⚠ No se pudo crear la orden: " + e.getMessage());
        }
    }

    /** 9) Ver pedidos COMPLETADOS -> GET /users/{uid}/history (ya implementado en tu server) */
    private void verPedidosCompletados() {
        try {
            String json = httpGet(BASE + "/users/" + usuario.getId() + "/history?from=0&to=20");
            // Respuesta es una lista de JSON-strings; cada item es un JSON con status, orderId, processId, techId, executed_at
            List<String> raw = mapper.readValue(json, new TypeReference<>() {});
            if (raw.isEmpty()) {
                System.out.println("No hay pedidos completados todavía.");
                return;
            }
            System.out.println("\n== Pedidos completados ==");
            for (String item : raw) {
                Map<String, Object> it = mapper.readValue(item, new TypeReference<>() {});
                System.out.printf(" • %s | %s | %s | por %s\n",
                        str(it.get("orderId")),
                        str(it.get("processId")),
                        str(it.get("status")),
                        str(it.get("techId")));
            }
        } catch (Exception e) {
            System.out.println("⚠ No se pudo leer el historial: " + e.getMessage());
        }
    }

    /** 10) Ver INBOX -> GET /users/{uid}/inbox?from=0&to=20 */
    private void verInbox() {
        try {
            String json = httpGet(BASE + "/users/" + usuario.getId() + "/inbox?from=0&to=20");
            // Respuesta es una lista de JSON-strings; cada item es el mensaje serializado
            List<String> raw = mapper.readValue(json, new TypeReference<>() {});
            if (raw.isEmpty()) {
                System.out.println("Tu inbox está vacío.");
                return;
            }
            System.out.println("\n== Inbox ==");
            for (String item : raw) {
                Map<String, Object> it = mapper.readValue(item, new TypeReference<>() {});
                System.out.printf(" • [%s] %s (orderId=%s) - from=%s\n",
                        str(it.getOrDefault("type","msg")),
                        str(it.getOrDefault("title","(sin título)")),
                        str(it.getOrDefault("orderId","-")),
                        str(it.getOrDefault("from","system")));
            }
        } catch (Exception e) {
            System.out.println("⚠ No se pudo leer el inbox: " + e.getMessage());
        }
    }

    /** 11) Sesión de consulta privada: /sessions/start → ejecutar query → /sessions/{id}/stop → mostrar duración */
    private void sesionConsultaPrivada() {
        try {
            // 1) Iniciar sesión
            String startJson = httpPostForm(BASE + "/sessions/start?userId=" + usuario.getId());
            Map<String, Object> start = mapper.readValue(startJson, new TypeReference<>() {});
            String sid = str(start.get("sessionId"));
            System.out.println("➡ Sesión iniciada: " + sid);

            // 2) Ejecutar una query usando tu motor actual
            RawQueryExecutor query = new RawQueryExecutor(cCRUD, mongoCRUD);
            System.out.print("Escribe tu query para la sesión privada: ");
            String input = scanner.nextLine();
            Object result = query.executeAuto(input);
            System.out.println("Resultado:\n" + result);

            // 3) Parar la sesión
            String stopJson = httpPostForm(BASE + "/sessions/" + sid + "/stop");
            Map<String, Object> stop = mapper.readValue(stopJson, new TypeReference<>() {});
            long ms = getAsLong(stop.get("duration_ms"));
            System.out.printf("✅ Sesión cerrada. Duración: %,d ms (≈ %.2f s)\n", ms, ms/1000.0);

        } catch (Exception e) {
            System.out.println("⚠ Error con la sesión privada: " + e.getMessage());
        }
    }

    private String httpGet(String url) throws Exception {
        HttpRequest rq = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> rs = http.send(rq, HttpResponse.BodyHandlers.ofString());
        if (rs.statusCode() >= 200 && rs.statusCode() < 300) return rs.body();
        throw new RuntimeException("GET " + url + " -> " + rs.statusCode() + " | " + rs.body());
    }

    private String httpPostJson(String url, Object body) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest rq = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> rs = http.send(rq, HttpResponse.BodyHandlers.ofString());
        if (rs.statusCode() >= 200 && rs.statusCode() < 300) return rs.body();
        throw new RuntimeException("POST " + url + " -> " + rs.statusCode() + " | " + rs.body());
    }

    private String httpPostForm(String url) throws Exception {
        HttpRequest rq = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> rs = http.send(rq, HttpResponse.BodyHandlers.ofString());
        if (rs.statusCode() >= 200 && rs.statusCode() < 300) return rs.body();
        throw new RuntimeException("POST " + url + " -> " + rs.statusCode() + " | " + rs.body());
    }

    private static String str(Object o) { return o == null ? null : String.valueOf(o); }
    private static long getAsLong(Object o) {
        if (o == null) return 0L;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(o)); } catch (Exception e) { return 0L; }
    }
}
