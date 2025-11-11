package com.uade.tpo.views;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.entity.User;
import com.uade.tpo.mongoDB.MongoDBCRUD;

public class MenuAdmin {

    private final User usuario;
    private final Scanner scanner = new Scanner(System.in);
    private final MongoDBCRUD mongoCRUD;   // ✅ Instancia del CRUD

    // ✅ HTTP para hablar con el backend Redis
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl = "http://localhost:8080";

    public MenuAdmin(User usuario, MongoDBCRUD mongoCRUD) {
        this.usuario = usuario;
        this.mongoCRUD = mongoCRUD;
    }

    public void mostrarMenu() {
        boolean salir = false;

        while (!salir) {
            SpaceAdder.addSpace(9);
            System.out.println("\n===== MENÚ ADMINISTRADOR =====");
            // —— opciones existentes (se mantienen) ——
            System.out.println("1. Cambiar datos de mi cuenta");
            System.out.println("2. Agregar o dar de baja cuentas de usuarios");
            System.out.println("3. Cerrar sesión");
            // —— funciones Redis para administración ——
            System.out.println("4. Ver catálogo de ofertas (/ofertas)");
            System.out.println("5. Ver inbox de un usuario");
            System.out.println("6. Ver historial de un usuario");
            System.out.println("7. Ver uso de sesiones de un usuario (yyyyMM actual)");
            System.out.println("8. Iniciar sesión de consulta privada para un usuario");
            System.out.println("9. Cerrar sesión de consulta privada (por sessionId)");

            System.out.print("Seleccione una opción: ");
            SpaceAdder.addSpace(3);

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> cambiarDatosCuenta();     // (Mongo) existente
                case "2" -> gestionarUsuarios();      // (Mongo) existente
                case "3" -> {
                    System.out.println("Cerrando sesión...");
                    salir = true;
                }
                case "4" -> verCatalogoOfertas();     // (Redis) NUEVO
                case "5" -> verInboxUsuario();        // (Redis) NUEVO
                case "6" -> verHistorialUsuario();    // (Redis) NUEVO
                case "7" -> verUsoSesionesUsuario();  // (Redis) NUEVO
                case "8" -> iniciarSesionPrivada();   // (Redis) NUEVO
                case "9" -> cerrarSesionPrivada();    // (Redis) NUEVO
                default -> System.out.println("Opción inválida, intente nuevamente.");
            }
        }
    }

    // ======================
    //  Antiguas (sin tocar)
    // ======================
    private void cambiarDatosCuenta() {
        System.out.println(">> Cambiando datos de la cuenta de " + usuario.getNombre());
        mongoCRUD.modificarAtributoUsuario(usuario);
    }

    private void gestionarUsuarios() {
        System.out.println(">> Gestión de usuarios (alta/baja)...");
        ViewAltaBaja view = new ViewAltaBaja(mongoCRUD);
        view.mostrarMenu();
    }

    // ============================
    //  NUEVO: utilidades con Redis
    // ============================
    private void verCatalogoOfertas() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/ofertas"))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() >= 400) {
                System.out.println("❌ Error consultando /ofertas: HTTP " + res.statusCode());
                System.out.println(res.body());
                return;
            }
            System.out.println("✅ Ofertas disponibles:");
            // formateo simple
            JsonNode arr = mapper.readTree(res.body());
            if (arr.isArray()) {
                for (JsonNode it : arr) {
                    String id   = it.path("id").asText();
                    String name = it.path("name").asText();
                    String desc = it.path("description").asText();
                    String cost = it.path("cost").asText();
                    System.out.println("- " + id + " | " + name + " | $" + cost + " | " + desc);
                }
            } else {
                System.out.println(res.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Excepción en verCatalogoOfertas: " + e.getMessage());
        }
    }

    private void verInboxUsuario() {
        try {
            System.out.print("userId (ej: 6912f3d12f1878260fce5f51): ");
            String uid = scanner.nextLine().trim();
            if (uid.isBlank()) { System.out.println("⚠️ userId obligatorio."); return; }

            System.out.print("from (default 0): ");
            String from = scanner.nextLine().trim();
            if (from.isBlank()) from = "0";

            System.out.print("to (default 10): ");
            String to = scanner.nextLine().trim();
            if (to.isBlank()) to = "10";

            String url = baseUrl + "/users/" + uid + "/inbox?from=" + from + "&to=" + to;

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() >= 400) {
                System.out.println("❌ Error consultando inbox: HTTP " + res.statusCode());
                System.out.println(res.body());
                return;
            }
            System.out.println("✅ Inbox de " + uid + ":");
            System.out.println(res.body());
        } catch (Exception e) {
            System.out.println("❌ Excepción en verInboxUsuario: " + e.getMessage());
        }
    }

    private void verHistorialUsuario() {
        try {
            System.out.print("userId (ej: u123): ");
            String uid = scanner.nextLine().trim();
            if (uid.isBlank()) { System.out.println("⚠️ userId obligatorio."); return; }

            System.out.print("from (default 0): ");
            String from = scanner.nextLine().trim();
            if (from.isBlank()) from = "0";

            System.out.print("to (default 10): ");
            String to = scanner.nextLine().trim();
            if (to.isBlank()) to = "10";

            String url = baseUrl + "/users/" + uid + "/history?from=" + from + "&to=" + to;

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() >= 400) {
                System.out.println("❌ Error consultando historial: HTTP " + res.statusCode());
                System.out.println(res.body());
                return;
            }
            System.out.println("✅ Historial de " + uid + ":");
            System.out.println(res.body());
        } catch (Exception e) {
            System.out.println("❌ Excepción en verHistorialUsuario: " + e.getMessage());
        }
    }

    private void verUsoSesionesUsuario() {
        try {
            System.out.print("userId (ej: u123): ");
            String uid = scanner.nextLine().trim();
            if (uid.isBlank()) { System.out.println("⚠️ userId obligatorio."); return; }

            // yyyyMM simple (del sistema local)
            String yyyyMM = new java.text.SimpleDateFormat("yyyyMM").format(new java.util.Date());
            String url = baseUrl + "/sessions/usage/" + uid + "?yyyyMM=" + yyyyMM;

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() >= 400) {
                System.out.println("❌ Error consultando uso de sesiones: HTTP " + res.statusCode());
                System.out.println(res.body());
                return;
            }
            System.out.println("✅ Uso de sesiones (" + yyyyMM + ") de " + uid + ":");
            System.out.println(res.body());
        } catch (Exception e) {
            System.out.println("❌ Excepción en verUsoSesionesUsuario: " + e.getMessage());
        }
    }

    private void iniciarSesionPrivada() {
        try {
            System.out.print("userId (ej: 6912f3d12f1878260fce5f51): ");
            String uid = scanner.nextLine().trim();
            if (uid.isBlank()) { System.out.println("⚠️ userId obligatorio."); return; }

            String url = baseUrl + "/sessions/start?userId=" + uid;

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() >= 400) {
                System.out.println("❌ Error iniciando sesión: HTTP " + res.statusCode());
                System.out.println(res.body());
                return;
            }
            System.out.println("✅ Sesión iniciada:");
            System.out.println(res.body());
        } catch (Exception e) {
            System.out.println("❌ Excepción en iniciarSesionPrivada: " + e.getMessage());
        }
    }

    private void cerrarSesionPrivada() {
        try {
            System.out.print("sessionId (ej: SID-xxxx): ");
            String sid = scanner.nextLine().trim();
            if (sid.isBlank()) { System.out.println("⚠️ sessionId obligatorio."); return; }

            String url = baseUrl + "/sessions/" + sid + "/stop";

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() >= 400) {
                System.out.println("❌ Error cerrando sesión: HTTP " + res.statusCode());
                System.out.println(res.body());
                return;
            }
            System.out.println("✅ Sesión cerrada:");
            System.out.println(res.body());
        } catch (Exception e) {
            System.out.println("❌ Excepción en cerrarSesionPrivada: " + e.getMessage());
        }
    }
}
