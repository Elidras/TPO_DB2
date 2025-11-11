package com.uade.tpo.views;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.entity.User;
import com.uade.tpo.mongoDB.MongoDBCRUD;

public class MenuTecnico {

    private final User usuario;
    private final Scanner scanner = new Scanner(System.in);
    private final MongoDBCRUD mongoCRUD;   // ✅ ya existía

    // ✅ Soporte HTTP para hablar con los endpoints Redis del backend
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl = "http://localhost:8080";

    // ✅ Guardamos último pedido reclamado para facilitar la entrega
    private String lastClaimedOrderId = null;
    private String lastClaimedOrderJson = null;

    public MenuTecnico(User usuario, MongoDBCRUD mongoCRUD) {
        this.usuario = usuario;
        this.mongoCRUD = mongoCRUD;
    }

    public void mostrarMenu() {
        boolean salir = false;

        while (!salir) {
            SpaceAdder.addSpace(9);
            System.out.println("\n===== MENÚ TECNICO =====");
            // —— funciones “antiguas” (se mantienen) ——
            System.out.println("1. Cambiar datos de mi cuenta");
            System.out.println("2. Solicitar mediciones de sensores");
            System.out.println("3. Consultar casilla de mensajes");
            System.out.println("4. Cambiar estado sensor");
            System.out.println("5. Cerrar sesión");
            // —— funciones Redis nuevas para técnico ——
            System.out.println("6. Reclamar siguiente pedido (cola Redis)");
            System.out.println("7. Entregar resultado de pedido reclamado");

            System.out.print("Seleccione una opción: ");
            SpaceAdder.addSpace(3);

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> cambiarDatosCuenta();          // (Mongo) se mantiene
                case "2" -> crearInformeMediciones();      // placeholder existente
                case "3" -> consultarCasillaMensajes();    // placeholder existente
                case "4" -> cambiarEstadoSensor();         // placeholder existente
                case "5" -> {
                    System.out.println("Cerrando sesión...");
                    salir = true;
                }
                case "6" -> reclamarSiguientePedido();     // (Redis) NUEVO
                case "7" -> entregarResultadoPedido();     // (Redis) NUEVO
                default -> System.out.println("Opción inválida, intente nuevamente.");
            }
        }
    }

    // =======================
    //  Antiguas (sin tocar)
    // =======================

    private void cambiarDatosCuenta() {
        System.out.println(">> Cambiando datos de la cuenta de " + usuario.getNombre());
        mongoCRUD.modificarAtributoUsuario(usuario);
    }

    private void crearInformeMediciones() {
        System.out.println(">> Abriendo plataforma de creacion de mediciones...");
        // Dejá la lógica previa de tu equipo aquí si aplica (Cassandra / etc.)
    }

    private void consultarCasillaMensajes(){
        System.out.println(">> Abriendo casilla de mensajes...");
        // Si más adelante queréis que el técnico vea algo propio, añadid aquí.
    }

    private void cambiarEstadoSensor(){
        System.out.println(">> Abriendo menú de cambio de estado de sensor...");
        // Mantener/ubicarse en la lógica que ya tuvieran
    }

    // =======================
    //  NUEVO: Redis / Pedidos
    // =======================

    /** POST /tech/claim con headers del técnico (X-User, X-Role=TECH).
     *  - Saca de la cola y marca IN_PROGRESS
     *  - Guarda el orderId reclamado para simplificar la entrega
     */
    private void reclamarSiguientePedido() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/tech/claim"))
                    .timeout(Duration.ofSeconds(10))
                    .header("X-User", usuario.getNombre() != null && !usuario.getNombre().isBlank() ? usuario.getNombre() : "tech01")
                    .header("X-Role", "TECH")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (res.statusCode() == 204) {
                System.out.println("⏳ No hay pedidos en cola ahora mismo.");
                return;
            }
            if (res.statusCode() == 403) {
                System.out.println("🚫 Debes ser TECH. (Falta X-Role=TECH)");
                return;
            }
            if (res.statusCode() >= 400) {
                System.out.println("❌ Error reclamando pedido: HTTP " + res.statusCode() + " -> " + res.body());
                return;
            }

            // El backend devuelve el JSON del pedido (IN_PROGRESS)
            String body = res.body();
            System.out.println("✅ Pedido reclamado:");
            System.out.println(body);

            // Guardamos el orderId para ayudar en la entrega
            var node = mapper.readTree(body);
            this.lastClaimedOrderJson = body;
            this.lastClaimedOrderId = node.hasNonNull("orderId") ? node.get("orderId").asText() : null;

            if (lastClaimedOrderId != null) {
                System.out.println("→ orderId guardado: " + lastClaimedOrderId);
            } else {
                System.out.println("⚠️ No se pudo extraer orderId del JSON devuelto.");
            }

        } catch (Exception e) {
            System.out.println("❌ Excepción reclamando pedido: " + e.getMessage());
        }
    }

    /** POST /tech/deliver
     *  Requiere: userId, orderId, title, content
     *  - Envía mensaje libre al inbox del usuario
     *  - Cambia estado de la orden a COMPLETED
     */
    private void entregarResultadoPedido() {
        try {
            // Si tenemos el último orderId reclamado, lo proponemos por defecto.
            String orderIdPorDefecto = (lastClaimedOrderId != null) ? lastClaimedOrderId : "";
            String orderId, userId, title, content;

            System.out.print("OrderId [" + orderIdPorDefecto + "]: ");
            orderId = scanner.nextLine().trim();
            if (orderId.isBlank()) orderId = orderIdPorDefecto;

            System.out.print("userId destinatario (ej: 6912f3d12f1878260fce5f51): ");
            userId = scanner.nextLine().trim();

            System.out.print("Título del informe: ");
            title = scanner.nextLine().trim();

            System.out.print("Contenido libre del técnico: ");
            content = scanner.nextLine().trim();

            if (orderId == null || orderId.isBlank()
                || userId == null || userId.isBlank()
                || title == null || title.isBlank()
                || content == null || content.isBlank()) {
                System.out.println("⚠️ Campos obligatorios: orderId, userId, title, content.");
                return;
            }

            var payload = new java.util.LinkedHashMap<String,Object>();
            payload.put("userId", userId);
            payload.put("orderId", orderId);
            payload.put("title", title);
            payload.put("content", content);

            String json = mapper.writeValueAsString(payload);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/tech/deliver"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("X-User", usuario.getNombre() != null && !usuario.getNombre().isBlank() ? usuario.getNombre() : "tech01")
                    .header("X-Role", "TECH")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (res.statusCode() >= 400) {
                System.out.println("❌ Error entregando resultado: HTTP " + res.statusCode() + " -> " + res.body());
                return;
            }

            System.out.println("✅ Entrega realizada. Respuesta:");
            System.out.println(res.body());

        } catch (Exception e) {
            System.out.println("❌ Excepción entregando resultado: " + e.getMessage());
        }
    }
}
