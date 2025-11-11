package com.uade.tpo.facturacion.service;

import com.uade.tpo.facturacion.model.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class SQLiteFacturacionService {

    private static final String DB_URL = "jdbc:sqlite:facturacion.db";
    private final StringRedisTemplate redisTemplate;

    public SQLiteFacturacionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        initializeDatabase();
    }

    /**
     * Conexión a SQLite
     */
    private Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            return conn;
        } catch (SQLException e) {
            System.err.println("Error al conectar con SQLite: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Inicializar base de datos
     */
    private void initializeDatabase() {
        System.out.println("Inicializando base de datos SQLite de facturacion...");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Tabla de facturas
            String createFacturasTable = """
                CREATE TABLE IF NOT EXISTS facturas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email_usuario TEXT NOT NULL,
                    fecha_emision TIMESTAMP NOT NULL,
                    fecha_vencimiento TIMESTAMP NOT NULL,
                    total REAL NOT NULL,
                    estado TEXT NOT NULL CHECK(estado IN ('PENDIENTE', 'PAGADA', 'VENCIDA', 'CANCELADA')),
                    descripcion TEXT,
                    horas_sesion REAL,
                    procesos_incluidos TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

            // Tabla de items de factura
            String createItemsTable = """
                CREATE TABLE IF NOT EXISTS items_factura (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    factura_id INTEGER NOT NULL,
                    concepto TEXT NOT NULL,
                    tipo_item TEXT NOT NULL,
                    cantidad INTEGER NOT NULL DEFAULT 1,
                    precio_unitario REAL NOT NULL,
                    subtotal REAL NOT NULL,
                    proceso_id TEXT,
                    FOREIGN KEY (factura_id) REFERENCES facturas (id) ON DELETE CASCADE
                )
                """;

            // Tabla de pagos
            String createPagosTable = """
                CREATE TABLE IF NOT EXISTS pagos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    factura_id INTEGER NOT NULL,
                    email_usuario TEXT NOT NULL,
                    fecha_pago TIMESTAMP NOT NULL,
                    monto_pagado REAL NOT NULL,
                    metodo_pago TEXT NOT NULL,
                    referencia TEXT,
                    FOREIGN KEY (factura_id) REFERENCES facturas (id)
                )
                """;

            // Ejecutar creaciones
            stmt.execute(createFacturasTable);
            stmt.execute(createItemsTable);
            stmt.execute(createPagosTable);

            System.out.println("Tablas de SQLite creadas/existen correctamente");

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos SQLite: " + e.getMessage());
            throw new RuntimeException("No se pudo inicializar la base de datos", e);
        }
    }

    /**
     * Crear factura basada en tiempo de sesion
     */
    public Factura crearFacturaDesdeSesion(String emailUsuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            //Calcular tiempo de sesion
            double tiempoTotalHoras = calcularTiempoSesionDesdeRedis(emailUsuario, fechaInicio, fechaFin);

            //Calcular costo
            double costoTotal = calcularCostoDesdeTiempoSesion(tiempoTotalHoras);

            // Insertar factura
            String sqlFactura = """
                INSERT INTO facturas (email_usuario, fecha_emision, fecha_vencimiento, total, estado, descripcion, horas_sesion)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

            LocalDateTime fechaEmision = LocalDateTime.now();
            LocalDateTime fechaVencimiento = fechaEmision.plusDays(30);
            String descripcion = String.format("Factura por uso de plataforma - %.2f horas de sesion", tiempoTotalHoras);

            int facturaId;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, emailUsuario);
                pstmt.setTimestamp(2, Timestamp.valueOf(fechaEmision));
                pstmt.setTimestamp(3, Timestamp.valueOf(fechaVencimiento));
                pstmt.setDouble(4, costoTotal);
                pstmt.setString(5, "PENDIENTE");
                pstmt.setString(6, descripcion);
                pstmt.setDouble(7, tiempoTotalHoras);

                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        facturaId = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la factura");
                    }
                }
            }

            //Insertar items de la factura
            crearItemFactura(conn, facturaId, "Uso de plataforma", "TIEMPO_SESION",
                    tiempoTotalHoras, costoTotal, null);

            conn.commit();

            System.out.println("Factura creada exitosamente - ID: " + facturaId + ", Usuario: " + emailUsuario + ", Total: $" + costoTotal);

            return obtenerFacturaPorId(facturaId);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error al hacer rollback: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Error al crear factura: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexion: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Calcular tiempo de sesion desde Redis
     */
    private double calcularTiempoSesionDesdeRedis(String emailUsuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        try {
            // Usar tu estructura existente de Redis para calcular tiempo de sesion
            // Asumiendo que tienes sesiones almacenadas en Redis
            String claveSesiones = "tpo:user:sessions:" + emailUsuario;

            // Si no hay datos en Redis, calcular basado en el rango de fechas
            if (Boolean.FALSE.equals(redisTemplate.hasKey(claveSesiones))) {
                // Fallback: calcular diferencia entre fechas
                long horas = java.time.Duration.between(fechaInicio, fechaFin).toHours();
                return Math.max(horas, 1.0); // Minimo 1 hora
            }

            // Aqui integraria con tu logica especifica de sesiones en Redis
            // Por ahora devuelvo un valor calculado del rango
            long horas = java.time.Duration.between(fechaInicio, fechaFin).toHours();
            return Math.max(horas, 1.0);

        } catch (Exception e) {
            System.err.println("Error al calcular tiempo de sesion desde Redis: " + e.getMessage());
            // Fallback seguro
            return 24.0; // 24 horas por defecto
        }
    }

    /**
     * Calcular costo basado en tiempo de sesion
     */
    public double calcularCostoDesdeTiempoSesion(double tiempoTotalHoras) {
        double tarifaBasePorHora = 5.0;

        if (tiempoTotalHoras <= 10) {
            return tiempoTotalHoras * tarifaBasePorHora;
        } else if (tiempoTotalHoras <= 50) {
            double horasBase = 10 * tarifaBasePorHora;
            double horasExtra = (tiempoTotalHoras - 10) * (tarifaBasePorHora * 0.8);
            return horasBase + horasExtra;
        } else {
            double horasBase = 10 * tarifaBasePorHora;
            double horasMedias = 40 * (tarifaBasePorHora * 0.8);
            double horasPreferenciales = (tiempoTotalHoras - 50) * (tarifaBasePorHora * 0.6);
            return horasBase + horasMedias + horasPreferenciales;
        }
    }

    /**
     * Crear factura por procesos ejecutados (usando tu catalogo de Redis)
     */
    public Factura crearFacturaPorProcesos(String emailUsuario, List<String> procesoIds) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            double costoTotal = 0.0;
            List<String> procesosIncluidos = new ArrayList<>();

            // Calcular costo total desde el catalogo de Redis
            for (String procesoId : procesoIds) {
                String claveProceso = "tpo:process:" + procesoId;
                Map<Object, Object> procesoData = redisTemplate.opsForHash().entries(claveProceso);

                if (!procesoData.isEmpty()) {
                    String nombre = (String) procesoData.get("name");
                    String costoStr = (String) procesoData.get("cost");
                    double costo = Double.parseDouble(costoStr);

                    costoTotal += costo;
                    procesosIncluidos.add(nombre);
                }
            }

            // Insertar factura
            String sqlFactura = """
                INSERT INTO facturas (email_usuario, fecha_emision, fecha_vencimiento, total, estado, descripcion, procesos_incluidos)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

            LocalDateTime fechaEmision = LocalDateTime.now();
            LocalDateTime fechaVencimiento = fechaEmision.plusDays(30);
            String descripcion = "Factura por ejecucion de procesos: " + String.join(", ", procesosIncluidos);

            int facturaId;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, emailUsuario);
                pstmt.setTimestamp(2, Timestamp.valueOf(fechaEmision));
                pstmt.setTimestamp(3, Timestamp.valueOf(fechaVencimiento));
                pstmt.setDouble(4, costoTotal);
                pstmt.setString(5, "PENDIENTE");
                pstmt.setString(6, descripcion);
                pstmt.setString(7, String.join("; ", procesosIncluidos));

                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        facturaId = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la factura");
                    }
                }
            }

            // Insertar items por cada proceso
            for (int i = 0; i < procesoIds.size(); i++) {
                String procesoId = procesoIds.get(i);
                String claveProceso = "tpo:process:" + procesoId;
                Map<Object, Object> procesoData = redisTemplate.opsForHash().entries(claveProceso);

                if (!procesoData.isEmpty()) {
                    String nombre = (String) procesoData.get("name");
                    String costoStr = (String) procesoData.get("cost");
                    double costo = Double.parseDouble(costoStr);

                    crearItemFactura(conn, facturaId, nombre, "PROCESO", 1, costo, procesoId);
                }
            }

            conn.commit();

            System.out.println("Factura por procesos creada - ID: " + facturaId + ", Usuario: " + emailUsuario + ", Total: $" + costoTotal);

            return obtenerFacturaPorId(facturaId);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error al hacer rollback: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Error al crear factura por procesos: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexion: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Obtener factura por ID
     */
    public Factura obtenerFacturaPorId(int id) {
        String sql = "SELECT * FROM facturas WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Factura factura = mapearFactura(rs);
                    factura.setItems(obtenerItemsFactura(conn, id));
                    return factura;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener factura por ID: " + e.getMessage(), e);
        }

        throw new RuntimeException("Factura no encontrada con ID: " + id);
    }

    /**
     * btener facturas por email
     */
    public List<Factura> obtenerFacturasPorEmail(String emailUsuario) {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM facturas WHERE email_usuario = ? ORDER BY fecha_emision DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, emailUsuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Factura factura = mapearFactura(rs);
                    factura.setItems(obtenerItemsFactura(conn, factura.getId()));
                    facturas.add(factura);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener facturas por email: " + e.getMessage(), e);
        }

        return facturas;
    }

    private void crearItemFactura(Connection conn, int facturaId, String concepto, String tipoItem,
                                  double cantidad, double precioUnitario, String procesoId) throws SQLException {
        String sql = """
            INSERT INTO items_factura (factura_id, concepto, tipo_item, cantidad, precio_unitario, subtotal, proceso_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facturaId);
            pstmt.setString(2, concepto);
            pstmt.setString(3, tipoItem);
            pstmt.setDouble(4, cantidad);
            pstmt.setDouble(5, precioUnitario);
            pstmt.setDouble(6, precioUnitario * cantidad);
            pstmt.setString(7, procesoId);

            pstmt.executeUpdate();
        }
    }

    private List<ItemFactura> obtenerItemsFactura(Connection conn, int facturaId) throws SQLException {
        List<ItemFactura> items = new ArrayList<>();
        String sql = "SELECT * FROM items_factura WHERE factura_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facturaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapearItemFactura(rs));
                }
            }
        }
        return items;
    }

    private Factura mapearFactura(ResultSet rs) throws SQLException {
        Factura factura = new Factura();
        factura.setId(rs.getInt("id"));
        factura.setEmailUsuario(rs.getString("email_usuario"));
        factura.setFechaEmision(rs.getTimestamp("fecha_emision").toLocalDateTime());
        factura.setFechaVencimiento(rs.getTimestamp("fecha_vencimiento").toLocalDateTime());
        factura.setTotal(rs.getDouble("total"));
        factura.setEstado(rs.getString("estado"));
        factura.setDescripcion(rs.getString("descripcion"));
        factura.setHorasSesion(rs.getDouble("horas_sesion"));
        factura.setProcesosIncluidos(rs.getString("procesos_incluidos"));
        factura.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return factura;
    }

    private ItemFactura mapearItemFactura(ResultSet rs) throws SQLException {
        ItemFactura item = new ItemFactura();
        item.setId(rs.getInt("id"));
        item.setFacturaId(rs.getInt("factura_id"));
        item.setConcepto(rs.getString("concepto"));
        item.setTipoItem(rs.getString("tipo_item"));
        item.setCantidad(rs.getInt("cantidad"));
        item.setPrecioUnitario(rs.getDouble("precio_unitario"));
        item.setSubtotal(rs.getDouble("subtotal"));
        item.setProcesoId(rs.getString("proceso_id"));
        return item;
    }
}