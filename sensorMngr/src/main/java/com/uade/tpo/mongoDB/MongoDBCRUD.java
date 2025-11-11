package com.uade.tpo.mongoDB;

import java.util.Optional;
import java.util.Scanner;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.uade.tpo.entity.User;

@Component
public class MongoDBCRUD {

    private static final String COLLECTION_NAME = "User";

    private final MongoDatabase mongoDatabase;
    private final Scanner scanner;

    public MongoDBCRUD(MongoDatabase mongoDatabase, Scanner scanner) {
        this.mongoDatabase = mongoDatabase;
        this.scanner = scanner;
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

        User user = new User(id, nombre, mailField, pass, edad, tipoUsuario);
        return Optional.of(user);
    }

    public void modificarAtributoUsuario(User user) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);

        System.out.println("Atributos disponibles para modificar:");
        System.out.println("1. nombre");
        System.out.println("2. mail");
        System.out.println("3. password");
        System.out.println("4. edad");
        System.out.println("5. tipoUsuario");

        // Re-check that user still exists in DB
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
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Debe ingresar un número.");
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

        System.out.print("Ingrese el nuevo valor para " + campo + ": ");
        String nuevoValor = scanner.nextLine();

        Document actualizacion;
        if (campo.equals("edad")) {
            try {
                int nuevaEdad = Integer.parseInt(nuevoValor);
                actualizacion = new Document("$set", new Document(campo, nuevaEdad));
                user.setEdad(nuevaEdad);
            } catch (NumberFormatException e) {
                System.out.println("Edad inválida. Debe ser un número entero.");
                return;
            }
        } else {
            actualizacion = new Document("$set", new Document(campo, nuevoValor));

            // update in-memory user
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

        Document filtro = new Document("mail", mail);

        Document userEliminado = collection.findOneAndDelete(filtro);

        if (userEliminado == null) {
            System.out.println("⚠️ No se encontró un usuario con ese mail.");
        } else {
            System.out.println("🗑️ Usuario con mail " + mail + " eliminado correctamente.");
        }
    }

    public void darDeAltaUsuario(User user) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);

        Document nuevoUser = new Document()
                .append("nombre", user.getNombre())
                .append("mail", user.getMail())
                .append("password", user.getPassword())
                .append("edad", user.getEdad())
                .append("tipoUsuario", user.getTipoUsuario());

        collection.insertOne(nuevoUser);

        System.out.println("✅ Usuario dado de alta correctamente en la base de datos.");
    }
}
