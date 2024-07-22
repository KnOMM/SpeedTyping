package org.development;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.development.gui.Background;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    static MultiWindowTextGUI gui;
    static boolean isOn = true;
    public static Connection connection;

    public static void main(String[] args) throws IOException, SQLException {

        try {
            String db = "jdbc:hsqldb:file:database/DB";
            String user = "SA";
            String password = "password";
            connection = DriverManager.getConnection(db, user, password);
            if (connection == null) {
                System.out.println("Problem with creating connection");
                System.exit(-1);
            }

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = terminalFactory.createScreen();
//        gui = new MultiWindowTextGUI(screen);
        screen.startScreen();


        Window window = new BasicWindow("window");

        gui = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new DefaultWindowManager(), null, new Background());
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
        LogIn login = new LogIn(window, screen, connection);
        LogOut logOut = new LogOut(window);

        Panel menu = new Panel(new LinearLayout(Direction.VERTICAL));
        menu.addComponent(new Label("test label"));
        menu.addComponent(new Label("test label2"));
        menu.addComponent(new Button("Log In", login));
        menu.addComponent(new Button("type test", new SpeedType(window, screen, connection)));
        menu.addComponent(new Button("Statistics",new Statistics(window, screen, connection)));
        menu.addComponent(new Button("Log Out", logOut));
        menu.addComponent(new Button("Exit", () -> {
            window.close();
            ((AsynchronousTextGUIThread) guiThread).stop();
            isOn = false;
        }));

        window.setComponent(menu);
        gui.addWindowAndWait(window);
        connection.close();
    }
}
