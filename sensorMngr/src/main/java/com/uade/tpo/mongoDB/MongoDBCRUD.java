package com.uade.tpo.mongoDB;

import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.uade.tpo.entity.User;

@Component
public class MongoDBCRUD {

    private final MongoDatabase mongoDatabase;

    public MongoDBCRUD(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public Optional<User> buscarUsuarioPorMailYPassword(String mail, String password) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("User");
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

    public void darDeBajaUsuarioPorMail(String mail) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("user");

        Document filtro = new Document("mail", mail);

        Document userEliminado = collection.findOneAndDelete(filtro);

        if (userEliminado == null) {
            System.out.println("⚠️ No se encontró un usuario con ese mail.");
        } else {
            System.out.println("🗑️ Usuario con mail " + mail + " eliminado correctamente.");
        }
    }

    public void darDeAltaUsuario(User user) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("user");

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