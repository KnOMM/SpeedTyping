package org.development;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.development.gui.Background;
import org.hsqldb.DatabaseManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    static MultiWindowTextGUI gui;
    static boolean isOn = true;
    public static Connection connection;

    private static final String DB_URL_TEMPLATE = "jdbc:hsqldb:file:%s/DB;shutdown=true";
    private static final String DB_DIR = "database";

    // Hardcoded database configuration
    private static final String DB_USER = "SA";
    private static final String DB_PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        // Retrieve the database URL from system properties
        String url = System.getProperty("db.url");

        // Load HSQLDB Driver
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("HSQLDB Driver not found");
        }

        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    public static void prepareDatabase() throws IOException {
        Path tempDir = Files.createTempDirectory(DB_DIR);

        // Extract database files from the JAR
//        extractResource("/db/DB.data", tempDir.resolve("DB.data"));
        extractResource("/database/DB.properties", tempDir.resolve("DB.properties"));
        extractResource("/database/DB.script", tempDir.resolve("DB.script"));

        // Update URL to point to the extracted location
        System.setProperty("db.url", String.format(DB_URL_TEMPLATE, tempDir.toAbsolutePath()));
//        System.out.println(String.format(DB_URL_TEMPLATE, tempDir.toAbsolutePath()));
    }

    private static void extractResource(String resourcePath, Path outputPath) throws IOException {
        try (InputStream resourceStream = DatabaseManager.class.getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            Files.copy(resourceStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void main(String[] args) throws IOException, SQLException {

//        // establish a connection with DB
//        try {
//            String db = "jdbc:hsqldb:file:src/main/resources/database/DB;shutdown=true";
//            String user = "SA";
//            String password = "password";
//            connection = DriverManager.getConnection(db, user, password);
//            if (connection == null) {
//                System.out.println("Problem with creating connection");
//                System.exit(-1);
//            }
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            System.exit(-1);
//        }


        prepareDatabase();

        connection = getConnection();

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = terminalFactory.createScreen();
        screen.startScreen();

        Window window = new BasicWindow("Speed Typing");
        window.setHints(List.of(Window.Hint.CENTERED));

        gui = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new DefaultWindowManager(), null, new Background());

        // Background update
        TextGUIThread guiThread = gui.getGUIThread();
        try {
            ((AsynchronousTextGUIThread) guiThread).start();
            new Thread(() -> {
                while (isOn && ((AsynchronousTextGUIThread) guiThread).getState() != AsynchronousTextGUIThread.State.STOPPED) {
                    guiThread.invokeLater(window::invalidate);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // main menu buttonsm
        Panel menu = new Panel(new LinearLayout(Direction.VERTICAL));
        menu.addComponent(new Button("Log In", new LogIn(window, screen, connection))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning)));
        menu.addComponent(new Button("type test", new SpeedType(window, screen, connection))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center)));
        menu.addComponent(new Button("Statistics", new Statistics(window, screen, connection))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center)));
        menu.addComponent(new Button("Log Out", new LogOut(window))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning)));
        menu.addComponent(new Button("Exit", () -> {
            window.close();
            ((AsynchronousTextGUIThread) guiThread).stop();
            isOn = false;
        }).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End)));

        window.setComponent(menu);
        gui.addWindowAndWait(window);
        connection.close();
    }
}
