package org.development.examples;

import java.sql.*;

public class JdbcExample {

    public static void main(String[] args) throws Exception {
        // JDBC HSQL tutorial code goes here

        String db = "jdbc:hsqldb:file:database/DB";
        String user = "SA";
        String password = "password";
        Connection connection = DriverManager.getConnection(db, user, password);

        String insertQuery = "INSERT INTO PLAYER VALUES (2,'McKenzie','password')";
        String creatQuery = "CREATE TABLE users (id INT NOT NULL, login VARCHAR(50) NOT NULL, password VARCHAR(20), register_date DATE, PRIMARY KEY(id));";
        Statement stmt = connection.createStatement();
        stmt.execute(creatQuery);
//        stmt.execute(insertQuery);

        stmt.close();
        connection.close();
    }
}