package com.uade.tpo.mongoDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.uade.tpo.entity.User;

public class MongoDBCRUD {

    private static MongoDBCRUD instance;

    private final MongoDatabase mongoDatabase;
    private final Scanner scanner;

    private static final String COLLECTION_NAME = "User";

    private MongoDBCRUD(MongoDatabase mongoDatabase, Scanner scanner) {
        this.mongoDatabase = mongoDatabase;
        this.scanner = scanner;
    }

    public static synchronized MongoDBCRUD getInstance(MongoDatabase db, Scanner scan) {
        if (instance == null) {
            instance = new MongoDBCRUD(db, scan);
        }
        return instance;
    }

    public Optional<User> buscarUsuarioPorMailYPassword(String mail, String password) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);

        Document filtro = new Document("mail", mail).append("password", password);
        Document doc = collection.find(filtro).first();

        if (doc == null) return Optional.empty();

        String id = doc.containsKey("_id") ? ((ObjectId) doc.get("_id")).toHexString() : null;
        String nombre = doc.getString("nombre");
        String mailField = doc.getString("mail");
        String pass = doc.getString("password");
        Integer edad = doc.containsKey("edad") ? doc.getInteger("edad") : null;
        String tipoUsuario = doc.containsKey("tipoUsuario") ? doc.getString("tipoUsuario") : "user";

        return Optional.of(new User(id, nombre, mailField, pass, edad, tipoUsuario));
    }

    public void modificarAtributoUsuario(User user) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);

        System.out.println("Atributos disponibles para modificar:");
        System.out.println("1. nombre");
        System.out.println("2. mail");
        System.out.println("3. password");
        System.out.println("4. edad");
        System.out.println("5. tipoUsuario");

        Document filtro = new Document("_id", new ObjectId(user.getId()));
        Document userDoc = collection.find(filtro).first();

        if (userDoc == null) {
            System.out.println("No se encontró el usuario en la base de datos.");
            return;
        }

        System.out.print("Ingrese el número del atributo que desea modificar: ");
        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Entrada inválida.");
            return;
        }

        String campo = switch (opcion) {
            case 1 -> "nombre";
            case 2 -> "mail";
            case 3 -> "password";
            case 4 -> "edad";
            case 5 -> "tipoUsuario";
            default -> null;
        };

        if (campo == null) {
            System.out.println("Opción no válida.");
            return;
        }

        System.out.print("Ingrese el nuevo valor: ");
        String nuevoValor = scanner.nextLine();

        Document actualizacion;

        if (campo.equals("edad")) {
            try {
                int nuevaEdad = Integer.parseInt(nuevoValor);
                actualizacion = new Document("$set", new Document("edad", nuevaEdad));
                user.setEdad(nuevaEdad);
            } catch (Exception e) {
                System.out.println("Edad inválida.");
                return;
            }
        } else {
            actualizacion = new Document("$set", new Document(campo, nuevoValor));
            switch (campo) {
                case "nombre" -> user.setNombre(nuevoValor);
                case "mail" -> user.setMail(nuevoValor);
                case "password" -> user.setPassword(nuevoValor);
                case "tipoUsuario" -> user.setTipoUsuario(nuevoValor);
            }
        }

        collection.updateOne(filtro, actualizacion);
        System.out.println("✅ Usuario actualizado correctamente en la base de datos.");
    }

    public void darDeBajaUsuarioPorMail(String mail) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);
        Document result = collection.findOneAndDelete(new Document("mail", mail));

        if (result == null)
            System.out.println("⚠️ No se encontró ese usuario.");
        else
            System.out.println("🗑️ Usuario eliminado con éxito.");
    }

    public void darDeAltaUsuario(User user) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);

        Document doc = new Document()
                .append("nombre", user.getNombre())
                .append("mail", user.getMail())
                .append("password", user.getPassword())
                .append("edad", user.getEdad())
                .append("tipoUsuario", user.getTipoUsuario());

        collection.insertOne(doc);

        System.out.println("✅ Usuario dado de alta correctamente.");
    }
    public List<Document> rawFind(String rawInput) {
        try {
            String trimmed = rawInput.trim();

            if (!trimmed.toLowerCase().startsWith("find(") || !trimmed.endsWith(")")) {
                throw new IllegalArgumentException("Input must be in format find({...})");
            }

            String json = trimmed.substring(trimmed.indexOf('(') + 1, trimmed.lastIndexOf(')')).trim();
            System.out.println(json);
            Document filter = Document.parse(json);

            return mongoDatabase
                    .getCollection("sensores")  // fixed collection
                    .find(filter)
                    .into(new ArrayList<>());

        } catch (Exception e) {
            System.err.println("❌ Error executing rawFind: " + e.getMessage());
            throw e;
        }
    }

    public void cambiarEstadoSensor(){
        
    }
}
